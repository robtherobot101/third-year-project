package seng302.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import seng302.Core.Clinician;
import seng302.Core.Donor;
import seng302.Core.Main;
import seng302.Core.TFScene;
import seng302.Files.History;

import javax.xml.soap.Text;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Class to handle all the logic for the Account Settings window.
 */
public class AccountSettingsController implements Initializable {

    private Donor currentDonor;

    public void setCurrentDonor(Donor currentDonor) {
        this.currentDonor = currentDonor;
        donorNameLabel.setText(currentDonor.getName());

    }

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button updateButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label donorNameLabel;


    /**
     * Populates the account details inputs based on the current donor's attributes.
     */
    public void populateAccountDetails() {
        usernameField.setText(currentDonor.getUsername());
        emailField.setText(currentDonor.getEmail());
        passwordField.setText(currentDonor.getPassword());
    }

    /**
     * Function which is called when the user presses the 'Update' button, updating
     * the account details of the user based on the current inputs.
     */
    public void updateAccountDetails() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("Are you sure would like to update account settings ? ");
        alert.setContentText("The changes made will take place instantly.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){

            if(!usernameField.getText().equals(currentDonor.getUsername())) {
                currentDonor.setUsername(usernameField.getText());
            }
            if(!emailField.getText().equals(currentDonor.getEmail())) {
                currentDonor.setEmail(emailField.getText());

            }
            if(!passwordField.getText().equals(currentDonor.getPassword())) {
                currentDonor.setPassword(passwordField.getText());

            }

            String text = History.prepareFileStringGUI(currentDonor.getId(), "updateAccountSettings");
            History.printToFile(Main.streamOut, text);
            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.close();
            Main.setCurrentDonor(currentDonor);



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
        Main.setAccountSettingsController(this);

    }
}
