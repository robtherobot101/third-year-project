package seng302.GUI.Controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import seng302.Controllers.DialogWindowController;
import seng302.Core.Disease;
import seng302.Core.TransplantWaitingListItem;
import seng302.GUI.TFScene;
import seng302.Generic.History;
import seng302.Generic.Main;
import seng302.Generic.WaitingListItem;
import seng302.User.Attribute.Organ;
import seng302.User.User;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @FXML
    private Button deregisterReceiverButton;

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
                        List<Integer> codes = Arrays.asList(1,2,3,4);
                        if (!(item.getOrganRegisteredDate() == null) && !(codes.contains(item.getOrganDeregisteredCode()))) {
                            transplantList.add(new TransplantWaitingListItem(user.getName(), user.getRegion(), sdf.parse(item.getOrganRegisteredDate()), item.getOrganType(), user.getId(), item.getWaitingListItemId()));
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
                                    transplantList.add(new TransplantWaitingListItem(user.getName(), user.getRegion(), sdf.parse(item.getOrganRegisteredDate()), item.getOrganType(), user.getId(), item.getWaitingListItemId()));
                                } else if ((user.getRegion() != null) && (user.getRegion().toLowerCase().contains(regionSearch.toLowerCase()))) {
                                    transplantList.add(new TransplantWaitingListItem(user.getName(), user.getRegion(), sdf.parse(item.getOrganRegisteredDate()), item.getOrganType(), user.getId(), item.getWaitingListItemId()));
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
        deregisterReceiverButton.setDisable(true);
    }

    /**
     * method to handle when the organ filter combo box is changed a nd thben updates the transplant waiting list
     */
    public void updateFoundUsersOnOrganChange() {
        updateFoundUsersWithFiltering(regionSearchTextField.getText(), organSearchComboBox.getValue().toString());
    }

    public void showDeregisterDialog() {
        //Set dialog window
        List<String> reasonCodes = new ArrayList<>();
        reasonCodes.add("1: Error Registering");
        reasonCodes.add("2: Disease Cured");
        reasonCodes.add("3: Receiver Deceased");
        reasonCodes.add("4: Successful Transplant");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("4: Successful Transplant", reasonCodes);
        dialog.setTitle("De-Registering Reason Code");
        dialog.setHeaderText("Select a reason code");
        dialog.setContentText("Reason Code: ");

        //Get Input Code
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(option -> processDeregister(option));
    }

    public void showDiseaseDeregisterDialog() {
        TransplantWaitingListItem selectedWaitingListItem = (TransplantWaitingListItem) transplantTable.getSelectionModel().getSelectedItem();
        User selectedUser = Main.getUserById(selectedWaitingListItem.getId());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cure Disease");
        dialog.setHeaderText("Select a disease to cure.");

        ButtonType loginButtonType = new ButtonType("Cure", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ObservableList<Disease> diseaseComboList = FXCollections.observableArrayList(selectedUser.getCurrentDiseases());
        final ComboBox diseaseComboBox = new ComboBox(diseaseComboList);
        diseaseComboBox.setCellFactory(new Callback<ListView<Disease>, ListCell<Disease>>() {
            @Override
            public ListCell<Disease> call(ListView<Disease> param) {
                final ListCell<Disease> cell = new ListCell<Disease>(){

                    @Override
                    protected void updateItem(Disease t, boolean bln) {
                        super.updateItem(t, bln);

                        if(t != null){
                            setText(t.getName());
                        }else{
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });

        grid.add(diseaseComboBox, 1, 1);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(option -> {
            if (diseaseComboBox.getValue() != null) {
                Disease selected = (Disease) diseaseComboBox.getValue();
                selected.setCured(true);
                ArrayList<WaitingListItem> selectedUserWaitingListItems= selectedUser.getWaitingListItems();
                selectedWaitingListItem.getId();
                for (WaitingListItem i: selectedUserWaitingListItems) {
                    if (i.getWaitingListItemId() == selectedWaitingListItem.getWaitingListId()) {
                        i.deregisterOrgan(2);
                        DialogWindowController.showInformation("De-Registered", "Organ transplant De-registered", "Reason Code 2 selected and disease cured");
                    }
                }
            } else {
                ArrayList<WaitingListItem> selectedUserWaitingListItems= selectedUser.getWaitingListItems();
                selectedWaitingListItem.getId();
                for (WaitingListItem i: selectedUserWaitingListItems) {
                    if (i.getWaitingListItemId() == selectedWaitingListItem.getWaitingListId()) {
                        i.deregisterOrgan(2);
                        DialogWindowController.showInformation("De-Registered", "Organ transplant De-registered", "Reason Code 2 selected. No disease cured");
                    }
                }
            }
        });
    }

    public void confirmDiseaseCuring() {
        TransplantWaitingListItem selectedWaitingListItem = (TransplantWaitingListItem) transplantTable.getSelectionModel().getSelectedItem();
        User selectedUser = Main.getUserById(selectedWaitingListItem.getId());
        if (!selectedUser.getCurrentDiseases().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cure Disease?");
            alert.setHeaderText("Would you like to select the cured disease?");
            alert.setContentText("Cure a Disease?");

            ButtonType buttonTypeOne = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne) {
                showDiseaseDeregisterDialog();
            } else {
                ArrayList<WaitingListItem> selectedUserWaitingListItems= selectedUser.getWaitingListItems();
                selectedWaitingListItem.getId();
                for (WaitingListItem i: selectedUserWaitingListItems) {
                    if (i.getWaitingListItemId() == selectedWaitingListItem.getWaitingListId()) {
                        i.deregisterOrgan(2);
                        DialogWindowController.showInformation("De-Registered", "Organ transplant De-registered", "Reason Code 2 selected. No disease cured");
                        break;
                    }
                }
            }
        }
    }

    public void processDeregister(String reason) {
        if (reason == "1: Error Registering") {
            errorDeregister();
        } else if (reason == "2: Disease Cured") {
            confirmDiseaseCuring();
        } else if (reason == "3: Receiver Deceased") {
            deathDeregister();
        } else if (reason == "4: Successful Transplant") {
            transplantDeregister();
        }
        Main.updateTransplantWaitingList();
    }

    private void transplantDeregister(){
        TransplantWaitingListItem selectedWaitingListItem = (TransplantWaitingListItem) transplantTable.getSelectionModel().getSelectedItem();
        User user = Main.getUserById(selectedWaitingListItem.getId());
        for (WaitingListItem i: user.getWaitingListItems()) {
            if (i.getWaitingListItemId() == selectedWaitingListItem.getWaitingListId()) {
                i.deregisterOrgan(1);
                break;
            }
        }
    }

    /**
     * Removes an organ from the transplant waiting list and writes it as an error to the history log.
     */
    private void errorDeregister(){
        TransplantWaitingListItem selectedWaitingListItem = (TransplantWaitingListItem) transplantTable.getSelectionModel().getSelectedItem();
        User user = Main.getUserById(selectedWaitingListItem.getId());
        Long userId = user.getId();
        for (WaitingListItem i: user.getWaitingListItems()) {
            if (i.getWaitingListItemId() == selectedWaitingListItem.getWaitingListId()) {
                i.deregisterOrgan(1);
                History.prepareFileStringGUI(userId, "deregisterError");
                break;
            }
        }
    }

    /**
     * Removes all organs waiting on transplant for a user.
     * Called when a receiver has deceased.
     */
    private void deathDeregister() {
        TransplantWaitingListItem selectedWaitingListItem = (TransplantWaitingListItem) transplantTable.getSelectionModel().getSelectedItem();
        User selectedUser = Main.getUserById(selectedWaitingListItem.getId());
        Long userId = selectedUser.getId();
        if (selectedUser.getWaitingListItems() != null) {
            History.prepareFileStringGUI(userId, "deregisterDeath");
            for (WaitingListItem item : selectedUser.getWaitingListItems()){
                item.deregisterOrgan(3);
            }

        }
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
        deregisterReceiverButton.setDisable(true);

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

        transplantTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            deregisterReceiverButton.setDisable(false);
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
                            Main.controlViewForClinician();

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
