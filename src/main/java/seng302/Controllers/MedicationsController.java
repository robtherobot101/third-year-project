package seng302.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import seng302.Core.Donor;
import seng302.Core.Main;
import seng302.Core.Medication;

import java.net.URL;
import java.util.ResourceBundle;

public class MedicationsController implements Initializable {

    private Donor currentDonor;
    public Donor getCurrentDonor() {
        return currentDonor;
    }

    public void setCurrentDonor(Donor currentDonor) {
        this.currentDonor = currentDonor;
        System.out.println("HELLO");
        donorNameLabel.setText("Donor: " + currentDonor.getName());
//        donorUndoStack.clear();
//        donorRedoStack.clear();
//        undoButton.setDisable(true);
//        undoWelcomeButton.setDisable(true);
//        redoButton.setDisable(true);
//        redoWelcomeButton.setDisable(true);
//        bloodPressureLabel.setText("");
    }

    @FXML
    private Label donorNameLabel;
    @FXML
    private TextField newMedicationField;
    @FXML
    private Label interactionsLabel;
    @FXML
    private Label drugALabel;
    @FXML
    private Label drugBLabel;
    @FXML
    private ListView historyListView;
    @FXML
    private ListView currentListView;

    private ObservableList<String> historicItems = FXCollections.observableArrayList();
    private ObservableList<String> currentItems = FXCollections.observableArrayList();

    public void addNewMedication() {
        //TODO Add in check for autocomplete
        String medicationChoice = newMedicationField.getText();
        currentDonor.getCurrentMedications().add(new Medication(medicationChoice));
        populateMedications();
    }

    public void populateMedications() {

        //Populate table for current medications

        if(currentDonor.getCurrentMedications().size() != 0) {
            for(Medication medication: currentDonor.getCurrentMedications()) {
                currentItems.add(medication.getName());
            }
            currentListView.setItems(currentItems);
        } else {
            return;
        }

        //Populate table for historic medications

        if(currentDonor.getHistoricMedications().size() != 0) {
            for(Medication medication: currentDonor.getHistoricMedications()) {
                historicItems.add(medication.getName());
            }
            historyListView.setItems(historicItems);

            historyListView.setItems(historicItems);
        } else {
            return;
        }



    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setMedicationsController(this);

    }


}
