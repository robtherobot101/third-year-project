package seng302.TestFX;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Window;
import junit.framework.TestCase;
import org.apache.http.client.HttpResponseException;
import org.junit.*;
import seng302.generic.Debugger;
import seng302.generic.WindowManager;
import seng302.User.Attribute.Gender;
import seng302.User.Medication.Medication;
import seng302.User.User;

import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MedicationsGUITest extends TestFXTest {

    private User testUser;

    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();
    }

    @Before
    public void setUp() throws SQLException {
        testUser = addTestUser();
        testUser.setGender(Gender.FEMALE);
        userWindowAsClinician(testUser);
        //loginAsDefaultClinician();
        //openUserAsClinician(testUser.getName());
        sleep(500);
        clickOn("Medications");
    }

    @After
    public void tearDown () {
        Platform.runLater(() ->{
            for(Window w:this.listWindows()){
                w.hide();
            }
        });

    }


    /**
     * Method to add a new medication to a user's current medications list.
     *
     * @param medication The medication to add.
     * @throws TimeoutException catch timeout of requests
     */
    private void addNewMedicationToCurrentMedications(String medication) throws TimeoutException {
        clickOn("#newMedicationField").write(medication);
        clickOn("#addNewMedicationButton");
        clickOn("#newMedicationField");
        // Not sure if there is a better way to do this
        waitForNodeVisible(5,"#waitingListButton");

        //sleep(3000);
    }

    /**
     * Add an invalid medication and verify it is not added to the user's current medications
     * @throws TimeoutException catch timeout of requests
     */
    @Test
    public void addInvalidMedicationForDonor() throws TimeoutException {
        //Add Medication for donor.
        addNewMedicationToCurrentMedications("TESTER");

        //Check if medication added is correct.
        ListView currentMedicationList = lookup("#currentListView").queryListView();
        assertEquals(0, currentMedicationList.getItems().size());
        push(KeyCode.ENTER);
    }
}