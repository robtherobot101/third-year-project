package seng302.TestFX;

import javafx.scene.control.TableView;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.util.WaitForAsyncUtils;
import seng302.User.Disease;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class MedicalHistoryGUITest extends TestFXTest {

    private TableView<Disease> currentDiseaseTableView, curedDiseaseTableView;
    private Disease currentTableSelectedDisease, curedTableSelectedDisease;

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    /**
     * Refreshes the currently selected diseases in both tables of Medical History
     */
    private void refreshTableSelections() {
        currentDiseaseTableView = lookup("#currentDiseaseTableView").query();
        curedDiseaseTableView = lookup("#curedDiseaseTableView").query();
        currentTableSelectedDisease = currentDiseaseTableView.getSelectionModel().getSelectedItem();
        curedTableSelectedDisease = curedDiseaseTableView.getSelectionModel().getSelectedItem();
    }

    /**
     * Method that can be called to path correctly to the stage to be tested.
     * @throws SQLException catch sql execution exceptions
     */
    private void enterMedicalHistoryView() throws SQLException {
        // Assumed that calling method is currently on login screen

        //DataManager.users.clear();
//        clickOn("#createAccountButton");
//        clickOn("#usernameInput").write("buzz");
//        clickOn("#emailInput").write("mkn29@uclive.ac.nz");
//        clickOn("#passwordInput").write("password123");
//        clickOn("#passwordConfirmInput").write("password123");
//        clickOn("#firstNameInput").write("Matthew");
//        clickOn("#middleNamesInput").write("Pieter");
//        clickOn("#lastNameInput").write("Knight");
//        clickOn("#dateOfBirthInput").write("12/06/1997");
//        doubleClickOn("#createAccountButton");
//
//        // Logout to be able to login as a clinician
//        clickOn("#logoutButton");
//        clickOn("OK");
        addTestUser();
        // Login as default clinician
        clickOn("#identificationInput");
        clickOn("#identificationInput").write("default");
        clickOn("#passwordInput").write("default");
        clickOn("#loginButton");

        // Click on the Created User in clinician table and enter the medications panel.
        doubleClickOn("Bobby Dong Flame");
        WaitForAsyncUtils.waitForFxEvents();

        // Coords of the button #medicalHistoryButton. Needs to be hardcoded as a workaround to a TestFX bug
        clickOn("#diseasesButton");
        //doubleClickOn(636, 435);

        // This is an interesting piece here, for some reason this is required to allow JavaFX to catch up and bring
        // the medical history pane up in time for testing

    }


    /**
     * Add a completely valid disease
     * @throws SQLException catch sql execution exceptions
     */
    @Ignore
    @Test
    public void addAllValidDisease() throws SQLException{
        enterMedicalHistoryView();
        clickOn("#newDiseaseTextField").write("Alzheimer's disease");
        clickOn("#dateOfDiagnosisInput").write("9/1/2010");
        clickOn("#addNewDiseaseButton");
        clickOn("Alzheimer's disease");
        refreshTableSelections();
        assertEquals(LocalDate.of(2010, 1, 9), currentTableSelectedDisease.getDiagnosisDate());
        assertEquals("Alzheimer's disease", currentTableSelectedDisease.getName());
        assertFalse(currentTableSelectedDisease.isCured());
        assertFalse(currentTableSelectedDisease.isChronic());

        verifyThat("Alzheimer's disease", isVisible());
    }


    /**
     * Add a disease with an empty diagnosis but valid date
     * @throws SQLException catch sql execution exceptions
     */
    @Ignore
    @Test
    public void addDiseaseEmptyName() throws SQLException{
        enterMedicalHistoryView();
        clickOn("#dateOfDiagnosisInput").write("4/04/2018");
        clickOn("#addNewDiseaseButton");

        // Checks an alert dialog was presented -> this checks disease was not added
        clickOn("OK");
    }

    /**
     * Add a disease with both cured and chronic marked
     * @throws SQLException catch sql execution exceptions
     */
    @Ignore
    @Test
    public void addDiseaseCuredAndChronic() throws SQLException{
        enterMedicalHistoryView();
        clickOn("#newDiseaseTextField").write("Alzheimer's disease");
        clickOn("#dateOfDiagnosisInput").write("9/1/2010");
        clickOn("#isCuredCheckBox");
        clickOn("#chronicCheckBox");
        clickOn("#addNewDiseaseButton");

        // Checks an alert dialog was presented -> this checks disease was not added
        clickOn("OK");
    }


    /**
     * Add a disease with an empty date of diagnosis but valid diagnosis
     * @throws SQLException catch sql execution exceptions
     */
    @Ignore
    @Test
    public void addDiseaseEmptyDate() throws SQLException{
        enterMedicalHistoryView();
        clickOn("#newDiseaseTextField").write("Diabetes");
        clickOn("#addNewDiseaseButton");

        // Checks an alert dialog was presented -> this checks disease was not added
        clickOn("OK");
    }


    /**
     * Add a disease with a valid diagnosis with date in future
     * @throws SQLException catch sql execution exceptions
     */
    @Ignore
    @Test
    public void addDiseaseFutureDate() throws SQLException{
        enterMedicalHistoryView();
        clickOn("#newDiseaseTextField").write("Asthma");
        clickOn("#dateOfDiagnosisInput").write("4/04/2020");
        clickOn("#addNewDiseaseButton");

        // Checks an alert dialog was presented -> this checks disease was not added
        clickOn("OK");
    }

    /**
     * Add a valid disease, then check if the chronic toggle updates the disease and gives visual feedback
     * @throws SQLException catch sql execution exceptions
     */
    @Ignore
    @Test
    public void checkChronicToggle() throws SQLException{
        enterMedicalHistoryView();
        currentDiseaseTableView = lookup("#currentDiseaseTableView").query();

        clickOn("#newDiseaseTextField").write("Asthma");
        clickOn("#dateOfDiagnosisInput").write("4/04/2018");
        clickOn("#addNewDiseaseButton");
        // Check disease was added correctly
        clickOn("Asthma");
        currentTableSelectedDisease = currentDiseaseTableView.getSelectionModel().getSelectedItem();
        assertFalse(currentTableSelectedDisease.isChronic());

        // Set it to chronic
        rightClickOn("Asthma");
        clickOn("Mark Asthma as chronic");
        assertTrue(currentTableSelectedDisease.isChronic());
        // Check the disease was visually updated
        verifyThat("(CHRONIC) Asthma", isVisible());

        // Toggle it back
        rightClickOn("(CHRONIC) Asthma");
        clickOn("Mark Asthma as not chronic");
        assertFalse(currentTableSelectedDisease.isChronic());
        verifyThat("Asthma", isVisible());
    }


    /**
     * Add a disease with a valid diagnosis with date in future
     * @throws SQLException catch sql execution exceptions
     */
    @Ignore
    @Test
    public void checkCuredCurrentToggle() throws SQLException{
        enterMedicalHistoryView();

        clickOn("#newDiseaseTextField").write("Asthma");
        clickOn("#dateOfDiagnosisInput").write("4/04/2018");
        clickOn("#addNewDiseaseButton");
        // Check disease was added correctly
        sleep(300);
        clickOn("Asthma");
        refreshTableSelections();

        assertFalse(currentTableSelectedDisease.isChronic());
        assertNull(curedTableSelectedDisease);

        // Set it to cured
        rightClickOn("Asthma");
        clickOn("Mark Asthma as cured");
        clickOn("Asthma");
        refreshTableSelections();
        assertNull(currentTableSelectedDisease);
        assertEquals("Asthma", curedTableSelectedDisease.getName());
        assertTrue(curedTableSelectedDisease.isCured());

        // Toggle it back to current
        rightClickOn("Asthma");
        clickOn("Mark Asthma as uncured");
        clickOn("Asthma");
        refreshTableSelections();
        assertNull(curedTableSelectedDisease);
        assertEquals("Asthma", currentTableSelectedDisease.getName());
        assertFalse(currentTableSelectedDisease.isCured());
    }

    /**
     * Checks when a cured disease is marked as chronic that it is moved to current
     * @throws SQLException catch sql execution exceptions
     */
    @Ignore
    @Test
    public void checkSettingACuredDiseaseToChronic() throws SQLException{
        enterMedicalHistoryView();

        clickOn("#newDiseaseTextField").write("Asthma");
        clickOn("#dateOfDiagnosisInput").write("4/04/2018");
        clickOn("#isCuredCheckBox");
        clickOn("#addNewDiseaseButton");
        // Check disease was added correctly
        clickOn("Asthma");
        refreshTableSelections();
        assertEquals("Asthma", curedTableSelectedDisease.getName());
        assertFalse(curedTableSelectedDisease.isChronic());
        assertTrue(curedTableSelectedDisease.isCured());
        assertNull(currentTableSelectedDisease);

        rightClickOn("Asthma");
        clickOn("Mark Asthma as chronic");
        clickOn("(CHRONIC) Asthma");
        refreshTableSelections();
        assertEquals("Asthma", currentTableSelectedDisease.getName());
        assertTrue(currentTableSelectedDisease.isChronic());
        assertFalse(currentTableSelectedDisease.isCured());
        assertNull(curedTableSelectedDisease);
    }

    /**
     * Checks when a disease is updated, changes are reflected appropriately
     * @throws SQLException catch sql execution exceptions
     */
    @Ignore
    @Test
    public void updateDisease() throws SQLException{
        enterMedicalHistoryView();

        clickOn("#newDiseaseTextField").write("Asthma");
        clickOn("#dateOfDiagnosisInput").write("4/04/2018");
        clickOn("#addNewDiseaseButton");
        // Check disease was added correctly
        clickOn("Asthma");
        refreshTableSelections();
        assertEquals("Asthma", currentTableSelectedDisease.getName());
        assertEquals(LocalDate.of(2018, 4, 4), currentTableSelectedDisease.getDiagnosisDate());
        assertNull(curedTableSelectedDisease);

        rightClickOn("Asthma");
        clickOn("Update disease");
        clickOn("#diseaseName").write("Lung cancer");
        //doubleClickOn("4/04/2018").clickOn("4/04/2018");
        clickOn("#dateOfDiagnosis").write("20/04/2018");
        doubleClickOn("Update");
        sleep(250);
        //clickOn("20/04/2018");
        clickOn("Lung cancer");
        refreshTableSelections();
        assertEquals("Lung cancer", currentTableSelectedDisease.getName());
        assertEquals(LocalDate.of(2018, 4, 20), currentTableSelectedDisease.getDiagnosisDate());
        assertNull(curedTableSelectedDisease);
    }
}