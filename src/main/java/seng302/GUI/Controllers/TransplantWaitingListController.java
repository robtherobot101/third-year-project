package seng302.GUI.Controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import seng302.Core.TransplantWaitingListItem;
import seng302.GUI.TFScene;
import seng302.Generic.Main;
import seng302.Generic.WaitingListItem;
import seng302.User.Attribute.Organ;
import seng302.User.User;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

/**
 * Class to handle the transplant waiting list window that displays all receivers waiting for an organ
 */
public class TransplantWaitingListController implements Initializable {

    @FXML
    private TableView transplantTable;

    @FXML
    private TableColumn organColumn;
    @FXML
    private TableColumn nameColumn;
    @FXML
    private TableColumn dateColumn;
    @FXML
    private TableColumn regionColumn;
    @FXML
    private ComboBox organSearchComboBox;
    @FXML
    private TextField regionSearchTextField;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
    private ObservableList<TransplantWaitingListItem> transplantList = FXCollections.observableArrayList();

    /**
     * returns to the clinician view
     */
    public void returnView(){
        Main.setScene(TFScene.clinician);
    }

    /**
     * Closes the application
     */
    public void close(){
        Platform.exit();
    }

    /**
     * Updates the transplant waiting list table and checks if reciever is waiting not complete
     */
    public void updateTransplantList() {
        transplantList.removeAll(transplantList);
        for (User user : Main.users) {
            if (!user.getWaitingListItems().isEmpty()) {
                for (WaitingListItem item : user.getWaitingListItems()) {
                    try {
                        if (!(item.getOrganRegisteredDate() == null)) {
                            transplantList.add(new TransplantWaitingListItem(user.getName(), user.getRegion(), sdf.parse(item.getOrganRegisteredDate()), item.getOrganType(), user.getId()));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        transplantTable.setItems(transplantList);
    }

    /**
     * updates the transplant waiting list table and filters users by a region.
     * @param regionSearch the search text to be applied to the user regions given by a user.
     * @param organSearch the organ to specifically search for given by a user.
     */
    public void updateFoundUsersWithFiltering(String regionSearch, String organSearch) {
        transplantList.removeAll(transplantList);
        for (User user : Main.users) {
            if (!user.getWaitingListItems().isEmpty()) {
                for (WaitingListItem item : user.getWaitingListItems()) {
                    try {
                        if (!(item.getOrganRegisteredDate() == null)) {
                            if (organSearch.equals("None") || organSearch == item.getOrganType().toString()) {
                                if (regionSearch.equals("") && (user.getRegion() == null)) {
                                    transplantList.add(new TransplantWaitingListItem(user.getName(), user.getRegion(), sdf.parse(item.getOrganRegisteredDate()), item.getOrganType(), user.getId()));
                                } else if ((user.getRegion() != null) && (user.getRegion().toLowerCase().contains(regionSearch.toLowerCase()))) {
                                    transplantList.add(new TransplantWaitingListItem(user.getName(), user.getRegion(), sdf.parse(item.getOrganRegisteredDate()), item.getOrganType(), user.getId()));
                                }
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        transplantTable.setItems(transplantList);
    }

    /**
     * method to handle when the organ filter combo box is changed a nd thben updates the transplant waiting list
     */
    public void updateFoundUsersOnOrganChange() {
        updateFoundUsersWithFiltering(regionSearchTextField.getText(), organSearchComboBox.getValue().toString());
    }

    /**
     * Initilizes the gui display with the correct content in the table.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        organColumn.setCellValueFactory(new PropertyValueFactory<>("organ"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        transplantTable.setItems(transplantList);

        Main.setTransplantWaitingListController(this);

        updateTransplantList();
        transplantTable.setItems(transplantList);

        //add options to organ filter combobox
        ObservableList<String> organSearchlist = FXCollections.observableArrayList();
        Organ[] organsList = Organ.values();
        organSearchlist.add("None");
        for (Organ o : organsList) {
            String v = o.toString();
            organSearchlist.add(v);
        }
        organSearchComboBox.setItems(organSearchlist);
        organSearchComboBox.setValue("None");

        //listener for when text is input into the region search text box
        regionSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFoundUsersWithFiltering(newValue, organSearchComboBox.getValue().toString());
        });


        transplantTable.setRowFactory(new Callback<TableView<TransplantWaitingListItem>, TableRow<TransplantWaitingListItem>>(){
            @Override
            public TableRow<TransplantWaitingListItem> call(TableView<TransplantWaitingListItem> tableView) {
                final TableRow<TransplantWaitingListItem> row = new TableRow<TransplantWaitingListItem>() {
                };
                //event to open receiver profile when clicked
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getClickCount() == 2) {
                        Stage stage = new Stage();

                        Main.addCliniciansUserWindow(stage);
                        stage.initModality(Modality.NONE);

                        try{
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/userWindow.fxml"));
                            Parent root = (Parent) loader.load();
                            UserWindowController userWindowController = loader.getController();
                            Main.setCurrentUser(Main.getUserById(row.getItem().getId()));
                            userWindowController.populateUserFields();
                            userWindowController.populateHistoryTable();
                            Main.medicationsViewForClinician();

                            Scene newScene = new Scene(root, 900, 575);
                            stage.setScene(newScene);
                            stage.show();
                            userWindowController.setAsChildWindow();
                        } catch (IOException | NullPointerException e) {
                            System.err.println("Unable to load fxml or save file.");
                            e.printStackTrace();
                            Platform.exit();
                        }
                    }
                });
                transplantTable.refresh();
                return row;

            }
        });
    }

}
