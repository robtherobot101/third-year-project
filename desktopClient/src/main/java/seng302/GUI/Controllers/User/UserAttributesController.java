package seng302.GUI.Controllers.User;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import org.apache.http.client.HttpResponseException;
import org.controlsfx.control.textfield.TextFields;
import seng302.Generic.Country;
import seng302.Generic.Debugger;
import seng302.Generic.WindowManager;
import seng302.User.Attribute.*;
import seng302.User.User;
import seng302.User.WaitingListItem;
import tornadofx.control.DateTimePicker;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class UserAttributesController extends UserTabController implements Initializable {
    @FXML
    private Label settingAttributesLabel, ageLabel, bmiLabel;
    @FXML
    private TextField firstNameField, middleNameField, lastNameField, addressField, heightField, weightField, bloodPressureTextField, preferredFirstNameField, preferredMiddleNamesField, preferredLastNameField, deathCityField, regionOfDeathField, regionField;
    @FXML
    private DatePicker dateOfBirthPicker;

    @FXML
    private ComboBox countryOfDeathComboBox, countryComboBox;

    @FXML
    private ComboBox<NZRegion> regionComboBox, regionOfDeathComboBox;

    @FXML
    private DateTimePicker dateOfDeathPicker;
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

    private Map<Organ, CheckBox> organTickBoxes;

    boolean deathInNewZealand = false;
    boolean inNewZealand = false;

    /**
     * sets the current user
     * @param user the user to set as the current user
     */
    public void setCurrentUser(User user) {
        currentUser = user;
        populateUserFields();
        updateBMI();
        updateAge();
        setDeathInNewZealand();
        setInNewZealand();
    }


    /**
     * sets the death of a user in New Zealand
     */
    public void setDeathInNewZealand() {
        if(countryOfDeathComboBox.getValue() != null) {
            deathInNewZealand = countryOfDeathComboBox.getValue().toString().equals("New Zealand");
            regionOfDeathComboBox.setVisible(deathInNewZealand);
            regionOfDeathField.setVisible(deathInNewZealand == false);
            if(deathInNewZealand){
                regionOfDeathField.setText("");
            }
        }
    }

    /**
     * sets the users address in New Zealand
     */
    public void setInNewZealand() {
        if(countryComboBox.getValue() != null) {
            inNewZealand = countryComboBox.getValue().toString().equals("New Zealand");
            regionComboBox.setVisible(inNewZealand);
            regionField.setVisible(inNewZealand == false);
            System.out.println(inNewZealand);
            if(inNewZealand){
                regionField.setText("");
            }
        }
    }

    /**
     * changes the country of death
     */
    public void countryOfDeathChanged() {
        setDeathInNewZealand();
        attributeFieldUnfocused();
    }

    /**
     * changes the country of residence
     */
    public void countryChanged() {
        setInNewZealand();
        attributeFieldUnfocused();
    }

    /**
     * Highlights the checkboxes in red if the user is also waiting to receive an organ of that type.
     * A tooltip is also added to highlighted checkboxes which tells the user what the problem is
     */
    public void highlightOrganCheckBoxes(){
        for(Organ organ: Organ.values()){
            if(organTickBoxes.get(organ).getStyleClass().contains("highlighted-checkbox")){
                organTickBoxes.get(organ).getStyleClass().remove("highlighted-checkbox");
                organTickBoxes.get(organ).setTooltip(null);
            }
            for(WaitingListItem item: currentUser.getWaitingListItems()){
                if(!organTickBoxes.get(organ).getStyleClass().contains("highlighted-checkbox") && item.getOrganType() == organ && organTickBoxes.get(organ).isSelected() && item.getStillWaitingOn()){
                    organTickBoxes.get(organ).getStyleClass().add("highlighted-checkbox");
                    organTickBoxes.get(organ).setTooltip(new Tooltip("User is waiting to receive this organ"));
                }
            }
        }
    }


    /**
     * Updates the age label for the user window based on the date of birth and death of the user.
     */
    public void updateAge() {
        LocalDate dobirthPick = dateOfBirthPicker.getValue();
        LocalDateTime dodeathPick = dateOfDeathPicker.getDateTimeValue();

        if (dodeathPick == null) {
            LocalDateTime today = LocalDateTime.now();
            double years = Duration.between(dobirthPick.atStartOfDay(), today).toDays() / 365.00;
            if (years < 0) {
                ageLabel.setText("Age: Invalid Input.");
            } else {
                ageLabel.setText("Age: " + String.format("%.1f", years) + " years");
            }
        } else {
            double years = Duration.between(dobirthPick.atStartOfDay(), dodeathPick).toDays() / 365.00;
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
        bmiLabel.setText("");
        if (!heightField.getText().isEmpty() && !weightField.getText().isEmpty()) {
            try {
                double height = Double.parseDouble(heightField.getText());
                double weight = Double.parseDouble(weightField.getText());
                double BMI = (weight / Math.pow(height, 2)) * 10000;
                if (!Double.isNaN(BMI)) {
                    bmiLabel.setText("BMI: " + String.format("%.2f", BMI));
                }
            } catch (NumberFormatException e) {
            }
        }
    }

    /**
     * /**
     * Function which takes all the inputs of the user attributes window.
     * Checks if all these inputs are valid and then sets the user's attributes to those inputted.
     *
     * @return returns boolean of it working or not
     */
    public boolean updateUser() {
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
                userController.requestFocus();
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
                userController.requestFocus();
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
                userController.requestFocus();
                return false;
            } else {
                for (String pressureComponent : bloodPressureList) {
                    try {
                        Integer.parseInt(pressureComponent);
                    } catch (NumberFormatException e) {
                        WindowManager.createAlert(AlertType.ERROR, "Error", "Error with the Blood Pressure Input ", "Please input a valid blood " +
                            "pressure input.").show();
                        userController.requestFocus();
                        return false;
                    }
                }
                userBloodPressure = bloodPressureTextField.getText();
            }
        }

        LocalDate currentDate = LocalDate.now();
        if (dateOfBirthPicker.getValue().isAfter(currentDate)) {
            WindowManager.createAlert(AlertType.ERROR, "Error", "Error with the Date Input ", "The date of birth cannot be after today.").show();
            userController.requestFocus();
            return false;
        } else if (dateOfDeathPicker.getValue() != null && dateOfDeathPicker.getValue().isAfter(currentDate)) {
            WindowManager.createAlert(AlertType.ERROR, "Error", "Error with the Date Input ", "The date of death cannot be after today.").show();
            userController.requestFocus();
            return false;
        } else if (dateOfDeathPicker.getValue() != null && dateOfBirthPicker.getValue().isAfter(dateOfDeathPicker.getValue())) {
            WindowManager.createAlert(AlertType.ERROR, "Error", "Error with the Date Input ", "The date of birth cannot be after the date of death" +
                ".").show();
            userController.requestFocus();
            return false;
        }

        userController.addHistoryEntry("Updated attribute", "A user attribute was updated.");
        //Commit changes
        currentUser.setNameArray(name);
        currentUser.setPreferredNameArray(preferredName);
        currentUser.setHeight(userHeight);
        currentUser.setWeight(userWeight);
        currentUser.setBloodPressure(userBloodPressure);
        currentUser.setDateOfBirth(dateOfBirthPicker.getValue());
        currentUser.setDateOfDeath(dateOfDeathPicker.getDateTimeValue());
        currentUser.setGender(genderComboBox.getValue());
        currentUser.setGenderIdentity(genderIdentityComboBox.getValue());
        currentUser.setBloodType(bloodTypeComboBox.getValue());
        currentUser.setAlcoholConsumption(alcoholConsumptionComboBox.getValue());
        currentUser.setSmokerStatus(smokerStatusComboBox.getValue());

        if(regionComboBox.getValue() != null) {
            currentUser.setRegion(regionComboBox.getValue().toString());
        }


        if(countryOfDeathComboBox.getValue() != null) {
            currentUser.setCountryOfDeath(countryOfDeathComboBox.getValue().toString());
        }

        if(countryComboBox.getValue() != null) {
            currentUser.setCountry(countryComboBox.getValue().toString());
        }

        currentUser.setCurrentAddress(addressField.getText());
        currentUser.setCityOfDeath(deathCityField.getText());

        if(deathInNewZealand) {
            if(regionOfDeathComboBox.getValue() != null) {
                currentUser.setRegionOfDeath(regionOfDeathComboBox.getValue().toString());
            } else {
                currentUser.setRegionOfDeath("");
            }
        } else {
            currentUser.setRegionOfDeath(regionOfDeathField.getText());
        }

        if(inNewZealand) {
            System.out.println("In nz");
            if(regionComboBox.getValue() != null) {
                currentUser.setRegion(regionComboBox.getValue().toString());
                System.out.println("Regoin: " +currentUser.getRegion());
            } else {
                currentUser.setRegion("");
            }
        } else {
            currentUser.setRegion(regionField.getText());
        }


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
        userController.setWelcomeText("Welcome, " + currentUser.getPreferredName());
        settingAttributesLabel.setText("Attributes for " + currentUser.getPreferredName());
        return true;
    }

    /**
     * Function which takes the current user object that has logged in and
     * takes all their attributes and populates the user attributes on the attributes pane accordingly.
     */
    public void populateUserFields() {
        try {
            List<String> validCountries = new ArrayList<>();
            for(Country c : WindowManager.getDataManager().getGeneral().getAllCountries(userController.getToken())) {
                if(c.getValid())
                    validCountries.add(c.getCountryName());
            }
            countryOfDeathComboBox.setItems(FXCollections.observableArrayList(validCountries));
            countryComboBox.setItems(FXCollections.observableArrayList(validCountries));
        } catch (HttpResponseException e) {
            Debugger.error("Could not populate combobox of countries. Failed to retrieve information from the server.");
        }
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

        if(currentUser.getRegionOfDeath() != null) {
            try{
                regionOfDeathComboBox.getSelectionModel().select(NZRegion.parse(currentUser.getRegionOfDeath()));
            } catch (IllegalArgumentException e) {
                regionOfDeathComboBox.getSelectionModel().select(null);
            }

        }
        regionOfDeathField.setText(currentUser.getRegionOfDeath());


        if(currentUser.getRegion() != null) {
            try {
                regionComboBox.getSelectionModel().select(NZRegion.parse(currentUser.getRegion()));
            } catch (IllegalArgumentException e) {
                regionComboBox.getSelectionModel().select(null);
            }
        }
        regionField.setText(currentUser.getRegion());

        deathCityField.setText(currentUser.getCityOfDeath());

        System.out.println(currentUser.getCityOfDeath());
        if(currentUser.getCountry() != null) {
            countryComboBox.getSelectionModel().select(currentUser.getCountry().toString());
        }

        if(currentUser.getCountryOfDeath() != null) {
            countryOfDeathComboBox.getSelectionModel().select(currentUser.getCountryOfDeath());
        }


        dateOfBirthPicker.setValue(currentUser.getDateOfBirth());
        dateOfDeathPicker.setDateTimeValue(currentUser.getDateOfDeath());
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
        highlightOrganCheckBoxes();
    }

    /**
     * Checks for any new updates when an attribute field loses focus, and appends to the attribute undo stack if there is new changes.
     */
    public void attributeFieldUnfocused() {
        User oldFields = new User(currentUser);
        if (updateUser() && !currentUser.attributeFieldsEqual(oldFields)) {
            addToUndoStack(oldFields);
            userController.setUndoRedoButtonsDisabled(false, true);
            titleBar.saved(false);
            statusIndicator.setStatus("Edited user details", false);
        }
    }


    /**
     * starts the user attributes controller
     * @param location not used
     * @param resources not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        regionComboBox.setItems(FXCollections.observableArrayList(NZRegion.values()));
        regionOfDeathComboBox.setItems(FXCollections.observableArrayList(NZRegion.values()));
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
        for(CheckBox checkbox:organTickBoxes.values()){
            checkbox.selectedProperty().addListener((observable, oldValue, newValue) -> highlightOrganCheckBoxes());
        }

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
        deathCityField.focusedProperty().addListener((observable, oldValue, newValue) -> {
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
    }

    /**
     * undos the last change
     */
    public void undo(){
        attributeFieldUnfocused();
        //Add the current fields to the redo stack
        redoStack.add(new User(currentUser));
        //Copy the attribute information from the top element of the undo stack
        currentUser.copyFieldsFrom(undoStack.getLast());
        //Remove the top element of the undo stack
        undoStack.removeLast();
        populateUserFields();
    }

    /**
     * redos the last undo
     */
    @Override
    public void redo() {
        attributeFieldUnfocused();
        //Add the current fields to the undo stack
        undoStack.add(new User(currentUser));
        //Copy the attribute information from the top element of the redo stack
        currentUser.copyFieldsFrom(redoStack.getLast());
        //Remove the top element of the redo stack
        redoStack.removeLast();
        populateUserFields();
    }

    /**
     * set whether to show the date of daeth controls
     * @param shown whaether to show or not
     */
    public void setDeathControlsShown(boolean shown) {
        dateOfDeathPicker.setDisable(!shown);
        deathCityField.setDisable(!shown);
    }
}
