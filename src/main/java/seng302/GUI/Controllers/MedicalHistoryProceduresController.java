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
import seng302.Generic.Procedure;
import seng302.GUI.StatusIndicator;
import seng302.GUI.TitleBar;
import seng302.Generic.History;
import seng302.Generic.IO;
import seng302.Generic.Main;
import seng302.User.Attribute.LoginType;
import seng302.User.User;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

import static seng302.Generic.IO.streamOut;

/**
 * Class which handles all the logic for the Medical History (Procedures) Window.
 * Handles all functions including:
 * Adding, deleting, updating and marking procedures in the Table Views.
 */
public class MedicalHistoryProceduresController extends PageController implements Initializable {

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
    @FXML
    private CheckBox isOrganAffectingCheckBox;


    private User currentUser;

    private ObservableList<Procedure> pendingProcedureItems, previousProcedureItems;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setMedicalHistoryProceduresController(this);
        setupListeners();
    }

    /**
     * Update the displayed user procedures to what is currently stored in the user object.
     */
    public void updateProcedures() {
        pendingProcedureItems.clear();
        pendingProcedureItems.addAll(currentUser.getPendingProcedures());
        previousProcedureItems.clear();
        previousProcedureItems.addAll(currentUser.getPreviousProcedures());
    }

    /**
     * Function to saving the most current versions of the previous and pending procedures to an undo stack
     */
    private void saveToUndoStack() {
        Main.addCurrentToProcedureUndoStack();
        currentUser.getPendingProcedures().clear();
        currentUser.getPendingProcedures().addAll(pendingProcedureItems);
        currentUser.getPreviousProcedures().clear();
        currentUser.getPreviousProcedures().addAll(previousProcedureItems);
    }

    /**
     * Adds a new procedure to the unsaved Donor Procedures array list.
     * Also checks for invalid input in the procedure summary, description and date fields.
     */
    public void addNewProcedure() {
        System.out.println("MedicalHistoryProceduresController: Adding new procedure");
        // Check for empty disease name TODO could be a listener to disable the add button
        if (summaryInput.getText().equals("")) {
            DialogWindowController.showWarning("Invalid Procedure", "",
                    "Invalid procedure summary provided.");
            summaryInput.clear();
            // Check for an empty date
        } else if (descriptionInput.getText().equals("")) {
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
                    dateOfProcedureInput.getValue(), isOrganAffectingCheckBox.isSelected());
            if(dateOfProcedureInput.getValue().isBefore(LocalDate.now())){
                previousProcedureItems.add(procedureToAdd);
            } else {
                pendingProcedureItems.add(procedureToAdd);
            }
            saveToUndoStack();
            summaryInput.clear();
            descriptionInput.clear();
            dateOfProcedureInput.getEditor().clear();
            isOrganAffectingCheckBox.setSelected(false);
            System.out.println("MedicalHistoryProceduresController: Finished adding new procedure");
            statusIndicator.setStatus("Added " + procedureToAdd, false);
            titleBar.saved(false);
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
                statusIndicator.setStatus("Deleted " + chosenProcedure, false);
                titleBar.saved(false);
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
                statusIndicator.setStatus("Deleted " + chosenProcedure, false);
                titleBar.saved(false);
                saveToUndoStack();
            }
            alert.close();
        }


    }

    /**
     * Saves the current state of the donor's procedures for both their previous and pending procedures.
     */
    public void save() {

        Alert alert = Main.createAlert(Alert.AlertType.CONFIRMATION,"Are you sure?","Are you sure would like to update the current user? ","By doing so, the donor will be updated with the following procedure details.");

        alert.getDialogPane().lookupButton(ButtonType.OK).setId("saveProcedureOK");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            currentUser.getPendingProcedures().clear();
            currentUser.getPendingProcedures().addAll(pendingProcedureItems);

            currentUser.getPreviousProcedures().clear();
            currentUser.getPreviousProcedures().addAll(previousProcedureItems);

            IO.saveUsers(IO.getUserPath(), LoginType.USER);
            String text = History.prepareFileStringGUI(currentUser.getId(), "procedures");
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
     * Function which produces a pop up window to update all the given fields for a procedure.
     * @param selectedProcedure The selected procedure that the user wishes to update.
     * @param pending If the procedure is a pending procedure or not.
     */
    private void updateProcedurePopUp(Procedure selectedProcedure, boolean pending) {

        // Create the custom dialog.
        Dialog<ArrayList<String>> dialog = new Dialog<>();
        dialog.setTitle("Update Procedure");
        dialog.setHeaderText("Update Procedure Details");
        dialog.getDialogPane().getStylesheets().add(Main.getDialogStyle());

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
        procedureSummary.setId("procedureSummary");
        TextField procedureDescription = new TextField();
        procedureDescription.setId("procedureDescription");
        procedureDescription.setPromptText(selectedProcedure.getDescription());
        DatePicker dateDue = new DatePicker();
        dateDue.setId("dateDue");
        dateDue.setPromptText(selectedProcedure.getDate().toString());

        grid.add(new Label("Summary:"), 0, 0);
        grid.add(procedureSummary, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(procedureDescription, 1, 1);
        grid.add(new Label("Date Due:"), 0, 2);
        grid.add(dateDue, 1, 2);

        // Enable/Disable login button depending on whether a username was entered.
        Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
        updateButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        procedureSummary.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButton.setDisable(newValue.trim().isEmpty());
        });

        // Do some validation (using the Java 8 lambda syntax).
        procedureDescription.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButton.setDisable(newValue.trim().isEmpty());
        });

        dateDue.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateButton.setDisable(newValue.toString().trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> procedureSummary.requestFocus());

        // Convert the result to a diseaseName-dateOfDiagnosis-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                System.out.println(dateDue.getValue());
                    String newSummary;
                    String newDescription;
                    String newDate = "";

                    if(procedureSummary.getText().equals("")) {
                        newSummary = selectedProcedure.getSummary();
                    } else {
                        newSummary = procedureSummary.getText();
                    }

                    if(procedureDescription.getText().equals("")) {
                        newDescription = selectedProcedure.getDescription();
                    } else {
                        newDescription = procedureDescription.getText();
                    }

                    if(dateDue.getValue() == null) {
                        newDate = selectedProcedure.getDate().toString();
                    } else {
                        if (dateDue.getValue().isBefore(currentUser.getDateOfBirth())) {
                            DialogWindowController.showWarning("Invalid Procedure", "",
                                    "Due date occurs before the user's date of birth.");
                            dateDue.getEditor().clear();
                            if (pending) {
                                updateProcedurePopUp(selectedProcedure, true);
                            } else {
                                updateProcedurePopUp(selectedProcedure, false);
                            }
                        } else {
                            newDate = dateDue.getValue().toString();
                        }
                    }

                    return new ArrayList<String>(Arrays.asList(newSummary, newDescription, newDate));
                }
            return null;
        });

        Optional<ArrayList<String>> result = dialog.showAndWait();

        result.ifPresent(newProcedureDetails -> {
            System.out.println("Summary=" + newProcedureDetails.get(0) + ", Description=" + newProcedureDetails.get(1) + ", DateDue=" + newProcedureDetails.get(2));
            selectedProcedure.setSummary(newProcedureDetails.get(0));
            selectedProcedure.setDescription(newProcedureDetails.get(1));
            LocalDate newDateFormat = LocalDate.parse(newProcedureDetails.get(2));
            selectedProcedure.setDate(newDateFormat);
            if(pending) {
                if(newDateFormat.isAfter(LocalDate.now())) {
                    pendingProcedureItems.remove(selectedProcedure);
                    pendingProcedureItems.add(selectedProcedure);
                } else {
                    pendingProcedureItems.remove(selectedProcedure);
                    previousProcedureItems.add(selectedProcedure);
                }

            } else {
                if(newDateFormat.isAfter(LocalDate.now())) {
                    previousProcedureItems.remove(selectedProcedure);
                    pendingProcedureItems.add(selectedProcedure);
                } else {
                    previousProcedureItems.remove(selectedProcedure);
                    previousProcedureItems.add(selectedProcedure);
                }
            }
            saveToUndoStack();

        });
    }

    /**
     * Function which sets up all the listeners for the text fields and also populates
     * the table views based on the values in the observable array lists.
     * Also creates the menu items for right clicking on a procedure.
     */
    private void setupListeners() {

        pendingProcedureTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                previousProcedureTableView.getSelectionModel().clearSelection();
            }
        });

        previousProcedureTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                pendingProcedureTableView.getSelectionModel().clearSelection();
            }
        });

        final ContextMenu pendingProcedureListContextMenu = new ContextMenu();

        // Update selected procedure on the pending procedures table
        MenuItem updatePendingProcedureMenuItem = new MenuItem();
        updatePendingProcedureMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Procedure selectedProcedure = pendingProcedureTableView.getSelectionModel().getSelectedItem();
                updateProcedurePopUp(selectedProcedure, true);
                statusIndicator.setStatus("Edited " + selectedProcedure, false);
                titleBar.saved(false);
            }
        });
        pendingProcedureListContextMenu.getItems().add(updatePendingProcedureMenuItem);

        // Toggle selected procedure from pending procedures as organ affecting
        MenuItem togglePendingProcedureOrganAffectingMenuItem = new MenuItem();
        togglePendingProcedureOrganAffectingMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Procedure selectedProcedure = pendingProcedureTableView.getSelectionModel().getSelectedItem();
                if (selectedProcedure.isOrganAffecting()) {
                    selectedProcedure.setOrganAffecting(false);
                    statusIndicator.setStatus("Set " + selectedProcedure + " as not affecting a donatable organ", false);
                } else {
                    selectedProcedure.setOrganAffecting(true);
                    statusIndicator.setStatus("Set " + selectedProcedure + " as affecting a donatable organ", false);
                }
                titleBar.saved(false);

                // To refresh the observableList to make chronic toggle visible
                pendingProcedureItems.remove(selectedProcedure);
                pendingProcedureItems.add(selectedProcedure);

            }
        });
        pendingProcedureListContextMenu.getItems().add(togglePendingProcedureOrganAffectingMenuItem);

        final ContextMenu previousProcedureListContextMenu = new ContextMenu();

        // Update selected procedure on the previous procedures table
        MenuItem updatePreviousProcedureMenuItem = new MenuItem();
        updatePreviousProcedureMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Procedure selectedProcedure = previousProcedureTableView.getSelectionModel().getSelectedItem();
                updateProcedurePopUp(selectedProcedure, false);
                statusIndicator.setStatus("Edited " + selectedProcedure, false);
                titleBar.saved(false);

            }
        });
        previousProcedureListContextMenu.getItems().add(updatePreviousProcedureMenuItem);

        // Toggle selected procedure from previous procedures as organ affecting
        MenuItem togglePreviousProcedureOrganAffectingMenuItem = new MenuItem();
        togglePreviousProcedureOrganAffectingMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Procedure selectedProcedure = previousProcedureTableView.getSelectionModel().getSelectedItem();
                if (selectedProcedure.isOrganAffecting()) {
                    selectedProcedure.setOrganAffecting(false);
                    statusIndicator.setStatus("Set " + selectedProcedure + " as not affecting a donatable organ", false);
                } else {
                    selectedProcedure.setOrganAffecting(true);
                    statusIndicator.setStatus("Set " + selectedProcedure + " as affecting a donatable organ", false);
                }
                titleBar.saved(false);
                // To refresh the observableList to make chronic toggle visible
                previousProcedureItems.remove(selectedProcedure);
                previousProcedureItems.add(selectedProcedure);

            }
        });
        previousProcedureListContextMenu.getItems().add(togglePreviousProcedureOrganAffectingMenuItem);

        /*Handles the right click action of showing a ContextMenu on the pendingProcedureTableView and sets the MenuItem
        text depending on the procedure chosen*/
        pendingProcedureTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    Procedure selectedProcedure = pendingProcedureTableView.getSelectionModel().getSelectedItem();
                    if (selectedProcedure.isOrganAffecting()) {
                        togglePendingProcedureOrganAffectingMenuItem.setText(String.format("Mark procedure as non organ affecting"));
                    } else {
                        togglePendingProcedureOrganAffectingMenuItem.setText(String.format("Mark procedure as organ affecting"));
                    }
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
                    if (selectedProcedure.isOrganAffecting()) {
                        togglePreviousProcedureOrganAffectingMenuItem.setText(String.format("Mark procedure as non organ affecting"));
                    } else {
                        togglePreviousProcedureOrganAffectingMenuItem.setText(String.format("Mark procedure as organ affecting"));
                    }
                    updatePreviousProcedureMenuItem.setText("Update previous procedure");
                    previousProcedureListContextMenu.show(previousProcedureTableView, event.getScreenX(), event.getScreenY());
                }
            }
        });

        // Sets the cell factory to style each pending procedure item depending on its details
        pendingSummaryColumn.setCellFactory(column -> {
            return new TableCell<Procedure, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {

                        setText(item);
                        Procedure currentProcedure = getTableView().getItems().get(getIndex());

                        // If the disease is chronic, update label + colour
                        if (currentProcedure.isOrganAffecting()) {
                            setText("* " + item);
                            this.setStyle("-fx-background-color: GREY;");
                        }
                    }
                }
            };
        });

        // Sets the cell factory to style each previous Procedure item depending on its details
        previousSummaryColumn.setCellFactory(column -> {
            return new TableCell<Procedure, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {

                        setText(item);
                        Procedure currentProcedure = getTableView().getItems().get(getIndex());

                        // If the disease is chronic, update label + colour
                        if (currentProcedure.isOrganAffecting()) {
                            setText("* " + item);
                            this.setStyle("-fx-background-color: GREY;");
                        }
                    }
                }
            };
        });

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
        pendingProceduresLabel.setVisible(!shown);
        previousProceduresLabel.setVisible(!shown);
        addNewProcedureButton.setVisible(shown);
        pendingProcedureTableView.setDisable(!shown);
        previousProcedureTableView.setDisable(!shown);
        deleteProcedureButton.setVisible(shown);
        saveProcedureButton.setVisible(shown);
        isOrganAffectingCheckBox.setVisible(shown);
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

        //Check if pending procedure due date is now past the current date
        for(Procedure procedure: currentUser.getPendingProcedures()) {
            if(procedure.getDate().isBefore(LocalDate.now())) {
                currentUser.getPendingProcedures().remove(procedure);
                currentUser.getPreviousProcedures().add(procedure);
            }
        }

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
