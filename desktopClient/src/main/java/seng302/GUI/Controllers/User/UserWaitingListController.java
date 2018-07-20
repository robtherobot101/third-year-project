package seng302.GUI.Controllers.User;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import seng302.GUI.StatusIndicator;
import seng302.GUI.TitleBar;
import seng302.Generic.WindowManager;
import seng302.User.Attribute.Organ;
import seng302.User.User;
import seng302.User.WaitingListItem;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;


/**
 * The controller for the waiting list pane
 */
public class UserWaitingListController extends UserTabController implements Initializable {

    @FXML
    private Button registerOrganButton, deregisterOrganButton;
    @FXML
    private TableView<WaitingListItem> waitingListTableView;
    @FXML
    private ComboBox<Organ> organTypeComboBox;
    @FXML
    private TableColumn organType, stillWaitingOn, organRegisteredDate, organDeregisteredDate;
    @FXML
    private Label organComboBoxLabel, transplantWaitingListLabel;

    private ObservableList<WaitingListItem> waitingListItems = FXCollections.observableArrayList();
    private ObservableList<Organ> organsInDropDown = FXCollections.observableArrayList(Arrays.asList(Organ.values()));

    /**
     * Sets the user that whose waiting list items will be displayed or modified.
     *
     * @param user The user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        transplantWaitingListLabel.setText("Transplant waiting list for: " + user.getName());
    }

    /**
     * If there is an Organ type selected in the combobox, a new ReceiverWaitingListItem
     * is added to the user's profile.
     */
    public void registerOrgan() {
        Organ organTypeSelected = organTypeComboBox.getSelectionModel().getSelectedItem();
        if (organTypeSelected != null) {
            userController.addCurrentUserToUndoStack();

            WaitingListItem newWaitingListItem = new WaitingListItem(currentUser.getName(), currentUser.getRegion(), currentUser.getId(), organTypeSelected);

            currentUser.getWaitingListItems().add(newWaitingListItem);
            populateWaitingList();
            statusIndicator.setStatus("Registered " + newWaitingListItem.getOrganType(), false);

        }
        populateOrgansComboBox();
        userController.populateUserAttributes();
        WindowManager.updateTransplantWaitingList();
    }


    /**
     * Removes the selected item from the user's waiting list and refreshes
     * the waiting TableView
     */
    public void deregisterOrgan() {
        WaitingListItem waitingListItemSelected = waitingListTableView.getSelectionModel().getSelectedItem();
        if (waitingListItemSelected != null) {
            userController.addCurrentUserToUndoStack();
            WindowManager.showDeregisterDialog(waitingListItemSelected);
            statusIndicator.setStatus("De-registered " + waitingListItemSelected.getOrganType(), false);
            populateWaitingList();
        }
        populateOrgansComboBox();
        userController.populateUserAttributes();
    }


    public void setStatusIndicator(StatusIndicator statusIndicator) {
        this.statusIndicator = statusIndicator;
    }


    public void setTitleBar(TitleBar titleBar) {
        this.titleBar = titleBar;
    }

    @Override
    public void undo() {
        redoStack.add(new User(currentUser));
        currentUser.copyWaitingListsFrom(undoStack.getLast());
        undoStack.removeLast();
        populateWaitingList();
    }

    @Override
    public void redo() {
        undoStack.add(new User(currentUser));
        currentUser.copyWaitingListsFrom(redoStack.getLast());
        redoStack.removeLast();
        populateWaitingList();
    }


    /**
     * Removes any currently registered organs from the combo box, as an already registered organ cannot be registered again.
     * It is re added if the organ is deregistered.
     */
    public void populateOrgansComboBox() {
        ArrayList<Organ> toBeRemoved = new ArrayList<>();
        ArrayList<Organ> toBeAdded = new ArrayList<>(Arrays.asList(Organ.values()));
        for (WaitingListItem waitingListItem : waitingListItems) {
            for (Organ type : Organ.values()) {
                if (waitingListItem.getOrganType() == type) {
                    if (waitingListItem.getStillWaitingOn()) {
                        toBeRemoved.add(type);
                    }
                }
            }
        }
        toBeAdded.removeAll(toBeRemoved);
        organsInDropDown.removeAll();
        organsInDropDown.clear();
        organsInDropDown.addAll(toBeAdded);
        organTypeComboBox.setItems(null);
        organTypeComboBox.setItems(organsInDropDown);
    }

    /**
     * Refreshes the list waiting list TableView
     */
    public void populateWaitingList() {
        //TODO
        //currentUser is null when an item is deregistered via the clinicians transplant waiting list
        // and the clinician hasn't yet viewed any user windows.

        //This should be fixed with Andrew's changes to dealing with multiple clinician
        if(currentUser != null){
            waitingListItems.clear();
            waitingListItems.addAll(currentUser.getWaitingListItems());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        organTypeComboBox.setItems(organsInDropDown);
        waitingListTableView.setItems(waitingListItems);
        organType.setCellValueFactory(new PropertyValueFactory<>("organType"));
        stillWaitingOn.setCellValueFactory(new PropertyValueFactory<>("stillWaitingOn"));
        organRegisteredDate.setCellValueFactory(new PropertyValueFactory<>("organRegisteredDate"));
        organDeregisteredDate.setCellValueFactory(new PropertyValueFactory<>("organDeregisteredDate"));

        deregisterOrganButton.setDisable(true);

        registerOrganButton.disableProperty().bind(
                Bindings.isNull(organTypeComboBox.getSelectionModel().selectedItemProperty())
        );

        waitingListTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                deregisterOrganButton.setDisable(true);
            } else if (newValue.getStillWaitingOn()) {
                deregisterOrganButton.setDisable(false);
            } else {
                deregisterOrganButton.setDisable(true);
            }
        });

        waitingListTableView.setRowFactory(new Callback<TableView<WaitingListItem>, TableRow<WaitingListItem>>() {
            @Override
            public TableRow<WaitingListItem> call(TableView<WaitingListItem> tableView) {
                return new TableRow<WaitingListItem>() {
                    @Override
                    public void updateItem(WaitingListItem item, boolean empty) {
                        super.updateItem(item, empty);
                        getStyleClass().remove("highlighted-row");
                        setTooltip(null);
                        if (item != null && !empty) {
                            if(currentUser.conflictingOrgans().contains(item.getOrganType())) {
                                setTooltip(new Tooltip("User is currently donating this organ"));
                                if (!getStyleClass().contains("highlighted-row")) {
                                    getStyleClass().add("highlighted-row");
                                }
                            }
                        }
                    }
                };
            }
        });
    }

    /**
     * Shows or hides the options for modifying the transplant waiting list
     * depending on the value of shown
     *
     * @param shown True if the controls are to be shown, otherwise false
     */
    public void setControlsShown(boolean shown) {
        this.transplantWaitingListLabel.setVisible(!shown);
        this.registerOrganButton.setVisible(shown);
        this.deregisterOrganButton.setVisible(shown);
        this.organTypeComboBox.setVisible(shown);
        this.organComboBoxLabel.setVisible(shown);
    }
}
