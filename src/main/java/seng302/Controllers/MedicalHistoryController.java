package seng302.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import seng302.Core.Disease;
import seng302.Core.Donor;
import seng302.Core.Main;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MedicalHistoryController implements Initializable {
    @FXML
    private DatePicker dateOfDiagnosisInput;
    @FXML
    private TextField newDiseaseTextField;

    private Donor currentDonor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setMedicalHistoryController(this);
    }

    @FXML
    public void addNewDisease() {
        if (newDiseaseTextField.getText() == "") {
            DialogWindowController.showWarning("Invalid Disease", "",
                    "Invalid disease name provided.");
            newDiseaseTextField.clear();
        } else if (dateOfDiagnosisInput.getValue().isAfter(LocalDate.now())) {
            DialogWindowController.showWarning("Invalid Disease", "",
                    "Diagnosis date occurs in the future.");
            dateOfDiagnosisInput.getEditor().clear();
        } else {
            Disease testDisease = new Disease(newDiseaseTextField.getText(), dateOfDiagnosisInput.getValue());
            currentDonor.getCurrentDiseases().add(testDisease);
        }
    }

    // TODO in populating tables
    public void populateDiseases(boolean isStartup) {
    }

    /**
     * Sets whether the control buttons are shown or not on the medications pane
     */
    public void setControlsShown(boolean shown) {
        //TODO toggle controls of finished window
    }

    /**
     * Function to set the current donor of this class to that of the instance of the application.
     * @param currentDonor The donor to set the current donor.
     */
    public void setCurrentDonor(Donor currentDonor) {
        this.currentDonor = currentDonor;
        System.out.println("Setting donor of Medical History pane...");
    }
}
