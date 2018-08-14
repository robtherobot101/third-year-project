package seng302.TestFX;

import org.apache.http.client.HttpResponseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import seng302.Generic.Debugger;
import seng302.Generic.WindowManager;
import seng302.User.User;

import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public class ReceiverGUITest extends TestFXTest {

    private User testUser;

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    @Before
    public void setUp() throws SQLException {
        try {
            WindowManager.getDataManager().getGeneral().reset(null);
        } catch (HttpResponseException e) {
            Debugger.error("Failed to reset the Database.");
        }
        testUser = addTestUser();
    }

    @Ignore
    @Test
    public void navigateToWaitingList() {
        loginAsDefaultClinician();
        openUserAsClinician(testUser.getName());
        sleep(500);
        clickOn("#waitingListButton");
        //TODO add a check to make sure the waiting list is being shown
    }
}