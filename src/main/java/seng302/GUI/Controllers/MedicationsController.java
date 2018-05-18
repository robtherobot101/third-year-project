package seng302.GUI.Controllers;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import seng302.Generic.Cache;
import seng302.Generic.History;
import seng302.Generic.WindowManager;
import seng302.User.Attribute.ProfileType;
import seng302.User.Medication.DrugInteraction;
import seng302.User.Medication.InteractionApi;
import seng302.User.Medication.Mapi;
import seng302.User.Medication.Medication;
import seng302.User.User;

import java.net.URL;
import java.util.*;

import static seng302.Generic.IO.streamOut;


/**
 * Class which handles all the logic for the Medications Pane.
 * Handles all functions including:
 * Saving, Adding new medications, moving medications between lists, deleting medications and comparing medications.
 */
public class MedicationsController extends PageController implements Initializable {

    @FXML
    private TextField newMedicationField;
    @FXML
    private Label userNameLabel, newMedicationLabel, activeIngredientsTitleLabel, activeIngredientsContentLabel, interactionsTitleLabel,
            interactionsContentLabel, historyTitleLabel, historyContentLabel;
    @FXML
    private ListView<Medication> historyListView = new ListView<>(), currentListView = new ListView<>();
    @FXML
    private Button moveToHistoryButton, moveToCurrentButton, addNewMedicationButton, deleteMedicationButton, compareButton;

    private boolean movingItem = false;
    private User currentUser;
    private ObservableList<Medication> historicItems, currentItems;
    private InteractionApi interactionApi = InteractionApi.getInstance();
    private String drugA = null, drugB = null;
    private boolean retrievingInteractions = false;
    private UserWindowController userWindowController;

    private Cache autocompleteCache;
    private Cache activeIngredientsCache;


    public void importCaches(){
        autocompleteCache = new Cache("target/autoComplete.json");
        activeIngredientsCache = new Cache("target/activeIngredients");
    }

    public void saveCaches(){

    }

    /**
     * Searches the autocomplete cache to see if this has already been queried and returns it. If it has not been queried it sends a
     * query to the MAPI autocomplete api and saves the result.
     * @param query The query to search for.
     * @return The results of the autocomplete api query as an ArrayList of String.
     */
    public ArrayList<String> searchAutocomplete(String query){
        if (autocompleteCache == null){
            importCaches();
        }

        String result = "";
        if (autocompleteCache.contains(query)) {
           result = autocompleteCache.get(query);
        } else {
            result = Mapi.autocomplete(query);
            autocompleteCache.put(query,result);
        }
        String[] temp = result.split("\\[");
        result = temp[1];
        if (result.length() > 4) {
            result = result.substring(1, result.length() - 3);
        } else {
            result = "";
        }
        temp = result.split("\",\"");
        System.out.println(Arrays.toString(temp));

        return new ArrayList<>(Arrays.asList(temp));
    }

    /**
     * Searches the autocomplete cache to see if this has already been queried and returns it. If it has not been queried it sends a
     * query to the MAPI active ingredients api and saves the result.
     * @param query The medicine to search for.
     * @return The results of the active ingredients api query as an ArrayList of String.
     */
    public ArrayList<String> searchActiveIngredients(String query){
        if (activeIngredientsCache == null){
            importCaches();
        }
        String result = "";
        if (activeIngredientsCache.contains(query)) {
            activeIngredientsCache.get(query);
        } else {
             result = Mapi.activeIngredients(query);
             activeIngredientsCache.put(query, result);
        }
        if (result.length() > 4) {
            result = result.substring(2, result.length() - 2);
        } else {
            result = "";
        }
        String[] temp = result.split("\",\"");
        return new ArrayList<>(Arrays.asList(temp));
    }

    /**
     * Initializes the medications pane to show medications for a specified user.
     *
     * @param currentUser The user to initialize the medications pane with
     */
    public void initializeUser(User currentUser) {
        this.currentUser = currentUser;
        userNameLabel.setText("User: " + currentUser.getName());
        addNewMedicationButton.setDisable(newMedicationField.getText().isEmpty());

        //Populate table for current medications
        currentItems = FXCollections.observableArrayList();
        currentItems.addAll(currentUser.getCurrentMedications());
        currentListView.setItems(currentItems);

        //Populate table for historic medications
        historicItems = FXCollections.observableArrayList();
        historicItems.addAll(currentUser.getHistoricMedications());
        historyListView.setItems(historicItems);

        checkSelections();
    }

    /**
     * Update the displayed user medications to what is currently stored in the user object.
     */
    public void updateMedications() {
        currentItems.clear();
        currentItems.addAll(currentUser.getCurrentMedications());
        historicItems.clear();
        historicItems.addAll(currentUser.getHistoricMedications());
        checkSelections();
    }

    /**
     *
     */
    private void saveToUndoStack() {
        userWindowController.addCurrentUserToUndoStack();
        currentUser.getCurrentMedications().clear();
        currentUser.getCurrentMedications().addAll(currentItems);
        currentUser.getHistoricMedications().clear();
        currentUser.getHistoricMedications().addAll(historicItems);
    }

    /**
     * Converts a String ArrayList query from Generic/Mapi to a single string with each ingredient separated by a newline
     *
     * @param ApiQueryResult String ArrayList returned from a call to Mapi.activeIngredients
     * @return String of newline separated ingredients
     */
    private String convertArrayListIngredientsToString(String[] ApiQueryResult) {
        // Check if query result is empty
        if (ApiQueryResult.length == 0) {
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
     * Function to handle when the user wants to add a new medication to the current medications list.
     * Adds the medication to the user's personal list and then updates the listview.
     */
    public void addNewMedication() {
        // This step is for getting the text from the text field.
        String medicationChoice = newMedicationField.getText();
        if (medicationChoice.equals("")) {
            WindowManager.createAlert(AlertType.ERROR, "Error", "Error with the Medication Input", "The input must not be empty.").show();
        } else {
            // Check for duplicates
            if (historicItems.contains(new Medication(medicationChoice)) ||
                    currentItems.contains(new Medication(medicationChoice))) {
                WindowManager.createAlert(AlertType.ERROR, "Error", "Error with the Medication Input", "That medication is already registered to " +
                        "this person.").show();
            } else {
                // This step is for adding a new medication to the copy of the user's medication list (which will then be saved later)
                // and then the list views are updated after.
                System.out.println(medicationChoice);
                statusIndicator.setStatus("Fetching from API", true);
                new Thread(() -> {
                    List<String> activeIngredients = searchActiveIngredients(medicationChoice);
                    Platform.runLater(() -> {
                        if (!activeIngredients.get(0).equals("")) {
                            Medication newMedication = new Medication(medicationChoice, activeIngredients.toArray(new String[0]));
                            newMedication.startedTaking();
                            currentItems.add(newMedication);
                            // NOTE: I have created another constructor in the Medications class for a medication with a name and
                            // active ingredients also.

                            newMedicationField.clear();
                            saveToUndoStack();
                            statusIndicator.setStatus("Added " + medicationChoice, false);
                            titleBar.saved(false);
                        } else {
                            WindowManager.createAlert(AlertType.ERROR, "Error", "Error with the Medication Input", String.format("The medication %s" +
                                    " does not exist.", medicationChoice)).show();
                        }
                    });
                }).start();

            }
        }
        // After clicking the button, it becomes disabled
        addNewMedicationButton.setDisable(true);
    }

    /**
     * Function to handle when the user wants to delete a medication from either listview.
     * Removes the medication from the user's personal list and then updates the respective listview.
     */
    public void deleteSelectedMedication() {
        Alert alert = WindowManager.createAlert(AlertType.CONFIRMATION, "Are you sure?",
                "Are you sure would like to delete the selected medication? ", "By doing so, the medication will be erased from the database.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (currentListView.getSelectionModel().getSelectedItem() != null) {
                Medication m = currentListView.getSelectionModel().getSelectedItem();
                currentItems.remove(m);
                statusIndicator.setStatus("Deleted " + m + " from current medications", false);
            } else if (historyListView.getSelectionModel().getSelectedItem() != null) {
                Medication m = historyListView.getSelectionModel().getSelectedItem();
                historicItems.remove(historyListView.getSelectionModel().getSelectedItem());
                statusIndicator.setStatus("Deleted " + m + " from historic medications", false);
            }
            titleBar.saved(false);
            saveToUndoStack();

        }
        alert.close();
    }

    /**
     * Moves the selected medication from the current medications listview to the historic medications listview.
     */
    public void moveMedicationToHistory() {
        Medication m = moveMedication(historicItems, currentListView);
        m.stoppedTaking();
        statusIndicator.setStatus("Moved " + m + " to history", false);
        titleBar.saved(false);
    }

    /**
     * Moves the selected medication from the historic medications listview to the current medications listview.
     */
    public void moveMedicationToCurrent() {
        Medication m = moveMedication(currentItems, historyListView);
        m.startedTaking();
        statusIndicator.setStatus("Moved " + m + " to current", false);
        titleBar.saved(false);
    }

    /**
     * Move a selected Medication from its corresponding Medication list to another Medication list.
     *
     * @param to   The Medication list to move the medication from
     * @param view The ListView to get the selected medication from
     * @return The medication which was moved
     */
    private Medication moveMedication(ObservableList<Medication> to, ListView<Medication> view) {
        movingItem = true;
        // Remove the medication from the ListView
        Medication m = view.getItems().remove(view.getSelectionModel().getSelectedIndex());
        // Add it to the 'to' list
        to.add(m);
        saveToUndoStack();
        movingItem = false;
        return m;
    }

    /**
     * Updates the user to the current state of the medications lists for both their historic and current medications.
     */
    public void updateUser() {
        currentUser.getHistoricMedications().clear();
        currentUser.getHistoricMedications().addAll(historicItems);
        currentUser.getCurrentMedications().clear();
        currentUser.getCurrentMedications().addAll(currentItems);
        String text = History.prepareFileStringGUI(currentUser.getId(), "medications");
        History.printToFile(streamOut, text);
    }

    /**
     * Acts on button push, selects whether the drug selected is in the historic medications pane or the current med pane.
     */
    public void updateComparison() {
        if (!currentListView.getSelectionModel().isEmpty()) {
            addToComparison(currentListView.getSelectionModel().getSelectedItem().toString());
        } else if (!historyListView.getSelectionModel().isEmpty()) {
            addToComparison(historyListView.getSelectionModel().getSelectedItem().toString());
        }
    }

    /**
     * Acts as a check whether the selected drug is being added to the comparison, or if the two selected drugs are being compared.
     *
     * @param selection the drug selected in the medications pane.
     */
    private void addToComparison(String selection) {
        if (drugA == null) {
            drugA = selection;
            interactionsTitleLabel.setText("Select a drug to compare " + drugA + " with and then click compare again");
        } else if (drugB == null && !drugA.equals(selection)) {
            compareButton.setDisable(true);
            retrievingInteractions = true;
            drugB = selection;
            interactionsTitleLabel.setText(String.format("Loading interactions between %s and %s...", drugA, drugB));

            new Thread(() -> Platform.runLater(() -> {
                LinkedList<String> symptoms = makeComparison(drugA, drugB);
                interactionsTitleLabel.setText(String.format("Interactions between %s and %s", drugA, drugB));
                if (symptoms.isEmpty()) {
                    interactionsContentLabel.setText("No interactions.");
                } else {
                    interactionsContentLabel.setText(String.join("\n", symptoms));
                }
                compareButton.setDisable(false);
                retrievingInteractions = false;
            })).start();
        } else {
            drugA = selection;
            interactionsTitleLabel.setText("Select a drug to compare " + drugA + " with and then click compare again");
            interactionsContentLabel.setText("");
            drugB = null;
        }
    }

    /**
     * Accesses the ehealth api with the two given drugs.
     * Finds all conditions based on users age and gender, and then finds the duration of each.
     * It modifies the string to add on the duration to the end eg "Nausea: 2-5 years"
     *
     * @param drugA The first drug being compared.
     * @param drugB The second drug being compared.
     * @return A hashset of each condition and it's duration.
     */
    private LinkedList<String> makeComparison(String drugA, String drugB) {
        LinkedList<String> symptoms = new LinkedList<>();
        drugA = drugA.replace(' ', '-');
        drugB = drugB.replace(' ', '-');

        DrugInteraction result = interactionApi.interactions(drugA, drugB);
        // Check to see if the api call was successful
        if (!result.getError()) {
            HashSet<String> ageSymptoms = result.ageInteraction(currentUser.getAgeDouble());
            HashSet<String> genderSymptoms = result.genderInteraction(currentUser.getGender());
            ageSymptoms.retainAll(genderSymptoms);

            for (String symptom : ageSymptoms) {
                if (result.getDuration(symptom).equals("not specified")) {
                    symptoms.add("-" + symptom);
                } else {
                    symptoms.add("-" + symptom + ": " + result.getDuration(symptom));
                }
            }
        } else {
            symptoms.add(result.getErrorMessage());
        }
        return symptoms;
    }

    /**
     * Sets whether the control buttons are shown or not on the medications pane.,
     *
     * @param shown A Boolean where true shows the control buttons and false hides them.
     */
    public void setControlsShown(boolean shown) {
        addNewMedicationButton.setVisible(shown);
        deleteMedicationButton.setVisible(shown);
        moveToCurrentButton.setVisible(shown);
        moveToHistoryButton.setVisible(shown);
        newMedicationField.setVisible(shown);
        newMedicationLabel.setVisible(shown);
    }

    /**
     * Ensure that there is only one selection.
     *
     * @param currentUpdated Whether the current medications list view was just updated. If this field is false then the historic medications field
     *                       has just been edited instead.
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
     * Sets controls enabled or disabled based on the current selections made. Sets active ingredients to show based on the currently selected
     * medication.
     */
    private void checkSelections() {
        boolean historySelectionIsNull = historyListView.getSelectionModel().getSelectedItem() == null, currentSelectionIsNull = currentListView
                .getSelectionModel().getSelectedItem() == null;
        moveToCurrentButton.setDisable(historySelectionIsNull);
        moveToHistoryButton.setDisable(currentSelectionIsNull);
        deleteMedicationButton.setDisable(historySelectionIsNull && currentSelectionIsNull);
        if (!retrievingInteractions) {
            compareButton.setDisable(historySelectionIsNull && currentSelectionIsNull);
        }
        Medication selected;
        if (!historySelectionIsNull) {
            selected = historyListView.getSelectionModel().getSelectedItem();
            // Display the ingredients
            activeIngredientsTitleLabel.setText("Active Ingredients in " + selected.getName());
            activeIngredientsContentLabel.setText(convertArrayListIngredientsToString(selected.getActiveIngredients()));
            // Display the usage history
            historyTitleLabel.setText("History of usage for " + selected.getName());
            historyContentLabel.setText(String.join("\n", selected.getHistory()));
        } else if (!currentSelectionIsNull) {
            selected = currentListView.getSelectionModel().getSelectedItem();
            // Display the ingredients
            activeIngredientsTitleLabel.setText("Active Ingredients in " + selected.getName());
            activeIngredientsContentLabel.setText(convertArrayListIngredientsToString(selected.getActiveIngredients()));
            // Display the usage history
            historyTitleLabel.setText("History of usage for " + selected.getName());
            historyContentLabel.setText(String.join("\n", selected.getHistory()));
        } else {
            activeIngredientsTitleLabel.setText("");
            activeIngredientsContentLabel.setText("");
            historyTitleLabel.setText("");
            historyContentLabel.setText("");
        }
    }

    /**
     * Sets up a reference to the parent user window controller for this controller.
     *
     * @param parent The user window controller that is the parent of this controller
     */
    public void setParent(UserWindowController parent) {
        userWindowController = parent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Attach the autocompletion box and set its endpoint to the MAPI API
        // ALso only enable the add button if a medication has been autocompleted
        new AutoCompletionTextFieldBinding<>(newMedicationField, param -> {
            if (newMedicationField.getText().length() == 0) {
                return null;
            }
            String medicine = newMedicationField.getText();
            // Show API call on status bar
            Platform.runLater(() -> statusIndicator.setStatus("Fetching from API", true));
            ArrayList<String> medicines = searchAutocomplete(medicine);
            // Reset status bar
            Platform.runLater(() -> Platform.runLater(() -> statusIndicator.ready()));
            if (medicines.size() > 5) {
                return medicines.subList(0, 5);
            } else {
                return medicines;
            }
        });
        newMedicationField.textProperty().addListener(((observable, oldValue, newValue) -> addNewMedicationButton.setDisable(newValue.isEmpty())));

        interactionsTitleLabel.setText("");
        interactionsContentLabel.setText("");

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
