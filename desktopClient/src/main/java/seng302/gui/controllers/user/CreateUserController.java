package seng302.gui.controllers.user;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.http.client.HttpResponseException;
import seng302.gui.TFScene;
import seng302.generic.Debugger;
import seng302.generic.WindowManager;
import seng302.User.User;

import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * A controller class for the create account screen.
 */
public class CreateUserController implements Initializable {

    @FXML
    private TextField usernameInput;
    @FXML
    private TextField emailInput;
    @FXML
    private TextField passwordConfirmInput;
    @FXML
    private TextField firstNameInput;
    @FXML
    private TextField middleNamesInput;
    @FXML
    private TextField lastNameInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private DatePicker dateOfBirthInput;
    @FXML
    private Button createAccountButton;
    @FXML
    private Label errorText;
    @FXML
    private AnchorPane background;

    private User user;
    private Stage stage;

    public User showAndWait(Stage stage) {
        this.stage = stage;
        stage.showAndWait();
        return user;
    }

    /**
     * Switches the currently displayed scene to the log in screen.
     */
    public void returnToLogin() {
        // If we are creating from login window, return us to login
        if (background.getScene().getWindow() == WindowManager.getStage()) {
            WindowManager.setScene(TFScene.login);
        }
        // Otherwise close the stage and return us to wherever we were before
        else {
            stage.close();
        }
    }

    /**
     * Removes focus from all fields.
     */
    public void requestFocus() {
        background.requestFocus();
    }

    /**
     * Attempts to create a new user account based on the information currently provided by the user. Provides appropriate feedback if this fails.
     *
     * @return The created user
     */
    public User createAccount() {
        try {
            if (!WindowManager.getDataManager().getGeneral().isUniqueIdentifier(usernameInput.getText())) {
                errorText.setText("That username is already taken.");
                errorText.setVisible(true);
                return null;
            } else if(!WindowManager.getDataManager().getGeneral().isUniqueIdentifier(emailInput.getText())) {
                errorText.setText("There is already a user account with that email.");
                errorText.setVisible(true);
                return null;
            }
        } catch (HttpResponseException e) {
            Debugger.error("Failed to check uniqueness of new user.");
        }
        if (!passwordInput.getText().equals(passwordConfirmInput.getText())) {
            errorText.setText("Passwords do not match");
            errorText.setVisible(true);
            return null;
        } else if (dateOfBirthInput.getValue().isAfter(LocalDate.now())) {
            errorText.setText("Date of birth is in the future");
            errorText.setVisible(true);
            return null;
        } else {
            errorText.setVisible(false);
            String username = usernameInput.getText().isEmpty() ? null : usernameInput.getText();
            String email = emailInput.getText().isEmpty() ? null : emailInput.getText();
            String[] middleNames = middleNamesInput.getText().isEmpty() ? new String[]{} : middleNamesInput.getText().split(",");
            user = new User(firstNameInput.getText(), middleNames, lastNameInput.getText(),
                    dateOfBirthInput.getValue(), username, email, passwordInput.getText());
            user.addHistoryEntry("Created", "This profile was created.");
            user.addHistoryEntry("Logged in", "This profile was logged in to.");

            // If we are creating from the login screen
            if (background.getScene().getWindow() == WindowManager.getStage()) {
                try {
                    WindowManager.getDataManager().getUsers().insertUser(user);
                    Map<Object, String> response = WindowManager.getDataManager().getGeneral().loginUser(user.getUsername(), user.getPassword());
                    User fromResponse = (User)response.keySet().iterator().next();
                    String token = response.values().iterator().next();

                    WindowManager.setCurrentUser(fromResponse, token);
                    WindowManager.setScene(TFScene.userWindow);
                    WindowManager.resetScene(TFScene.createAccount);
                    return null;
                } catch(HttpResponseException e) {
                    Debugger.error("Failed to insert new user and log in.");
                }
            }
        }
        stage.close();
        return user;
    }

    /**
     * Enable/disable the create account button based on whether the required information is present or not.
     */
    private void checkRequiredFields() {
        createAccountButton.setDisable((usernameInput.getText().isEmpty() || emailInput.getText().isEmpty()) || firstNameInput.getText().isEmpty() ||
                passwordInput.getText().isEmpty() || passwordConfirmInput.getText().isEmpty() || dateOfBirthInput.getValue() == null);
    }

    /**
     * Sets the enter key press to attempt log in if sufficient information is present.
     */
    public void setEnterEvent() {
        WindowManager.getScene(TFScene.createAccount).setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !createAccountButton.isDisable()) {
                createAccount();
            }
        });
    }

    /**
     * Add listeners to enable/disable the create account button based on information supplied
     *
     * @param location  Not used
     * @param resources Not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WindowManager.setCreateUserController(this);
        usernameInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
        emailInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
        passwordInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
        passwordConfirmInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
        firstNameInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
        dateOfBirthInput.valueProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
    }
}
