package seng302.GUI.Controllers.Clinician;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.http.client.HttpResponseException;
import seng302.Generic.Debugger;
import seng302.Generic.WindowManager;
import seng302.User.Clinician;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Class to handle all the logic for the Account Settings window.
 */
public class ClinicianSettingsController implements Initializable {

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
        Debugger.log(clinician);
        Debugger.log(clinician.getName());
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
//        for (Clinician user : DataManager.clinicians) {
//            if (user != clinician) {
//                if (!usernameField.getText().isEmpty() && usernameField.getText().equals(user.getUsername())) {
//                    errorLabel.setText("That username is already taken.");
//                    errorLabel.setVisible(true);
//                    return;
//                }
//            }
//        }
        int clinicianId = 0;
        try {
            // Display an error if the username is taken and the input has not been changed (The username can be taken by the clinician being modified).
            if (!WindowManager.getDataManager().getGeneral().isUniqueIdentifier(usernameField.getText()) && !(clinician.getUsername().equals(usernameField.getText()))) {
                errorLabel.setText("That username is already taken.");
                errorLabel.setVisible(true);
                return;
            }
        } catch (HttpResponseException e) {
            Debugger.error("Failed to check uniqueness of clinician with id: " + clinician.getStaffID());
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
            try {
                WindowManager.getDataManager().getClinicians().updateClinician(clinician);
            } catch (HttpResponseException e) {
                Debugger.error("Failed to update clinician with id: " + clinician.getStaffID());
            }
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
