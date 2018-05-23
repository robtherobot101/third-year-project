package seng302.TestFX;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import seng302.User.Attribute.Gender;
import seng302.User.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

public class DrugInteractionGUITest extends TestFXTest {

    private String testDrugA = "Escitalopram";
    private String testDrugB = "Diazepam";
    private HashSet<String> symptomsForAAndB = new HashSet<>(Arrays.asList(
        "-fatigue: 1 - 6 months",
        "-nausea: < 1 month",
        "-drug ineffective: < 1 month",
        "-weight increased: 1 - 2 years",
        "-dizziness: 2 - 5 years",
        "-headache",
        "-suicidal ideation: 6 - 12 months"
    ));

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    @Before
    public void setUp() {
        User user = addTestUser();
        user.setGender(Gender.FEMALE);
        user.setDateOfBirth(LocalDate.of(1985, 12, 12));
        loginAsDefaultClinician();
        openUserAsClinician(user.getName());
        sleep(500);
        clickOn("Medications");
    }

    /**
     * Types the given drug into the add medication field and adds it to the
     * user's list of medications. Only medications which are recognised by the
     * auto-complete box should be passed here. This method should only be called while in a
     * user's medications pane with the controls enabled. Only
     *
     * @param drug The given medication which is recognised by the add medication auto-complete box
     * @throws TimeoutException If the auto-complete does not recognise the drug within 10 seconds.
     */
    private void addValidMedication(String drug) throws TimeoutException {
        clickOn("#newMedicationField").write(drug.substring(0, drug.length() - 1));
        waitForNodeVisible(10, drug);
        clickOn(drug);
        clickOn("#addNewMedicationButton");
    }

    /**
     * Adds the given medications to the user's medications list and compares them for symptom interactions.
     * Only medications which are recognised by the auto-complete box should be passed here.
     * This method should only be called while in a user's medications pane with the controls enabled.
     *
     * @param drugA The name of the first medication
     * @param drugB The name of the second medication
     * @throws TimeoutException If either medication is not recognised by the auto-complete box within 10 seconds.
     */
    private void compareMedications(String drugA, String drugB) throws TimeoutException {
        Node drugARow = from(lookup("#historyListView").query(), lookup("#currentListView").query()).lookup(drugA).query();
        clickOn(drugARow);
        waitForNodeEnabled(10, "#compareButton");
        clickOn("#compareButton");

        Node drugBRow = from(lookup("#historyListView").query(), lookup("#currentListView").query()).lookup(drugB).query();
        clickOn(drugBRow);
        waitForNodeEnabled(10, "#compareButton");
        clickOn("#compareButton");
        waitForNodeEnabled(10, "#compareButton");
    }

    /**
     * Adds the given medications to the user's medications list and compares them for symptom interactions.
     * Only medications which are recognised by the auto-complete box should be passed here.
     * This method should only be called while in a user's medications pane with the controls enabled.
     *
     * @param drugA The name of the first medication
     * @param drugB The name of the second medication
     * @throws TimeoutException If either medication is not recognised by the auto-complete box within 10 seconds.
     */
    private void addAndCompare(String drugA, String drugB) throws TimeoutException {
        addValidMedication(drugA);
        addValidMedication(drugB);
        compareMedications(drugA, drugB);
    }

    /**
     * Takes the symptoms from the interactionsContentLabel in a user's medications pane and
     * returns them as a set of strings.
     *
     * @return A set of symptom strings
     */
    private HashSet<String> getResultSet() {
        Label resultLabel = lookup("#interactionsContentLabel").query();
        return new HashSet<>(Arrays.asList(resultLabel.getText().split(System.lineSeparator())));
    }

    @Ignore
    @Test
    public void compareDrugsWithInteractionSymptoms_returnsCorrectResults() throws TimeoutException {
        addAndCompare(testDrugA, testDrugB);
        assertEquals(symptomsForAAndB, getResultSet());
    }

    @Ignore
    @Test
    public void compareDrugsWithInteractionSymptomsPassedInReverseOrder_returnsCorrectResults() throws TimeoutException {
        addAndCompare(testDrugA, testDrugB);
        assertEquals(symptomsForAAndB, getResultSet());
    }

    @Ignore
    @Test
    public void compareDrugsWithInteractionSymptomsFromHistory_returnsCorrectResults() throws TimeoutException {
        waitForNodeVisible(10, "#moveToHistoryButton");

        addValidMedication(testDrugA);
        addValidMedication(testDrugB);

        clickOn(testDrugA);
        waitForNodeEnabled(10, "#moveToHistoryButton");
        clickOn("#moveToHistoryButton");

        clickOn(testDrugB);
        waitForNodeEnabled(10, "#moveToHistoryButton");
        clickOn("#moveToHistoryButton");

        compareMedications(testDrugA, testDrugB);
        assertEquals(symptomsForAAndB, getResultSet());
    }

    @Ignore
    @Test
    public void compareDrugsWhichHaveNoInteractionsForUser_noResults() throws TimeoutException {
        addAndCompare("Marinol", "Codeine sulfate");
        waitForNodeEnabled(10, "#compareButton");

        HashSet<String> expected = new HashSet<>();
        expected.add("No interactions.");
        assertEquals(expected, getResultSet());
    }

    @Ignore
    @Test
    public void compareDrugsWithoutInformation_noResultsAndUserNotified() throws TimeoutException {
        addAndCompare("Selsun", "Inversine");
        waitForNodeEnabled(10, "#compareButton");

        HashSet<String> expected = new HashSet<>();
        expected.add("Could not retrieve interaction symptoms (no information available).");
        assertEquals(expected, getResultSet());
    }

    @Ignore
    @Test
    public void compareDrugsWhichCauseInternalServerError_noResultsAndUserNotified() throws TimeoutException {
        addAndCompare("Codeine sulfate", "Maxolon");
        waitForNodeEnabled(10, "#compareButton");

        HashSet<String> expected = new HashSet<>(Arrays.asList(
            "Could not retrieve interaction symptoms (an error occurred on the server)."
        ));
        assertEquals(expected, getResultSet());
    }
}