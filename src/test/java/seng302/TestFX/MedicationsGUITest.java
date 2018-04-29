package seng302.TestFX;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;
import seng302.Generic.Main;
import seng302.User.Attribute.Gender;
import seng302.User.Medication.Medication;
import seng302.User.User;

public class MedicationsGUITest extends TestFXTest {
    private User testUser;

    @BeforeClass
    public static void setupClass() throws TimeoutException  {
        defaultTestSetup();
    }

    @Before
    public void setUp() {
        testUser = addTestUser();
        testUser.setGender(Gender.FEMALE);
        loginAsDefaultClinician();
        openUserAsClinician(testUser.getName());
        sleep(500);
        clickOn("Medications");
    }

    /**
     * Method to add a new medication to a user's current medications list.
     *
     * @param medication The medication to add.
     */
    private void addNewMedicationToCurrentMedications(String medication) {
        clickOn("#newMedicationField").write(medication);
        clickOn("#addNewMedicationButton");
        clickOn("#newMedicationField");
        // Not sure if there is a better way to do this
        sleep(3000);
    }

    /**
     * Add a simple medication and verify it is correct
     */
    @Test
    public void addMedicationForDonor() {
        //Add Medication for donor.
        addNewMedicationToCurrentMedications("Asacol");

        //Check if medication added is correct.
        ListView currentMedicationList = lookup("#currentListView").queryListView();
        Medication topResult = (Medication) currentMedicationList.getItems().get(0);
        assertTrue(topResult.getName().equalsIgnoreCase("Asacol"));
    }

    /**
     * Add an invalid medication and verify it is not added to the user's current medications
     */
    @Test
    public void addInvalidMedicationForDonor() {
        //Add Medication for donor.
        addNewMedicationToCurrentMedications("TESTER");

        //Check if medication added is correct.
        ListView currentMedicationList = lookup("#currentListView").queryListView();
        assertEquals(0, currentMedicationList.getItems().size());
    }

    /**
     * Adds a medication to the current medications and moves that medication to the history of medications
     */
    @Test
    public void moveMedicationToHistory() {
        //Add Medication for donor.
        addNewMedicationToCurrentMedications("Asacol");

        clickOn("Asacol");
        clickOn("#moveToHistoryButton");

        //Check if medication moved is correct.
        ListView historyMedicationList = lookup("#historyListView").queryListView();
        Medication topResult = (Medication) historyMedicationList.getItems().get(0);
        assertTrue(topResult.getName().equalsIgnoreCase("Asacol"));

    }

    /**
     * Adds a medication to the current medications and moves that medication to the history of medications
     */
    @Test
    public void moveMedicationBackToCurrent() {
        //Add Medication for donor.
        addNewMedicationToCurrentMedications("Asacol");

        clickOn("Asacol");
        clickOn("#moveToHistoryButton");

        clickOn("Asacol");
        clickOn("#moveToCurrentButton");

        //Check if medication added is correct.
        ListView currentMedicationList = lookup("#currentListView").queryListView();
        Medication topResult = (Medication) currentMedicationList.getItems().get(0);
        assertTrue(topResult.getName().equalsIgnoreCase("Asacol"));

    }

    /**
     * Adds a medication to the donor and then deletes it, checking if the deletion is successful.
     */
    @Test
    public void deleteMedicationForDonor() {
        //Add Medication for donor.
        addNewMedicationToCurrentMedications("Asacol");

        sleep(1500);
        clickOn("Asacol");
        clickOn("#deleteMedicationButton");
        sleep(200);
        clickOn("OK");

        //Check if medication added is correct.
        ListView currentMedicationList = lookup("#currentListView").queryListView();
        assertEquals(0, currentMedicationList.getItems().size());
    }

    /**
     * Adds a medication to the donor and then saves the medications, and then checks that the donor has been updated in the back end
     * as well as checking that the current medications table has been populated.
     */
    @Test
    public void saveMedicationsForDonor() {
        //Add Medication for donor.
        addNewMedicationToCurrentMedications("Asacol");

        clickOn("#saveMedicationButton");
        clickOn("OK");
        clickOn("Exit");

        //Check if medication added is correct in the Medication Array List of the User.
        TableView donorList = lookup("#profileTable").queryTableView();
        User topDonor = (User) donorList.getItems().get(0);
        assertTrue(topDonor.getCurrentMedications().get(0).getName().equalsIgnoreCase("Asacol"));

        openUserAsClinician(testUser.getName());
        clickOn("Medications");

        //Check if medication added is correct and is populated when the user re-enters the medications window.
        ListView currentMedicationList = lookup("#currentListView").queryListView();
        Medication topMedication = (Medication) currentMedicationList.getItems().get(0);
        assertTrue(topMedication.getName().equalsIgnoreCase("Asacol"));

    }

    @Test
    public void undoTest() {
        //Action 1 to undo
        addNewMedicationToCurrentMedications("Asacol");

        //Action 2 to undo
        clickOn("Asacol");
        clickOn("#moveToHistoryButton");

        //Action 3 to undo
        addNewMedicationToCurrentMedications("Ibuprofen");

        ListView currentMedicationList = lookup("#currentListView").queryListView();
        ListView historicMedicationList = lookup("#historyListView").queryListView();
        assertEquals(1, currentMedicationList.getItems().size());
        assertEquals("Ibuprofen", ((Medication) currentMedicationList.getItems().get(0)).getName());
        assertEquals(1, historicMedicationList.getItems().size());
        assertEquals("Asacol", ((Medication) historicMedicationList.getItems().get(0)).getName());

        clickOn("#undoBannerButton");
        currentMedicationList = lookup("#currentListView").queryListView();
        historicMedicationList = lookup("#historyListView").queryListView();
        assertEquals(0, currentMedicationList.getItems().size());
        assertEquals(1, historicMedicationList.getItems().size());
        assertEquals("Asacol", ((Medication) historicMedicationList.getItems().get(0)).getName());

        clickOn("#undoBannerButton");
        currentMedicationList = lookup("#currentListView").queryListView();
        historicMedicationList = lookup("#historyListView").queryListView();
        assertEquals(1, currentMedicationList.getItems().size());
        assertEquals("Asacol", ((Medication) currentMedicationList.getItems().get(0)).getName());
        assertEquals(0, historicMedicationList.getItems().size());

        clickOn("#undoBannerButton");
        currentMedicationList = lookup("#currentListView").queryListView();
        historicMedicationList = lookup("#historyListView").queryListView();
        assertEquals(0, currentMedicationList.getItems().size());
        assertEquals(0, historicMedicationList.getItems().size());
    }

    @Test
    public void redoTest() {
        //Action 1 to undo and then be discarded due to new changes
        addNewMedicationToCurrentMedications("Cidofovir");

        //Action 2 to undo and then redo
        clickOn("Cidofovir");
        clickOn("#moveToHistoryButton");

        //Action 3 to undo and then redo
        addNewMedicationToCurrentMedications("Ibuprofen");

        clickOn("#undoBannerButton");
        clickOn("#undoBannerButton");
        clickOn("#undoBannerButton");
        ListView currentMedicationList = lookup("#currentListView").queryListView();
        ListView historicMedicationList = lookup("#historyListView").queryListView();
        assertEquals(0, currentMedicationList.getItems().size());
        assertEquals(0, historicMedicationList.getItems().size());

        clickOn("#redoBannerButton");
        currentMedicationList = lookup("#currentListView").queryListView();
        historicMedicationList = lookup("#historyListView").queryListView();
        assertEquals(1, currentMedicationList.getItems().size());
        assertEquals("Cidofovir", ((Medication) currentMedicationList.getItems().get(0)).getName());
        assertEquals(0, historicMedicationList.getItems().size());

        clickOn("#redoBannerButton");
        currentMedicationList = lookup("#currentListView").queryListView();
        historicMedicationList = lookup("#historyListView").queryListView();
        assertEquals(0, currentMedicationList.getItems().size());
        assertEquals(1, historicMedicationList.getItems().size());
        assertEquals("Cidofovir", ((Medication) historicMedicationList.getItems().get(0)).getName());

        //This change should clear the redo stack
        clickOn("Cidofovir");
        clickOn("#moveToCurrentButton");
        assertEquals(1, currentMedicationList.getItems().size());
        assertEquals("Cidofovir", ((Medication) currentMedicationList.getItems().get(0)).getName());
        assertEquals(0, historicMedicationList.getItems().size());

        //The redo button should now be disabled and the GUI should not change when it is clicked
        clickOn("#redoBannerButton");
        assertEquals(1, currentMedicationList.getItems().size());
        assertEquals("Cidofovir", ((Medication) currentMedicationList.getItems().get(0)).getName());
        assertEquals(0, historicMedicationList.getItems().size());
    }


    @Test
    public void compareDrugsWithInteractionSymptoms_returnsCorrectResults() throws TimeoutException {
        addNewMedicationToCurrentMedications("Diazepam");
        addNewMedicationToCurrentMedications("Escitalopram");
        Node drugARow = from(lookup("#currentListView")).lookup("Escitalopram").query();
        clickOn(drugARow);
        clickOn("#compareButton");
        Node drugBRow = from(lookup("#currentListView")).lookup("Diazepam").query();
        clickOn(drugBRow);
        clickOn("#compareButton");
        waitForEnabled(5, "#compareButton");
        Label resultsLabel = lookup("#interactionsContentLabel").query();
        String results = resultsLabel.getText().toLowerCase();
        String[] toCheck = {"fatigue: 1 - 6 months", "nausea: < 1 month", "drug ineffective: < 1 month", "weight increased: 1 - 2 years", "dizziness: 2 - 5 years", "suicidal ideation: 6 - 12 months"};
        for (String item : toCheck) {
            TestCase.assertTrue(results.contains(item));
        }
    }
}