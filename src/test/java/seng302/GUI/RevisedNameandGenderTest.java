package seng302.GUI;

import javafx.scene.control.ComboBox;
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
import seng302.Core.User;
import seng302.Core.Main;
import seng302.Core.Medication;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

import static org.testfx.api.FxToolkit.registerPrimaryStage;

public class RevisedNameandGenderTest extends ApplicationTest {

    private static final boolean runHeadless = true;

    private Main mainGUI;

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

    private void enterAttributesPanel() {

        Main.users.clear();
        // Assumed that calling method is currently on login screen
        clickOn("#createAccountButton");

        // Create a valid user
        clickOn("#usernameInput").write("test");
        clickOn("#emailInput").write("testie@testmail.com");
        clickOn("#passwordInput").write("password123");
        clickOn("#passwordConfirmInput").write("password123");
        clickOn("#firstNameInput").write("Testie");
        clickOn("#middleNamesInput").write("Test");
        clickOn("#lastNameInput").write("McTest");
        clickOn("#dateOfBirthInput").write("20/4/1969");

        doubleClickOn("#createAccountButton");

        // Logout to be able to login as a clinician
        clickOn("#logoutButton");
        clickOn("OK");

        // Login as default clinician
        //clickOn("#identificationInput");
        clickOn("#identificationInput").write("default");
        clickOn("#passwordInput").write("default");
        clickOn("#loginButton");


        //Click on the Created User in clinician table and enter the medications panel.
        doubleClickOn("Testie Test McTest");
        clickOn("#attributesButton");
    }


    @Test
    public void changeNameTest(){

        enterAttributesPanel();

        //Add a new medication for the user.
        doubleClickOn("#firstNameField").write("New");
        doubleClickOn("#middleNameField").write("Name");
        doubleClickOn("#lastNameField").write("Test");
        clickOn("#saveButton");

        Assert.assertEquals(Main.users.get(0).getName(), "Testie Test McTest");
        Assert.assertEquals(Main.users.get(0).getPreferredName(),"New Name Test");

    }

    @Test
    public void changeGenderTest(){

        enterAttributesPanel();

        Assert.assertEquals(Main.users.get(0).getGender(), null);
        Assert.assertEquals(Main.users.get(0).getGenderIdentity(), null);

        clickOn("#genderComboBox").clickOn("Male");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getGender().toString(), "Male");
        Assert.assertEquals(Main.users.get(0).getGenderIdentity().toString(), "Male");

        clickOn("#genderComboBox").clickOn("Female");
        clickOn("#saveButton");

        push(KeyCode.getKeyCode("Enter"));

        Assert.assertEquals(Main.users.get(0).getGender().toString(), "Male");
        Assert.assertEquals(Main.users.get(0).getGenderIdentity().toString(), "Female");

    }
}
