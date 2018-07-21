package seng302.GUI.Controllers.User;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import seng302.Generic.APIResponse;
import seng302.Generic.Debugger;
import seng302.Generic.WindowManager;
import seng302.User.User;
import seng302.User.WaitingListItem;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static seng302.Generic.WindowManager.setButtonSelected;

/**
 * Class which handles all the logic for the User Window.
 * Handles all functions including:
 * Saving, Undo, Redo, All input fields and more.
 */
public class UserController implements Initializable {
    @FXML
    private Label userDisplayText;
    @FXML
    private GridPane background;
    @FXML
    private AnchorPane medicationsPane, diseasesPane, proceduresPane, attributesPane, historyPane, waitingListPane;
    @FXML
    private Pane welcomePane;
    @FXML
    private MenuItem undoButton, redoButton, logoutMenuItem;
    @FXML
    private Button logoutButton, undoBannerButton, redoBannerButton, medicationsButton, waitingListButton,
            userAttributesButton, diseasesButton, proceduresButton, historyButton;
    @FXML
    private StatusBar statusBar;
    @FXML
    private UserAttributesController attributesController;
    @FXML
    private UserMedicationsController medicationsController;
    @FXML
    private UserDiseasesController diseasesController;
    @FXML
    private UserProceduresController proceduresController;
    @FXML
    private UserWaitingListController waitingListController;
    @FXML
    private UserHistoryController historyController;

    public StatusIndicator statusIndicator = new StatusIndicator();
    private TitleBar titleBar;

    private User currentUser;


    public UserController() {
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
        if (currentUser.getPreferredName() != null) {
            setWelcomeText("Welcome, " + currentUser.getPreferredName());
        } else {
            setWelcomeText("Welcome, " + currentUser.getName());
        }

        diseasesController.setCurrentUser(currentUser);
        proceduresController.setCurrentUser(currentUser);
        waitingListController.setCurrentUser(currentUser);
        waitingListController.populateWaitingList();
        medicationsController.setCurrentUser(currentUser);
        attributesController.setCurrentUser(currentUser);
        attributesController.populateUserFields();
        historyController.setCurrentUser(currentUser);
        historyController.populateTable();

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
     * Refresh the current user waiting list and correctly set highlights for organs that are being donated and received.
     */
    public void populateHistoryTable() {
        historyController.populateTable();
    }

    /**
     * Updates the list of diseases on the disease tab to the list stored in the current user.
     */
    public void updateDiseases() {
        diseasesController.updateDiseases();
    }

    /**
     * Updates the current user with the most recent server version
     */
    public void refresh() {
        Alert alert = WindowManager.createAlert(AlertType.CONFIRMATION, "Confirm Refresh", "Are you sure you want to refresh?",
                "Refreshing will overwrite your all unsaved changes.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK && attributesController.updateUser()) {
            Gson gson = new Gson();
            APIResponse response = WindowManager.getDatabase().loginUser(currentUser.getUsername(), currentUser.getPassword());
            System.out.println(response.getAsString());
            if (response.isValidJson()) {
                attributesController.undoStack.clear();
                attributesController.redoStack.clear();
                medicationsController.undoStack.clear();
                medicationsController.redoStack.clear();
                proceduresController.undoStack.clear();
                proceduresController.redoStack.clear();
                diseasesController.undoStack.clear();
                diseasesController.redoStack.clear();
                waitingListController.undoStack.clear();
                waitingListController.redoStack.clear();
                setUndoRedoButtonsDisabled(true, true);
                JsonObject serverResponse = response.getAsJsonObject();
                setCurrentUser(gson.fromJson(serverResponse, User.class));
                alert.close();
            } else {
                alert.close();
                alert = WindowManager.createAlert(AlertType.ERROR, "Refresh Failed", "Refresh failed",
                        "User data could not be refreshed because there was an error contacting the server.");
                alert.showAndWait();
            }
        }
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
            Debugger.log("Setting this as the main user window controller. This should NOT be coming up if this is a child window (popup from clinician or admin)");
            WindowManager.setUserController(this);
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

        historyController.setStatusIndicator(statusIndicator);
        historyController.setTitleBar(titleBar);
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
            medicationsController.addToUndoStack(new User(currentUser));
        } else if (diseasesPane.isVisible()) {
            diseasesController.addToUndoStack(new User(currentUser));
        } else if (proceduresPane.isVisible()) {
            proceduresController.addToUndoStack(new User(currentUser));
        } else if (waitingListPane.isVisible()) {
            waitingListController.addToUndoStack(new User(currentUser));
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
        historyPane.setVisible(false);
        diseasesPane.setVisible(false);
        proceduresPane.setVisible(false);
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
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Attributes");
    }

    /**
     * Sets the medications pane as the visible pane
     */
    public void showMedicationsPane() {
        hideAllTabs();
        medicationsPane.setVisible(true);
        setButtonSelected(medicationsButton, true);
        setUndoRedoButtonsDisabled(medicationsController.undoEmpty(), medicationsController.redoEmpty());
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Medications");
    }

    /**
     * Sets the diseases pane as the visible pane
     */
    public void showMedicalHistoryDiseasesPane() {
        hideAllTabs();
        diseasesPane.setVisible(true);
        setButtonSelected(diseasesButton, true);
        setUndoRedoButtonsDisabled(diseasesController.undoEmpty(), diseasesController.redoEmpty());
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Disease History");
    }

    /**
     * Sets the medical history pane as the visible pane
     */
    public void showMedicalHistoryProceduresPane() {
        hideAllTabs();
        proceduresPane.setVisible(true);
        setButtonSelected(proceduresButton, true);
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Procedure History");
    }

    /**
     * Sets the history pane as the visible pane
     */
    public void showHistoryPane() {
        hideAllTabs();
        historyPane.setVisible(true);
        setButtonSelected(historyButton, true);
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Action History");
    }

    public void showWaitingListPane() {
        hideAllTabs();
        waitingListPane.setVisible(true);
        setUndoRedoButtonsDisabled(waitingListController.undoEmpty(), waitingListController.redoEmpty());
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Waiting List");
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
            attributesController.populateUserFields();
            historyController.populateTable();
            WindowManager.getDatabase().updateUser(currentUser);
            WindowManager.getDatabase().updateUserOrgans(currentUser);
            WindowManager.getDatabase().updateUserProcedures(currentUser);
            WindowManager.getDatabase().updateUserDiseases(currentUser);

            System.out.println("Attempting to save waitingListItems");
            System.out.println("CurrentState: ");
            for(WaitingListItem i:currentUser.getWaitingListItems()){
                System.out.println(i.getOrganType() + "," + i.getStillWaitingOn());
            }

            WindowManager.getDatabase().updateWaitingListItems(currentUser);

            currentUser.addHistoryEntry("Updated", "Changes were saved to the server.");
            titleBar.saved(true);
            titleBar.setTitle(currentUser.getPreferredName(), "User");
            statusIndicator.setStatus("Saved", false);

            WindowManager.getClinicianController().updateFoundUsers();
            WindowManager.updateTransplantWaitingList();


            /*int index = 0;
            for(User user: DataManager.users) {
                if(user.getUsername().equals(currentUser.getUsername())) {
                    break;
                }
                index++;
            }
            DataManager.users.set(index, currentUser);*/
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
            medicationsController.undo();
            setUndoRedoButtonsDisabled(medicationsController.undoEmpty(), false);
        } else if (waitingListPane.isVisible()) {
            waitingListController.undo();
            setUndoRedoButtonsDisabled(waitingListController.undoEmpty(), false);
        } else if (proceduresPane.isVisible()) {
            proceduresController.undo();
            setUndoRedoButtonsDisabled(proceduresController.undoEmpty(), false);
        } else if (diseasesPane.isVisible()) {
            diseasesController.undo();
            setUndoRedoButtonsDisabled(diseasesController.undoEmpty(), false);
        }
        currentUser.addHistoryEntry("Action undone", "An action was undone.");
        historyController.populateTable();
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
            medicationsController.redo();
            setUndoRedoButtonsDisabled(false, medicationsController.redoEmpty());
        } else if (waitingListPane.isVisible()) {
            waitingListController.redo();
            setUndoRedoButtonsDisabled(false, waitingListController.redoEmpty());

        } else if (proceduresPane.isVisible()) {
            proceduresController.redo();
            setUndoRedoButtonsDisabled(false, proceduresController.redoEmpty());
        } else if (diseasesPane.isVisible()) {
            diseasesController.redo();
            setUndoRedoButtonsDisabled(false, diseasesController.redoEmpty());
        }
        currentUser.addHistoryEntry("Action redone", "An action was redone.");
        historyController.populateTable();
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
                    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/user/userAccountSettings.fxml"));
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
            Debugger.log("Exiting GUI");
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