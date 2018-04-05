package seng302.GUI;

import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.Core.Donor;
import seng302.Core.Main;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

public class MedicationsTest extends ApplicationTest {

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
            System.setProperty("java.awt.headless", "true");
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
     * TODO sort out why this crashes
     *
     * Hot tip: All tests start on the app launch screen and we need to navigate to the area to be tested.
     */
    private void enterMedicationPanel() {
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
        press(KeyCode.TAB);
        release(KeyCode.TAB);


        doubleClickOn("#createAccountButton");

        // Logout to be able to login as a clinician
        clickOn("#logoutButton");
        clickOn("OK");


        // Login as default clinician
        clickOn("#identificationInput");
        clickOn("#identificationInput").write("default");
        clickOn("#passwordInput").write("default");
        clickOn("#loginButton");

        TableView searchDonorTable = lookup("#profileTable").queryTableView();
        Donor topResult = (Donor) searchDonorTable.getItems().get(0);
        org.junit.Assert.assertTrue(topResult.getName().equalsIgnoreCase("Bobby Flame"));

    }



    /**
     * Add a simple medication and verify it appears
     */
    @Test
    public void testInputMedication(){
        enterMedicationPanel();
    }
}