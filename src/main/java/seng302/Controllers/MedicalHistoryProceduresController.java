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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Pair;
import seng302.Core.Disease;
import seng302.Core.Main;
import seng302.Core.Procedure;
import seng302.Core.User;

import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

public class MedicalHistoryProceduresController implements Initializable {

    @FXML
    private DatePicker dateOfProcedureInput;
    @FXML
    private TextField summaryInput, descriptionInput;
    @FXML
    private TableView<Procedure> previousProcedureTableView, pendingProcedureTableView;
    @FXML
    private TableColumn<Procedure, String> previousSummaryColumn, previousDescriptionColumn, previousDateColumn, pendingSummaryColumn, pendingDescriptionColumn, pendingDateColumn;
    @FXML
    private Label donorNameLabel;

    private User currentUser;

    private ObservableList<Procedure> pendingProcedureItems, previousProcedureItems;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setMedicalHistoryProceduresController(this);
        setupListeners();


    }

    /**
     * Adds a new procedure to the unsaved Donor Procedures array list.
     * Also checks for invalid input in both the disease text field and date field.
     */
    public void addNewProcedure() {
        System.out.println("MedicalHistoryProceduresController: Adding new procedure");
        // Check for empty disease name TODO could be a listener to disable the add button
        if (summaryInput.getText() == "") {
            DialogWindowController.showWarning("Invalid Procedure", "",
                    "Invalid procedure summary provided.");
            summaryInput.clear();
            // Check for an empty date
        } else if (descriptionInput.getText() == "") {
            DialogWindowController.showWarning("Invalid Procedure", "",
                    "Invalid procedure description provided.");
            descriptionInput.clear();
        }
        else if (dateOfProcedureInput.getValue() == null) {
            DialogWindowController.showWarning("Invalid Procedure", "",
                    "No date provided.");
            // Check for a date in the future
//        } else if (dateOfDiagnosisInput.getValue().isAfter(LocalDate.now())) {
//            DialogWindowController.showWarning("Invalid Disease", "",
//                    "Diagnosis date occurs in the future.");
//            dateOfDiagnosisInput.getEditor().clear();
        } else {
            // Add the new procedure
            Procedure procedureToAdd = new Procedure(summaryInput.getText(), descriptionInput.getText(),
                    dateOfProcedureInput.getValue());
            pendingProcedureItems.add(procedureToAdd);
            System.out.println("MedicalHistoryProceduresController: Finished adding new procedure");
        }
    }

//
//    private void addCurrentDisease(Disease diseaseToAdd) {
//        if (currentDiseaseItems.contains(diseaseToAdd)) {
//            // Disease already exists in cured items
//            DialogWindowController.showWarning("Invalid Disease", "",
//                    "Disease already exists.");
//        } else {
//            currentDiseaseItems.add(diseaseToAdd);
//        }
//    }
//
//    /**
//     * Deletes a disease from either the current or cured list views for the donor.
//     */
//    public void deleteDisease() {
//        System.out.println("MedicalHistoryDiseasesController: Deleting disease");
//
//        if (currentDiseaseTableView.getSelectionModel().getSelectedItem() != null) {
//
//            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//            alert.setTitle("Are you sure?");
//            alert.setHeaderText("Are you sure would like to delete the selected current disease? ");
//            alert.setContentText("By doing so, the disease will be erased from the database.");
//            Optional<ButtonType> result = alert.showAndWait();
//            if (result.get() == ButtonType.OK) {
//                Disease chosenDisease = currentDiseaseTableView.getSelectionModel().getSelectedItem();
//                currentDiseaseItems.remove(chosenDisease);
//            }
//            alert.close();
//        }
//
//        else if (curedDiseaseTableView.getSelectionModel().getSelectedItem() != null) {
//
//            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//            alert.setTitle("Are you sure?");
//            alert.setHeaderText("Are you sure would like to delete the selected cured disease? ");
//            alert.setContentText("By doing so, the disease will be erased from the database.");
//            Optional<ButtonType> result = alert.showAndWait();
//            if (result.get() == ButtonType.OK) {
//                Disease chosenDisease = curedDiseaseTableView.getSelectionModel().getSelectedItem();
//                curedDiseaseItems.remove(chosenDisease);
//            }
//            alert.close();
//        }
//
//        //TODO create update for diseases for history when deleting
////            String text = History.prepareFileStringGUI(currentDonor.getId(), "update");
////            History.printToFile(streamOut, text);
//        //populateHistoryTable();
//
//    }
//
//    /**
//     * Saves the current state of the donor's diseases for both their current and cured diseases.
//     */
//    public void save() {
//
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Are you sure?");
//        alert.setHeaderText("Are you sure would like to update the current donor? ");
//        alert.setContentText("By doing so, the donor will be updated with the following disease details.");
//        Optional<ButtonType> result = alert.showAndWait();
//        if (result.get() == ButtonType.OK) {
//            currentDonor.getCurrentDiseases().clear();
//            currentDonor.getCurrentDiseases().addAll(currentDiseaseItems);
//
//            currentDonor.getCuredDiseases().clear();
//            currentDonor.getCuredDiseases().addAll(curedDiseaseItems);
//
//            Main.saveUsers(Main.getUserPath(), true);
//            //TODO create update for diseases for history
////            String text = History.prepareFileStringGUI(currentDonor.getId(), "update");
////            History.printToFile(streamOut, text);
//            //populateHistoryTable();
//            alert.close();
//        } else {
//            alert.close();
//        }
//    }
//
//    private void updateDiseasePopUp(Disease selectedDisease, boolean current) {
//
//        // Create the custom dialog.
//        Dialog<Pair<String, LocalDate>> dialog = new Dialog<>();
//        dialog.setTitle("Update Disease");
//        dialog.setHeaderText("Update Disease Details");
//
//        // Set the button types.
//        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
//        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);
//
//        // Create the username and password labels and fields.
//        GridPane grid = new GridPane();
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(20, 10, 10, 10));
//
//        TextField diseaseName = new TextField();
//        diseaseName.setPromptText(selectedDisease.getName());
//        DatePicker dateOfDiagnosis = new DatePicker();
//        dateOfDiagnosis.setPromptText(selectedDisease.getDiagnosisDate().toString());
//
//        grid.add(new Label("Name:"), 0, 0);
//        grid.add(diseaseName, 1, 0);
//        grid.add(new Label("Date of Diagnosis:"), 0, 1);
//        grid.add(dateOfDiagnosis, 1, 1);
//
//        // Enable/Disable login button depending on whether a username was entered.
//        Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
//        updateButton.setDisable(true);
//
//        // Do some validation (using the Java 8 lambda syntax).
//        diseaseName.textProperty().addListener((observable, oldValue, newValue) -> {
//            updateButton.setDisable(newValue.trim().isEmpty());
//        });
//
//        dateOfDiagnosis.focusedProperty().addListener((observable, oldValue, newValue) -> {
//            if(!newValue) updateButton.setDisable(false);
//        });
//
//        dialog.getDialogPane().setContent(grid);
//
//        // Request focus on the username field by default.
//        Platform.runLater(() -> diseaseName.requestFocus());
//
//        // Convert the result to a diseaseName-dateOfDiagnosis-pair when the login button is clicked.
//        dialog.setResultConverter(dialogButton -> {
//            if (dialogButton == updateButtonType) {
//                if (dateOfDiagnosis.getValue().isAfter(LocalDate.now())) {
//                    DialogWindowController.showWarning("Invalid Disease", "",
//                            "Diagnosis date occurs in the future.");
//                    dateOfDiagnosis.getEditor().clear();
//                    if(current) {
//                        updateDiseasePopUp(selectedDisease, true);
//                    } else {
//                        updateDiseasePopUp(selectedDisease, false);
//                    }
//
//                } else {
//                    return new Pair<>(diseaseName.getText(), dateOfDiagnosis.getValue());
//                }
//            }
//            return null;
//        });
//
//        Optional<Pair<String, LocalDate>> result = dialog.showAndWait();
//
//        result.ifPresent(newDiseaseDetails -> {
//            System.out.println("Name=" + newDiseaseDetails.getKey() + ", DateOfDiagnosis=" + newDiseaseDetails.getValue());
//            selectedDisease.setName(newDiseaseDetails.getKey());
//            selectedDisease.setDiagnosisDate(newDiseaseDetails.getValue());
//            if(current) {
//                currentDiseaseItems.remove(selectedDisease);
//                currentDiseaseItems.add(selectedDisease);
//            } else {
//                curedDiseaseItems.remove(selectedDisease);
//                curedDiseaseItems.add(selectedDisease);
//            }
//
//        });
//    }
//
//
    private void setupListeners() {
//        final ContextMenu currentDiseaseListContextMenu = new ContextMenu();
//
//        // Update selected disease on the current disease table
//        MenuItem updateCurrentDiseaseMenuItem = new MenuItem();
//        updateCurrentDiseaseMenuItem.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                Disease selectedDisease = currentDiseaseTableView.getSelectionModel().getSelectedItem();
//                updateDiseasePopUp(selectedDisease, true);
//
//            }
//        });
//        currentDiseaseListContextMenu.getItems().add(updateCurrentDiseaseMenuItem);
//
//        // Toggle selected disease from current diseases as chronic
//        MenuItem toggleCurrentChronicMenuItem = new MenuItem();
//        toggleCurrentChronicMenuItem.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                Disease selectedDisease = currentDiseaseTableView.getSelectionModel().getSelectedItem();
//                if (selectedDisease.isChronic()) {
//                    selectedDisease.setChronic(false);
//                } else {
//                    selectedDisease.setChronic(true);
//                }
//                selectedDisease.setDiagnosisDate(LocalDate.now());
//
//                // To refresh the observableList to make chronic toggle visible
//                currentDiseaseItems.remove(selectedDisease);
//                currentDiseaseItems.add(selectedDisease);
//            }
//        });
//        currentDiseaseListContextMenu.getItems().add(toggleCurrentChronicMenuItem);
//
//        // Marks disease as cured from the current disease table and moves it to the cured disease table
//        MenuItem setCuredMenuItem = new MenuItem();
//        setCuredMenuItem.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                Disease selectedDisease = currentDiseaseTableView.getSelectionModel().getSelectedItem();
//                selectedDisease.setCured(true);
//                selectedDisease.setChronic(false);
//                selectedDisease.setDiagnosisDate(LocalDate.now());
//
//                currentDiseaseItems.remove(selectedDisease);
//                curedDiseaseItems.add(selectedDisease);
//            }
//        });
//        currentDiseaseListContextMenu.getItems().add(setCuredMenuItem);
//
//
//        final ContextMenu curedDiseaseContextMenu = new ContextMenu();
//
//        // Update selected disease from the cured disease table
//        MenuItem updateCuredDiseaseMenuItem = new MenuItem();
//        updateCuredDiseaseMenuItem.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                Disease selectedDisease = curedDiseaseTableView.getSelectionModel().getSelectedItem();
//                updateDiseasePopUp(selectedDisease, false);
//            }
//        });
//        curedDiseaseContextMenu.getItems().add(updateCuredDiseaseMenuItem);
//
//        // Set the selected disease from the cured disease table as chronic (move to current disease table also)
//        MenuItem setCuredChronicDiseaseMenuItem = new MenuItem();
//        setCuredChronicDiseaseMenuItem.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                Disease selectedDisease = curedDiseaseTableView.getSelectionModel().getSelectedItem();
//                selectedDisease.setChronic(true);
//                selectedDisease.setCured(false);
//                selectedDisease.setDiagnosisDate(LocalDate.now());
//
//                curedDiseaseItems.remove(selectedDisease);
//                currentDiseaseItems.add(selectedDisease);
//            }
//        });
//        curedDiseaseContextMenu.getItems().add(setCuredChronicDiseaseMenuItem);
//
//        // Set the selected disease from the cured disease table as uncured (move to current disease table also)
//        MenuItem setUncuredMenuItem = new MenuItem();
//        setUncuredMenuItem.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                Disease selectedDisease = curedDiseaseTableView.getSelectionModel().getSelectedItem();
//                selectedDisease.setCured(false);
//                selectedDisease.setDiagnosisDate(LocalDate.now());
//
//                curedDiseaseItems.remove(selectedDisease);
//                currentDiseaseItems.add(selectedDisease);
//            }
//        });
//        curedDiseaseContextMenu.getItems().add(setUncuredMenuItem);
//
//        /*Handles the right click action of showing a ContextMenu on the currentDiseaseListView and sets the MenuItem
//        text depending on the disease chosen*/
//        currentDiseaseTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                if (event.getButton().equals(MouseButton.SECONDARY)) {
//                    Disease selectedDisease = currentDiseaseTableView.getSelectionModel().getSelectedItem();
//                    if (selectedDisease.isChronic()) {
//                        toggleCurrentChronicMenuItem.setText(String.format("Mark %s as not chronic",
//                                selectedDisease.getName()));
//                    } else {
//                        toggleCurrentChronicMenuItem.setText(String.format("Mark %s as chronic",
//                                selectedDisease.getName()));
//                    }
//                    setCuredMenuItem.setText(String.format("Mark %s as cured",
//                            selectedDisease.getName()));
//                    updateCurrentDiseaseMenuItem.setText("Update disease");
//                    currentDiseaseListContextMenu.show(currentDiseaseTableView, event.getScreenX(), event.getScreenY());
//                }
//            }
//        });
//
//        /*Handles the right click action of showing a ContextMenu on the curedDiseaseTableView and sets the MenuItem
//        text depending on the disease chosen*/
//        curedDiseaseTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                if (event.getButton().equals(MouseButton.SECONDARY)) {
//                    Disease selectedDisease = curedDiseaseTableView.getSelectionModel().getSelectedItem();
//                    if (selectedDisease.isChronic()) {
//                        setCuredChronicDiseaseMenuItem.setText(String.format("Mark %s as not chronic",
//                                selectedDisease.getName()));
//                    } else {
//                        setCuredChronicDiseaseMenuItem.setText(String.format("Mark %s as chronic",
//                                selectedDisease.getName()));
//                    }
//                    setUncuredMenuItem.setText(String.format("Mark %s as uncured",
//                            selectedDisease.getName()));
//                    updateCuredDiseaseMenuItem.setText("Update disease");
//                    curedDiseaseContextMenu.show(curedDiseaseTableView, event.getScreenX(), event.getScreenY());
//                }
//            }
//        });
//
//
//        // Sets the cell factory to style each Procedure item depending on its details
//        pendingDateColumn.setCellFactory(column -> {
//            return new TableCell<Procedure, String>() {
//                @Override
//                protected void updateItem(String item, boolean empty) {
//                    super.updateItem(item, empty);
//
//                    if (item == null || empty) {
//                        setText(null);
//                        setStyle("");
//                    } else {
//
//                        setText(item);
//                        Procedure currentProcedure = getTableView().getItems().get(getIndex());
//
////                        // If the disease is chronic, update label + colour
////                        if (currentDisease.isChronic()) {
////                            setText("(CHRONIC) " + item);
////                            //TODO ISSUE WITH SETTING COLOR HERE VS. EXTERNAL CSS FILE
////                            setTextFill(Color.RED);
////                        }
//                    }
//                }
//            };
//        });

//        currentDiseaseTableView.sortPolicyProperty().set(
//                new Callback<TableView<Disease>, Boolean>() {
//
//                    @Override
//                    public Boolean call(TableView<Disease> param) {
//                        Comparator<Disease> comparator = new Comparator<Disease>() {
//                            @Override
//                            public int compare(Disease d1, Disease d2) {
//                                if (d1.isChronic()) {
//                                    return 0;
//                                } else if (d2.isChronic()) {
//                                    return 1;
//                                } else {
//                                    return d1.getName().compareTo(d2.getName());
//                                }
//                            }
//                        };
//                        FXCollections.sort(currentDiseaseTableView.getItems(), comparator);
//                        return true;
//                    }
//                }
//        );

        // Set up columns to extract correct information from a Procedure object
        pendingDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        pendingSummaryColumn.setCellValueFactory(new PropertyValueFactory<>("summary"));
        pendingDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        previousDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        previousSummaryColumn.setCellValueFactory(new PropertyValueFactory<>("summary"));
        previousDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

//    /**
//     * Sets whether the control buttons are shown or not on the medications pane
//     */
//    public void setControlsShown(boolean shown) {
//        dateOfDiagnosisInput.setVisible(shown);
//        addNewDiseaseButton.setVisible(shown);
//        newDiseaseDateLabel.setVisible(shown);
//        newDiseaseLabel.setVisible(shown);
//        chronicCheckBox.setVisible(shown);
//        newDiseaseTextField.setVisible(shown);
//        deleteDiseaseButton.setVisible(shown);
//        saveDiseaseButton.setVisible(shown);
//        isCuredCheckBox.setVisible(shown);
//        currentDiseaseTableView.setDisable(!shown);
//        curedDiseaseTableView.setDisable(!shown);
//    }

    /**
     * Function to set the current user of this class to that of the instance of the application.
     * @param currentUser The donor to set the current donor.
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        donorNameLabel.setText("User: " + currentUser.getName());

        pendingProcedureItems = FXCollections.observableArrayList();
        pendingProcedureItems.addAll(currentUser.getPendingProcedures());
        pendingProcedureTableView.setItems(pendingProcedureItems);

        previousProcedureItems = FXCollections.observableArrayList();
        previousProcedureItems.addAll(currentUser.getPreviousProcedures());
        previousProcedureTableView.setItems(previousProcedureItems);


        //unsavedDonorDiseases = currentDonor.getDiseases();
        //pastDiseasesCopy = currentDonor.getCuredDiseases();
        System.out.println("MedicalHistoryProcedureController: Setting donor of Medical History pane...");
    }
}
