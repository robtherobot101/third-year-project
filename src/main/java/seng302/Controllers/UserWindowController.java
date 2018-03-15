package seng302.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import seng302.Core.Main;

import java.net.URL;
import java.util.ResourceBundle;

public class UserWindowController implements Initializable {

    @FXML
    private TextField nickyTest;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setUserWindowController(this);

        String tester = nickyTest.getText();

    }
}
