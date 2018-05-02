package seng302.GUI.Controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import seng302.Generic.Disease;
import seng302.Generic.History;
import seng302.Generic.IO;
import seng302.Generic.Main;
import seng302.User.User;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import static seng302.Generic.IO.streamOut;

public class MedicalHistoryDiseasesController extends PageController implements Initializable {
    @FXML
    private DatePicker dateOfDiagnosisInput;
    @FXML
    private TextField newDiseaseTextField;
    @FXML
    private TableView<Disease> currentDiseaseTableView, curedDiseaseTableView;
    @FXML
    private TableColumn<Disease, String> curedDiagnosisColumn, curedDateColumn, currentDiagnosisColumn, currentDateColumn;
    @FXML
    private CheckBox chronicCheckBox, isCuredCheckBox;
    @FXML
    private Label donorNameLabel;
    @FXML
    private Label newDiseaseLabel;
    @FXML
    private Label newDiseaseDateLabel;
    @FXML
    private Button addNewDiseaseButton;
    @FXML
    private Button deleteDiseaseButton;
    @FXML
    private Button saveDiseaseButton;
    @FXML
    private Button todayButton;

    private boolean sortCurrentDiagnosisAscending, sortCurrentDatesAscending, sortCurrentByDate;

    private boolean sortCuredDiagnosisAscending, sortCuredDatesAscending, sortCuredByDate;

    private User currentUser;

    private ObservableList<Disease> currentDiseaseItems, curedDiseaseItems;

    private Label currentDiagnosisColumnLabel, currentDateColumnLabel;

    private Label curedDiagnosisColumnLabel, curedDateColumnLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setMedicalHistoryDiseasesController(this);
        setupListeners();
        initialiseCurrentTableViewSorting();
        initialiseCuredTableViewSorting();
    }

    /**
     * Called when the 'Today' button is pressed -> fills in the current date
     */
    public void getCurrentDateFillDate() {
        dateOfDiagnosisInput.setValue(LocalDate.now());
    }

    /**
     * Adds a new disease to the unsaved Donor Diseases array list and then calls populate diseases to update the
     * current diseases list view.
     * Also checks for invalid input in both the disease text field and date field.
     */
    public void addNewDisease() {
        System.out.println("MedicalHistoryDiseasesController: Adding new disease");
        // Check for empty disease name
        if (newDiseaseTextField.getText().isEmpty()) {
            DialogWindowController.showWarning("Invalid Disease", "",
                    "Invalid disease name provided.");
            newDiseaseTextField.clear();
        // Check for an empty date
        } else if (dateOfDiagnosisInput.getValue() == null) {
            DialogWindowController.showWarning("Invalid Disease", "",
                    "No date provided.");
        // Check if the date of diagnosis was before the current user's birthday
        } else if (dateOfDiagnosisInput.getValue().isBefore(currentUser.getDateOfBirth())) {
            DialogWindowController.showWarning("Invalid Disease", "",
                    "Date of diagnosis before date of birth.");
            dateOfDiagnosisInput.setValue(null);
        // Check for a date in the future
        } else if (dateOfDiagnosisInput.getValue().isAfter(LocalDate.now())) {
            DialogWindowController.showWarning("Invalid Disease", "",
                    "Diagnosis date occurs in the future.");
            dateOfDiagnosisInput.setValue(null);
        } else if (isCuredCheckBox.isSelected() && chronicCheckBox.isSelected()){
            //TODO could make the checkboxs toeggle each other as only 1 can be selected
            DialogWindowController.showWarning("Invalid Disease", "",
                    "Disease cannot be chronic and cured.");
            isCuredCheckBox.setSelected(false);
            chronicCheckBox.setSelected(false);
        } else {
            // Add the new disease
            Disease diseaseToAdd = new Disease(newDiseaseTextField.getText(), dateOfDiagnosisInput.getValue(),
                    chronicCheckBox.isSelected(), isCuredCheckBox.isSelected());
            newDiseaseTextField.clear();
            dateOfDiagnosisInput.setValue(null);
            isCuredCheckBox.setSelected(false);
            chronicCheckBox.setSelected(false);
            if (diseaseToAdd.isCured()) {
                addCuredDisease(diseaseToAdd);
            } else {
                addCurrentDisease(diseaseToAdd);
            }
            System.out.println("MedicalHistoryDiseasesController: Finished adding new disease");
            statusIndicator.setStatus("Added " + diseaseToAdd, false);
            titleBar.saved(false);
        }
    }

    /**
     * Adds a disease marked as cured to the cured disease item list and presents in table.
     * @param diseaseToAdd new cured disease to add
     */
    private void addCuredDisease(Disease diseaseToAdd) {
        if (curedDiseaseItems.contains(diseaseToAdd)) {
            // Disease already exists in cured items
            DialogWindowController.showWarning("Invalid Disease", "",
                    "Disease already exists.");
        } else {
            curedDiseaseItems.add(diseaseToAdd);
            sortCurrentDiseases(false);
        }
    }

    /**
     * Adds a disease marked as not cured to the current disease item list and presents in table.
     * @param diseaseToAdd new uncured disease to add
     */
    private void addCurrentDisease(Disease diseaseToAdd) {
        if (currentDiseaseItems.contains(diseaseToAdd)) {
            // Disease already exists in cured items
            DialogWindowController.showWarning("Invalid Disease", "",
                    "Disease already exists.");
        } else {
            currentDiseaseItems.add(diseaseToAdd);
            sortCurrentDiseases(false);
        }
    }

    /**
     * Deletes a disease from either the current or cured list views for the donor.
     */
    public void deleteDisease() {
        System.out.println("MedicalHistoryDiseasesController: Deleting disease");

        if (currentDiseaseTableView.getSelectionModel().getSelectedItem() != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Are you sure?");
            alert.setHeaderText("Are you sure would like to delete the selected current disease? ");
            alert.setContentText("By doing so, the disease will be erased from the database.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Disease chosenDisease = currentDiseaseTableView.getSelectionModel().getSelectedItem();
                currentDiseaseItems.remove(chosenDisease);
                statusIndicator.setStatus("Removed " + chosenDisease, false);
                titleBar.saved(false);
            }
            alert.close();
        }

        else if (curedDiseaseTableView.getSelectionModel().getSelectedItem() != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Are you sure?");
            alert.setHeaderText("Are you sure would like to delete the selected cured disease? ");
            alert.setContentText("By doing so, the disease will be erased from the database.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Disease chosenDisease = curedDiseaseTableView.getSelectionModel().getSelectedItem();
                curedDiseaseItems.remove(chosenDisease);
                statusIndicator.setStatus("Removed " + chosenDisease, false);
                titleBar.saved(false);
            }
            alert.close();
        }
         
    }

    /**
     * Saves the current state of the donor's diseases for both their current and cured diseases.
     */
    public void save() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("Are you sure would like to update the current user? ");
        alert.setContentText("By doing so, the donor will be updated with the following disease details.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            currentUser.getCurrentDiseases().clear();
            currentUser.getCurrentDiseases().addAll(currentDiseaseItems);

            currentUser.getCuredDiseases().clear();
            currentUser.getCuredDiseases().addAll(curedDiseaseItems);

            IO.saveUsers(IO.getUserPath(), true);
            String text = History.prepareFileStringGUI(currentUser.getId(), "diseases");
            History.printToFile(streamOut, text);
            //populateHistoryTable();
            alert.close();
            statusIndicator.setStatus("Saved", false);
            titleBar.saved(true);
        } else {
            alert.close();
        }
    }

    /**
     * Creates a popup dialog to modify the name and date of the selectedDisease
     * @param selectedDisease disease to update information of
     * @param current
     */
    private void updateDiseasePopUp(Disease selectedDisease, boolean current) {

        // Create the custom dialog.
        Dialog<Pair<String, LocalDate>> dialog = new Dialog<>();
        dialog.setTitle("Update Disease");
        dialog.setHeaderText("Update Disease Details");

        // Set the button types.
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        TextField diseaseName = new TextField();
        diseaseName.setPromptText(selectedDisease.getName());
        diseaseName.setId("diseaseName");
        DatePicker dateOfDiagnosis = new DatePicker();
        //diseaseName.setText(selectedDisease.getName());
        dateOfDiagnosis.setPromptText(selectedDisease.getDiagnosisDate().toString());
        dateOfDiagnosis.setId("dateOfDiagnosis");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(diseaseName, 1, 0);
        grid.add(new Label("Date of Diagnosis:"), 0, 1);
        grid.add(dateOfDiagnosis, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
        updateButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        diseaseName.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButton.setDisable(newValue.trim().isEmpty());
        });

        dateOfDiagnosis.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) updateButton.setDisable(false);
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> diseaseName.requestFocus());

        // Convert the result to a diseaseName-dateOfDiagnosis-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                if (dateOfDiagnosis.getValue().isAfter(LocalDate.now())) {
                    DialogWindowController.showWarning("Invalid Disease", "",
                            "Diagnosis date occurs in the future.");
                    dateOfDiagnosis.setValue(null);
                    if(current) {
                        updateDiseasePopUp(selectedDisease, true);
                    } else {
                        updateDiseasePopUp(selectedDisease, false);
                    }

                } else {
                    return new Pair<>(diseaseName.getText(), dateOfDiagnosis.getValue());
                }
            }
            return null;
        });

        Optional<Pair<String, LocalDate>> result = dialog.showAndWait();

        result.ifPresent(newDiseaseDetails -> {
            System.out.println("Name=" + newDiseaseDetails.getKey() + ", DateOfDiagnosis=" + newDiseaseDetails.getValue());
            selectedDisease.setName(newDiseaseDetails.getKey());
            selectedDisease.setDiagnosisDate(newDiseaseDetails.getValue());
            if(current) {
                currentDiseaseItems.remove(selectedDisease);
                currentDiseaseItems.add(selectedDisease);
                sortCurrentDiseases(false);
                currentDiseaseTableView.refresh();
            } else {
                curedDiseaseItems.remove(selectedDisease);
                curedDiseaseItems.add(selectedDisease);
                sortCuredDiseases(false);
                curedDiseaseTableView.refresh();
            }
        });
    }

    /**
     * Initialises flags + listeners for the current disease table view sorting
     */
    private void initialiseCurrentTableViewSorting() {
        // Stops the default TableColumn sorting behaviour
        currentDiagnosisColumn.setSortable(false);
        currentDateColumn.setSortable(false);

        // Flag to toggle between sorting by date or diagnosis on the current disease TableView
        sortCurrentByDate = true;

        // Creates a label onto the current diagnosis column that can be clicked, and modified with emoji arrows ;)
        currentDiagnosisColumnLabel = new Label("Diagnosis");
        currentDiagnosisColumn.setGraphic(currentDiagnosisColumnLabel);
        sortCurrentDiagnosisAscending = true;

        // When the current diagnosis column label is clicked:
        currentDiagnosisColumnLabel.setOnMouseClicked(event -> {
            sortCurrentByDate = false;
            sortCurrentDiseases(true);
        });

        // Creates a label onto the current date column that can be clicked, and modified with emoji arrows ;)
        currentDateColumnLabel = new Label("Date");
        currentDateColumn.setGraphic(currentDateColumnLabel);
        sortCurrentDatesAscending = true;

        // When the current date column label is clicked:
        currentDateColumnLabel.setOnMouseClicked(event -> {
            sortCurrentByDate = true;
            sortCurrentDiseases(true);
        });
    }

    /**
     * Initialises flags + listeners for the cured disease table view sorting
     */
    private void initialiseCuredTableViewSorting() {
        // Stops the default TableColumn sorting behaviour
        curedDiagnosisColumn.setSortable(false);
        curedDateColumn.setSortable(false);

        // Flag to toggle between sorting by date or diagnosis on the cured disease TableView
        sortCuredByDate = true;

        // Creates a label onto the cured diagnosis column that can be clicked, and modified with emoji arrows ;)
        curedDiagnosisColumnLabel = new Label("Diagnosis");
        curedDiagnosisColumn.setGraphic(curedDiagnosisColumnLabel);
        sortCuredDiagnosisAscending = true;

        // When the cured diagnosis column label is clicked:
        curedDiagnosisColumnLabel.setOnMouseClicked(event -> {
            sortCuredByDate = false;
            sortCuredDiseases(true);
        });

        // Creates a label onto the cured date column that can be clicked, and modified with emoji arrows ;)
        curedDateColumnLabel = new Label("Date");
        curedDateColumn.setGraphic(curedDateColumnLabel);
        sortCuredDatesAscending = true;

        // When the cured date column label is clicked:
        curedDateColumnLabel.setOnMouseClicked(event -> {
            sortCuredByDate = true;
            sortCuredDiseases(true);
        });
    }

    /**
     * Sorts the current disease list according to flags sortCurrentDatesAscending and sortCurrentDiagnosisAscending
     * @param toggle whether to just keep with the current sort settings or to flip the order
     */
    private void sortCurrentDiseases(boolean toggle) {
        if (!sortCurrentByDate) {
            // Sort by diagnosis
            if (sortCurrentDiagnosisAscending) {
                if (toggle) {
                    sortCurrentDiagnosisAscending = false;
                    sortCurrentDiseases(false);
                    return;
                }
                FXCollections.sort(currentDiseaseItems, Disease.ascNameComparator);
                currentDiagnosisColumnLabel.setText("Diagnosis ⬆️️");
                currentDateColumnLabel.setText("Date");

            } else {
                if (toggle) {
                    sortCurrentDiagnosisAscending = true;
                    sortCurrentDiseases(false);
                    return;
                }
                FXCollections.sort(currentDiseaseItems, Disease.descNameComparator);
                currentDiagnosisColumnLabel.setText("Diagnosis ⬇️");
                currentDateColumnLabel.setText("Date");
            }
            currentDiseaseTableView.getSelectionModel().select(null);
            currentDiseaseTableView.setItems(currentDiseaseItems);
        } else {
            // Sort by date
            if (sortCurrentDatesAscending) {
                if (toggle) {
                    sortCurrentDatesAscending = false;
                    sortCurrentDiseases(false);
                    return;
                }
                FXCollections.sort(currentDiseaseItems, Disease.ascDateComparator);
                currentDiagnosisColumnLabel.setText("Diagnosis");
                currentDateColumnLabel.setText("Date ⬆️️");

            } else {
                if (toggle) {
                    sortCurrentDatesAscending = true;
                    sortCurrentDiseases(false);
                    return;
                }
                FXCollections.sort(currentDiseaseItems, Disease.descDateComparator);
                currentDiagnosisColumnLabel.setText("Diagnosis");
                currentDateColumnLabel.setText("Date ⬇️");
            }
            currentDiseaseTableView.getSelectionModel().select(null);
            currentDiseaseTableView.setItems(currentDiseaseItems);
        }
    }

    /**
     * Sorts the cured disease list according to flags sortCuredDatesAscending and sortCuredDiagnosisAscending
     * @param toggle whether to just keep with the cured sort settings or to flip the order
     */
    private void sortCuredDiseases(boolean toggle) {
        if (!sortCuredByDate) {
            // Sort by diagnosis
            if (sortCuredDiagnosisAscending) {
                if (toggle) {
                    sortCuredDiagnosisAscending = false;
                    sortCuredDiseases(false);
                    return;
                }
                FXCollections.sort(curedDiseaseItems, Disease.ascNameComparator);
                curedDiagnosisColumnLabel.setText("Diagnosis ⬆️️");
                curedDateColumnLabel.setText("Date");

            } else {
                if (toggle) {
                    sortCuredDiagnosisAscending = true;
                    sortCuredDiseases(false);
                    return;
                }
                FXCollections.sort(curedDiseaseItems, Disease.descNameComparator);
                curedDiagnosisColumnLabel.setText("Diagnosis ⬇️");
                curedDateColumnLabel.setText("Date");
            }
            curedDiseaseTableView.getSelectionModel().select(null);
            curedDiseaseTableView.setItems(curedDiseaseItems);
        } else {
            // Sort by date
            if (sortCuredDatesAscending) {
                if (toggle) {
                    sortCuredDatesAscending = false;
                    sortCuredDiseases(false);
                    return;
                }
                FXCollections.sort(curedDiseaseItems, Disease.ascDateComparator);
                curedDiagnosisColumnLabel.setText("Diagnosis");
                curedDateColumnLabel.setText("Date ⬆️️");

            } else {
                if (toggle) {
                    sortCuredDatesAscending = true;
                    sortCuredDiseases(false);
                    return;
                }
                FXCollections.sort(curedDiseaseItems, Disease.descDateComparator);
                curedDiagnosisColumnLabel.setText("Diagnosis");
                curedDateColumnLabel.setText("Date ⬇️");
            }
            curedDiseaseTableView.getSelectionModel().select(null);
            curedDiseaseTableView.setItems(curedDiseaseItems);
        }
    }

    /**
     * Creates required context menus and TableView related listeners to modify appearance of chronic diseases,
     * sort diseases by custom criteria and present the context menus
     */
    private void setupListeners() {
        final ContextMenu currentDiseaseListContextMenu = new ContextMenu();

        // Update selected disease on the current disease table
        MenuItem updateCurrentDiseaseMenuItem = new MenuItem();
        updateCurrentDiseaseMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Disease selectedDisease = currentDiseaseTableView.getSelectionModel().getSelectedItem();
                updateDiseasePopUp(selectedDisease, true);
                statusIndicator.setStatus("Edited " + selectedDisease, false);
                titleBar.saved(false);
            }
        });
        currentDiseaseListContextMenu.getItems().add(updateCurrentDiseaseMenuItem);

        // Toggle selected disease from current diseases as chronic
        MenuItem toggleCurrentChronicMenuItem = new MenuItem();
        toggleCurrentChronicMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Disease selectedDisease = currentDiseaseTableView.getSelectionModel().getSelectedItem();
                if (selectedDisease.isChronic()) {
                    selectedDisease.setChronic(false);
                    statusIndicator.setStatus("Marked " + selectedDisease + " as not chronic", false);
                } else {
                    selectedDisease.setChronic(true);
                    statusIndicator.setStatus("Marked " + selectedDisease + " as chronic", false);
                }
                titleBar.saved(false);
                selectedDisease.setDiagnosisDate(LocalDate.now());

                // To refresh the observableList to make chronic toggle visible
                currentDiseaseItems.remove(selectedDisease);
                currentDiseaseItems.add(selectedDisease);
                currentDiseaseTableView.refresh();
                sortCurrentDiseases(false);
            }
        });
        currentDiseaseListContextMenu.getItems().add(toggleCurrentChronicMenuItem);

        // Marks disease as cured from the current disease table and moves it to the cured disease table
        MenuItem setCuredMenuItem = new MenuItem();
        setCuredMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Disease selectedDisease = currentDiseaseTableView.getSelectionModel().getSelectedItem();
                selectedDisease.setCured(true);
                selectedDisease.setChronic(false);
                selectedDisease.setDiagnosisDate(LocalDate.now());

                currentDiseaseItems.remove(selectedDisease);
                curedDiseaseItems.add(selectedDisease);
                sortCuredDiseases(false);

                statusIndicator.setStatus("Marked " + selectedDisease + " as cured", false);
                titleBar.saved(false);

            }
        });
        currentDiseaseListContextMenu.getItems().add(setCuredMenuItem);


        final ContextMenu curedDiseaseContextMenu = new ContextMenu();

        // Update selected disease from the cured disease table
        MenuItem updateCuredDiseaseMenuItem = new MenuItem();
        updateCuredDiseaseMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Disease selectedDisease = curedDiseaseTableView.getSelectionModel().getSelectedItem();
                updateDiseasePopUp(selectedDisease, false);
                statusIndicator.setStatus("Edited " + selectedDisease, false);
                titleBar.saved(false);
            }
        });
        curedDiseaseContextMenu.getItems().add(updateCuredDiseaseMenuItem);

        // Set the selected disease from the cured disease table as chronic (move to current disease table also)
        MenuItem setCuredChronicDiseaseMenuItem = new MenuItem();
        setCuredChronicDiseaseMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Disease selectedDisease = curedDiseaseTableView.getSelectionModel().getSelectedItem();
                selectedDisease.setChronic(true);
                selectedDisease.setCured(false);
                selectedDisease.setDiagnosisDate(LocalDate.now());

                curedDiseaseItems.remove(selectedDisease);
                currentDiseaseItems.add(selectedDisease);
                sortCurrentDiseases(false);

                statusIndicator.setStatus("Marked " + selectedDisease + " as chronic", false);
                titleBar.saved(false);
            }
        });
        curedDiseaseContextMenu.getItems().add(setCuredChronicDiseaseMenuItem);

        // Set the selected disease from the cured disease table as uncured (move to current disease table also)
        MenuItem setUncuredMenuItem = new MenuItem();
        setUncuredMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Disease selectedDisease = curedDiseaseTableView.getSelectionModel().getSelectedItem();
                selectedDisease.setCured(false);
                selectedDisease.setDiagnosisDate(LocalDate.now());

                curedDiseaseItems.remove(selectedDisease);
                currentDiseaseItems.add(selectedDisease);
                sortCurrentDiseases(false);

                statusIndicator.setStatus("Marked " + selectedDisease + " as uncured", false);
                titleBar.saved(false);
            }
        });
        curedDiseaseContextMenu.getItems().add(setUncuredMenuItem);

        /*Handles the right click action of showing a ContextMenu on the currentDiseaseListView and sets the MenuItem
        text depending on the disease chosen*/
        currentDiseaseTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    Disease selectedDisease = currentDiseaseTableView.getSelectionModel().getSelectedItem();
                    if (selectedDisease.isChronic()) {
                        toggleCurrentChronicMenuItem.setText(String.format("Mark %s as not chronic",
                                selectedDisease.getName()));
                    } else {
                        toggleCurrentChronicMenuItem.setText(String.format("Mark %s as chronic",
                                selectedDisease.getName()));
                    }
                    setCuredMenuItem.setText(String.format("Mark %s as cured",
                            selectedDisease.getName()));
                    updateCurrentDiseaseMenuItem.setText("Update disease");
                    currentDiseaseListContextMenu.show(currentDiseaseTableView, event.getScreenX(), event.getScreenY());
                }
            }
        });

        /*Handles the right click action of showing a ContextMenu on the curedDiseaseTableView and sets the MenuItem
        text depending on the disease chosen*/
        curedDiseaseTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    Disease selectedDisease = curedDiseaseTableView.getSelectionModel().getSelectedItem();
                    if (selectedDisease.isChronic()) {
                        setCuredChronicDiseaseMenuItem.setText(String.format("Mark %s as not chronic",
                                selectedDisease.getName()));
                    } else {
                        setCuredChronicDiseaseMenuItem.setText(String.format("Mark %s as chronic",
                                selectedDisease.getName()));
                    }
                    setUncuredMenuItem.setText(String.format("Mark %s as uncured",
                            selectedDisease.getName()));
                    updateCuredDiseaseMenuItem.setText("Update disease");
                    curedDiseaseContextMenu.show(curedDiseaseTableView, event.getScreenX(), event.getScreenY());
                }
            }
        });


        // Sets the cell factory to style each Disease item depending on its details
        currentDiagnosisColumn.setCellFactory(column -> {
            return new TableCell<Disease, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {

                        setText(item);
                        Disease currentDisease = getTableView().getItems().get(getIndex());
                        // If the disease is chronic, update label + colour
                        if (currentDisease.isChronic()) {
                            setText("(CHRONIC) " + item);
                            this.setStyle("-fx-background-color: RED;");
                        } else {
                            setText(item);
                            // TODO missing the highlight style here
                            this.setStyle("-fx-background-color: #396a93;");
                        }
                    }
                }
            };
        });

        // Set up columns to extract correct information from a Disease object
        currentDiagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        currentDateColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosisDate"));

        curedDiagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        curedDateColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosisDate"));
    }

    /**
     * Sets whether the control buttons are shown or not on the medications pane
     */
    public void setControlsShown(boolean shown) {
        dateOfDiagnosisInput.setVisible(shown);
        addNewDiseaseButton.setVisible(shown);
        newDiseaseDateLabel.setVisible(shown);
        newDiseaseLabel.setVisible(shown);
        chronicCheckBox.setVisible(shown);
        newDiseaseTextField.setVisible(shown);
        deleteDiseaseButton.setVisible(shown);
        saveDiseaseButton.setVisible(shown);
        isCuredCheckBox.setVisible(shown);
        todayButton.setVisible(shown);
        currentDiseaseTableView.setDisable(!shown);
        curedDiseaseTableView.setDisable(!shown);
    }

    /**
     * Function to set the current donor of this class to that of the instance of the application.
     * @param currentUser The donor to set the current donor.
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        donorNameLabel.setText("User: " + currentUser.getName());

        currentDiseaseItems = FXCollections.observableArrayList();
        currentDiseaseItems.addAll(currentUser.getCurrentDiseases());
        currentDiseaseTableView.setItems(currentDiseaseItems);

        curedDiseaseItems = FXCollections.observableArrayList();
        curedDiseaseItems.addAll(currentUser.getCuredDiseases());
        curedDiseaseTableView.setItems(curedDiseaseItems);

        System.out.println("MedicalHistoryDiseasesController: Setting donor of Medical History pane...");
    }
}
