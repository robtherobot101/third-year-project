package seng302.Controllers;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import seng302.Core.*;

import java.net.URL;
import java.util.*;


public class WaitingListController implements Initializable {
    @FXML
    private Button addOrgan;

    @FXML
    private Button removeOrgan;

    @FXML
    private TableView waitingList;
    @FXML
    private ComboBox organTypeComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setWaitingListController(this);
    }
}
