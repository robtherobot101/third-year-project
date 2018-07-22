package seng302.TestFX;

import javafx.application.Platform;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.apache.http.client.HttpResponseException;
import org.junit.After;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import seng302.GUI.TFScene;
import seng302.Generic.DataManager;
import seng302.Generic.Debugger;
import seng302.Generic.WindowManager;
import seng302.User.Admin;
import seng302.User.Clinician;
import seng302.User.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

abstract class TestFXTest extends ApplicationTest {

    protected static final boolean runHeadless = true;
/*
    @Override
    public void start(Stage stage) {





        DataManager.users.clear();
        DataManager.clinicians.clear();
        DataManager.clinicians.add(new Clinician("default", "default", "default"));
        DataManager.admins.clear();
        DataManager.admins.add(new Admin("admin", "default", "default_admin"));
        WindowManager.resetScene(TFScene.userWindow);
        WindowManager mainGUI = new WindowManager();
        mainGUI.start(stage);
    }

    @After
    public void tearDown() throws TimeoutException, SQLException {
        DataManager.users.clear();
        DataManager.clinicians.clear();
        DataManager.admins.clear();
        try {
            WindowManager.getDatabase().resetDatabase();
            WindowManager.getDatabase().loadSampleData();
        } catch (HttpResponseException e) {
            Debugger.error("Failed to reset and resample database.");
        }
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }*/

    protected static void defaultTestSetup() throws TimeoutException {
        if (runHeadless) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("headless.geometry", "3840x2060-32");
        } else {
            System.setProperty("testfx.robot.write_sleep", "1");
        }
        registerPrimaryStage();
    }

    protected User addTestUser() throws SQLException {
        User testUser = new User(
                "Bobby", new String[]{"Dong"}, "Flame",
                LocalDate.of(1969, 8, 4),
                "bflame",
                "flameman@hotmail.com",
                "password123");
        try {
            WindowManager.getDataManager().getUsers().insertUser(testUser);
        } catch (HttpResponseException e) {
            Debugger.log("Failed to insert new user.");
        }
        return testUser;
    }

    protected void loginAsDefaultAdmin() {
        clickOn("#identificationInput").write("admin");
        clickOn("#passwordInput").write("default");
        clickOn("#loginButton");
    }

    protected void userWindow(User user) {
        Platform.runLater(() -> {
            WindowManager.setCurrentUser(user);
            WindowManager.setScene(TFScene.userWindow);
        });
        waitForFxEvents();
    }

    public void userWindowAsClinician(User user) {
        Platform.runLater(() -> {
            WindowManager.newCliniciansUserWindow(user);
        });
        waitForFxEvents();
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


    protected void loginAsDefaultClinician() {
        clickOn("#identificationInput");
        write("default");
        clickOn("#passwordInput");
        write("default");
        clickOn("#loginButton");
    }


    public void openClinicianWindow(Clinician testClinician){
        Platform.runLater(() ->{
            WindowManager.setClinician(testClinician);
            WindowManager.setScene(TFScene.clinician);
        });
        waitForFxEvents();
    }


    protected void waitForNodeVisible(int timeout, String id) throws TimeoutException {
        Callable<Boolean> callable = () -> {
            Node nodeFound = lookup(id).query();
            if (nodeFound == null) {
                return false;
            } else {
                if (nodeFound.isVisible()) {
                    //Let the GUI skin catchup to the controller state
                    waitForFxEvents();
                    return true;
                } else {
                    return false;
                }
            }
        };
        WaitForAsyncUtils.waitFor(timeout, TimeUnit.SECONDS, callable);
    }

    protected void waitForNodeEnabled(int timeout, String id) throws TimeoutException {
        Callable<Boolean> callable = () -> {
            Node nodeFound = lookup(id).query();
            if (nodeFound == null) {
                return false;
            } else {
                if (!nodeFound.isDisable()) {
                    //Let the GUI skin catchup to the controller state
                    waitForFxEvents();
                    return true;
                } else {
                    return false;
                }
            }
        };
        WaitForAsyncUtils.waitFor(timeout, TimeUnit.SECONDS, callable);
    }
}
