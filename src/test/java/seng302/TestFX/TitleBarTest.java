package seng302.TestFX;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.testfx.api.FxToolkit.registerPrimaryStage;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import seng302.GUI.TitleBar;
import seng302.User.User;

public class TitleBarTest extends TestFXTest {

    private Stage stage;
    private TitleBar titleBar;
    private User user;

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    @Before
    public void setUp() throws TimeoutException {
        stage = registerPrimaryStage();
        titleBar = new TitleBar();
        titleBar.setStage(stage);
        user = new User("William Shakespeare", LocalDate.parse("23/04/1564", User.dateFormat));
    }

    @Ignore
    @Test
    public void setTitle() {
        titleBar.setTitle("Page 1");
        assertEquals("Page 1", stage.getTitle());
        titleBar.setTitle("");
        assertEquals("", stage.getTitle());
    }

    @Ignore
    @Test
    public void setTitle1() {
        titleBar.setTitle(user.getName(), "User", "Home");
        assertEquals("User: William Shakespeare - Home", stage.getTitle());
        titleBar.setTitle(user.getName(), "User", null);
        assertEquals("User: William Shakespeare", stage.getTitle());
    }

    @Ignore
    @Test
    public void saved() {
        titleBar.saved(false);
        assert (stage.getTitle().endsWith("*"));
        titleBar.saved(false);
        assertFalse(stage.getTitle().endsWith("**"));
        titleBar.saved(true);
        assertFalse(stage.getTitle().endsWith("*"));
    }
}