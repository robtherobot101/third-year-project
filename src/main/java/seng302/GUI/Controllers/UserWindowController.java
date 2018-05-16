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
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.xml.crypto.Data;
import org.controlsfx.control.StatusBar;
import seng302.GUI.StatusIndicator;
import seng302.GUI.TFScene;
import seng302.GUI.TitleBar;
import seng302.Generic.*;
import seng302.User.Attribute.*;
import seng302.User.User;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static seng302.Generic.IO.streamOut;
import static seng302.Generic.WindowManager.setButtonSelected;

/**
 * Class which handles all the logic for the User Window.
 * Handles all functions including:
 * Saving, Undo, Redo, All input fields and more.
 */
public class UserWindowController implements Initializable {
    @FXML
    private Label userDisplayText, userHistoryLabel;
    @FXML
    private GridPane historyGridPane, background;
    @FXML
    private AnchorPane medicationsPane, medicalHistoryDiseasesPane, medicalHistoryProceduresPane, attributesPane;
    @FXML
    private AnchorPane waitingListPane;
    @FXML
    private Pane welcomePane;
    @FXML
    private MenuItem undoButton, redoButton, logoutMenuItem;
    @FXML
    private Button logoutButton, undoBannerButton, redoBannerButton, medicationsButton, waitingListButton,
            userAttributesButton, diseasesButton, proceduresButton, historyButton;
    @FXML
    private TreeTableView<String> historyTreeTableView;
    @FXML
    private TreeTableColumn<String, String> dateTimeColumn, actionColumn;
    @FXML
    private StatusBar statusBar;
    @FXML
    private UserAttributesController attributesController;
    @FXML
    private MedicationsController medicationsController;
    @FXML
    private MedicalHistoryDiseasesController diseasesController;
    @FXML
    private MedicalHistoryProceduresController proceduresController;
    @FXML
    private WaitingListController waitingListController;

    public StatusIndicator statusIndicator = new StatusIndicator();
    private TitleBar titleBar;

    private LinkedList<User> waitingListUndoStack = new LinkedList<>(), waitingListRedoStack = new LinkedList<>();
    private LinkedList<User> medicationUndoStack = new LinkedList<>(), medicationRedoStack = new LinkedList<>();
    private LinkedList<User> procedureUndoStack = new LinkedList<>(), procedureRedoStack = new LinkedList<>();
    private LinkedList<User> diseaseUndoStack = new LinkedList<>(), diseaseRedoStack = new LinkedList<>();
    private User currentUser;


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
        setWelcomeText("Welcome, " + currentUser.getPreferredName());

        diseasesController.setCurrentUser(currentUser);
        proceduresController.setCurrentUser(currentUser);
        waitingListController.setCurrentUser(currentUser);
        waitingListController.populateWaitingList();
        medicationsController.setCurrentUser(currentUser);
        attributesController.setCurrentUser(currentUser);
        attributesController.populateUserFields();

        setUndoRedoButtonsDisabled(true, true);

        titleBar.setTitle(currentUser.getPreferredName(), "User", "Home");
    }

    public void setWelcomeText(String text) {
        userDisplayText.setText(text);
    }

    /**
     * Refresh the current user attributes from the user input fields and correctly set highlights for organs that are being donated and received.
     */
    public void populateUserAttributes() {
        attributesController.populateUserFields();
        attributesController.highlightOrganCheckBoxes();
    }

    /**
     * Refresh the current user waiting list and correctly set highlights for organs that are being donated and received.
     */
    public void populateWaitingList() {
        waitingListController.populateWaitingList();
        attributesController.highlightOrganCheckBoxes();
    }

    /**
     * Updates the list of diseases on the disease tab to the list stored in the current user.
     */
    public void updateDiseases() {
        diseasesController.updateDiseases();
    }

    /**
     * Set up the User window.
     *
     * @param location  Not used
     * @param resources Not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (WindowManager.getScene(TFScene.userWindow) == null) {
            System.out.println("Setting this as the main user window controller. This should NOT be coming up if this is a child window (popup from clinician or admin)");
            WindowManager.setUserWindowController(this);
        }
        welcomePane.setVisible(true);
        attributesController.setParent(this);
        medicationsController.setParent(this);
        diseasesController.setParent(this);
        proceduresController.setParent(this);
        waitingListController.setParent(this);

        setControlsShown(false);

        Image welcomeImage = new Image("/OrganDonation.jpg");
        BackgroundImage imageBackground = new BackgroundImage(welcomeImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        welcomePane.setBackground(new Background(imageBackground));

        waitingListButton.setOnAction((ActionEvent event) -> {
            showWaitingListPane();
            waitingListController.populateWaitingList();
            waitingListController.populateOrgansComboBox();
        });

        statusIndicator.setStatusBar(statusBar);

        // Pass the status bar and title bar objects to the embedded controllers
        attributesController.setStatusIndicator(statusIndicator);
        attributesController.setTitleBar(titleBar);

        medicationsController.setStatusIndicator(statusIndicator);
        medicationsController.setTitleBar(titleBar);

        diseasesController.setStatusIndicator(statusIndicator);
        diseasesController.setTitleBar(titleBar);

        proceduresController.setStatusIndicator(statusIndicator);
        proceduresController.setTitleBar(titleBar);

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
     * Add the current user object to the relevant undo stack.
     */
    public void addCurrentUserToUndoStack() {
        if (attributesPane.isVisible()) {
            attributesController.addToUndoStack(new User(currentUser));
        } else if (medicationsPane.isVisible()) {
            medicationUndoStack.add(new User(currentUser));
            medicationRedoStack.clear();
        } else if (medicalHistoryDiseasesPane.isVisible()) {
            diseaseUndoStack.add(new User(currentUser));
            diseaseRedoStack.clear();
        } else if (medicalHistoryProceduresPane.isVisible()) {
            procedureUndoStack.add(new User(currentUser));
            procedureRedoStack.clear();
        } else if (waitingListPane.isVisible()) {
            waitingListUndoStack.add(new User(currentUser));
            waitingListRedoStack.clear();
        }
        setUndoRedoButtonsDisabled(false, true);
    }

    /**
     * Set whether the undo and redo buttons are enabled.
     *
     * @param undoDisabled Whether the undo buttons should be disabled
     * @param redoDisabled Whether the redo buttons should be disabled
     */
    public void setUndoRedoButtonsDisabled(boolean undoDisabled, boolean redoDisabled) {
        undoButton.setDisable(undoDisabled);
        undoBannerButton.setDisable(undoDisabled);
        redoButton.setDisable(redoDisabled);
        redoBannerButton.setDisable(redoDisabled);
    }

    /**
     * Hides all of the main panes.
     */
    private void hideAllTabs() {
        setButtonSelected(userAttributesButton, false);
        setButtonSelected(medicationsButton, false);
        setButtonSelected(diseasesButton, false);
        setButtonSelected(proceduresButton, false);
        setButtonSelected(historyButton, false);

        welcomePane.setVisible(false);
        attributesPane.setVisible(false);
        historyGridPane.setVisible(false);
        medicalHistoryDiseasesPane.setVisible(false);
        medicalHistoryProceduresPane.setVisible(false);
        medicationsPane.setVisible(false);
        waitingListPane.setVisible(false);
        setUndoRedoButtonsDisabled(true, true);
    }

    /**
     * Sets the welcome pane as the visible pane
     */
    public void showWelcomePane() {
        hideAllTabs();
        welcomePane.setVisible(true);
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Home");
    }

    /**
     * Sets the User Attribute pane as the visible pane
     */
    public void showAttributesPane() {
        hideAllTabs();
        attributesPane.setVisible(true);
        setButtonSelected(userAttributesButton, true);
        setUndoRedoButtonsDisabled(attributesController.undoEmpty(), attributesController.redoEmpty());
        System.out.println(currentUser);
        System.out.println(currentUser.getPreferredName());
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Attributes");
    }

    /**
     * Sets the medications pane as the visible pane
     */
    public void showMedicationsPane() {
        hideAllTabs();
        medicationsPane.setVisible(true);
        setButtonSelected(medicationsButton, true);
        setUndoRedoButtonsDisabled(medicationUndoStack.isEmpty(), medicationRedoStack.isEmpty());
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Medications");
    }

    /**
     * Sets the diseases pane as the visible pane
     */
    public void showMedicalHistoryDiseasesPane() {
        hideAllTabs();
        medicalHistoryDiseasesPane.setVisible(true);
        setButtonSelected(diseasesButton, true);
        setUndoRedoButtonsDisabled(diseaseUndoStack.isEmpty(), diseaseRedoStack.isEmpty());
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Disease History");
    }

    /**
     * Sets the medical history pane as the visible pane
     */
    public void showMedicalHistoryProceduresPane() {
        hideAllTabs();
        medicalHistoryProceduresPane.setVisible(true);
        setButtonSelected(proceduresButton, true);
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Procedure History");
    }

    /**
     * Sets the history pane as the visible pane
     */
    public void showHistoryPane() {
        hideAllTabs();
        historyGridPane.setVisible(true);
        setButtonSelected(historyButton, true);
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Action History");
    }

    public void showWaitingListPane() {
        hideAllTabs();
        waitingListPane.setVisible(true);
        setUndoRedoButtonsDisabled(waitingListUndoStack.isEmpty(), waitingListRedoStack.isEmpty());
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Waiting List");
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
            medicationsController.updateUser();
            diseasesController.updateUser();
            proceduresController.updateUser();
            IO.saveUsers(IO.getUserPath(), ProfileType.USER);
            attributesController.populateUserFields();
            String text = History.prepareFileStringGUI(currentUser.getId(), "update");
            History.printToFile(streamOut, text);
            populateHistoryTable();
            titleBar.saved(true);
            titleBar.setTitle(currentUser.getPreferredName(), "User");
            statusIndicator.setStatus("Saved", false);
            WindowManager.getClinicianController().updateFoundUsers();
            WindowManager.updateTransplantWaitingList();
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
            // Perform the undo and get whether the stack is now empty
            attributesController.undo();
            setUndoRedoButtonsDisabled(attributesController.undoEmpty(), false);
        } else if (medicationsPane.isVisible()) {
            medicationsController.updateUser();
            //Add the current medication lists to the redo stack
            medicationRedoStack.add(new User(currentUser));
            //Copy the medication lists from the top element of the undo stack
            currentUser.copyMedicationListsFrom(medicationUndoStack.getLast());
            //Remove the top element of the undo stack
            medicationUndoStack.removeLast();

            setUndoRedoButtonsDisabled(medicationUndoStack.isEmpty(), false);
            medicationsController.updateMedications();
        } else if (waitingListPane.isVisible()) {
            waitingListRedoStack.add(new User(currentUser));
            currentUser.copyWaitingListsFrom(waitingListUndoStack.getLast());
            waitingListUndoStack.removeLast();
            setUndoRedoButtonsDisabled(waitingListUndoStack.isEmpty(), false);
            waitingListController.populateWaitingList();
        } else if (medicalHistoryProceduresPane.isVisible()) {
            proceduresController.updateUser();
            //Add the current procedures lists to the redo stack
            procedureRedoStack.add(new User(currentUser));
            //Copy the procedures lists from the top element of the undo stack
            currentUser.copyProceduresListsFrom(procedureUndoStack.getLast());
            //Remove the top element of the undo stack
            procedureUndoStack.removeLast();

            setUndoRedoButtonsDisabled(procedureUndoStack.isEmpty(), false);
            proceduresController.updateProcedures();
        } else if (medicalHistoryDiseasesPane.isVisible()) {
            diseasesController.updateUser();
            //Add the current disease lists to the redo stack
            diseaseRedoStack.add(new User(currentUser));
            //Copy the disease lists from the top element of the undo stack
            currentUser.copyDiseaseListsFrom(diseaseUndoStack.getLast());
            //Remove the top element of the undo stack
            diseaseUndoStack.removeLast();

            setUndoRedoButtonsDisabled(diseaseUndoStack.isEmpty(), false);
            diseasesController.updateDiseases();
        }
        String text = History.prepareFileStringGUI(currentUser.getId(), "undo");
        History.printToFile(streamOut, text);
        populateHistoryTable();
        statusIndicator.setStatus("Undid last action", false);
        titleBar.saved(false);
    }

    /**
     * Called when the redo button is pushed, and reverts the last undo performed by the user.
     * Then checks to see if there are any other actions that can be redone and adjusts the buttons accordingly.
     */
    public void redo() {
        if (attributesPane.isVisible()) {
            attributesController.redo();
            setUndoRedoButtonsDisabled(false, attributesController.redoEmpty());
        } else if (medicationsPane.isVisible()) {
            medicationsController.updateUser();
            //Add the current medication lists to the undo stack
            medicationUndoStack.add(new User(currentUser));
            //Copy the medications lists from the top element of the redo stack
            currentUser.copyMedicationListsFrom(medicationRedoStack.getLast());
            //Remove the top element of the redo stack
            medicationRedoStack.removeLast();

            setUndoRedoButtonsDisabled(false, medicationRedoStack.isEmpty());
            medicationsController.updateMedications();
        } else if (waitingListPane.isVisible()) {
            waitingListUndoStack.add(new User(currentUser));
            currentUser.copyWaitingListsFrom(waitingListRedoStack.getLast());
            waitingListRedoStack.removeLast();
            setUndoRedoButtonsDisabled(false, waitingListRedoStack.isEmpty());
            waitingListController.populateWaitingList();
        } else if (medicalHistoryProceduresPane.isVisible()) {
            proceduresController.updateUser();
            //Add the current procedures lists to the redo stack
            procedureUndoStack.add(new User(currentUser));
            //Copy the procedures lists from the top element of the undo stack
            currentUser.copyProceduresListsFrom(procedureRedoStack.getLast());
            //Remove the top element of the undo stack
            procedureRedoStack.removeLast();

            setUndoRedoButtonsDisabled(false, procedureRedoStack.isEmpty());
            proceduresController.updateProcedures();
        } else if (medicalHistoryDiseasesPane.isVisible()) {
            diseasesController.updateUser();
            //Add the current disease lists to the redo stack
            diseaseUndoStack.add(new User(currentUser));
            //Copy the disease lists from the top element of the undo stack
            currentUser.copyDiseaseListsFrom(diseaseRedoStack.getLast());
            //Remove the top element of the undo stack
            diseaseRedoStack.removeLast();

            setUndoRedoButtonsDisabled(false, diseaseRedoStack.isEmpty());
            diseasesController.updateDiseases();
        }
        String text = History.prepareFileStringGUI(currentUser.getId(), "redo");
        History.printToFile(streamOut, text);
        populateHistoryTable();
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
        waitingListController.setControlsShown(shown);
        proceduresController.setControlsShown(shown);
        diseasesController.setControlsShown(shown);
        medicationsController.setControlsShown(shown);
        logoutMenuItem.setDisable(shown);
        logoutButton.setDisable(shown);
        waitingListButton.setVisible((currentUser != null && currentUser.isReceiver()) || shown);
    }
}