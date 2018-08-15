package seng302.gui.controllers;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import org.apache.http.client.HttpResponseException;
import seng302.gui.TFScene;
import seng302.generic.Debugger;
import seng302.generic.WindowManager;
import seng302.User.Admin;
import seng302.User.Clinician;
import seng302.User.User;

import java.net.URL;
import java.util.Map;
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

    private Gson gson;

    /**
     * logs in a user
     */
    public void login(){
        try {
            Map<Object, String> response = WindowManager.getDataManager().getGeneral().loginUser(identificationInput.getText(), passwordInput.getText());
            Object user = response.keySet().iterator().next();
            String token = response.values().iterator().next();
            if (user != null) {
                if (user instanceof User) {
                    Debugger.log("LoginController: Logging in as user...");
                    if (((User) user).getDateOfDeath() != null) {
                        errorMessage.setText("User logging in is deceased. Please consult a registered clinician.");
                        errorMessage.setVisible(true);
                    } else {
                        loadUser((User) user, token);
                    }
                } else if (user instanceof Admin) {
                    Debugger.log("LoginController: Logging in as admin...");
                    loadAdmin((Admin)user, token);
                } else if (user instanceof Clinician) {
                    Debugger.log("LoginController: Logging in as clinician...");
                    loadClinician((Clinician)user, token);
                }  else {
                    errorMessage.setText("Username/email and password combination not recognized.");
                    errorMessage.setVisible(true);
                }
            } else {
                errorMessage.setText("Username/email and password combination not recognized.");
                errorMessage.setVisible(true);
            }

        } catch (HttpResponseException e) {
            Debugger.error("Could not login. ");
        }
    }

    /**
     * loads a specific user
     * @param user the user to load
     * @param token the users token
     */
    private void loadUser(User user, String token) {
        WindowManager.setCurrentUser(user, token);
        WindowManager.setScene(TFScene.userWindow);
        resetScene();
    }

    /**
     * loads a specific clinician
     * @param clinician the clinician to load
     * @param token the users token
     */
    private void loadClinician(Clinician clinician, String token) {
        WindowManager.setCurrentClinician(clinician, token);
        WindowManager.setScene(TFScene.clinician);
        resetScene();
    }

    /**
     * loads a specific admin
     * @param admin the admin to load
     * @param token the users token
     */
    private void loadAdmin(Admin admin, String token) {
        WindowManager.setCurrentAdmin(admin, token);
        WindowManager.setScene(TFScene.admin);
        resetScene();
    }

    /**
     * resets the current scene
     */
    private void resetScene(){
        identificationInput.setText("");
        passwordInput.setText("");
        loginButton.setDisable(true);
        errorMessage.setVisible(false);
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
        gson = new Gson();
        WindowManager.setLoginController(this);
        requestFocus();
        identificationInput.textProperty().addListener((observable, oldValue, newValue) ->
                loginButton.setDisable(identificationInput.getText().isEmpty() || passwordInput.getText().isEmpty()));
        passwordInput.textProperty().addListener((observable, oldValue, newValue) ->
                loginButton.setDisable(identificationInput.getText().isEmpty() || passwordInput.getText().isEmpty()));
    }
}
