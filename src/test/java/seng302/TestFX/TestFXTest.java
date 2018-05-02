package seng302.TestFX;

import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import java.time.LocalDate;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import seng302.Generic.Main;
import seng302.User.User;

abstract class TestFXTest extends ApplicationTest {
    protected static final boolean runHeadless = true;

    @Override
    public void start(Stage stage) {
        Main mainGUI = new Main();
        mainGUI.start(stage);
        Main.users.clear();
    }

    @After
    public void tearDown() throws TimeoutException {
        Main.users.clear();
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    protected static void defaultTestSetup() throws TimeoutException {
        if (runHeadless) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "false");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("headless.geometry", "1600x1200-32");
        }
        registerPrimaryStage();
    }

    protected static void headedTestSetup() throws TimeoutException {
        System.setProperty("testfx.robot.write_sleep", "1");
        registerPrimaryStage();
    }

    protected User addTestUser() {
        User testUser = new User(
            "Bobby", new String[]{"Dong"}, "Flame",
            LocalDate.of(1969, 8, 4),
            "bflame",
            "flameman@hotmail.com",
            "password123");
        Main.users.add(testUser);
        return testUser;
    }

    protected void loginAsDefaultClinician() {
        clickOn("#identificationInput"); write("default");
        clickOn("#passwordInput"); write("default");
        clickOn("#loginButton");
    }

    protected void loginAs(User user) {
        clickOn("#identificationInput"); write(user.getEmail());
        clickOn("#passwordInput"); write(user.getPassword());
        clickOn("#loginButton");
    }

    protected void openUserAsClinician(String name) {
        Node row = from(lookup("#profileTable")).lookup(name).query();
        doubleClickOn(row);
    }

    protected void waitForEnabled(int timeout, String cssID) throws TimeoutException {
        Callable<Boolean> callable = () -> {
            if (lookup(cssID).query() == null) {
                return false;
            } else {
                return !lookup(cssID).query().isDisable();
            }
        };
        WaitForAsyncUtils.waitFor(timeout, TimeUnit.SECONDS, callable);
    }


    /**
     * Waits until the node denoted by the given id can be found and is visible.
     * If the waiting time exceeds the given timeout in seconds, a TimeOutException
     * is thrown.
     * @param timeout The timeout in seconds
     * @param id The fx identifier of the node
     * @throws TimeoutException If the waiting time exceeds the given timeout.
     */
    protected void waitForNodeVisible(int timeout, String id) throws TimeoutException{
        Callable<Boolean> callable = () -> {
            Node nodeFound = lookup(id).query();
            if(nodeFound==null){
                return false;
            }else{
                if(nodeFound.isVisible()){
                    //Let the GUI skin catchup to the controller state
                    waitForFxEvents();
                    return true;
                }else{
                    return false;
                }
            }
        };
        WaitForAsyncUtils.waitFor(timeout, TimeUnit.SECONDS, callable);
    }

    /**
     * Waits until the node denoted by the given id can be found and is enabled.
     * If the waiting time exceeds the given timeout in seconds, a TimeOutException
     * is thrown.
     * @param timeout The timeout in seconds
     * @param id The fx identifier of the node
     * @throws TimeoutException If the waiting time exceeds the given timeout.
     */
    protected void waitForNodeEnabled(int timeout, String id) throws TimeoutException{
        Callable<Boolean> callable = () -> {
            Node nodeFound = lookup(id).query();
            if(nodeFound==null){
                return false;
            }else{
                if(!nodeFound.isDisable()){
                    //Let the GUI skin catchup to the controller state
                    waitForFxEvents();
                    return true;
                }else{
                    return false;
                }
            }
        };
        WaitForAsyncUtils.waitFor(timeout, TimeUnit.SECONDS, callable);
    }
}
