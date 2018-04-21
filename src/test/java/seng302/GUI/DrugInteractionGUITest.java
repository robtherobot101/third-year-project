package seng302.GUI;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableRow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.*;
import org.junit.runners.model.TestTimedOutException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.ListViewMatchers;
import org.testfx.matcher.control.TableViewMatchers;
import org.testfx.util.WaitForAsyncUtils;
import seng302.Core.User;
import seng302.Core.Gender;
import seng302.Core.Main;

import static junit.framework.TestCase.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;
import static org.testfx.util.NodeQueryUtils.bySelector;

import org.testfx.service.support.*;

import javax.swing.text.TableView;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class DrugInteractionGUITest extends ApplicationTest {
    private static final boolean runHeadless = true;
    private User user = new User("test,user", LocalDate.of(1983,7,4));

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
        user.setGender(Gender.FEMALE);
        Main.users.add(user);
        navigateToMedicationsPane();
    }

    @After
    public void tearDown () throws Exception {
        Main.users.remove(user);
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Override
    public void start (Stage stage) throws Exception {
        Main mainGUI = new Main();
        mainGUI.start(stage);
    }


    public void loginAsDefaultClinician() {
        clickOn("#identificationInput"); write("default");
        clickOn("#passwordInput"); write("default");
        clickOn("#loginButton");
    }


    public void waitForEnabled(int timeout, String cssID) throws TimeoutException{
        Callable<Boolean> callable = () -> {
            if (lookup(cssID).query()==null){
                return false;
            } else{
                return !lookup(cssID).query().isDisable();
            }
        };
        WaitForAsyncUtils.waitFor(timeout, TimeUnit.SECONDS, callable);
    }


    private void navigateToMedicationsPane() {
        loginAsDefaultClinician();
        clickOn("#profileSearchTextField").write("test user");
        System.out.println();
        Node row = from(lookup("#profileTable")).lookup("test user").query();
        doubleClickOn(row);
        clickOn("Medications");
        sleep(1000);
    }

    @Test
    public void compareDrugsWithInteractionSymptoms_returnsCorrectResults() throws TimeoutException{
        clickOn("#newMedicationField"); write("Diazepam");
        clickOn("#addNewMedicationButton");
        clickOn("#newMedicationField"); write("Escitalopram");
        clickOn("#addNewMedicationButton");
        Node drugARow = from(lookup("#currentListView")).lookup("Escitalopram").query();
        clickOn(drugARow);
        clickOn("#compareButton");
        Node drugBRow = from(lookup("#currentListView")).lookup("Diazepam").query();
        clickOn(drugBRow);
        clickOn("#compareButton");
        waitForEnabled(5,"#compareButton");
        Label resultsLabel = lookup("#interactionsContentLabel").query();
        String results = resultsLabel.getText().toLowerCase();
        String[] toCheck = {"fatigue: 1 - 6 months", "nausea: < 1 month", "drug ineffective: < 1 month", "weight increased: 1 - 2 years", "dizziness: 2 - 5 years", "headache", "suicidal ideation: 6 - 12 months"};
        for (String item: toCheck) {
            assertTrue(results.contains(item));
        }
    }

    @Ignore //TODO Change this test - it no longer works as invalid drugs can not be added
    @Test
    public void compareInvalidDrugs_returnsZeroSymptoms() throws TimeoutException {
        clickOn("#newMedicationField"); write("badDrugA");
        clickOn("#addNewMedicationButton");
        clickOn("#newMedicationField"); write("badDrugB");
        clickOn("#addNewMedicationButton");
        Node drugARow = from(lookup("#currentListView")).lookup("badDrugB").query();
        clickOn(drugARow);
        clickOn("#compareButton");
        Node drugBRow = from(lookup("#currentListView")).lookup("badDrugA").query();
        clickOn(drugBRow);
        clickOn("#compareButton");
        waitForEnabled(5,"#compareButton");
        ListView results = lookup("#interactionListView").query();
        verifyThat(results, list -> list.getItems().contains("Invalid comparison."));
    }
}