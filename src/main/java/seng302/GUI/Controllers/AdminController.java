package seng302.GUI.Controllers;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import seng302.Generic.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import seng302.GUI.TFScene;
import seng302.User.Admin;
import seng302.User.Attribute.Gender;
import seng302.User.Attribute.LoginType;
import seng302.User.Attribute.Organ;
import seng302.User.Clinician;
import seng302.User.User;

/**
 * Class to control all the logic for the currentAdmin interactions with the application.
 */
public class AdminController implements Initializable {

    private Admin currentAdmin;

    private FadeTransition fadeIn = new FadeTransition(
            Duration.millis(1000)
    );

    @FXML
    private TabPane TableTabPane;

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


    private int resultsPerPage;
    private int numberXofResults;

    private int page = 1;
    private ArrayList<User> usersFound;

    private ArrayList<UserWindowController> userWindows = new ArrayList<UserWindowController>();

    private ArrayList<Clinician> clinicianUndoStack = new ArrayList<>();
    private ArrayList<Clinician> clinicianRedoStack = new ArrayList<>();


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
        staffIDLabel.setText(Long.toString(currentAdmin.getStaffID()));
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
    public void updateClinician() {
        addClinicianToUndoStack(currentAdmin);
        System.out.println("Name=" + currentAdmin.getName() + ", Address=" + currentAdmin.getWorkAddress() + ", Region=" + currentAdmin.getRegion());


        // Create the custom dialog.
        Dialog<ArrayList<String>> dialog = new Dialog<>();
        dialog.setTitle("Update Clinician");
        dialog.setHeaderText("Update Clinician Details");

        dialog.getDialogPane().getStylesheets().add(Main.getDialogStyle());

        // Set the button types.
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        TextField clinicianName = new TextField();
        clinicianName.setPromptText(currentAdmin.getName());
        clinicianName.setId("clinicianName");
        TextField clinicianAddress = new TextField();
        clinicianAddress.setId("clinicianAddress");
        clinicianAddress.setPromptText(currentAdmin.getWorkAddress());
        TextField clinicianRegion = new TextField();
        clinicianRegion.setId("clinicianRegion");
        clinicianRegion.setPromptText(currentAdmin.getRegion());

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
        clinicianName.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButton.setDisable(newValue.trim().isEmpty());
        });

        // Do some validation (using the Java 8 lambda syntax).
        clinicianAddress.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButton.setDisable(newValue.trim().isEmpty());
        });

        clinicianRegion.textProperty().addListener((observable, oldValue, newValue) -> {
            updateButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> clinicianName.requestFocus());

        // Convert the result to a diseaseName-dateOfDiagnosis-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                String newName;
                String newAddress;
                String newRegion;

                if(clinicianName.getText().equals("")) {
                    newName = currentAdmin.getName();
                } else {
                    newName = clinicianName.getText();
                }

                if(clinicianAddress.getText().equals("")) {
                    newAddress = currentAdmin.getWorkAddress();
                } else {
                    newAddress = clinicianAddress.getText();
                }

                if(clinicianRegion.getText().equals("")) {
                    newRegion = currentAdmin.getRegion();
                } else {
                    newRegion = clinicianRegion.getText();
                }

                return new ArrayList<String>(Arrays.asList(newName, newAddress, newRegion));
            }
            return null;
        });

        Optional<ArrayList<String>> result = dialog.showAndWait();

        result.ifPresent(newClinicianDetails -> {
            System.out.println("Name=" + newClinicianDetails.get(0) + ", Address=" + newClinicianDetails.get(1) + ", Region=" + newClinicianDetails.get(2));
            currentAdmin.setName(newClinicianDetails.get(0));
            currentAdmin.setWorkAddress(newClinicianDetails.get(1));
            currentAdmin.setRegion(newClinicianDetails.get(2));
            save();
            updateDisplay();

        });
    }

    /**
     * Saves the currentAdmin ArrayList to a JSON file
     */
    public void save(){
        IO.saveUsers(IO.getAdminPath(), LoginType.ADMIN);
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
     * @param oldClinician the clincian being placed in the redo stack.
     * @return the previous iteration of the currentAdmin object.
     */
    public Clinician clinicianUndo(Clinician oldClinician) {
        if (clinicianUndoStack != null) {
            Clinician newClinician = clinicianUndoStack.get(clinicianUndoStack.size() - 1);
            clinicianUndoStack.remove(clinicianUndoStack.size() - 1);
            clinicianRedoStack.add(oldClinician);
            return newClinician;
        }
        return null;
    }

    /**
     * Creates a deep copy of the current currentAdmin and adds that copy to the undo stack. Then updates the GUI button to be usable.
     * @param clinician the currentAdmin object being copied.
     */
    public void addClinicianToUndoStack(Clinician clinician) {
        Clinician prevClinician = new Clinician(clinician);
        clinicianUndoStack.add(prevClinician);
        if (undoWelcomeButton.isDisable()) {
            undoWelcomeButton.setDisable(false);
        }
    }

    /**
     * Pops the topmost currentAdmin object from the redo stack and returns it, while adding the provided currentAdmin object to the undo stack.
     * @param newClinician the clinican being placed on the undo stack.
     * @return the topmost currentAdmin object on the redo stack.
     */
    public Clinician clinicianRedo(Clinician newClinician){
        if (clinicianRedoStack != null) {
            Clinician oldClinician = clinicianRedoStack.get(clinicianRedoStack.size() - 1);
            addClinicianToUndoStack(newClinician);
            clinicianRedoStack.remove(clinicianRedoStack.size() - 1);
            return oldClinician;
        } else{
            return null;
        }
    }


    /**
     * Updates the ObservableList for the profile table
     */
    public void displayPage(int pageSize) {
        currentPage.clear();
        currentPage.addAll(getPage(pageSize));
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

    }

    /**
     * Updates the list of users found from the search
     */
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


    /**
     * Splits the sorted list of found users and returns a page worth
     * @return The sorted page of results
     */
    public ObservableList<User> getPage(int pageSize){
        int firstIndex = Math.max((page-1),0)*pageSize;
        int lastIndex = Math.min(users.size(), page*pageSize);
        if(lastIndex<firstIndex){
            System.out.println(firstIndex+" to "+lastIndex+ " is an illegal page");
            return FXCollections.observableArrayList(new ArrayList<User>());
        }
        return FXCollections.observableArrayList(new ArrayList(users.subList(firstIndex, lastIndex)));
    }

    /**
     * Sets the User Attribute pane as the visible pane
     */
    public void showMainPane() {
        mainPane.setVisible(true);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialise observable lists that correlate to the three TableViews
        currentUsers = FXCollections.observableArrayList(Main.users);
        currentClinicians = FXCollections.observableArrayList(Main.clinicians);
        currentAdmins = FXCollections.observableArrayList(Main.admins);

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






        resultsPerPage = 3;
        numberXofResults = 5;

        profileSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
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

        });




        

        numberOfResutsToDisplay.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){
                if(newValue.equals("First page")){
                    displayPage(resultsPerPage);
                }else if(((String)newValue).contains("Top")){
                    displayPage(numberXofResults);
                }else if (((String)newValue).contains("All")){
                    displayPage(usersFound.size());
                }
            }
        });

        fadeIn.setDelay(Duration.millis(1000));
        fadeIn.setFromValue(1.0);
        fadeIn.setToValue(0.0);
        fadeIn.setCycleCount(0);
        fadeIn.setAutoReverse(false);

        userTableView.setItems(currentPage);

        System.out.println("AdminController: Setting main controller of myself");
        Main.setAdminController(this);

        updateFoundUsers();

        userTableView.setItems(currentPage);

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
                IO.saveUsers(IO.getClinicianPath(), LoginType.USER);
            }
        } catch (IOException e) {
            System.err.println("Unable to load fxml or save file.");
            e.printStackTrace();
            Platform.exit();
        }

    }
}
