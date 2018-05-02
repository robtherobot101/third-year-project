package seng302.TestFX;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import seng302.Core.Procedure;
import seng302.Generic.Main;
import seng302.User.User;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class MedicalHistoryProceduresGUITest extends ApplicationTest {

    private Main mainGUI;
    private static final boolean runHeadless = true;

    private TableView<Procedure> pendingProcedureTableView, previousProcedureTableView;
    private Procedure pendingTableSelectedProcedure, previousTableSelectedProcedure;

    @BeforeClass
    public static void setupSpec() throws Exception {
        if (runHeadless) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("headless.geometry", "1920x1080-32");
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
        mainGUI = new Main();
        mainGUI.start(stage);
    }

    /**
     * Refreshes the currently selected procedures in both tables of Medical History (Procedures)
     */
    private void refreshTableSelections() {
        pendingProcedureTableView = lookup("#pendingProcedureTableView").query();
        previousProcedureTableView = lookup("#previousProcedureTableView").query();
        pendingTableSelectedProcedure = pendingProcedureTableView.getSelectionModel().getSelectedItem();
        previousTableSelectedProcedure = previousProcedureTableView.getSelectionModel().getSelectedItem();
    }

    /**
     * Method that can be called to path correctly to the stage to be tested.
     *
     */
    private void enterMedicalHistoryProceduresView() {
        // Assumed that calling method is currently on login screen

        // Checks if our test user already exists
        String[] names = new String[]{"Andrew", "French"};
        ObservableList<User> results = mainGUI.getUserByName(names);

        // If it doesn't exist -> add the user
        if (results.isEmpty()) {
            System.out.println("MedicalHistoryProceduresGUITest: Test user not found -> adding test user");
            clickOn("#createAccountButton");
            clickOn("#usernameInput").write("andy");
            clickOn("#emailInput").write("afr66@uclive.ac.nz");
            clickOn("#passwordInput").write("password123");
            clickOn("#passwordConfirmInput").write("password123");
            clickOn("#firstNameInput").write("Andrew");
            clickOn("#middleNamesInput").write("Robert");
            clickOn("#lastNameInput").write("French");
            clickOn("#dateOfBirthInput").write("04/08/1997");
            doubleClickOn("#createAccountButton");

            // Logout to be able to login as a clinician
            clickOn("#logoutButton");
            clickOn("OK");
        }
        System.out.println("MedicalHistoryProceduresGUITest: Logging in as default clinician");
        // Login as default clinician
        //clickOn("#identificationInput");
        clickOn("#identificationInput").write("default");
        clickOn("#passwordInput").write("default");
        doubleClickOn("#loginButton");

        System.out.println("MedicalHistoryGUITest: Selecting test user -> entering medical history");
        // Click on the Created User in clinician table and enter the medications panel.
        doubleClickOn("Andrew Robert French");
        WaitForAsyncUtils.waitForFxEvents();

        // Coords of the button #medicalHistoryButton. Needs to be hardcoded as a workaround to a TestFX bug
        clickOn("#proceduresButton");
        //doubleClickOn(636, 435);


        // This is an interesting piece here, for some reason this is required to allow JavaFX to catch up and bring
        // the medical history pane up in time for testing

    }

    /**
     * Adds a new procedure to the user's pending procedures table view
     */
    private void addNewProcedureToPendingProcedures() {
        enterMedicalHistoryProceduresView();

        clickOn("#summaryInput").write("Arm Transplant");
        clickOn("#descriptionInput").write("Transfer of arm");
        clickOn("#dateOfProcedureInput").write("4/04/2020");
        clickOn("#addNewProcedureButton");
    }


    /**
     * Add a completely valid procedure (both a pending and previous procedure)
     */
    @Test
    public void addAllValidProcedure() {
        enterMedicalHistoryProceduresView();

        //Pending Procedure
        clickOn("#summaryInput").write("Knee Replacement");
        clickOn("#descriptionInput").write("Elective Surgery; Making new knee");
        clickOn("#dateOfProcedureInput").write("9/1/2020");
        clickOn("#isOrganAffectingCheckBox");
        clickOn("#addNewProcedureButton");
        clickOn("Elective Surgery; Making new knee");
        refreshTableSelections();
        assertEquals(LocalDate.of(2020, 1, 9), pendingTableSelectedProcedure.getDate());
        assertEquals("Knee Replacement", pendingTableSelectedProcedure.getSummary());
        assertEquals("Elective Surgery; Making new knee", pendingTableSelectedProcedure.getDescription());
        assertTrue(pendingTableSelectedProcedure.isOrganAffecting());
        verifyThat("* Knee Replacement", isVisible());

        //Previous Procedure
        clickOn("#summaryInput").write("Heart Transplant");
        clickOn("#descriptionInput").write("Replacement of heart with new heart");
        clickOn("#dateOfProcedureInput").write("9/1/2000");
        clickOn("#addNewProcedureButton");
        clickOn("Replacement of heart with new heart");
        refreshTableSelections();
        assertEquals(LocalDate.of(2000, 1, 9), previousTableSelectedProcedure.getDate());
        assertEquals("Heart Transplant", previousTableSelectedProcedure.getSummary());
        assertEquals("Replacement of heart with new heart", previousTableSelectedProcedure.getDescription());
        assertFalse(previousTableSelectedProcedure.isOrganAffecting());
        verifyThat("Heart Transplant", isVisible());
    }


    /**
     * Add a procedure with an empty diagnosis but valid date
     */
    @Test
    public void addProcedureEmptySummaryAndDescription() {
        enterMedicalHistoryProceduresView();
        clickOn("#dateOfProcedureInput").write("9/1/2020");
        clickOn("#addNewProcedureButton");

        // Checks an alert dialog was presented -> this checks disease was not added
        clickOn("OK");
    }


    /**
     * Add a disease with an empty date of diagnosis but valid diagnosis
     */
    @Test
    public void addProcedureEmptyDate() {
        enterMedicalHistoryProceduresView();
        clickOn("#summaryInput").write("Arm Transplant");
        clickOn("#addNewProcedureButton");

        // Checks an alert dialog was presented -> this checks disease was not added
        clickOn("OK");
    }

    /**
     * Add a procedure with a valid diagnosis with date before user's date of birth
     */
    @Test
    public void addProcedureDateBeforeDOB() {
        enterMedicalHistoryProceduresView();
        clickOn("#summaryInput").write("Arm Transplant");
        clickOn("#descriptionInput").write("Transfer of arm");
        clickOn("#dateOfProcedureInput").write("4/04/2002");
        clickOn("#addNewProcedureButton");

        // Checks an alert dialog was presented -> this checks disease was not added
        clickOn("OK");
    }

    /**
     * Add a valid procedure, then check if the organ affecting toggle updates the procedure and gives visual feedback
     */
    @Test
    public void checkOrganAffectingToggle() {


        addNewProcedureToPendingProcedures();
        pendingProcedureTableView = lookup("#pendingProcedureTableView").query();
        // Check disease was added correctly
        clickOn("Arm Transplant");
        pendingTableSelectedProcedure = pendingProcedureTableView.getSelectionModel().getSelectedItem();
        assertFalse(pendingTableSelectedProcedure.isOrganAffecting());

        // Set it to chronic
        rightClickOn("Arm Transplant");
        clickOn("Mark procedure as organ affecting");
        assertTrue(pendingTableSelectedProcedure.isOrganAffecting());
        // Check the disease was visually updated
        verifyThat("* Arm Transplant", isVisible());

        // Toggle it back
        rightClickOn("* Arm Transplant");
        clickOn("Mark procedure as non organ affecting");
        assertFalse(pendingTableSelectedProcedure.isOrganAffecting());
        verifyThat("Arm Transplant", isVisible());
    }


    /**
     * Checks when a procedure is updated, changes are reflected appropriately
     */
    @Test
    public void updateProcedure() {
        addNewProcedureToPendingProcedures();
        // Check procedure was added correctly
        clickOn("Arm Transplant");
        refreshTableSelections();
        assertEquals("Arm Transplant", pendingTableSelectedProcedure.getSummary());
        assertEquals("Transfer of arm", pendingTableSelectedProcedure.getDescription());
        assertEquals(LocalDate.of(2020, 4, 4), pendingTableSelectedProcedure.getDate());
        assertNull(previousTableSelectedProcedure);

        rightClickOn("Arm Transplant");
        clickOn("Update pending procedure");
        clickOn("#procedureSummary").write("Leg Removal");
        clickOn("#procedureDescription").write("Removal of leg");
        clickOn("#dateDue").write("3/04/2021");
        clickOn("Update");
        clickOn("Removal of leg");
        refreshTableSelections();
        assertEquals("Leg Removal", pendingTableSelectedProcedure.getSummary());
        assertEquals("Removal of leg", pendingTableSelectedProcedure.getDescription());
        assertEquals(LocalDate.of(2021, 4, 3), pendingTableSelectedProcedure.getDate());
        assertNull(previousTableSelectedProcedure);

        //Update with due date in the past
        rightClickOn("Leg Removal");
        clickOn("Update pending procedure");
        clickOn("#dateDue").write("3/04/2017");
        clickOn("#procedureDescription");
        clickOn("Update");
        clickOn("Leg Removal");
        refreshTableSelections();
        assertEquals("Leg Removal", previousTableSelectedProcedure.getSummary());
        assertEquals("Removal of leg", previousTableSelectedProcedure.getDescription());
        assertEquals(LocalDate.of(2017, 4, 3), previousTableSelectedProcedure.getDate());
        assertNull(pendingTableSelectedProcedure);

    }

    /**
     * Adds a procedure to the user and then deletes it, checking if the deletion is successful.
     */
    @Test
    public void deleteProcedure() {
        //Add Procedure for user.
        addNewProcedureToPendingProcedures();

        clickOn("Arm Transplant");
        clickOn("#deleteProcedureButton");
        sleep(200);
        clickOn("OK");

        //Check if medication added is correct.
        pendingProcedureTableView = lookup("#pendingProcedureTableView").query();
        assertEquals(0, pendingProcedureTableView.getItems().size());
    }


    /**
     * Adds a medication to the donor and then saves the medications, and then checks that the donor has been updated in the back end
     * as well as checking that the current medications table has been populated.
     */
    @Test
    public void saveProcedure() {
        //Add Medication for donor.
        addNewProcedureToPendingProcedures();

        clickOn("#saveProcedureButton");
        sleep(200);
        clickOn("OK");
        clickOn("Exit");
        sleep(200);
        clickOn("OK");

        //Check if procedure added is correct in the Medication Array List of the User.
        TableView donorList = lookup("#profileTable").queryTableView();
        User topDonor = (User) donorList.getItems().get(0);
        assertTrue(topDonor.getPendingProcedures().get(0).getSummary().equalsIgnoreCase("Arm Transplant"));
        assertTrue(topDonor.getPendingProcedures().get(0).getDescription().equalsIgnoreCase("Transfer of arm"));
        assertEquals(LocalDate.of(2020, 4, 4), topDonor.getPendingProcedures().get(0).getDate());

        doubleClickOn("Andrew Robert French");
        WaitForAsyncUtils.waitForFxEvents();

        // Coords of the button #medicalHistoryButton. Needs to be hardcoded as a workaround to a TestFX bug
        clickOn("#proceduresButton");

        //Check if medication added is correct and is populated when the user re-enters the medications window.
        pendingProcedureTableView = lookup("#pendingProcedureTableView").query();
        Procedure topProcedure = pendingProcedureTableView.getItems().get(0);
        assertEquals("Arm Transplant", topProcedure.getSummary());
        assertEquals("Transfer of arm", topProcedure.getDescription());
        assertEquals(LocalDate.of(2020, 4, 4), topProcedure.getDate());

        //Get rid of procedure to not affect further tests
        clickOn("Arm Transplant");
        clickOn("#deleteProcedureButton");
        sleep(200);
        clickOn("OK");
        clickOn("#saveProcedureButton");
        sleep(200);
        clickOn("OK");
    }

}
