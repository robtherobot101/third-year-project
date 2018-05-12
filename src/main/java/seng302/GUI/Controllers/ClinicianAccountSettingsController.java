package seng302.GUI.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import seng302.Generic.DataManager;
import seng302.Generic.IO;
import seng302.Generic.WindowManager;
import seng302.User.Attribute.LoginType;
import seng302.User.Clinician;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Class to handle all the logic for the Account Settings window.
 */
public class ClinicianAccountSettingsController implements Initializable {

    @FXML
    private TextField usernameField, passwordField;
    @FXML
    private Button updateButton, cancelButton;
    @FXML
    private Label userNameLabel, errorLabel;
    @FXML
    private AnchorPane background;

    private Clinician clinician;

    public void setCurrentClinician(Clinician clinician) {
        this.clinician = clinician;
        if (clinician.getName() == null) {
            clinician.setName("Name not set");
        }
        System.out.println(clinician);
        System.out.println(clinician.getName());
        userNameLabel.setText("clinician: " + clinician.getName());
    }

    /**
     * Populates the account details inputs based on the current user's attributes.
     */
    public void populateAccountDetails() {
        userNameLabel.setText("clinician: " + clinician.getName());
        usernameField.setText(clinician.getUsername() != null ? clinician.getUsername() : "");
        passwordField.setText(clinician.getPassword());
    }

    /**
     * Function which is called when the user presses the 'Update' button, updating
     * the account details of the user based on the current inputs.
     */
    public void updateAccountDetails() {
        for (Clinician user : DataManager.clinicians) {
            if (user != clinician) {
                if (!usernameField.getText().isEmpty() && usernameField.getText().equals(user.getUsername())) {
                    errorLabel.setText("That username is already taken.");
                    errorLabel.setVisible(true);
                    return;
                }
            }
        }
        errorLabel.setVisible(false);
        Alert alert = WindowManager.createAlert(AlertType.CONFIRMATION, "Are you sure?", "Are you sure would like to update account settings ? ",
                "The changes made will take place instantly.");
        alert.getDialogPane().lookupButton(ButtonType.OK).setId("accountSettingsConfirmationOKButton");


        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            clinician.setUsername(usernameField.getText());
            clinician.setPassword(passwordField.getText());

            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.close();
            WindowManager.setClinician(clinician);
            IO.saveUsers(IO.getClinicianPath(), LoginType.CLINICIAN);
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
        updateButton.setDisable(usernameField.getText().isEmpty() || passwordField.getText().isEmpty());
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
        WindowManager.setClincianAccountSettingsController(this);
        usernameField.textProperty().addListener(((observable, oldValue, newValue) -> updateUpdateButtonState()));
        passwordField.textProperty().addListener(((observable, oldValue, newValue) -> updateUpdateButtonState()));
    }
}
