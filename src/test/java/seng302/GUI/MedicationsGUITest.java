package seng302.GUI;

import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.*;
import org.junit.runners.JUnit4;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import seng302.Core.Donor;
import seng302.Core.Main;
import seng302.Core.Medication;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

public class MedicationsGUITest extends ApplicationTest {

    private static final boolean runHeadless = false;

    /**
     * Ensures the tests are run in background if the property runHeadless == true
     *
     * Note: tests still take the same amount of time in background
     *
     * @throws Exception
     */
    @BeforeClass
    public static void setupSpec() throws Exception {
        if (runHeadless) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("headless.geometry", "1600x1200-32");
        }
        registerPrimaryStage();
    }

    @Before
    public void setUp () throws Exception {

    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Override
    public void start (Stage stage) throws Exception {
        Main mainGUI = new Main();
        mainGUI.start(stage);
    }

    /**
     * Method that can be called to path correctly to the stage to be tested.
     *
     * Hot tip: All tests start on the app launch screen and we need to navigate to the area to be tested.
     */
    private void enterMedicationPanel() {

        Main.donors.clear();
        // Assumed that calling method is currently on login screen
        clickOn("#createAccountButton");

        // Create a valid user
        clickOn("#usernameInput").write("bflame");
        clickOn("#emailInput").write("flameman@hotmail.com");
        clickOn("#passwordInput").write("password123");
        clickOn("#passwordConfirmInput").write("password123");
        clickOn("#firstNameInput").write("Bobby");
        clickOn("#middleNamesInput").write("Dong");
        clickOn("#lastNameInput").write("Flame");
        clickOn("#dateOfBirthInput").write("4/8/1969");

        doubleClickOn("#createAccountButton");

        // Logout to be able to login as a clinician
        clickOn("#logoutButton");
        clickOn("OK");

        // Login as default clinician
        clickOn("#identificationInput");
        clickOn("#identificationInput").write("default");
        clickOn("#passwordInput").write("default");
        clickOn("#loginButton");


        //Click on the Created User in clinician table and enter the medications panel.
        doubleClickOn("Bobby Dong Flame");
        clickOn("Medications");

    }

    /**
     * Method to add a new medication to a user's current medications list.
     * @param medication The medication to add.
     */
    private void addNewMedicationToCurrentMedications(String medication) {

        enterMedicationPanel();

        //Add a new medication for the donor.

        clickOn("#newMedicationField").write(medication);
        clickOn("#donorNameLabel");
        clickOn("#addNewMedicationButton");
    }

    /**
     * Add a simple medication and verify it is correct
     */
    @Test
    public void addMedicationForDonor(){

        //Add Medication for donor.
        addNewMedicationToCurrentMedications("Asacol");

        //Check if medication added is correct.
        ListView currentMedicationList = lookup("#currentListView").queryListView();
        Medication topResult = (Medication) currentMedicationList.getItems().get(0);
        org.junit.Assert.assertTrue(topResult.getName().equalsIgnoreCase("Asacol"));
    }

    /**
     * Add an invalid medication and verify it is not added to the user's current medications
     */
    @Test
    public void addInvalidMedicationForDonor(){

        //Add Medication for donor.
        addNewMedicationToCurrentMedications("TESTER");

        //Check if medication added is correct.
        ListView currentMedicationList = lookup("#currentListView").queryListView();
        org.junit.Assert.assertTrue(currentMedicationList.getItems().size() == 0);
    }

    /**
     * Adds a medication to the current medications and moves that medication to the history of medications
     */
    @Test
    public void moveMedicationToHistory(){

        //Add Medication for donor.
        addNewMedicationToCurrentMedications("Asacol");

        clickOn("Asacol");
        clickOn("#moveToHistoryButton");

        //Check if medication moved is correct.
        ListView historyMedicationList = lookup("#historyListView").queryListView();
        Medication topResult = (Medication) historyMedicationList.getItems().get(0);
        org.junit.Assert.assertTrue(topResult.getName().equalsIgnoreCase("Asacol"));

    }

    /**
     * Adds a medication to the current medications and moves that medication to the history of medications
     */
    @Test
    public void moveMedicationBackToCurrent(){

        //Add Medication for donor.
        addNewMedicationToCurrentMedications("Asacol");

        clickOn("Asacol");
        clickOn("#moveToHistoryButton");

        clickOn("Asacol");
        clickOn("#moveToCurrentButton");

        //Check if medication added is correct.
        ListView currentMedicationList = lookup("#currentListView").queryListView();
        Medication topResult = (Medication) currentMedicationList.getItems().get(0);
        org.junit.Assert.assertTrue(topResult.getName().equalsIgnoreCase("Asacol"));

    }

    /**
     * Adds a medication to the donor and then deletes it, checking if the deletion is successful.
     */
    @Test
    public void deleteMedicationForDonor(){

        //Add Medication for donor.
        addNewMedicationToCurrentMedications("Asacol");

        clickOn("Asacol");
        clickOn("#deleteMedicationButton");
        clickOn("OK");

        //Check if medication added is correct.
        ListView currentMedicationList = lookup("#currentListView").queryListView();
        org.junit.Assert.assertTrue(currentMedicationList.getItems().size() == 0);

    }

    /**
     * Adds a medication to the donor and then saves the medications, and then checks that the donor has been updated in the back end
     * as well as checking that the current medications table has been populated.
     */
    @Test
    public void saveMedicationsForDonor(){

        //Add Medication for donor.
        addNewMedicationToCurrentMedications("Asacol");

        clickOn("#saveMedicationButton");
        clickOn("OK");
        clickOn("Exit");
        clickOn("OK");

        //Check if medication added is correct in the Medication Array List of the Donor.
        TableView donorList = lookup("#profileTable").queryTableView();
        Donor topDonor = (Donor) donorList.getItems().get(0);
        org.junit.Assert.assertTrue(topDonor.getCurrentMedications().get(0).getName().equalsIgnoreCase("Asacol"));

        doubleClickOn("Bobby Dong Flame");
        clickOn("Medications");

        //Check if medication added is correct and is populated when the user re-enters the medications window.
        ListView currentMedicationList = lookup("#currentListView").queryListView();
        Medication topMedication = (Medication) currentMedicationList.getItems().get(0);
        org.junit.Assert.assertTrue(topMedication.getName().equalsIgnoreCase("Asacol"));


    }


}