package seng302.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import seng302.Core.Main;
import seng302.Core.TFScene;

import java.net.URL;
import java.util.ResourceBundle;

public class TransplantWaitingListController implements Initializable {


    public void returnView(){
        Main.setScene(TFScene.clinician);
    }

    /**
     * Closes the application
     */
    public void close(){
        Platform.exit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
