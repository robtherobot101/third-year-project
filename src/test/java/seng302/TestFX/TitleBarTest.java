package seng302.TestFX;

import javafx.stage.Stage;
import org.junit.Before;

import org.junit.BeforeClass;
import org.junit.Test;
import seng302.GUI.TitleBar;
import seng302.User.User;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.testfx.api.FxToolkit.registerPrimaryStage;

public class TitleBarTest extends TestFXTest {

    private Stage stage;
    private TitleBar titleBar;
    private User user;

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    protected void defaultTest1Setup() throws TimeoutException {

            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("headless.geometry", "1600x1200-32");
        //return registerPrimaryStage();
    }

    @Before
    public void setUp() throws TimeoutException {
        stage = registerPrimaryStage();
        titleBar = new TitleBar();
        titleBar.setStage(stage);
        user = new User("William Shakespeare", LocalDate.parse("23/04/1564", User.dateFormat));
    }

    @Test
    public void setTitle() {
        titleBar.setTitle("Page 1");
        assertEquals("Page 1", stage.getTitle());
        titleBar.setTitle("");
        assertEquals("", stage.getTitle());
    }

    @Test
    public void setTitle1() {
        titleBar.setTitle(user.getName(), "User", "Home");
        assertEquals("User: William Shakespeare - Home", stage.getTitle());
        titleBar.setTitle(user.getName(), "User", null);
        assertEquals("User: William Shakespeare", stage.getTitle());
    }

    @Test
    public void saved() {
        titleBar.saved(false);
        assert(stage.getTitle().endsWith("*"));
        titleBar.saved(false);
        assertFalse(stage.getTitle().endsWith("**"));
        titleBar.saved(true);
        assertFalse(stage.getTitle().endsWith("*"));
    }
}