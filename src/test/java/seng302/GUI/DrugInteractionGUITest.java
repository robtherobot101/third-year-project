
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;
import static org.testfx.util.NodeQueryUtils.bySelector;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

import org.testfx.service.support.*;

import javax.swing.text.TableView;
import java.sql.Time;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class DrugInteractionGUITest extends ApplicationTest {
    private Main mainGUI;
    private static final boolean runHeadless = true;
    User user = new User("test,user", LocalDate.of(1983,7,4));

    @BeforeClass
    public static void setupSpec() throws Exception {

        if (runHeadless) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "false");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("headless.geometry", "1600x1200-32");
        }
        registerPrimaryStage();
    }

    @Before
    public void setUp () throws Exception {
        user.setGender(Gender.FEMALE);
        mainGUI.users.add(user);
        navigateToMedicationsPane();
    }

    @After
    public void tearDown () throws Exception {
        mainGUI.users.remove(user);
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
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



    public void waitForNodeVisible(int timeout, String id) throws TimeoutException{
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

    public void waitForNodeEnabled(int timeout, String id) throws TimeoutException{
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

    public void addValidMedication(String drug) throws TimeoutException{
        clickOn("#newMedicationField").write(drug.substring(0,drug.length()-1));
        waitForNodeVisible(10, drug);
        clickOn(drug);
        clickOn("#addNewMedicationButton");
    }

    public void compareMedications(String drugA, String drugB) throws TimeoutException{
        Node drugARow = from(lookup("#currentListView")).lookup(drugA).query();
        clickOn(drugARow);
        waitForNodeEnabled(10,"#compareButton");
        clickOn("#compareButton");

        Node drugBRow = from(lookup("#currentListView")).lookup(drugB).query();
        clickOn(drugBRow);
        waitForNodeEnabled(10,"#compareButton");
        clickOn("#compareButton");
    }

    private void navigateToMedicationsPane() {
        loginAsDefaultClinician();
        clickOn("#profileSearchTextField").write("test user");
        System.out.println();
        Node row = from(lookup("#profileTable")).lookup("test user").query();
        doubleClickOn(row);
        clickOn("Medications");
    }



    @Test
    public void compareDrugsWithInteractionSymptoms_returnsCorrectResults() throws TimeoutException{
        addValidMedication("Diazepam");
        addValidMedication("Escitalopram");

        compareMedications("Escitalopram","Diazepam");
        waitForNodeEnabled(10,"#compareButton");

        Label resultLabel = (Label)lookup("#interactionsContentLabel").query();
        HashSet<String> results = new HashSet<>(Arrays.asList(resultLabel.getText().split(System.lineSeparator())));
        HashSet<String> expected = new HashSet<String>();
        expected.add("-fatigue: 1 - 6 months");
        expected.add("-nausea: < 1 month");
        expected.add("-drug ineffective: < 1 month");
        expected.add("-weight increased: 1 - 2 years");
        expected.add("-dizziness: 2 - 5 years");
        expected.add("-headache");
        expected.add("-suicidal ideation: 6 - 12 months");
        assertEquals(expected,results);
    }

    @Test
    public void compareInvalidDrugs_returnsZeroSymptoms() throws TimeoutException{
        String badDrugA = "badDrugA";
        String badDrugB = "badDrugB";
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
        waitForNodeEnabled(5,"#compareButton");
        ListView results = (ListView)lookup("#interactionListView").query();
        verifyThat(results, list -> list.getItems().contains("Invalid comparison."));
    }
}