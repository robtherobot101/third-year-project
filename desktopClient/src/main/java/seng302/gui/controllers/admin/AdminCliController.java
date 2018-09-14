package seng302.gui.controllers.admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import seng302.User.Attribute.ProfileType;
import seng302.User.Importers.ProfileReader;
import seng302.User.Importers.UserReaderJSON;
import seng302.User.User;
import seng302.generic.IO;
import seng302.generic.WindowManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class AdminCliController implements Initializable {
    @FXML
    private TextField commandInputField;
    @FXML
    private ListView<String> commandOutputView;

    private ObservableList<String> capturedOutput;

    private ArrayList<String> commandInputHistory;
    private int currentHistoryIndex;
    private String token;
    private String string = "TF > ";

    /**
     * sets the token to be used by the cli controller
     * @param token String the token to access and make changes to the database
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * starts the admin cli controller
     * @param location not used
     * @param resources not used
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialise output components
        currentHistoryIndex = 1;
        commandInputHistory = new ArrayList<>();
        commandInputHistory.add(string);
        capturedOutput = FXCollections.observableArrayList();
        commandOutputView.setItems(capturedOutput);

        commandInputField.setOnKeyPressed(event ->  {
            if (event.getCode() == KeyCode.UP) {
                String command = getCommandFromHistory(true);
                commandInputField.setText(command);
                Platform.runLater(() -> commandInputField.positionCaret(command.length()));
            } else if (event.getCode() == KeyCode.DOWN) {
                String command = getCommandFromHistory(false);
                commandInputField.setText(command);
            }
        });
        commandInputField.setText(string);
        commandInputField.positionCaret(5);
        commandInputField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.startsWith(string)) {
                commandInputField.setText(oldValue);
            }
        });

        // Instantiate a new CLI and pipe its output to our ObservableList
    }

    /**
     * Gets the correct previously inputted command
     * @param up whether it is getting an earlier entry
     * @return the previous command
     */
    private String getCommandFromHistory(boolean up) {
        if (commandInputHistory.isEmpty()) {
            return string;
        } else if (!up && currentHistoryIndex == commandInputHistory.size() - 1) {
            currentHistoryIndex = commandInputHistory.size();
            return string;
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
            currentHistoryIndex++;
        }
        return currentHistoryIndex;
    }


    /**
     * Called when the enter key is pressed on the command input TextField
     */
    public void onEnter() {
        if (!commandInputField.getText().equals(string)) {
            capturedOutput.add(commandInputField.getText());

            String response = WindowManager.getDataManager().getGeneral().sendCommand(commandInputField.getText().substring(5), token);
            if(isInstruction(response)) {
                executeInstruction(response);
            }else{
                capturedOutput.add(response);
            }

            commandInputHistory.add(commandInputField.getText());
            currentHistoryIndex = commandInputHistory.size();
            commandInputField.setText(string);
            commandInputField.positionCaret(5);
            commandOutputView.scrollTo(capturedOutput.size() - 1);


        }
    }

    /**
     * method to check if the given input is a valid command
     * @param response String given instruction
     * @return boolean if it is an instruction
     */
    private boolean isInstruction(String response){
        return response == "CLEAR";
    }

    /**
     * method to execute the given instruction
     * @param response the input command
     */
    private void executeInstruction(String response){
        if (response.equalsIgnoreCase("CLEAR")) {
            capturedOutput.clear();
        }
    }
}
