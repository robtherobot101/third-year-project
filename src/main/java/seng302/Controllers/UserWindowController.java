package seng302.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import seng302.Core.Donor;
import seng302.Core.Main;
import seng302.TUI.CommandLineInterface;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserWindowController implements Initializable {

    private Donor currentDonor;

    public void setCurrentDonor(Donor currentDonor) {
        this.currentDonor = currentDonor;
        userDisplayText.setText("Currently logged in as: " + currentDonor.getName());
    }

    @FXML
    private Label userDisplayText;
    @FXML
    private Pane attributesPane;
    @FXML
    private Pane historyPane;
    @FXML
    private Pane medicationsPane;
    @FXML
    private Pane welcomePane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setUserWindowController(this);
        welcomePane.setVisible(true);
        attributesPane.setVisible(false);
        historyPane.setVisible(false);
        medicationsPane.setVisible(false);
    }

    public void showHistoryPane() {
        welcomePane.setVisible(false);
        attributesPane.setVisible(false);
        historyPane.setVisible(true);
        medicationsPane.setVisible(false);

    }

    public void showMedicationsPane() {
        welcomePane.setVisible(false);
        attributesPane.setVisible(false);
        historyPane.setVisible(false);
        medicationsPane.setVisible(true);
    }

    public void showAttributesPane() {
        welcomePane.setVisible(false);
        attributesPane.setVisible(true);
        historyPane.setVisible(false);
        medicationsPane.setVisible(false);
    }

    public void stop() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("Are you sure would like to exit the application? ");
        alert.setContentText("Exiting without saving loses your non-saved data.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            System.out.println("Exiting GUI");
            Platform.exit();
        } else {
            alert.close();
        }

    }

    public void tester() {
        CommandLineInterface commandLineInterface = new CommandLineInterface();
        String[] helpString = new String[]{"Andy"};
        commandLineInterface.showHelp(helpString);
    }
}
