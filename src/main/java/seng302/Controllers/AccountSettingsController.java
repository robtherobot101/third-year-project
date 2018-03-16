package seng302.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import seng302.Core.Clinician;
import seng302.Core.Main;
import seng302.Core.TFScene;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AccountSettingsController implements Initializable {
    private String username;
    private String password;
    private String email;

    public void update() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("You are about update your username and");
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            System.out.println("OK");
        } else {
            System.out.println("cancel");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setAccountSettingsContorller(this);
    }
}
