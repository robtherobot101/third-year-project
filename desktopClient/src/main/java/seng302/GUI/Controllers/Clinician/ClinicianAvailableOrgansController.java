package seng302.GUI.Controllers.Clinician;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import seng302.Generic.WindowManager;

import java.net.URL;
import java.util.ResourceBundle;


public class ClinicianAvailableOrgansController implements Initializable{

    @FXML
    AnchorPane organsPane;

    @FXML
    TableView organsTable;

    @FXML
    TableColumn organColumn, nameColumn, countdownColumn, dateOfDeathColumn, regionColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WindowManager.setClinicianAvailableOrgansController(this);
    }
}
