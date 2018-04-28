
package seng302.TestFX;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import seng302.User.*;
import seng302.User.Attribute.*;
import seng302.Generic.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DrugInteractionGUITest extends TestFXTest {
    private static final boolean runHeadless = true;
    private User user = new User("test,user", LocalDate.of(1983,7,4));
    private String testDrugA = "Escitalopram";
    private String testDrugB = "Diazepam";
    HashSet<String> symptomsForAAndB = new HashSet<String>(Arrays.asList(
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
    public void setUp () throws Exception {
        user = addTestUser();
        user.setGender(Gender.FEMALE);
        user.setDateOfBirth(LocalDate.of(1985,12,12));
        loginAsDefaultClinician();
        openUserAsClinician(user.getName());
        sleep(500);
        clickOn("Medications");
    }

    /**
     * Waits until the node denoted by the given id can be found and is visible.
     * If the waiting time exceeds the given timeout in seconds, a TimeOutException
     * is thrown.
     * @param timeout The timeout in seconds
     * @param id The fx identifier of the node
     * @throws TimeoutException If the waiting time exceeds the given timeout.
     */
    private void waitForNodeVisible(int timeout, String id) throws TimeoutException{
        Callable<Boolean> callable = () -> {
            Node nodeFound = lookup(id).query();
            if(nodeFound==null){
                return false;
            }else{
                if(nodeFound.isVisible()){
                    //Let the GUI skin catchup to the controller state
                    waitForFxEvents();
                    return true;
                }else{
                    return false;
                }
            }
        };
        WaitForAsyncUtils.waitFor(timeout, TimeUnit.SECONDS, callable);
    }

    /**
     * Waits until the node denoted by the given id can be found and is enabled.
     * If the waiting time exceeds the given timeout in seconds, a TimeOutException
     * is thrown.
     * @param timeout The timeout in seconds
     * @param id The fx identifier of the node
     * @throws TimeoutException If the waiting time exceeds the given timeout.
     */
    private void waitForNodeEnabled(int timeout, String id) throws TimeoutException{
        Callable<Boolean> callable = () -> {
            Node nodeFound = lookup(id).query();
            if(nodeFound==null){
                return false;
            }else{
                if(!nodeFound.isDisable()){
                    //Let the GUI skin catchup to the controller state
                    waitForFxEvents();
                    return true;
                }else{
                    return false;
                }
            }
        };
        WaitForAsyncUtils.waitFor(timeout, TimeUnit.SECONDS, callable);
    }

    /**
     * Types the given drug into the add medication field and adds it to the
     * user's list of medications. Only medications which are recognised by the
     * auto-complete box should be passed here. This method should only be called while in a
     * user's medications pane with the controls enabled. Only
     * @param drug The given medication which is recognised by the add medication auto-complete box
     * @throws TimeoutException If the auto-complete does not recognise the drug within 10 seconds.
     */
    private void addValidMedication(String drug) throws TimeoutException{
        clickOn("#newMedicationField").write(drug.substring(0,drug.length()-1));
        waitForNodeVisible(10, drug);
        clickOn(drug);
        clickOn("#addNewMedicationButton");
    }

    /**
     * Adds the given medications to the user's medications list and compares them for symptom interactions.
     * Only medications which are recognised by the auto-complete box should be passed here.
     * This method should only be called while in a user's medications pane with the controls enabled.
     * @param drugA The name of the first medication
     * @param drugB The name of the second medication
     * @throws TimeoutException If either medication is not recognised by the auto-complete box within 10 seconds.
     */
    private void compareMedications(String drugA, String drugB) throws TimeoutException{
        Node drugARow = from(lookup("#historyListView").query(), lookup("#currentListView").query()).lookup(drugA).query();
        clickOn(drugARow);
        waitForNodeEnabled(10,"#compareButton");
        clickOn("#compareButton");

        Node drugBRow = from(lookup("#historyListView").query(), lookup("#currentListView").query()).lookup(drugB).query();
        clickOn(drugBRow);
        waitForNodeEnabled(10,"#compareButton");
        clickOn("#compareButton");
        waitForNodeEnabled(10,"#compareButton");
    }

    /**
     * Adds the given medications to the user's medications list and compares them for symptom interactions.
     * Only medications which are recognised by the auto-complete box should be passed here.
     * This method should only be called while in a user's medications pane with the controls enabled.
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
     * @return A set of symptom strings
     */
    private HashSet<String> getResultSet(){
        Label resultLabel = lookup("#interactionsContentLabel").query();
        return new HashSet<>(Arrays.asList(resultLabel.getText().split(System.lineSeparator())));
    }

    @Test
    public void compareDrugsWithInteractionSymptoms_returnsCorrectResults() throws TimeoutException{
        addAndCompare(testDrugA,testDrugB);
        assertEquals(symptomsForAAndB,getResultSet());
    }

    @Test
    public void compareDrugsWithInteractionSymptomsPassedInReverseOrder_returnsCorrectResults() throws TimeoutException{
        addAndCompare(testDrugA,testDrugB);
        assertEquals(symptomsForAAndB,getResultSet());
    }

    @Test
    public void compareDrugsWithInteractionSymptomsFromHistory_returnsCorrectResults() throws TimeoutException{
        waitForNodeVisible(10, "#moveToHistoryButton");
        addValidMedication(testDrugA);
        clickOn(testDrugA);
        clickOn("#moveToHistoryButton");

        addValidMedication(testDrugB);
        clickOn(testDrugB);
        clickOn("#moveToHistoryButton");

        compareMedications(testDrugA, testDrugB);
        assertEquals(symptomsForAAndB,getResultSet());
    }

    @Test
    public void compareDrugsWhichHaveNoInteractionsForUser_noResults() throws TimeoutException{
        addAndCompare("Marinol","Codeine sulfate");
        waitForNodeEnabled(10,"#compareButton");

        HashSet<String> expected = new HashSet<>();
        expected.add("No interactions.");
        assertEquals(expected,getResultSet());
    }

    @Test
    public void compareDrugsWithoutInformation_noResultsAndUserNotified() throws TimeoutException{
        addAndCompare("Selsun","Inversine");
        waitForNodeEnabled(10,"#compareButton");

        HashSet<String> expected = new HashSet<>();
        expected.add("Could not retrieve interaction symptoms (no information available).");
        assertEquals(expected,getResultSet());
    }

    @Test
    public void compareDrugsWhichCauseInternalServerError_noResultsAndUserNotified() throws TimeoutException{
        addAndCompare("Codeine sulfate","Maxolon");
        waitForNodeEnabled(10,"#compareButton");

        HashSet<String> expected = new HashSet<>(Arrays.asList(
                "Could not retrieve interaction symptoms (an error occurred on the server)."
        ));
        assertEquals(expected,getResultSet());
    }
}