package seng302.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import seng302.Core.Disease;
import seng302.Core.Donor;
import seng302.Core.Main;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
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

    private Donor currentDonor;

    private ArrayList<Disease> unsavedDonorDiseases = new ArrayList<>();
    private ObservableList<Disease> currentDiseaseItems = FXCollections.observableArrayList();
    private ObservableList<Disease> curedDiseaseItems = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setMedicalHistoryController(this);
        setupListeners();
    }

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


    private void setupListeners() {
        final ContextMenu currentDiseaseListContextMenu = new ContextMenu();
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
                    setText(d.getName() + " at " + d.getDiagnosisDate());
                    if (d.isChronic()) {
                        setStyle("-fx-background-color: #ff0000");
                    } else {
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
                    setText(d.getName() + " at " + d.getDiagnosisDate());
                    if (d.isChronic()) {
                        setStyle("-fx-background-color: #ff0000");
                    } else {
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
        //TODO toggle controls of finished window
    }

    /**
     * Function to set the current donor of this class to that of the instance of the application.
     * @param currentDonor The donor to set the current donor.
     */
    public void setCurrentDonor(Donor currentDonor) {
        this.currentDonor = currentDonor;
        donorNameLabel.setText(currentDonor.getName());
        //unsavedDonorDiseases = currentDonor.getDiseases();
        //pastDiseasesCopy = currentDonor.getCuredDiseases();
        System.out.println("MedicalHistoryController: Setting donor of Medical History pane...");
    }
}
