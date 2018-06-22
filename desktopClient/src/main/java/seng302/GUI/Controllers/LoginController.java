package seng302.GUI.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import seng302.GUI.TFScene;
import seng302.Generic.*;
import seng302.User.Admin;
import seng302.User.Attribute.ProfileType;
import seng302.User.Clinician;
import seng302.User.User;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * A controller class for the log in screen.
 */
public class LoginController implements Initializable {

    @FXML
    private TextField identificationInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private Label errorMessage;
    @FXML
    private Button loginButton;
    @FXML
    private AnchorPane background;

    /**
     * Attempts to log in based on the information currently provided by the user. Provides appropriate feedback if log in fails.
     */
    public void login() {
        boolean identificationMatched = false;
        ProfileType typeMatched = null;

        // Check for a user match
        User currentUser = null;
        try {
            currentUser = WindowManager.getDatabase().loginUser(identificationInput.getText(), passwordInput.getText());
        } catch(SQLException e) {
            e.printStackTrace();
        }

        //Do a db search here
        if(currentUser != null) {
            typeMatched = ProfileType.USER;
            identificationMatched = true;
            Debugger.log("LoginController: Logging in as user...");
        }



        // Check for a clinician match
        Clinician currentClinician = null;
        try {
            currentClinician = WindowManager.getDatabase().loginClinician(identificationInput.getText(), passwordInput.getText());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Do a db search here
        if(currentClinician != null) {
            typeMatched = ProfileType.CLINICIAN;
            identificationMatched = true;
            Debugger.log("LoginController: Logging in as clinician...");
        }

        // Check for an admin match
        Admin currentAdmin = null;
        try {
            currentAdmin = WindowManager.getDatabase().loginAdmin(identificationInput.getText(), passwordInput.getText());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Do a db search here
        if(currentAdmin != null) {
            typeMatched = ProfileType.ADMIN;
            identificationMatched = true;
            Debugger.log("LoginController: Logging in as admin...");
        }



        if (identificationMatched) {
            //if (typeMatched != null) {
                //Reset scene to original state
                identificationInput.setText("");
                passwordInput.setText("");
                loginButton.setDisable(true);
                errorMessage.setVisible(false);

                switch (typeMatched) {
                    case USER:
                        WindowManager.setCurrentUser(currentUser);
                        WindowManager.setScene(TFScene.userWindow);
                        break;
                    case CLINICIAN:
                        //Add all users from Database
                        DataManager.users.clear();
                        try{
                            DataManager.users.addAll(WindowManager.getDatabase().getAllUsers());
                            WindowManager.getDatabase().refreshUserWaitinglists();
                        } catch(SQLException e) {
                            e.printStackTrace();
                        }

                        WindowManager.setClinician(currentClinician);
                        WindowManager.setScene(TFScene.clinician);
                        break;
                    case ADMIN:
                        DataManager.users.clear();
                        DataManager.clinicians.clear();
                        DataManager.admins.clear();
                        try{
                            DataManager.users.addAll(WindowManager.getDatabase().getAllUsers());
                            DataManager.clinicians.addAll(WindowManager.getDatabase().getAllClinicians());
                            DataManager.admins.addAll(WindowManager.getDatabase().getAllAdmins());
                        } catch(SQLException e) {
                            e.printStackTrace();
                        }
                        WindowManager.setAdmin(currentAdmin);
                        WindowManager.setScene(TFScene.admin);
                }
//            } else {
//                errorMessage.setText("Incorrect password.");
//                errorMessage.setVisible(true);
//            }
        } else {
            errorMessage.setText("Username/email and password combination not recognized.");
            errorMessage.setVisible(true);
        }
    }

    /**
     * Removes focus from all fields.
     */
    public void requestFocus() {
        background.requestFocus();
    }

    /**
     * Switches the displayed scene to the create account scene.
     */
    public void createAccount() {
        WindowManager.setScene(TFScene.createAccount);
    }

    /**
     * Sets the enter key press to attempt log in if sufficient information is present.
     */
    public void setEnterEvent() {
        WindowManager.getScene(TFScene.login).setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !loginButton.isDisable()) {
                login();
            }
        });
    }

    /**
     * Add listeners to enable/disable the login button based on information supplied
     *
     * @param location  Not used
     * @param resources Not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WindowManager.setLoginController(this);
        requestFocus();
        identificationInput.textProperty().addListener((observable, oldValue, newValue) ->
                loginButton.setDisable(identificationInput.getText().isEmpty() || passwordInput.getText().isEmpty()));
        passwordInput.textProperty().addListener((observable, oldValue, newValue) ->
                loginButton.setDisable(identificationInput.getText().isEmpty() || passwordInput.getText().isEmpty()));
    }
}
