package seng302.gui.controllers.user;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import seng302.generic.WindowManager;
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
        populateWaitingList();
        transplantWaitingListLabel.setText("Transplant waiting list for: " + user.getName());
    }

    /**
     * If there is an Organ type selected in the combobox, a new ReceiverWaitingListItem
     * is added to the user's profile.
     */
    public void registerOrgan() {
        Organ organTypeSelected = organTypeComboBox.getSelectionModel().getSelectedItem();
        if (organTypeSelected != null) {
            if(currentUser.getDateOfDeath() == null) {
                userController.addCurrentUserToUndoStack();

                WaitingListItem newWaitingListItem = new WaitingListItem(currentUser.getName(), currentUser.getRegion(), currentUser.getId(), organTypeSelected);

                currentUser.getWaitingListItems().add(newWaitingListItem);
                userController.addHistoryEntry("Waiting list item added", "A new waiting list item (" + newWaitingListItem.getOrganType() + ") was added.");
                statusIndicator.setStatus("Registered " + newWaitingListItem.getOrganType(), false);

                // Slow boi
                Platform.runLater(() -> {
                    populateWaitingList();
                });
            } else {
                Alert alert = WindowManager.createAlert(Alert.AlertType.ERROR, "Error", "Failed to de-register", "New items cannot be added after a users's death.");
                alert.show();
            }
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
            WindowManager.showDeregisterDialog(waitingListItemSelected, currentUser);
            statusIndicator.setStatus("De-registered " + waitingListItemSelected.getOrganType(), false);
            populateWaitingList();
        }
        populateOrgansComboBox();
        userController.populateUserAttributes();
    }

    /**
     * undos the last change
     */
    @Override
    public void undo() {
        redoStack.add(new User(currentUser));
        currentUser.copyWaitingListsFrom(undoStack.getLast());
        undoStack.removeLast();
        populateWaitingList();
    }

    /**
     * redos the last undo
     */
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
        if (currentUser != null){
            waitingListItems.clear();
            waitingListItems.addAll(currentUser.getWaitingListItems());
        }
    }

    /**
     * starts the user waiting ist controller
     * @param location not used
     * @param resources not used
     */
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
                            for(Organ o:currentUser.getOrgans()){
                                if(o.equals(item.getOrganType()) && item.getStillWaitingOn()){
                                    setTooltip(new Tooltip("user is currently donating this organ"));
                                    if (!getStyleClass().contains("highlighted-row")) {
                                        getStyleClass().add("highlighted-row");
                                    }
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
