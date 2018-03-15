package seng302.Controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import seng302.Core.Clinician;
import seng302.Core.Donor;
import seng302.Core.Main;
import seng302.Core.TFScene;

public class LoginController implements Initializable {
    @FXML
    private TextField identificationInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private Label errorMessage;
    @FXML
    private Button loginButton;
    @FXML
    private AnchorPane background;

    public void login() {
        boolean identificationMatched = false;
        Donor currentDonor = null;
        for (Donor donor: Main.donors) {
            if (donor.getUsername() != null && donor.getUsername().equals(identificationInput.getText()) ||
                donor.getEmail() != null && donor.getEmail().equals(identificationInput.getText())) {
                identificationMatched = true;
                if (donor.getPassword().equals(passwordInput.getText())) {
                    currentDonor = donor;
                }
            }
        }

        for(Clinician clinician: Main.clinicians){
            if(clinician.getUsername() != null && clinician.getUsername().equals(identificationInput.getText())){
                identificationMatched = true;
                if(clinician.getPassword().equals(passwordInput.getText())){
                    Main.setClinician(clinician);
                    Main.setScene(TFScene.clinician);
                    loggedIn = true;
                }
            }
        }

        if (identificationMatched) {
            if (currentDonor != null) {
                //Reset scene to original state
                identificationInput.setText("");
                passwordInput.setText("");
                loginButton.setDisable(true);
                errorMessage.setVisible(false);
                Main.setCurrentDonor(currentDonor);
                Main.setScene(TFScene.userWindow);
            } else {
                errorMessage.setText("Incorrect password.");
                errorMessage.setVisible(true);
            }
        } else {
            errorMessage.setText("Username or email not recognized.");
            errorMessage.setVisible(true);
        }
    }

    public void requestFocus() {
        background.requestFocus();
    }

    public void createAccount() {
        Main.setScene(TFScene.createAccount);
    }

    public void setEnterEvent() {
        Main.getScene(TFScene.login).setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !loginButton.isDisable()) {
                login();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setLoginController(this);
        requestFocus();
        identificationInput.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(identificationInput.getText().isEmpty() || passwordInput.getText().isEmpty());
        });
        passwordInput.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(identificationInput.getText().isEmpty() || passwordInput.getText().isEmpty());
        });
    }
}
