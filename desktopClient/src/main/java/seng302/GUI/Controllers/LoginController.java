package seng302.GUI.Controllers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.validator.routines.UrlValidator;
import seng302.GUI.TFScene;
import seng302.Generic.*;
import seng302.User.Admin;
import seng302.User.Attribute.ProfileType;
import seng302.User.Clinician;
import seng302.User.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;

/**
 * A controller class for the log in screen.
 */
public class LoginController implements Initializable {

    @FXML
    private TextField serverInput;
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

    public void login(){
        APIResponse response = WindowManager.getDatabase().loginUser(identificationInput.getText(), passwordInput.getText());
        System.out.println(response.getAsString());
        if (response.isValidJson()) {
            JsonObject serverResponse = response.getAsJsonObject();
            if (serverResponse.get("accountType") == null) {
                Debugger.log("LoginController: Logging in as user...");
                loadUser(gson.fromJson(serverResponse, User.class));
            } else if (serverResponse.get("accountType").getAsString().equals("CLINICIAN")) {
                Debugger.log("LoginController: Logging in as clinician...");
                loadClinician(gson.fromJson(serverResponse, Clinician.class));
            } else if (serverResponse.get("accountType").getAsString().equals("ADMIN")) {
                Debugger.log("LoginController: Logging in as admin...");
                loadAdmin(gson.fromJson(serverResponse, Admin.class));
            } else {
                errorMessage.setText("Username/email and password combination not recognized.");
                errorMessage.setVisible(true);
            }
        } else {
            errorMessage.setText("Username/email and password combination not recognized.");
            errorMessage.setVisible(true);
        }
    }

    private void loadUser(User user) {
        User matched = SearchUtils.getUserById(user.getId());
        WindowManager.setCurrentUser(matched);
        WindowManager.setScene(TFScene.userWindow);
        resetScene();
    }

    private void loadClinician(Clinician clinician) {
        //Add all users from Database
        DataManager.users.clear();
        try{
            DataManager.clearUsers();
            DataManager.addAllUsers(WindowManager.getDatabase().getAllUsers());
            WindowManager.getDatabase().refreshUserWaitinglists();
        } catch(Exception e) {
            e.printStackTrace();
        }

        WindowManager.setClinician(clinician);
        WindowManager.setScene(TFScene.clinician);
        resetScene();
    }

    private void loadAdmin(Admin admin) {
        DataManager.users.clear();
        DataManager.clinicians.clear();
        DataManager.admins.clear();
        try{
            DataManager.addAllUsers(WindowManager.getDatabase().getAllUsers());
            DataManager.clinicians.addAll(WindowManager.getDatabase().getAllClinicians());
            DataManager.admins.addAll(WindowManager.getDatabase().getAllAdmins());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        WindowManager.setAdmin(admin);
        WindowManager.setScene(TFScene.admin);
        resetScene();
    }

    private void resetScene(){
        identificationInput.setText("");
        passwordInput.setText("");
        loginButton.setDisable(true);
        errorMessage.setVisible(false);
    }

    public void testConnection(){
        APIServer server = new APIServer("http://" + serverInput.getText());
        if(server.testConnection().equals("1")){
            WindowManager.createAlert(Alert.AlertType.INFORMATION, "Connection successful", "Success", "Successfully connected to the server").showAndWait();
        }
        else{
            WindowManager.createAlert(Alert.AlertType.WARNING, "Warning", "Connection failed", "Unable to establish connection to server").showAndWait();
        }
    }

    private boolean connectServer(String url){
        UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
        if (urlValidator.isValid(url)) {
            APIServer server = new APIServer(url);
            System.out.println("URL is valid");
            server.testConnection();
            return true;
        }
        else{
            errorMessage.setText("Invalid URL given");
            errorMessage.setVisible(true);
            return false;
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
        gson = new Gson();
        WindowManager.setLoginController(this);
        requestFocus();
        identificationInput.textProperty().addListener((observable, oldValue, newValue) ->
                loginButton.setDisable(identificationInput.getText().isEmpty() || passwordInput.getText().isEmpty()));
        passwordInput.textProperty().addListener((observable, oldValue, newValue) ->
                loginButton.setDisable(identificationInput.getText().isEmpty() || passwordInput.getText().isEmpty()));
    }
}
