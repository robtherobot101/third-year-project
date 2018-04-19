package seng302.Controllers;

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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import seng302.Core.Disease;
import seng302.Core.Donor;
import seng302.Core.Main;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class MedicalHistoryController implements Initializable {
    @FXML
    private DatePicker dateOfDiagnosisInput;
    @FXML
    private TextField newDiseaseTextField;
    @FXML
    private ListView<Disease> currentDiseaseListView;
    @FXML
    private ListView<Disease> curedDiseaseListView;
    @FXML
    private CheckBox chronicCheckBox;
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


    private Donor currentDonor;

    private ArrayList<Disease> unsavedDonorDiseases = new ArrayList<>();
    private ObservableList<Disease> currentDiseaseItems = FXCollections.observableArrayList();
    private ObservableList<Disease> curedDiseaseItems = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setMedicalHistoryController(this);
        setupListeners();
    }

    /**
     * Adds a new disease to the unsaved Donor Diseases array list and then calls populate diseases to update the
     * current diseases list view.
     * Also checks for invalid input in both the disease text field and date field.
     */
    public void addNewDisease() {
        System.out.println("MedicalHistoryController: Adding new disease");
        // Check for empty disease name TODO could be a listener to disable the add button
        if (newDiseaseTextField.getText() == "") {
            DialogWindowController.showWarning("Invalid Disease", "",
                    "Invalid disease name provided.");
            newDiseaseTextField.clear();
        // Check for an empty date
        } else if (dateOfDiagnosisInput.getValue() == null) {
            DialogWindowController.showWarning("Invalid Disease", "",
                    "No date provided.");
        // Check for a date in the future
        } else if (dateOfDiagnosisInput.getValue().isAfter(LocalDate.now())) {
            DialogWindowController.showWarning("Invalid Disease", "",
                    "Diagnosis date occurs in the future.");
            dateOfDiagnosisInput.getEditor().clear();
        } else {
            // Add the new disease
            unsavedDonorDiseases.add(new Disease(newDiseaseTextField.getText(), dateOfDiagnosisInput.getValue(),
                    chronicCheckBox.isSelected()));
            populateDiseases(false);
            System.out.println("MedicalHistoryController: Finished adding new disease");
        }
    }

    /**
     * Deletes a disease from either the current or cured list views for the donor.
     */
    public void deleteDisease() {
        System.out.println("MedicalHistoryController: Deleting disease");

        if (currentDiseaseListView.getSelectionModel().getSelectedItem() != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Are you sure?");
            alert.setHeaderText("Are you sure would like to delete the selected current disease? ");
            alert.setContentText("By doing so, the disease will be erased from the database.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Disease chosenDisease = currentDiseaseListView.getSelectionModel().getSelectedItem();
                unsavedDonorDiseases.remove(chosenDisease);
                populateDiseases(false);
            }
            alert.close();
        }

        else if (curedDiseaseListView.getSelectionModel().getSelectedItem() != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Are you sure?");
            alert.setHeaderText("Are you sure would like to delete the selected cured disease? ");
            alert.setContentText("By doing so, the disease will be erased from the database.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Disease chosenDisease = curedDiseaseListView.getSelectionModel().getSelectedItem();
                unsavedDonorDiseases.remove(chosenDisease);
                populateDiseases(false);
            }
            alert.close();
        }

            //TODO create update for diseases for history when deleting
//            String text = History.prepareFileStringGUI(currentDonor.getId(), "update");
//            History.printToFile(streamOut, text);
            //populateHistoryTable();

    }

    /**
     * Saves the current state of the donor's diseases for both their current and cured diseases.
     */
    public void save() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("Are you sure would like to update the current donor? ");
        alert.setContentText("By doing so, the donor will be updated with the following disease details.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            currentDonor.getDiseases().clear();
            currentDonor.getDiseases().addAll(unsavedDonorDiseases);
            Main.saveUsers(Main.getDonorPath(), true);
            //TODO create update for diseases for history
//            String text = History.prepareFileStringGUI(currentDonor.getId(), "update");
//            History.printToFile(streamOut, text);
            //populateHistoryTable();
            alert.close();
        } else {
            alert.close();
        }
    }

    private void updateDiseasePopUp(Disease selectedDisease) {

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
        DatePicker dateOfDiagnosis = new DatePicker();
        dateOfDiagnosis.setPromptText(selectedDisease.getDiagnosisDate().toString());

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
                    dateOfDiagnosis.getEditor().clear();
                    updateDiseasePopUp(selectedDisease);
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
            populateDiseases(false);
        });
    }


    private void setupListeners() {
        final ContextMenu currentDiseaseListContextMenu = new ContextMenu();

        //Update current menu item

        MenuItem updateCurrentDiseaseMenuItem = new MenuItem();
        updateCurrentDiseaseMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Disease selectedDisease = currentDiseaseListView.getSelectionModel().getSelectedItem();
                updateDiseasePopUp(selectedDisease);

            }
        });

        currentDiseaseListContextMenu.getItems().add(updateCurrentDiseaseMenuItem);

        //-----

        MenuItem toggleCurrentChronicMenuItem = new MenuItem();
        toggleCurrentChronicMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Disease selectedDisease = currentDiseaseListView.getSelectionModel().getSelectedItem();
                if (selectedDisease.isChronic()) {
                    selectedDisease.setChronic(false);
                } else {
                    selectedDisease.setChronic(true);
                }
                selectedDisease.setDiagnosisDate(LocalDate.now());
                populateDiseases(false);
            }
        });

        MenuItem setCuredMenuItem = new MenuItem();
        setCuredMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Disease selectedDisease = currentDiseaseListView.getSelectionModel().getSelectedItem();
                selectedDisease.setCured(true);
                selectedDisease.setChronic(false);
                selectedDisease.setDiagnosisDate(LocalDate.now());
                populateDiseases(false);
            }
        });
        currentDiseaseListContextMenu.getItems().add(toggleCurrentChronicMenuItem);
        currentDiseaseListContextMenu.getItems().add(setCuredMenuItem);


        final ContextMenu curedDiseaseContextMenu = new ContextMenu();

        //Update cured menu item

        MenuItem updateCuredDiseaseMenuItem = new MenuItem();
        updateCuredDiseaseMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Disease selectedDisease = curedDiseaseListView.getSelectionModel().getSelectedItem();
                updateDiseasePopUp(selectedDisease);
            }
        });

        curedDiseaseContextMenu.getItems().add(updateCuredDiseaseMenuItem);

        //-----

        MenuItem toggleCuredChronicMenuItem = new MenuItem();
        toggleCuredChronicMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Disease selectedDisease = curedDiseaseListView.getSelectionModel().getSelectedItem();
                selectedDisease.setChronic(true);
                selectedDisease.setCured(false);
                selectedDisease.setDiagnosisDate(LocalDate.now());
                populateDiseases(false);
            }
        });

        MenuItem setUncuredMenuItem = new MenuItem();
        setUncuredMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Disease selectedDisease = curedDiseaseListView.getSelectionModel().getSelectedItem();
                selectedDisease.setCured(false);
                selectedDisease.setDiagnosisDate(LocalDate.now());
                populateDiseases(false);
            }
        });

        curedDiseaseContextMenu.getItems().add(toggleCuredChronicMenuItem);
        curedDiseaseContextMenu.getItems().add(setUncuredMenuItem);


        currentDiseaseListView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    Disease selectedDisease = currentDiseaseListView.getSelectionModel().getSelectedItem();
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
                    currentDiseaseListContextMenu.show(currentDiseaseListView, event.getScreenX(), event.getScreenY());
                }
            }
        });

        curedDiseaseListView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    Disease selectedDisease = curedDiseaseListView.getSelectionModel().getSelectedItem();
                    if (selectedDisease.isChronic()) {
                        toggleCuredChronicMenuItem.setText(String.format("Mark %s as not chronic",
                                selectedDisease.getName()));
                    } else {
                        toggleCuredChronicMenuItem.setText(String.format("Mark %s as chronic",
                                selectedDisease.getName()));
                    }
                    setUncuredMenuItem.setText(String.format("Mark %s as uncured",
                            selectedDisease.getName()));
                    updateCuredDiseaseMenuItem.setText("Update disease");
                    curedDiseaseContextMenu.show(curedDiseaseListView, event.getScreenX(), event.getScreenY());
                }
            }
        });

        currentDiseaseListView.setCellFactory(lv -> new ListCell<Disease>() {;
            @Override
            protected void updateItem(Disease d, boolean empty) {
                super.updateItem(d, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {

                    if (d.isChronic()) {
                        setText(d.getName() + " at " + d.getDiagnosisDate() + " (CHRONIC)");
                        setStyle("-fx-background-color: #ff0000");
                    } else {
                        setText(d.getName() + " at " + d.getDiagnosisDate());
                        setStyle("");
                    }
                }
            }
        });

        curedDiseaseListView.setCellFactory(lv -> new ListCell<Disease>() {;
            @Override
            protected void updateItem(Disease d, boolean empty) {
                super.updateItem(d, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    if (d.isChronic()) {
                        setText(d.getName() + " at " + d.getDiagnosisDate() + " (CHRONIC)");
                        setStyle("-fx-background-color: #ff0000");
                    } else {
                        setText(d.getName() + " at " + d.getDiagnosisDate());
                        setStyle("");
                    }
                }
            }
        });
    }

    /**
     * Populates both list views based on the current status of the current donors medication status
     * and past medications. Must act differently for when starting and mid change.
     */
    public void populateDiseases(boolean isSetup) {
        System.out.println("MedicalHistoryController: Populating tables...");

        try {
            //Populate table for current medications
            currentDiseaseItems.clear();
            curedDiseaseItems.clear();
            if (isSetup) {
                for (Disease d : currentDonor.getDiseases()) {
                    if (d.isCured()) {
                        curedDiseaseItems.add(d);
                    } else {
                        currentDiseaseItems.add(d);
                    }
                }
            } else {
                for (Disease d : unsavedDonorDiseases) {
                    if (d.isCured()) {
                        curedDiseaseItems.add(d);
                    } else {
                        currentDiseaseItems.add(d);
                    }
                }
            }

            currentDiseaseListView.setItems(currentDiseaseItems);
            curedDiseaseListView.setItems(curedDiseaseItems);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        currentDiseaseListView.setDisable(!shown);
        curedDiseaseListView.setDisable(!shown);
    }

    /**
     * Function to set the current donor of this class to that of the instance of the application.
     * @param currentDonor The donor to set the current donor.
     */
    public void setCurrentDonor(Donor currentDonor) {
        this.currentDonor = currentDonor;
        donorNameLabel.setText("Donor: " + currentDonor.getName());
        //unsavedDonorDiseases = currentDonor.getDiseases();
        //pastDiseasesCopy = currentDonor.getCuredDiseases();
        System.out.println("MedicalHistoryController: Setting donor of Medical History pane...");
    }
}
