package seng302.Core;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TableRow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.ListViewMatchers;
import org.testfx.matcher.control.TableViewMatchers;
import org.testfx.util.WaitForAsyncUtils;
import seng302.Core.Main;

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

    private Main mainGUI;
    private static final boolean runHeadless = true;
    Donor donor = new Donor("test user", LocalDate.of(1994,7,4));

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
        mainGUI.donors.add(donor);
        navigateToMedicationsPane();
    }

    @After
    public void tearDown () throws Exception {
        mainGUI.donors.remove(donor);
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


    public void waitForEnabled(int timeout, String cssID) throws TimeoutException{
        Callable<Boolean> callable = () -> {
            if(lookup(cssID).query()==null){
                return false;
            }else{
                if(lookup(cssID).query().isDisable()==false){
                    return true;
                }else{
                    return false;
                }
            }
        };
        WaitForAsyncUtils.waitFor(timeout, TimeUnit.SECONDS, callable);
    }


    private void navigateToMedicationsPane() {
        loginAsDefaultClinician();
        clickOn("#profileSearchTextField").write("tes");
        System.out.println();
        Node row = from(lookup("#profileTable")).lookup("test user").query();
        doubleClickOn(row);
        clickOn("#medicationsButton");
    }

    @Test
    public void compareDrugsWithInteractionSymptoms_returnsCorrectResults(){
        clickOn("#newMedicationField"); write("diazepam");
        clickOn("#addNewMedicationButton");
        clickOn("#newMedicationField"); write("escitalopram");
        clickOn("#addNewMedicationButton");
        Node drugARow = from(lookup("#currentListView")).lookup("escitalopram").query();
        clickOn(drugARow);
        clickOn("#compareButton");
        Node drugBRow = from(lookup("#currentListView")).lookup("diazepam").query();
        clickOn(drugBRow);
        clickOn("#compareButton");
        sleep(2000);
        //Todo Verify the results in the symptom table
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
        waitForEnabled(5,"#compareButton");
        verifyThat(lookup("#interactionListView"), ListViewMatchers.hasListCell("Invalid comparison."));
    }
}