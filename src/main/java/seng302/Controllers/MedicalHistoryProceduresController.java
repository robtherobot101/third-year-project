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

    @FXML
    private Button addNewProcedureButton, deleteProcedureButton, saveProcedureButton;
    @FXML
    private Label newProcedureDateLabel, newProcedureLabel, pendingProceduresLabel, previousProceduresLabel;


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
        } else if (dateOfProcedureInput.getValue().isBefore(currentUser.getDateOfBirth())){
            DialogWindowController.showWarning("Invalid Procedure", "",
                    "Due date occurs before the user's date of birth.");
        } else {
            // Add the new procedure
            Procedure procedureToAdd = new Procedure(summaryInput.getText(), descriptionInput.getText(),
                    dateOfProcedureInput.getValue());
            if(dateOfProcedureInput.getValue().isBefore(LocalDate.now())){
                previousProcedureItems.add(procedureToAdd);
            } else {
                pendingProcedureItems.add(procedureToAdd);
            }
            System.out.println("MedicalHistoryProceduresController: Finished adding new procedure");
        }
    }


    /**
     * Deletes a procedure from either the pending or previous table views for the procedures.
     */
    public void deleteProcedure() {
        System.out.println("MedicalHistoryProceduresController: Deleting disease");

        if (pendingProcedureTableView.getSelectionModel().getSelectedItem() != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Are you sure?");
            alert.setHeaderText("Are you sure would like to delete the selected pending procedure? ");
            alert.setContentText("By doing so, the procedure will be erased from the database.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Procedure chosenProcedure = pendingProcedureTableView.getSelectionModel().getSelectedItem();
                pendingProcedureItems.remove(chosenProcedure);
            }
            alert.close();
        }

        else if (previousProcedureTableView.getSelectionModel().getSelectedItem() != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Are you sure?");
            alert.setHeaderText("Are you sure would like to delete the selected previous procedure? ");
            alert.setContentText("By doing so, the procedure will be erased from the database.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Procedure chosenProcedure = previousProcedureTableView.getSelectionModel().getSelectedItem();
                previousProcedureItems.remove(chosenProcedure);
            }
            alert.close();
        }

        //TODO create update for diseases for history when deleting
//            String text = History.prepareFileStringGUI(currentDonor.getId(), "update");
//            History.printToFile(streamOut, text);
        //populateHistoryTable();

    }

    /**
     * Saves the current state of the donor's procedures for both their previous and pending procedures.
     */
    public void save() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("Are you sure would like to update the current user? ");
        alert.setContentText("By doing so, the donor will be updated with the following procedure details.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            currentUser.getPendingProcedures().clear();
            currentUser.getPendingProcedures().addAll(pendingProcedureItems);

            currentUser.getPreviousProcedures().clear();
            currentUser.getPreviousProcedures().addAll(previousProcedureItems);

            Main.saveUsers(Main.getUserPath(), true);
            //TODO create update for diseases for history
//            String text = History.prepareFileStringGUI(currentDonor.getId(), "update");
//            History.printToFile(streamOut, text);
            //populateHistoryTable();
            alert.close();
        } else {
            alert.close();
        }
    }

    private void updateProcedurePopUp(Procedure selectedProcedure, boolean pending) {

        // Create the custom dialog.
        Dialog<Pair<String, LocalDate>> dialog = new Dialog<>();
        dialog.setTitle("Update Procedure");
        dialog.setHeaderText("Update Procedure Details");

        // Set the button types.
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        TextField procedureSummary = new TextField();
        procedureSummary.setPromptText(selectedProcedure.getSummary());
        DatePicker dateDue = new DatePicker();
        dateDue.setPromptText(selectedProcedure.getDate().toString());

        grid.add(new Label("Summary:"), 0, 0);
        grid.add(procedureSummary, 1, 0);
        grid.add(new Label("Date Due:"), 0, 1);
        grid.add(dateDue, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
        updateButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        procedureSummary.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButton.setDisable(newValue.trim().isEmpty());
        });

        dateDue.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) updateButton.setDisable(false);
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> procedureSummary.requestFocus());

        // Convert the result to a diseaseName-dateOfDiagnosis-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                if (dateDue.getValue().isBefore(currentUser.getDateOfBirth())) {
                    DialogWindowController.showWarning("Invalid Procedure", "",
                            "Due date occurs before the user's date of birth.");
                    dateDue.getEditor().clear();
                    if(pending) {
                        updateProcedurePopUp(selectedProcedure, true);
                    } else {
                        updateProcedurePopUp(selectedProcedure, false);
                    }

                } else {
                    return new Pair<>(procedureSummary.getText(), dateDue.getValue());
                }
            }
            return null;
        });

        Optional<Pair<String, LocalDate>> result = dialog.showAndWait();

        result.ifPresent(newProcedureDetails -> {
            System.out.println("Summary=" + newProcedureDetails.getKey() + ", DateDue=" + newProcedureDetails.getValue());
            selectedProcedure.setSummary(newProcedureDetails.getKey());
            selectedProcedure.setDate(newProcedureDetails.getValue());
            if(pending) {
                if(newProcedureDetails.getValue().isAfter(LocalDate.now())) {
                    pendingProcedureItems.remove(selectedProcedure);
                    pendingProcedureItems.add(selectedProcedure);
                } else {
                    pendingProcedureItems.remove(selectedProcedure);
                    previousProcedureItems.add(selectedProcedure);
                }

            } else {
                if(newProcedureDetails.getValue().isAfter(LocalDate.now())) {
                    previousProcedureItems.remove(selectedProcedure);
                    pendingProcedureItems.add(selectedProcedure);
                } else {
                    previousProcedureItems.remove(selectedProcedure);
                    previousProcedureItems.add(selectedProcedure);
                }

            }

        });
    }


    private void setupListeners() {
        final ContextMenu pendingProcedureListContextMenu = new ContextMenu();

        // Update selected procedure on the pending procedures table
        MenuItem updatePendingProcedureMenuItem = new MenuItem();
        updatePendingProcedureMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Procedure selectedProcedure = pendingProcedureTableView.getSelectionModel().getSelectedItem();
                updateProcedurePopUp(selectedProcedure, true);

            }
        });
        pendingProcedureListContextMenu.getItems().add(updatePendingProcedureMenuItem);
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
        final ContextMenu previousProcedureListContextMenu = new ContextMenu();

        // Update selected procedure on the previous procedures table
        MenuItem updatePreviousProcedureMenuItem = new MenuItem();
        updatePreviousProcedureMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Procedure selectedProcedure = previousProcedureTableView.getSelectionModel().getSelectedItem();
                updateProcedurePopUp(selectedProcedure, false);

            }
        });
        previousProcedureListContextMenu.getItems().add(updatePreviousProcedureMenuItem);
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
        /*Handles the right click action of showing a ContextMenu on the pendingProcedureTableView and sets the MenuItem
        text depending on the procedure chosen*/
        pendingProcedureTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    Procedure selectedProcedure = pendingProcedureTableView.getSelectionModel().getSelectedItem();
                    updatePendingProcedureMenuItem.setText("Update pending procedure");
                    pendingProcedureListContextMenu.show(pendingProcedureTableView, event.getScreenX(), event.getScreenY());
                }
            }
        });

        /*Handles the right click action of showing a ContextMenu on the previousProcedureTableView and sets the MenuItem
        text depending on the procedure chosen*/
        previousProcedureTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    Procedure selectedProcedure = previousProcedureTableView.getSelectionModel().getSelectedItem();
                    updatePreviousProcedureMenuItem.setText("Update previous procedure");
                    previousProcedureListContextMenu.show(previousProcedureTableView, event.getScreenX(), event.getScreenY());
                }
            }
        });
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

    /**
     * Sets whether the control buttons are shown or not on the medications pane
     */
    public void setControlsShown(boolean shown) {
        dateOfProcedureInput.setVisible(shown);
        summaryInput.setVisible(shown);
        descriptionInput.setVisible(shown);
        newProcedureDateLabel.setVisible(shown);
        newProcedureLabel.setVisible(shown);
        pendingProceduresLabel.setVisible(shown);
        previousProceduresLabel.setVisible(shown);
        addNewProcedureButton.setVisible(shown);
        pendingProcedureTableView.setDisable(!shown);
        previousProcedureTableView.setDisable(!shown);
        deleteProcedureButton.setVisible(shown);
        saveProcedureButton.setVisible(shown);
    }


    private void updatePendingProcedures() {
        //Check if pending procedure due date is now past the current date
        for(Procedure procedure: currentUser.getPendingProcedures()) {
            if(procedure.getDate().isBefore(LocalDate.now())) {
                currentUser.getPendingProcedures().remove(procedure);
                currentUser.getPreviousProcedures().add(procedure);
            }
        }
    }

    /**
     * Function to set the current user of this class to that of the instance of the application.
     * @param currentUser The donor to set the current donor.
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        donorNameLabel.setText("User: " + currentUser.getName());

        updatePendingProcedures();

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
