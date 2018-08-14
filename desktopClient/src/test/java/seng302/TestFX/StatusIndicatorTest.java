package seng302.TestFX;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxToolkit.registerPrimaryStage;

import java.util.concurrent.TimeoutException;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.controlsfx.control.StatusBar;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import seng302.gui.StatusIndicator;

public class StatusIndicatorTest extends TestFXTest {

    private StatusBar statusBar;
    private StatusIndicator statusIndicator;

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    @Override
    public void start(Stage stage) {
        statusBar = new StatusBar();
    }

    @Before
    public void setUp() throws TimeoutException {
        Stage stage = registerPrimaryStage();
        statusBar = new StatusBar();
        StackPane root = new StackPane();
        root.getChildren().add(statusBar);
        Platform.runLater(() -> stage.setScene(new Scene(root, 100, 100)));
        statusIndicator = new StatusIndicator();
        statusIndicator.setStatusBar(statusBar);
    }

    @Test
    public void setStatus() {
        statusIndicator.setStatus("Status nominal", false);
        assertEquals("Status nominal", statusBar.getText());
        assertEquals(0, statusBar.getProgress(), 0.0001);
        statusIndicator.setStatus("Working...", true);
        assertEquals("Working...", statusBar.getText());
        assertEquals(ProgressBar.INDETERMINATE_PROGRESS, statusBar.getProgress(), 0.0001);
    }

    @Test
    public void ready() {
        statusIndicator.ready();
        assertEquals("Ready", statusBar.getText());
        assertEquals(0, statusBar.getProgress(), 0.0001);
    }
}