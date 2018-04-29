package seng302.TestFX;

import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit.ApplicationTest;
import seng302.GUI.TitleBar;
import seng302.User.User;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.testfx.api.FxToolkit.registerPrimaryStage;

class TitleBarTest extends ApplicationTest {

    private Stage stage;
    private TitleBar titleBar;
    private User user;

    @Override
    public void start(Stage stage) {
        System.out.println(stage);
        this.stage = stage;
        stage.show();
    }

    @BeforeEach
    public void setUp() throws TimeoutException {
        stage = registerPrimaryStage();
        titleBar = new TitleBar();
        titleBar.setStage(stage);
        user = new User("William Shakespeare", LocalDate.parse("23/04/1564", User.dateFormat));
    }

    @Test
    void setTitle() {
        titleBar.setTitle("Page 1");
        assertEquals("Page 1", stage.getTitle());
        titleBar.setTitle("");
        assertEquals("", stage.getTitle());
    }

    @Test
    void setTitle1() {
        titleBar.setTitle(user, "Home");
        assertEquals("User: William Shakespeare - Home", stage.getTitle());
        user.setName("");
        titleBar.setTitle(user, "");
        assertEquals("User:  - ", stage.getTitle());
    }

    @Test
    void saved() {
        titleBar.saved(false);
        assert(stage.getTitle().endsWith("*"));
        titleBar.saved(false);
        assertFalse(stage.getTitle().endsWith("**"));
        titleBar.saved(true);
        assertFalse(stage.getTitle().endsWith("*"));
    }
}