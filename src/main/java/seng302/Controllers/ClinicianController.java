package seng302.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import seng302.Core.Clinician;
import seng302.Core.Main;
import seng302.Core.TFScene;

import java.net.URL;
import java.util.ResourceBundle;

public class ClinicianController implements Initializable {

    private Clinician clinician;

    @FXML
    private Pane background;
    @FXML
    private TextField nameInput;
    @FXML
    private TextField staffIDInput;
    @FXML
    private TextField addressInput;
    @FXML
    private TextField regionInput;

    /**
     * Sets the current clinician
     * @param clinician The clinician to se as the current
     */
    public void setClinician(Clinician clinician) {
        this.clinician = clinician;
    }

    /**
     * Updates all the displayed TextFields to the values
     * from the current clinician
     */
    public void updateDisplay() {
        nameInput.setText(clinician.getName());
        staffIDInput.setText(clinician.getStaffID());
        addressInput.setText(clinician.getWorkAddress());
        regionInput.setText(clinician.getRegion());
    }

    /**
     * Updates the current clinicians attributes to
     * reflect those of the values in the displayed TextFields
     */
    public void updateClinician() {
        clinician.setName(nameInput.getText());
        clinician.setStaffID(staffIDInput.getText());
        clinician.setWorkAddress(addressInput.getText());
        clinician.setRegion(regionInput.getText());
        System.out.println("Updated to: " + clinician);
    }

    public void requestFocus() { background.requestFocus(); }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setClinicianController(this);
    }
}
