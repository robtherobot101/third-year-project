package seng302.TestFX;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import seng302.Generic.WindowManager;
import seng302.User.Clinician;
import seng302.User.User;

import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class ReceiverGUITest extends TestFXTest {

    private User testUser;

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    @Before
    public void setUp() {
        testUser = addTestUser();
    }

    @Test
    public void navigateToWaitingList() {
        loginAsDefaultClinician();
        openUserAsClinician(testUser.getName());
        sleep(500);
        clickOn("#waitingListButton");
        //TODO add a check to make sure the waiting list is being shown
    }
}