package seng302.GUI.Controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import seng302.User.Admin;
import seng302.User.Attribute.LoginType;
import seng302.User.Clinician;
import seng302.User.User;
import seng302.Generic.Main;
import seng302.GUI.TFScene;
import seng302.Generic.History;
import sun.rmi.runtime.Log;

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
        LoginType typeMatched = null;

        // Check for a user match
        User currentUser = null;
        for (User user: Main.users) {
            if (user.getUsername() != null && user.getUsername().equals(identificationInput.getText()) ||
                user.getEmail() != null && user.getEmail().equals(identificationInput.getText())) {
                identificationMatched = true;
                if (user.getPassword().equals(passwordInput.getText())) {
                    currentUser = user;
                    typeMatched = LoginType.USER;
                    String text = History.prepareFileStringGUI(user.getId(), "login");
                    History.printToFile(Main.streamOut, text);
                }
            }
        }

        // Check for a clinician match
        Clinician currentClinician = null;
        Admin currentAdmin = null;
        for (Clinician clinician: Main.clinicians) {
            if (clinician.getUsername() != null && clinician.getUsername().equals(identificationInput.getText())) {
                identificationMatched = true;
                if (clinician.getPassword().equals(passwordInput.getText())) {
                    System.out.println(clinician instanceof Admin);
                    if (clinician instanceof Admin) {
                        System.out.println("LoginController: Logging in as admin...");
                        currentAdmin = (Admin) clinician;
                        typeMatched = LoginType.ADMIN;
                    } else {
                        System.out.println("LoginController: Logging in as clinician...");
                        currentClinician = clinician;
                        typeMatched = LoginType.CLINICIAN;
                    }
                    // TODO write login of clinician to history
                }
            }

        }

        if (identificationMatched) {
            if (typeMatched != null) {
                //Reset scene to original state
                identificationInput.setText("");
                passwordInput.setText("");
                loginButton.setDisable(true);
                errorMessage.setVisible(false);

                switch (typeMatched) {
                    case USER:
                        Main.setCurrentUser(currentUser);
                        Main.setScene(TFScene.userWindow);
                        break;
                    case CLINICIAN:
                        Main.setClinician(currentClinician);
                        Main.setScene(TFScene.clinician);
                        break;
                    case ADMIN:
                        Main.setAdmin(currentAdmin);
                        System.out.println("LoginController: Setting scene to admin...");
                        Main.setScene(TFScene.admin);
                }
            } else {
                errorMessage.setText("Incorrect password.");
                errorMessage.setVisible(true);
            }
        } else {
            errorMessage.setText("Username or email not recognized.");
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
        Main.setScene(TFScene.createAccount);
    }

    /**
     * Sets the enter key press to attempt log in if sufficient information is present.
     */
    public void setEnterEvent() {
        Main.getScene(TFScene.login).setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !loginButton.isDisable()) {
                login();
            }
        });
    }

    /**
     * Add listeners to enable/disable the login button based on information supplied
     * @param location Not used
     * @param resources Not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setLoginController(this);
        requestFocus();
        identificationInput.textProperty().addListener((observable, oldValue, newValue) ->
                loginButton.setDisable(identificationInput.getText().isEmpty() || passwordInput.getText().isEmpty()));
        passwordInput.textProperty().addListener((observable, oldValue, newValue) ->
                loginButton.setDisable(identificationInput.getText().isEmpty() || passwordInput.getText().isEmpty()));
    }
}
