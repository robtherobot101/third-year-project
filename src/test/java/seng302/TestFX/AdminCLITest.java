package seng302.TestFX;

import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertNotNull;

public class AdminCLITest extends TestFXTest {

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    @Ignore
    @Test
    public void tabExists() {
        loginAsDefaultAdmin();
        clickOn("#cliTabButton");
        sleep(200);
        TabPane tableTabPane = lookup("#tableTabPane").query();
        assertNull(tableTabPane); //Ensure that the search tab is no longer open
    }

    @Ignore
    @Test
    public void embeddedCliExists() {
        loginAsDefaultAdmin();
        clickOn("#cliTabButton");
        sleep(200);
        TextField textField = lookup("#commandInputField").query();
        assertNotNull(textField); //Ensure that the CLI is now showing
    }
}
