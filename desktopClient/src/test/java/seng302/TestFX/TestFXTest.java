package seng302.TestFX;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.apache.http.client.HttpResponseException;
import org.junit.After;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import seng302.data.interfaces.AdminsDAO;
import seng302.data.interfaces.CliniciansDAO;
import seng302.data.interfaces.GeneralDAO;
import seng302.data.interfaces.UsersDAO;
import seng302.data.local.AdminsM;
import seng302.data.local.CliniciansM;
import seng302.data.local.GeneralM;
import seng302.data.local.UsersM;
import seng302.gui.TFScene;
import seng302.generic.DataManager;
import seng302.generic.Debugger;
import seng302.generic.WindowManager;
import seng302.User.Clinician;
import seng302.User.User;
import java.time.LocalDate;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

abstract class TestFXTest extends ApplicationTest {

    private static final boolean runHeadless = true;

    @Override
    public void start(Stage stage) {
        Platform.runLater(() -> {
            try {
                WindowManager.resetScene(TFScene.userWindow);
                WindowManager mainGUI = new WindowManager();
                mainGUI.start(stage);

                WindowManager.getDataManager().getGeneral().reset("masterToken");
            } catch (HttpResponseException e) {
                e.printStackTrace();
            }
        });
    }

    @After
    public void tearDown() throws TimeoutException, HttpResponseException {
        WindowManager.getDataManager().getGeneral().reset("masterToken");
        WindowManager.getDataManager().getGeneral().resample("masterToken");

        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    void useLocalStorage() {
        UsersDAO users = new UsersM();
        CliniciansDAO clinicians = new CliniciansM();
        AdminsDAO admins = new AdminsM();
        GeneralDAO general = new GeneralM(users,clinicians,admins);
        WindowManager.setDataManager(new DataManager(users,clinicians,admins,general));
    }

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

    protected User addTestUser() {
        User testUser = new User(
                "Bobby", new String[]{"Dong"}, "Flame",
                LocalDate.of(1969, 8, 4),
                "bflame",
                "flameman@hotmail.com",
                "cfj3742",
                "password123"
        );
        try {
            WindowManager.getDataManager().getUsers().insertUser(testUser);
        } catch (HttpResponseException e) {
            Debugger.log("Failed to insert new user.");
        }
        return testUser;
    }

    void loginAsDefaultAdmin() {
        clickOn("#identificationInput").write("admin");
        clickOn("#passwordInput").write("default");
        clickOn("#loginButton");
    }

    void userWindow(User user) {
        Platform.runLater(() -> {
            WindowManager.setCurrentUser(user, null);
            WindowManager.setScene(TFScene.userWindow);
        });
        waitForFxEvents();
    }

    void userWindowAsClinician(User user) {
        Platform.runLater(() -> WindowManager.newAdminsUserWindow(user, null));
        waitForFxEvents();
    }


    void openUserAsClinician(String name) {
        Node row = from(lookup("#profileTable")).lookup(name).query();
        doubleClickOn(row);
    }

    void waitForEnabled(int timeout, String cssID) throws TimeoutException {
        Callable<Boolean> callable = () -> lookup(cssID).query() != null && !lookup(cssID).query().isDisable();
        WaitForAsyncUtils.waitFor(timeout, TimeUnit.SECONDS, callable);
    }


    void loginAsDefaultClinician() {
        clickOn("#identificationInput");
        write("default");
        clickOn("#passwordInput");
        write("default");
        clickOn("#loginButton");
    }


    void openClinicianWindow(Clinician testClinician){
        Platform.runLater(() ->{
            WindowManager.setCurrentClinician(testClinician, null);
            WindowManager.setScene(TFScene.clinician);
        });
        waitForFxEvents();
    }


    void waitForNodeVisible(int timeout, String id) throws TimeoutException {
        Callable<Boolean> callable = () -> {
            Node nodeFound = lookup(id).query();
            if (nodeFound == null) {
                return false;
            } else {
                if (nodeFound.isVisible()) {
                    //Let the gui skin catchup to the controller state
                    waitForFxEvents();
                    return true;
                } else {
                    return false;
                }
            }
        };
        WaitForAsyncUtils.waitFor(timeout, TimeUnit.SECONDS, callable);
    }

    void waitForNodeEnabled(String id) throws TimeoutException {
        Callable<Boolean> callable = () -> {
            Node nodeFound = lookup(id).query();
            if (nodeFound == null) {
                return false;
            } else {
                if (!nodeFound.isDisable()) {
                    //Let the gui skin catchup to the controller state
                    waitForFxEvents();
                    return true;
                } else {
                    return false;
                }
            }
        };
        WaitForAsyncUtils.waitFor(10, TimeUnit.SECONDS, callable);
    }
}
