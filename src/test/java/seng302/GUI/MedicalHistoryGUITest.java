package seng302.GUI;

import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runners.JUnit4;
import org.testfx.api.FxAssert;
import org.testfx.api.FxToolkit;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import seng302.Core.Disease;
import seng302.Core.Main;
import seng302.Core.User;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

public class MedicalHistoryGUITest extends ApplicationTest {

    private Main mainGUI;
    private static final boolean runHeadless = true;

    private TableView<Disease> currentDiseaseTableView, curedDiseaseTableView;
    private Disease currentTableSelectedDisease, curedTableSelectedDisease;

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
     *
     */
    private void enterMedicalHistoryView() {
        // Assumed that calling method is currently on login screen

        // Checks if our test user already exists
        String[] names = new String[]{"Matthew", "Knight"};
        ArrayList<User> results = mainGUI.getUserByName(names);

        // If it doesn't exist -> add the user
        if (results.isEmpty()) {
            System.out.println("MedicalHistoryGUITest: Test user not found -> adding test user");
            clickOn("#createAccountButton");
            clickOn("#usernameInput").write("buzz");
            clickOn("#emailInput").write("mkn29@uclive.ac.nz");
            clickOn("#passwordInput").write("password123");
            clickOn("#passwordConfirmInput").write("password123");
            clickOn("#firstNameInput").write("Matthew");
            clickOn("#middleNamesInput").write("Pieter");
            clickOn("#lastNameInput").write("Knight");
            clickOn("#dateOfBirthInput").write("12/06/1997");
            doubleClickOn("#createAccountButton");

            // Logout to be able to login as a clinician
            clickOn("#logoutButton");
            clickOn("OK");
        }
        System.out.println("MedicalHistoryGUITest: Logging in as default clinician");
        // Login as default clinician
        clickOn("#identificationInput");
        clickOn("#identificationInput").write("default");
        clickOn("#passwordInput").write("default");
        doubleClickOn("#loginButton");

        System.out.println("MedicalHistoryGUITest: Selecting test user -> entering medical history");
        // Click on the Created User in clinician table and enter the medications panel.
        doubleClickOn("Matthew Pieter Knight");
        WaitForAsyncUtils.waitForFxEvents();

        // Coords of the button #medicalHistoryButton. Needs to be hardcoded as a workaround to a TestFX bug
        doubleClickOn(636, 435);


        // This is an interesting piece here, for some reason this is required to allow JavaFX to catch up and bring
        // the medical history pane up in time for testing

    }


    /**
     * Add a completely valid disease
     */
    @Test
    public void addAllValidDisease() {
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
     */
    @Test
    public void addDiseaseEmptyName() {
        enterMedicalHistoryView();
        clickOn("#dateOfDiagnosisInput").write("4/04/2018");
        clickOn("#addNewDiseaseButton");

        // Checks an alert dialog was presented -> this checks disease was not added
        clickOn("OK");
    }

    /**
     * Add a disease with both cured and chronic marked
     */
    @Test
    public void addDiseaseCuredAndChronic() {
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
     */
    @Test
    public void addDiseaseEmptyDate() {
        enterMedicalHistoryView();
        clickOn("#newDiseaseTextField").write("Diabetes");
        clickOn("#addNewDiseaseButton");

        // Checks an alert dialog was presented -> this checks disease was not added
        clickOn("OK");
    }


    /**
     * Add a disease with a valid diagnosis with date in future
     */
    @Test
    public void addDiseaseFutureDate() {
        enterMedicalHistoryView();
        clickOn("#newDiseaseTextField").write("Asthma");
        clickOn("#dateOfDiagnosisInput").write("4/04/2020");
        clickOn("#addNewDiseaseButton");

        // Checks an alert dialog was presented -> this checks disease was not added
        clickOn("OK");
    }

    /**
     * Add a valid disease, then check if the chronic toggle updates the disease and gives visual feedback
     */
    @Test
    public void checkChronicToggle() {
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
     */
    @Test
    public void checkCuredCurrentToggle() {
        enterMedicalHistoryView();

        clickOn("#newDiseaseTextField").write("Asthma");
        clickOn("#dateOfDiagnosisInput").write("4/04/2018");
        clickOn("#addNewDiseaseButton");
        // Check disease was added correctly
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
     */
    @Test
    public void checkSettingACuredDiseaseToChronic() {
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
     */
    @Test
    public void updateDisease() {
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
        write("Lung cancer");
        clickOn("Update");
        refreshTableSelections();
        assertEquals("Lung cancer", currentTableSelectedDisease.getName());
        assertEquals(LocalDate.now(), currentTableSelectedDisease.getDiagnosisDate());
        assertNull(curedTableSelectedDisease);
    }
}