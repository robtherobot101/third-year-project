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
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Class to handle all the logic for the Account Settings window.
 */
public class ClinicianSettingsController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button updateButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private AnchorPane background;

    private Clinician clinician;
    private String token;

    public void setCurrentClinician(Clinician clinician, String token) {
        this.clinician = clinician;
        this.token = token;
        if (clinician.getName() == null) {
            clinician.setName("Name not set");
        }
        Debugger.log(clinician);
        Debugger.log(clinician.getName());
        userNameLabel.setText("Clinician: " + clinician.getName());
        populateAccountDetails();
    }

    /**
     * Populates the account details inputs based on the current User's attributes.
     */
    public void populateAccountDetails() {
        userNameLabel.setText("Clinician: " + clinician.getName());
        usernameField.setText(clinician.getUsername() != null ? clinician.getUsername() : "");
        passwordField.setText(clinician.getPassword());
    }

    /**
     * Function which is called when the User presses the 'Update' button, updating
     * the account details of the User based on the current inputs.
     */
    public void updateAccountDetails() {
        try {
            // Display an error if the username is taken and the input has not been changed (The username can be taken by the Clinician being modified).
            if (!WindowManager.getDataManager().getGeneral().isUniqueIdentifier(usernameField.getText()) && !(clinician.getUsername().equals(usernameField.getText()))) {
                errorLabel.setText("That username is already taken.");
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
                Alert alert = WindowManager.createAlert(AlertType.CONFIRMATION, "Are you sure?", "Are you sure would like to update account settings ? ",
                        "The changes made will take place instantly.");
                alert.getDialogPane().lookupButton(ButtonType.OK).setId("accountSettingsConfirmationOKButton");


                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    clinician.setUsername(usernameField.getText());
                    clinician.setPassword(passwordField.getText());

                    Stage stage = (Stage) updateButton.getScene().getWindow();
                    stage.close();
                    WindowManager.setCurrentClinician(clinician, token);
                    try {
                        WindowManager.getDataManager().getClinicians().updateClinician(clinician, token);
                    } catch (HttpResponseException e) {
                        Debugger.error("Failed to update Clinician with id: " + clinician.getStaffID());
                    }
                } else {
                    alert.close();
                }
            }
        } catch (HttpResponseException e) {
            Debugger.error("Failed to check uniqueness of Clinician with id: " + clinician.getStaffID());
        }
    }

    /**
     * Removes focus from all fields.
     */
    public void requestFocus() {
        background.requestFocus();
    }

    /**
     * Function which closes the current stage upon the User pressing the 'cancel' button.
     */
    public void exit() {
        Stage stage = (Stage) updateButton.getScene().getWindow();
        stage.close();
    }

    /**
     * updates the state of the update button
     */
    private void updateUpdateButtonState() {
        updateButton.setDisable(usernameField.getText().isEmpty() || passwordField.getText().isEmpty());
    }

    /**
     * checks if enter was pressed and if it was it updates the account details
     */
    public void setEnterEvent() {
        updateButton.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !updateButton.isDisable()) {
                updateAccountDetails();
            }
        });
    }

    /**
     * starts up the Clinician setting controller
     * @param location not used
     * @param resources not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WindowManager.setClinicianAccountSettingsController(this);
        usernameField.textProperty().addListener(((observable, oldValue, newValue) -> updateUpdateButtonState()));
        passwordField.textProperty().addListener(((observable, oldValue, newValue) -> updateUpdateButtonState()));
    }
}
