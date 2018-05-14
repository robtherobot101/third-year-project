package seng302.GUI.Controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import seng302.Generic.DataManager;
import seng302.Generic.Disease;
import seng302.Generic.History;
import seng302.Generic.ReceiverWaitingListItem;
import seng302.Generic.SearchUtils;
import seng302.Generic.TransplantWaitingListItem;
import seng302.Generic.WaitingListItem;
import seng302.Generic.WindowManager;
import seng302.User.Attribute.Organ;
import seng302.User.User;

/**
 * Class to handle the transplant waiting list window that displays all receivers waiting for an organ
 */
public class TransplantWaitingListController implements Initializable {

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

    private ObservableList<TransplantWaitingListItem> transplantList = FXCollections.observableArrayList();

    /**
     * Closes the application
     */
    public void close() {
        Platform.exit();
    }

    /**
     * Updates the transplant waiting list table and checks if reciever is waiting not complete
     */
    public void updateTransplantList() {
        transplantList.clear();
        for (User user : DataManager.users) {
            for (ReceiverWaitingListItem item : user.getWaitingListItems()) {
                List<Integer> codes = Arrays.asList(1, 2, 3, 4);
                if (!(item.getOrganRegisteredDate() == null) && !(codes.contains(item.getOrganDeregisteredCode()))) {
                    transplantList.add(new TransplantWaitingListItem(user.getName(), user.getRegion(), item.getOrganRegisteredDate(), item
                        .getOrganType(), user.getId(), item.getWaitingListItemId()));
                }
            }
        }
        deregisterReceiverButton.setDisable(true);
    }

    /**
     * updates the transplant waiting list table and filters users by a region.
     *
     * @param regionSearch the search text to be applied to the user regions given by a user.
     * @param organSearch  the organ to specifically search for given by a user.
     */
    public void updateFoundUsersWithFiltering(String regionSearch, String organSearch) {
        transplantList.clear();
        for (User user : DataManager.users) {
            for (ReceiverWaitingListItem item : user.getWaitingListItems()) {
                if (!(item.getOrganRegisteredDate() == null)) {
                    if (organSearch.equals("None") || organSearch.equals(item.getOrganType().toString())) {
                        List<Integer> codes = Arrays.asList(1, 2, 3, 4);
                        if (regionSearch.equals("") && (user.getRegion() == null) && !(item.getOrganRegisteredDate() == null) && !(codes
                            .contains(item.getOrganDeregisteredCode()))) {
                            transplantList.add(new TransplantWaitingListItem(user.getName(), user.getRegion(), item.getOrganRegisteredDate(),
                                item.getOrganType(), user.getId(), item.getWaitingListItemId()));
                        } else if ((user.getRegion() != null) && (user.getRegion().toLowerCase().contains(regionSearch.toLowerCase())) && !
                            (item.getOrganRegisteredDate() == null) && !(codes.contains(item.getOrganDeregisteredCode()))) {
                            transplantList.add(new TransplantWaitingListItem(user.getName(), user.getRegion(), item.getOrganRegisteredDate(),
                                item.getOrganType(), user.getId(), item.getWaitingListItemId()));
                        }
                    }
                }
            }
        }
        deregisterReceiverButton.setDisable(true);
    }

    /**
     * method to handle when the organ filter combo box is changed and then updates the transplant waiting list
     */
    public void updateFoundUsersOnOrganChange() {
        updateFoundUsersWithFiltering(regionSearchTextField.getText(), organSearchComboBox.getValue().toString());
    }

    public void showDeregisterDialogFromClinicianList() {
        showDeregisterDialog((TransplantWaitingListItem) transplantTable.getSelectionModel().getSelectedItem());
    }

    /**
     * Method to show de-registering reason code for a user to make a selection
     */
    public void showDeregisterDialog(WaitingListItem selectedItem) {
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
        result.ifPresent(s -> processDeregister(s, selectedItem));
    }

    /**
     * method to call the correct handling de-registering method based on the reason code given
     *
     * @param reason the reason code given by the clinician
     */
    private void processDeregister(String reason, WaitingListItem selectedItem) {
        if (Objects.equals(reason, "1: Error Registering")) {
            errorDeregister(selectedItem);
        } else if (Objects.equals(reason, "2: Disease Cured")) {
            confirmDiseaseCuring(selectedItem);
        } else if (Objects.equals(reason, "3: Receiver Deceased")) {
            showDeathDateDialog(selectedItem);
        } else if (Objects.equals(reason, "4: Successful Transplant")) {
            transplantDeregister(selectedItem);
        }

        updateFoundUsersWithFiltering("", "None");
        WindowManager.updateUserWaitingLists();
        deregisterReceiverButton.setDisable(true);
    }

    /**
     * Removes an organ from the transplant waiting list and writes it as an error to the history log.
     */
    private void errorDeregister(WaitingListItem selectedWaitingListItem) {
        User user = SearchUtils.getUserById(selectedWaitingListItem.getUserId());
        Long userId = user.getId();
        for (ReceiverWaitingListItem i : user.getWaitingListItems()) {
            if (Objects.equals(i.getWaitingListItemId(), selectedWaitingListItem.getWaitingListItemId())) {
                i.deregisterOrgan(1);
                History.prepareFileStringGUI(userId, "deregisterError");
                break;
            }
        }
    }

    /**
     * method to show dialog to confirm the curing of a disease and then to perform the operations.
     */
    private void confirmDiseaseCuring(WaitingListItem selectedWaitingListItem) {
        User selectedUser = SearchUtils.getUserById(selectedWaitingListItem.getUserId());
        if (!selectedUser.getCurrentDiseases().isEmpty()) {
            Alert alert = WindowManager.createAlert(Alert.AlertType.CONFIRMATION, "Cure Disease?", "Would you like to select the cured disease?", "Cure a" +
                " Disease?");
            alert.showAndWait();

            ButtonType buttonTypeOne = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType buttonTypeCancel = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne) {
                showDiseaseDeregisterDialog(selectedWaitingListItem);
            } else {
                ArrayList<ReceiverWaitingListItem> selectedUserWaitingListItems = selectedUser.getWaitingListItems();
                for (ReceiverWaitingListItem i : selectedUserWaitingListItems) {
                    if (i.getWaitingListItemId().equals(selectedWaitingListItem.getWaitingListItemId())) {
                        i.deregisterOrgan(2);
                        alert = WindowManager.createAlert(Alert.AlertType.INFORMATION, "De-Registered", "Organ transplant De-registered", "Reason " +
                            "Code 2 selected. No disease cured");
                        alert.showAndWait();
                        break;
                    }
                }
            }
        } else {
            ArrayList<ReceiverWaitingListItem> selectedUserWaitingListItems = selectedUser.getWaitingListItems();
            for (ReceiverWaitingListItem i : selectedUserWaitingListItems) {
                if (i.getWaitingListItemId().equals(selectedWaitingListItem.getWaitingListItemId())) {
                    i.deregisterOrgan(2);
                    Alert alert = WindowManager.createAlert(Alert.AlertType.INFORMATION, "De-Registered", "Organ transplant De-registered", "Reason Code " +
                        "2 selected. No disease cured");
                    alert.showAndWait();
                    break;
                }
            }
        }
    }



    /**
     * method to show dialog for a clinician to choose from a receivers listed diseases, if any, to cure.
     */
    private void showDiseaseDeregisterDialog(WaitingListItem selectedWaitingListItem) {
        User selectedUser = SearchUtils.getUserById(selectedWaitingListItem.getUserId());

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
                    ArrayList<ReceiverWaitingListItem> selectedUserWaitingListItems = selectedUser.getWaitingListItems();
                    ArrayList<Disease> currentDiseases = selectedUser.getCurrentDiseases();
                    ArrayList<Disease> curedDiseases = selectedUser.getCuredDiseases();
                    curedDiseases.add(selected);
                    selectedUser.setCuredDiseases(curedDiseases);
                    currentDiseases.remove(selected);
                    selectedUser.setCurrentDiseases(currentDiseases);
                    for (ReceiverWaitingListItem i : selectedUserWaitingListItems) {
                        if (i.getWaitingListItemId().equals(selectedWaitingListItem.getWaitingListItemId())) {
                            i.deregisterOrgan(2);
                            Alert alert = WindowManager.createAlert(Alert.AlertType.INFORMATION, "De-Registered", "Organ transplant De-registered",
                                "Reason Code 2 selected and disease cured");
                            alert.showAndWait();
                            for (UserWindowController userWindowController: WindowManager.getCliniciansUserWindows().values()) {
                                userWindowController.updateDiseases();
                            }
                            break;
                        }
                    }
                } else {
                    Alert alert = WindowManager.createAlert(Alert.AlertType.INFORMATION, "Invaild Disease", "Please Select a disease", "Select a " +
                        "disease to cure");
                    alert.showAndWait();
                    showDiseaseDeregisterDialog(selectedWaitingListItem);
                }
            } else {
                confirmDiseaseCuring(selectedWaitingListItem);
            }
        });
    }


    /**
     * method to show dialog the prompts the user asking for a select receivers date of death
     */
    private void showDeathDateDialog(WaitingListItem selectedItem) {
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
            System.out.println(result.get());
            if (result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                if (deathDatePicker.getValue() == null) {
                    Alert alert = WindowManager.createAlert(Alert.AlertType.WARNING, "Invaild Date", "Date needs to be in format dd/mm/yyyy",
                        "Please enter a date that is either today or earlier");
                    alert.showAndWait();
                    showDeathDateDialog(selectedItem);
                } else if (deathDatePicker.getValue().isAfter(LocalDate.now())) {
                    Alert alert = WindowManager.createAlert(Alert.AlertType.WARNING, "Invaild Date", "Date is in the future", "Please enter a date " +
                        "that is either today or earlier");
                    alert.showAndWait();
                    showDeathDateDialog(selectedItem);
                } else {
                    deathDeregister(deathDatePicker.getValue(), selectedItem);
                }
            }
        });
    }

    /**
     * Method to deregister an organ when a successful transplant is complete
     */
    private void transplantDeregister(WaitingListItem selectedWaitingListItem) {
        User user = SearchUtils.getUserById(selectedWaitingListItem.getUserId());
        for (ReceiverWaitingListItem i : user.getWaitingListItems()) {
            if (i.getWaitingListItemId().equals(selectedWaitingListItem.getWaitingListItemId())) {
                i.deregisterOrgan(1);
                break;
            }
        }
    }


    /**
     * Removes all organs waiting on transplant for a user.
     * Called when a receiver has deceased.
     *
     * @param deathDateInput LocalDate date to be set for a users date of death
     */
    private void deathDeregister(LocalDate deathDateInput, WaitingListItem selectedWaitingListItem) {
        User selectedUser = SearchUtils.getUserById(selectedWaitingListItem.getUserId());
        Long userId = selectedUser.getId();

        if (selectedUser.getWaitingListItems() != null) {
            History.prepareFileStringGUI(userId, "deregisterDeath");
            ArrayList<ReceiverWaitingListItem> tempItems = new ArrayList<>();
            for (ReceiverWaitingListItem item : selectedUser.getWaitingListItems()) {
                ReceiverWaitingListItem temp = new ReceiverWaitingListItem(item);
                temp.deregisterOrgan(3);
                tempItems.add(temp);
            }
            selectedUser.getWaitingListItems().clear();
            selectedUser.getWaitingListItems().addAll(tempItems);
        }
        selectedUser.setDateOfDeath(deathDateInput);
        for (UserWindowController userWindowController : WindowManager.getCliniciansUserWindows().values()) {
            if (userWindowController.getCurrentUser() == selectedUser) {
                userWindowController.populateUserAttributes();
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
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("organRegisteredDate"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        transplantTable.setItems(transplantList);
        transplantTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        deregisterReceiverButton.setDisable(true);

        WindowManager.setTransplantWaitingListController(this);

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
        regionSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> updateFoundUsersWithFiltering(newValue,
                organSearchComboBox.getValue().toString()));

        transplantTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> deregisterReceiverButton
                .setDisable(false));

        transplantTable.setRowFactory(new Callback<TableView<TransplantWaitingListItem>, TableRow<TransplantWaitingListItem>>() {
            @Override
            public TableRow<TransplantWaitingListItem> call(TableView<TransplantWaitingListItem> tableView) {
                final TableRow<TransplantWaitingListItem> row = new TableRow<TransplantWaitingListItem>() {
                    @Override
                    public void updateItem(TransplantWaitingListItem item, boolean empty) {
                        super.updateItem(item, empty);
                        getStyleClass().remove("highlighted-row");
                        setTooltip(null);
                        if (item != null && !empty) {
                            if (SearchUtils.getUserById(item.getUserId()).getOrgans().contains(item.getOrganType())) {
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
                        WindowManager.newCliniciansUserWindow(SearchUtils.getUserById(row.getItem().getUserId()));
                    }
                });
                transplantTable.refresh();
                return row;

            }
        });
    }
}
