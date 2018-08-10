package seng302.GUI.Controllers.Clinician;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.http.client.HttpResponseException;
import seng302.GUI.Controllers.User.UserController;
import seng302.Generic.Debugger;
import seng302.Generic.WindowManager;
import seng302.User.Attribute.Organ;
import seng302.User.Disease;
import seng302.User.User;
import seng302.User.WaitingListItem;
import tornadofx.control.DateTimePicker;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Class to handle the transplant waiting list window that displays all receivers waiting for an organ
 */
public class ClinicianWaitingListController implements Initializable {

    @FXML
    private TableView transplantTable;
    @FXML
    private TableColumn organColumn, nameColumn, dateColumn, regionColumn;
    @FXML
    private ComboBox organSearchComboBox;
    @FXML
    private TextField regionSearchTextField;
    @FXML
    private Button deregisterReceiverButton;

    private ObservableList<WaitingListItem> transplantList = FXCollections.observableArrayList();

    private String token;

    /**
     * Closes the application
     */
    public void close() {
        Platform.exit();
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Checks whether this waiting list has an API token.
     *
     * @return Whether this waiting list has an API token
     */
    public boolean hasToken() {
        return token != null;
    }

    /**
     * Updates the transplant waiting list table and checks if receiver is waiting not complete
     */
    public void updateTransplantList() {
        try {
            transplantList.clear();
            for(WaitingListItem item : WindowManager.getDataManager().getGeneral().getAllWaitingListItems(token)) {
                if (item.getStillWaitingOn()) {
                    addUserInfo(item);
                    transplantList.add(item);
                }
            }
            deregisterReceiverButton.setDisable(true);
        } catch (HttpResponseException e) {
            Debugger.error("Failed to retrieve all users and refresh transplant waiting list..");
        }
    }

    /**
     * adds the user info to a waiting list item
     * @param item the waiting list item to update
     */
    public void addUserInfo(WaitingListItem item) {
        try{
            User user = WindowManager.getDataManager().getUsers().getUser(item.getUserId().intValue(), token);
            item.setReceiverName(user.getName());
            item.setReceiverRegion(user.getRegion());
        } catch (HttpResponseException e) {
            Debugger.error("Failed to retrieve user with ID: " + item.getUserId());
        } catch (NullPointerException e) {

        }
    }

    /**
     * updates the transplant  list table and filters users by a region.
     *
     * @param regionSearch the search text to be applied to the user regions given by a user.
     * @param organSearch  the organ to specifically search for given by a user.
     */
    public void updateFoundUsersWithFiltering(String regionSearch, String organSearch) {
        try {
            transplantList.clear();
            Collection<User> users = WindowManager.getDataManager().getUsers().getAllUsers(token);
            for (User user : users) {
                for (WaitingListItem item : user.getWaitingListItems()) {
                    if (item.getStillWaitingOn()) {
                        if (organSearch.equals("None") || organSearch.equals(item.getOrganType().toString())) {
                            if(user.getOrgans().contains(item.getOrganType())){
                                item.setIsConflicting(true);
                            }

                            if (regionSearch.equals("") && (user.getRegion() == null) && item.getStillWaitingOn()) {
                                addUserInfo(item);
                                transplantList.add(item);
                            } else if ((user.getRegion() != null) && (user.getRegion().toLowerCase().contains(regionSearch.toLowerCase())) && item.getStillWaitingOn()) {
                                addUserInfo(item);
                                transplantList.add(item);
                            }
                        }
                    }
                }
            }
            deregisterReceiverButton.setDisable(true);
            //transplantTable.refresh();
        } catch (HttpResponseException e) {
            Debugger.error("Failed to retrieve all users and filter transplant waiting list.");
        }
    }

    /**
     * method to handle when the organ filter combo box is changed and then updates the transplant waiting list
     */
    public void updateFoundUsersOnOrganChange() {
        Debugger.log(organSearchComboBox.getValue().toString());
        Debugger.log(regionSearchTextField.getText());
        updateFoundUsersWithFiltering(regionSearchTextField.getText(), organSearchComboBox.getValue().toString());
    }

    /**
     * shows th ederegister dialog for a waiting list item
     */
    public void showDeregisterDialogFromClinicianList() {
        try {
            WaitingListItem selectedItem = (WaitingListItem) transplantTable.getSelectionModel().getSelectedItem();
            User user = WindowManager.getDataManager().getUsers().getUser(selectedItem.getUserId().intValue(), token);
            showDeregisterDialog(selectedItem, user);
            WindowManager.getDataManager().getUsers().updateUser(user, token);

            updateTransplantList();
        } catch (HttpResponseException e) {
            Debugger.error("Could not update waiting list item.");
        }
    }

    /**
     * Method to show de-registering reason code for a user to make a selection
     * @param user the user item for the deregister dialog
     * @param selectedItem The item being de registered.
     */
    public void showDeregisterDialog(WaitingListItem selectedItem, User user) {
        //Set dialog window
        List<String> reasonCodes = new ArrayList<>();
        reasonCodes.add("1: Error Registering");
        reasonCodes.add("2: Disease Cured");
        reasonCodes.add("3: Receiver Deceased");
        reasonCodes.add("4: Successful Transplant");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("4: Successful Transplant", reasonCodes);
        WindowManager.setIconAndStyle(dialog.getDialogPane());
        dialog.setTitle("De-Registering Reason Code");
        dialog.setHeaderText("Select a reason code");
        dialog.setContentText("Reason Code: ");

        //Get Input Code
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s -> processDeregister(s, selectedItem, user));
    }

    /**
     * method to call the correct handling de-registering method based on the reason code given
     *
     * @param reason the reason code given by the clinician
     */
    private void processDeregister(String reason, WaitingListItem selectedItem, User user) {
        try{
            if (Objects.equals(reason, "1: Error Registering")) {
                errorDeregister(selectedItem, user);
            } else if (Objects.equals(reason, "2: Disease Cured")) {
                confirmDiseaseCuring(selectedItem, user);
            } else if (Objects.equals(reason, "3: Receiver Deceased")) {
                showDeathDateDialog(selectedItem, user);
            } else if (Objects.equals(reason, "4: Successful Transplant")) {
                transplantDeregister(selectedItem, user);
            }

            WindowManager.updateUserWaitingLists();
            updateFoundUsersWithFiltering("", "None");
            deregisterReceiverButton.setDisable(true);

        } catch (HttpResponseException e) {
            Debugger.error("Failed to de-register waiting list item.");
        }
    }

    /**
     * Removes an organ from the transplant waiting list and writes it as an error to the history log.
     */
    private void errorDeregister(WaitingListItem selectedWaitingListItem, User user) throws HttpResponseException {
        Long userId = user.getId();
        deregisterWaitingListItem(selectedWaitingListItem,user,1);
    }

    /**
     * method to show dialog to confirm the curing of a disease and then to perform the operations.
     */
    private void confirmDiseaseCuring(WaitingListItem selectedWaitingListItem, User selectedUser) throws HttpResponseException {
        if (!selectedUser.getCurrentDiseases().isEmpty()) {
            Alert alert = WindowManager.createAlert(Alert.AlertType.CONFIRMATION, "Cure Disease?", "Would you like to select the cured disease?", "Cure a" +
                " Disease?");

            ButtonType buttonTypeOne = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne) {
                showDiseaseDeregisterDialog(selectedWaitingListItem, selectedUser);
            } else {
                deregisterWaitingListItem(selectedWaitingListItem,selectedUser,2);
                alert = WindowManager.createAlert(Alert.AlertType.INFORMATION, "De-Registered", "Organ transplant De-registered", "Reason " + "Code 2 selected. No disease cured");
                alert.showAndWait();
            }
        } else {
            deregisterWaitingListItem(selectedWaitingListItem,selectedUser,2);
            Alert alert = WindowManager.createAlert(Alert.AlertType.INFORMATION, "De-Registered", "Organ transplant De-registered", "Reason Code " + "2 selected. No disease cured");
            alert.showAndWait();
        }
    }

    /**
     * deregisters a waiting list item
     * @param item the waiting list item to deregister
     * @param user the user to remove the waiting list item from
     * @param code the code for why it was deregistered
     */
    public void deregisterWaitingListItem(WaitingListItem item, User user, int code) {
        for(WaitingListItem i : user.getWaitingListItems()) {
            if(i.getStillWaitingOn() && i.getOrganType().equals(item.getOrganType())) {
                i.deregisterOrgan(code);
                item.deregisterOrgan(code);
                for (UserController userController: WindowManager.getCliniciansUserWindows().values()) {
                    if (userController.getCurrentUser().equals(user)) {
                        userController.addHistoryEntry("Waiting list item deregistered", "A waiting list item (" + item.getOrganType() + ") was deregistered.");
                    }
                }
            }
        }
    }


    /**
     * method to show dialog for a clinician to choose from a receivers listed diseases, if any, to cure.
     */
    private void showDiseaseDeregisterDialog(WaitingListItem selectedWaitingListItem, User selectedUser) throws HttpResponseException {

        Dialog<ButtonType> dialog = new Dialog<>();
        WindowManager.setIconAndStyle(dialog.getDialogPane());
        dialog.setTitle("Cure Disease");
        dialog.setHeaderText("Select a disease to cure.");

        ButtonType cureButtonType = new ButtonType("Cure", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(cureButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ObservableList<Disease> diseaseComboList = FXCollections.observableArrayList(selectedUser.getCurrentDiseases());
        final ComboBox<Disease> diseaseComboBox = new ComboBox<>(diseaseComboList);
        diseaseComboBox.setCellFactory(new Callback<ListView<Disease>, ListCell<Disease>>() {
            @Override
            public ListCell<Disease> call(ListView<Disease> param) {
                return new ListCell<Disease>() {

                    @Override
                    protected void updateItem(Disease t, boolean bln) {
                        super.updateItem(t, bln);

                        if (t != null) {
                            setText(t.getName());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });

        grid.add(diseaseComboBox, 1, 1);
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(option -> {
            if (result.get() == cureButtonType) {
                if (diseaseComboBox.getValue() != null) {
                    Disease selected = diseaseComboBox.getValue();
                    selected.setCured(true);
                    ArrayList<Disease> currentDiseases = selectedUser.getCurrentDiseases();
                    ArrayList<Disease> curedDiseases = selectedUser.getCuredDiseases();
                    curedDiseases.add(selected);
                    selectedUser.setCuredDiseases(curedDiseases);
                    currentDiseases.remove(selected);
                    selectedUser.setCurrentDiseases(currentDiseases);

                    deregisterWaitingListItem(selectedWaitingListItem, selectedUser, 2);
                    Alert alert = WindowManager.createAlert(Alert.AlertType.INFORMATION, "De-Registered", "Organ transplant De-registered", "Reason Code 2 selected and disease cured");
                    alert.showAndWait();
                    for (UserController userController : WindowManager.getCliniciansUserWindows().values()) {
                        userController.updateDiseases();
                    }
                } else {
                    Alert alert = WindowManager.createAlert(Alert.AlertType.INFORMATION, "Invaild Disease", "Please Select a disease", "Select a " +
                        "disease to cure");
                    alert.showAndWait();
                    try {
                        showDiseaseDeregisterDialog(selectedWaitingListItem, selectedUser);
                    } catch (HttpResponseException e) {
                        Debugger.error("Failed to de-register waiting list item.");
                    }
                }
            } else {
                try {
                    confirmDiseaseCuring(selectedWaitingListItem, selectedUser);
                } catch (HttpResponseException e) {
                    Debugger.error("Failed to de-register waiting list item.");
                }
            }
        });
    }


    /**
     * method to show dialog the prompts the user asking for a select receivers date of death
     */
    private void showDeathDateDialog(WaitingListItem selectedItem, User user) throws HttpResponseException {
        Dialog<ButtonType> dialog = new Dialog<>();
        WindowManager.setIconAndStyle(dialog.getDialogPane());
        dialog.setTitle("Date of Death");
        dialog.setHeaderText("Please provide the date of death");

        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DateTimePicker deathDatePicker = new DateTimePicker();
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
            if (result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                if (deathDatePicker.getValue() == null) {
                    Alert alert = WindowManager.createAlert(Alert.AlertType.WARNING, "Invaild Date", "Date needs to be in format dd/mm/yyyy",
                        "Please enter a date that is either today or earlier");
                    alert.showAndWait();
                    try {
                        showDeathDateDialog(selectedItem, user);
                    } catch (HttpResponseException e) {
                        Debugger.error("Failed to de-register waiting list item.");
                    }
                } else if (deathDatePicker.getValue().isAfter(LocalDate.now())) {
                    Alert alert = WindowManager.createAlert(Alert.AlertType.WARNING, "Invaild Date", "Date is in the future", "Please enter a date " +
                        "that is either today or earlier");
                    alert.showAndWait();
                    try {
                        showDeathDateDialog(selectedItem, user);
                    } catch (HttpResponseException e) {
                        Debugger.error("Failed to de-register waiting list item.");
                    }
                } else {
                    try {
                        deathDeregister(deathDatePicker.getDateTimeValue(), selectedItem, user);
                    } catch (HttpResponseException e) {
                        Debugger.error("Failed to de-register waiting list item.");
                    }
                }
            }
        });
    }

    /**
     * Method to deregister an organ when a successful transplant is complete
     */
    private void transplantDeregister(WaitingListItem selectedWaitingListItem, User user) {
        deregisterWaitingListItem(selectedWaitingListItem, user, 4);
    }


    /**
     * Removes all organs waiting on transplant for a user.
     * Called when a receiver has deceased.
     *
     * @param deathDateInput LocalDate date to be set for a users date of death
     */
    private void deathDeregister(LocalDateTime deathDateInput, WaitingListItem selectedWaitingListItem, User selectedUser) throws HttpResponseException {
        deregisterWaitingListItem(selectedWaitingListItem,selectedUser,3);
        if (selectedUser.getWaitingListItems() != null) {
            for (WaitingListItem item : selectedUser.getWaitingListItems()) {
                deregisterWaitingListItem(item,selectedUser,3);
            }
        }
        selectedUser.setDateOfDeath(deathDateInput);

        for (UserController userController : WindowManager.getCliniciansUserWindows().values()) {
            if (userController.getCurrentUser() == selectedUser) {
                userController.populateUserAttributes();
                userController.addHistoryEntry("Waiting list entry deregistered", "Organ was deregistered from waiting list because the user died.");
            }
        }
    }

    /**
     * Initilizes the gui display with the correct content in the table.
     *
     * @param location  Not Used
     * @param resources Not Used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        organColumn.setCellValueFactory(new PropertyValueFactory<>("organType"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("receiverName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("organRegisteredDate"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("receiverRegion"));

        //transplantTable.setItems(transplantList);
        transplantTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        deregisterReceiverButton.setDisable(true);

        WindowManager.setTransplantWaitingListController(this);


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
        regionSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> updateFoundUsersWithFiltering(newValue,
                organSearchComboBox.getValue().toString()));

        transplantTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> deregisterReceiverButton
                .setDisable(false));

        transplantTable.setRowFactory(new Callback<TableView<WaitingListItem>, TableRow<WaitingListItem>>() {
            @Override
            public TableRow<WaitingListItem> call(TableView<WaitingListItem> tableView) {
                final TableRow<WaitingListItem> row = new TableRow<WaitingListItem>() {
                    @Override
                    public void updateItem(WaitingListItem item, boolean empty) {
                        super.updateItem(item, empty);
                        getStyleClass().remove("highlighted-row");
                        setTooltip(null);
                        if (item != null && !empty) {
                            if(item.isConflicting()) {
                                setTooltip(new Tooltip("User is currently donating this organ"));
                                if (!getStyleClass().contains("highlighted-row")) {
                                    getStyleClass().add("highlighted-row");
                                }
                            }
                        }
                    }
                };
                //event to open receiver profile when clicked
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getClickCount() == 2) {
                        try {
                            WindowManager.newAdminsUserWindow(WindowManager.getDataManager().getUsers().getUser(row.getItem().getUserId().intValue(), token), token);
                        } catch (HttpResponseException e) {
                            Debugger.error("COuld not open user window. Failed to fetch user with id: " + row.getItem().getUserId());
                        }
                    }
                });
                transplantTable.refresh();
                return row;
            }
        });
    }
}
