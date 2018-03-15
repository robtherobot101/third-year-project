package seng302.Controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import seng302.Core.Donor;
import seng302.Core.Main;
import seng302.Core.TFScene;
import seng302.Files.History;

public class CreateAccountController implements Initializable {
    @FXML
    private TextField usernameInput;
    @FXML
    private TextField emailInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private TextField passwordConfirmInput;
    @FXML
    private TextField firstNameInput;
    @FXML
    private TextField middleNamesInput;
    @FXML
    private TextField lastNameInput;
    @FXML
    private DatePicker dateOfBirthInput;
    @FXML
    private Button createAccountButton;
    @FXML
    private Label errorText;
    @FXML
    private AnchorPane background;

    public void returnToLogin() {
        Main.setScene(TFScene.login);
    }

    public void requestFocus() {
        background.requestFocus();
    }

    public void createAccount() {
        if (passwordInput.getText().equals(passwordConfirmInput.getText())) {
            errorText.setVisible(false);
            String username = usernameInput.getText().isEmpty() ? null : usernameInput.getText();
            String email = emailInput.getText().isEmpty() ? null : emailInput.getText();
            String[] middleNames = middleNamesInput.getText().isEmpty() ? new String[]{} : middleNamesInput.getText().split(",");
            Donor newDonor = new Donor(firstNameInput.getText(), middleNames, lastNameInput.getText(),
                dateOfBirthInput.getValue(), username, email, passwordInput.getText());
            Main.donors.add(newDonor);
            String text = History.prepareFileStringGUI(newDonor.getId(), "create");
            History.printToFile(Main.streamOut, text);
        } else {
            errorText.setVisible(true);
        }
    }

    private void checkRequiredFields() {
        createAccountButton.setDisable((usernameInput.getText().isEmpty() && emailInput.getText().isEmpty()) || firstNameInput.getText().isEmpty() ||
            passwordInput.getText().isEmpty() || passwordConfirmInput.getText().isEmpty() || dateOfBirthInput.getValue() == null);
    }

    public void setEnterEvent() {
        Main.getScene(TFScene.createAccount).setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !createAccountButton.isDisable()) {
                createAccount();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setCreateAccountController(this);
        usernameInput.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRequiredFields();
        });
        emailInput.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRequiredFields();
        });
        passwordInput.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRequiredFields();
        });
        passwordConfirmInput.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRequiredFields();
        });
        firstNameInput.textProperty().addListener((observable, oldValue, newValue) -> {
            checkRequiredFields();
        });
        dateOfBirthInput.valueProperty().addListener((observable, oldValue, newValue) -> {
            checkRequiredFields();
        });
    }
}
