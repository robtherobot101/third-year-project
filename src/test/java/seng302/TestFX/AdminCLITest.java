package seng302.TestFX;

import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AdminCLITest extends TestFXTest {

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    @Test
    public void tabExists() {
        loginAsDefaultAdmin();
        clickOn("#cliTabButton");
        sleep(200);
        GridPane mainPane = lookup("#mainPane").query();
        assertTrue(!mainPane.isVisible()); //Ensure that the search tab is no longer open
    }

    @Test
    public void embeddedCliExists() {
        loginAsDefaultAdmin();
        clickOn("#cliTabButton");
        sleep(200);
        TextField textField = lookup("#commandInputField").query();
        assertNotNull(textField); //Ensure that the CLI is now showing
    }
}
