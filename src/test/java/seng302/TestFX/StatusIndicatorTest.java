package seng302.TestFX;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.controlsfx.control.StatusBar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit.ApplicationTest;
import seng302.GUI.StatusIndicator;
import seng302.User.User;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxToolkit.registerPrimaryStage;

class StatusIndicatorTest extends ApplicationTest {

    private StatusBar statusBar;
    private StatusIndicator statusIndicator;

    @Override
    public void start(Stage stage) {
        statusBar = new StatusBar();
    }

    @BeforeEach
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
    void setStatus() {
        statusIndicator.setStatus("Status nominal", false);
        assertEquals("Status nominal", statusBar.getText());
        assertEquals(0, statusBar.getProgress());
        statusIndicator.setStatus("Working...", true);
        assertEquals("Working...", statusBar.getText());
        assertEquals(ProgressBar.INDETERMINATE_PROGRESS, statusBar.getProgress());
    }

    @Test
    void ready() {
        statusIndicator.ready();
        assertEquals("Ready", statusBar.getText());
        assertEquals(0, statusBar.getProgress());
    }
}