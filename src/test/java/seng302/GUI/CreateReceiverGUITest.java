package seng302.GUI;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import seng302.Core.User;
import seng302.Core.Gender;
import seng302.Core.Main;

import java.time.LocalDate;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.api.FxToolkit.registerPrimaryStage;


public class CreateReceiverGUITest extends ApplicationTest {

    private Main mainGUI;
    private static final boolean runHeadless = true;
    User user = new User("test,user", LocalDate.of(1983,7,4));


    @BeforeClass
    public static void setupSpec() throws Exception {

        if (runHeadless) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("headless.geometry", "1600x1200-32");
        }
        registerPrimaryStage();
    }

    @Before
    public void setUp () throws Exception {
        mainGUI.users.add(user);
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
        mainGUI.users.remove(user);
    }

    @Override
    public void start (Stage stage) throws Exception {
        mainGUI = new Main();
        mainGUI.start(stage);
    }


    public void loginAsDefaultClinician() {
        clickOn("#identificationInput"); write("default");
        clickOn("#passwordInput"); write("default");
        clickOn("#loginButton");
    }

    private void enterAccountCreation() {
        clickOn("#createAccountButton");
    }

    @Test
    public void navigate_to_waiting_list(){
        loginAsDefaultClinician();
        clickOn("#profileSearchTextField").write("test user");
        Node row = from(lookup("#profileTable")).lookup("test user").query();
        doubleClickOn(row);
        clickOn("#waitingListButton");
        sleep(1000);
    }
}