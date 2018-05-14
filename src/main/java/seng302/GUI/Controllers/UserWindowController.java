package seng302.GUI.Controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.StatusBar;
import seng302.GUI.StatusIndicator;
import seng302.GUI.TFScene;
import seng302.GUI.TitleBar;
import seng302.Generic.History;
import seng302.Generic.IO;
import seng302.Generic.WindowManager;
import seng302.User.Attribute.*;
import seng302.User.User;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static seng302.Generic.IO.streamOut;

/**
 * Class which handles all the logic for the User Window.
 * Handles all functions including:
 * Saving, Undo, Redo, All input fields and more.
 */
public class UserWindowController implements Initializable {

    private TitleBar titleBar;
    @FXML
    private Label userDisplayText, userHistoryLabel;
    @FXML
    private GridPane historyGridPane, background;
    @FXML
    private AnchorPane attributesPane, medicationsPane, medicalHistoryDiseasesPane, medicalHistoryProceduresPane;
    @FXML
    private AnchorPane waitingListPane;
    @FXML
    private Pane welcomePane;
    @FXML
    private MenuItem undoButton, redoButton, logoutMenuItem;
    @FXML
    private Button logoutButton, undoBannerButton, redoBannerButton, medicationsButton, medicalHistoryButton, waitingListButton;
    @FXML
    private TreeTableView<String> historyTreeTableView;
    @FXML
    private TreeTableColumn<String, String> dateTimeColumn, actionColumn;

    private ArrayList<User> waitingListUndoStack = new ArrayList<>(), waitingListRedoStack = new ArrayList<>();
    private LinkedList<User> attributeUndoStack = new LinkedList<>(), attributeRedoStack = new LinkedList<>(), medicationUndoStack = new
            LinkedList<>(), medicationRedoStack = new LinkedList<>();
    private LinkedList<User> procedureUndoStack = new LinkedList<>(), procedureRedoStack = new LinkedList<>();
    private LinkedList<User> diseaseUndoStack = new LinkedList<>(), diseaseRedoStack = new LinkedList<>();
    private User currentUser;

    @FXML
    private StatusBar statusBar;
    @FXML
    private MedicationsController medicationsController;
    @FXML
    private MedicalHistoryDiseasesController medicalHistoryDiseasesController;
    @FXML
    private MedicalHistoryProceduresController medicalHistoryProceduresController;
    @FXML
    private WaitingListController waitingListController;
    @FXML
    private AttributesController attributesController;

    public StatusIndicator statusIndicator = new StatusIndicator();


    public UserWindowController() {
        this.titleBar = new TitleBar();
        titleBar.setStage(WindowManager.getStage());
    }

    /**
     * Set the stage the controller is associated with
     *
     * @param stage The stage on which the window is shown
     */
    public void setTitleBar(Stage stage) {
        titleBar.setStage(stage);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        attributesController.currentUser = currentUser;
        userDisplayText.setText("Currently logged in as: " + currentUser.getPreferredName());
        attributeUndoStack.clear();
        attributeRedoStack.clear();
        undoButton.setDisable(true);
        undoBannerButton.setDisable(true);
        redoButton.setDisable(true);
        redoBannerButton.setDisable(true);
        //bloodPressureLabel.setText("");
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Home");
    }

    /**
     * Set up the User window.
     *
     * @param location  Not used
     * @param resources Not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WindowManager.setUserWindowController(this);
        welcomePane.setVisible(true);
        attributesPane.setVisible(false);
        historyGridPane.setVisible(false);
        medicationsPane.setVisible(false);


        WindowManager.controlViewForUser();

        Image welcomeImage = new Image("/OrganDonation.jpg");
        BackgroundImage imageBackground = new BackgroundImage(welcomeImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        welcomePane.setBackground(new Background(imageBackground));



        waitingListButton.setOnAction((ActionEvent event) -> {
            showWaitingListPane();
            WindowManager.getWaitingListController().populateWaitingList();
            WindowManager.getWaitingListController().populateOrgansComboBox();
        });

        statusIndicator.setStatusBar(statusBar);

        // Pass the status bar and title bar objects to the embedded controllers
        attributesController.setStatusIndicator(statusIndicator);
        attributesController.setTitleBar(titleBar);
        attributesController.setParent(this);

        medicationsController.setStatusIndicator(statusIndicator);
        medicationsController.setTitleBar(titleBar);

        medicalHistoryDiseasesController.setStatusIndicator(statusIndicator);
        medicalHistoryDiseasesController.setTitleBar(titleBar);

        medicalHistoryProceduresController.setStatusIndicator(statusIndicator);
        medicalHistoryProceduresController.setTitleBar(titleBar);

        waitingListController.setStatusIndicator(statusIndicator);
        waitingListController.setTitleBar(titleBar);
    }

    /**
     * Removes focus from all fields.
     */
    public void requestFocus() {
        background.requestFocus();
    }

    /**
     * Add the current user object to the medications undo stack.
     */
    public void addCurrentToAttributesUndoStack(User user) {
        attributeUndoStack.add(user);
        attributeUndoStack.add(new User(currentUser));
        attributeRedoStack.clear();
        setUndoRedoButtonsDisabled(false, true);
    }

    /**
     * Add the current user object to the medications undo stack.
     */
    public void addCurrentToMedicationUndoStack() {
        medicationUndoStack.add(new User(currentUser));
        medicationRedoStack.clear();
        setUndoRedoButtonsDisabled(false, true);
    }

    public void addCurrentToDiseaseUndoStack() {
        diseaseUndoStack.add(new User(currentUser));
        diseaseRedoStack.clear();
        setUndoRedoButtonsDisabled(false, true);
    }

    /**
     * Add the current user object to the procedures undo stack.
     */
    public void addCurrentToProceduresUndoStack() {
        procedureUndoStack.add(new User(currentUser));
        procedureRedoStack.clear();
        setUndoRedoButtonsDisabled(false, true);
    }

    /**
     * Adds the current user object to the waiting list undo stack.
     */
    public void addCurrentToWaitingListUndoStack() {
        waitingListUndoStack.add(new User(currentUser));
        waitingListRedoStack.clear();
        setUndoRedoButtonsDisabled(false, true);
    }

    /**
     * Set whether the undo and redo buttons are enabled.
     *
     * @param undoDisabled Whether the undo buttons should be disabled
     * @param redoDisabled Whether the redo buttons should be disabled
     */
    private void setUndoRedoButtonsDisabled(boolean undoDisabled, boolean redoDisabled) {
        undoButton.setDisable(undoDisabled);
        undoBannerButton.setDisable(undoDisabled);
        redoButton.setDisable(redoDisabled);
        redoBannerButton.setDisable(redoDisabled);
    }

    public void showWaitingListButton() {
        waitingListButton.setVisible(true);
    }

    /**
     * Sets the history pane as the visible pane
     */
    public void showHistoryPane() {
        welcomePane.setVisible(false);
        attributesPane.setVisible(false);
        historyGridPane.setVisible(true);
        medicalHistoryDiseasesPane.setVisible(false);
        medicalHistoryProceduresPane.setVisible(false);
        medicationsPane.setVisible(false);
        waitingListPane.setVisible(false);
        setUndoRedoButtonsDisabled(true, true);
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Action History");
    }


    public void showWaitingListPane() {
        welcomePane.setVisible(false);
        attributesPane.setVisible(false);
        medicalHistoryDiseasesPane.setVisible(false);
        medicalHistoryProceduresPane.setVisible(false);
        historyGridPane.setVisible(false);
        medicationsPane.setVisible(false);
        waitingListPane.setVisible(true);
        setUndoRedoButtonsDisabled(waitingListUndoStack.isEmpty(), waitingListRedoStack.isEmpty());
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Waiting List");
    }

    /**
     * Sets the medications pane as the visible pane
     */
    public void showMedicationsPane() {
        welcomePane.setVisible(false);
        attributesPane.setVisible(false);
        historyGridPane.setVisible(false);
        medicalHistoryDiseasesPane.setVisible(false);
        medicalHistoryProceduresPane.setVisible(false);
        medicationsPane.setVisible(true);
        waitingListPane.setVisible(false);
        setUndoRedoButtonsDisabled(medicationUndoStack.isEmpty(), medicationRedoStack.isEmpty());
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Medications");
    }

    /**
     * Sets the User Attribute pane as the visible pane
     */
    public void showAttributesPane() {
        welcomePane.setVisible(false);
        attributesPane.setVisible(true);
        historyGridPane.setVisible(false);
        medicalHistoryDiseasesPane.setVisible(false);
        medicalHistoryProceduresPane.setVisible(false);
        medicationsPane.setVisible(false);
        waitingListPane.setVisible(false);
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Attributes");
    }

    /**
     * Sets the diseases pane as the visible pane
     */
    public void showMedicalHistoryDiseasesPane() {
        welcomePane.setVisible(false);
        attributesPane.setVisible(false);
        historyGridPane.setVisible(false);
        medicalHistoryDiseasesPane.setVisible(true);
        medicalHistoryProceduresPane.setVisible(false);
        medicationsPane.setVisible(false);
        waitingListPane.setVisible(false);
        setUndoRedoButtonsDisabled(attributeUndoStack.isEmpty(), attributeRedoStack.isEmpty());
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Disease History");
        setUndoRedoButtonsDisabled(diseaseUndoStack.isEmpty(), diseaseRedoStack.isEmpty());
    }

    /**
     * Sets the medical history pane as the visible pane
     */
    public void showMedicalHistoryProceduresPane() {
        welcomePane.setVisible(false);
        attributesPane.setVisible(false);
        historyGridPane.setVisible(false);
        medicalHistoryDiseasesPane.setVisible(false);
        medicalHistoryProceduresPane.setVisible(true);
        medicationsPane.setVisible(false);
        waitingListPane.setVisible(false);
        setUndoRedoButtonsDisabled(attributeUndoStack.isEmpty(), attributeRedoStack.isEmpty());
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Procedure History");
    }

    /**
     * Sets the welcome pane as the visible pane
     */
    public void showWelcomePane() {
        welcomePane.setVisible(true);
        attributesPane.setVisible(false);
        historyGridPane.setVisible(false);
        medicalHistoryDiseasesPane.setVisible(false);
        medicalHistoryProceduresPane.setVisible(false);
        medicationsPane.setVisible(false);
        waitingListPane.setVisible(false);
        setUndoRedoButtonsDisabled(true, true);
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Home");
    }

    /**
     * Populates the history table based on the action history of the current user.
     * Gets the user history from the History.getUserHistory() function.
     * Sorts these into tree nodes based on new sessions.
     */
    public void populateHistoryTable() {
        userHistoryLabel.setText("History of actions for " + currentUser.getPreferredName());
        String[][] userHistory = History.getUserHistory(currentUser.getId());
        ArrayList<TreeItem<String>> treeItems = new ArrayList<>();
        if (userHistory[0][0] != null) {
            TreeItem<String> sessionNode = new TreeItem<>("Session 1 on " + userHistory[0][0].substring(0, userHistory[0][0].length() - 1));
            TreeItem<String> outerItem1 = new TreeItem<>("Create at " + userHistory[0][1]);
            TreeItem<String> outerItem2 = new TreeItem<>("Login at " + userHistory[0][1]);
            sessionNode.getChildren().add(outerItem1);
            sessionNode.getChildren().add(outerItem2);
            treeItems.add(sessionNode);

            int sessionNumber = 2;
            for (int i = 2; i < userHistory.length; i++) {
                if (!(userHistory[i][4] == null) && !(userHistory[i][4].equals("create"))) {
                    switch (userHistory[i][4]) {
                        case "update":
                        case "undo":
                        case "redo":
                        case "medications":
                        case "procedures":
                        case "diseases":
                        case "logout":
                        case "quit":
                            sessionNode.getChildren().add(new TreeItem<>(userHistory[i][4].substring(0, 1).toUpperCase() + userHistory[i][4]
                                    .substring(1) + " at " + userHistory[i][1]));
                            break;
                        case "updateAccountSettings":
                            sessionNode.getChildren().add(new TreeItem<>(userHistory[i][4].substring(0, 1).toUpperCase() + userHistory[i][4]
                                    .substring(1, 6) +
                                    " " + userHistory[i][4].substring(6, 13) + " at " + userHistory[i][1]));
                            break;

                        case "waitinglist":
                            sessionNode.getChildren().add(new TreeItem<>(userHistory[i][4].substring(0, 1).toUpperCase() + userHistory[i][4]
                                    .substring(1, 7) + " " + userHistory[i][4].substring(7) + " modified " + " at " + userHistory[i][1]));
                            break;

                        case "modifyUser":
                            sessionNode.getChildren().add(new TreeItem<>(userHistory[i][4].substring(0, 1).toUpperCase() + userHistory[i][4]
                                    .substring(1, 6) +
                                    " " + userHistory[i][4].substring(6, 10) + " at " + userHistory[i][1]));
                            break;
                        case "login":
                            sessionNode = new TreeItem<>("Session " + sessionNumber + " on " + userHistory[i][0].substring(0, userHistory[i][0]
                                    .length() -
                                    1));
                            treeItems.add(sessionNode);
                            sessionNode.getChildren().add(new TreeItem<>("Login at " + userHistory[i][1]));
                            sessionNumber++;
                            break;
                        case "view":
                            sessionNode = new TreeItem<>("Session " + sessionNumber + " on " + userHistory[i][0].substring(0, userHistory[i][0]
                                    .length() -
                                    1));
                            treeItems.add(sessionNode);
                            sessionNode.getChildren().add(new TreeItem<>("View at " + userHistory[i][1]));
                            sessionNumber++;
                            break;
                    }
                }
            }
        }
        final TreeItem<String> root = new TreeItem<>("User History");
        root.setExpanded(true);
        root.getChildren().setAll(treeItems);

        //Defining cell content
        dateTimeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<String, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue()));

        actionColumn.setCellValueFactory(param -> {
            String userName = currentUser.getName(), toCheck = param.getValue().getValue().substring(0, 12);
            if (toCheck.equals("Update Account")) {
                return new ReadOnlyStringWrapper("Updated account settings for user " + userName + ".");
            }
            switch (toCheck.substring(0, 6)) {
                case "Create":
                    return new ReadOnlyStringWrapper("Created a new user profile with name " + userName + ".");
                case "Update":
                    return new ReadOnlyStringWrapper("Updated user attributes for user " + userName + ".");
                case "Logout":
                    return new ReadOnlyStringWrapper("User with id: " + currentUser.getId() + " logged out successfully.");
            }
            if (toCheck.substring(0, 5).equals("Login")) {
                return new ReadOnlyStringWrapper("User with id: " + currentUser.getId() + " logged in successfully.");
            }
            switch (toCheck.substring(0, 4)) {
                case "Undo":
                    return new ReadOnlyStringWrapper("Reversed last action.");
                case "Redo":
                    return new ReadOnlyStringWrapper("Reversed last undo.");
                case "Quit":
                    return new ReadOnlyStringWrapper("Quit the application.");
                case "View":
                    return new ReadOnlyStringWrapper("-Clinician- Viewed user " + userName + " .");
            }
            if (toCheck.equals("Modify User")) {
                return new ReadOnlyStringWrapper("-Clinician- Modified user " + userName + "'s attributes.");
            }
            if (toCheck.substring(0, 11).equals("Medications")) {
                return new ReadOnlyStringWrapper("-Clinician- Modified user " + userName + "'s medications.");
            }
            if (toCheck.substring(0, 12).equals("Waiting list")) {
                return new ReadOnlyStringWrapper("-Clinician- Modified user " + userName + "'s waiting list.");
            }
            if (toCheck.substring(0, 8).equals("Diseases")) {
                return new ReadOnlyStringWrapper("-Clinician- Modified user " + userName + "'s diseases.");
            }
            if (toCheck.substring(0, 10).equals("Procedures")) {
                return new ReadOnlyStringWrapper("-Clinician- Modified user " + userName + "'s procedures.");
            }
            return null;
        });

        //Creating a tree table view
        historyTreeTableView.setRoot(root);
        historyTreeTableView.setShowRoot(true);
    }

    /**
     * Saves the current state of the GUI.
     * Gets all the inputs for the user attributes and sets the user attributes to those by calling the update user function.
     * Then calls the populate user function to repopulate the user fields.
     */
    public void save() {
        Alert alert = WindowManager.createAlert(AlertType.CONFIRMATION, "Are you sure?",
                "Are you sure would like to update the current user? ", "By doing so, the user will be updated with all filled in fields.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK && attributesController.updateUser()) {
            IO.saveUsers(IO.getUserPath(), ProfileType.USER);
            attributesController.populateUserFields();
            String text = History.prepareFileStringGUI(currentUser.getId(), "update");
            History.printToFile(streamOut, text);
            populateHistoryTable();
            titleBar.saved(true);
            titleBar.setTitle(currentUser.getPreferredName(), "User");
            statusIndicator.setStatus("Saved", false);
            WindowManager.getClinicianController().updateFoundUsers();
        }
        alert.close();
    }

    /**
     * Called when the undo button is pushed, and reverts the last action performed by the user.
     * Then checks to see if there are any other actions that can be undone and adjusts the buttons accordingly.
     */
    public void undo() {
        titleBar.saved(false);
        if (attributesPane.isVisible()) {
            attributesController.attributeFieldUnfocused();
            //Add the current fields to the redo stack
            attributeRedoStack.add(new User(currentUser));
            //Copy the attribute information from the top element of the undo stack
            currentUser.copyFieldsFrom(attributeUndoStack.getLast());
            //Remove the top element of the undo stack
            attributeUndoStack.removeLast();
            attributesController.populateUserFields();

            setUndoRedoButtonsDisabled(attributeUndoStack.isEmpty(), false);
            String text = History.prepareFileStringGUI(currentUser.getId(), "undo");
            History.printToFile(streamOut, text);
            populateHistoryTable();
        } else if (medicationsPane.isVisible()) {
            //Add the current medication lists to the redo stack
            medicationRedoStack.add(new User(currentUser));
            //Copy the medication lists from the top element of the undo stack
            currentUser.copyMedicationListsFrom(medicationUndoStack.getLast());
            //Remove the top element of the undo stack
            medicationUndoStack.removeLast();

            setUndoRedoButtonsDisabled(medicationUndoStack.isEmpty(), false);
            WindowManager.updateMedications();
        } else if (waitingListPane.isVisible()) {
            waitingListRedoStack.add(new User(currentUser));
            currentUser.copyWaitingListsFrom(waitingListUndoStack.get(waitingListUndoStack.size() - 1));
            waitingListUndoStack.remove(waitingListUndoStack.size() - 1);
            setUndoRedoButtonsDisabled(waitingListUndoStack.isEmpty(), false);
            WindowManager.updateWaitingList();
        } else if (medicalHistoryProceduresPane.isVisible()) {
            //Add the current procedures lists to the redo stack
            procedureRedoStack.add(new User(currentUser));
            //Copy the proceudres lists from the top element of the undo stack
            currentUser.copyProceduresListsFrom(procedureUndoStack.get(procedureUndoStack.size() - 1));
            //Remove the top element of the undo stack
            procedureUndoStack.remove(procedureUndoStack.size() - 1);

            setUndoRedoButtonsDisabled(procedureUndoStack.isEmpty(), false);
            WindowManager.updateProcedures();
        } else if (medicalHistoryDiseasesPane.isVisible()) {
            //Add the current disease lists to the redo stack
            diseaseRedoStack.add(new User(currentUser));
            //Copy the disease lists from the top element of the undo stack
            currentUser.copyDiseaseListsFrom(diseaseUndoStack.get(diseaseUndoStack.size() - 1));
            //Remove the top element of the undo stack
            diseaseUndoStack.remove(diseaseUndoStack.size() - 1);

            setUndoRedoButtonsDisabled(diseaseUndoStack.isEmpty(), false);
            WindowManager.updateDiseases();
        }
        statusIndicator.setStatus("Undid last action", false);
        titleBar.saved(false);
    }

    /**
     * Called when the redo button is pushed, and reverts the last undo performed by the user.
     * Then checks to see if there are any other actions that can be redone and adjusts the buttons accordingly.
     */
    public void redo() {
        if (attributesPane.isVisible()) {
            attributesController.attributeFieldUnfocused();
            //Add the current fields to the undo stack
            attributeUndoStack.add(new User(currentUser));
            //Copy the attribute information from the top element of the redo stack
            currentUser.copyFieldsFrom(attributeRedoStack.getLast());
            //Remove the top element of the redo stack
            attributeRedoStack.removeLast();
            attributesController.populateUserFields();

            setUndoRedoButtonsDisabled(false, attributeRedoStack.isEmpty());
            String text = History.prepareFileStringGUI(currentUser.getId(), "redo");
            History.printToFile(streamOut, text);
            populateHistoryTable();
        } else if (medicationsPane.isVisible()) {
            //Add the current medication lists to the undo stack
            medicationUndoStack.add(new User(currentUser));
            //Copy the medications lists from the top element of the redo stack
            currentUser.copyMedicationListsFrom(medicationRedoStack.getLast());
            //Remove the top element of the redo stack
            medicationRedoStack.removeLast();

            setUndoRedoButtonsDisabled(false, medicationRedoStack.isEmpty());
            WindowManager.updateMedications();
        } else if (waitingListPane.isVisible()) {
            waitingListUndoStack.add(new User(currentUser));
            currentUser.copyWaitingListsFrom(waitingListRedoStack.get(waitingListRedoStack.size() - 1));
            waitingListRedoStack.remove(waitingListRedoStack.size() - 1);
            setUndoRedoButtonsDisabled(false, waitingListRedoStack.isEmpty());
            WindowManager.updateWaitingList();
        } else if (medicalHistoryProceduresPane.isVisible()) {
            //Add the current procedures lists to the redo stack
            procedureUndoStack.add(new User(currentUser));
            //Copy the proceudres lists from the top element of the undo stack
            currentUser.copyProceduresListsFrom(procedureRedoStack.get(procedureRedoStack.size() - 1));
            //Remove the top element of the undo stack
            procedureRedoStack.remove(procedureRedoStack.size() - 1);

            setUndoRedoButtonsDisabled(false, procedureRedoStack.isEmpty());
            WindowManager.updateProcedures();
        } else if (medicalHistoryDiseasesPane.isVisible()) {
            //Add the current disease lists to the redo stack
            diseaseUndoStack.add(new User(currentUser));
            //Copy the disease lists from the top element of the undo stack
            currentUser.copyDiseaseListsFrom(diseaseRedoStack.get(diseaseRedoStack.size() - 1));
            //Remove the top element of the undo stack
            diseaseRedoStack.remove(diseaseRedoStack.size() - 1);

            setUndoRedoButtonsDisabled(false, medicationRedoStack.isEmpty());
            WindowManager.updateDiseases();
        }
        statusIndicator.setStatus("Redid last action", false);
        titleBar.saved(false);
    }

    /**
     * Function which is called when the user wants to update their account settings in the user Window,
     * and creates a new account settings window to do so. Then does a prompt for the password as well.
     */
    public void updateAccountSettings() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("View Account Settings");
        dialog.setHeaderText("In order to view your account settings, \nplease enter your login details.");
        dialog.setContentText("Please enter your password:");
        WindowManager.setIconAndStyle(dialog.getDialogPane());
        dialog.getDialogPane().getStyleClass().add("dialog");

        Optional<String> password = dialog.showAndWait();
        if (password.isPresent()) { //Ok was pressed, Else cancel
            if (password.get().equals(currentUser.getPassword())) {
                try {
                    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/accountSettings.fxml"));
                    Stage stage = new Stage();
                    stage.getIcons().add(WindowManager.getIcon());
                    stage.setResizable(false);
                    stage.setTitle("Account Settings");
                    stage.setScene(new Scene(root, 270, 330));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    WindowManager.setCurrentUserForAccountSettings(currentUser);
                    WindowManager.setAccountSettingsEnterEvent();
                    stage.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else { // Password incorrect
                WindowManager.createAlert(AlertType.INFORMATION, "Incorrect", "Incorrect password. ", "Please enter the correct password to view " +
                        "account settings").show();
            }
        }
    }

    /**
     * Disable the logout button if this user window is the child of a clinician window.
     */
    public void setAsChildWindow() {
        logoutMenuItem.setDisable(true);
        logoutButton.setDisable(true);
    }

    /**
     * Function which is called when the user wants to logout of the application and log into a new user
     */
    public void logout() {
        Alert alert = WindowManager.createAlert(AlertType.CONFIRMATION, "Are you sure?", "Are you sure would like to log out? ", "Logging out " +
                "without saving loses your non-saved data.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            String text = History.prepareFileStringGUI(currentUser.getId(), "logout");
            History.printToFile(streamOut, text);
            WindowManager.setScene(TFScene.login);
            WindowManager.resetScene(TFScene.userWindow);
        } else {
            alert.close();
        }
    }

    /**
     * Function which is called when the user wants to exit the application.
     */
    public void stop() {
        Alert alert = WindowManager.createAlert(AlertType.CONFIRMATION, "Are you sure?",
                "Are you sure would like to exit the window? ", "Exiting without saving loses your non-saved data.");
        alert.getDialogPane().lookupButton(ButtonType.OK).setId("exitOK");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.out.println("Exiting GUI");
            String text = History.prepareFileStringGUI(currentUser.getId(), "quit");
            History.printToFile(streamOut, text);

            Stage stage = (Stage) welcomePane.getScene().getWindow();
            stage.close();
        } else {
            alert.close();
        }

    }

    /**
     * Sets the waiting list button to visible if shown is true
     *
     * @param shown True if the waiting list button is to be shown, otherwise False
     */
    public void setControlsShown(Boolean shown) {
        if (currentUser != null) {
            if (currentUser.isReceiver()) {
                waitingListButton.setVisible(true);
            }
        } else {
            waitingListButton.setVisible(shown);
        }
    }

    public void populateUserFields(){
        attributesController.populateUserFields();
    }


    public ArrayList<User> getWaitingListUndoStack() {
        return waitingListUndoStack;
    }
}
