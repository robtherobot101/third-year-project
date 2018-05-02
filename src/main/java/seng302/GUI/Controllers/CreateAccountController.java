package seng302.GUI.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import seng302.GUI.TFScene;
import seng302.Generic.History;
import seng302.Generic.IO;
import seng302.Generic.Main;
import seng302.User.User;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static seng302.Generic.IO.streamOut;

/**
 * A controller class for the create account screen.
 */
public class CreateAccountController implements Initializable {
    @FXML
    private TextField usernameInput, emailInput, passwordConfirmInput, firstNameInput, middleNamesInput, lastNameInput;
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

    /**
     * Switches the currently displayed scene to the log in screen.
     */
    public void returnToLogin() {
        Main.setScene(TFScene.login);
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
        for (User user: Main.users) {
            if (!usernameInput.getText().isEmpty() && usernameInput.getText().equals(user.getUsername())) {
                errorText.setText("That username is already taken.");
                errorText.setVisible(true);
                return;
            } else if (!emailInput.getText().isEmpty() && emailInput.getText().equals(user.getEmail())) {
                errorText.setText("There is already a user account with that email.");
                errorText.setVisible(true);
                return;
            }
        }
        if (!passwordInput.getText().equals(passwordConfirmInput.getText())) {
            errorText.setText("Passwords do not match");
            errorText.setVisible(true);
        } else if (dateOfBirthInput.getValue().isAfter(LocalDate.now())) {
            errorText.setText("Date of birth is in the future");
            errorText.setVisible(true);
        } else {
            errorText.setVisible(false);
            String username = usernameInput.getText().isEmpty() ? null : usernameInput.getText();
            String email = emailInput.getText().isEmpty() ? null : emailInput.getText();
            String[] middleNames = middleNamesInput.getText().isEmpty() ? new String[]{} : middleNamesInput.getText().split(",");
            User newUser = new User(firstNameInput.getText(), middleNames, lastNameInput.getText(),
                    dateOfBirthInput.getValue(), username, email, passwordInput.getText());
            Main.users.add(newUser);
            History.printToFile(streamOut, History.prepareFileStringGUI(newUser.getId(), "create"));
            History.printToFile(streamOut, History.prepareFileStringGUI(newUser.getId(), "login"));
            Main.setCurrentUser(newUser);
            IO.saveUsers(IO.getUserPath(), true);
            Main.setScene(TFScene.userWindow);
        }
    }

    /**
     * Enable/disable the create account button based on whether the required information is present or not.
     */
    private void checkRequiredFields() {
        createAccountButton.setDisable((usernameInput.getText().isEmpty() && emailInput.getText().isEmpty()) || firstNameInput.getText().isEmpty() ||
            passwordInput.getText().isEmpty() || passwordConfirmInput.getText().isEmpty() || dateOfBirthInput.getValue() == null);
    }

    /**
     * Sets the enter key press to attempt log in if sufficient information is present.
     */
    public void setEnterEvent() {
        Main.getScene(TFScene.createAccount).setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !createAccountButton.isDisable()) {
                createAccount();
            }
        });
    }

    /**
     * Add listeners to enable/disable the create account button based on information supplied
     * @param location Not used
     * @param resources Not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setCreateAccountController(this);
        usernameInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
        emailInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
        passwordInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
        passwordConfirmInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
        firstNameInput.textProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
        dateOfBirthInput.valueProperty().addListener((observable, oldValue, newValue) -> checkRequiredFields());
    }
}
