package seng302.GUI.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import seng302.GUI.CommandLineInterface;
import seng302.GUI.TFScene;
import seng302.Generic.WindowManager;

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

    private ArrayList<String> commandInputHistory;
    private int currentHistoryIndex;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialise output components
        currentHistoryIndex = 1;
        commandInputHistory = new ArrayList<>();
        commandInputHistory.add("");
        capturedOutput = FXCollections.observableArrayList();
        commandOutputView.setItems(capturedOutput);

        WindowManager.setAdminCliController(this);

        commandInputField.setOnKeyPressed(event ->  {
            if (event.getCode() == KeyCode.UP) {
                commandInputField.setText(getCommandFromHistory(true));
            } else if (event.getCode() == KeyCode.DOWN) {
                commandInputField.setText(getCommandFromHistory(false));
            }
        });

        // Instantiate a new CLI and pipe its output to our ObservableList
        commandLineInterface = new CommandLineInterface();
        commandLineInterface.setOutput(capturedOutput);
    }

    /**
     * Gets the correct previously inputted command
     * @param up whether it is getting an earlier entry
     * @return the previous command
     */
    private String getCommandFromHistory(boolean up) {
        if (commandInputHistory.isEmpty()) {
            return "";
        } else {
            return commandInputHistory.get(getCommandIndex(up));
        }
    }

    /**
     * Calculates the correct index of the command history
     * @param up whether it is getting an earlier entry
     * @return the correct index of the command within the commandInputHistory
     */
    private int getCommandIndex(boolean up) {
        if (up) {
            if (currentHistoryIndex == 0) {
                return 0;
            } else {
                currentHistoryIndex--;
            }
        } else {
            if (currentHistoryIndex == commandInputHistory.size() - 1) {
                return currentHistoryIndex;
            } else {
                currentHistoryIndex++;
            }
        }
        return currentHistoryIndex;
    }


    /**
     * Called when the enter key is pressed on the command input TextField
     */
    public void onEnter() {
        capturedOutput.add("TF > " + commandInputField.getText());
        commandLineInterface.readCommand(commandInputField.getText());
        commandInputHistory.add(commandInputField.getText());
        currentHistoryIndex = commandInputHistory.size();
        commandInputField.clear();
        commandOutputView.scrollTo(capturedOutput.size()-1);
    }
}
