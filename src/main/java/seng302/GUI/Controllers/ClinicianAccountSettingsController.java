package seng302.GUI.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import seng302.User.Clinician;
import seng302.Generic.Main;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Class to handle all the logic for the Account Settings window.
 */
public class ClinicianAccountSettingsController implements Initializable {
    private Clinician currentClinician;

    public void setCurrentClinician(Clinician currentClinician) {
        this.currentClinician = currentClinician;
        //clinicianNameLabel.setText(currentClinician.getName());
    }

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button updateButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label clinicianNameLabel;


    /**
     * Populates the account details inputs based on the current user's attributes.
     */
    public void populateAccountDetails() {
        System.out.println(currentClinician);
        clinicianNameLabel.setText(currentClinician.getName());
        usernameField.setText(currentClinician.getUsername());
        passwordField.setText(currentClinician.getPassword());
    }

    /**
     * Function which is called when the user presses the 'Update' button, updating
     * the account details of the user based on the current inputs.
     */
    public void updateAccountDetails() {
        Alert alert = Main.createAlert(AlertType.CONFIRMATION, "Are you sure?", "Are you sure would like to update account settings ? ", "The changes made will take place instantly.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){

            currentClinician.setUsername(usernameField.getText());
            currentClinician.setPassword(passwordField.getText());


            //String text = History.prepareFileStringGUI(currentClinician.getId(), "updateAccountSettings");
            //History.printToFile(Main.streamOut, text);

            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.close();


            //Very unsure about this
            Main.setClinician(currentClinician);

        } else {
            alert.close();
        }
    }

    /**
     * Function which closes the current stage upon the user pressing the 'cancel' button.
     */
    public void exit() {
        Stage stage = (Stage) updateButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setClincianAccountSettingsController(this);

    }
}
