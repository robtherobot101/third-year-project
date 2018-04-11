package seng302.GUI;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.Core.Main;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

public class LoginGUITest extends ApplicationTest {

    private Main mainGUI;
    private static final boolean runHeadless = true;

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
        mainGUI = new Main();
        mainGUI.start(stage);
    }

    /**
     * Method that can be called to path correctly to the stage to be tested.
     *
     * Hot tip: All tests start on the app launch screen and we need to navigate to the area to be tested.
     */
    private void enterAccountCreation() {
        // Assumed that calling method is currently on login screen
        clickOn("#createAccountButton");
    }

    /**
     * Simple proof of concept test of inputting a valid donor for testing TestFX
     */
    @Test
    public void createValidUser(){
        enterAccountCreation();
        clickOn("#usernameInput"); write("buzz");
        clickOn("#emailInput"); write("mkn29@uclive.ac.nz");
        clickOn("#passwordInput"); write("password123");
        clickOn("#passwordConfirmInput"); write("password123");
        clickOn("#firstNameInput"); write("Matthew");
        clickOn("#middleNamesInput"); write("Pieter");
        clickOn("#lastNameInput"); write("Knight");
        clickOn("#dateOfBirthInput"); write("12/06/1997");
        clickOn("#createAccountButton");
    }

    @Test
    public void loginAsDefaultClinician() {
        clickOn("#identificationInput").write("default");
        clickOn("#passwordInput"); write("default");
        clickOn("#loginButton");
    }
}