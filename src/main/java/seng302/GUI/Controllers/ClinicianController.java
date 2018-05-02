package seng302.GUI.Controllers;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
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
import org.controlsfx.control.StatusBar;
import seng302.GUI.StatusIndicator;
import seng302.GUI.TFScene;
import seng302.User.Attribute.Gender;
import seng302.User.Attribute.Organ;
import seng302.GUI.TitleBar;
import seng302.Generic.History;
import seng302.Generic.IO;
import seng302.Generic.Main;
import seng302.User.Clinician;
import seng302.User.User;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static seng302.Generic.IO.streamOut;

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
    private Button redoWelcomeButton;

    @FXML
    private GridPane mainPane;

    @FXML
    private ComboBox numberOfResutsToDisplay;

    @FXML
    private TextField clinicianRegionField;

    @FXML
    private MenuItem accountSettingsMenuItem;
    @FXML
    private ComboBox clinicianGenderComboBox;
    @FXML
    private TextField clinicianAgeField;
    @FXML
    private ComboBox clinicianUserTypeComboBox;
    @FXML
    private ComboBox clinicianOrganComboBox;

    //@FXML
    private StatusBar statusBar;

    private FadeTransition fadeIn = new FadeTransition(
            Duration.millis(1000)
    );

    private Clinician clinician;

    private StatusIndicator statusIndicator = new StatusIndicator();
    private TitleBar titleBar = new TitleBar();

    private int resultsPerPage;
    private int numberXofResults;

    private int page = 1;
    private ArrayList<User> usersFound;

    private LinkedList<Clinician> clinicianUndoStack = new LinkedList<>(), clinicianRedoStack = new LinkedList<>();

    private ObservableList<User> currentPage = FXCollections.observableArrayList();
    private ObservableList<Object> users;

    private String searchNameTerm = "";
    private String searchRegionTerm = "";
    private String searchGenderTerm = null;
    private String searchAgeTerm = "";
    private String searchOrganTerm = null;
    private String searchUserTypeTerm = null;

    public ClinicianController() {
        this.titleBar = new TitleBar();
        titleBar.setStage(Main.getStage());

    }

    public void setTitle(){
        titleBar.setTitle(clinician.getName(), "Clinician", null);
    }

    /**
     * Sets the current clinician
     * @param clinician The clinician to se as the current
     */
    public void setClinician(Clinician clinician) {
        this.clinician = clinician;
        if (clinician.getRegion() == null) {
            clinician.setRegion("");
        }
        if (clinician.getWorkAddress() == null) {
            clinician.setWorkAddress("");
        }
        updateDisplay();
    }

    /**
     * Updates all the displayed TextFields to the values
     * from the current clinician
     */
    public void updateDisplay() {
        titleBar.setTitle(clinician.getName(), "Clinician", null);
        System.out.print(clinician);
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
    private void edited(){
        titleBar.saved(false);
    }

//    /**
//     * Refreshes the results in the user profile table to match the values
//     * in the user ArrayList in Main
//     */
//    public void updateUserTable(){
//        updatePageButtons();
//        displayCurrentPage();
//        updateResultsSummary();
//    }

    /**
     * Logs out the clinician. The user is asked if they're sure they want to log out, if yes,
     * all open user windows spawned by the clinician are closed and the main scene is returned to the logout screen.
     */
    public void logout() {
        Alert alert = Main.createAlert(Alert.AlertType.CONFIRMATION, "Are you sure?", "Are you sure would like to log out? ",
                "Logging out without saving loses your non-saved data.");
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
        dialog.getDialogPane().getStylesheets().add(Main.getDialogStyle());
        dialog.setTitle("View Account Settings");
        dialog.setHeaderText("In order to view your account settings, \nplease enter your login details.");
        dialog.setContentText("Please enter your password:");

        Optional<String> password = dialog.showAndWait();
        if(password.isPresent()){ //Ok was pressed, Else cancel
            if(password.get().equals(clinician.getPassword())){
                try {
                    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/accountSettingsClinician.fxml"));
                    Stage stage = new Stage();
                    stage.setResizable(false);
                    stage.setTitle("Account Settings");
                    stage.setScene(new Scene(root, 290, 280));
                    stage.initModality(Modality.APPLICATION_MODAL);

                    Main.setCurrentClinicianForAccountSettings(clinician);
                    Main.setClinicianAccountSettingsEnterEvent();

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
    public void updateClinicianPopUp() {
        //addClinicianToUndoStack(clinician);
        System.out.println("Name=" + clinician.getName() + ", Address=" + clinician.getWorkAddress() + ", Region=" + clinician.getRegion());


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

                return new ArrayList<String>(Arrays.asList(newName, newAddress, newRegion));
            }
            return null;
        });

        Optional<ArrayList<String>> result = dialog.showAndWait();

        result.ifPresent(newClinicianDetails -> {
            System.out.println("Name=" + newClinicianDetails.get(0) + ", Address=" + newClinicianDetails.get(1) + ", Region=" + newClinicianDetails.get(2));
            clinician.setName(newClinicianDetails.get(0));
            clinician.setWorkAddress(newClinicianDetails.get(1));
            clinician.setRegion(newClinicianDetails.get(2));
            save();
            updateDisplay();

        });
    }

    public boolean updateClinician() {
//        if (clinician.getName().equals(nameInput.getText()) &&
//                clinician.getRegion().equals(regionInput.getText()) &&
//                clinician.getWorkAddress().equals(addressInput.getText())) {
//            return false;
//        } else {
//            clinician.setName(nameInput.getText());
//            clinician.setWorkAddress(addressInput.getText());
//            clinician.setRegion(regionInput.getText());
//            //updatedSuccessfully.setOpacity(1.0);
//            fadeIn.playFromStart();
//            titleBar.setTitle(clinician.getName(), "Clinician", null);
//            statusIndicator.setStatus("Updated clinician details", false);
//            return true;
//        }
        return true;
    }

    /**
     * Saves the clinician ArrayList to a JSON file
     */
    public void save() {
        Alert alert = Main.createAlert(Alert.AlertType.CONFIRMATION, "Are you sure?",
                "Are you sure would like to update the current clinician? ", "By doing so, the clinician will be updated with all filled in fields.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            IO.saveUsers(IO.getClinicianPath(), false);
        }
        alert.close();
    }

    /**
     * Closes the application
     */
    public void close() {
        Platform.exit();
    }

    /**
     * Changes the focus to the pane when pressed
     */
    public void requestFocus() { background.requestFocus(); }


//    /**
//     * Checks for any new updates when an attribute field loses focus, and appends to the attribute undo stack if there is new changes.
//     */
//    private void attributeFieldUnfocused() {
//        Clinician oldFields = new Clinician(clinician);
//        if (updateClinician()) {
//            clinicianUndoStack.add(new Clinician(oldFields));
//            clinicianRedoStack.clear();
//            undoWelcomeButton.setDisable(false);
//            redoWelcomeButton.setDisable(true);
//        }
//    }

    /**
     * The main clincian undo function. Called from the button press, reads from the undo stack and then updates the GUI accordingly.
     */
    public void undo() {
        clinicianRedoStack.add(new Clinician(clinician));
        clinician.copyFieldsFrom(clinicianUndoStack.getLast());
        clinicianUndoStack.removeLast();

        updateDisplay();
        redoWelcomeButton.setDisable(false);
        undoWelcomeButton.setDisable(clinicianUndoStack.isEmpty());
        titleBar.saved(false);
        statusIndicator.setStatus("Undid last action", false);
    }

    /**
     * The main clinician redo function. Called from the button press, reads from the redo stack and then updates the GUI accordingly.
     */
    public void redo() {
        clinicianUndoStack.add(new Clinician(clinician));
        clinician.copyFieldsFrom(clinicianRedoStack.getLast());
        clinicianRedoStack.removeLast();

        updateDisplay();
        undoWelcomeButton.setDisable(false);
        redoWelcomeButton.setDisable(clinicianRedoStack.isEmpty());
        titleBar.saved(false);
        statusIndicator.setStatus("Redid last action", false);
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

    /**
     * Function which populates the combo box for displaying a certain number of results based on the search fields.
     * @param numberOfSearchResults the number of results of the users found
     */
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

        clinicianGenderComboBox.setItems(FXCollections.observableArrayList(Gender.values()));
        clinicianUserTypeComboBox.setItems(FXCollections.observableArrayList(Arrays.asList("Donor", "Receiver", "Neither")));
        clinicianOrganComboBox.setItems(FXCollections.observableArrayList(Organ.values()));

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


        profileName.setCellValueFactory(new PropertyValueFactory<>("name"));
        profileUserType.setCellValueFactory(new PropertyValueFactory<>("type"));
        profileAge.setCellValueFactory(new PropertyValueFactory<>("ageString"));
        profileGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        profileRegion.setCellValueFactory(new PropertyValueFactory<>("region"));

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

        profileTable.setItems(currentPage);

        Main.setClinicianController(this);

        updateFoundUsers();

        profileTable.setItems(currentPage);

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
                        Stage stage = new Stage();
                        stage.setMinHeight(Main.mainWindowMinHeight);
                        stage.setMinWidth(Main.mainWindowMinWidth);
                        stage.setHeight(Main.mainWindowPrefHeight);
                        stage.setWidth(Main.mainWindowPrefWidth);

                        Main.addCliniciansUserWindow(stage);
                        stage.initModality(Modality.NONE);

                        try{
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/userWindow.fxml"));
                            Parent root = (Parent) loader.load();
                            UserWindowController userWindowController = loader.getController();
                            userWindowController.setTitleBar(stage);
                            Main.setCurrentUser(row.getItem());

                            String text = History.prepareFileStringGUI(row.getItem().getId(), "view");
                            History.printToFile(streamOut, text);

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

        profileTable.refresh();
    }

    /**
     * calls the transplantWaitingList controller and displays it.
     * also refreshes the waitinglist table data
     */
    public void transplantWaitingList() {
        Main.getTransplantWaitingListController().updateTransplantList();
        //background.setVisible(false);
        Main.setScene(TFScene.transplantList);
        titleBar.setTitle(clinician.getName(), "Clinician", "Transplant Waiting List");
    }
}