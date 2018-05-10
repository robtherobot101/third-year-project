package seng302.GUI.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import seng302.GUI.CommandLineInterface;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminCliController implements Initializable {
    @FXML
    private TextField commandInputField;
    @FXML
    private ListView<String> commandOutputView;

    private CommandLineInterface commandLineInterface;

    private ObservableList<String> capturedOutput;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialise output components
        capturedOutput = FXCollections.observableArrayList();
        commandOutputView.setItems(capturedOutput);

        // Instantiate a new CLI and pipe its output to our ObservableList
        commandLineInterface = new CommandLineInterface();
        commandLineInterface.setOutput(capturedOutput);
    }

    /**
     * Called when the enter key is pressed on the command input TextField
     */
    public void onEnter() {
        capturedOutput.add("TF > " + commandInputField.getText());
        commandLineInterface.readCommand(commandInputField.getText());
        commandInputField.clear();
        commandOutputView.scrollTo(capturedOutput.size()-1);
    }
}
