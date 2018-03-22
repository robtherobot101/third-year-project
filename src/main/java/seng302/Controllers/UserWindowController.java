package seng302.Controllers;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
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

import java.awt.event.ActionEvent;
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

    private Donor currentDonor;

    public Donor getCurrentDonor() {
        return currentDonor;
    }

    public void setCurrentDonor(Donor currentDonor) {
        this.currentDonor = currentDonor;
        userDisplayText.setText("Currently logged in as: " + currentDonor.getName());
    }

    @FXML
    private Label userDisplayText;

    @FXML
    private GridPane attributesGridPane;
    @FXML
    private GridPane historyGridPane;
    @FXML
    private Pane medicationsPane;
    @FXML
    private Pane welcomePane;

    @FXML
    private TextField firstNameField;
    @FXML
    private Label settingAttributesLabel;
    @FXML
    private TextField middleNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField regionField;
    @FXML
    private DatePicker dateOfBirthPicker;
    @FXML
    private DatePicker dateOfDeathPicker;
    @FXML
    private TextField heightField;
    @FXML
    private TextField weightField;
    @FXML
    private ComboBox genderComboBox;
    @FXML
    private ComboBox bloodTypeComboBox;
    @FXML
    private CheckBox liverCheckBox;
    @FXML
    private CheckBox kidneyCheckBox;
    @FXML
    private CheckBox pancreasCheckBox;
    @FXML
    private CheckBox heartCheckBox;
    @FXML
    private CheckBox lungCheckBox;
    @FXML
    private CheckBox intestineCheckBox;
    @FXML
    private CheckBox corneaCheckBox;
    @FXML
    private CheckBox middleEarCheckBox;
    @FXML
    private CheckBox skinCheckBox;
    @FXML
    private CheckBox boneMarrowCheckBox;
    @FXML
    private CheckBox connectiveTissueCheckBox;
    @FXML
    private Button saveButton;
    @FXML
    private MenuItem undoButton;
    @FXML
    private MenuItem redoButton;
    @FXML
    private Label ageLabel;
    @FXML
    private Label bmiLabel;
    @FXML
    private ComboBox smokerStatusComboBox;
    @FXML
    private TextField bloodPressureTextField;
    @FXML
    private ComboBox alcoholConsumptionComboBox;


    @FXML
    private Button undoWelcomeButton;
    @FXML
    private Button redoWelcomeButton;

    @FXML
    private Label userHistoryLabel;
    @FXML
    private TreeTableView<String> historyTreeTableView;
    @FXML
    private TreeTableColumn<String, String> dateTimeColumn;
    @FXML
    private TreeTableColumn<String, String> actionColumn;
    @FXML
    private GridPane background;

    private ArrayList<Donor> donorUndoStack = new ArrayList<>();
    private ArrayList<Donor> donorRedoStack = new ArrayList<>();

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
        if (donorRedoStack != null) {
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

    public ArrayList<Donor> getDonorUndoStack() {
        return donorUndoStack;
    }

    public ArrayList<Donor> getDonorRedoStack() {
        return donorRedoStack;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setUserWindowController(this);
        welcomePane.setVisible(true);
        attributesGridPane.setVisible(false);
        historyGridPane.setVisible(false);
        medicationsPane.setVisible(false);

        Image welcomeImage = new Image("/OrganDonation.jpg");
        BackgroundImage imageBackground = new BackgroundImage(welcomeImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        welcomePane.setBackground(new Background(imageBackground));

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

        heightField.textProperty().addListener((observable, oldValue, newValue) -> updateBMI());
        weightField.textProperty().addListener((observable, oldValue, newValue) -> updateBMI());
        /*
        heightField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.isEmpty()) {
                    double parsed = Double.parseDouble(newValue);
                    if (parsed < 0) {
                        ((StringProperty) observable).setValue(oldValue);
                    }
                }
            } catch (NumberFormatException e) {
                ((StringProperty) observable).setValue(oldValue);
            }
        });*/
    }

    /**
     * Removes focus from all fields.
     */
    public void requestFocus() {
        background.requestFocus();
    }

    public void fieldUnfocused() {
        System.out.println("asd");
        /*
        System.out.println("test");
        undoWelcomeButton.setDisable(false);*/
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

        ArrayList<TreeItem<String>> treeItems = new ArrayList<TreeItem<String>>();

        String[][] userHistory = History.getUserHistory(currentDonor.getId());
        TreeItem<String> sessionNode = new TreeItem<>("Session 1 on " + userHistory[0][0].substring(0, userHistory[0][0].length() - 1));
        TreeItem<String> outerItem1 = new TreeItem<>("Create at " + userHistory[0][1]);
        TreeItem<String> outerItem2 = new TreeItem<>("Login at " + userHistory[0][1]);
        sessionNode.getChildren().add(outerItem1);
        sessionNode.getChildren().add(outerItem2);
        treeItems.add(sessionNode);
        int sessionNumber = 2;
        for (int i = 2; i < userHistory.length; i++) {

            if (userHistory[i][4] == null) {

            } else if (userHistory[i][4].equals("create")) {

            } else {
                if (userHistory[i][4].equals("update")) {
                    TreeItem<String> newItem = new TreeItem<>(userHistory[i][4].substring(0, 1).toUpperCase() + userHistory[i][4].substring(1) + " " +
                            "at " + userHistory[i][1]);
                    sessionNode.getChildren().add(newItem);
                }
                if (userHistory[i][4].equals("undo")) {
                    TreeItem<String> newItem = new TreeItem<>(userHistory[i][4].substring(0, 1).toUpperCase() + userHistory[i][4].substring(1) + " " +
                            "at " + userHistory[i][1]);
                    sessionNode.getChildren().add(newItem);
                }
                if (userHistory[i][4].equals("redo")) {
                    TreeItem<String> newItem = new TreeItem<>(userHistory[i][4].substring(0, 1).toUpperCase() + userHistory[i][4].substring(1) + " " +
                            "at " + userHistory[i][1]);
                    sessionNode.getChildren().add(newItem);
                }
                if (userHistory[i][4].equals("quit")) {
                    TreeItem<String> newItem = new TreeItem<>(userHistory[i][4].substring(0, 1).toUpperCase() + userHistory[i][4].substring(1) + " " +
                            "at " + userHistory[i][1]);
                    sessionNode.getChildren().add(newItem);
                }
                System.out.println(userHistory[i][4]);
                if (userHistory[i][4].equals("updateAccountSettings")) {
                    TreeItem<String> newItem = new TreeItem<>(userHistory[i][4].substring(0, 1).toUpperCase() + userHistory[i][4].substring(1, 6) +
                            " " + userHistory[i][4].substring(6, 13) + " at " + userHistory[i][1]);
                    sessionNode.getChildren().add(newItem);
                }
                if (userHistory[i][4].equals("login")) {

                    sessionNode = new TreeItem<>("Session " + sessionNumber + " on " + userHistory[i][0].substring(0, userHistory[i][0].length() -
                            1));
                    treeItems.add(sessionNode);
                    TreeItem<String> newItem = new TreeItem<>("Login at " + userHistory[i][1]);
                    sessionNode.getChildren().add(newItem);
                    sessionNumber++;
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
            String userName = currentDonor.getName();
            if (param.getValue().getValue().substring(0, 6).equals("Create")) {
                String creationString = "Created a new user profile with name " + userName;
                return new ReadOnlyStringWrapper(creationString);
            }
            if (param.getValue().getValue().substring(0, 5).equals("Login")) {
                return new ReadOnlyStringWrapper("User with id: " + userHistory[0][3] + " logged in successfully.");
            }
            if (param.getValue().getValue().substring(0, 6).equals("Update")) {
                return new ReadOnlyStringWrapper("Updated user attributes for user " + userName);
            }
            if (param.getValue().getValue().substring(0, 4).equals("Undo")) {
                return new ReadOnlyStringWrapper("Reversed last action.");
            }
            if (param.getValue().getValue().substring(0, 4).equals("Redo")) {
                return new ReadOnlyStringWrapper("Reversed last undo.");
            }
            if (param.getValue().getValue().substring(0, 4).equals("Quit")) {
                return new ReadOnlyStringWrapper("Quit the application.");
            }
            if (param.getValue().getValue().substring(0, 12).equals("Update Account")) {
                return new ReadOnlyStringWrapper("Updated account settings for user " + userName);
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

        ObservableList<String> genders =
                FXCollections.observableArrayList(
                        "Male",
                        "Female",
                        "Other"
                );
        genderComboBox.setItems(genders);

        ObservableList<String> bloodTypes =
                FXCollections.observableArrayList(
                        "A-",
                        "A+",
                        "B-",
                        "B+",
                        "AB-",
                        "AB+",
                        "O-",
                        "O+"
                );
        bloodTypeComboBox.setItems(bloodTypes);

        ObservableList<String> smokerStatuses =
                FXCollections.observableArrayList(
                        "Current",
                        "Past",
                        "Never"
                );
        smokerStatusComboBox.setItems(smokerStatuses);

        if (currentDonor.getSmokerStatus() != null) {
            smokerStatusComboBox.setValue(currentDonor.getSmokerStatus().toString());
        } else {
            smokerStatusComboBox.setValue("Smoker Status");
        }

        ObservableList<String> alcoholConsumptions =
                FXCollections.observableArrayList(
                        "None",
                        "Low",
                        "Average",
                        "High",
                        "Very High",
                        "Alcoholic"
                );
        alcoholConsumptionComboBox.setItems(alcoholConsumptions);

        if (currentDonor.getAlcoholConsumption() != null) {
            alcoholConsumptionComboBox.setValue(currentDonor.getAlcoholConsumption().toString());
        } else {
            alcoholConsumptionComboBox.setValue("Alcohol Consumption");
        }


        if (currentDonor.getGender() != null) {
            System.out.println(currentDonor.getGender());
            String firstLetter = currentDonor.getGender().toString().substring(0, 1);
            String restOfWord = currentDonor.getGender().toString().substring(1);
            genderComboBox.setValue(firstLetter.toUpperCase() + restOfWord);
        } else {
            genderComboBox.setValue("Gender");
        }

        if (currentDonor.getBloodType() != null) {
            bloodTypeComboBox.setValue(currentDonor.getBloodType().toString());
        } else {
            bloodTypeComboBox.setValue("Blood Type");
        }

        EnumSet<Organ> donorOrgans = currentDonor.getOrgans();
        System.out.println(donorOrgans.toString());
        if (donorOrgans.contains(Organ.LIVER)) {
            liverCheckBox.setSelected(true);
        } else {
            liverCheckBox.setSelected(false);
        }
        if (donorOrgans.contains(Organ.KIDNEY)) {
            kidneyCheckBox.setSelected(true);
        } else {
            kidneyCheckBox.setSelected(false);
        }
        if (donorOrgans.contains(Organ.PANCREAS)) {
            pancreasCheckBox.setSelected(true);
        } else {
            pancreasCheckBox.setSelected(false);
        }
        if (donorOrgans.contains(Organ.HEART)) {
            heartCheckBox.setSelected(true);
        } else {
            heartCheckBox.setSelected(false);
        }
        if (donorOrgans.contains(Organ.LUNG)) {
            lungCheckBox.setSelected(true);
        } else {
            lungCheckBox.setSelected(false);
        }
        if (donorOrgans.contains(Organ.INTESTINE)) {
            intestineCheckBox.setSelected(true);
        } else {
            intestineCheckBox.setSelected(false);
        }
        if (donorOrgans.contains(Organ.CORNEA)) {
            corneaCheckBox.setSelected(true);
        } else {
            corneaCheckBox.setSelected(false);
        }
        if (donorOrgans.contains(Organ.EAR)) {
            middleEarCheckBox.setSelected(true);
        } else {
            middleEarCheckBox.setSelected(false);
        }
        if (donorOrgans.contains(Organ.SKIN)) {
            skinCheckBox.setSelected(true);
        } else {
            skinCheckBox.setSelected(false);
        }
        if (donorOrgans.contains(Organ.BONE)) {
            boneMarrowCheckBox.setSelected(true);
        } else {
            boneMarrowCheckBox.setSelected(false);
        }
        if (donorOrgans.contains(Organ.TISSUE)) {
            connectiveTissueCheckBox.setSelected(true);
        } else {
            connectiveTissueCheckBox.setSelected(false);
        }
        if (currentDonor.getWeight() != -1) {
            weightField.setText(Double.toString(currentDonor.getWeight()));
        } else {
            weightField.setText("");
        }

        if (currentDonor.getHeight() != -1) {
            heightField.setText(Double.toString(currentDonor.getHeight()));

        } else {
            heightField.setText("");

        }

        updateBMI();

    }

    /**
     * Function which takes all the inputs of the user attributes window.
     * Checks if all these inputs are valid and then sets the user's attributes to those inputted.
     */
    public void updateDonor() {

        String firstName = firstNameField.getText();
        String[] middleNames = middleNameField.getText().isEmpty() ? new String[]{} : middleNameField.getText().split(",");
        String lastName = lastNameField.getText();
        //currentDonor.set(firstNameField.getText() + " " + middleNamesStr + lastNameField.getText());

        int isLastName = lastNameField.getText() == null || lastNameField.getText().isEmpty() ? 0 : 1;
        String[] name = new String[1 + middleNames.length + isLastName];
        name[0] = firstName;
        System.arraycopy(middleNames, 0, name, 1, middleNames.length);
        if (isLastName == 1) {
            name[name.length - 1] = lastName;
        }
        String result = "";
        for (String donorName : name) {
            result += donorName;
            result += ",";
        }
        currentDonor.setName(result.substring(0, result.length()));

        Gender donorGender = null;
        try {
            String genderPick = (String) genderComboBox.getValue();
            switch (genderPick) {
                case "Male":
                    donorGender = Gender.MALE;
                    break;
                case "Female":
                    donorGender = Gender.FEMALE;
                    break;
                case "Other":
                    donorGender = Gender.OTHER;
                    break;
                default:
                    donorGender = null;
                    break;
            }
        } catch (Exception e) {

        }

        SmokerStatus smokerStatus;
        String smokerPick = (String) smokerStatusComboBox.getValue();
        switch (smokerPick) {
            case "Never":
                smokerStatus = SmokerStatus.NEVER;
                break;
            case "Current":
                smokerStatus = SmokerStatus.CURRENT;
                break;
            case "Past":
                smokerStatus = SmokerStatus.PAST;
                break;
            default:
                smokerStatus = null;
                break;
        }

        AlcoholConsumption alcoholConsumption;
        String alcoholPick = (String) alcoholConsumptionComboBox.getValue();
        switch (alcoholPick) {
            case "None":
                alcoholConsumption = AlcoholConsumption.NONE;
                break;
            case "Low":
                alcoholConsumption = AlcoholConsumption.LOW;
                break;
            case "Average":
                alcoholConsumption = AlcoholConsumption.AVERAGE;
                break;
            case "High":
                alcoholConsumption = AlcoholConsumption.HIGH;
                break;
            case "Very High":
                alcoholConsumption = AlcoholConsumption.VERYHIGH;
                break;
            case "Alcoholic":
                alcoholConsumption = AlcoholConsumption.ALCOHOLIC;
                break;
            default:
                alcoholConsumption = null;
                break;
        }

        BloodType donorBloodType = null;
        try {
            String bloodTypePick = (String) bloodTypeComboBox.getValue();

            switch (bloodTypePick) {
                case "A-":
                    donorBloodType = BloodType.A_NEG;
                    break;
                case "A+":
                    donorBloodType = BloodType.A_POS;
                    break;
                case "B-":
                    donorBloodType = BloodType.B_NEG;
                    break;
                case "B+":
                    donorBloodType = BloodType.B_POS;
                    break;
                case "AB-":
                    donorBloodType = BloodType.AB_NEG;
                    break;
                case "AB+":
                    donorBloodType = BloodType.AB_POS;
                    break;
                case "O-":
                    donorBloodType = BloodType.O_NEG;
                    break;
                case "O+":
                    donorBloodType = BloodType.O_POS;
                    break;
                case "Blood Type":
                    donorBloodType = null;
                    break;
            }
        } catch (Exception e) {
            System.out.println("Input a blood type.");
            donorBloodType = null;
        }

        double donorHeight = -1;
        if (!heightField.getText().equals("")) {
            try {
                donorHeight = Double.parseDouble(heightField.getText());
                currentDonor.setHeight(donorHeight);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error with the Height Input ");
                alert.setContentText("Please input a valid height input.");
                alert.show();
                return;
            }
        }
        currentDonor.setHeight(donorHeight);


        double donorWeight = -1;
        if (!weightField.getText().equals("")) {
            try {
                donorWeight = Double.parseDouble(weightField.getText());
                currentDonor.setWeight(donorWeight);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error with the Weight Input ");
                alert.setContentText("Please input a valid weight input.");
                alert.show();
                return;
            }
        }
        currentDonor.setWeight(donorWeight);

        LocalDate currentDate = LocalDate.now();
        System.out.println(currentDate);
        System.out.println(dateOfBirthPicker.getValue());
        if(dateOfBirthPicker.getValue().isAfter(currentDate)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error with the Date Input.");
            alert.setContentText("The date of birth cannot be after today.");
            alert.show();
            return;
        } else if(dateOfDeathPicker.getValue().isAfter(currentDate)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error with the Date Input ");
            alert.setContentText("The date of death cannot be after today.");
            alert.show();
            return;
        } else {
            if(dateOfBirthPicker.getValue().isAfter(dateOfDeathPicker.getValue())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error with the Date Input ");
                alert.setContentText("The date of birth cannot be after the date of death.");
                alert.show();
                return;
            } else {
                currentDonor.setDateOfBirth(dateOfBirthPicker.getValue());
                currentDonor.setDateOfDeath(dateOfDeathPicker.getValue());
            }
        }


        try {

            currentDonor.setGender(donorGender);
            currentDonor.setBloodType(donorBloodType);
            currentDonor.setRegion(regionField.getText());
            currentDonor.setCurrentAddress(addressField.getText());
            currentDonor.setSmokerStatus(smokerStatus);
            currentDonor.setAlcoholConsumption(alcoholConsumption);
            currentDonor.setBloodPressure(bloodPressureTextField.getText());


            if (liverCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.LIVER);

            }
            if (!liverCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.LIVER);
            }
            if (kidneyCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.KIDNEY);
            }
            if (!kidneyCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.KIDNEY);
            }
            if (pancreasCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.PANCREAS);
            }
            if (!pancreasCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.PANCREAS);
            }
            if (heartCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.HEART);
            }
            if (!heartCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.HEART);
            }
            if (lungCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.LUNG);
            }
            if (!lungCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.LUNG);
            }
            if (intestineCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.INTESTINE);
            }
            if (!intestineCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.INTESTINE);
            }
            if (corneaCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.CORNEA);
            }
            if (!corneaCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.CORNEA);
            }
            if (middleEarCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.EAR);
            }
            if (!middleEarCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.EAR);
            }
            if (skinCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.SKIN);
            }
            if (!skinCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.SKIN);
            }
            if (boneMarrowCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.BONE);
            }
            if (!boneMarrowCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.BONE);
            }
            if (connectiveTissueCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.TISSUE);
            }
            if (!connectiveTissueCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.TISSUE);
            }


            settingAttributesLabel.setText("Attributes for " + currentDonor.getName());
            userDisplayText.setText("Currently logged in as: " + currentDonor.getName());
            System.out.println(currentDonor.toString());
            Main.saveUsers(Main.getDonorPath(), true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Saves the current state of the GUI.
     * Gets all the inputs for the user attributes and sets the user attributes to those by calling the update donor function.
     * Then calls the populate donor function to repopulate the donor fields.
     */
    public void save() {
        //changeSinceLastUndoStackPush = false;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("Are you sure would like to update the current donor? ");
        alert.setContentText("By doing so, the donor will be updated with all filled in fields.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (donorUndoStack.isEmpty()) {
                undoButton.setDisable(false);
                undoWelcomeButton.setDisable(false);
            }
            addDonorToUndoStack(currentDonor);
            updateDonor();
            populateDonorFields();
            String text = History.prepareFileStringGUI(currentDonor.getId(), "update");
            History.printToFile(streamOut, text);
            populateHistoryTable();
            alert.close();
        } else {
            alert.close();
        }
    }

    /**
     * Called when the undo button is pushed, and reverts the last action performed by the user.
     * Then checks to see if there are any other actions that can be undone and adjusts the buttons accordingly.
     */
    public void undo() {
        /*
        if (changeSinceLastUndoStackPush) {
            addDonorToUndoStack(currentDonor);
            updateDonor();
            changeSinceLastUndoStackPush = false;
        }*/

        currentDonor = donorUndo(currentDonor);

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
        currentDonor = donorRedo(currentDonor);
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
            long days = Duration.between(dobirthPick.atStartOfDay(), today.atStartOfDay()).toDays();
            double years = days/365.00;
            if(years < 0) {
                ageLabel.setText("Age: Invalid Input.");
            } else {
                String age = String.format("%.1f", years);
                ageLabel.setText("Age: " + age + " years");
            }

        } else {
            long days = Duration.between(dobirthPick.atStartOfDay(), dodeathPick.atStartOfDay()).toDays();
            double years = days/365.00;
            if(years < 0) {
                ageLabel.setText("Age: Invalid Input.");
            } else {
                String age = String.format("%.1f", years);
                ageLabel.setText("Age: " + age + " years (At Death)");
            }

        }
    }

    /**
     * Updates the BMI label for the user based on the height and weight fields inputted.
     */
    public void updateBMI() {
        try {
            if ((heightField.getText().equals("")) || (weightField.getText().equals(""))) {
                System.out.print("Input a character in both fields.");
            } else {
                double height = Double.parseDouble(heightField.getText());
                double weight = Double.parseDouble(weightField.getText());
                double BMI = ((weight) / (Math.pow(height, 2))) * 10000;
                String bmiString = String.format("%.2f", BMI);
                bmiLabel.setText("BMI: " + bmiString);
            }
        } catch(Exception e) {
            bmiLabel.setText("BMI: Invalid Input.");

        }

    }

    /**
     * Updates the validity label of the blood pressure based on the input from the user.
     */
    public void updateBloodPressure() {
        try {
            String userBloodPressure = bloodPressureTextField.getText();

        } catch(Exception e) {
            bloodPressureLabel.setText("Invalid Input.");
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

    /**
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
        alert.setHeaderText("Are you sure would like to exit the application? ");
        alert.setContentText("Exiting without saving loses your non-saved data.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            System.out.println("Exiting GUI");
            String text = History.prepareFileStringGUI(currentDonor.getId(), "quit");
            History.printToFile(streamOut, text);
            Platform.exit();
        } else {
            alert.close();
        }

    }

}
