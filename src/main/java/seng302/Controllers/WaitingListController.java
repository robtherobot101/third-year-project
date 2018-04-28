package seng302.Controllers;

import com.sun.xml.internal.bind.v2.TODO;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import seng302.Core.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;


public class WaitingListController implements Initializable {
    @FXML
    private Button registerOrganButton;

    @FXML
    private Button deregisterOrganButton;

    @FXML
    private TableView<WaitingListItem> waitingList;

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
    public void registerOrgan(){
        Organ organTypeSelected = organTypeComboBox.getSelectionModel().getSelectedItem();
        if(organTypeSelected != null){
            WaitingListItem temp = new WaitingListItem(organTypeSelected, currentUser);
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
        }
    }

    /**
     * Removes the selected item from the user's waiting list and refreshes
     * the waiting TableView
     */
    public void deregisterOrgan(){
        WaitingListItem waitingListItemSelected = waitingList.getSelectionModel().getSelectedItem();
        if(waitingListItemSelected != null){
            waitingListItemSelected.deregisterOrgan();
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
        stillWaitingOn.setCellValueFactory(new PropertyValueFactory<>("stillWaitingOn"));
        organRegisteredDate.setCellValueFactory(new PropertyValueFactory<>("organRegisteredDate"));
        organDeregisteredDate.setCellValueFactory(new PropertyValueFactory<>("organDeregisteredDate"));

        registerOrganButton.setDisable(true);
        deregisterOrganButton.setDisable(true);

        organTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            registerOrganButton.setDisable(false);
        });

        waitingList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue==null){
                deregisterOrganButton.setDisable(true);
            }else{
                deregisterOrganButton.setDisable(false);
            }
        });

        waitingList.setRowFactory(new Callback<TableView<WaitingListItem>, TableRow<WaitingListItem>>() {
            Boolean highlight = false;
            @Override
            public TableRow<WaitingListItem> call(TableView<WaitingListItem> tableView) {
                return new TableRow<WaitingListItem>() {
                    @Override
                    public void updateItem(WaitingListItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (getStyleClass().contains("highlighted-row")) {
                            getStyleClass().remove("highlighted-row");
                        }
                        if(item != null && !empty) {

                            if(item.isDonatingOrgan(currentUser)){
                                //TODO Highlight the row
                                System.out.println("User is donating "+item.getOrganType());
                                highlight = true;
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

    public void setControlsShown(boolean shown) {
        this.registerOrganButton.setVisible(shown);
        this.deregisterOrganButton.setVisible(shown);
        this.organTypeComboBox.setVisible(shown);
        this.organComboBoxLabel.setVisible(shown);
    }
}
