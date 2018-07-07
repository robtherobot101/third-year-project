package seng302.GUI.Controllers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
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

/*    *//**
     * Attempts to log in based on the information currently provided by the user. Provides appropriate feedback if log in fails.
     *//*
    public void login() {
        boolean identificationMatched = false;
        ProfileType typeMatched = null;

        // Try to connect to the given server
        if(!connectServer("http://" + serverInput.getText())) return;

        // Check for a user match
        User currentUser = null;

        *//*      Here is some skeleton code of what I think would work for Login with the server.
                I don't know enough about the whole system to go ahead with it, and have been looking at Apaches HTTPClient as well
                Also it doesn't work properly atm - sends a GET instead of a POST.
                Something to discuss at standups!
                Sourced some stuff from https://tinyurl.com/y7uf24be
                - Jono

        try {
            String url = ("http://" + serverInput.getText() + "/login");
            URL obj = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) obj.openConnection();


            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            String urlParameters = "usernameEmail=" + identificationInput.getText() + "&password=" + passwordInput.getText();
            System.out.println(urlParameters);

            connection.setDoOutput(true); // Triggers POST.
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            output.writeBytes(urlParameters);
            output.flush();
            output.close();

            System.out.println("\nSending 'POST' request to URL : " + url);


            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        *//*

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
    }*/

    public void login(){
        Response response = WindowManager.getDatabase().loginUser(identificationInput.getText(), passwordInput.getText());
        System.out.println(response.getAsString());
        if(response.isValidJson()) {
            login(response.getAsJsonObject());
        }else {
            errorMessage.setText("Username/email and password combination not recognized.");
            errorMessage.setVisible(true);
        }
    }

    private void login(JsonObject serverResponse) {
        boolean identificationMatched = false;
        ProfileType typeMatched = null;

        User currentUser = null;
        Clinician currentClinician = null;
        Admin currentAdmin = null;
        if(serverResponse.get("accountType") == null){
            currentUser = gson.fromJson(serverResponse, User.class);
            typeMatched = ProfileType.USER;
            identificationMatched = true;
            Debugger.log("LoginController: Logging in as user...");

        }else if(serverResponse.get("accountType").getAsString().equals("CLINICIAN")) {
            currentClinician = gson.fromJson(serverResponse, Clinician.class);
            typeMatched = ProfileType.CLINICIAN;
            identificationMatched = true;
            Debugger.log("LoginController: Logging in as clinician...");

        }else if(serverResponse.get("accountType").getAsString().equals("ADMIN")){
            currentAdmin = gson.fromJson(serverResponse, Admin.class);
            typeMatched = ProfileType.ADMIN;
            identificationMatched = true;
            Debugger.log("LoginController: Logging in as admin...");
        }

        if (identificationMatched) {
            resetScene();
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
        }
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
