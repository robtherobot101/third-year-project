package seng302.GUI.Controllers;

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
import seng302.Generic.History;
import seng302.Generic.Main;
import seng302.Generic.WaitingListItem;
import seng302.User.Attribute.Organ;
import seng302.User.User;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import static seng302.Generic.IO.streamOut;


/**
 * The controller for the waiting list pane
 */
public class WaitingListController extends PageController implements Initializable {

    @FXML
    private Button registerOrganButton;

    @FXML
    private Button deregisterOrganButton;

    @FXML
    private TableView<WaitingListItem> waitingListTableView;

    @FXML
    private ComboBox<Organ> organTypeComboBox;

    @FXML
    private TableColumn organType;

    @FXML
    private TableColumn stillWaitingOn;

    @FXML
    private TableColumn organRegisteredDate;

    @FXML
    private TableColumn organDeregisteredDate;

    @FXML
    private Label organComboBoxLabel;

    @FXML
    private Label transplantWaitingListLabel;

    private User currentUser;

    private ObservableList<WaitingListItem> waitingListItems = FXCollections.observableArrayList();
    private ObservableList<Organ> organsInDropDown = FXCollections.observableArrayList(Arrays.asList(Organ.values()));

    /**
     * Sets the user that whose waiting list items will be displayed or modified.
     * @param user The user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        transplantWaitingListLabel.setText("Transplant waiting list for: " + user.getName());
    }

    /**
     * If there is an Organ type selected in the combobox, a new WaitingListItem
     * is added to the user's profile.
     */
    public void registerOrgan() {
        String text = History.prepareFileStringGUI(currentUser.getId(), "waitinglist");
        History.printToFile(streamOut, text);
        Organ organTypeSelected = organTypeComboBox.getSelectionModel().getSelectedItem();
        if (organTypeSelected != null) {
            addToUndoStack();
            WaitingListItem temp = new WaitingListItem(organTypeSelected);
            boolean found = false;
            for (WaitingListItem item : currentUser.getWaitingListItems()) {
                if (temp.getOrganType() == item.getOrganType()) {
                    currentUser.getWaitingListItems().remove(item);
                    currentUser.getWaitingListItems().add(new WaitingListItem(item));
                    currentUser.getWaitingListItems().get(currentUser.getWaitingListItems().size() -1).registerOrgan();
                    found = true;
                    break;
                }
            }
            if (!found) {
                currentUser.getWaitingListItems().add(temp);
            }
            populateWaitingList();
            statusIndicator.setStatus("Registered " + temp.getOrganType(), false);

        }
        populateOrgansComboBox();

    }



    /**
     * Removes the selected item from the user's waiting list and refreshes
     * the waiting TableView
     */
    public void deregisterOrgan() {
        String text = History.prepareFileStringGUI(currentUser.getId(), "waitinglist");
        History.printToFile(streamOut, text);
        WaitingListItem waitingListItemSelected = waitingListTableView.getSelectionModel().getSelectedItem();
        if (waitingListItemSelected != null) {
            addToUndoStack();
            currentUser.getWaitingListItems().remove(waitingListItemSelected);
            currentUser.getWaitingListItems().add(new WaitingListItem(waitingListItemSelected));
            currentUser.getWaitingListItems().get(currentUser.getWaitingListItems().size() -1).deregisterOrgan();
            populateWaitingList();
            //statusIndicator.setStatus("Deregistered " + waitingListItemSelected.getOrganType(), false);
        }
        populateOrgansComboBox();

    }


    public void setStatusIndicator(StatusIndicator statusIndicator){
        this.statusIndicator = statusIndicator;
    }


    public void setTitleBar(TitleBar titleBar){
        this.titleBar = titleBar;
    }


    /**
     * Removes any currently registered organs from the combo box, as an already registered organ cannot be registered again.
     * It is re added if the organ is deregistered.
     */
    public void populateOrgansComboBox(){
        ArrayList<Organ> toBeRemoved = new ArrayList<Organ>();
        ArrayList<Organ> toBeAdded = new ArrayList<Organ>(Arrays.asList(Organ.values()));
        for(WaitingListItem waitingListItem: waitingListItems){
            for(Organ type: Organ.values()){
                if(waitingListItem.getOrganType() == type){
                    if(waitingListItem.getStillWaitingOn()){
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
        System.out.println(toBeAdded.size());
    }

    /**
     * Refreshes the list waiting list TableView
     */
    public void populateWaitingList() {
        waitingListItems.clear();
        waitingListItems.addAll(currentUser.getWaitingListItems());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setWaitingListController(this);
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
                        if (getStyleClass().contains("highlighted-row")) {
                            getStyleClass().remove("highlighted-row");
                        }
                        setTooltip(null);
                        if (item != null && !empty) {
                            if (item.isDonatingOrgan(currentUser) && item.getStillWaitingOn()) {
                                setTooltip(new Tooltip("User is currently donating this organ"));
                                System.out.println("User is donating " + item.getOrganType());
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
     * @param shown True if the controls are to be shown, otherwise false
     */
    public void setControlsShown(boolean shown) {
        this.transplantWaitingListLabel.setVisible(!shown);
        this.registerOrganButton.setVisible(shown);
        this.deregisterOrganButton.setVisible(shown);
        this.organTypeComboBox.setVisible(shown);
        this.organComboBoxLabel.setVisible(shown);
    }

    /**
     * Calls the main class which has access to the static controller allowing it to manually add the user to the waitinglist undo stack.
     */
    public void addToUndoStack(){
        Main.addCurrentToWaitingListUndoStack();
    }

}
