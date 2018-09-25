package seng302.TestFX;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.TimeoutException;
import javafx.scene.control.TableView;
import org.apache.http.client.HttpResponseException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;
import seng302.generic.WindowManager;
import seng302.User.Procedure;
import seng302.User.Attribute.Organ;
import seng302.User.User;

public class MedicalHistoryProceduresGUITest extends TestFXTest {
    private TableView<Procedure> pendingProcedureTableView, previousProcedureTableView;
    private Procedure pendingTableSelectedProcedure, previousTableSelectedProcedure;

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
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
     * @throws SQLException catch sql execution exceptions
     * @throws HttpResponseException catch connection errors
     */
    private void enterMedicalHistoryProceduresView() throws SQLException, HttpResponseException {
        // Assumed that calling method is currently on login screen
        WindowManager.getDataManager().getGeneral().reset("masterToken");
        addTestUser();
        loginAsDefaultClinician();

        // Click on the Created User in clinician table and enter the medications panel.
        doubleClickOn("Bobby Dong Flame");
        WaitForAsyncUtils.waitForFxEvents();

        // Coords of the button #medicalHistoryButton. Needs to be hardcoded as a workaround to a TestFX bug
        clickOn("#proceduresButton");
        //doubleClickOn(636, 435);

        // This is an interesting piece here, for some reason this is required to allow JavaFX to catch up and bring
        // the medical history pane up in time for testing

    }

    /**
     * Adds a new procedure to the user's pending procedures table view
     * @throws SQLException catch sql execution exceptions
     * @throws HttpResponseException catch connection errors
     */
    private void addNewProcedureToPendingProcedures() throws SQLException, HttpResponseException {
        enterMedicalHistoryProceduresView();

        clickOn("#summaryInput").write("Arm Transplant");
        clickOn("#descriptionInput").write("Transfer of arm");
        clickOn("#dateOfProcedureInput").write("4/04/2020");
        clickOn("#addNewProcedureButton");
    }


    /**
     * Add a completely valid procedure (both a pending and previous procedure)
     * @throws SQLException catch sql execution exceptions
     * @throws HttpResponseException catch connection errors
     */
    @Test
    public void addAllValidProcedure() throws SQLException, HttpResponseException{
        enterMedicalHistoryProceduresView();

        //Pending Procedure
        clickOn("#summaryInput").write("Knee Replacement");
        clickOn("#descriptionInput").write("Elective Surgery; Making new knee");
        clickOn("#dateOfProcedureInput").write("9/1/2020");
        clickOn("#organAffectChoiceBox");
        clickOn("#pancreasCheckBox");
        clickOn("#lungCheckBox");
        clickOn("#heartCheckBox");
        clickOn("#addNewProcedureButton");
        clickOn("Elective Surgery; Making new knee");
        refreshTableSelections();
        assertEquals(LocalDate.of(2020, 1, 9), pendingTableSelectedProcedure.getDate());
        assertEquals("Knee Replacement", pendingTableSelectedProcedure.getSummary());
        assertEquals("Elective Surgery; Making new knee", pendingTableSelectedProcedure.getDescription());
        assertTrue(pendingTableSelectedProcedure.getOrgansAffected().contains(Organ.PANCREAS));
        assertTrue(pendingTableSelectedProcedure.getOrgansAffected().contains(Organ.LUNG));
        assertTrue(pendingTableSelectedProcedure.getOrgansAffected().contains(Organ.HEART));
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
        assertTrue(previousTableSelectedProcedure.getOrgansAffected().isEmpty());
        verifyThat("Heart Transplant", isVisible());
    }

    /**
     * Add a disease with an empty date of diagnosis but valid diagnosis
     * @throws SQLException catch sql execution exceptions
     * @throws HttpResponseException catch connection errors
     */
    @Test
    public void addProcedureEmptyDate() throws SQLException, HttpResponseException{
        enterMedicalHistoryProceduresView();
        clickOn("#summaryInput").write("Arm Transplant");
        clickOn("#addNewProcedureButton");

        // Checks an alert dialog was presented -> this checks disease was not added
        clickOn("OK");
    }

    /**
     * Checks when a procedure is updated, changes are reflected appropriately
     * @throws SQLException catch sql execution exceptions
     * @throws HttpResponseException catch connection errors
     */
    @Test
    public void updateProcedure() throws SQLException, HttpResponseException{
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
        assertTrue(pendingTableSelectedProcedure.getOrgansAffected().isEmpty());
        assertNull(previousTableSelectedProcedure);

        //Update with due date in the past
        rightClickOn("Leg Removal");
        clickOn("Update pending procedure");
        clickOn("#dateDue").write("3/04/2017");
        clickOn("#procedureDescription");
        clickOn("#updateOrganChoiceBox");
        clickOn("#pancreasCheckBox");
        clickOn("#lungCheckBox");
        clickOn("#heartCheckBox");
        clickOn("#dateDue");
        clickOn("Update");
        clickOn("Removal of leg");
        refreshTableSelections();
        assertEquals("Leg Removal", previousTableSelectedProcedure.getSummary());
        assertEquals("Removal of leg", previousTableSelectedProcedure.getDescription());
        assertEquals(LocalDate.of(2017, 4, 3), previousTableSelectedProcedure.getDate());
        assertTrue(previousTableSelectedProcedure.getOrgansAffected().contains(Organ.PANCREAS));
        assertTrue(previousTableSelectedProcedure.getOrgansAffected().contains(Organ.LUNG));
        assertTrue(previousTableSelectedProcedure.getOrgansAffected().contains(Organ.HEART));
        assertNull(pendingTableSelectedProcedure);

    }

    /**
     * Adds a procedure to the user and then deletes it, checking if the deletion is successful.
     * @throws SQLException catch sql execution exceptions
     * @throws HttpResponseException catch connection errors
     */
    @Test
    public void deleteProcedure() throws SQLException, HttpResponseException{
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
}
