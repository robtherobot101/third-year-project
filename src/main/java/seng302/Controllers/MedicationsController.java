package seng302.Controllers;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import seng302.Core.Donor;
import seng302.Core.Main;
import seng302.Core.Mapi;
import seng302.Core.Medication;
import seng302.Files.History;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * Class which handles all the logic for the Medications Pane.
 * Handles all functions including:
 * Saving, Adding new medications, moving medications between lists, deleting medications and comparing medications.
 */
public class MedicationsController implements Initializable {

    @FXML
    private TextField newMedicationField;
    @FXML
    private Label donorNameLabel, newMedicationLabel, interactionsLabel, drugALabel, drugBLabel;
    @FXML
    private ListView<String> historyListView = new ListView<>(), currentListView = new ListView<>();
    @FXML
    private Button saveMedicationButton, moveToHistoryButton, moveToCurrentButton, addNewMedicationButton, deleteMedicationButton;

    private boolean movingItem = false;
    private Donor currentDonor;
    private ArrayList<Medication> historicMedicationsCopy, currentMedicationsCopy;
    private ObservableList<String> historicItems, currentItems;

    /**
     * Function to set the current donor of this class to that of the instance of the application.
     *
     * @param currentDonor The donor to set the current donor.
     */
    public void setCurrentDonor(Donor currentDonor) {
        this.currentDonor = currentDonor;
        donorNameLabel.setText("Donor: " + currentDonor.getName());
        addNewMedicationButton.setDisable(true);
        historicMedicationsCopy = new ArrayList<>();
        historicMedicationsCopy.addAll(currentDonor.getHistoricMedications());
        currentMedicationsCopy = new ArrayList<>();
        currentMedicationsCopy.addAll(currentDonor.getCurrentMedications());
        historicItems = FXCollections.observableArrayList();
        currentItems = FXCollections.observableArrayList();
        checkSelections();
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
    private Label histDrugLabel;
    @FXML
    private Label currDrugLabel;
    @FXML
    private Label histDrugIngredients;
    @FXML
    private Label currDrugIngredients;
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
     * Converts a String ArrayList query from Core/Mapi to a single string with each ingredient separated by a newline
     * @param ApiQueryResult String ArrayList returned from a call to Mapi.activeIngredients
     * @return String of newline separated ingredients
     */
    private String convertArrayListIngredientsToString(ArrayList<String> ApiQueryResult) {
        // Check if query result is empty
        if (ApiQueryResult.get(0).isEmpty()) {
            // Invalid drug/no active ingredients
            return "No active ingredients found";
        }

        StringBuilder displayedActiveIngredients = new StringBuilder();
        for (String currentIngredient : ApiQueryResult) {
            displayedActiveIngredients.append("-").append(currentIngredient).append("\n");
        }
        return displayedActiveIngredients.toString();
    }

    /**
     * Called when a object is selected in the currentListView, filling in the active ingredient section.
     */
    @FXML
    public void currentMedicationClicked() {
        String selectedItem = currentListView.getSelectionModel().getSelectedItem();

        // Check if it is an actual item selected, not just a highlight
        if (selectedItem != null) {
            // Set drug title text
            currDrugLabel.setText(selectedItem);

            // Display the ingredients
            currDrugIngredients.setText(convertArrayListIngredientsToString(
                    new Mapi().activeIngredients(selectedItem)));
        }
    }

    /**
     * Called when a object is selected in the historyListView, filling in the active ingredient section.
     */
    @FXML
    public void historyMedicationClicked() {
        String selectedItem = historyListView.getSelectionModel().getSelectedItem();

        // Check if it is an actual item selected, not just a highlight
        if (selectedItem != null) {
            // Set drug title text
            histDrugLabel.setText(selectedItem);

            // Display the ingredients
            histDrugIngredients.setText(convertArrayListIngredientsToString(
                    new Mapi().activeIngredients(selectedItem)));
        }
    }


    /**
     * Function to handle when the user wants to add a new medication to the current medications list.
     * Adds the medication to the donor's personal list and then updates the listview.
     */
    public void addNewMedication() {
        //TODO **
        // STORY 19 - Add in new medication - autocomplete DO HERE.

        // This step is for getting the text from the text field.
        String medicationChoice = newMedicationField.getText();
        if (medicationChoice.equals("")) {
            Main.createAlert(AlertType.ERROR, "Error", "Error with the Medication Input", "The input must not be empty.").show();
        } else {
            boolean duplicate = false;
            for (Medication medication: historicMedicationsCopy) {
                if (medication.getName().equals(medicationChoice)) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                for (Medication medication : currentMedicationsCopy) {
                    if (medication.getName().equals(medicationChoice)) {
                        duplicate = true;
                        break;
                    }
                }
            }
            if (duplicate) {
                Main.createAlert(AlertType.ERROR, "Error", "Error with the Medication Input", "That medication is already registered to this person.").show();
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
    }

    /**
     * Function to handle when the user wants to delete a medication from either listview.
     * Removes the medication from the donor's personal list and then updates the respective listview.
     */
    public void deleteSelectedMedication() {
        Alert alert = Main.createAlert(AlertType.CONFIRMATION, "Are you sure?",
            "Are you sure would like to delete the selected medication? ", "By doing so, the medication will be erased from the database.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (currentListView.getSelectionModel().getSelectedItem() != null) {
                deleteMedication(currentMedicationsCopy, currentListView.getSelectionModel().getSelectedItem());
            } else if (historyListView.getSelectionModel().getSelectedItem() != null) {
                deleteMedication(historicMedicationsCopy, historyListView.getSelectionModel().getSelectedItem());
            }

            //TODO create update for medications for history when deleting
//            String text = History.prepareFileStringGUI(currentDonor.getId(), "update");
//            History.printToFile(streamOut, text);
            //populateHistoryTable();
        }
        alert.close();
    }

    /**
     * Deletes a medication from an ArrayList of medications using the medication name.
     *
     * @param deleteFrom The ArrayList of medications to delete the medication from
     * @param toDelete The name of the medication
     */
    private void deleteMedication(ArrayList<Medication> deleteFrom, String toDelete) {
        for (Medication medication: deleteFrom) {
            if (medication.getName().equals(toDelete)) {
                deleteFrom.remove(medication);
                break;
            }
        }
        populateMedications(false);
    }

    /**
     * Moves the selected medication from the current medications listview to the historic medications listview.
     */
    public void moveMedicationToHistory() {
        moveMedication(historicMedicationsCopy, currentMedicationsCopy, currentListView);
    }

    /**
     * Moves the selected medication from the historic medications listview to the current medications listview.
     */
    public void moveMedicationToCurrent() {
        moveMedication(currentMedicationsCopy, historicMedicationsCopy, historyListView);
    }

    /**
     * Move a selected Medication from its corresponding Medication list to another Medication list.
     *
     * @param to The Medication list to move the medication from
     * @param from The Medication list to move the medication to
     * @param view The ListView to get the selected medication from
     */
    private void moveMedication(ArrayList<Medication> to, ArrayList<Medication> from, ListView<String> view) {
        movingItem = true;

        //Get the item the user has selected
        String medicationString = view.getSelectionModel().getSelectedItem();
        //Get the medication object reference
        Medication medicationChoice = null;
        for (Medication medication: from) {
            if (medication.getName().equals(medicationString)) {
                medicationChoice = medication;
                break;
            }
        }

        to.add(medicationChoice);
        from.remove(medicationChoice);
        populateMedications(false);
        movingItem = false;
    }

    /**
     * Populates both list views based on the current status of the current donors medication status
     * and past medications. Must act differently for when starting and mid change.
     */
    public void populateMedications(Boolean startUp) {
        if (startUp) {


            //Populate table for current medications
            currentItems.clear();
            for (Medication medication : currentDonor.getCurrentMedications()) {
                currentItems.add(medication.getName());
            }
            currentListView.setItems(currentItems);

            //Populate table for historic medications
            historicItems.clear();
            for (Medication medication : currentDonor.getHistoricMedications()) {
                historicItems.add(medication.getName());
            }
            historyListView.setItems(historicItems);
        } else {
            //Populate table for current medications
            currentItems.clear();
            for (Medication medication : currentMedicationsCopy) {
                currentItems.add(medication.getName());
            }
            currentListView.setItems(currentItems);

            //Populate table for historic medications
            historicItems.clear();
            for (Medication medication : historicMedicationsCopy) {
                historicItems.add(medication.getName());
            }
            historyListView.setItems(historicItems);
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

    /**
     * Ensure that there is only one selection.
     *
     * @param currentUpdated Whether the current medications list view was just updated. If this field is false then the historic medications field has just been edited instead.
     */
    private void checkSelectionNumber(boolean currentUpdated) {
        if (!(historyListView.getSelectionModel().getSelectedItem() == null) && !(currentListView.getSelectionModel().getSelectedItem() == null)) {
            if (currentUpdated) {
                historyListView.getSelectionModel().clearSelection();
            } else {
                currentListView.getSelectionModel().clearSelection();
            }
        }
    }

    /**
     * Sets controls enabled or disabled based on the current selections made.
     */
    private void checkSelections() {
        moveToCurrentButton.setDisable(historyListView.getSelectionModel().getSelectedItem() == null);
        moveToHistoryButton.setDisable(currentListView.getSelectionModel().getSelectedItem() == null);
        deleteMedicationButton.setDisable(historyListView.getSelectionModel().getSelectedItem() == null && currentListView.getSelectionModel().getSelectedItem() == null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.setMedicationsController(this);
        newMedicationField.textProperty().addListener((observable, oldValue, newValue) -> {
            addNewMedicationButton.setDisable(newValue.isEmpty());
            //TODO Add your listener code here James
            if (!newMedicationField.getText().isEmpty() || !newMedicationField.getText().equals("")) {
                System.out.println("aksjgdijhas");
                ArrayList<String> results = Mapi.autocomplete(newMedicationField.getText());
                System.out.println(results);
                new AutoCompletionTextFieldBinding<String>(newMedicationField, param -> {
                    if (results.size() > 5) {
                        return results.subList(0,4);
                    } else {
                        return results;
                    }
                });
//                ArrayList<String> results = Mapi.autocomplete(newMedicationField.getText());
//                newMedicationField.getEntries().addAll(results);
            }
        histDrugLabel.setText("");
        currDrugLabel.setText("");
        histDrugIngredients.setText("");
        currDrugIngredients.setText("");


        });
        currentListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!movingItem) {
                checkSelectionNumber(true);
            }
            checkSelections();
        });
        historyListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!movingItem) {
                checkSelectionNumber(false);
            }
            checkSelections();
        });
    }
}
