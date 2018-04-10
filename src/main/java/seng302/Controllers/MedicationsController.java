package seng302.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import seng302.Core.*;
import seng302.Files.History;

import javax.sound.midi.SysexMessage;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;

/**
 * Class which handles all the logic for the Medications Pane.
 * Handles all functions including:
 * Saving, Adding new medications, moving medications between lists, deleting medications and comparing medications.
 */
public class MedicationsController implements Initializable {

    private Donor currentDonor;

    private ArrayList<Medication> historicMedicationsCopy = new ArrayList<>();
    private ArrayList<Medication> currentMedicationsCopy = new ArrayList<>();

    private InteractionApi interactionApi = new InteractionApi();

    /**
     * Function to set the current donor of this class to that of the instance of the application.
     * @param currentDonor The donor to set the current donor.
     */
    public void setCurrentDonor(Donor currentDonor) {
        this.currentDonor = currentDonor;
        System.out.println("HELLO");
        donorNameLabel.setText("Donor: " + currentDonor.getName());
        addNewMedicationButton.setDisable(true);
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
    private Label newMedicationLabel, interactionsLabel, drugALabel, drugBLabel;
    @FXML
    private ListView<String> historyListView = new ListView<>();
    @FXML
    private ListView<String> currentListView = new ListView<>();
    @FXML
    private ListView<String> interactionListView = new ListView<>();

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
    private ObservableList<String> interactionItems = FXCollections.observableArrayList();

    /**
     * Function to handle when the user wants to add a new medication to the current medications list.
     * Adds the medication to the donor's personal list and then updates the listview.
     */
    public void addNewMedication() {
        //TODO **
        // STORY 19 - Add in new medication - autocomplete DO HERE.

        // This step is for getting the text from the text field.
        String medicationChoice = newMedicationField.getText();
        if(medicationChoice.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error with the Medication Input");
            alert.setContentText("The input must not be empty.");
            alert.show();

        } else {
            // This step is for adding a new medication to the copy of the donor's medication list (which will then be saved later)
            // and then the list views are updated after.
            currentMedicationsCopy.add(new Medication(medicationChoice));
            // NOTE: I have created another constructor in the Medications class for a medication with a name and
            // active ingredients also.
            // TODO **

            newMedicationField.clear();
            populateMedications(false);
        }

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
     * Acts on button push, selects whether the drug selected is in the historic medications pane or the current med pane.
     */
    public void compare(){
        String currentSelection = currentListView.getSelectionModel().getSelectedItem();
        String historicSelection = historyListView.getSelectionModel().getSelectedItem();
        if(currentSelection != null){
            addToComparison(currentSelection);
        }else if(historicSelection != null){
            addToComparison(historicSelection);
        }
    }

    /**
     * Acts as a check whether the selected drug is being added to the comparison, or if the two selected drugs are being compared.
     * @param selection the drug selected in the medications pane.
     */
    public void addToComparison(String selection){
        String drugA = drugALabel.getText();
        String drugB = drugBLabel.getText();
        if(drugA.equals("Drug A")){
            drugALabel.setText(selection);
        }else if(drugB.equals("Drug B")){
            drugBLabel.setText(selection);
            System.out.println("Make comparison");
            drugA = drugALabel.getText();
            drugB = drugBLabel.getText();
            HashSet<String> symptoms = makeComparison(drugA, drugB);
            interactionItems.addAll(symptoms);
            FXCollections.reverse(interactionItems);

            interactionListView.setItems(interactionItems);

        }else{
            drugALabel.setText(selection);
            drugBLabel.setText("Drug B");
        }
    }

    /**
     * Accesses the ehealth api with the two given drugs.
     * Finds all conditions based on donors age and gender, and then finds the duration of each.
     * It modifies the string to add on the duration to the end eg "Nausea: 2-5 years"
     * @param drugA The first drug being compared.
     * @param drugB The second drug being compared.
     * @return A hashset of each condition and it's duration.
     */
    public HashSet<String> makeComparison(String drugA, String drugB){
        DrugInteraction result = new DrugInteraction(interactionApi.interactions(drugA, drugB));
        HashSet<String> ageSymptoms = result.ageInteraction(currentDonor.getAgeDouble());
        HashSet<String> genderSymptoms = result.genderInteraction(currentDonor.getGender());
        HashSet<String> symptoms = new HashSet<>();

        ageSymptoms.addAll(genderSymptoms);

        HashMap<String, HashSet<String>> durationInteraction = result.getDurationInteraction();

        for (Map.Entry<String, HashSet<String>> entry : durationInteraction.entrySet()){
            HashSet<String> interactions = entry.getValue();
            interactions.retainAll(ageSymptoms);
            for (String interaction : interactions){
                interaction += ": " + entry.getKey();
                //System.out.println(interaction);
                symptoms.add(interaction);
            }
        }
        return symptoms;
    }

    /**
     * Sets whether the control buttons are shown or not on the medications pane
     */
    public void setControlsShown(boolean shown) {
        addNewMedicationButton.setVisible(shown);
        deleteMedicationButton.setVisible(shown);
        moveToCurrentButton.setVisible(shown);
        moveToHistoryButton.setVisible(shown);
        saveMedicationButton.setVisible(shown);
        newMedicationField.setVisible(shown);
        newMedicationLabel.setVisible(shown);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setMedicationsController(this);
        newMedicationField.textProperty().addListener((observable, oldValue, newValue) -> {
            addNewMedicationButton.setDisable(newValue.isEmpty());
            //TODO Add your listener code here James
        });
    }
}
