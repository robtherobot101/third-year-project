package seng302.Controllers;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import seng302.Core.*;
import seng302.Files.History;
import seng302.TUI.CommandLineInterface;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class UserWindowController implements Initializable {

    private Donor currentDonor;

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
    private TreeTableColumn<String, String> historyDateTimeColumn;
    @FXML
    private TreeTableColumn<String, String> historyActionColumn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setUserWindowController(this);
        welcomePane.setVisible(true);
        attributesGridPane.setVisible(false);
        historyGridPane.setVisible(false);
        medicationsPane.setVisible(false);
    }

    public void showHistoryPane() {
        welcomePane.setVisible(false);
        attributesGridPane.setVisible(false);
        historyGridPane.setVisible(true);
        medicationsPane.setVisible(false);

    }

    public void showMedicationsPane() {
        welcomePane.setVisible(false);
        attributesGridPane.setVisible(false);
        historyGridPane.setVisible(false);
        medicationsPane.setVisible(true);
    }

    public void showAttributesPane() {
        welcomePane.setVisible(false);
        attributesGridPane.setVisible(true);
        historyGridPane.setVisible(false);
        medicationsPane.setVisible(false);
    }

    public void populateHistoryTable() {
        userHistoryLabel.setText("History of actions for " + currentDonor.getName());

        ArrayList<TreeItem<String>> treeItems = new ArrayList<TreeItem<String>>();

        String[][] userHistory = History.getUserHistory(currentDonor.getId());
        for(int i = 0; i < userHistory.length; i++) {

//            for(int j = 0; j < userHistory[i].length; j++) {
//                System.out.println(userHistory[i][j]);
//            }
            System.out.println(userHistory[i][1]);

            try{
                final TreeItem<String> childNode = new TreeItem<>(userHistory[i][1]);
                treeItems.add(childNode);
            } catch(Exception e) {
                e.printStackTrace();
            }

        }

        final TreeItem<String> root = new TreeItem<>("User History");
        root.setExpanded(true);

        root.getChildren().setAll(treeItems);


        //Defining cell content
        historyDateTimeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<String, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue()));

        //Creating a tree table view
        historyTreeTableView.setRoot(root);
        historyTreeTableView.setShowRoot(true);


    }

    public void populateDonorFields() {


        //TODO THEERE IS A PROBLEM WITH THE NAMES

        settingAttributesLabel.setText("Attributes for " + currentDonor.getName());
        String[] splitNames = currentDonor.getNameArray();
        firstNameField.setText(splitNames[0]);
        if (splitNames.length > 2) {
            String[] middleName = new String[splitNames.length-2];
            System.arraycopy(splitNames, 1, middleName, 0, splitNames.length-2);
            middleNameField.setText(String.join(",", middleName));
            lastNameField.setText(splitNames[splitNames.length-1]);
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

        if(currentDonor.getSmokerStatus() != null) {
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

        if(currentDonor.getAlcoholConsumption() != null) {
            alcoholConsumptionComboBox.setValue(currentDonor.getAlcoholConsumption().toString());
        } else {
            alcoholConsumptionComboBox.setValue("Alcohol Consumption");
        }


        if(currentDonor.getGender() != null) {
            System.out.println(currentDonor.getGender());
            String firstLetter = currentDonor.getGender().toString().substring(0, 1);
            String restOfWord = currentDonor.getGender().toString().substring(1);
            genderComboBox.setValue(firstLetter.toUpperCase() + restOfWord);
        } else {
            genderComboBox.setValue("Gender");
        }

        if(currentDonor.getBloodType() != null) {
            bloodTypeComboBox.setValue(currentDonor.getBloodType().toString());
        } else {
            bloodTypeComboBox.setValue("Blood Type");
        }

        EnumSet<Organ> donorOrgans = currentDonor.getOrgans();
        System.out.println(donorOrgans.toString());
        if(donorOrgans.contains(Organ.LIVER)) {
            liverCheckBox.setSelected(true);
        } else {
            liverCheckBox.setSelected(false);
        }
        if(donorOrgans.contains(Organ.KIDNEY)) {
            kidneyCheckBox.setSelected(true);
        } else {
            kidneyCheckBox.setSelected(false);
        }
        if(donorOrgans.contains(Organ.PANCREAS)) {
            pancreasCheckBox.setSelected(true);
        } else {
            pancreasCheckBox.setSelected(false);
        }
        if(donorOrgans.contains(Organ.HEART)) {
            heartCheckBox.setSelected(true);
        } else {
            heartCheckBox.setSelected(false);
        }
        if(donorOrgans.contains(Organ.LUNG)) {
            lungCheckBox.setSelected(true);
        } else {
            lungCheckBox.setSelected(false);
        }
        if(donorOrgans.contains(Organ.INTESTINE)) {
            intestineCheckBox.setSelected(true);
        } else {
            intestineCheckBox.setSelected(false);
        }
        if(donorOrgans.contains(Organ.CORNEA)) {
            corneaCheckBox.setSelected(true);
        } else {
            corneaCheckBox.setSelected(false);
        }
        if(donorOrgans.contains(Organ.EAR)) {
            middleEarCheckBox.setSelected(true);
        } else {
            middleEarCheckBox.setSelected(false);
        }
        if(donorOrgans.contains(Organ.SKIN)) {
            skinCheckBox.setSelected(true);
        } else {
            skinCheckBox.setSelected(false);
        }
        if(donorOrgans.contains(Organ.BONE)) {
            boneMarrowCheckBox.setSelected(true);
        } else {
            boneMarrowCheckBox.setSelected(false);
        }
        if(donorOrgans.contains(Organ.TISSUE)) {
            connectiveTissueCheckBox.setSelected(true);
        } else {
            connectiveTissueCheckBox.setSelected(false);
        }
        if(currentDonor.getWeight() != -1) {
            weightField.setText(Double.toString(currentDonor.getWeight()));
        } else {
            weightField.setText("");
        }

        if(currentDonor.getHeight() != -1) {
            heightField.setText(Double.toString(currentDonor.getHeight()));

        } else {
            heightField.setText("");

        }

        updateBMI();

    }

    public void updateDonor() {

        //TODO
        //Problem with middle names
        String firstName = firstNameField.getText();
        String[] middleNames = middleNameField.getText().isEmpty() ? new String[]{} : middleNameField.getText().split(",");
        String lastName = lastNameField.getText();
        //currentDonor.set(firstNameField.getText() + " " + middleNamesStr + lastNameField.getText());

        int isLastName = lastNameField.getText() == null || lastNameField.getText().isEmpty() ? 0 : 1;
        String[] name = new String[1 + middleNames.length + isLastName];
        name[0] = firstName;
        System.arraycopy(middleNames, 0, name, 1, middleNames.length);
        if (isLastName == 1) {
            name[name.length-1] = lastName;
        }
        String result = "";
        for(String donorName: name) {
            result += donorName;
            result += ",";
        }
        currentDonor.setName(result.substring(0, result.length()));

        Gender donorGender = null;
        try {
            String genderPick = (String) genderComboBox.getValue();
            if (genderPick.equals("Male")) {
                donorGender = Gender.MALE;
            } else if (genderPick.equals("Female")) {
                donorGender = Gender.FEMALE;
            } else if (genderPick.equals("Other")) {
                donorGender = Gender.OTHER;
            } else {
                donorGender = null;
            }
        } catch(Exception e) {

        }

        SmokerStatus smokerStatus;
        String smokerPick = (String) smokerStatusComboBox.getValue();
        if (smokerPick.equals("Never")) {
            smokerStatus = SmokerStatus.NEVER;
        } else if(smokerPick.equals("Current")) {
            smokerStatus = SmokerStatus.CURRENT;
        } else if(smokerPick.equals("Past")) {
            smokerStatus = SmokerStatus.PAST;
        } else {
            smokerStatus = null;
        }

        AlcoholConsumption alcoholConsumption;
        String alcoholPick = (String) alcoholConsumptionComboBox.getValue();
        if (alcoholPick.equals("None")) {
            alcoholConsumption = AlcoholConsumption.NONE;
        } else if(alcoholPick.equals("Low")) {
            alcoholConsumption = AlcoholConsumption.LOW;
        } else if(alcoholPick.equals("Average")) {
            alcoholConsumption = AlcoholConsumption.AVERAGE;
        } else if(alcoholPick.equals("High")) {
            alcoholConsumption = AlcoholConsumption.HIGH;
        } else if(alcoholPick.equals("Very High")) {
            alcoholConsumption = AlcoholConsumption.VERYHIGH;
        } else if(alcoholPick.equals("Alcoholic")) {
            alcoholConsumption = AlcoholConsumption.ALCOHOLIC;
        } else {
            alcoholConsumption = null;
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
        if(!heightField.getText().equals("")) {
            try{
                donorHeight = Double.parseDouble(heightField.getText());
                currentDonor.setHeight(donorHeight);
            } catch(Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error with the Height Input ");
                alert.setContentText("Please input a valid height input.");
                alert.show();
                return;
            }
        }


        double donorWeight = -1;
        try{
            donorWeight = Double.parseDouble(weightField.getText());
            currentDonor.setWeight(donorWeight);
        } catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error with the Weight Input ");
            alert.setContentText("Please input a valid weight input.");
            alert.show();
            return;
        }


        try {
            currentDonor.setDateOfBirth(dateOfBirthPicker.getValue());
            currentDonor.setDateOfDeath(dateOfDeathPicker.getValue());
            currentDonor.setGender(donorGender);
            currentDonor.setBloodType(donorBloodType);
            currentDonor.setRegion(regionField.getText());
            currentDonor.setCurrentAddress(addressField.getText());
            currentDonor.setSmokerStatus(smokerStatus);
            currentDonor.setAlcoholConsumption(alcoholConsumption);
            currentDonor.setBloodPressure(bloodPressureTextField.getText());


            if(liverCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.LIVER);

            }
            if(!liverCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.LIVER);
            }
            if(kidneyCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.KIDNEY);
            }
            if(!kidneyCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.KIDNEY);
            }
            if(pancreasCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.PANCREAS);
            }
            if(!pancreasCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.PANCREAS);
            }
            if(heartCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.HEART);
            }
            if(!heartCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.HEART);
            }
            if(lungCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.LUNG);
            }
            if(!lungCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.LUNG);
            }
            if(intestineCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.INTESTINE);
            }
            if(!intestineCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.INTESTINE);
            }
            if(corneaCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.CORNEA);
            }
            if(!corneaCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.CORNEA);
            }
            if(middleEarCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.EAR);
            }
            if(!middleEarCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.EAR);
            }
            if(skinCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.SKIN);
            }
            if(!skinCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.SKIN);
            }
            if(boneMarrowCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.BONE);
            }
            if(!boneMarrowCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.BONE);
            }
            if(connectiveTissueCheckBox.isSelected()) {
                currentDonor.setOrgan(Organ.TISSUE);
            }
            if(!connectiveTissueCheckBox.isSelected()) {
                currentDonor.removeOrgan(Organ.TISSUE);
            }


            settingAttributesLabel.setText("Attributes for " + currentDonor.getName());
            userDisplayText.setText("Currently logged in as: " + currentDonor.getName());
            System.out.println(currentDonor.toString());
            //TODO Implement save function, saving to database and updating the old donor.
            Main.saveUsers(Main.getDonorPath(), true);

        } catch(Exception e) {
            System.out.println(e);
        }

    }

    public void save() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("Are you sure would like to update the current donor? ");
        alert.setContentText("By doing so, the donor will be updated with all filled in fields.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            if(Main.getDonorUndoStack().isEmpty()){
                undoButton.setDisable(false);
                undoWelcomeButton.setDisable(false);
            }
            Main.addDonorToUndoStack(currentDonor);
            updateDonor();
            populateDonorFields();
            String text = History.prepareFileStringGUI(currentDonor.getId(), "update");
            History.printToFile(Main.streamOut, text);
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
        System.out.println(currentDonor.getOrgans().toString());
        System.out.println(currentDonor.getCurrentAddress().toString());
        currentDonor = Main.donorUndo(currentDonor);
        System.out.println(currentDonor.getOrgans().toString());
        System.out.println(currentDonor.getCurrentAddress().toString());
        populateDonorFields();
        redoButton.setDisable(false);
        redoWelcomeButton.setDisable(false);
        if(Main.getDonorUndoStack().isEmpty()){
            undoButton.setDisable(true);
            undoWelcomeButton.setDisable(true);
        }
        String text = History.prepareFileStringGUI(currentDonor.getId(), "undo");
        History.printToFile(Main.streamOut, text);

    }

    /**
     * Called when the redo button is pushed, and reverts the last undo performed by the user.
     * Then checks to see if there are any other actions that can be redone and adjusts the buttons accordingly.
     */
    public void redo() {
        currentDonor = Main.donorRedo(currentDonor);
        populateDonorFields();
        undoButton.setDisable(false);
        undoWelcomeButton.setDisable(false);
        if(Main.getDonorRedoStack().isEmpty()){
            redoButton.setDisable(true);
            redoWelcomeButton.setDisable(true);
        }
        String text = History.prepareFileStringGUI(currentDonor.getId(), "redo");
        History.printToFile(Main.streamOut, text);
    }

    public void updateAge() {
        LocalDate dobirthPick = dateOfBirthPicker.getValue();
        LocalDate dodeathPick = dateOfDeathPicker.getValue();

        if(dodeathPick == null) {
            LocalDate today = LocalDate.now();
            long days = Duration.between(dobirthPick.atStartOfDay(), today.atStartOfDay()).toDays();
            double years = days/365.00;
            System.out.println(years);
            String age = String.format("%.1f", years);
            ageLabel.setText("Age: " + age + " years");
        } else {
            long days = Duration.between(dobirthPick.atStartOfDay(), dodeathPick.atStartOfDay()).toDays();
            double years = days/365.00;
            System.out.println(years);
            String age = String.format("%.1f", years);
            ageLabel.setText("Age: " + age + " years (At Death)");
        }
    }

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
            System.out.println("Enter a valid character.");

        }

    }

    public void updateAccountSettings() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("View Account Settings");
        dialog.setHeaderText("In order to view your account settings, \nplease enter your login details.");
        dialog.setContentText("Please enter your password:");

        Optional<String> password = dialog.showAndWait();
        if ((password.isPresent()) && (password.get().equals(currentDonor.getPassword())) ) {
            System.out.println("Authenticated!");
            Main.setScene(TFScene.accountSettings);
            Main.setCurrentDonorForAccountSettings(currentDonor);

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Incorrect");
            alert.setHeaderText("Incorrect password. ");
            alert.setContentText("Please enter the correct password to view account settings");
            alert.show();
        }
    }


    public void stop() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("Are you sure would like to exit the application? ");
        alert.setContentText("Exiting without saving loses your non-saved data.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            System.out.println("Exiting GUI");
            String text = History.prepareFileStringGUI(currentDonor.getId(), "quit");
            History.printToFile(Main.streamOut, text);
            Platform.exit();
        } else {
            alert.close();
        }

    }

    public void tester() {
        CommandLineInterface commandLineInterface = new CommandLineInterface();
        String[] helpString = new String[]{"Andy"};
        commandLineInterface.showHelp(helpString);
    }
}
