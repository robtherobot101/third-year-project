package seng302.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import seng302.Core.Donor;
import seng302.Core.Main;
import seng302.TUI.CommandLineInterface;

import java.net.URL;
import java.util.ResourceBundle;

public class UserWindowController implements Initializable {

    private Donor currentDonor;

    public void setCurrentDonor(Donor currentDonor) {
        this.currentDonor = currentDonor;
        userDisplayText.setText("Currently logged in as: " + currentDonor.getName());
    }

    @FXML
    private Label userDisplayText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setUserWindowController(this);
    }

    public void tester() {
        CommandLineInterface commandLineInterface = new CommandLineInterface();
        String[] helpString = new String[]{"Andy"};
        commandLineInterface.showHelp(helpString);
    }
}
