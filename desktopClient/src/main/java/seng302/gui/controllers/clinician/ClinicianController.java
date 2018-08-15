package seng302.gui.controllers.clinician;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.apache.http.client.HttpResponseException;
import org.controlsfx.control.StatusBar;
import seng302.User.Attribute.Gender;
import seng302.User.Attribute.NZRegion;
import seng302.User.Attribute.Organ;
import seng302.User.Clinician;
import seng302.User.User;
import seng302.generic.Country;
import seng302.generic.Debugger;
import seng302.generic.WindowManager;
import seng302.gui.StatusIndicator;
import seng302.gui.TFScene;
import seng302.gui.TitleBar;

import java.net.URL;
import java.util.*;

import static seng302.generic.WindowManager.setButtonSelected;

/**
 * Class to control all the logic for the clinician interactions with the application.
 */
public class ClinicianController implements Initializable {
    @FXML
    private TableColumn profileName;
    @FXML
    private TableColumn profileUserType;
    @FXML
    private TableColumn profileAge;
    @FXML
    private TableColumn profileGender;
    @FXML
    private TableColumn profileRegion;
    @FXML
    private TableView profileTable;
    @FXML
    private TextField profileSearchTextField;
    @FXML
    private Pane background;
    @FXML
    private Label staffIDLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label regionLabel;
    @FXML
    private Label clinicianDisplayText;
    @FXML
    private Label userDisplayText;
    @FXML
    private Button undoWelcomeButton;
    @FXML
    private Button transplantListButton;
    @FXML
    private Button homeButton;
    @FXML
    private Button organListButton;
    @FXML
    private GridPane mainPane;
    @FXML
    private MenuItem accountSettingsMenuItem;
    @FXML
    private ComboBox clinicianGenderComboBox;
    @FXML
    private ComboBox clinicianUserTypeComboBox;
    @FXML
    private ComboBox clinicianOrganComboBox;
    @FXML
    private ComboBox numberOfResultsToDisplay;
    @FXML
    private TextField clinicianAgeField;
    @FXML
    private AnchorPane transplantListPane;
    @FXML
    private AnchorPane organsPane;
    @FXML
    private StatusBar statusBar;
    @FXML
    private ClinicianWaitingListController waitingListController;
    @FXML
    private ClinicianAvailableOrgansController availableOrgansController;
    @FXML
    private ComboBox countryComboBox;
    @FXML
    private ComboBox<String> regionComboBox;
    @FXML
    private TextField clinicianRegionField;


    private FadeTransition fadeIn = new FadeTransition(
            Duration.millis(1000)
    );

    private Clinician clinician;

    private StatusIndicator statusIndicator = new StatusIndicator();
    private TitleBar titleBar;

    private int resultsPerPage;
    private int numberXofResults;

    private List<User> usersFound = new ArrayList<>();

    private ObservableList<User> currentUsers = FXCollections.observableArrayList();

    private String searchNameTerm = "";
    private String searchRegionTerm = "";
    private String searchGenderTerm = null;
    private String searchAgeTerm = "";
    private String searchOrganTerm = null;
    private String searchUserTypeTerm = null;
    private String token;

    private String clinicianString = "clinician";
    private String areYouSure = "Are you sure?";

    public ClinicianController() {
        this.titleBar = new TitleBar();
        titleBar.setStage(WindowManager.getStage());

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
    public void setRegion(String value, ComboBox countryComboBox, ComboBox<String> regionComboBox, TextField regionField) {
        String country = countryComboBox.getValue().toString();
        boolean useCombo = false;
        if (country != null) {
            useCombo = country.equalsIgnoreCase("New Zealand");
        }
        if(value == null) {
            if(useCombo){
                regionComboBox.getSelectionModel().select("All Regions");
            } else {
                regionField.setText("");
            }
        } else {
            if(useCombo){
                regionComboBox.getSelectionModel().select(value);
            } else {
                regionField.setText(value);
            }
        }
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
            setRegion(userValue,countryComboBox, regionComboBox, regionField);
        } else {
            setRegion("", countryComboBox, regionComboBox, regionField);
        }
    }

    /**
     * Updates the visibility of the region controls and updates the undo stack if changes were made
     */
    public void countryChanged() {
        String currentRegion = getRegion(countryComboBox, regionComboBox, clinicianRegionField);
        setRegionControls(currentRegion, countryComboBox.getValue().toString(), regionComboBox, clinicianRegionField);
        updateFoundUsers(resultsPerPage,false);
    }

    public Clinician getClinician() {
        return clinician;
    }


    /**
     * Checks whether this clinician has an API token.
     *
     * @return Whether this clinician has an API token
     */
    public boolean hasToken() {
        return token != null;
    }

    /**
     * Sets the current clinician
     *
     * @param clinician The clinician to se as the current
     * @param token The login token of this clinician
     */
    public void setClinician(Clinician clinician, String token) {
        this.clinician = clinician;
        this.token = token;
        waitingListController.setToken(token);
        availableOrgansController.setToken(token);

        if (clinician.getRegion() == null) {
            clinician.setRegion("");
        }
        if (clinician.getWorkAddress() == null) {
            clinician.setWorkAddress("");
        }

        try {
            List<String> validCountries = new ArrayList<>();
            List<Country> allCountries = WindowManager.getDataManager().getGeneral().getAllCountries(token);
            for(Country c : allCountries) {
                if(c.getValid())
                    validCountries.add(c.getCountryName());
            }
            countryComboBox.setItems(FXCollections.observableArrayList(validCountries));
            countryComboBox.getItems().add("All Countries");
        } catch (HttpResponseException e) {
            Debugger.error("Could not populate combobox of countries. Failed to retrieve information from the server.");
        }

        List<String> nzRegions = new ArrayList<>();
        for(NZRegion r : NZRegion.values()) {
            nzRegions.add(r.toString());
        }
        regionComboBox.setItems(FXCollections.observableArrayList(nzRegions));
        regionComboBox.getItems().add("All Regions");


        countryComboBox.setValue("All Countries");
        setRegionControls("", "All Countries", regionComboBox, clinicianRegionField);

        waitingListController.setup();

        updateDisplay();
        WindowManager.updateTransplantWaitingList();
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public int getNumberXofResults() {
        return numberXofResults;
    }

    /**
     * Updates all the displayed TextFields to the values
     * from the current clinician
     */
    public void updateDisplay() {
        titleBar.setTitle(clinician.getName(), "Clinician", null);
        userDisplayText.setText("Welcome " + clinician.getName());
        staffIDLabel.setText(Long.toString(clinician.getStaffID()));
        nameLabel.setText("Name: " + clinician.getName());
        addressLabel.setText("Address: " + clinician.getWorkAddress());
        regionLabel.setText("Region: " + clinician.getRegion());
        staffIDLabel.setText("Staff ID: " + Long.toString(clinician.getStaffID()));
    }

    /**
     * Update the window title when there are unsaved changes
     */
    @FXML
    private void edited() {
        titleBar.saved(false);
    }

    /**
     * Logs out this clinician on the server, removing its authorisation token.
     */
    public void serverLogout() {
        try {
            WindowManager.getDataManager().getGeneral().logoutUser(token);
        } catch (HttpResponseException e) {
            Debugger.error("Failed to log out on server.");
        }
        this.token = null;
    }

    /**
     * Refresh the items in the search table, waiting list table, and the current clinician's attributes.
     */
    public void refresh() {
        Optional<ButtonType> result = Optional.empty();
        Alert alert = WindowManager.createAlert(Alert.AlertType.CONFIRMATION, "Confirm Refresh", "Are you sure you want to refresh?",
                "Refreshing will update your clinician attributes to the latest version from the server, as well as updating the search table and the waiting list item table.");
        result = alert.showAndWait();

        if (result != null && result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Clinician latest = WindowManager.getDataManager().getClinicians().getClinician((int) clinician.getStaffID(), token);
                setClinician(latest, token);
                updateFoundUsers();
                WindowManager.updateTransplantWaitingList();
                WindowManager.updateAvailableOrgans();
            } catch (HttpResponseException e) {
                Debugger.error("Failed to fetch admin with id: " + clinician.getStaffID());
                e.printStackTrace();
                alert.close();
                alert = WindowManager.createAlert(Alert.AlertType.ERROR, "Refresh Failed", "Refresh failed",
                        "Clinician data could not be refreshed because there was an error contacting the server.");
                alert.showAndWait();
            }
        }
        alert.close();
    }

    /**
     * Logs out the clinician. The user is asked if they're sure they want to log out, if yes,
     * all open user windows spawned by the clinician are closed and the main scene is returned to the logout screen.
     */
    public void logout() {
        Alert alert = WindowManager.createAlert(Alert.AlertType.CONFIRMATION, areYouSure, "Are you sure would like to log out? ",
                "Logging out without saving loses your non-saved data.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            availableOrgansController.stopTimer();
            serverLogout();
            WindowManager.resetScene(TFScene.login);
            WindowManager.closeAllChildren();
            WindowManager.setScene(TFScene.login);
            WindowManager.resetScene(TFScene.clinician);
        } else {
            alert.close();
        }
    }


    /**
     * Function which is called when the user wants to update their account settings in the user Window,
     * and creates a new account settings window to do so. Then does a prompt for the password as well.
     */
    public void updateAccountSettings() {
        TextInputDialog dialog = new TextInputDialog("");
        WindowManager.setIconAndStyle(dialog.getDialogPane());
        dialog.setTitle("View Account Settings");
        dialog.setHeaderText("In order to view your account settings, \nplease enter your login details.");
        dialog.setContentText("Please enter your password:");
        Optional<String> password = dialog.showAndWait();
        if (password.isPresent()) { //Ok was pressed, Else cancel
            if (password.get().equals(clinician.getPassword())) {
                try {
                    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/clinician/clinicianSettings.fxml"));
                    Stage stage = new Stage();
                    stage.getIcons().add(WindowManager.getIcon());
                    stage.setResizable(false);
                    stage.setTitle("Account Settings");
                    stage.setScene(new Scene(root, 290, 280));
                    stage.initModality(Modality.APPLICATION_MODAL);

                    WindowManager.setCurrentClinicianForAccountSettings(clinician, token);
                    WindowManager.setClinicianAccountSettingsEnterEvent();

                    stage.showAndWait();
                } catch (Exception e) {
                    Debugger.error(e.getLocalizedMessage());
                }
            } else { // Password incorrect
                WindowManager.createAlert(Alert.AlertType.INFORMATION, "Incorrect",
                        "Incorrect password. ", "Please enter the correct password to view account settings").show();
            }
        }
    }

    /**
     * Updates the current clinicians attributes to
     * reflect those of the values in the displayed TextFields
     */
    public void updateClinicianPopUp() {
        Debugger.log("Name=" + clinician.getName() + ", Address=" + clinician.getWorkAddress() + ", Region=" + clinician.getRegion());

        // Create the custom dialog.
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update clinician");
        dialog.setHeaderText("Update clinician Details");
        WindowManager.setIconAndStyle(dialog.getDialogPane());

        // Set the button types.
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().lookupButton(updateButtonType).setId("clinicianSettingsPopupUpdateButton");

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        TextField clinicianName = new TextField();
        clinicianName.setPromptText(clinician.getName());
        clinicianName.setId("clinicianName");
        TextField clinicianAddress = new TextField();
        clinicianAddress.setId("clinicianAddress");
        clinicianAddress.setPromptText(clinician.getWorkAddress());
        TextField clinicianRegion = new TextField();
        clinicianRegion.setId("clinicianRegion");
        clinicianRegion.setPromptText(clinician.getRegion());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(clinicianName, 1, 0);
        grid.add(new Label("Address:"), 0, 1);
        grid.add(clinicianAddress, 1, 1);
        grid.add(new Label("Region:"), 0, 2);
        grid.add(clinicianRegion, 1, 2);

        // Enable/Disable login button depending on whether a username was entered.
        Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
        updateButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        clinicianName.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));

        // Do some validation (using the Java 8 lambda syntax).
        clinicianAddress.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));

        clinicianRegion.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(clinicianName::requestFocus);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == updateButtonType) {
            String newName;
            String newAddress;
            String newRegion;

            if (clinicianName.getText().equals("")) {
                newName = clinician.getName();
            } else {
                newName = clinicianName.getText();
            }

            if (clinicianAddress.getText().equals("")) {
                newAddress = clinician.getWorkAddress();
            } else {
                newAddress = clinicianAddress.getText();
            }

            if (clinicianRegion.getText().equals("")) {
                newRegion = clinician.getRegion();
            } else {
                newRegion = clinicianRegion.getText();
            }
            save(newName, newAddress, newRegion);
            updateDisplay();
        }
    }

    /**
     * Saves the clinician ArrayList to a JSON file. Used for updating attributes only
     * @param newName the updated Clinicians name.
     * @param newAddress the updated Clinicians address.
     * @param newRegion the updated Clinicians region.
     */
    public void save(String newName, String newAddress, String newRegion) {
        Alert alert = WindowManager.createAlert(Alert.AlertType.CONFIRMATION, areYouSure,
                "Are you sure would like to update the current clinician? ", "By doing so, the clinician will be updated with all filled in fields.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Debugger.log("Name=" + newName + ", Address=" + newAddress + ", Region=" + newRegion);
            clinician.setName(newName);
            clinician.setWorkAddress(newAddress);
            clinician.setRegion(newRegion);
            try {
                WindowManager.getDataManager().getClinicians().updateClinician(clinician, token);
            } catch (HttpResponseException e) {
                Debugger.error("Failed to update clinician with id: " + clinician.getStaffID());
            }
        }
        alert.close();
    }

    /**
     * Closes the application
     */
    public void close() {
        Alert alert = WindowManager.createAlert(Alert.AlertType.CONFIRMATION, areYouSure, "Are you sure would like to exit? ",
                "You will lose any unsaved data.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            for (Stage userWindow : WindowManager.getCliniciansUserWindows().keySet()) {
                userWindow.close();
            }
            Platform.exit();
        } else {
            alert.close();
        }
    }

    /**
     * Changes the focus to the pane when pressed
     */
    public void requestFocus() {
        background.requestFocus();
    }

    /**
     * Clears the filter fields of the advanced filters
     */
    public void clearFilter() {
        clinicianRegionField.clear();
        clinicianAgeField.clear();
        clinicianGenderComboBox.setValue(null);
        clinicianOrganComboBox.setValue(null);
        clinicianUserTypeComboBox.setValue(null);
        countryComboBox.setValue("All Countries");
        setRegion(null, countryComboBox, regionComboBox,clinicianRegionField);
    }

    public void updateFoundUsers(){
        updateFoundUsers(resultsPerPage, false);
    }


    /**
     * Updates the list of users found from the search
     * @param count The count of users in the application
     * @param onlyChangingPage boolean to handle the changing page
     */
    public void updateFoundUsers(int count, boolean onlyChangingPage) {
        try {
            profileSearchTextField.setPromptText("There are " + WindowManager.getDataManager().getUsers().count(token) + " users in total");
        } catch (HttpResponseException e) {
            Debugger.error("Failed to fetch all users.");
        }
        Map<String, String> searchMap = new HashMap<>();

        if (!searchNameTerm.equals("")){
            searchMap.put("name", searchNameTerm);
        }

        //Add in check for region

        String region = getRegion(countryComboBox, regionComboBox, clinicianRegionField);
        if(!region.equals("") && !region.equals("All Regions")) {
            searchMap.put("region", region);
        }


        //Add in check for country

        if (!countryComboBox.getValue().toString().equals("All Countries")) {
            searchMap.put("country", countryComboBox.getValue().toString());
        }


        //Add in check for age

        if (!searchAgeTerm.equals("")) {
            searchMap.put("age", searchAgeTerm);
        }

        //Add in check for gender

        if (searchGenderTerm != null) {
            searchMap.put("gender", searchGenderTerm);
        }

        //Add in check for organ

        if (searchOrganTerm != null) {
            searchMap.put("organ", searchOrganTerm);
        }

        //Add in check for user type

        if (searchUserTypeTerm != null) {
            if (searchUserTypeTerm.equals("neither")) {
                searchMap.put("userType", "");
                searchUserTypeTerm = "";
            }
            searchMap.put("userType", searchUserTypeTerm);
        }

        try {
            int totalNumberOfResults = WindowManager.getDataManager().getUsers().count(token);
            searchMap.put("count", String.valueOf(count));

            usersFound = WindowManager.getDataManager().getUsers().queryUsers(searchMap, token);
            currentUsers = FXCollections.observableArrayList(usersFound);
            profileTable.setItems(currentUsers);

            if(!onlyChangingPage) {
                populateNResultsComboBox(totalNumberOfResults);
            }
        } catch (HttpResponseException e) {
            Debugger.error("Failed to perform user search on the server.");
        }
    }

    public void firstClinicianLoad(){
        try {
            profileSearchTextField.setPromptText("There are " + WindowManager.getDataManager().getUsers().count(token) + " users int total");
            currentUsers = FXCollections.observableArrayList(WindowManager.getDataManager().getUsers().getAllUsers(token));
        } catch (HttpResponseException e){
            Debugger.error("Failed to perform user search on the server.");
        }
    }

    /**
     * Function which populates the combo box for displaying a certain number of results based on the search fields.
     *
     * @param numberOfSearchResults the number of results of the users found
     */
    public void populateNResultsComboBox(int numberOfSearchResults) {
        String results = " results";
        numberOfResultsToDisplay.getItems().clear();
        String firstPage = "First page";
        numberOfResultsToDisplay.setDisable(true);
        numberOfResultsToDisplay.getItems().add(firstPage);
        numberOfResultsToDisplay.getSelectionModel().select(firstPage);
        if (numberOfSearchResults > resultsPerPage && numberOfSearchResults < numberXofResults) {
            numberOfResultsToDisplay.setDisable(false);
            numberOfResultsToDisplay.getItems().add("All " + numberOfSearchResults + results);
        } else if (numberOfSearchResults > resultsPerPage && numberOfSearchResults > numberXofResults) {
            numberOfResultsToDisplay.setDisable(false);
            numberOfResultsToDisplay.getItems().add("Top " + numberXofResults + results);
            numberOfResultsToDisplay.getItems().add("All " + numberOfSearchResults + results);
        }
    }

    /**
     * starts up the clinician controller
     * @param location not used
     * @param resources not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        clinicianGenderComboBox.setItems(FXCollections.observableArrayList(Gender.values()));
        clinicianUserTypeComboBox.setItems(FXCollections.observableArrayList(Arrays.asList("Donor", "Receiver", "Neither")));
        clinicianOrganComboBox.setItems(FXCollections.observableArrayList(Organ.values()));

        resultsPerPage = 15;
        numberXofResults = 200;

        profileSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchNameTerm = newValue;
            updateFoundUsers(resultsPerPage, false);
        });

        clinicianAgeField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchAgeTerm = newValue;
            updateFoundUsers(resultsPerPage,false);
        });


        regionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateFoundUsers(resultsPerPage,false);
        });

        clinicianRegionField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFoundUsers(resultsPerPage,false);
        });


        clinicianGenderComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                searchGenderTerm = null;

            } else {
                searchGenderTerm = newValue.toString();
            }
            updateFoundUsers(resultsPerPage,false);
        });

        clinicianUserTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                searchUserTypeTerm = null;

            } else {
                searchUserTypeTerm = newValue.toString();
            }
            updateFoundUsers(resultsPerPage,false);

        });

        clinicianOrganComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                searchOrganTerm = null;

            } else {
                searchOrganTerm = newValue.toString();
            }
            updateFoundUsers(resultsPerPage,false);

        });

        profileName.setCellValueFactory(new PropertyValueFactory<>("name"));
        profileUserType.setCellValueFactory(new PropertyValueFactory<>("type"));
        profileAge.setCellValueFactory(new PropertyValueFactory<>("ageString"));
        profileGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        profileRegion.setCellValueFactory(new PropertyValueFactory<>("region"));

        numberOfResultsToDisplay.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals("First page")) {
                    updateFoundUsers(resultsPerPage,true);
                } else if (((String) newValue).contains("Top")) {
                    updateFoundUsers(numberXofResults,true);
                } else if (((String) newValue).contains("All")) {
                    try{
                        updateFoundUsers(WindowManager.getDataManager().getUsers().count(token),true);
                    } catch (HttpResponseException e) {
                        Debugger.log("Could not update table. Failed to retrieve the total number of users.");
                    }
                }
            }
        });

        fadeIn.setDelay(Duration.millis(1000));
        fadeIn.setFromValue(1.0);
        fadeIn.setToValue(0.0);
        fadeIn.setCycleCount(0);
        fadeIn.setAutoReverse(false);

        profileTable.setItems(currentUsers);

        WindowManager.setClinicianController(this);

        profileTable.setItems(currentUsers);

        /*
         * RowFactory for the profileTable.
         * Displays a tooltip when the mouse is over a table entry.
         * Adds a mouse click listener to each row in the table so that a user window
         * is opened when the event is triggered
         */
        profileTable.setRowFactory(new Callback<TableView<User>, TableRow<User>>() {
            @Override
            public TableRow<User> call(TableView<User> tableView) {
                final TableRow<User> row = new TableRow<User>() {
                    private Tooltip tooltip = new Tooltip();

                    @Override
                    public void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        if (user == null || empty) {
                            setTooltip(null);
                        } else {
                            if (user.getOrgans().isEmpty()) {
                                tooltip.setText("Preferred name :" + user.getPreferredName() + ".");
                            } else {
                                String organs = user.getOrgans().toString();
                                tooltip.setText("Preferred name :" + user.getPreferredName() + ". Donor: " + organs.substring(1, organs.length() -
                                        1));
                            }
                            setTooltip(tooltip);
                        }
                    }
                };
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getClickCount() == 2) {
                        try{
                            User latestCopy = WindowManager.getDataManager().getUsers().getUser((long)row.getItem().getId(), token);
                            row.setItem(latestCopy);
                            WindowManager.newCliniciansUserWindow(latestCopy, token);

                        } catch (HttpResponseException e) {
                            Debugger.error("Failed to open user window. user could not be fetched from the server.");
                        }
                    }
                });
                return row;
            }
        });
        statusIndicator.setStatusBar(statusBar);
        profileTable.refresh();
    }

    /**
     * Hides all of the main panes.
     */
    private void hideAllTabs() {
        setButtonSelected(homeButton, false);
        setButtonSelected(transplantListButton, false);
        setButtonSelected(organListButton, false);

        mainPane.setVisible(false);
        transplantListPane.setVisible(false);
        organsPane.setVisible(false);
    }

    /**
     * Sets the user Attribute pane as the visible pane.
     */
    public void showMainPane() {
        hideAllTabs();
        setButtonSelected(homeButton, true);
        mainPane.setVisible(true);
        availableOrgansController.stopTimer();
    }

    /**
     * Calls the transplantWaitingList controller and displays it.
     * also refreshes the waitinglist table data
     */
    public void transplantWaitingList() {
        hideAllTabs();
        setButtonSelected(transplantListButton, true);
        transplantListPane.setVisible(true);
        availableOrgansController.stopTimer();

        titleBar.setTitle(clinician.getName(), "Clinician", "Transplant Waiting List");
    }

    /**
     * Calls the available organs controller and displays it.
     * also refreshes the table data
     */
    public void organsAvailable() {
        hideAllTabs();
        setButtonSelected(organListButton, true);
        organsPane.setVisible(true);
        availableOrgansController.startTimer();
        availableOrgansController.setup();

        WindowManager.updateAvailableOrgans();
        titleBar.setTitle(clinician.getName(), clinicianString, "Available Organs");
    }
}
