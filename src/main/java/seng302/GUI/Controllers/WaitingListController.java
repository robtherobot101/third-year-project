package seng302.GUI.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import seng302.GUI.StatusIndicator;
import seng302.GUI.TitleBar;
import seng302.Generic.*;

import java.net.URL;
import java.util.*;
import seng302.User.Attribute.Organ;
import seng302.User.User;


public class WaitingListController extends PageController implements Initializable {
    @FXML
    private Button addOrganButton;

    @FXML
    private Button removeOrganButton;

    @FXML
    private TableView<WaitingListItem> waitingList;

    @FXML
    private ComboBox<Organ> organTypeComboBox;

    @FXML
    private TableColumn organType;

    @FXML
    private TableColumn organRegisteredDate;

    @FXML
    private TableColumn organDeregisteredDate;

    private User currentUser;

    private ObservableList<WaitingListItem> waitingListItems = FXCollections.observableArrayList();


    /**
     * Sets the user that whose waiting list items will be displayed or modified.
     * @param user
     */
    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    /**
     * If there is an Organ type selected in the combobox, a new WaitingListItem
     * is added to the user's profile.
     */
    public void addOrgan(){
        Organ organTypeSelected = organTypeComboBox.getSelectionModel().getSelectedItem();
        if(organTypeSelected != null){
            WaitingListItem temp = new WaitingListItem(organTypeSelected);
            boolean found = false;
            for (WaitingListItem item : currentUser.getWaitingListItems()){
                if (temp.getOrganType() == item.getOrganType()){
                    item.registerOrgan();
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
    }

    /**
     * Removes the selected item from the user's waiting list and refreshes
     * the waiting TableView
     */
    public void removeOrgan(){
        WaitingListItem waitingListItemSelected = waitingList.getSelectionModel().getSelectedItem();
        if(waitingListItemSelected != null){
            waitingListItemSelected.deregisterOrgan();
            populateWaitingList();
            statusIndicator.setStatus("Deregistered " + waitingListItemSelected.getOrganType(), false);
        }
    }

    /**
     * Refreshes the list waiting list TableView
     */
    public void populateWaitingList(){
        waitingListItems.clear();
        waitingListItems.addAll(currentUser.getWaitingListItems());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setWaitingListController(this);
        organTypeComboBox.setItems(FXCollections.observableArrayList(Organ.values()));
        waitingList.setItems(waitingListItems);
        organType.setCellValueFactory(new PropertyValueFactory<>("organType"));
        organRegisteredDate.setCellValueFactory(new PropertyValueFactory<>("organRegisteredDate"));
        organDeregisteredDate.setCellValueFactory(new PropertyValueFactory<>("organDeregisteredDate"));
    }
}
