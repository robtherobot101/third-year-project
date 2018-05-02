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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import seng302.Generic.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import seng302.GUI.TFScene;
import seng302.User.Admin;
import seng302.User.Attribute.LoginType;
import seng302.User.Clinician;
import seng302.User.User;

/**
 * Class to control all the logic for the currentAdmin interactions with the application.
 */
public class AdminController implements Initializable {

    private Admin currentAdmin;

    @FXML
    private TabPane tableTabPane;

    // User Tab Pane FXML elements
    @FXML
    private TableView<User> userTableView;

    @FXML
    private TableColumn<User, String> userNameTableColumn, userTypeTableColumn, userGenderTableColumn,
            userRegionTableColumn;
    @FXML
    private TableColumn<User, Double> userAgeTableColumn;

    // Clinician Tab Pane FXML elements
    @FXML
    private TableView<Clinician> clinicianTableView;

    @FXML
    private TableColumn<Clinician, String> clinicianUsernameTableColumn, clinicianNameTableColumn,
            clinicianAddressTableColumn, clinicianRegionTableColumn;
    @FXML
    private TableColumn<Clinician, Long> clinicianIDTableColumn;


    // Admin Tab Pane FXML elements
    @FXML
    private TableView<Admin> adminTableView;
    @FXML
    private TableColumn<Admin, String> adminUsernameTableColumn, adminNameTableColumn;






    @FXML
    private TextField profileSearchTextField;
    @FXML
    private Pane background;
    @FXML
    private Label staffIDLabel;

    @FXML
    private Label userDisplayText;

    @FXML
    private Button undoWelcomeButton;

    @FXML
    private Button redoWelcomeButton;

    @FXML
    private GridPane mainPane;

    @FXML
    private ComboBox numberOfResutsToDisplay;

    @FXML
    private TextField clinicianRegionField;
    @FXML
    private ComboBox clinicianGenderComboBox;
    @FXML
    private TextField clinicianAgeField;
    @FXML
    private ComboBox clinicianUserTypeComboBox;
    @FXML
    private ComboBox clinicianOrganComboBox;
    @FXML
    private Label adminNameLabel;
    @FXML
    private Label adminAddressLabel;


    private int resultsPerPage;
    private int numberXofResults;

    private int page = 1;
    private ArrayList<User> usersFound;

    private ArrayList<UserWindowController> userWindows = new ArrayList<UserWindowController>();

    private LinkedList<Admin> adminUndoStack = new LinkedList<>();
    private LinkedList<Admin> adminRedoStack = new LinkedList<>();


    private ObservableList<User> currentUsers;
    private ObservableList<Clinician> currentClinicians;
    private ObservableList<Admin> currentAdmins;

    private String searchNameTerm = "";
    private String searchRegionTerm = "";
    private String searchGenderTerm = null;
    private String searchAgeTerm = "";
    private String searchOrganTerm = null;
    private String searchUserTypeTerm = null;

    ObservableList<Object> users;

    /**
     * Sets the current currentAdmin
     * @param currentAdmin The currentAdmin to se as the current
     */
    public void setAdmin(Admin currentAdmin) {
        // Fetch all profiles when the currentAdmin is set, saves time by not performing this when the admin fxml is
        // initialised but now


        this.currentAdmin = currentAdmin;
        updateDisplay();
    }

    /**
     * Updates all the displayed TextFields to the values
     * from the current currentAdmin
     */
    private void updateDisplay() {
        System.out.print(currentAdmin);
        userDisplayText.setText("Welcome " + currentAdmin.getName());
        adminNameLabel.setText("Name: " + currentAdmin.getName());
        adminAddressLabel.setText("Address: " + currentAdmin.getWorkAddress());
        staffIDLabel.setText(Long.toString(currentAdmin.getStaffID()));
    }

    private void refreshLatestProfiles() {
        // Initialise observable lists that correlate to the three TableViews
        currentUsers = Main.users;
        currentClinicians = Main.clinicians;
        currentAdmins = Main.admins;
    }

    /**
     * Logs out the currentAdmin. The user is asked if they're sure they want to log out, if yes,
     * all open user windows spawned by the currentAdmin are closed and the main scene is returned to the logout screen.
     */
    public void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("Are you sure would like to log out? ");
        alert.setContentText("Logging out without saving loses your non-saved data.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            for(Stage userWindow: Main.getCliniciansUserWindows()){
                userWindow.close();
            }
            Main.setScene(TFScene.login);
            Main.clearUserScreen();
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
        dialog.setTitle("View Account Settings");
        dialog.setHeaderText("In order to view your account settings, \nplease enter your login details.");
        dialog.setContentText("Please enter your password:");

        Optional<String> password = dialog.showAndWait();
        if(password.isPresent()){ //Ok was pressed, Else cancel
            if(password.get().equals(currentAdmin.getPassword())){
                try {
                    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/clinicianAccountSettings.fxml"));
                    Stage stage = new Stage();
                    stage.setTitle("Account Settings");
                    stage.setScene(new Scene(root, 290, 350));
                    stage.initModality(Modality.APPLICATION_MODAL);

                    Main.setCurrentClinicianForAccountSettings(currentAdmin);

                    stage.showAndWait();
                } catch (Exception e) {
                    System.out.println("here");
                    e.printStackTrace();
                }
            }else{ // Password incorrect
                Main.createAlert(Alert.AlertType.INFORMATION, "Incorrect",
                        "Incorrect password. ", "Please enter the correct password to view account settings").show();
            }
        }
    }



    /**
     * Updates the current clinicians attributes to
     * reflect those of the values in the displayed TextFields
     */
    public void updateAdminPopUp() {
        addAdminToUndoStack(currentAdmin);
        System.out.println("Name=" + currentAdmin.getName() + ", Address=" + currentAdmin.getWorkAddress());


        // Create the custom dialog.
        Dialog<ArrayList<String>> dialog = new Dialog<>();
        dialog.setTitle("Update Admin");
        dialog.setHeaderText("Update Admin Details");

        dialog.getDialogPane().getStylesheets().add(Main.getDialogStyle());

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
        adminName.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButton.setDisable(newValue.trim().isEmpty());
        });

        // Do some validation (using the Java 8 lambda syntax).
        adminAddress.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButton.setDisable(newValue.trim().isEmpty());
        });


        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> adminName.requestFocus());

        // Convert the result to a diseaseName-dateOfDiagnosis-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                String newName;
                String newAddress;

                if(adminName.getText().equals("")) {
                    newName = currentAdmin.getName();
                } else {
                    newName = adminName.getText();
                }

                if(adminAddress.getText().equals("")) {
                    newAddress = currentAdmin.getWorkAddress();
                } else {
                    newAddress = adminAddress.getText();
                }

                return new ArrayList<String>(Arrays.asList(newName, newAddress));
            }
            return null;
        });

        Optional<ArrayList<String>> result = dialog.showAndWait();

        result.ifPresent(newAdminDetails -> {
            System.out.println("Name=" + newAdminDetails.get(0) + ", Address=" + newAdminDetails.get(1));
            currentAdmin.setName(newAdminDetails.get(0));
            currentAdmin.setWorkAddress(newAdminDetails.get(1));
            save();
            updateDisplay();

        });
    }

    /**
     * Saves the currentAdmin ArrayList to a JSON file
     */
    public void save(){
        IO.saveUsers(IO.getAdminPath(), LoginType.ADMIN);
        IO.saveUsers(IO.getUserPath(), LoginType.USER);
        IO.saveUsers(IO.getClinicianPath(), LoginType.CLINICIAN);
    }

    /**
     * Closes the application
     */
    public void close(){
        Platform.exit();
    }

    /**
     * Changes the focus to the pane when pressed
     */
    public void requestFocus() { background.requestFocus(); }

    /**
     * The main clincian undo function. Called from the button press, reads from the undo stack and then updates the GUI accordingly.
     */
    public void undo(){
        // TODO implement undo
        /*currentAdmin = clinicianUndo(currentAdmin);
        updateDisplay();
        redoWelcomeButton.setDisable(false);

        if (clinicianUndoStack.isEmpty()){
            undoWelcomeButton.setDisable(true);
        }*/
    }

    /**
     * The main clincian redo function. Called from the button press, reads from the redo stack and then updates the GUI accordingly.
     */
    public void redo(){
        // TODO implement redo
        /*currentAdmin = clinicianRedo(currentAdmin);
        updateDisplay();
        undoWelcomeButton.setDisable(false);
        if(clinicianRedoStack.isEmpty()){
            redoWelcomeButton.setDisable(true);
        }*/
    }

    /**
     * Reads the top element of the undo stack and removes it, while placing the current currentAdmin in the redo stack.
     * Then returns the currentAdmin from the undo stack.
     * @param oldAdmin the admin being placed in the redo stack.
     * @return the previous iteration of the currentAdmin object.
     */
    public Admin adminUndo(Admin oldAdmin) {
        if (adminUndoStack != null) {
            Admin newAdmin = adminUndoStack.get(adminUndoStack.size() - 1);
            adminUndoStack.remove(adminUndoStack.size() - 1);
            adminRedoStack.add(oldAdmin);
            return newAdmin;
        }
        return null;
    }

    /**
     * Creates a deep copy of the current currentAdmin and adds that copy to the undo stack. Then updates the GUI button to be usable.
     * @param admin the currentAdmin object being copied.
     */
    public void addAdminToUndoStack(Admin admin) {
        Admin prevAdmin = new Admin(admin);
        adminUndoStack.add(prevAdmin);
        if (undoWelcomeButton.isDisable()) {
            undoWelcomeButton.setDisable(false);
        }
    }

    /**
     * Pops the topmost currentAdmin object from the redo stack and returns it, while adding the provided currentAdmin object to the undo stack.
     * @param newAdmin the admin being placed on the undo stack.
     * @return the topmost currentAdmin object on the redo stack.
     */
    public Admin adminRedo(Admin newAdmin){
        if (adminRedoStack != null) {
            Admin oldAdmin = adminRedoStack.get(adminRedoStack.size() - 1);
            addAdminToUndoStack(newAdmin);
            adminRedoStack.remove(adminRedoStack.size() - 1);
            return oldAdmin;
        } else{
            return null;
        }
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

    } /*

    *//**
     * Updates the list of users found from the search
     *//*
    public void updateFoundUsers(){
        usersFound = Main.getUsersByNameAlternative(searchNameTerm);

        //Add in check for region

        if(!searchRegionTerm.equals("")) {
            ArrayList<User> newUsersFound = Main.getUsersByRegionAlternative(searchRegionTerm);
            usersFound.retainAll(newUsersFound);

        }

        //Add in check for age

        if(!searchAgeTerm.equals("")) {
            ArrayList<User> newUsersFound = Main.getUsersByAgeAlternative(searchAgeTerm);
            usersFound.retainAll(newUsersFound);
        }


        //Add in check for gender

        if(searchGenderTerm != null) {
            ArrayList<User> newUsersFound = new ArrayList<>();
            for(User user: usersFound) {
                if(searchGenderTerm.equals(user.getGender().toString()) && (user.getGender() != null)) {
                    newUsersFound.add(user);
                }
            }
            usersFound = newUsersFound;
        }

        //Add in check for organ

        if(searchOrganTerm != null) {
            ArrayList<User> newUsersFound = new ArrayList<>();
            for(User user: usersFound) {
                if((user.getOrgans().contains(Organ.parse(searchOrganTerm))) && (user.getOrgans().size() != 0)) {
                    newUsersFound.add(user);
                }
            }
            usersFound = newUsersFound;
        }

        //Add in check for user type

        if(searchUserTypeTerm != null) {
            ArrayList<User> newUsersFound = new ArrayList<>();
            for(User user: usersFound) {
                if(searchUserTypeTerm.equals(user.getType()) && (user.getType() != null)) {
                    newUsersFound.add(user);
                }
            }
            usersFound = newUsersFound;
        }

        users = FXCollections.observableArrayList(usersFound);
        populateNResultsComboBox(usersFound.size());
        //displayPage(resultsPerPage);
    }

    public void populateNResultsComboBox(int numberOfSearchResults){
        numberOfResutsToDisplay.getItems().clear();
        String firstPage = "First page";
        numberOfResutsToDisplay.getItems().add(firstPage);
        numberOfResutsToDisplay.getSelectionModel().select(firstPage);
        if(numberOfSearchResults > resultsPerPage && numberOfSearchResults < numberXofResults){
            numberOfResutsToDisplay.getItems().add("All " + numberOfSearchResults+" results");
        }else if(numberOfSearchResults > resultsPerPage && numberOfSearchResults > numberXofResults){
            numberOfResutsToDisplay.getItems().add("Top "+numberXofResults+" results");
            numberOfResutsToDisplay.getItems().add("All " + numberOfSearchResults+" results");
        }
    }
*/



    /**
     * Sets the User Attribute pane as the visible pane
     */
    public void showMainPane() {
        mainPane.setVisible(true);
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
        userAgeTableColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
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


        /*profileSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            page = 1;
            searchNameTerm = newValue;
            updateFoundUsers();
        });

        clinicianRegionField.textProperty().addListener((observable, oldValue, newValue) -> {
            page = 1;
            searchRegionTerm = newValue;
            updateFoundUsers();
        });

        clinicianAgeField.textProperty().addListener((observable, oldValue, newValue) -> {
            page = 1;
            searchAgeTerm = newValue;
            updateFoundUsers();
        });

        clinicianGenderComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            page = 1;
            if(newValue == null) {
                searchGenderTerm = null;

            } else {
                searchGenderTerm = newValue.toString();
            }
            updateFoundUsers();

        });

        clinicianUserTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            page = 1;
            if(newValue == null) {
                searchUserTypeTerm = null;

            } else {
                searchUserTypeTerm = newValue.toString();
            }
            updateFoundUsers();

        });

        clinicianOrganComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            page = 1;
            if(newValue == null) {
                searchOrganTerm = null;

            } else {
                searchOrganTerm = newValue.toString();
            }
            updateFoundUsers();

        });*/


        System.out.println("AdminController: Setting main controller of myself");
        Main.setAdminController(this);


        /**
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
                    if (!row.isEmpty() && event.getClickCount()==2) {
                        System.out.println(row.getItem());
                        Stage stage = new Stage();
                        stage.setMinHeight(550);
                        stage.setMinWidth(650);

                        Main.addCliniciansUserWindow(stage);
                        stage.initModality(Modality.NONE);

                        try{
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/userWindow.fxml"));
                            Parent root = (Parent) loader.load();
                            UserWindowController userWindowController = loader.getController();
                            userWindowController.setTitleBar(stage);
                            Main.setCurrentUser(row.getItem());

                            String text = History.prepareFileStringGUI(row.getItem().getId(), "view");
                            History.printToFile(IO.streamOut, text);

                            userWindowController.populateUserFields();
                            userWindowController.populateHistoryTable();
                            userWindowController.showWaitingListButton();
                            Main.controlViewForClinician();

                            Scene newScene = new Scene(root, 900, 575);
                            stage.setScene(newScene);
                            stage.show();
                            userWindowController.setAsChildWindow();
                        } catch (IOException | NullPointerException e) {
                            System.err.println("Unable to load fxml or save file.");
                            e.printStackTrace();
                            Platform.exit();
                        }
                    }
                });
                return row;
            }
        });

        userTableView.refresh();
    }

    /**
     * calls the transplantWaitingList controller and displays it.
     * also refreshes the waitinglist table data
     */
    public void transplantWaitingList() {
        Main.getTransplantWaitingListController().updateTransplantList();
        //background.setVisible(false);
        Main.setScene(TFScene.transplantList);
    }

    @FXML
    private void createAdmin(){
        Stage stage = new Stage();
        stage.setMinHeight(Main.mainWindowMinHeight);
        stage.setMinWidth(Main.mainWindowMinWidth);
        stage.setHeight(Main.mainWindowPrefHeight);
        stage.setWidth(Main.mainWindowPrefWidth);
        stage.initModality(Modality.NONE);

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/createAdmin.fxml"));
            Parent root = (Parent) loader.load();
            CreateAdminController createAdminController = loader.getController();
            Scene newScene = new Scene(root, 900, 575);
            stage.setScene(newScene);
            Admin newAdmin = createAdminController.showAndWait(stage);
            System.out.println(newAdmin);
            if(newAdmin != null){
                Main.admins.add(newAdmin);
                IO.saveUsers(IO.getAdminPath(), LoginType.ADMIN);
            }
        } catch (IOException e) {
            System.err.println("Unable to load fxml or save file.");
            e.printStackTrace();
            Platform.exit();
        }
    }

    @FXML
    private void createClinician(){
        Stage stage = new Stage();
        stage.setMinHeight(Main.mainWindowMinHeight);
        stage.setMinWidth(Main.mainWindowMinWidth);
        stage.setHeight(Main.mainWindowPrefHeight);
        stage.setWidth(Main.mainWindowPrefWidth);
        stage.initModality(Modality.NONE);

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/createClinician.fxml"));
            Parent root = (Parent) loader.load();
            CreateClinicianController createClinicianController = loader.getController();

            Scene newScene = new Scene(root, 900, 575);
            stage.setScene(newScene);
            Clinician newClinician = createClinicianController.showAndWait(stage);
            System.out.println(newClinician);
            if(newClinician != null){
                Main.clinicians.add(newClinician);
                IO.saveUsers(IO.getClinicianPath(), LoginType.CLINICIAN);
            }
        } catch (IOException e) {
            System.err.println("Unable to load fxml or save file.");
            e.printStackTrace();
            Platform.exit();
        }
    }

    @FXML
    private void createUser(){
        Stage stage = new Stage();
        stage.setMinHeight(Main.mainWindowMinHeight);
        stage.setMinWidth(Main.mainWindowMinWidth);
        stage.setHeight(Main.mainWindowPrefHeight);
        stage.setWidth(Main.mainWindowPrefWidth);
        stage.initModality(Modality.NONE);

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/createAccount.fxml"));
            Parent root = (Parent) loader.load();
            CreateAccountController createAccountController = loader.getController();

            Scene newScene = new Scene(root, 900, 575);
            stage.setScene(newScene);
            User user = createAccountController.showAndWait(stage);
            System.out.println(user);
            if(user != null){
                Main.users.add(user);
                IO.saveUsers(IO.getUserPath(), LoginType.USER);
            }
        } catch (IOException e) {
            System.err.println("Unable to load fxml or save file.");
            e.printStackTrace();
            Platform.exit();
        }

    }

    @FXML
    private void delete(){
        switch(tableTabPane.getSelectionModel().getSelectedItem().getId()){
            case "usersTab":
                User user = userTableView.getSelectionModel().getSelectedItem();
                if(Main.createAlert(Alert.AlertType.CONFIRMATION, "Delete", "Delete " + user.getName() + "?", "Are you sure you want to delete this user?").showAndWait().get() == ButtonType.OK){
                    currentUsers.remove(user);
                }
                break;
            case "administratorsTab":
                Admin admin = adminTableView.getSelectionModel().getSelectedItem();
                if(admin.getName().equals("default_admin")){
                    Main.createAlert(Alert.AlertType.ERROR, "Forbidden", "Cannot delete default admin", "This account is protected and cannot be removed").showAndWait();
                }
                else if(Main.createAlert(Alert.AlertType.CONFIRMATION, "Delete", "Delete " + admin.getName() + "?", "Are you sure you want to delete this admin?").showAndWait().get() == ButtonType.OK){
                    currentAdmins.remove(admin);
                }
                break;
            case "cliniciansTab":
                Clinician clinician = clinicianTableView.getSelectionModel().getSelectedItem();
                if(Main.createAlert(Alert.AlertType.CONFIRMATION, "Delete", "Delete " + clinician.getName() + "?", "Are you sure you want to delete this clinician?").showAndWait().get() == ButtonType.OK){
                    currentClinicians.remove(clinician);
                }
                break;
        }
    }
}
