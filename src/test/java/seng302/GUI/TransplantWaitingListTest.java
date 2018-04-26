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
import seng302.Core.TransplantWaitingListItem;
import seng302.Core.User;
import seng302.Core.Main;
import seng302.Core.Medication;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;
import static org.junit.Assert.assertEquals;

public class TransplantWaitingListTest extends ApplicationTest{
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
        Main.users.clear();
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Override
    public void start (Stage stage) throws Exception {
        Main mainGUI = new Main();
        mainGUI.start(stage);
    }

    private void createAccounts() {
        Main.users.clear();
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

        // Logout to be able to create another account
        clickOn("#logoutButton");
        clickOn("OK");

        // Assumed that calling method is currently on login screen
        clickOn("#createAccountButton");


        // Create a valid user
        clickOn("#usernameInput").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE).write("bobr");
        clickOn("#emailInput").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE).write("bob@live.com");
        clickOn("#passwordInput").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE).write("password");
        clickOn("#passwordConfirmInput").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE).write("password");
        clickOn("#firstNameInput").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE).write("Bob");
        clickOn("#middleNamesInput").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE);
        clickOn("#lastNameInput").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE).write("Ross");
        clickOn("#dateOfBirthInput").push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE).write("12/12/1957");

        doubleClickOn("#createAccountButton");

        // Logout to be able to create another account
        clickOn("#logoutButton");
        clickOn("OK");
    }

    @Test
    public void checkForBlankTables() {
        //login as clinician
        clickOn("#identificationInput").write("default");
        clickOn("#passwordInput").write("default");
        clickOn("#loginButton");

        //navigate to table
        clickOn("#transplantList");

        //check if no recivers in table
        ListView currentWaitingList = lookup("#transplantTable").queryListView();
        assertEquals(currentWaitingList, null);
    }

    @Test
    public void checkForFullTable() {
        createAccounts();

        //login as clinician
        clickOn("#identificationInput").write("default");
        clickOn("#passwordInput").write("default");
        clickOn("#loginButton");

        //navigate to table
        clickOn("#transplantList");

        ListView currentWaitingList = lookup("#transplantTable").queryListView();
        assertEquals(currentWaitingList, null);
    }
}
