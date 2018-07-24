package seng302.GUI.Controllers.Admin;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import seng302.Generic.WindowManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialise output components
        currentHistoryIndex = 1;
        commandInputHistory = new ArrayList<>();
        commandInputHistory.add("TF > ");
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
        commandInputField.setText("TF > ");
        commandInputField.positionCaret(5);
        commandInputField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.startsWith("TF > ")) {
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
            return "TF > ";
        } else if (!up && currentHistoryIndex == commandInputHistory.size() - 1) {
            currentHistoryIndex = commandInputHistory.size();
            return "TF > ";
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
        if (!commandInputField.getText().equals("TF > ")) {
            capturedOutput.add(commandInputField.getText());

            String response = WindowManager.getDataManager().getGeneral().sendCommand(commandInputField.getText().substring(5), token);
            if(isInstruction(response)) {
                executeInstruction(response);
            }else{
                capturedOutput.add(response);
            }



            commandInputHistory.add(commandInputField.getText());
            currentHistoryIndex = commandInputHistory.size();
            commandInputField.setText("TF > ");
            commandInputField.positionCaret(5);
            commandOutputView.scrollTo(capturedOutput.size() - 1);
        }
    }


    public boolean isInstruction(String response){
        return Arrays.asList("CLEAR").contains(response);
    }

    public void executeInstruction(String response){
        switch (response) {
            case "CLEAR": capturedOutput.clear();
        }
    }
}
