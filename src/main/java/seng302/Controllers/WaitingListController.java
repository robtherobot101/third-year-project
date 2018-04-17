package seng302.Controllers;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import seng302.Core.*;

import java.net.URL;
import java.util.*;


public class WaitingListController implements Initializable {
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

    private Donor currentUser;



    private ObservableList<WaitingListItem> waitingListItems = FXCollections.observableArrayList();


    /**
     * Sets the user that whose waiting list items will be displayed or modified.
     * @param user
     */
    public void setCurrentUser(Donor user){
        this.currentUser = user;
    }

    /**
     * If there is an Organ type selected in the combobox, a new WaitingListItem
     * is added to the user's profile.
     */
    public void addOrgan(){
        Organ organTypeSelected = organTypeComboBox.getSelectionModel().getSelectedItem();
        if(organTypeSelected != null){
            System.out.println("Current user: "+currentUser);
            System.out.println("Current user waiting list: "+currentUser.getWaitingListItems());
            currentUser.getWaitingListItems().add(new WaitingListItem(organTypeSelected));
            populateWaitingList();
        }
    }

    /**
     * Removes the selected item from the user's waiting list and refreshes
     * the waiting TableView
     */
    public void removeOrgan(){
        WaitingListItem waitingListItemSelected = waitingList.getSelectionModel().getSelectedItem();
        if(waitingListItemSelected != null){
            currentUser.getWaitingListItems().remove(waitingListItemSelected);
            populateWaitingList();
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
