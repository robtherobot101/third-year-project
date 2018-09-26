package seng302.gui.controllers.user;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.http.client.HttpResponseException;
import seng302.User.DonatableOrgan;
import seng302.generic.Country;
import seng302.generic.Debugger;
import seng302.generic.WindowManager;
import seng302.User.Attribute.*;
import seng302.User.User;
import seng302.User.WaitingListItem;
import tornadofx.control.DateTimePicker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class UserAttributesController extends UserTabController implements Initializable {
    @FXML
    private Label settingAttributesLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label bmiLabel;

    @FXML
    private Label dateOfDeathLabel;
    @FXML
    private Label countryOfDeathLabel;
    @FXML
    private Label regionOfDeathLabel;
    @FXML
    private Label cityOfDeathLabel;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField middleNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField weightField;
    @FXML
    private TextField bloodPressureTextField;
    @FXML
    private TextField preferredFirstNameField;
    @FXML
    private TextField preferredMiddleNamesField;
    @FXML
    private TextField preferredLastNameField;
    @FXML
    private TextField regionField;
    @FXML
    private TextField nhiField;
    @FXML
    private DatePicker dateOfBirthPicker;

    @FXML
    private ComboBox countryComboBox;

    @FXML
    private ComboBox<String> regionComboBox;

    @FXML
    private ComboBox<Gender> genderComboBox;
    @FXML
    private ComboBox<Gender> genderIdentityComboBox;
    @FXML
    private ComboBox<BloodType> bloodTypeComboBox;
    @FXML
    private ComboBox<SmokerStatus> smokerStatusComboBox;
    @FXML
    private ComboBox<AlcoholConsumption> alcoholConsumptionComboBox;
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
    private ImageView profileImage;
    @FXML
    private Button changePhotoButton;
    @FXML
    private Button updateDeathDetailsButton;
    @FXML
    private Label dateOfDeath;
    @FXML
    private Label countryOfDeath;
    @FXML
    private Label regionOfDeath;
    @FXML
    private Label cityOfDeath;

    private Map<Organ, CheckBox> organTickBoxes;

    private int updatingFields = 0;

    /**
     * Sets the current user and populates the fields
     * @param user the user to set as the current user
     */
    public void setCurrentUser(User user) {
        currentUser = user;
        populateUserFields();
        if (currentUser.getCountry() != null) {
            setRegionControls(currentUser.getRegion(), currentUser.getCountry(), regionComboBox, regionField);
        }
    }

    /**
     * Updates the current clinicians attributes to
     * reflect those of the values in the displayed TextFields
     */
    public void popupDeathDetails() {
        // Create the custom dialog.
        Dialog<ArrayList<Object>> dialog = new Dialog<>();
        dialog.setTitle("Update death details");
        dialog.setHeaderText("Update death details");
        WindowManager.setIconAndStyle(dialog.getDialogPane());

        // Set the button types.
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        DateTimePicker popupDateOfDeathPicker = new DateTimePicker();
        ComboBox popupCountryOfDeathComboBox = new ComboBox();
        popupCountryOfDeathComboBox.maxWidthProperty().bind(dialog.widthProperty());

        ComboBox popupRegionOfDeathComboBox = new ComboBox();
        popupRegionOfDeathComboBox.maxWidthProperty().bind(dialog.widthProperty());
        TextField popupRegionOfDeathField = new TextField();
        TextField popupCityOfDeathField = new TextField();
        Label errorLabel = new Label();
        errorLabel.setText("");

        grid.add(new Label("Date and Time:"), 0, 0);
        grid.add(popupDateOfDeathPicker, 1, 0);

        grid.add(new Label("Country:"), 0, 1);
        grid.add(popupCountryOfDeathComboBox, 1, 1);

        grid.add(new Label("Region:"), 0, 2);
        grid.add(popupRegionOfDeathComboBox, 1, 2);
        grid.add(popupRegionOfDeathField, 1, 2);

        grid.add(new Label("City:"), 0, 3);
        grid.add(popupCityOfDeathField, 1, 3);

        errorLabel.setStyle("-fx-text-fill: red;");
        grid.add(errorLabel, 0, 4, 2, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
        updateButton.setDisable(true);

        AtomicBoolean dateIsValid = new AtomicBoolean(false);
        AtomicBoolean countryIsValid = new AtomicBoolean(false);
        AtomicBoolean regionIsValid = new AtomicBoolean(false);
        AtomicBoolean cityIsValid = new AtomicBoolean(false);
        AtomicBoolean inputIsValid = new AtomicBoolean(false);

        // Do some validation (using the Java 8 lambda syntax).
        popupDateOfDeathPicker.valueProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue != null) {
                if (newValue.isAfter(LocalDate.now())) {
                    errorLabel.setText("The date of death cannot be after today.");
                    dateIsValid.set(false);
                } else if (newValue.isBefore(dateOfBirthPicker.getValue())) {
                    errorLabel.setText("The date of death must be after the date of birth.");
                    dateIsValid.set(false);

                } else {
                    dateIsValid.set(true);
                }
            } else {
                errorLabel.setText("The date of death must be defined.");
                dateIsValid.set(false);
            }
            inputIsValid.set(dateIsValid.get() && countryIsValid.get() && regionIsValid.get() && cityIsValid.get());
            updateButton.setDisable(!inputIsValid.get());
            errorLabel.setVisible(!dateIsValid.get());
        });

        popupCountryOfDeathComboBox.valueProperty().addListener((observable, oldValue, newValue)  -> {
            countryIsValid.set(!Objects.equals(newValue,""));
            inputIsValid.set(dateIsValid.get() && countryIsValid.get() && regionIsValid.get() && cityIsValid.get());
            updateButton.setDisable(!inputIsValid.get());

            // Setup listener on the countries combobox to switch the regions input type to ComboBox when selected country is "New Zealand"
            setRegionControls(currentUser.getRegionOfDeath(), popupCountryOfDeathComboBox.getValue().toString(), popupRegionOfDeathComboBox, popupRegionOfDeathField);
        });

        popupRegionOfDeathComboBox.valueProperty().addListener((observable, oldValue, newValue)  -> {
            regionIsValid.set(!Objects.equals(getRegion(popupCountryOfDeathComboBox, popupRegionOfDeathComboBox, popupRegionOfDeathField),""));
            inputIsValid.set(dateIsValid.get() && countryIsValid.get() && regionIsValid.get() && cityIsValid.get());
            updateButton.setDisable(!inputIsValid.get());
        });

        popupRegionOfDeathField.textProperty().addListener((observable, oldValue, newValue)  -> {
            regionIsValid.set(!Objects.equals(getRegion(popupCountryOfDeathComboBox, popupRegionOfDeathComboBox, popupRegionOfDeathField),""));
            inputIsValid.set(dateIsValid.get() && countryIsValid.get() && regionIsValid.get() && cityIsValid.get());
            updateButton.setDisable(!inputIsValid.get());
        });

        popupCityOfDeathField.textProperty().addListener((observable, oldValue, newValue)  -> {
            cityIsValid.set(!Objects.equals(newValue, ""));
            inputIsValid.set(dateIsValid.get() && countryIsValid.get() && regionIsValid.get() && cityIsValid.get());
            updateButton.setDisable(!inputIsValid.get());
        });

        // Populate countries combobox
        try {
            List<String> validCountries = new ArrayList<>();
            for(Country c : WindowManager.getDataManager().getGeneral().getAllCountries(userController.getToken())) {
                if(c.getValid())
                    validCountries.add(c.getCountryName());
            }
            popupCountryOfDeathComboBox.setItems(FXCollections.observableArrayList(validCountries));
        } catch (HttpResponseException e) {
            Debugger.error("Could not populate combobox of countries. Failed to retrieve information from the server.");
        }

        // Populate regions combobox
        popupRegionOfDeathComboBox.setItems(FXCollections.observableArrayList(
                Arrays.stream(NZRegion.values()).map(NZRegion::toString).collect(Collectors.toList())
        ));

        // If user death details have not been set, default the death location to user location. Set death date to now
        if(Objects.equals(dateOfDeath.getText().trim(),"")) {

            popupDateOfDeathPicker.setDateTimeValue(LocalDateTime.now());
            if(popupCountryOfDeathComboBox.getItems().contains(countryComboBox.getValue())) {
                popupCountryOfDeathComboBox.setValue(countryComboBox.getValue());
            } else {
                popupCountryOfDeathComboBox.setValue("");
            }
            setRegion(getRegion(countryComboBox, regionComboBox, regionField), popupRegionOfDeathComboBox, popupRegionOfDeathField);
        } else { // Otherwise, populate death location and timestamp
            popupDateOfDeathPicker.setDateTimeValue(LocalDateTime.parse(dateOfDeath.getText()));
            if(popupCountryOfDeathComboBox.getItems().contains(countryOfDeath.getText())) {
                popupCountryOfDeathComboBox.setValue(countryOfDeath.getText());
            } else {
                popupCountryOfDeathComboBox.setValue("");
            }
            setRegion(regionOfDeath.getText(), popupRegionOfDeathComboBox, popupRegionOfDeathField);
            popupCityOfDeathField.setText(cityOfDeath.getText());
        }

        setRegionControls(getRegion(popupCountryOfDeathComboBox, popupRegionOfDeathComboBox, popupRegionOfDeathField)
                , popupCountryOfDeathComboBox.getValue().toString(), popupRegionOfDeathComboBox, popupRegionOfDeathField);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(popupDateOfDeathPicker::requestFocus);
        // Convert the result to a diseaseName-dateOfDiagnosis-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {

                return new ArrayList<Object>(Arrays.asList(
                        popupDateOfDeathPicker.getDateTimeValue(),
                        popupCountryOfDeathComboBox.getValue(),
                        getRegion(popupCountryOfDeathComboBox, popupRegionOfDeathComboBox, popupRegionOfDeathField),
                        popupCityOfDeathField.getText()
                ));
            }
            return null;
        });

        Optional<ArrayList<Object>> result = dialog.showAndWait();

        result.ifPresent(newDeathDetails -> {
            this.dateOfDeath.setText(newDeathDetails.get(0).toString());
            this.countryOfDeath.setText((String) newDeathDetails.get(1));
            this.regionOfDeath.setText((String) newDeathDetails.get(2));
            this.cityOfDeath.setText((String) newDeathDetails.get(3));
            attributeFieldUnfocused();
        });
    }


    /**
     * Gets the region from the regionComboBox or regionField. If New Zealand is the selected country in the
     * given countryComboBox, then the value in the regionComboBox is returned, otherwise the value in
     * the regionField is returned.
     *
     * @param countryComboBox The ComboBox of countries
     * @param regionComboBox The ComboBox of New Zealand regions
     * @param regionField The TextField for regions outside of New Zealand
     * @return the value in the regionComboBox if New Zealand is the selected country, otherwise the value in the regionField.
     */
    public String getRegion(ComboBox<Country> countryComboBox, ComboBox<String> regionComboBox, TextField regionField) {
        boolean getFromComboBox = Objects.equals(countryComboBox.getValue(), "New Zealand");
        if(getFromComboBox) {
            return regionComboBox.getValue();
        }
        return regionField.getText();
    }

    /**
     * Sets the current value of the given regionComboBox and regionField to the given value.
     *
     * @param value The value which the ComboBox and TextField will be set to
     * @param regionComboBox The ComboBox of New Zealand regions
     * @param regionField The TextField for regions outside of New Zealand
     */
    public void setRegion(String value, ComboBox<String> regionComboBox, TextField regionField) {
        regionComboBox.getSelectionModel().select(value);
        regionField.setText(value);
    }

    /**
     * Sets the visibility of the given regionComboBox and regionField depending on the value of the given
     * countryComboBox and userValue.
     * If New Zealand is selected in the countryComboBox, the  regionComboBox is visible, otherwise the regionField is visible.
     *
     * @param userValue The region value of the user (Could be region, or regionOfDeath)
     * @param country The country the user is from
     * @param regionComboBox The ComboBox of New Zealand regions
     * @param regionField The TextField for regions outside of New Zealand
     */
    public void setRegionControls(String userValue, String country, ComboBox<String> regionComboBox, TextField regionField) {
        boolean useCombo = false;
        if (country != null) {
            useCombo = country.equalsIgnoreCase("New Zealand");
        }

        regionComboBox.setVisible(useCombo);
        regionField.setVisible(!useCombo);
        boolean validNZRegion;

        try {
            validNZRegion = Arrays.asList(NZRegion.values()).contains(NZRegion.parse(userValue));
        } catch (IllegalArgumentException e) {
            validNZRegion = false;
        }
        if((useCombo && validNZRegion) || (!useCombo && !validNZRegion)) {
            setRegion(userValue, regionComboBox, regionField);
        } else {
            setRegion("", regionComboBox, regionField);
        }
    }



    /**
     * Updates the visibility of the region controls and updates the undo stack if changes were made
     */
    public void countryChanged() {
        setRegionControls(currentUser.getRegion(), countryComboBox.getValue() != null ? countryComboBox.getValue().toString() : "", regionComboBox, regionField);
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
                    organTickBoxes.get(organ).setTooltip(new Tooltip("user is waiting to receive this organ"));
                }
            }
        }
    }


    /**
     * Updates the age label for the user window based on the date of birth and death of the user.
     */
    public void updateAge() {
        LocalDate dobirthPick = currentUser.getDateOfBirth();
        LocalDateTime dodeathPick = currentUser.getDateOfDeath();

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
                double bmi = (weight / Math.pow(height, 2)) * 10000;
                if (!Double.isNaN(bmi)) {
                    bmiLabel.setText("BMI: " + String.format("%.2f", bmi));
                }
            } catch (NumberFormatException e) {
                Debugger.error(e.getStackTrace());
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

        if (!User.checkNhi(nhiField.getText())) {
            WindowManager.createAlert(AlertType.ERROR, "Error", "Error with the NHI Input ", "Please input a valid NHI input.").show();
            userController.requestFocus();
            return false;
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
        }

        updatingFields++;

        userController.addHistoryEntry("Updated attribute", "A user attribute was updated.");
        //Commit changes
        currentUser.setNameArray(name);
        currentUser.setPreferredNameArray(preferredName);
        currentUser.setHeight(userHeight);
        currentUser.setWeight(userWeight);
        currentUser.setBloodPressure(userBloodPressure);
        currentUser.setDateOfBirth(dateOfBirthPicker.getValue());
        currentUser.setNhi(nhiField.getText());


        if(!Objects.equals(dateOfDeath.getText().trim(), "")) {
            currentUser.setDateOfDeath(LocalDateTime.parse(dateOfDeath.getText()));
        }
        currentUser.setCountryOfDeath(countryOfDeath.getText());
        currentUser.setRegionOfDeath(regionOfDeath.getText());
        currentUser.setCityOfDeath(cityOfDeath.getText());


        currentUser.setGender(genderComboBox.getValue());
        currentUser.setGenderIdentity(genderIdentityComboBox.getValue());
        currentUser.setBloodType(bloodTypeComboBox.getValue());
        currentUser.setAlcoholConsumption(alcoholConsumptionComboBox.getValue());
        currentUser.setSmokerStatus(smokerStatusComboBox.getValue());


        currentUser.setRegion(getRegion(
                countryComboBox, regionComboBox, regionField
        ));

        if(countryComboBox.getValue() != null) {
            currentUser.setCountry(countryComboBox.getValue().toString());
        }

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
        userController.setWelcomeText("Welcome, " + currentUser.getPreferredName());
        settingAttributesLabel.setText("Attributes for " + currentUser.getPreferredName());
        updateAge();
        updateBMI();
        updatingFields--;
        return true;
    }

    /**
     * Function which takes the current user object that has logged in and
     * takes all their attributes and populates the user attributes on the attributes pane accordingly.
     */
    public void populateUserFields() {
        Debugger.log("Repopulating user attributes page.");
        updatingFields++;
        try {
            List<String> validCountries = new ArrayList<>();
            for(Country c : WindowManager.getDataManager().getGeneral().getAllCountries(userController.getToken())) {
                if(c.getValid())
                    validCountries.add(c.getCountryName());
            }
            countryComboBox.setItems(FXCollections.observableArrayList(validCountries));
        } catch (HttpResponseException e) {
            Debugger.error(e.getMessage());
            Debugger.error("Could not populate combobox of countries. Failed to retrieve information from the server.");
        }
        settingAttributesLabel.setText("Attributes for " + currentUser.getPreferredName());
        extractNames(currentUser.getNameArray(), firstNameField, middleNameField, lastNameField);
        extractNames(currentUser.getPreferredNameArray(), preferredFirstNameField, preferredMiddleNamesField, preferredLastNameField);
        addressField.setText(currentUser.getCurrentAddress());
        nhiField.setText(currentUser.getNhi());

        countryComboBox.getSelectionModel().select(currentUser.getCountry());

        setRegion(currentUser.getRegion(), regionComboBox, regionField);

        countryOfDeath.setText(currentUser.getCountryOfDeath());
        regionOfDeath.setText(currentUser.getRegionOfDeath());
        cityOfDeath.setText(currentUser.getCityOfDeath());

        dateOfBirthPicker.setValue(currentUser.getDateOfBirth());
        dateOfDeath.setText(currentUser.getDateOfDeath() == null ? "" : currentUser.getDateOfDeath().toString());
        updateAge();

        bloodPressureTextField.setText(currentUser.getBloodPressure());

        genderComboBox.getSelectionModel().select(currentUser.getGender());
        genderIdentityComboBox.getSelectionModel().select(currentUser.getGenderIdentity());
        bloodTypeComboBox.getSelectionModel().select(currentUser.getBloodType());
        smokerStatusComboBox.getSelectionModel().select(currentUser.getSmokerStatus());
        alcoholConsumptionComboBox.getSelectionModel().select(currentUser.getAlcoholConsumption());

        try {
            List<DonatableOrgan> donatableOrgans = WindowManager.getDataManager().getGeneral().getSingleUsersDonatableOrgans(userController.getToken(), currentUser.getId());
            for (Organ key : organTickBoxes.keySet()) {
                organTickBoxes.get(key).setSelected(currentUser.getOrgans().contains(key));
                for (DonatableOrgan organ : donatableOrgans) {
                    if (organ.getOrganType() == key && (organ.getInTransfer() != 0 || organ.isExpired())) {
                        organTickBoxes.get(key).setDisable(true);
                    }
                }
            }
        } catch (HttpResponseException e) {
            for (Organ key : organTickBoxes.keySet()) {
                organTickBoxes.get(key).setSelected(currentUser.getOrgans().contains(key));
            }
        }

        for (Organ key : organTickBoxes.keySet()) {
            organTickBoxes.get(key).setSelected(currentUser.getOrgans().contains(key));
        }

        weightField.setText(currentUser.getWeight() == -1 ? "" : Double.toString(currentUser.getWeight()));
        heightField.setText(currentUser.getHeight() == -1 ? "" : Double.toString(currentUser.getHeight()));
        //set profile image

        Debugger.log("Attempting to update photo when populating attributes page");
        profileImage.setImage(WindowManager.getDataManager().getUsers().getUserPhoto((int) currentUser.getId(), userController.getToken()));

        updateAge();
        updateBMI();
        highlightOrganCheckBoxes();
        updatingFields--;
    }

    /**
     * Extracts first, middle, and last names from a name array, and sets text fields to match their values.
     *
     * @param splitNames The array of names.
     * @param firstNameField The textfield for the first name
     * @param middleNameField The textfield for the middle names
     * @param lastNameField The textfield for the last name
     */
    private void extractNames(String[] splitNames, TextField firstNameField, TextField middleNameField, TextField lastNameField) {
        firstNameField.setText(splitNames[0]);
        if (splitNames.length > 2) {
            String[] middleName = new String[splitNames.length - 2];
            System.arraycopy(splitNames, 1, middleName, 0, splitNames.length - 2);
            middleNameField.setText(String.join(" ", middleName));
            lastNameField.setText(splitNames[splitNames.length - 1]);
        } else if (splitNames.length == 2) {
            middleNameField.setText("");
            lastNameField.setText(splitNames[1]);
        } else {
            middleNameField.setText("");
            lastNameField.setText("");
        }
    }


    /**
     * Checks for any new updates when an attribute field loses focus, and appends to the attribute undo stack if there is new changes.
     */
    public void attributeFieldUnfocused() {
        if (updatingFields < 1) {
            updatingFields++;
            Debugger.log("running unfocused method");
            User oldFields = new User(currentUser);
            if (updateUser() && !currentUser.attributeFieldsEqual(oldFields)) {
                addToUndoStack(oldFields);
                userController.setUndoRedoButtonsDisabled(false, true);
                titleBar.saved(false);
                statusIndicator.setStatus("Edited user details", false);
            }
            updatingFields--;
        }
    }



    /**
     * Called when the upload photo button is pressed. Presents a dialog with a choice of deleting an uploaded photo, or uploading a new one.
     */
    public void changeProfilePhoto() {
        List<String> options = new ArrayList<>();
        options.add("Upload a new profile photo");
        if (currentUser.getProfileImageType() != null) {
            options.add("Delete the current profile photo");
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Photo Options");
        alert.setHeaderText("Maximum Image size is 5MB.");
        alert.setContentText("Accepted file formats are JPEG, PNG, and BMP. Images MUST be square.");

        ButtonType uploadNewPhoto = new ButtonType("Upload new");
        ButtonType useDefaultPhoto = new ButtonType("Use default");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(uploadNewPhoto, useDefaultPhoto, buttonTypeCancel);
        WindowManager.setIconAndStyle(alert.getDialogPane());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == uploadNewPhoto) {
                uploadProfileImage();
            } else if (result.get() == useDefaultPhoto) {
                deleteProfileImage();
            }
        }


    }


    /**
     * Removes a profile photo from the server, if it exists. Then fetches the default photo with a seperate request.
     * This maintains REST.
     */
    private void deleteProfileImage() {
        try {
            WindowManager.getDataManager().getUsers().deleteUserPhoto(currentUser.getId(), userController.getToken());
            //success catch, set to default photo
            Image profilePhoto = WindowManager.getDataManager().getUsers().getUserPhoto(currentUser.getId(), userController.getToken());
            profileImage.setImage(profilePhoto);
            currentUser.setProfileImageType(null);

        } catch (HttpResponseException e) {
            Debugger.error(e.getStackTrace());
        }
    }

    /**
     * Uploads a new profile photo, and sends and stores it on the server by converting it to a base64 string.
     * Images MUST be square, and bmp, png or jpg.
     * The max file size is 5MB.
     */
    private void uploadProfileImage() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter(
                        "JPEG, PNG, BITMAP", "*.png", "*.jpg", "*.jpeg", "*.bmp"
                );
        fileChooser.getExtensionFilters().add(fileExtensions);
        try {
            File file = fileChooser.showOpenDialog(stage);
            String fileType = Files.probeContentType(file.toPath()).split("/")[1];
            if (file.length() < 5000000){
                if(fileType.equals("png") || fileType.equals("jpg") || fileType.equals("bmp") || fileType.equals("jpeg")){
                    currentUser.setProfileImageType(fileType);
                    BufferedImage bImage = ImageIO.read(file);
                    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                    ImageIO.write(bImage, fileType, byteOutputStream);
                    byte[] byteArrayImage = byteOutputStream.toByteArray();

                    String image = Base64.getEncoder().encodeToString(byteArrayImage);
                    WindowManager.getDataManager().getUsers().updateUserPhoto(currentUser.getId(), image, userController.getToken());
                    Image profileImg = SwingFXUtils.toFXImage(bImage, null);
                    if (profileImg.getWidth() == profileImg.getHeight()) {
                        profileImage.setImage(profileImg);
                    } else {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Image not square");
                        alert.setContentText("Image file must have the same width and height.");
                        WindowManager.setIconAndStyle(alert.getDialogPane());
                        alert.showAndWait();
                    }

                }

            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("File Size Too Large");
                alert.setContentText("File size must be below 5MB.");
                WindowManager.setIconAndStyle(alert.getDialogPane());
                alert.showAndWait();
            }
        } catch (NullPointerException e){
            Debugger.error("Error: NULL");
        } catch (IOException e) {
            Debugger.error(e.getStackTrace());
        }
    }


    /**
     * starts the user attributes controller
     * @param location not used
     * @param resources not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<String> nzRegions = new ArrayList<>();
        for(NZRegion r : NZRegion.values()) {
            nzRegions.add(r.toString());
        }
        regionComboBox.setItems(FXCollections.observableArrayList(nzRegions));
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
        dateOfBirthPicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
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

        regionField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                attributeFieldUnfocused();
            }
        });

        nhiField.focusedProperty().addListener((observable, oldValue, newValue) -> {
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
        updatingFields++;
        attributeFieldUnfocused();
        //Add the current fields to the redo stack
        redoStack.add(new User(currentUser));
        //Copy the attribute information from the top element of the undo stack
        currentUser.copyFieldsFrom(undoStack.getLast());
        //Remove the top element of the undo stack
        undoStack.removeLast();
        populateUserFields();
        countryChanged();
        updatingFields--;
    }

    /**
     * redos the last undo
     */
    @Override
    public void redo() {
        updatingFields++;
        attributeFieldUnfocused();
        //Add the current fields to the undo stack
        undoStack.add(new User(currentUser));
        //Copy the attribute information from the top element of the redo stack
        currentUser.copyFieldsFrom(redoStack.getLast());
        //Remove the top element of the redo stack
        redoStack.removeLast();
        populateUserFields();
        countryChanged();
        updatingFields--;
    }

    /**
     * Set whether clinician/admin level controls should be shown or not.
     */
    public void setControlsShown(boolean shown) {
        nhiField.setDisable(!shown);
    }

    /**
     * set whether to show the date of death controls
     * @param shown whether to show or not
     */
    public void setDeathControlsShown(boolean shown) {
        updateDeathDetailsButton.setVisible(shown);
    }
}
