package seng302.GUI.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import seng302.Generic.IO;
import seng302.User.Attribute.LoginType;
import seng302.User.User;
import seng302.Generic.Main;
import seng302.Generic.History;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static seng302.Generic.IO.streamOut;

/**
 * Class to handle all the logic for the Account Settings window.
 */
public class AccountSettingsController implements Initializable {
    @FXML
    private TextField usernameField, emailField, passwordField;
    @FXML
    private Button updateButton, cancelButton;
    @FXML
    private Label userNameLabel, errorLabel;
    @FXML
    private AnchorPane background;

    private User currentUser;

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        userNameLabel.setText("user: " + currentUser.getName());
    }

    /**
     * Populates the account details inputs based on the current user's attributes.
     */
    public void populateAccountDetails() {
        userNameLabel.setText("user: " + currentUser.getName());
        usernameField.setText(currentUser.getUsername() != null ? currentUser.getUsername() : "");
        emailField.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        passwordField.setText(currentUser.getPassword());
    }

    /**
     * Function which is called when the user presses the 'Update' button, updating
     * the account details of the user based on the current inputs.
     */
    public void updateAccountDetails() {
        for (User user: Main.users) {
            if (user != currentUser) {
                if (!usernameField.getText().isEmpty() && usernameField.getText().equals(user.getUsername())) {
                    errorLabel.setText("That username is already taken.");
                    errorLabel.setVisible(true);
                    return;
                } else if (!emailField.getText().isEmpty() && emailField.getText().equals(user.getEmail())) {
                    errorLabel.setText("There is already a user account with that email.");
                    errorLabel.setVisible(true);
                    return;
                }
            }
        }
        errorLabel.setVisible(false);
        Alert alert = Main.createAlert(AlertType.CONFIRMATION, "Are you sure?", "Are you sure would like to update account settings ? ", "The changes made will take place instantly.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            currentUser.setUsername(usernameField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setPassword(passwordField.getText());

            String text = History.prepareFileStringGUI(currentUser.getId(), "updateAccountSettings");
            History.printToFile(streamOut, text);
            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.close();
            Main.setCurrentUser(currentUser);
            IO.saveUsers(IO.getUserPath(), LoginType.USER);
        } else {
            alert.close();
        }
    }

    /**
     * Removes focus from all fields.
     */
    public void requestFocus() {
        background.requestFocus();
    }

    /**
     * Function which closes the current stage upon the user pressing the 'cancel' button.
     */
    public void exit() {
        Stage stage = (Stage) updateButton.getScene().getWindow();
        stage.close();
    }

    private void updateUpdateButtonState() {
        updateButton.setDisable((usernameField.getText().isEmpty() && emailField.getText().isEmpty())|| passwordField.getText().isEmpty());
    }

    public void setEnterEvent() {
        updateButton.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !updateButton.isDisable()) {
                updateAccountDetails();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setAccountSettingsController(this);
        usernameField.textProperty().addListener(((observable, oldValue, newValue) -> updateUpdateButtonState()));
        passwordField.textProperty().addListener(((observable, oldValue, newValue) -> updateUpdateButtonState()));
        emailField.textProperty().addListener(((observable, oldValue, newValue) -> updateUpdateButtonState()));
    }
}
