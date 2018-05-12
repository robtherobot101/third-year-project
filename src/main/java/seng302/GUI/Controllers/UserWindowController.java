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
import static seng302.Generic.WindowManager.setButtonSelected;

/**
 * Class which handles all the logic for the User Window.
 * Handles all functions including:
 * Saving, Undo, Redo, All input fields and more.
 */
public class UserWindowController implements Initializable {

    private TitleBar titleBar;
    @FXML
    private Label userDisplayText, settingAttributesLabel, ageLabel, bmiLabel, bloodPressureLabel, userHistoryLabel;
    @FXML
    private GridPane attributesGridPane, historyGridPane, background;
    @FXML
    private AnchorPane medicationsPane, medicalHistoryDiseasesPane, medicalHistoryProceduresPane;
    @FXML
    private AnchorPane waitingListPane;
    @FXML
    private Pane welcomePane;
    @FXML
    private TextField firstNameField, middleNameField, lastNameField, addressField, regionField, heightField, weightField, bloodPressureTextField, preferredFirstNameField, preferredMiddleNamesField, preferredLastNameField;
    @FXML
    private DatePicker dateOfBirthPicker, dateOfDeathPicker;
    @FXML
    private ComboBox<Gender> genderComboBox, genderIdentityComboBox;
    @FXML
    private ComboBox<BloodType> bloodTypeComboBox;
    @FXML
    private ComboBox<SmokerStatus> smokerStatusComboBox;
    @FXML
    private ComboBox<AlcoholConsumption> alcoholConsumptionComboBox;
    @FXML
    private CheckBox liverCheckBox, kidneyCheckBox, pancreasCheckBox, heartCheckBox, lungCheckBox, intestineCheckBox, corneaCheckBox,
            middleEarCheckBox, skinCheckBox, boneMarrowCheckBox, connectiveTissueCheckBox;
    @FXML
    private MenuItem undoButton, redoButton, logoutMenuItem;
    @FXML
    private Button logoutButton, undoBannerButton, redoBannerButton, medicationsButton, waitingListButton,
            userAttributesButton, diseasesButton, proceduresButton, historyButton;
    @FXML
    private TreeTableView<String> historyTreeTableView;
    @FXML
    private TreeTableColumn<String, String> dateTimeColumn, actionColumn;

    private HashMap<Organ, CheckBox> organTickBoxes;

    private LinkedList<User> waitingListUndoStack = new LinkedList<>(), waitingListRedoStack = new LinkedList<>();
    private LinkedList<User> attributeUndoStack = new LinkedList<>(), attributeRedoStack = new LinkedList<>();
    private LinkedList<User> medicationUndoStack = new LinkedList<>(), medicationRedoStack = new LinkedList<>();
    private LinkedList<User> procedureUndoStack = new LinkedList<>(), procedureRedoStack = new LinkedList<>();
    private LinkedList<User> diseaseUndoStack = new LinkedList<>(), diseaseRedoStack = new LinkedList<>();
    private User currentUser;

    @FXML
    private StatusBar statusBar;
    @FXML
    private MedicationsController medicationsController;
    @FXML
    private MedicalHistoryDiseasesController diseasesController;
    @FXML
    private MedicalHistoryProceduresController proceduresController;
    @FXML
    private WaitingListController waitingListController;

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

        diseasesController.setCurrentUser(currentUser);
        proceduresController.setCurrentUser(currentUser);
        waitingListController.setCurrentUser(currentUser);
        waitingListController.populateWaitingList();
        medicationsController.initializeUser(currentUser);

        userDisplayText.setText("Currently logged in as: " + currentUser.getPreferredName());
        attributeUndoStack.clear();
        attributeRedoStack.clear();
        undoButton.setDisable(true);
        undoBannerButton.setDisable(true);
        redoButton.setDisable(true);
        redoBannerButton.setDisable(true);
        bloodPressureLabel.setText("");
        titleBar.setTitle(currentUser.getPreferredName(), "User", "Home");
    }

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
        WindowManager.setUserWindowController(this);
        welcomePane.setVisible(true);
        attributesGridPane.setVisible(false);
        historyGridPane.setVisible(false);
        medicationsPane.setVisible(false);
        genderComboBox.setItems(FXCollections.observableArrayList(Gender.values()));
        genderIdentityComboBox.setItems(FXCollections.observableArrayList(Gender.values()));
        bloodTypeComboBox.setItems(FXCollections.observableArrayList(BloodType.values()));
        alcoholConsumptionComboBox.setItems(FXCollections.observableArrayList(AlcoholConsumption.values()));
        smokerStatusComboBox.setItems(FXCollections.observableArrayList(SmokerStatus.values()));

        organTickBoxes = new HashMap<>();
        organTickBoxes.put(Organ.KIDNEY, kidneyCheckBox);
        organTickBoxes.put(Organ.CORNEA, corneaCheckBox);
        organTickBoxes.put(Organ.BONE, boneMarrowCheckBox);
        organTickBoxes.put(Organ.LIVER, liverCheckBox);
        organTickBoxes.put(Organ.EAR, middleEarCheckBox);
        organTickBoxes.put(Organ.HEART, heartCheckBox);
        organTickBoxes.put(Organ.INTESTINE, intestineCheckBox);
        organTickBoxes.put(Organ.PANCREAS, pancreasCheckBox);
        organTickBoxes.put(Organ.SKIN, skinCheckBox);
        organTickBoxes.put(Organ.TISSUE, connectiveTissueCheckBox);
        organTickBoxes.put(Organ.LUNG, lungCheckBox);

        setControlsShown(false);

        Image welcomeImage = new Image("/OrganDonation.jpg");
        BackgroundImage imageBackground = new BackgroundImage(welcomeImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        welcomePane.setBackground(new Background(imageBackground));

        //Add listeners for attribute undo and redo
        preferredFirstNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                attributeFieldUnfocused();
            }
        });
        preferredMiddleNamesField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                attributeFieldUnfocused();
            }
        });
        preferredLastNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                attributeFieldUnfocused();
            }
        });
        firstNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                attributeFieldUnfocused();
            }
        });
        middleNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                attributeFieldUnfocused();
            }
        });
        lastNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                attributeFieldUnfocused();
            }
        });
        addressField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                attributeFieldUnfocused();
            }
        });
        regionField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                attributeFieldUnfocused();
            }
        });
        dateOfBirthPicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                attributeFieldUnfocused();
            }
        });
        dateOfDeathPicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                attributeFieldUnfocused();
            }
        });
        heightField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                attributeFieldUnfocused();
            }
        });
        weightField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                attributeFieldUnfocused();
            }
        });
        bloodPressureTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                attributeFieldUnfocused();
            }
        });

        //Add listeners to correctly update BMI and blood pressure based on user input
        heightField.textProperty().addListener((observable, oldValue, newValue) -> updateBMI());
        weightField.textProperty().addListener((observable, oldValue, newValue) -> updateBMI());
        bloodPressureTextField.textProperty().addListener((observable, oldValue, newValue) -> updateBloodPressure());

        waitingListButton.setOnAction((ActionEvent event) -> {
            showWaitingListPane();
            waitingListController.populateWaitingList();
            waitingListController.populateOrgansComboBox();
        });

        statusIndicator.setStatusBar(statusBar);

        // Pass the status bar and title bar objects to the embedded controllers
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
     * Checks for any new updates when an attribute field loses focus, and appends to the attribute undo stack if there is new changes.
     */
    public void attributeFieldUnfocused() {
        User oldFields = new User(currentUser);
        if (updateUser() && !currentUser.fieldsEqual(oldFields)) {
            attributeUndoStack.add(new User(oldFields));
            attributeRedoStack.clear();
            setUndoRedoButtonsDisabled(false, true);
            titleBar.saved(false);
            statusIndicator.setStatus("Edited user details", false);
        }
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
     * Hides all of the main panes.
     */
    private void hideAllTabs() {
        setButtonSelected(userAttributesButton, false);
        setButtonSelected(medicationsButton, false);
        setButtonSelected(diseasesButton, false);
        setButtonSelected(proceduresButton, false);
        setButtonSelected(historyButton, false);

        welcomePane.setVisible(false);
        attributesGridPane.setVisible(false);
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
        attributesGridPane.setVisible(true);
        setButtonSelected(userAttributesButton, true);
        setUndoRedoButtonsDisabled(attributeUndoStack.isEmpty(), attributeRedoStack.isEmpty());
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
     * Function which takes the current user object that has logged in and
     * takes all their attributes and populates the user attributes on the attributes pane accordingly.
     */
    public void populateUserFields() {
        settingAttributesLabel.setText("Attributes for " + currentUser.getPreferredName());
        String[] splitNames = currentUser.getNameArray();
        firstNameField.setText(splitNames[0]);
        if (splitNames.length > 2) {
            String[] middleName = new String[splitNames.length - 2];
            System.arraycopy(splitNames, 1, middleName, 0, splitNames.length - 2);
            middleNameField.setText(String.join(",", middleName));
            lastNameField.setText(splitNames[splitNames.length - 1]);
        } else if (splitNames.length == 2) {
            middleNameField.setText("");
            lastNameField.setText(splitNames[1]);
        } else {
            middleNameField.setText("");
            lastNameField.setText("");
        }
        String[] splitPreferredNames = currentUser.getPreferredNameArray();
        preferredFirstNameField.setText(splitPreferredNames[0]);
        if (splitPreferredNames.length > 2) {
            String[] preferredMiddleName = new String[splitPreferredNames.length - 2];
            System.arraycopy(splitPreferredNames, 1, preferredMiddleName, 0, splitPreferredNames.length - 2);
            preferredMiddleNamesField.setText(String.join(",", preferredMiddleName));
            preferredLastNameField.setText(splitPreferredNames[splitPreferredNames.length - 1]);
        } else if (splitPreferredNames.length == 2) {
            preferredMiddleNamesField.setText("");
            preferredLastNameField.setText(splitPreferredNames[1]);
        } else {
            preferredMiddleNamesField.setText("");
            preferredLastNameField.setText("");
        }
        addressField.setText(currentUser.getCurrentAddress());
        regionField.setText(currentUser.getRegion());

        dateOfBirthPicker.setValue(currentUser.getDateOfBirth());
        dateOfDeathPicker.setValue(currentUser.getDateOfDeath());
        updateAge();

        bloodPressureTextField.setText(currentUser.getBloodPressure());

        genderComboBox.setValue(currentUser.getGender());
        genderIdentityComboBox.setValue(currentUser.getGenderIdentity());
        bloodTypeComboBox.setValue(currentUser.getBloodType());
        smokerStatusComboBox.setValue(currentUser.getSmokerStatus());
        alcoholConsumptionComboBox.setValue(currentUser.getAlcoholConsumption());

        for (Organ key : organTickBoxes.keySet()) {
            organTickBoxes.get(key).setSelected(currentUser.getOrgans().contains(key));
        }

        weightField.setText(currentUser.getWeight() == -1 ? "" : Double.toString(currentUser.getWeight()));
        heightField.setText(currentUser.getHeight() == -1 ? "" : Double.toString(currentUser.getHeight()));

        updateBMI();
        updateBloodPressure();
    }

    /**
     * Function which takes all the inputs of the user attributes window.
     * Checks if all these inputs are valid and then sets the user's attributes to those inputted.
     */
    private boolean updateUser() {
        //Extract names from user
        String firstName = firstNameField.getText();
        String[] middleNames = middleNameField.getText().isEmpty() ? new String[]{""} : middleNameField.getText().split(",");
        String lastName = lastNameField.getText();

        int isLastName = lastNameField.getText() == null || lastNameField.getText().isEmpty() ? 0 : 1;
        String[] name = new String[1 + middleNames.length + isLastName];
        name[0] = firstName;
        System.arraycopy(middleNames, 0, name, 1, middleNames.length);
        if (isLastName == 1) {
            name[name.length - 1] = lastName;
        }

        String preferredFirstName = preferredFirstNameField.getText();
        String[] preferredMiddleNames = preferredMiddleNamesField.getText().isEmpty() ? new String[]{""} : preferredMiddleNamesField.getText().split(",");
        System.out.println(preferredMiddleNames[0]);
        String preferredLastName = preferredLastNameField.getText();

        int isPreferredLastName = preferredLastNameField.getText() == null || preferredLastNameField.getText().isEmpty() ? 0 : 1;
        String[] preferredName = new String[1 + preferredMiddleNames.length + isPreferredLastName];
        preferredName[0] = preferredFirstName;
        System.arraycopy(preferredMiddleNames, 0, preferredName, 1, preferredMiddleNames.length);
        if (isLastName == 1) {
            preferredName[preferredName.length - 1] = preferredLastName;
        }


        double userHeight = -1;
        if (!heightField.getText().equals("")) {
            try {
                userHeight = Double.parseDouble(heightField.getText());
                currentUser.setHeight(userHeight);
            } catch (NumberFormatException e) {
                WindowManager.createAlert(AlertType.ERROR, "Error", "Error with the Height Input ", "Please input a valid height input.").show();
                return false;
            }
        }

        double userWeight = -1;
        if (!weightField.getText().equals("")) {
            try {
                userWeight = Double.parseDouble(weightField.getText());
                currentUser.setWeight(userWeight);
            } catch (NumberFormatException e) {
                WindowManager.createAlert(AlertType.ERROR, "Error", "Error with the Weight Input ", "Please input a valid weight input.").show();
                return false;
            }
        }

        String userBloodPressure = "";
        String bloodPressure = bloodPressureTextField.getText();
        if (bloodPressure != null && !bloodPressure.equals("")) {
            String[] bloodPressureList = bloodPressureTextField.getText().split("/");
            if (bloodPressureList.length != 2) {
                WindowManager.createAlert(AlertType.ERROR, "Error", "Error with the Blood Pressure Input ", "Please input a valid blood pressure " +
                        "input.").show();
                return false;
            } else {
                for (String pressureComponent : bloodPressureList) {
                    try {
                        Integer.parseInt(pressureComponent);
                    } catch (NumberFormatException e) {
                        WindowManager.createAlert(AlertType.ERROR, "Error", "Error with the Blood Pressure Input ", "Please input a valid blood " +
                                "pressure input.").show();
                        return false;
                    }
                }
                userBloodPressure = bloodPressureTextField.getText();
            }
        }

        LocalDate currentDate = LocalDate.now();
        if (dateOfBirthPicker.getValue().isAfter(currentDate)) {
            WindowManager.createAlert(AlertType.ERROR, "Error", "Error with the Date Input ", "The date of birth cannot be after today.").show();
            return false;
        } else if (dateOfDeathPicker.getValue() != null && dateOfDeathPicker.getValue().isAfter(currentDate)) {
            WindowManager.createAlert(AlertType.ERROR, "Error", "Error with the Date Input ", "The date of death cannot be after today.").show();
            return false;
        } else if (dateOfDeathPicker.getValue() != null && dateOfBirthPicker.getValue().isAfter(dateOfDeathPicker.getValue())) {
            WindowManager.createAlert(AlertType.ERROR, "Error", "Error with the Date Input ", "The date of birth cannot be after the date of death" +
                    ".").show();
            return false;
        }

        //Commit changes
        currentUser.setNameArray(name);
        currentUser.setPreferredNameArray(preferredName);
        currentUser.setHeight(userHeight);
        currentUser.setWeight(userWeight);
        currentUser.setBloodPressure(userBloodPressure);
        currentUser.setDateOfBirth(dateOfBirthPicker.getValue());
        currentUser.setDateOfDeath(dateOfDeathPicker.getValue());
        currentUser.setGender(genderComboBox.getValue());
        currentUser.setGenderIdentity(genderIdentityComboBox.getValue());
        currentUser.setBloodType(bloodTypeComboBox.getValue());
        currentUser.setAlcoholConsumption(alcoholConsumptionComboBox.getValue());
        currentUser.setSmokerStatus(smokerStatusComboBox.getValue());
        currentUser.setRegion(regionField.getText());
        currentUser.setCurrentAddress(addressField.getText());
        for (Organ key : organTickBoxes.keySet()) {
            if (currentUser.getOrgans().contains(key)) {
                if (!organTickBoxes.get(key).isSelected()) {
                    currentUser.getOrgans().remove(key);
                }
            } else {
                if (organTickBoxes.get(key).isSelected()) {
                    currentUser.getOrgans().add(key);
                }
            }
        }
        settingAttributesLabel.setText("Attributes for " + currentUser.getPreferredName());
        userDisplayText.setText("Currently logged in as: " + currentUser.getPreferredName());
        System.out.println(currentUser.toString());
        return true;
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
        if (result.get() == ButtonType.OK && updateUser()) {
            medicationsController.updateUser();
            diseasesController.updateUser();
            proceduresController.updateUser();
            IO.saveUsers(IO.getUserPath(), LoginType.USER);
            populateUserFields();
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
        if (attributesGridPane.isVisible()) {
            attributeFieldUnfocused();
            //Add the current fields to the redo stack
            attributeRedoStack.add(new User(currentUser));
            //Copy the attribute information from the top element of the undo stack
            currentUser.copyFieldsFrom(attributeUndoStack.getLast());
            //Remove the top element of the undo stack
            attributeUndoStack.removeLast();
            populateUserFields();

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
            medicationsController.updateMedications();
        } else if (waitingListPane.isVisible()) {
            waitingListRedoStack.add(new User(currentUser));
            currentUser.copyWaitingListsFrom(waitingListUndoStack.get(waitingListUndoStack.size() - 1));
            waitingListUndoStack.remove(waitingListUndoStack.size() - 1);
            setUndoRedoButtonsDisabled(waitingListUndoStack.isEmpty(), false);
            waitingListController.populateWaitingList();
        } else if (medicalHistoryProceduresPane.isVisible()) {
            //Add the current procedures lists to the redo stack
            procedureRedoStack.add(new User(currentUser));
            //Copy the proceudres lists from the top element of the undo stack
            currentUser.copyProceduresListsFrom(procedureUndoStack.get(procedureUndoStack.size() - 1));
            //Remove the top element of the undo stack
            procedureUndoStack.remove(procedureUndoStack.size() - 1);

            setUndoRedoButtonsDisabled(procedureUndoStack.isEmpty(), false);
            proceduresController.updateProcedures();
        } else if (medicalHistoryDiseasesPane.isVisible()) {
            //Add the current disease lists to the redo stack
            diseaseRedoStack.add(new User(currentUser));
            //Copy the disease lists from the top element of the undo stack
            currentUser.copyDiseaseListsFrom(diseaseUndoStack.get(diseaseUndoStack.size() - 1));
            //Remove the top element of the undo stack
            diseaseUndoStack.remove(diseaseUndoStack.size() - 1);

            setUndoRedoButtonsDisabled(diseaseUndoStack.isEmpty(), false);
            diseasesController.updateDiseases();
        }
        statusIndicator.setStatus("Undid last action", false);
        titleBar.saved(false);
    }

    /**
     * Called when the redo button is pushed, and reverts the last undo performed by the user.
     * Then checks to see if there are any other actions that can be redone and adjusts the buttons accordingly.
     */
    public void redo() {
        if (attributesGridPane.isVisible()) {
            attributeFieldUnfocused();
            //Add the current fields to the undo stack
            attributeUndoStack.add(new User(currentUser));
            //Copy the attribute information from the top element of the redo stack
            currentUser.copyFieldsFrom(attributeRedoStack.getLast());
            //Remove the top element of the redo stack
            attributeRedoStack.removeLast();
            populateUserFields();

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
            medicationsController.updateMedications();
        } else if (waitingListPane.isVisible()) {
            waitingListUndoStack.add(new User(currentUser));
            currentUser.copyWaitingListsFrom(waitingListRedoStack.get(waitingListRedoStack.size() - 1));
            waitingListRedoStack.remove(waitingListRedoStack.size() - 1);
            setUndoRedoButtonsDisabled(false, waitingListRedoStack.isEmpty());
            waitingListController.populateWaitingList();
        } else if (medicalHistoryProceduresPane.isVisible()) {
            //Add the current procedures lists to the redo stack
            procedureUndoStack.add(new User(currentUser));
            //Copy the proceudres lists from the top element of the undo stack
            currentUser.copyProceduresListsFrom(procedureRedoStack.get(procedureRedoStack.size() - 1));
            //Remove the top element of the undo stack
            procedureRedoStack.remove(procedureRedoStack.size() - 1);

            setUndoRedoButtonsDisabled(false, procedureRedoStack.isEmpty());
            proceduresController.updateProcedures();
        } else if (medicalHistoryDiseasesPane.isVisible()) {
            //Add the current disease lists to the redo stack
            diseaseUndoStack.add(new User(currentUser));
            //Copy the disease lists from the top element of the undo stack
            currentUser.copyDiseaseListsFrom(diseaseRedoStack.get(diseaseRedoStack.size() - 1));
            //Remove the top element of the undo stack
            diseaseRedoStack.remove(diseaseRedoStack.size() - 1);

            setUndoRedoButtonsDisabled(false, medicationRedoStack.isEmpty());
            diseasesController.updateDiseases();
        }
        statusIndicator.setStatus("Redid last action", false);
        titleBar.saved(false);
    }

    /**
     * Updates the age label for the user window based on the date of birth and death of the user.
     */
    public void updateAge() {
        LocalDate dobirthPick = dateOfBirthPicker.getValue();
        LocalDate dodeathPick = dateOfDeathPicker.getValue();

        if (dodeathPick == null) {
            LocalDate today = LocalDate.now();
            double years = Duration.between(dobirthPick.atStartOfDay(), today.atStartOfDay()).toDays() / 365.00;
            if (years < 0) {
                ageLabel.setText("Age: Invalid Input.");
            } else {
                ageLabel.setText("Age: " + String.format("%.1f", years) + " years");
            }
        } else {
            double years = Duration.between(dobirthPick.atStartOfDay(), dodeathPick.atStartOfDay()).toDays() / 365.00;
            if (years < 0) {
                ageLabel.setText("Age: Invalid Input.");
            } else {
                ageLabel.setText("Age: " + String.format("%.1f", years) + " years (At Death)");
            }
        }
    }

    /**
     * Updates the BMI label for the user based on the height and weight fields inputted.
     */
    private void updateBMI() {
        if (!heightField.getText().isEmpty() && !weightField.getText().isEmpty()) {
            try {
                double height = Double.parseDouble(heightField.getText());
                double weight = Double.parseDouble(weightField.getText());
                double BMI = (weight / Math.pow(height, 2)) * 10000;
                bmiLabel.setText("BMI: " + String.format("%.2f", BMI));
            } catch (NumberFormatException e) {
                bmiLabel.setText("");
            }
        } else {
            bmiLabel.setText("");
        }
    }

    /**
     * Updates the validity label of the blood pressure based on the input from the user.
     */
    private void updateBloodPressure() {
        String[] pressureList = bloodPressureTextField.getText().split("/");
        if (!bloodPressureTextField.getText().isEmpty()) {
            if (pressureList.length == 2) {
                try {
                    Integer.parseInt(pressureList[0]);
                    Integer.parseInt(pressureList[1]);
                    bloodPressureLabel.setText("");
                } catch (NumberFormatException e) {
                    bloodPressureLabel.setText("Invalid input");
                }
            } else {
                bloodPressureLabel.setText("Invalid input");
            }
        }
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
        waitingListController.setControlsShown(shown);
        proceduresController.setControlsShown(shown);
        diseasesController.setControlsShown(shown);
        medicationsController.setControlsShown(shown);

        if (currentUser != null) {
            if (currentUser.isReceiver()) {
                waitingListButton.setVisible(true);
            }
        } else {
            waitingListButton.setVisible(shown);
        }
    }
}