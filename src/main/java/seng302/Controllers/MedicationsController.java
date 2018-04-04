package seng302.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import seng302.Core.Donor;
import seng302.Core.Main;
import seng302.Core.Medication;
import seng302.Files.History;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static seng302.Core.Main.streamOut;

public class MedicationsController implements Initializable {

    private Donor currentDonor;

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
    private ListView<String> historyListView = new ListView<>();
    @FXML
    private ListView<String> currentListView = new ListView<>();

    private ObservableList<String> historicItems = FXCollections.observableArrayList();
    private ObservableList<String> currentItems = FXCollections.observableArrayList();

    public void addNewMedication() {
        //TODO **
        // STORY 19 - Add in new medication - autocomplete DO HERE.

        // This step is for getting the text from the text field.
        String medicationChoice = newMedicationField.getText();

        // This step is for adding a new medication to the donor's medication and then the list views are updated after.
        currentDonor.getCurrentMedications().add(new Medication(medicationChoice));
        // NOTE: I have created another constructor in the Medications class for a medication with a name and
        // active ingredients also.
        // TODO **

        newMedicationField.clear();
        populateMedications();
    }

    public void deleteSelectedMedication() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("Are you sure would like to delete the selected medication? ");
        alert.setContentText("By doing so, the medication will be erased from the database.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {

            if (currentListView.getSelectionModel().getSelectedItem() != null) {
                String medicationString = currentListView.getSelectionModel().getSelectedItem();
                Medication medicationChoice = null;
                for(Medication medication : currentDonor.getCurrentMedications()) {
                    if(medication.getName().equals(medicationString)) {
                        medicationChoice = medication;
                    }
                }
                currentDonor.getCurrentMedications().remove(medicationChoice);
                populateMedications();
            }
            else {
                if (historyListView.getSelectionModel().getSelectedItem() != null) {
                    String medicationString = historyListView.getSelectionModel().getSelectedItem();
                    Medication medicationChoice = null;
                    for(Medication medication : currentDonor.getHistoricMedications()) {
                        if(medication.getName().equals(medicationString)) {
                            medicationChoice = medication;
                        }
                    }
                    currentDonor.getHistoricMedications().remove(medicationChoice);
                    populateMedications();
                }
            }

            //TODO create update for medications for history when deleting
//            String text = History.prepareFileStringGUI(currentDonor.getId(), "update");
//            History.printToFile(streamOut, text);
            //populateHistoryTable();
            alert.close();
        } else {
            alert.close();
        }



    }

    public void moveMedicationToHistory() {
        if (currentListView.getSelectionModel().getSelectedItem() != null) {

            String medicationString = currentListView.getSelectionModel().getSelectedItem();
            currentDonor.getHistoricMedications().add(new Medication(medicationString));

            Medication medicationChoice = null;
            for(Medication medication : currentDonor.getCurrentMedications()) {
                if(medication.getName().equals(medicationString)) {
                    medicationChoice = medication;
                }
            }
            currentDonor.getCurrentMedications().remove(medicationChoice);

            populateMedications();


        } else {
            System.out.println("You must be in the Current LIst to move to the right");
        }


    }

    public void moveMedicationToCurrent() {
        if (historyListView.getSelectionModel().getSelectedItem() != null) {

            String medicationString = historyListView.getSelectionModel().getSelectedItem();
            currentDonor.getCurrentMedications().add(new Medication(medicationString));

            Medication medicationChoice = null;
            for(Medication medication : currentDonor.getHistoricMedications()) {
                if(medication.getName().equals(medicationString)) {
                    medicationChoice = medication;
                }
            }
            currentDonor.getHistoricMedications().remove(medicationChoice);

            populateMedications();

        } else {
            System.out.println("You must be in the history LIst to move to the left");
        }

    }

    public void populateMedications() {

        //Populate table for current medications
        currentItems.clear();

        if(currentDonor.getCurrentMedications().size() != 0) {
            for(Medication medication: currentDonor.getCurrentMedications()) {
                currentItems.add(medication.getName());
            }
            currentListView.setItems(currentItems);
        } else {
            return;
        }

        //Populate table for historic medications
        historicItems.clear();

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

    public void save() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("Are you sure would like to update the current donor? ");
        alert.setContentText("By doing so, the donor will be updated with the following medication details.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Main.saveUsers(Main.getDonorPath(), true);
            //TODO create update for medications for history
//            String text = History.prepareFileStringGUI(currentDonor.getId(), "update");
//            History.printToFile(streamOut, text);
            //populateHistoryTable();
            alert.close();
        } else {
            alert.close();
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setMedicationsController(this);

    }


}
