package seng302.TestFX;

import java.util.concurrent.TimeoutException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import seng302.User.User;

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