package seng302.TestFX;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import org.apache.http.client.HttpResponseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import seng302.Generic.Debugger;
import seng302.Generic.WindowManager;
import seng302.User.User;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class AdminCLITest extends TestFXTest {

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        //defaultTestSetup();
    }

    @Before
    public void setupTest() throws SQLException {
        useLocalStorage();
        loginAsDefaultAdmin();
        sleep(800);
        clickOn("#cliTabButton");
        sleep(200);
    }

    @Test
    public void tabExists() {
        GridPane mainPane = lookup("#mainPane").query();
        assertTrue(!mainPane.isVisible()); //Ensure that the search tab is no longer open
    }

    @Test
    public void embeddedCliExists() {
        TextField textField = lookup("#commandInputField").query();
        assertNotNull(textField); //Ensure that the CLI is now showing
    }

    @Ignore
    @Test
    public void checkDeletionIsConsistent() throws SQLException {
        //DataManager.users.clear();
        addTestUser();
        clickOn("Home");
        assertEquals(1, lookup("#userTableView").queryTableView().getItems().size()); //Make sure the test user is in the admin table

        clickOn("#cliTabButton");
        clickOn("#commandInputField").write("deleteuser 0");
        press(KeyCode.ENTER);
        release(KeyCode.ENTER);
        clickOn("#commandInputField").write("y");
        press(KeyCode.ENTER);
        release(KeyCode.ENTER);

        clickOn("Home");
        assertEquals(0, lookup("#userTableView").queryTableView().getItems().size()); //Make sure the test user is no longer in the admin table
    }

    @Ignore //Works non-headless but not headless?
    @Test
    public void checkDeletionClosesUserIfOpen() throws SQLException {
        //DataManager.users.clear();
        User testUser = addTestUser();
        clickOn("Home");
        doubleClickOn(testUser.getName());
        clickOn("#cliTabButton");
        clickOn("#commandInputField").write("deleteuser 0");
        press(KeyCode.ENTER);
        release(KeyCode.ENTER);
        clickOn("#commandInputField").write("y");
        press(KeyCode.ENTER);
        release(KeyCode.ENTER);
        sleep(1000);
        assertEquals(0, WindowManager.getCliniciansUserWindows().size());
    }

    @Ignore
    @Test
    public void checkImportIsConsistent() throws IOException, SQLException {
        addTestUser();
        sleep(500);
        clickOn("Home");
        sleep(500);
        assertEquals(1, lookup("#userTableView").queryTableView().getItems().size()); //Make sure the test user is in the admin table

        new File("testsave").createNewFile();//Create a new blank file to load from
        clickOn("#cliTabButton");
        clickOn("#commandInputField").write("import -r ../testsave");
        press(KeyCode.ENTER);
        release(KeyCode.ENTER);
        clickOn("Home");

        assertEquals(0, lookup("#userTableView").queryTableView().getItems().size()); //Make sure the test user is no longer in the admin table

        new File("testsave").delete();
    }

    @Test
    public void checkPromptIsPresent() {
        TextField input = lookup("#commandInputField").query();
        assertEquals("TF > ", input.getText());

        clickOn("#commandInputField").write("testtesttest");
        press(KeyCode.ENTER);
        release(KeyCode.ENTER);

        assertEquals("TF > ", input.getText());
    }

    @Ignore
    @Test
    public void checkClearCommand() throws TimeoutException{
        waitForNodeVisible(300,"#commandInputField");
        clickOn("#commandInputField").write("testtesttest");
        press(KeyCode.ENTER);
        release(KeyCode.ENTER);
        assertEquals(2, lookup("#commandOutputView").queryListView().getItems().size());
        clickOn("#commandInputField").write("clear");
        press(KeyCode.ENTER);
        release(KeyCode.ENTER);
        assertEquals(0, lookup("#commandOutputView").queryListView().getItems().size());
    }

    @Ignore
    @Test
    public void cliInputIsRead() {
        clickOn("#commandInputField");
        //DataManager.users.clear();
        write("adduser \"Test,User\" 01/10/1998");
        press(KeyCode.ENTER);
        release(KeyCode.ENTER);
        try {
            assertEquals(1, WindowManager.getDataManager().getUsers().getAllUsers(null).size());
        } catch (HttpResponseException e) {
            Debugger.error("Should avoid using DB for testing where possible. Failed to fecth all users.");
        }
    }

    @Test
    public void cliOutputIsShown() {
        clickOn("#commandInputField");
        //DataManager.users.clear();
        write("adduser \"Test,User\" 01/10/1998");
        press(KeyCode.ENTER);
        release(KeyCode.ENTER);
        sleep(200);
        assertEquals(2, lookup("#commandOutputView").queryListView().getItems().size());
    }

    @Test
    public void cliHistoryPrevious() {
        TextField commandInputField = lookup("#commandInputField").query();
        clickOn("#commandInputField").write("a");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        write("b");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        write("c");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        write("d");
        press(KeyCode.ENTER).release(KeyCode.ENTER);

        press(KeyCode.UP).release(KeyCode.UP);
        assertEquals("TF > d", commandInputField.getText());
        press(KeyCode.UP).release(KeyCode.UP);
        assertEquals("TF > c", commandInputField.getText());
        press(KeyCode.UP).release(KeyCode.UP);
        assertEquals("TF > b", commandInputField.getText());
        press(KeyCode.UP).release(KeyCode.UP);
        assertEquals("TF > a", commandInputField.getText());

        // Check for a second time that the history doesn't break when overrun
        press(KeyCode.UP).release(KeyCode.UP);
        assertEquals("TF > ", commandInputField.getText());
    }

    @Test
    public void cliHistoryLater() {
        TextField commandInputField = lookup("#commandInputField").query();
        clickOn("#commandInputField").write("a");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        write("b");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        write("c");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        write("d");
        press(KeyCode.ENTER).release(KeyCode.ENTER);

        press(KeyCode.UP).release(KeyCode.UP);
        assertEquals("TF > d", commandInputField.getText());
        press(KeyCode.UP).release(KeyCode.UP);
        assertEquals("TF > c", commandInputField.getText());
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        assertEquals("TF > d", commandInputField.getText());
        press(KeyCode.DOWN).release(KeyCode.DOWN);

        // Check for a second time that the history doesn't break when overrun
        assertEquals("TF > ", commandInputField.getText());
    }

    @Test
    public void cliHistoryNoHistory() {
        TextField commandInputField = lookup("#commandInputField").query();
        clickOn("#commandInputField");
        press(KeyCode.UP).release(KeyCode.UP);
        assertEquals("TF > ", commandInputField.getText());
        press(KeyCode.DOWN).release(KeyCode.DOWN);
        assertEquals("TF > ", commandInputField.getText());
    }
}
