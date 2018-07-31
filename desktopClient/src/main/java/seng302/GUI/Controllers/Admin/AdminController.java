package seng302.GUI.Controllers.Admin;

import com.google.gson.Gson;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.http.client.HttpResponseException;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.StatusBar;
import seng302.Data.Interfaces.GeneralDAO;
import seng302.GUI.Controllers.Clinician.ClinicianWaitingListController;
import seng302.GUI.Controllers.Clinician.CreateClinicianController;
import seng302.GUI.Controllers.User.CreateUserController;
import seng302.GUI.StatusIndicator;
import seng302.GUI.TFScene;
import seng302.Generic.*;
import seng302.User.Admin;
import seng302.User.Attribute.Gender;
import seng302.User.Attribute.Organ;
import seng302.User.Attribute.ProfileType;
import seng302.User.Clinician;
import seng302.User.Medication.InteractionApi;
import seng302.User.User;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static seng302.Generic.IO.getJarPath;
import static seng302.Generic.WindowManager.setButtonSelected;

/**
 * Class to control all the logic for the currentAdmin interactions with the application.
 */
public class AdminController implements Initializable {

    @FXML
    private TabPane tableTabPane;
    // User Tab Pane FXML elements
    @FXML
    private TableView<User> userTableView;
    @FXML
    private TableColumn<User, String> userNameTableColumn, userTypeTableColumn, userGenderTableColumn, userRegionTableColumn;
    @FXML
    private TableColumn<User, Double> userAgeTableColumn;
    // Clinician Tab Pane FXML elements
    @FXML
    private TableView<Clinician> clinicianTableView;
    @FXML
    private TableColumn<Clinician, String> clinicianUsernameTableColumn, clinicianNameTableColumn, clinicianAddressTableColumn, clinicianRegionTableColumn;
    @FXML
    private TableColumn<Clinician, Long> clinicianIDTableColumn;

    // Admin Tab Pane FXML elements
    @FXML
    private TableView<Admin> adminTableView;
    @FXML
    private TableColumn<Admin, String> adminUsernameTableColumn, adminNameTableColumn;
    @FXML
    private TableColumn<Admin, Long> adminIDTableColumn;
    @FXML
    private Pane background;
    @FXML
    private Label staffIDLabel, userDisplayText, adminNameLabel, adminAddressLabel;
    @FXML
    private Button undoWelcomeButton,redoWelcomeButton, homeButton, transplantListButton, cliTabButton;
    @FXML
    private GridPane mainPane;
    @FXML
    private TextField profileSearchTextField, adminRegionField, adminAgeField;
    @FXML
    private ComboBox<Gender> adminGenderComboBox;
    @FXML
    private ComboBox<Organ> adminOrganComboBox;
    @FXML
    private ComboBox<String> adminUserTypeComboBox;

    @FXML
    private ComboBox numberOfResultsToDisplay;

    @FXML
    private StatusBar statusBar;
    @FXML
    private AnchorPane cliPane, transplantListPane;
    @FXML
    private AdminCliController cliController;
    @FXML
    private ClinicianWaitingListController waitingListController;

    private StatusIndicator statusIndicator = new StatusIndicator();
    private List<User> usersFound = new ArrayList<>();
    private LinkedList<Admin> adminUndoStack = new LinkedList<>(), adminRedoStack = new LinkedList<>();

    private Admin currentAdmin;

    private ObservableList<User> currentUsers = FXCollections.observableArrayList();
    private ObservableList<Clinician> currentClinicians = FXCollections.observableArrayList();
    private ObservableList<Admin> currentAdmins = FXCollections.observableArrayList();

    private String searchNameTerm = "";
    private String searchRegionTerm = "";
    private String searchGenderTerm = null;
    private String searchAgeTerm = "";
    private String searchOrganTerm = null;
    private String searchUserTypeTerm = null;

    private int resultsPerPage;
    private int numberXofResults;

    private Gson gson = new Gson();
    private String token;

    /**
     * Sets the current currentAdmin
     * @param currentAdmin The currentAdmin to se as the current
     * @param token the token used to communicate with the admin cli
     */
    public void setAdmin(Admin currentAdmin, String token) {
        this.currentAdmin = currentAdmin;
        this.token = token;
        cliController.setToken(token);
        waitingListController.setToken(token);
        updateDisplay();
        refreshLatestProfiles();
    }

    /**
     * method to get the current admin
     * @return Admin the current admin
     */
    public Admin getAdmin() {
        return this.currentAdmin;
    }

    /**
     * Updates all the displayed TextFields to the values
     * from the current currentAdmin
     */
    private void updateDisplay() {
        userDisplayText.setText("Welcome " + currentAdmin.getName());
        adminNameLabel.setText("Name: " + currentAdmin.getName());
        adminAddressLabel.setText("Address: " + currentAdmin.getWorkAddress());
        staffIDLabel.setText(Long.toString(currentAdmin.getStaffID()));
    }

    /**
     * Refreshes the profiles from WindowManager and loads them into local lists
     */
    public void refreshLatestProfiles() {
        try {
            currentUsers.setAll(WindowManager.getDataManager().getUsers().getAllUsers(token));
            currentClinicians.setAll(WindowManager.getDataManager().getClinicians().getAllClinicians(token));
            currentAdmins.setAll(WindowManager.getDataManager().getAdmins().getAllAdmins(token));
        } catch (HttpResponseException e) {
            Debugger.error("Failed to retrieve all users, clinicians, and admins.");
        }
    }

    /**
     * Logs out this admin on the server, removing its authorisation token.
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
     * Logs out the currentAdmin. The user is asked if they're sure they want to log out, if yes,
     * all open user windows spawned by the currentAdmin are closed and the main scene is returned to the logout screen.
     */
    public void logout() {
        Alert alert = WindowManager.createAlert(Alert.AlertType.CONFIRMATION, "Are you sure?", "Are you sure would like to log out? ",
                "Logging out without saving loses your non-saved data.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(null) == ButtonType.OK) {
            serverLogout();
            WindowManager.closeAllChildren();
            WindowManager.setScene(TFScene.login);
            WindowManager.resetScene(TFScene.admin);
        } else {
            alert.close();
        }
    }


    /**
     * Updates the current clinicians attributes to
     * reflect those of the values in the displayed TextFields
     */
    public void updateAdminPopUp() {
        addAdminToUndoStack(currentAdmin);

        // Create the custom dialog.
        Dialog<ArrayList<String>> dialog = new Dialog<>();
        dialog.setTitle("Update Admin");
        dialog.setHeaderText("Update Admin Details");
        WindowManager.setIconAndStyle(dialog.getDialogPane());

        // Set the button types.
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        TextField adminName = new TextField();
        adminName.setPromptText(currentAdmin.getName());
        adminName.setId("adminName");
        TextField adminAddress = new TextField();
        adminAddress.setId("adminAddress");
        adminAddress.setPromptText(currentAdmin.getWorkAddress());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(adminName, 1, 0);
        grid.add(new Label("Address:"), 0, 1);
        grid.add(adminAddress, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node updateButton = dialog.getDialogPane().lookupButton(updateButtonType);
        updateButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        adminName.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));

        // Do some validation (using the Java 8 lambda syntax).
        adminAddress.textProperty().addListener((observable, oldValue, newValue) -> updateButton.setDisable(newValue.trim().isEmpty()));

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(adminName::requestFocus);

        // Convert the result to a diseaseName-dateOfDiagnosis-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                String newName;
                String newAddress;

                if (adminName.getText().equals("")) {
                    newName = currentAdmin.getName();
                } else {
                    newName = adminName.getText();
                }

                if (adminAddress.getText().equals("")) {
                    newAddress = currentAdmin.getWorkAddress();
                } else {
                    newAddress = adminAddress.getText();
                }

                return new ArrayList<>(Arrays.asList(newName, newAddress));
            }
            return null;
        });

        Optional<ArrayList<String>> result = dialog.showAndWait();

        result.ifPresent(newAdminDetails -> {
            currentAdmin.setName(newAdminDetails.get(0));
            currentAdmin.setWorkAddress(newAdminDetails.get(1));
            save();
            updateDisplay();
        });
    }

    /**
     * Saves the currentAdmin ArrayList to a JSON file
     */
    public void save() {
        Debugger.log("AdminController: Save called");
        Alert alert = WindowManager.createAlert(Alert.AlertType.CONFIRMATION, "Are you sure?",
                "Are you sure would like to save all profiles? ",
                "All profiles will be saved (user, clinician, admin).");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                WindowManager.getDataManager().getAdmins().updateAdminDetails(currentAdmin, token);
            } catch (HttpResponseException e) {
                Debugger.error("Failed to save admin with id: " + currentAdmin.getStaffID());
            }

            //TODO PUT in save to Database for Users and Clinicians
            //IO.saveUsers(IO.getAdminPath(), LoginType.ADMIN);
            //IO.saveUsers(IO.getUserPath(), LoginType.USER);
            //IO.saveUsers(IO.getClinicianPath(), LoginType.CLINICIAN);
        }
        alert.close();
    }

    /**
     * Shows a dialog to load a profile JSON from file, along with success/failure alerts.
     */
    public void load() {
        Debugger.log("AdminController: Load called");

        // Formats the initial load dialog window
        Alert loadDialog = new Alert(Alert.AlertType.CONFIRMATION);
        loadDialog.setTitle("Confirm Data Type");
        loadDialog.setHeaderText("Please Select the Profile Type to Import");
        loadDialog.setContentText("This will close other open ODMS windows.");

        // Add in custom ButtonTypes
        ButtonType userButton = new ButtonType("Users");
        ButtonType clinicianButton = new ButtonType("Clinicians");
        ButtonType adminButton = new ButtonType("Admins");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        loadDialog.getButtonTypes().setAll(userButton, clinicianButton, adminButton, cancelButton);

        String fileToLoadPath;
        boolean loadSuccessful = false;
        boolean loadAborted = false;

        // Perform actions based on the load
        Optional<ButtonType> result = loadDialog.showAndWait();

        // Switch based on the text string the button contains
        if (result.orElse(null) != null) {
            WindowManager.closeAllChildren();
            String selectedButtonText = result.orElse(null).getText();
            switch (selectedButtonText) {
                case "Users":
                    fileToLoadPath = getSelectedFilePath(ProfileType.USER);
                    if (fileToLoadPath != null) {
                        String extension = "";

                        int i = fileToLoadPath.lastIndexOf('.');
                        if (i > 0) {
                            extension = fileToLoadPath.substring(i+1);
                        }
                        if (extension.equals("csv")) {
                            loadSuccessful = IO.importUserCSV(fileToLoadPath, token);
                        } else if (extension.equals("json")) {
                            loadSuccessful = IO.importProfiles(fileToLoadPath, ProfileType.USER, token);
                        } else {
                            loadSuccessful = false;
                        }

                    } else {
                        loadAborted = true;
                    }
                    break;
                case "Clinicians":
                    fileToLoadPath = getSelectedFilePath(ProfileType.CLINICIAN);
                    if (fileToLoadPath != null) {
                        loadSuccessful = IO.importProfiles(fileToLoadPath, ProfileType.CLINICIAN, token);
                    } else {
                        loadAborted = true;
                    }
                    break;
                case "Admins":
                    String fileToLoad = getSelectedFilePath(ProfileType.ADMIN);
                    if (fileToLoad != null) {
                        loadSuccessful = IO.importProfiles(fileToLoad, ProfileType.ADMIN, token);
                    } else {
                        loadAborted = true;
                    }
                    break;
                default:
                    // If the cancel button is pressed, don't want to harass the user with the extra dialog
                    loadAborted = true;
            }
        }

        // Present an alert informing the user on the load outcome
        if (loadSuccessful) {
            Alert successAlert = WindowManager.createAlert(Alert.AlertType.INFORMATION, "Load successful",
                    "",
                    "All profiles successfully loaded.");
            successAlert.showAndWait();
            refreshLatestProfiles();
            updateFoundUsers();
        } else if (loadAborted) {
            Alert abortAlert = WindowManager.createAlert(Alert.AlertType.INFORMATION, "Load cancelled",
                    "",
                    "No profile data loaded.");
            abortAlert.showAndWait();
        } else {
            Alert failureAlert = WindowManager.createAlert(Alert.AlertType.INFORMATION, "Load failed",
                    "",
                    "Failed to load profiles from file");
            failureAlert.showAndWait();
        }
    }

    /**
     * Opens a FileChooser to get the file path of the selected file
     * @param profileType Profile type the user will specified the path for
     * @return Absolute file path to the specified JSON file of specific profileType
     */
    private String getSelectedFilePath(ProfileType profileType) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtensionJSON =
                new FileChooser.ExtensionFilter(
                        "JSON Files", "*.json");

        fileChooser.getExtensionFilters().add(fileExtensionJSON);

        // Customise the title bar to help the user (and us!) on the profile type to browse for
        switch (profileType) {
            case USER:
                FileChooser.ExtensionFilter fileExtensionJSONCSV =
                        new FileChooser.ExtensionFilter(
                                "JSON/CSV Files", "*.csv", "*.json");
                fileChooser.getExtensionFilters().remove(fileExtensionJSON);
                fileChooser.getExtensionFilters().add(fileExtensionJSONCSV);
                fileChooser.setTitle("Open User File");
                break;
            case CLINICIAN:
                fileChooser.setTitle("Open Clinician File");
                break;
            case ADMIN:
                fileChooser.setTitle("Open Admin File");
                break;
            default:
                throw new IllegalArgumentException("Not a valid file import type.");
        }

        // Present the FileChooser, return null on cancel
        try {
            File file = fileChooser.showOpenDialog(WindowManager.getStage());
            return file.getAbsolutePath();
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Closes the application
     */
    public void close() {
        Alert alert = WindowManager.createAlert(Alert.AlertType.CONFIRMATION, "Are you sure?", "Are you sure would like to exit? ",
                "You will lose any unsaved data.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
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
     * The main admin undo function. Called from the button press, reads from the undo stack and then updates the GUI accordingly.
     */
    public void undo() {
        // TODO implement undo
        try {
            WindowManager.getDataManager().getGeneral().reset(token);
        } catch (HttpResponseException e) {
            Debugger.error("Failed to reset the database.");
        }
    }

    /**
     * The main admin redo function. Called from the button press, reads from the redo stack and then updates the GUI accordingly.
     */
    public void redo() {
        // TODO implement redo
    }

    /**
     * Creates a deep copy of the current currentAdmin and adds that copy to the undo stack. Then updates the GUI button to be usable.
     *
     * @param admin the currentAdmin object being copied.
     */
    private void addAdminToUndoStack(Admin admin) {
        adminUndoStack.add(new Admin(admin));
        if (undoWelcomeButton.isDisable()) {
            undoWelcomeButton.setDisable(false);
        }
    }

    /**
     * Clears the filter fields of the advanced filters
     */
    public void clearFilter() {
        adminRegionField.clear();
        adminAgeField.clear();
        adminGenderComboBox.setValue(null);
        adminOrganComboBox.setValue(null);
        adminUserTypeComboBox.setValue(null);
    }

    /**
     * refreshes the list of users with a max ammount
     */
    public void updateFoundUsers() {
        updateFoundUsers(resultsPerPage,false);
    }

    /**
     * Updates the list of users found from the search
     * @param count int the max ammount of users to display
     * @param onlyChangingPage boolean if there is only 1 page in the admin view
     */
    public void updateFoundUsers(int count, boolean onlyChangingPage) {
        try {
            profileSearchTextField.setPromptText("There are " + WindowManager.getDataManager().getUsers().count(token) + " users");
        } catch (HttpResponseException e) {
            Debugger.error("Failed to fetch all users.");
        }
        Map<String, String> searchMap = new HashMap<>();

        if (!searchNameTerm.equals("")){
            searchMap.put("name", searchNameTerm);
        }

        //Add in check for region

        if (!searchRegionTerm.equals("")) {
            searchMap.put("region", searchRegionTerm);
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
            searchMap.put("count", String.valueOf(WindowManager.getDataManager().getUsers().count(token)));
            int totalNumberOfResults = WindowManager.getDataManager().getUsers().queryUsers(searchMap, token).size();
            searchMap.put("count", String.valueOf(count));

            usersFound = WindowManager.getDataManager().getUsers().queryUsers(searchMap, token);
            currentUsers = FXCollections.observableArrayList(usersFound);
            userTableView.setItems(currentUsers);

            if(!onlyChangingPage) {
                populateNResultsComboBox(totalNumberOfResults);
            }
        } catch (HttpResponseException e) {
            Debugger.error("Failed to perform user search on the server.");
        }
    }


    /**
     * Checks whether this admin has an API token.
     *
     * @return Whether this admin has an API token
     */
    public boolean hasToken() {
        return token != null;
    }

    /**
     * Function which populates the combo box for displaying a certain number of results based on the search fields.
     *
     * @param numberOfSearchResults the number of results of the users found
     */
    public void populateNResultsComboBox(int numberOfSearchResults) {
        numberOfResultsToDisplay.getItems().clear();
        String firstPage = "First page";
        numberOfResultsToDisplay.setDisable(true);
        numberOfResultsToDisplay.getItems().add(firstPage);
        numberOfResultsToDisplay.getSelectionModel().select(firstPage);
        if (numberOfSearchResults > resultsPerPage && numberOfSearchResults < numberXofResults) {
            numberOfResultsToDisplay.setDisable(false);
            numberOfResultsToDisplay.getItems().add("All " + numberOfSearchResults + " results");
        } else if (numberOfSearchResults > resultsPerPage && numberOfSearchResults > numberXofResults) {
            numberOfResultsToDisplay.setDisable(false);
            numberOfResultsToDisplay.getItems().add("Top " + numberXofResults + " results");
            numberOfResultsToDisplay.getItems().add("All " + numberOfSearchResults + " results");
        }
    }

    /**
     * starts up the admin controller
     * @param location not used
     * @param resources not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        resultsPerPage = 15;
        numberXofResults = 200;

        // Set the items of the TableView to populate objects
        userTableView.setItems(currentUsers);
        clinicianTableView.setItems(currentClinicians);
        adminTableView.setItems(currentAdmins);

        // Set User TableColumns to point at correct attributes
        userNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userTypeTableColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        userAgeTableColumn.setCellValueFactory(new PropertyValueFactory<>("ageString"));
        userGenderTableColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        userRegionTableColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        // Set Clinician TableColumns to point at correct attributes
        clinicianUsernameTableColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        clinicianNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        clinicianAddressTableColumn.setCellValueFactory(new PropertyValueFactory<>("workAddress"));
        clinicianRegionTableColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
        clinicianIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("staffID"));

        // Set Admin TableColumns to point at correct attributes
        adminNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        adminUsernameTableColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        adminIDTableColumn.setCellValueFactory(new PropertyValueFactory<>("staffID"));

        // Set up listeners that only allow 1 item to be selected at a time across the three tables
        userTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                clinicianTableView.getSelectionModel().clearSelection();
                adminTableView.getSelectionModel().clearSelection();
            }
        });

        clinicianTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                userTableView.getSelectionModel().clearSelection();
                adminTableView.getSelectionModel().clearSelection();
            }
        });

        adminTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                userTableView.getSelectionModel().clearSelection();
                clinicianTableView.getSelectionModel().clearSelection();
            }
        });

        // Set up a context menu containing a delete option for admins
        final ContextMenu profileMenu = new ContextMenu();

        MenuItem deleteProfile = new MenuItem();
        deleteProfile.setOnAction(event -> {
            User selectedUser = userTableView.getSelectionModel().getSelectedItem();
            Clinician selectedClinician = clinicianTableView.getSelectionModel().getSelectedItem();
            Admin selectedAdmin = adminTableView.getSelectionModel().getSelectedItem();

            Alert alert = WindowManager.createAlert(Alert.AlertType.CONFIRMATION, "Are you sure?", "Confirm profile deletion",
                    "Are you sure you want to delete this profile? This cannot be undone.");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.orElse(null) == ButtonType.OK) {
                if (selectedUser != null) {
                    // A user has been selected for deletion
                    Debugger.log("Deleting User: " + selectedUser);
                    try {
                        WindowManager.getDataManager().getUsers().removeUser(selectedUser.getId(), token);
                        refreshLatestProfiles();
                    } catch (HttpResponseException e) {
                        Debugger.error("Failed to remove user with id: " + selectedUser.getId());
                    }
                    //IO.saveUsers(IO.getUserPath(), LoginType.USER);

                    statusIndicator.setStatus("Deleted user " + selectedUser.getName(), false);
                } else if (selectedClinician != null) {
                    // A clinician has been selected for deletion
                    Debugger.log("Deleting Clinician: " + selectedClinician);

                    try {
                        WindowManager.getDataManager().getClinicians().removeClinician(selectedClinician.getStaffID(), token);
                        refreshLatestProfiles();
                    } catch (HttpResponseException e) {
                        Debugger.error("Failed to remove clinician with id: " + selectedClinician.getStaffID());
                    }
                    //IO.saveUsers(IO.getUserPath(), LoginType.USER);

                    statusIndicator.setStatus("Deleted clinician " + selectedClinician.getName(), false);
                } else if (selectedAdmin != null) {
                    // An admin has been selected for deletion
                    Debugger.log("Deleting Admin: " + selectedAdmin);
                    try{
                        WindowManager.getDataManager().getAdmins().removeAdmin(selectedAdmin.getStaffID(), token);
                        refreshLatestProfiles();
                    } catch (HttpResponseException e) {
                        Debugger.error("Failed to remove admin with id: " + currentAdmin.getStaffID());
                    }
                    //IO.saveUsers(IO.getAdminPath(), LoginType.ADMIN);

                    statusIndicator.setStatus("Deleted admin " + selectedAdmin.getName(), false);
                }
                refreshLatestProfiles();
            }
        });
        profileMenu.getItems().add(deleteProfile);

        //Add in a edit clinician option on the clinicians view.
        MenuItem editClinician = new MenuItem();
        editClinician.setOnAction(event -> {
            Clinician selectedClinician = clinicianTableView.getSelectionModel().getSelectedItem();
            updateClinicianPopUp(selectedClinician);
        });
        editClinician.setVisible(false);
        profileMenu.getItems().add(editClinician);

        userTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                User selectedUser = userTableView.getSelectionModel().getSelectedItem();
                // No need to check for default user
                if (selectedUser != null) {
                    deleteProfile.setText("Delete " + selectedUser.getName());
                    profileMenu.show(userTableView, event.getScreenX(), event.getScreenY());
                }
            }
        });

        clinicianTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                Clinician selectedClinician = clinicianTableView.getSelectionModel().getSelectedItem();
                if (selectedClinician != null) {
                    // Check if this is the default clinician
                    if (selectedClinician.getStaffID() == 1) {
                        deleteProfile.setDisable(true);
                        deleteProfile.setText("Cannot delete default clinician");
                    } else {
                        deleteProfile.setDisable(false);
                        deleteProfile.setText("Delete " + selectedClinician.getName());
                        editClinician.setVisible(true);
                        editClinician.setText("Edit " + selectedClinician.getName());
                    }
                    profileMenu.show(clinicianTableView, event.getScreenX(), event.getScreenY());
                }
            }
        });

        adminTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                Admin selectedAdmin = adminTableView.getSelectionModel().getSelectedItem();
                if (selectedAdmin != null) {
                    // Check if this is the default clinician
                    if (selectedAdmin.getStaffID() == 1) {
                        deleteProfile.setDisable(true);
                        deleteProfile.setText("Cannot delete default admin");
                    } else {
                        deleteProfile.setDisable(false);
                        deleteProfile.setText("Delete " + selectedAdmin.getName());
                    }
                    profileMenu.show(adminTableView, event.getScreenX(), event.getScreenY());
                }
            }
        });

        adminGenderComboBox.setItems(FXCollections.observableArrayList(Gender.values()));
        adminUserTypeComboBox.setItems(FXCollections.observableArrayList(Arrays.asList("Donor", "Receiver", "Neither")));
        adminOrganComboBox.setItems(FXCollections.observableArrayList(Organ.values()));


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

        updateFoundUsers(resultsPerPage, false);

        try {
            profileSearchTextField.setPromptText("There are " + WindowManager.getDataManager().getUsers().count(token) + " users");
        } catch (HttpResponseException e) {
            Debugger.error("Could not set name search textfield placeholder. Failed to retrieve the number of users.");
        }

        profileSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchNameTerm = newValue;
            updateFoundUsers();
        });

        adminRegionField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchRegionTerm = newValue;
            updateFoundUsers();
        });

        adminAgeField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchAgeTerm = newValue;
            updateFoundUsers();
        });

        adminGenderComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                searchGenderTerm = null;

            } else {
                searchGenderTerm = newValue.toString();
            }
            updateFoundUsers();
        });

        adminUserTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                searchUserTypeTerm = null;
            } else {
                searchUserTypeTerm = newValue;
            }
            updateFoundUsers();
        });

        adminOrganComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                searchOrganTerm = null;
            } else {
                searchOrganTerm = newValue.toString();
            }
            updateFoundUsers();
        });

        WindowManager.setAdminController(this);

        /*
         * RowFactory for the userTableView.
         * Displays a tooltip when the mouse is over a table entry.
         * Adds a mouse click listener to each row in the table so that a user window
         * is opened when the event is triggered
         */
        userTableView.setRowFactory(new Callback<TableView<User>, TableRow<User>>() {
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
                                tooltip.setText(user.getName() + ".");
                            } else {
                                String organs = user.getOrgans().toString();
                                tooltip.setText(user.getName() + ". User: " + organs.substring(1, organs.length() - 1));
                            }
                            setTooltip(tooltip);
                        }
                    }
                };
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getClickCount() == 2) {
                        WindowManager.newAdminsUserWindow(row.getItem(), token);
                    }
                });
                return row;
            }
        });
        statusIndicator.setStatusBar(statusBar);
        userTableView.refresh();
    }

    /**
     * Updates the current clinicians attributes to
     * reflect those of the values in the displayed TextFields
     * @param clinician Clinician the clincian to use to display its info
     */
    public void updateClinicianPopUp(Clinician clinician) {
        //adminUndoStack.add(new Clinician(clinician));
        //undoWelcomeButton.setDisable(false);
        Debugger.log("Name=" + clinician.getName() + ", Address=" + clinician.getWorkAddress() + ", Region=" + clinician.getRegion());

        // Create the custom dialog.
        Dialog<ArrayList<String>> dialog = new Dialog<>();
        dialog.setTitle("Update Clinician");
        dialog.setHeaderText("Update Clinician Details");
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

        dialog.setResultConverter(dialogButton -> {
            String newName = "";
            String newAddress = "";
            String newRegion = "";
            if (dialogButton == updateButtonType) {

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
            }
            return new ArrayList<>(Arrays.asList(newName, newAddress, newRegion));
        });

        Optional<ArrayList<String>> result = dialog.showAndWait();
        result.ifPresent(newClinicianDetails -> {
            Debugger.log("Name=" + newClinicianDetails.get(0) + ", Address=" + newClinicianDetails.get(1) + ", Region=" + newClinicianDetails
                    .get(2));
            clinician.setName(newClinicianDetails.get(0));
            clinician.setWorkAddress(newClinicianDetails.get(1));
            clinician.setRegion(newClinicianDetails.get(2));
            try {
                WindowManager.getDataManager().getClinicians().updateClinician(clinician, token);
                refreshLatestProfiles();
            } catch (HttpResponseException e) {
                Debugger.error("Failed to update clinician with id: " + clinician.getStaffID());
            }


        });
    }


    /**
     * Hides all of the main panes.
     */
    private void hideAllTabs() {
        setButtonSelected(homeButton, false);
        setButtonSelected(transplantListButton, false);
        setButtonSelected(cliTabButton, false);

        mainPane.setVisible(false);
        transplantListPane.setVisible(false);
        cliPane.setVisible(false);
        undoWelcomeButton.setDisable(true);
        redoWelcomeButton.setDisable(true);
    }

    /**
     * Sets the User Attribute pane as the visible pane.
     */
    public void showMainPane() {
        hideAllTabs();
        setButtonSelected(homeButton, true);
        mainPane.setVisible(true);
        undoWelcomeButton.setDisable(adminUndoStack.isEmpty());
        redoWelcomeButton.setDisable(adminRedoStack.isEmpty());

        //Could be updated in the CLI
        clinicianTableView.refresh();
        userTableView.refresh();
    }

    /**
     * Calls the transplantWaitingList controller and displays it.
     * also refreshes the waitinglist table data
     */
    public void transplantWaitingList() {
        hideAllTabs();
        setButtonSelected(transplantListButton, true);
        transplantListPane.setVisible(true);

        WindowManager.updateTransplantWaitingList();
    }

    /**
     * Switches the active pane to the CLI pane.
     */
    public void viewCli() {
        hideAllTabs();
        setButtonSelected(cliTabButton, true);
        cliPane.setVisible(true);
    }

    /**
     * Resets the database. Called by Database , then Reset
     */
    public void databaseReset() {

        Alert alert = WindowManager.createAlert(Alert.AlertType.CONFIRMATION, "Are you sure?", "Confirm database reset",
                "Are you sure you want to reset the entire database? All admins, clinicians and users will be deleted. This cannot be undone.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(null) == ButtonType.OK) {

            Debugger.log("DB reset called");
            try {
                WindowManager.getDataManager().getGeneral().reset(token);
                WindowManager.closeAllChildren();
                WindowManager.setScene(TFScene.login);
                WindowManager.resetScene(TFScene.admin);
            } catch (HttpResponseException e) {
                Debugger.error("Failed to reset the database.");
            }

        }
    }

    /**
     * Resamples the database. Called by Database, then Resample
     */
    public void databaseResample() {
        Alert alert = WindowManager.createAlert(Alert.AlertType.CONFIRMATION, "Are you sure?", "Confirm database reset",
                "Are you sure you want to reset the entire database? All admins, clinicians and users will be deleted. This cannot be undone.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(null) == ButtonType.OK) {
            Debugger.log("DB resample called");
            try {
                WindowManager.getDataManager().getGeneral().resample(token);
            } catch (HttpResponseException e) {
                Debugger.error("Failed to resample the database.");
            }
        }
    }

    /**
     * Opens separate window to input + create a new admin
     */
    @FXML
    private void createAdmin() {
        Stage stage = new Stage();
        stage.setMinHeight(0);
        stage.setMinWidth(0);
        stage.setHeight(TFScene.createAccount.getHeight());
        stage.setWidth(TFScene.createAccount.getHeight());
        stage.setResizable(false);
        stage.initModality(Modality.NONE);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/createAdmin.fxml"));
            Parent root = loader.load();
            CreateAdminController createAdminController = loader.getController();
            Scene newScene = new Scene(root, 900, 575);
            stage.setScene(newScene);
            Admin newAdmin = createAdminController.showAndWait(stage);
            if (newAdmin != null) {
                try {
                    WindowManager.getDataManager().getAdmins().insertAdmin(newAdmin, token);
                    refreshLatestProfiles();
                } catch (HttpResponseException e) {
                    Debugger.error("Failed to post admin to the server.");
                }
                //IO.saveUsers(IO.getAdminPath(), LoginType.ADMIN);
                statusIndicator.setStatus("Added new admin " + newAdmin.getUsername(), false);
            }
        } catch (IOException e) {
            Debugger.error("Unable to load fxml or save file.");
            e.printStackTrace();
            Platform.exit();
        }
    }

    /**
     * Opens separate window to input + create a new clinician
     */
    @FXML
    private void createClinician() {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setMinHeight(0);
        stage.setMinWidth(0);
        stage.setHeight(TFScene.createAccount.getHeight());
        stage.setWidth(TFScene.createAccount.getHeight());
        stage.initModality(Modality.NONE);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/clinician/createClinician.fxml"));
            Parent root = loader.load();
            CreateClinicianController createClinicianController = loader.getController();

            Scene newScene = new Scene(root, 900, 575);
            stage.setScene(newScene);
            Clinician newClinician = createClinicianController.showAndWait(stage);
            if (newClinician != null) {
                try {
                    WindowManager.getDataManager().getClinicians().insertClinician(newClinician, token);
                    refreshLatestProfiles();
                } catch (HttpResponseException e) {
                    Debugger.error("Failed to insert new clinician.");
                }
                //IO.saveUsers(IO.getClinicianPath(), LoginType.CLINICIAN);
                statusIndicator.setStatus("Added new clinician " + newClinician.getUsername(), false);
            }
        } catch (IOException e) {
            Debugger.error("Unable to load fxml or save file.");
            e.printStackTrace();
            Platform.exit();
        }
    }

    /**
     * Opens separate window to input + create a new user
     */
    @FXML
    private void createUser() {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setMinHeight(0);
        stage.setMinWidth(0);
        stage.setHeight(TFScene.createAccount.getHeight());
        stage.setWidth(TFScene.createAccount.getHeight());
        stage.initModality(Modality.NONE);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user/createUser.fxml"));
            Parent root = loader.load();
            CreateUserController createUserController = loader.getController();

            Scene newScene = new Scene(root, 900, 575);
            stage.setScene(newScene);
            User user = createUserController.showAndWait(stage);
            if (user != null) {
                try{
                    WindowManager.getDataManager().getUsers().insertUser(user);
                    refreshLatestProfiles();
                } catch(HttpResponseException e) {
                    Debugger.error("Failed to insert new user.");
                }
                //IO.saveUsers(IO.getUserPath(), LoginType.USER);
                statusIndicator.setStatus("Added new user " + user.getUsername(), false);
            } else {
                Debugger.error("AdminController: Failed to create user");
            }
        } catch (IOException e) {
            Debugger.error("Unable to load fxml or save file.");
            e.printStackTrace();
            Platform.exit();
        }
    }

    /**
     * clears the local api cache files
     */
    public void clearCache (){
        Cache autocompleteCache = IO.importCache(IO.getJarPath() + "/autocomplete.json");
        Cache activeIngredientsCache = IO.importCache(IO.getJarPath() + "/activeIngredients.json");

        autocompleteCache.clear();
        activeIngredientsCache.clear();

        autocompleteCache.save();
        activeIngredientsCache.save();

        InteractionApi.getInstance();
        Cache cache = IO.importCache(getJarPath() + File.separatorChar + "interactions.json");
        cache.clear();
        cache.save();
        InteractionApi.setCache(cache);
    }

    /**
     * sets the valid countries for users to choose from
     */
    public void setCountries(){
        try {
            Dialog<ArrayList<String>> dialog = new Dialog<>();
            dialog.setTitle("Update Countries");
            dialog.setHeaderText("Update Countries");
            WindowManager.setIconAndStyle(dialog.getDialogPane());
            ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);


            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 10, 10, 10));

            final ObservableList<String> countries = FXCollections.observableArrayList();
            GeneralDAO generalDB = WindowManager.getDataManager().getGeneral();
            List<Country> countryList = generalDB.getAllCountries(token);
            for(Country country : countryList) {
                countries.add(country.getCountryName());
            }
            CheckComboBox<String> countryCheckComboBox = new CheckComboBox<>(countries);
            for (Country country : countryList) {
                if (country.getValid()) {
                    countryCheckComboBox.getItemBooleanProperty(country.getCountryName()).setValue(true);
                }
            }

            grid.add(new Label("Countries:"), 0, 0);
            grid.add(countryCheckComboBox, 1, 0);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == updateButtonType) {
                    try {
                        for (Country country : countryList) {
                            country.setValid(countryCheckComboBox.getItemBooleanProperty(country.getCountryName()).getValue());
                        }
                        generalDB.updateCountries(countryList, token);
                        return null;
                    } catch (HttpResponseException e) {
                        databaseError();
                    }
                }
                return null;
            });

            dialog.showAndWait();


        } catch (HttpResponseException e) {
            databaseError();
        }
    }

    /**
     * method to show an error message when there is an error with the database connection
     */
    private void databaseError(){
        Dialog<ArrayList<String>> dialog = new Dialog<>();
        dialog.setTitle("Error");
        dialog.setHeaderText("Error");
        WindowManager.setIconAndStyle(dialog.getDialogPane());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Label label = new Label();
        label.setText("Could not connect to the Sever");

        dialog.getDialogPane().setContent(label);
        dialog.showAndWait();
    }
}
