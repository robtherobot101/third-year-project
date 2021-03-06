package seng302.TestFX;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.apache.http.client.HttpResponseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import seng302.gui.TFScene;
import seng302.generic.WindowManager;
import seng302.User.User;

import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

public class CreateAndLoginGUITest extends TestFXTest {

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    @Before
    public void setup() throws HttpResponseException {
        //WindowManager.getDataManager().getGeneral().reset("masterToken");
        WindowManager.resetScene(TFScene.createAccount);
        //DataManager.users.clear();
    }

    @Test
    public void createValidUser() {
        clickOn("#createAccountButton");

        clickOn("#usernameInput");
        write("buzz");
        clickOn("#emailInput");
        write("mkn29@uclive.ac.nz");
        clickOn("#nhiInput");
        write("lsm4962");
        clickOn("#passwordInput");
        write("password123");
        clickOn("#passwordConfirmInput");
        write("password123");
        clickOn("#dateOfBirthInput");
        write("12/06/1997");
        clickOn("#firstNameInput");
        write("Matthew");
        clickOn("#middleNamesInput");
        write("Pieter");
        clickOn("#lastNameInput");
        write("Knight");

        clickOn("#createAccountButton");
        sleep(500);
        //Make sure that the create account button is no longer shown (because the account is now created and the scene should have changed)
        assertNull(lookup("#createAccountButton").query());
    }

    @Test
    public void enterInsufficientDetails() {
        clickOn("#createAccountButton");

        clickOn("#usernameInput");
        write("buzz");
        clickOn("#dateOfBirthInput");
        write("12/06/1997");
        clickOn("#passwordInput");
        write("password123");
        clickOn("#passwordConfirmInput");
        write("password123");
        try {
            //This wait call should time out as there is no name supplied and the button should not ever enable
            waitForEnabled(1, "#createAccountButton");
            fail();
        } catch (TimeoutException e) {
            clickOn("#firstNameInput");
            write("Matthew");
            clickOn("#createAccountButton");
            sleep(500);
            //Make sure that the create account button is no longer shown (because the account is now created and the scene should have changed)
            Button createAccountButton = lookup("#createAccountButton").query();
            assertTrue(createAccountButton.isDisabled());
        }
    }

    @Test
    public void passwordMismatch() {
        clickOn("#createAccountButton");

        clickOn("#usernameInput");
        write("buzz");
        clickOn("#firstNameInput");
        write("Matthew");
        clickOn("#emailInput");
        write("mkn29@uclive.ac.nz");
        clickOn("#nhiInput");
        write("lsm4962");
        clickOn("#dateOfBirthInput");
        write("12/06/1997");
        clickOn("#passwordInput");
        write("password123");
        clickOn("#passwordConfirmInput");
        write("password1234");

        clickOn("#createAccountButton");
        //Make sure the scene did not change

        verifyThat("Passwords do not match", Node::isVisible);

        //assertNotNull(lookup("#createAccountButton").query());

        //Now fix the mismatch and try again
        clickOn("#passwordInput");
        push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.DELETE);
        write("password1234");
        //verifyThat("#passwordInput", hasText("password1234"));
        clickOn("#createAccountButton");
        //sleep(250);
        //Make sure that the create account button is no longer shown (because the account is now created and the scene should have changed)
        assertNull(lookup("#createAccountButton").query());
        //verifyThat("#userDisplayText", Node::isVisible);
    }

    @Test
    public void testInvalidLoginAsUser() throws SQLException {
        User testUser = addTestUser();

        clickOn("#identificationInput");
        write(testUser.getUsername());
        clickOn("#passwordInput");
        write(testUser.getPassphrase().substring(0, 3));
        clickOn("#loginButton");
        //Make sure that the user gui is not showing
        assertNull(lookup("#undoBannerButton").query());

        clickOn("#passwordInput");
        write(testUser.getPassphrase().substring(3));
        clickOn("#loginButton");
        //Make sure that the user gui is now showing
        assertNotNull(lookup("#undoBannerButton").query());
    }

    @Test
    public void testLoginAsDefaultClinician() {
        loginAsDefaultClinician();
        //Make sure that the clinician gui is now showing
        assertNotNull(lookup("#homeButton").query());
    }
}