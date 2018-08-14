package seng302.GUI.Controllers.Admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.http.client.HttpResponseException;
import seng302.generic.Debugger;
import seng302.generic.WindowManager;
import seng302.User.Admin;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A controller class for the create admin screen.
 */
public class CreateAdminController implements Initializable {

    @FXML
    private TextField usernameInput, passwordConfirmInput, firstNameInput, middleNamesInput, lastNameInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private Button createAccountButton;
    @FXML
    private Label errorText;
    @FXML
    private AnchorPane background;

    private Admin admin;
    private Stage stage;

    /**
     * method to show the create admin controller
     *
     * @param stage the current stage to use
     * @return the created admin user
     */
    public Admin showAndWait(Stage stage) {
        this.stage = stage;
        stage.showAndWait();
        return admin;
    }

    /**
     * Closes the popup
     */
    public void cancel() {
        stage.close();
    }

    /**
     * Removes focus from all fields.
     */
    public void requestFocus() {
        background.requestFocus();
    }

    /**
     * Attempts to create a new user account based on the information currently provided by the user. Provides appropriate feedback if this fails.
     */
    public void createAccount() {
        try{
            if (!WindowManager.getDataManager().getGeneral().isUniqueIdentifier(usernameInput.getText())) {
                errorText.setText("That username is already taken.");
                errorText.setVisible(true);
            }
            else if (!passwordInput.getText().equals(passwordConfirmInput.getText())) {
                errorText.setText("Passwords do not match");
                errorText.setVisible(true);
            } else {
                errorText.setVisible(false);
                String username = usernameInput.getText();
                String name = firstNameInput.getText() + " " + middleNamesInput.getText() + " " + lastNameInput.getText();
                String password = passwordInput.getText();
                admin = new Admin(username, password, name);
                stage.close();
            }
        } catch (HttpResponseException e) {
            Debugger.error("Failed to check uniqueness of new admin.");
        }

    }

    /**
     * Enable/disable the create account button based on whether the required information is present or not.
     */
    private void checkRequiredFields() {
        createAccountButton.setDisable(usernameInput.getText().isEmpty() || firstNameInput.getText().isEmpty() ||
                passwordInput.getText().isEmpty() || passwordConfirmInput.getText().isEmpty());
    }

    /**
     * Add listeners to enable/disable the create account button based on information supplied
     *
     * @param location  Not used
     * @param resources Not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
        passwordInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
        passwordConfirmInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
        firstNameInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
    }

}
