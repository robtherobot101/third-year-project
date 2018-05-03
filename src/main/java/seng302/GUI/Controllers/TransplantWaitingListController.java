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
import javafx.util.StringConverter;
import seng302.GUI.TFScene;
import seng302.Generic.*;
import seng302.User.Attribute.Organ;
import seng302.User.User;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        Main.getClinicianController().setTitle();
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
        transplantList.clear();
        for (User user : Main.users) {
            if (!user.getWaitingListItems().isEmpty()) {
                for (ReceiverWaitingListItem item : user.getWaitingListItems()) {
                    List<Integer> codes = Arrays.asList(1,2,3,4);
                    if (!(item.getOrganRegisteredDate() == null) && !(codes.contains(item.getOrganDeregisteredCode()))) {
                        transplantList.add(new TransplantWaitingListItem(user.getName(), user.getRegion(), item.getOrganRegisteredDate(), item.getOrganType(), user.getId(), item.getWaitingListItemId()));
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
        transplantList.clear();
        for (User user : Main.users) {
            if (!user.getWaitingListItems().isEmpty()) {
                for (ReceiverWaitingListItem item : user.getWaitingListItems()) {
                    if (!(item.getOrganRegisteredDate() == null)) {
                        if (organSearch.equals("None") || organSearch == item.getOrganType().toString()) {
                            List<Integer> codes = Arrays.asList(1,2,3,4);
                            if (regionSearch.equals("") && (user.getRegion() == null) && !(item.getOrganRegisteredDate() == null) && !(codes.contains(item.getOrganDeregisteredCode()))) {
                                transplantList.add(new TransplantWaitingListItem(user.getName(), user.getRegion(), item.getOrganRegisteredDate(), item.getOrganType(), user.getId(), item.getWaitingListItemId()));
                            } else if ((user.getRegion() != null) && (user.getRegion().toLowerCase().contains(regionSearch.toLowerCase())) && !(item.getOrganRegisteredDate() == null) && !(codes.contains(item.getOrganDeregisteredCode()))) {
                                transplantList.add(new TransplantWaitingListItem(user.getName(), user.getRegion(), item.getOrganRegisteredDate(), item.getOrganType(), user.getId(), item.getWaitingListItemId()));
                            }
                        }
                    }
                }
            }
        }
        transplantTable.setItems(transplantList);
        deregisterReceiverButton.setDisable(true);
    }

    /**
     * method to handle when the organ filter combo box is changed and then updates the transplant waiting list
     */
    public void updateFoundUsersOnOrganChange() {
        updateFoundUsersWithFiltering(regionSearchTextField.getText(), organSearchComboBox.getValue().toString());
    }


    /**
     * Method to show de-registering reason code for a user to make a selection
     */
    public void showDeregisterDialog() {
        //Set dialog window
        List<String> reasonCodes = new ArrayList<>();
        reasonCodes.add("1: Error Registering");
        reasonCodes.add("2: Disease Cured");
        reasonCodes.add("3: Receiver Deceased");
        reasonCodes.add("4: Successful Transplant");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("4: Successful Transplant", reasonCodes);
        dialog.getDialogPane().getStylesheets().add(Main.getDialogStyle());
        dialog.setTitle("De-Registering Reason Code");
        dialog.setHeaderText("Select a reason code");
        dialog.setContentText("Reason Code: ");

        //Get Input Code
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(this::processDeregister);
    }

    /**
     * method to show dialog for a clinician to choose from a receivers listed diseases, if any, to cure.
     */
    public void showDiseaseDeregisterDialog() {
        WaitingListItem selectedWaitingListItem;
        if (Main.getWaitingListController().getDeregisterPressed()){
            selectedWaitingListItem = Main.getWaitingListController().getWaitingList().getSelectionModel().getSelectedItem();

        } else {
            selectedWaitingListItem = (TransplantWaitingListItem) transplantTable.getSelectionModel().getSelectedItem();
        }
        User selectedUser = Main.getUserById(selectedWaitingListItem.getUserId());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(Main.getDialogStyle());
        dialog.setTitle("Cure Disease");
        dialog.setHeaderText("Select a disease to cure.");

        ButtonType cureButtonType = new ButtonType("Cure", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(cureButtonType, ButtonType.CANCEL);

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
            if (result.get() == cureButtonType) {
                if (diseaseComboBox.getValue() != null) {
                    Disease selected = (Disease) diseaseComboBox.getValue();
                    selected.setCured(true);
                    ArrayList<ReceiverWaitingListItem> selectedUserWaitingListItems = selectedUser.getWaitingListItems();
                    ArrayList<Disease> currentDiseases = selectedUser.getCurrentDiseases();
                    ArrayList<Disease> curedDiseases = selectedUser.getCuredDiseases();
                    curedDiseases.add(selected);
                    selectedUser.setCuredDiseases(curedDiseases);
                    currentDiseases.remove(selected);
                    selectedUser.setCurrentDiseases(currentDiseases);
                    selectedWaitingListItem.getUserId();
                    for (ReceiverWaitingListItem i : selectedUserWaitingListItems) {
                        if (i.getWaitingListItemId() == selectedWaitingListItem.getWaitingListItemId()) {
                            i.deregisterOrgan(2);
                            DialogWindowController.showInformation("De-Registered", "Organ transplant De-registered", "Reason Code 2 selected and disease cured");
                            Main.updateDiseases();
                            break;
                        }
                    }
                } else {
                    DialogWindowController.showInformation("Invaild Disease", "Please Select a disease", "Select a disease to cure");
                    showDiseaseDeregisterDialog();
                }
            } else {
                confirmDiseaseCuring();
            }
        });
    }

    /**
     * method to show dialog to confirm the curing of a disease and then to perform the operations.
     */
    public void confirmDiseaseCuring() {
        WaitingListItem selectedWaitingListItem;
        if (Main.getWaitingListController().getDeregisterPressed()){
            selectedWaitingListItem = Main.getWaitingListController().getWaitingList().getSelectionModel().getSelectedItem();

        } else {
            selectedWaitingListItem = (TransplantWaitingListItem) transplantTable.getSelectionModel().getSelectedItem();
        }
        User selectedUser = Main.getUserById(selectedWaitingListItem.getUserId());
        if (!selectedUser.getCurrentDiseases().isEmpty()) {
            Alert alert = Main.createAlert(Alert.AlertType.CONFIRMATION, "Cure Disease?", "Would you like to select the cured disease?", "Cure a Disease?");

            ButtonType buttonTypeOne = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne) {
                showDiseaseDeregisterDialog();
            } else {
                ArrayList<ReceiverWaitingListItem> selectedUserWaitingListItems= selectedUser.getWaitingListItems();
                selectedWaitingListItem.getUserId();
                for (ReceiverWaitingListItem i: selectedUserWaitingListItems) {
                    if (i.getWaitingListItemId() == selectedWaitingListItem.getWaitingListItemId()) {
                        i.deregisterOrgan(2);
                        DialogWindowController.showInformation("De-Registered", "Organ transplant De-registered", "Reason Code 2 selected. No disease cured");
                        break;
                    }
                }
            }
        } else {
            ArrayList<ReceiverWaitingListItem> selectedUserWaitingListItems= selectedUser.getWaitingListItems();
            for (ReceiverWaitingListItem i: selectedUserWaitingListItems) {
                if (i.getWaitingListItemId() == selectedWaitingListItem.getWaitingListItemId()) {
                    i.deregisterOrgan(2);
                    DialogWindowController.showInformation("De-Registered", "Organ transplant De-registered", "Reason Code 2 selected. No disease cured");
                    break;
                }
            }
        }
    }

    /**
     * method to call the correct handling de-registering method based on the reason code given
     * @param reason the reason code given by the clinician
     */
    public void processDeregister(String reason) {
        System.out.println("\n"+reason);
        if (Objects.equals(reason, "1: Error Registering")) {
            errorDeregister();
        } else if (Objects.equals(reason, "2: Disease Cured")) {
            confirmDiseaseCuring();
        } else if (Objects.equals(reason, "3: Receiver Deceased")) {
            showDeathDateDialog();
        } else if (Objects.equals(reason, "4: Successful Transplant")) {
            transplantDeregister();
        }

        System.out.println(Main.getWaitingListController().deregisterPressed);

        if (Main.getWaitingListController().deregisterPressed){
            Main.updateWaitingList();
        } else {
            Main.updateTransplantWaitingList();
        }
        deregisterReceiverButton.setDisable(true);
    }

    /**
     * Method to deregister an organ when a successful transplant is complete
     */
    public void transplantDeregister(){
        WaitingListItem selectedWaitingListItem;
        if (Main.getWaitingListController().getDeregisterPressed()){
            selectedWaitingListItem = (ReceiverWaitingListItem) Main.getWaitingListController().getWaitingList().getSelectionModel().getSelectedItem();
        } else {
            selectedWaitingListItem = (TransplantWaitingListItem) transplantTable.getSelectionModel().getSelectedItem();
        }
        User user = Main.getUserById(selectedWaitingListItem.getUserId());
        for (ReceiverWaitingListItem i: user.getWaitingListItems()) {
            if (i.getWaitingListItemId() == selectedWaitingListItem.getWaitingListItemId()) {
                i.deregisterOrgan(1);
                break;
            }
        }
    }

    /**
     * Removes an organ from the transplant waiting list and writes it as an error to the history log.
     */
    public void errorDeregister(){
        WaitingListItem selectedWaitingListItem;
        if (Main.getWaitingListController().getDeregisterPressed()){
            selectedWaitingListItem = (ReceiverWaitingListItem) Main.getWaitingListController().getWaitingList().getSelectionModel().getSelectedItem();

        } else {
            selectedWaitingListItem = (TransplantWaitingListItem) transplantTable.getSelectionModel().getSelectedItem();
        }
        User user = Main.getUserById(selectedWaitingListItem.getUserId());
        Long userId = user.getId();
        for (ReceiverWaitingListItem i: user.getWaitingListItems()) {
            if (Objects.equals(i.getWaitingListItemId(), selectedWaitingListItem.getWaitingListItemId())) {
                i.deregisterOrgan(1);
                History.prepareFileStringGUI(userId, "deregisterError");
                break;
            }
        }
    }

    /**
     * method to show dialog the prompts the user asking for a select receivers date of death
     */
    public void showDeathDateDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(Main.getDialogStyle());
        dialog.setTitle("Date of Death");
        dialog.setHeaderText("Please provide the date of death");

        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker deathDatePicker = new DatePicker();
        deathDatePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
        deathDatePicker.setValue(LocalDate.now());
        grid.add(deathDatePicker, 1, 1);
        dialog.getDialogPane().setContent(grid);



        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(option -> {
            if (result.get() == ButtonType.OK) {
                if (deathDatePicker.getValue() == null) {
                    DialogWindowController.showWarning("Invaild Date", "Date needs to be in format dd/mm/yyyy", "Please enter a date that is either today or earlier");
                    showDeathDateDialog();
                } else if (deathDatePicker.getValue().isAfter(LocalDate.now())) {
                    DialogWindowController.showWarning("Invaild Date", "Date is in the future", "Please enter a date that is either today or earlier");
                    showDeathDateDialog();
                } else {
                    deathDeregister(deathDatePicker.getValue());
                }
            }
        });
    }


    /**
     * Removes all organs waiting on transplant for a user.
     * Called when a receiver has deceased.
     * @param deathDateInput LocalDate date to be set for a users date of death
     */
    public void deathDeregister(LocalDate deathDateInput) {
        WaitingListItem selectedWaitingListItem;
        if (Main.getWaitingListController().getDeregisterPressed()){
            selectedWaitingListItem = Main.getWaitingListController().getWaitingList().getSelectionModel().getSelectedItem();
        } else {
            selectedWaitingListItem = (TransplantWaitingListItem) transplantTable.getSelectionModel().getSelectedItem();
        }
        User selectedUser = Main.getUserById(selectedWaitingListItem.getUserId());
        Long userId = selectedUser.getId();
        if (selectedUser.getWaitingListItems() != null) {
            History.prepareFileStringGUI(userId, "deregisterDeath");
            ArrayList<ReceiverWaitingListItem> tempItems = new ArrayList<>();
            for (ReceiverWaitingListItem item : selectedUser.getWaitingListItems()){
                ReceiverWaitingListItem temp = new ReceiverWaitingListItem(item);
                temp.deregisterOrgan(3);
                tempItems.add(temp);
            }
            selectedUser.getWaitingListItems().clear();
            selectedUser.getWaitingListItems().addAll(tempItems);
        }
        selectedUser.setDateOfDeath(deathDateInput);
        if (Main.getWaitingListController().getDeregisterPressed()){
            Main.getUserWindowController().populateUserFields();
        }
    }

    /**
     * Initilizes the gui display with the correct content in the table.
     * @param location Not Used
     * @param resources Not Used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        organColumn.setCellValueFactory(new PropertyValueFactory<>("organType"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("organRegisteredDate"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        transplantTable.setItems(transplantList);
        transplantTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
                            Main.setCurrentUser(Main.getUserById(row.getItem().getUserId()));
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
