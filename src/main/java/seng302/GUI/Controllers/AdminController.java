package seng302.GUI.Controllers;

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
import org.controlsfx.control.StatusBar;
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
import java.sql.SQLException;
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
    private StatusBar statusBar;
    @FXML
    private AnchorPane cliPane, transplantListPane;

    private StatusIndicator statusIndicator = new StatusIndicator();
    private ArrayList<User> usersFound;
    private LinkedList<Admin> adminUndoStack = new LinkedList<>(), adminRedoStack = new LinkedList<>();

    private Admin currentAdmin;

    private ObservableList<User> currentUsers;
    private ObservableList<Clinician> currentClinicians;
    private ObservableList<Admin> currentAdmins;

    private String searchNameTerm = "";
    private String searchRegionTerm = "";
    private String searchGenderTerm = null;
    private String searchAgeTerm = "";
    private String searchOrganTerm = null;
    private String searchUserTypeTerm = null;


    /**
     * Sets the current currentAdmin
     *
     * @param currentAdmin The currentAdmin to se as the current
     */
    public void setAdmin(Admin currentAdmin) {
        this.currentAdmin = currentAdmin;
        updateDisplay();
        refreshLatestProfiles();
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
        // Initialise lists that correlate to the three TableViews
        currentUsers = DataManager.users;
        currentClinicians = DataManager.clinicians;
        currentAdmins = DataManager.admins;
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
        System.out.println("AdminController: Save called");
        Alert alert = WindowManager.createAlert(Alert.AlertType.CONFIRMATION, "Are you sure?",
                "Are you sure would like to save all profiles? ",
                "All profiles will be saved (user, clinician, admin).");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                WindowManager.getDatabase().updateAdminDetails(currentAdmin);
            } catch (SQLException e) {
                e.printStackTrace();
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
        System.out.println("AdminController: Load called");

        // Formats the initial load dialog window
        Alert loadDialog = new Alert(Alert.AlertType.CONFIRMATION);
        loadDialog.setTitle("Confirm Data Type");
        loadDialog.setHeaderText("Please Select the JSON Profile Type to Import");
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
                        loadSuccessful = IO.importUsers(fileToLoadPath, ProfileType.USER);
                    } else {
                        loadAborted = true;
                    }
                    break;
                case "Clinicians":
                    fileToLoadPath = getSelectedFilePath(ProfileType.CLINICIAN);
                    if (fileToLoadPath != null) {
                        loadSuccessful = IO.importUsers(fileToLoadPath, ProfileType.CLINICIAN);
                    } else {
                        loadAborted = true;
                    }
                    break;
                case "Admins":
                    String fileToLoad = getSelectedFilePath(ProfileType.ADMIN);
                    if (fileToLoad != null) {
                        loadSuccessful = IO.importUsers(fileToLoad, ProfileType.ADMIN);
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
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter(
                        "JSON Files", "*.json");
        fileChooser.getExtensionFilters().add(fileExtensions);

        // Customise the titlebar to help the user (and us!) on the profile type to browse for
        switch (profileType) {
            case USER:
                fileChooser.setTitle("Open User File");
                break;
            case CLINICIAN:
                fileChooser.setTitle("Open Clinician File");
                break;
            case ADMIN:
                fileChooser.setTitle("Open Admin File");
                break;
            default:
                throw new IllegalArgumentException("Not a valid JSON import type.");
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
            WindowManager.getDatabase().resetDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
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
     * Updates the list of users found from the search
     */
    public void updateFoundUsers() {
        usersFound = SearchUtils.getUsersByNameAlternative(searchNameTerm);

        //Add in check for region

        if (!searchRegionTerm.equals("")) {
            ArrayList<User> newUsersFound = SearchUtils.getUsersByRegionAlternative(searchRegionTerm);
            usersFound.retainAll(newUsersFound);

        }

        //Add in check for age

        if (!searchAgeTerm.equals("")) {
            ArrayList<User> newUsersFound = SearchUtils.getUsersByAgeAlternative(searchAgeTerm);
            usersFound.retainAll(newUsersFound);
        }

        //Add in check for gender

        if (searchGenderTerm != null) {
            ArrayList<User> newUsersFound = new ArrayList<>();
            for (User user : usersFound) {
                if ((user.getGender() != null) && (searchGenderTerm.equals(user.getGender().toString()))) {
                    newUsersFound.add(user);
                }
            }
            usersFound = newUsersFound;
        }

        //Add in check for organ

        if (searchOrganTerm != null) {
            ArrayList<User> newUsersFound = new ArrayList<>();
            for (User user : usersFound) {
                if ((user.getOrgans().size() != 0) && (user.getOrgans().contains(Organ.parse(searchOrganTerm)))) {
                    newUsersFound.add(user);
                }
            }
            usersFound = newUsersFound;
        }

        //Add in check for user type

        if (searchUserTypeTerm != null) {
            if (searchUserTypeTerm.equals("Neither")) {
                searchUserTypeTerm = "";
            }
            ArrayList<User> newUsersFound = new ArrayList<>();
            for (User user : usersFound) {
                if (user.getType().equals("Donor/Receiver") && (!searchUserTypeTerm.equals(""))) {
                    newUsersFound.add(user);
                } else if ((searchUserTypeTerm.equals(user.getType()))) {
                    newUsersFound.add(user);
                }
            }
            usersFound = newUsersFound;
        }

        currentUsers = FXCollections.observableArrayList(usersFound);
        userTableView.setItems(currentUsers);
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshLatestProfiles();

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
                    System.out.println("Deleting User: " + selectedUser);

                    DataManager.users.remove(selectedUser);
                    try {
                        WindowManager.getDatabase().removeUser(selectedUser);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    //IO.saveUsers(IO.getUserPath(), LoginType.USER);

                    statusIndicator.setStatus("Deleted user " + selectedUser.getName(), false);
                } else if (selectedClinician != null) {
                    // A clinician has been selected for deletion
                    System.out.println("Deleting Clinician: " + selectedClinician);

                    DataManager.clinicians.remove(selectedClinician);
                    try {
                        WindowManager.getDatabase().removeClinician(selectedClinician);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    //IO.saveUsers(IO.getUserPath(), LoginType.USER);

                    statusIndicator.setStatus("Deleted clinician " + selectedClinician.getName(), false);
                } else if (selectedAdmin != null) {
                    // An admin has been selected for deletion
                    System.out.println("Deleting Admin: " + selectedAdmin);

                    DataManager.admins.remove(selectedAdmin);
                    try{
                        WindowManager.getDatabase().removeAdmin(selectedAdmin);
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                    //IO.saveUsers(IO.getAdminPath(), LoginType.ADMIN);

                    statusIndicator.setStatus("Deleted admin " + selectedAdmin.getName(), false);
                }
                refreshLatestProfiles();
            }
        });
        profileMenu.getItems().add(deleteProfile);

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
                    if (selectedClinician.getStaffID() == 0) {
                        deleteProfile.setDisable(true);
                        deleteProfile.setText("Cannot delete default clinician");
                    } else {
                        deleteProfile.setDisable(false);
                        deleteProfile.setText("Delete " + selectedClinician.getName());
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
                    if (selectedAdmin.getStaffID() == 0) {
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
                        WindowManager.newCliniciansUserWindow(row.getItem());
                    }
                });
                return row;
            }
        });
        statusIndicator.setStatusBar(statusBar);
        userTableView.refresh();
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

            System.out.println("AdminController: DB reset called");
            try {
                WindowManager.getDatabase().resetDatabase();
                DataManager.users.clear();
                DataManager.users.addAll(WindowManager.getDatabase().getAllUsers());
                WindowManager.closeAllChildren();
                WindowManager.setScene(TFScene.login);
                WindowManager.resetScene(TFScene.admin);
            } catch (SQLException e) {
                e.printStackTrace();
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
            System.out.println("AdminController: DB resample called");
            try {
                WindowManager.getDatabase().loadSampleData();
                DataManager.users.addAll(WindowManager.getDatabase().getAllUsers());
            } catch (SQLException e) {
                e.printStackTrace();
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/createAdmin.fxml"));
            Parent root = loader.load();
            CreateAdminController createAdminController = loader.getController();
            Scene newScene = new Scene(root, 900, 575);
            stage.setScene(newScene);
            Admin newAdmin = createAdminController.showAndWait(stage);
            if (newAdmin != null) {
                    DataManager.admins.add(newAdmin);
                try {
                    WindowManager.getDatabase().insertAdmin(newAdmin);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //IO.saveUsers(IO.getAdminPath(), LoginType.ADMIN);
                statusIndicator.setStatus("Added new admin " + newAdmin.getUsername(), false);
            }
        } catch (IOException e) {
            System.err.println("Unable to load fxml or save file.");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/createClinician.fxml"));
            Parent root = loader.load();
            CreateClinicianController createClinicianController = loader.getController();

            Scene newScene = new Scene(root, 900, 575);
            stage.setScene(newScene);
            Clinician newClinician = createClinicianController.showAndWait(stage);
            if (newClinician != null) {
                DataManager.clinicians.add(newClinician);
                try {
                    WindowManager.getDatabase().insertClinician(newClinician);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //IO.saveUsers(IO.getClinicianPath(), LoginType.CLINICIAN);
                statusIndicator.setStatus("Added new clinician " + newClinician.getUsername(), false);
            }
        } catch (IOException e) {
            System.err.println("Unable to load fxml or save file.");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/createAccount.fxml"));
            Parent root = loader.load();
            CreateAccountController createAccountController = loader.getController();

            Scene newScene = new Scene(root, 900, 575);
            stage.setScene(newScene);
            User user = createAccountController.showAndWait(stage);
            if (user != null) {
                DataManager.users.add(user);
                try{
                    WindowManager.getDatabase().insertUser(user);
                } catch(SQLException e) {
                    e.printStackTrace();
                }
                //IO.saveUsers(IO.getUserPath(), LoginType.USER);
                statusIndicator.setStatus("Added new user " + user.getUsername(), false);
            } else {
                System.out.println("AdminController: Failed to create user");
            }
        } catch (IOException e) {
            System.err.println("Unable to load fxml or save file.");
            e.printStackTrace();
            Platform.exit();
        }
    }

    public void clearCache (){
        Cache autocompleteCache = IO.importCache(IO.getJarPath() + "/autocomplete.json");
        Cache activeIngredientsCache = IO.importCache(IO.getJarPath() + "/activeIngredients.json");

        autocompleteCache.clear();
        activeIngredientsCache.clear();

        autocompleteCache.save();
        activeIngredientsCache.save();

        InteractionApi.getInstance();
        Cache cache = IO.importCache(getJarPath() + "/interactions.json");
        cache.clear();
        cache.save();
        InteractionApi.setCache(cache);
    }
}
