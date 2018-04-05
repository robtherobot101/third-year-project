package seng302.Controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng302.Core.*;
import seng302.Files.History;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static seng302.Core.Main.streamOut;

/**
 * Class which handles all the logic for the User Window.
 * Handles all functions including:
 * Saving, Undo, Redo, All input fields and more.
 */
public class UserWindowController implements Initializable {
    @FXML
    private Label userDisplayText, settingAttributesLabel, ageLabel, bmiLabel, bloodPressureLabel, userHistoryLabel;
    @FXML
    private GridPane attributesGridPane, historyGridPane, background;
    @FXML
    private AnchorPane medicationsPane;
    @FXML
    private Pane welcomePane;
    @FXML
    private TextField firstNameField, middleNameField, lastNameField, addressField, regionField, heightField, weightField, bloodPressureTextField;
    @FXML
    private DatePicker dateOfBirthPicker, dateOfDeathPicker;
    @FXML
    private ComboBox<Gender> genderComboBox;
    @FXML
    private ComboBox<BloodType> bloodTypeComboBox;
    @FXML
    private ComboBox<SmokerStatus> smokerStatusComboBox;
    @FXML
    private ComboBox<AlcoholConsumption> alcoholConsumptionComboBox;
    @FXML
    private CheckBox liverCheckBox, kidneyCheckBox, pancreasCheckBox, heartCheckBox, lungCheckBox, intestineCheckBox, corneaCheckBox, middleEarCheckBox, skinCheckBox, boneMarrowCheckBox, connectiveTissueCheckBox;
    @FXML
    private MenuItem undoButton, redoButton;
    @FXML
    private Button logoutButton, undoWelcomeButton, redoWelcomeButton;
    @FXML
    private TreeTableView<String> historyTreeTableView;
    @FXML
    private TreeTableColumn<String, String> dateTimeColumn, actionColumn;

    private HashMap<Organ, CheckBox> organTickBoxes;
    private ArrayList<Donor> donorUndoStack = new ArrayList<>(), donorRedoStack = new ArrayList<>();
    private Donor currentDonor;
    private boolean childWindow = false;
    @FXML
    private Button medicationsButton;
    @FXML
    private Button medicalHistoryButton;

    public ArrayList<Donor> getDonorUndoStack() {
        return donorUndoStack;
    }

    public ArrayList<Donor> getDonorRedoStack() {
        return donorRedoStack;
    }

    public Donor getCurrentDonor() {
        return currentDonor;
    }

    public void setCurrentDonor(Donor currentDonor) {
        this.currentDonor = currentDonor;
        userDisplayText.setText("Currently logged in as: " + currentDonor.getName());
        donorUndoStack.clear();
        donorRedoStack.clear();
        undoButton.setDisable(true);
        undoWelcomeButton.setDisable(true);
        redoButton.setDisable(true);
        redoWelcomeButton.setDisable(true);
        bloodPressureLabel.setText("");
    }

    /**
     * Adds a donor object to the donor undo stack. This is called whenever a user saves any changes in the GUI.
     *
     * @param donor donor object being added to the top of the stack.
     */
    public void addDonorToUndoStack(Donor donor) {
        Donor prevDonor = new Donor(donor);
        donorUndoStack.add(prevDonor);
    }


    /**
     * Called when clicking the undo button. Takes the most recent donor object on the stack and returns it.
     * Then removes it from the undo stack and adds it to the redo stack.
     *
     * @return the most recent saved version of the donor.
     */
    public Donor donorUndo(Donor oldDonor) {
        if (donorUndoStack != null) {
            Donor newDonor = donorUndoStack.get(donorUndoStack.size() - 1);
            donorUndoStack.remove(donorUndoStack.size() - 1);
            donorRedoStack.add(oldDonor);
            if (streamOut != null) {
//                String text = History.prepareFileStringGUI(oldDonor.getId(), "undo");
//                History.printToFile(streamOut, text);
            }
            return newDonor;
        } else {
            System.out.println("Undo somehow being called with nothing to undo.");
            return null;
        }
    }

    /**
     * A reverse of undo. Can only be called if an action has already been undone, and re loads the donor from the redo stack.
     *
     * @return the donor on top of the redo stack.
     */
    public Donor donorRedo(Donor newDonor) {
        if (donorRedoStack != null && donorRedoStack.size() != 0) {
            Donor oldDonor = donorRedoStack.get(donorRedoStack.size() - 1);
            addDonorToUndoStack(newDonor);
            donorRedoStack.remove(donorRedoStack.size() - 1);
            if (streamOut != null) {
//                String text = History.prepareFileStringGUI(oldDonor.getId(), "redo");
//                History.printToFile(streamOut, text);
            }
            return oldDonor;
        } else {
            System.out.println("Redo somehow being called with nothing to redo.");
            return null;
        }
    }

    /**
     * Set up the User window.
     * @param location Not used
     * @param resources Not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setUserWindowController(this);
        welcomePane.setVisible(true);
        attributesGridPane.setVisible(false);
        historyGridPane.setVisible(false);
        medicationsPane.setVisible(false);
        genderComboBox.setItems(FXCollections.observableArrayList(Gender.values()));
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

        Main.medicationsViewForDonor();

        Image welcomeImage = new Image("/OrganDonation.jpg");
        BackgroundImage imageBackground = new BackgroundImage(welcomeImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        welcomePane.setBackground(new Background(imageBackground));

        //Add listeners for attribute undo and redo
        firstNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) fieldUnfocused();
        });
        middleNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) fieldUnfocused();
        });
        lastNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) fieldUnfocused();
        });
        addressField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) fieldUnfocused();
        });
        regionField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) fieldUnfocused();
        });
        dateOfBirthPicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) fieldUnfocused();
        });
        dateOfDeathPicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) fieldUnfocused();
        });
        heightField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) fieldUnfocused();
        });
        weightField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) fieldUnfocused();
        });
        bloodPressureTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) fieldUnfocused();
        });

        //Add listeners to correctly update BMI and blood pressure based on user input
        heightField.textProperty().addListener((observable, oldValue, newValue) -> updateBMI());
        weightField.textProperty().addListener((observable, oldValue, newValue) -> updateBMI());
        bloodPressureTextField.textProperty().addListener((observable, oldValue, newValue) -> updateBloodPressure());
    }

    /**
     * Removes focus from all fields.
     */
    public void requestFocus() {
        background.requestFocus();
    }

    /**
     * Checks for any new updates when a field loses focus, and appends to undostack if there is new changes.
     */
    public void fieldUnfocused() {
        Donor oldFields = new Donor(currentDonor);
        if (updateDonor() && !currentDonor.fieldsEqual(oldFields)) {
            addDonorToUndoStack(oldFields);
            undoButton.setDisable(false);
            undoWelcomeButton.setDisable(false);

            donorRedoStack.clear();
            redoButton.setDisable(true);
            redoWelcomeButton.setDisable(true);
        }
    }

    /**
     * Sets the history pane as the visible pane
     */
    public void showHistoryPane() {
        welcomePane.setVisible(false);
        attributesGridPane.setVisible(false);
        historyGridPane.setVisible(true);
        medicationsPane.setVisible(false);
    }

    /**
     * Sets the medications pane as the visible pane
     */
    public void showMedicationsPane() {
        welcomePane.setVisible(false);
        attributesGridPane.setVisible(false);
        historyGridPane.setVisible(false);
        medicationsPane.setVisible(true);
    }

    /**
     * Sets the Donor Attributes pane as the visible pane
     */
    public void showAttributesPane() {
        welcomePane.setVisible(false);
        attributesGridPane.setVisible(true);
        historyGridPane.setVisible(false);
        medicationsPane.setVisible(false);
    }

    /**
     * Sets the welcome pane as the visible pane
     */
    public void showWelcomePane() {
        welcomePane.setVisible(true);
        attributesGridPane.setVisible(false);
        historyGridPane.setVisible(false);
        medicationsPane.setVisible(false);
    }

    /**
     * Populates the history table based on the action history of the current user.
     * Gets the user history from the History.getUserHistory() function.
     * Sorts these into tree nodes based on new sessions.
     */
    public void populateHistoryTable() {
        userHistoryLabel.setText("History of actions for " + currentDonor.getName());
        String[][] userHistory = History.getUserHistory(currentDonor.getId());

        ArrayList<TreeItem<String>> treeItems = new ArrayList<>();
        TreeItem<String> sessionNode = new TreeItem<>("Session 1 on " + userHistory[0][0].substring(0, userHistory[0][0].length() - 1));
        TreeItem<String> outerItem1 = new TreeItem<>("Create at " + userHistory[0][1]);
        TreeItem<String> outerItem2 = new TreeItem<>("Login at " + userHistory[0][1]);
        sessionNode.getChildren().add(outerItem1);
        sessionNode.getChildren().add(outerItem2);
        treeItems.add(sessionNode);

        int sessionNumber = 2;
        for (int i = 2; i < userHistory.length; i++) {
            if (!(userHistory[i][4] == null) && !(userHistory[i][4].equals("create"))){
                switch (userHistory[i][4]) {
                    case "update":
                    case "undo":
                    case "redo":
                    case "quit":
                        sessionNode.getChildren().add(new TreeItem<>(userHistory[i][4].substring(0, 1).toUpperCase() + userHistory[i][4].substring(1) + " at " + userHistory[i][1]));
                        break;
                    case "updateAccountSettings":
                        sessionNode.getChildren().add(new TreeItem<>(userHistory[i][4].substring(0, 1).toUpperCase() + userHistory[i][4].substring(1, 6) +
                            " " + userHistory[i][4].substring(6, 13) + " at " + userHistory[i][1]));
                        break;
                    case "login":
                        sessionNode = new TreeItem<>("Session " + sessionNumber + " on " + userHistory[i][0].substring(0, userHistory[i][0].length() -
                            1));
                        treeItems.add(sessionNode);
                        sessionNode.getChildren().add(new TreeItem<>("Login at " + userHistory[i][1]));
                        sessionNumber++;
                        break;
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
            String userName = currentDonor.getName(), toCheck = param.getValue().getValue().substring(0, 12);
            if (toCheck.equals("Update Account")) {
                return new ReadOnlyStringWrapper("Updated account settings for user " + userName);
            }
            switch (toCheck.substring(0, 6)) {
                case "Create":
                    return new ReadOnlyStringWrapper("Created a new user profile with name " + userName);
                case "Update":
                    return new ReadOnlyStringWrapper("Updated user attributes for user " + userName);
            }
            if (toCheck.substring(0, 5).equals("Login")) {
                return new ReadOnlyStringWrapper("User with id: " + userHistory[0][3] + " logged in successfully.");
            }
            switch (toCheck.substring(0, 4)) {
                case "Undo":
                    return new ReadOnlyStringWrapper("Reversed last action.");
                case "Redo":
                    return new ReadOnlyStringWrapper("Reversed last undo.");
                case "Quit":
                    return new ReadOnlyStringWrapper("Quit the application.");
            }
            return null;
        });

        //Creating a tree table view
        historyTreeTableView.setRoot(root);
        historyTreeTableView.setShowRoot(true);
    }

    /**
     * Function which takes the current donor object that has logged in and
     * takes all their attributes and populates the donor attributes on the attributes pane accordingly.
     */
    public void populateDonorFields() {
        settingAttributesLabel.setText("Attributes for " + currentDonor.getName());
        String[] splitNames = currentDonor.getNameArray();
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
        addressField.setText(currentDonor.getCurrentAddress());
        regionField.setText(currentDonor.getRegion());

        dateOfBirthPicker.setValue(currentDonor.getDateOfBirth());
        dateOfDeathPicker.setValue(currentDonor.getDateOfDeath());
        updateAge();

        bloodPressureTextField.setText(currentDonor.getBloodPressure());

        genderComboBox.setValue(currentDonor.getGender());
        bloodTypeComboBox.setValue(currentDonor.getBloodType());
        smokerStatusComboBox.setValue(currentDonor.getSmokerStatus());
        alcoholConsumptionComboBox.setValue(currentDonor.getAlcoholConsumption());

        for (Organ key: organTickBoxes.keySet()) {
            organTickBoxes.get(key).setSelected(currentDonor.getOrgans().contains(key));
        }

        weightField.setText(currentDonor.getWeight() == -1 ? "" : Double.toString(currentDonor.getWeight()));
        heightField.setText(currentDonor.getHeight() == -1 ? "" : Double.toString(currentDonor.getHeight()));

        updateBMI();
        updateBloodPressure();
    }

    /**
     * Function which takes all the inputs of the user attributes window.
     * Checks if all these inputs are valid and then sets the user's attributes to those inputted.
     */
    private boolean updateDonor() {
        //Extract names from donor
        String firstName = firstNameField.getText();
        String[] middleNames = middleNameField.getText().isEmpty() ? new String[]{} : middleNameField.getText().split(",");
        String lastName = lastNameField.getText();

        int isLastName = lastNameField.getText() == null || lastNameField.getText().isEmpty() ? 0 : 1;
        String[] name = new String[1 + middleNames.length + isLastName];
        name[0] = firstName;
        System.arraycopy(middleNames, 0, name, 1, middleNames.length);
        if (isLastName == 1) {
            name[name.length - 1] = lastName;
        }

        double donorHeight = -1;
        if (!heightField.getText().equals("")) {
            try {
                donorHeight = Double.parseDouble(heightField.getText());
                currentDonor.setHeight(donorHeight);
            } catch (NumberFormatException e) {
                createErrorAlert("Error with the Height Input ", "Please input a valid height input.");
                return false;
            }
        }

        double donorWeight = -1;
        if (!weightField.getText().equals("")) {
            try {
                donorWeight = Double.parseDouble(weightField.getText());
                currentDonor.setWeight(donorWeight);
            } catch (NumberFormatException e) {
                createErrorAlert("Error with the Weight Input ", "Please input a valid weight input.");
                return false;
            }
        }

        String donorBloodPressure = "";
        String bloodPressure = bloodPressureTextField.getText();
        if (bloodPressure != null && !bloodPressure.equals("")) {
            String[] bloodPressureList = bloodPressureTextField.getText().split("/");
            if (bloodPressureList.length != 2) {
                createErrorAlert("Error with the Blood Pressure Input ", "Please input a valid blood pressure input.");
                return false;
            } else {
                for (String pressureComponent: bloodPressureList) {
                    try {
                        Integer.parseInt(pressureComponent);
                    } catch (NumberFormatException e) {
                        createErrorAlert("Error with the Blood Pressure Input ", "Please input a valid blood pressure input.");
                        return false;
                    }
                }
                donorBloodPressure = bloodPressureTextField.getText();
            }
        }

        LocalDate currentDate = LocalDate.now();
        if (dateOfBirthPicker.getValue().isAfter(currentDate)) {
            createErrorAlert("Error with the Date Input ", "The date of birth cannot be after today.");
            return false;
        } else if(dateOfDeathPicker.getValue() != null && dateOfDeathPicker.getValue().isAfter(currentDate)) {
            createErrorAlert("Error with the Date Input ", "The date of death cannot be after today.");
            return false;
        } else if(dateOfDeathPicker.getValue() != null && dateOfBirthPicker.getValue().isAfter(dateOfDeathPicker.getValue())) {
            createErrorAlert("Error with the Date Input ", "The date of birth cannot be after the date of death.");
            return false;
        }

        //Commit changes
        currentDonor.setNameArray(name);
        currentDonor.setHeight(donorHeight);
        currentDonor.setWeight(donorWeight);
        currentDonor.setBloodPressure(donorBloodPressure);
        currentDonor.setDateOfBirth(dateOfBirthPicker.getValue());
        currentDonor.setDateOfDeath(dateOfDeathPicker.getValue());
        currentDonor.setGender(genderComboBox.getValue());
        currentDonor.setBloodType(bloodTypeComboBox.getValue());
        currentDonor.setAlcoholConsumption(alcoholConsumptionComboBox.getValue());
        currentDonor.setSmokerStatus(smokerStatusComboBox.getValue());
        currentDonor.setRegion(regionField.getText());
        currentDonor.setCurrentAddress(addressField.getText());
        for (Organ key: organTickBoxes.keySet()) {
            if (currentDonor.getOrgans().contains(key)) {
                if (!organTickBoxes.get(key).isSelected()) {
                    currentDonor.getOrgans().remove(key);
                }
            } else {
                if (organTickBoxes.get(key).isSelected()) {
                    currentDonor.getOrgans().add(key);
                }
            }
        }
        settingAttributesLabel.setText("Attributes for " + currentDonor.getName());
        userDisplayText.setText("Currently logged in as: " + currentDonor.getName());
        System.out.println(currentDonor.toString());
        return true;
    }

    private void createErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    /**
     * Saves the current state of the GUI.
     * Gets all the inputs for the user attributes and sets the user attributes to those by calling the update donor function.
     * Then calls the populate donor function to repopulate the donor fields.
     */
    public void save() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("Are you sure would like to update the current donor? ");
        alert.setContentText("By doing so, the donor will be updated with all filled in fields.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK && updateDonor()) {
            Main.saveUsers(Main.getDonorPath(), true);
            populateDonorFields();
            String text = History.prepareFileStringGUI(currentDonor.getId(), "update");
            History.printToFile(streamOut, text);
            populateHistoryTable();
        }
        alert.close();
    }

    /**
     * Called when the undo button is pushed, and reverts the last action performed by the user.
     * Then checks to see if there are any other actions that can be undone and adjusts the buttons accordingly.
     */
    public void undo() {
        fieldUnfocused();
        currentDonor.copyFieldsFrom(donorUndo(new Donor(currentDonor)));
        populateDonorFields();

        redoButton.setDisable(false);
        redoWelcomeButton.setDisable(false);
        if (donorUndoStack.isEmpty()) {
            undoButton.setDisable(true);
            undoWelcomeButton.setDisable(true);
        }
        String text = History.prepareFileStringGUI(currentDonor.getId(), "undo");
        History.printToFile(streamOut, text);
        populateHistoryTable();
    }

    /**
     * Called when the redo button is pushed, and reverts the last undo performed by the user.
     * Then checks to see if there are any other actions that can be redone and adjusts the buttons accordingly.
     */
    public void redo() {
        fieldUnfocused();
        currentDonor.copyFieldsFrom(donorRedo(new Donor(currentDonor)));
        populateDonorFields();

        undoButton.setDisable(false);
        undoWelcomeButton.setDisable(false);
        if (donorRedoStack.isEmpty()) {
            redoButton.setDisable(true);
            redoWelcomeButton.setDisable(true);
        }
        String text = History.prepareFileStringGUI(currentDonor.getId(), "redo");
        History.printToFile(streamOut, text);
        populateHistoryTable();
    }

    /**
     * Updates the age label for the user window based on the date of birth and death of the user.
     */
    public void updateAge() {
        LocalDate dobirthPick = dateOfBirthPicker.getValue();
        LocalDate dodeathPick = dateOfDeathPicker.getValue();

        if (dodeathPick == null) {
            LocalDate today = LocalDate.now();
            double years = Duration.between(dobirthPick.atStartOfDay(), today.atStartOfDay()).toDays()/365.00;
            if(years < 0) {
                ageLabel.setText("Age: Invalid Input.");
            } else {
                ageLabel.setText("Age: " + String.format("%.1f", years) + " years");
            }
        } else {
            double years = Duration.between(dobirthPick.atStartOfDay(), dodeathPick.atStartOfDay()).toDays()/365.00;
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
            } catch(NumberFormatException e) {
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

        Optional<String> password = dialog.showAndWait();
        if(password.isPresent()){ //Ok was pressed, Else cancel
            if(password.get().equals(currentDonor.getPassword())){
                try {
                    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/accountSettings.fxml"));
                    Stage stage = new Stage();
                    stage.setTitle("Account Settings");
                    stage.setScene(new Scene(root, 270, 350));
                    stage.initModality(Modality.APPLICATION_MODAL);
                    Main.setCurrentDonorForAccountSettings(currentDonor);
                    stage.showAndWait();
                } catch (Exception e) {
                    System.out.println("here");
                    e.printStackTrace();
                }
            }else{ // Password incorrect
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Incorrect");
                alert.setHeaderText("Incorrect password. ");
                alert.setContentText("Please enter the correct password to view account settings");
                alert.show();
            }
        }
    }

    public void setAsChildWindow(){
        logoutButton.setDisable(true);
    }
    /*
     * Function which is called when the user wants to logout of the application and log into a new user
     */
    public void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("Are you sure would like to log out? ");
        alert.setContentText("Logging out without saving loses your non-saved data.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.out.println("Exiting GUI");
            String text = History.prepareFileStringGUI(currentDonor.getId(), "quit");
            History.printToFile(streamOut, text);

            Main.setScene(TFScene.login);
        } else {
            alert.close();
        }
    }

    /**
     * Function which is called when the user wants to exit the application.
     */
    public void stop() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("Are you sure would like to exit the window? ");
        alert.setContentText("Exiting without saving loses your non-saved data.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.out.println("Exiting GUI");
            String text = History.prepareFileStringGUI(currentDonor.getId(), "quit");
            History.printToFile(streamOut, text);

            Stage stage = (Stage) welcomePane.getScene().getWindow();
            stage.close();
        } else {
            alert.close();
        }

    }

}
