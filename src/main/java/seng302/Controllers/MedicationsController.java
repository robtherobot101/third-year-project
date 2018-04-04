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
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Class which handles all the logic for the Medications Pane.
 * Handles all functions including:
 * Saving, Adding new medications, moving medications between lists, deleting medications and comparing medications.
 */
public class MedicationsController implements Initializable {

    private Donor currentDonor;

    private ArrayList<Medication> historicMedicationsCopy = new ArrayList<>();
    private ArrayList<Medication> currentMedicationsCopy = new ArrayList<>();

    /**
     * Function to set the current donor of this class to that of the instance of the application.
     * @param currentDonor The donor to set the current donor.
     */
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

    @FXML
    private Button saveMedicationButton;
    @FXML
    private Button moveToHistoryButton;
    @FXML
    private Button moveToCurrentButton;
    @FXML
    private Button addNewMedicationButton;
    @FXML
    private Button deleteMedicationButton;


    private ObservableList<String> historicItems = FXCollections.observableArrayList();
    private ObservableList<String> currentItems = FXCollections.observableArrayList();

    /**
     * Function to handle when the user wants to add a new medication to the current medications list.
     * Adds the medication to the donor's personal list and then updates the listview.
     */
    public void addNewMedication() {
        //TODO **
        // STORY 19 - Add in new medication - autocomplete DO HERE.

        // This step is for getting the text from the text field.
        String medicationChoice = newMedicationField.getText();

        // This step is for adding a new medication to the copy of the donor's medication list (which will then be saved later)
        // and then the list views are updated after.
        currentMedicationsCopy.add(new Medication(medicationChoice));
        // NOTE: I have created another constructor in the Medications class for a medication with a name and
        // active ingredients also.
        // TODO **

        newMedicationField.clear();
        populateMedications(false);
    }

    /**
     * Function to handle when the user wants to delete a medication from either listview.
     * Removes the medication from the donor's personal list and then updates the respective listview.
     */
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
                for(Medication medication : currentMedicationsCopy) {
                    if(medication.getName().equals(medicationString)) {
                        medicationChoice = medication;
                    }
                }
                currentMedicationsCopy.remove(medicationChoice);
                populateMedications(false);
            }
            else {
                if (historyListView.getSelectionModel().getSelectedItem() != null) {
                    String medicationString = historyListView.getSelectionModel().getSelectedItem();
                    Medication medicationChoice = null;
                    for(Medication medication : historicMedicationsCopy) {
                        if(medication.getName().equals(medicationString)) {
                            medicationChoice = medication;
                        }
                    }
                    historicMedicationsCopy.remove(medicationChoice);
                    populateMedications(false);
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

    /**
     * Moves the selected medication from the current medications listview to the historic medications listview.
     */
    public void moveMedicationToHistory() {
        if (currentListView.getSelectionModel().getSelectedItem() != null) {

            //ADD
            String medicationString = currentListView.getSelectionModel().getSelectedItem();
            historicMedicationsCopy.add(new Medication(medicationString));

            //REMOVE
            Medication medicationChoice = null;
            for(Medication medication : currentMedicationsCopy) {
                if(medication.getName().equals(medicationString)) {
                    medicationChoice = medication;
                }
            }
            currentMedicationsCopy.remove(medicationChoice);

            //UPDATE
            populateMedications(false);


        } else {
            System.out.println("You must be in the Current LIst to move to the right");
        }


    }

    /**
     * Moves the selected medication from the historic medications listview to the current medications listview.
     */
    public void moveMedicationToCurrent() {
        if (historyListView.getSelectionModel().getSelectedItem() != null) {

            //ADD
            String medicationString = historyListView.getSelectionModel().getSelectedItem();
            currentMedicationsCopy.add(new Medication(medicationString));

            //REMOVE
            Medication medicationChoice = null;
            for(Medication medication : historicMedicationsCopy) {
                if(medication.getName().equals(medicationString)) {
                    medicationChoice = medication;
                }
            }
            historicMedicationsCopy.remove(medicationChoice);

            //UPDATE
            populateMedications(false);

        } else {
            System.out.println("You must be in the history LIst to move to the left");
        }

    }

    /**
     * Populates both list views based on the current status of the current donors medication status
     * and past medications. Must act differently for when starting and mid change.
     */
    public void populateMedications(Boolean startUp) {

        if(startUp == true) {
            //Populate table for current medications
            currentItems.clear();

            try {
                for(Medication medication: currentDonor.getCurrentMedications()) {
                    currentItems.add(medication.getName());
                }
                currentListView.setItems(currentItems);
            } catch(Exception e) {
                e.printStackTrace();
            }



            //Populate table for historic medications
            historicItems.clear();

            try {
                for (Medication medication : currentDonor.getHistoricMedications()) {
                    historicItems.add(medication.getName());
                }
                historyListView.setItems(historicItems);
            } catch(Exception e) {
                e.printStackTrace();
            }

        } else {

            //Populate table for current medications
            currentItems.clear();

            try {
                for(Medication medication: currentMedicationsCopy) {
                    currentItems.add(medication.getName());
                }
                currentListView.setItems(currentItems);
            } catch(Exception e) {
                e.printStackTrace();
            }



            //Populate table for historic medications
            historicItems.clear();

            try {
                for (Medication medication : historicMedicationsCopy) {
                    historicItems.add(medication.getName());
                }
                historyListView.setItems(historicItems);
            } catch(Exception e) {
                e.printStackTrace();
            }

        }


    }

    /**
     * Saves the current state of the donor's medications lists for both their historic and current medications.
     */
    public void save() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("Are you sure would like to update the current donor? ");
        alert.setContentText("By doing so, the donor will be updated with the following medication details.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            currentDonor.getHistoricMedications().clear();
            currentDonor.getHistoricMedications().addAll(historicMedicationsCopy);
            currentDonor.getCurrentMedications().clear();
            currentDonor.getCurrentMedications().addAll(currentMedicationsCopy);
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

    /**
     * Disables all the medications buttons as a donor is unable to interact with them.
     */
    public void setMedicationsViewForDonor() {
        addNewMedicationButton.setDisable(true);
        deleteMedicationButton.setDisable(true);
        moveToCurrentButton.setDisable(true);
        moveToHistoryButton.setDisable(true);
        saveMedicationButton.setDisable(true);
    }

    /**
     * Enables all the medications buttons as a clinician is able to interact with them.
     */
    public void setMedicationsViewForClinician() {
        addNewMedicationButton.setDisable(false);
        deleteMedicationButton.setDisable(false);
        moveToCurrentButton.setDisable(false);
        moveToHistoryButton.setDisable(false);
        saveMedicationButton.setDisable(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setMedicationsController(this);

    }


}