package seng302.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import seng302.Core.Donor;
import seng302.Core.Main;
import seng302.Core.TFScene;
import seng302.Files.History;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Class to handle all the logic for the Account Settings window.
 */
public class AccountSettingsController implements Initializable {
    @FXML
    private TextField usernameField, emailField, passwordField;
    @FXML
    private Button updateButton, cancelButton;
    @FXML
    private Label donorNameLabel, errorLabel;
    @FXML
    private AnchorPane background;

    private Donor currentDonor;

    public void setCurrentDonor(Donor currentDonor) {
        this.currentDonor = currentDonor;
        donorNameLabel.setText("donor: " + currentDonor.getName());
    }

    /**
     * Populates the account details inputs based on the current donor's attributes.
     */
    public void populateAccountDetails() {
        donorNameLabel.setText("donor: " + currentDonor.getName());
        usernameField.setText(currentDonor.getUsername() != null ? currentDonor.getUsername() : "");
        emailField.setText(currentDonor.getEmail() != null ? currentDonor.getEmail() : "");
        passwordField.setText(currentDonor.getPassword());
    }

    /**
     * Function which is called when the user presses the 'Update' button, updating
     * the account details of the user based on the current inputs.
     */
    public void updateAccountDetails() {
        for (Donor donor: Main.donors) {
            if (donor != currentDonor) {
                if (!usernameField.getText().isEmpty() && usernameField.getText().equals(donor.getUsername())) {
                    errorLabel.setText("That username is already taken.");
                    errorLabel.setVisible(true);
                    return;
                } else if (!emailField.getText().isEmpty() && emailField.getText().equals(donor.getEmail())) {
                    errorLabel.setText("There is already a donor account with that email.");
                    errorLabel.setVisible(true);
                    return;
                }
            }
        }
        errorLabel.setVisible(false);
        Alert alert = Main.createAlert(AlertType.CONFIRMATION, "Are you sure?", "Are you sure would like to update account settings ? ", "The changes made will take place instantly.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            currentDonor.setUsername(usernameField.getText());
            currentDonor.setEmail(emailField.getText());
            currentDonor.setPassword(passwordField.getText());

            String text = History.prepareFileStringGUI(currentDonor.getId(), "updateAccountSettings");
            History.printToFile(Main.streamOut, text);
            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.close();
            Main.setCurrentDonor(currentDonor);
            Main.saveUsers(Main.getDonorPath(), true);
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
