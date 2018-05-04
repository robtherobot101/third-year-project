package seng302.TestFX;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import seng302.Generic.Main;
import seng302.User.Attribute.Gender;
import seng302.User.Attribute.Organ;
import seng302.User.User;

public class ClinicianFilterSearchGUITest extends TestFXTest {

    private User testUserBobby;
    private User testUserAndy;
    private User testUserTest;

    private TableView<User> userTableView;
    private User selectedUser;


    @BeforeClass
    public static void setupClass() throws TimeoutException {
        defaultTestSetup();

    }

    /**
     * Adds three new users to be used for all of the tests.
     */
    @Before
    public void setUp() {
        Main.users.clear();
        testUserBobby = new User(
            "Bobby", new String[]{"Dong"}, "Flame",
            LocalDate.of(1969, 8, 4),
            "bflame",
            "flameman@hotmail.com",
            "password123");
        testUserBobby.setGender(Gender.MALE);
        testUserBobby.setRegion("Auckland");
        testUserBobby.setOrgan(Organ.PANCREAS);
        Main.users.add(testUserBobby);

        testUserAndy = new User(
            "Andy", new String[]{"Pandy"}, "Fandy",
            LocalDate.of(1997, 1, 4),
            "andy",
            "andy@hotmail.com",
            "andyANDY");
        testUserAndy.setGender(Gender.FEMALE);
        testUserAndy.setRegion("Arrowtown");
        testUserAndy.setOrgan(Organ.HEART);
        testUserAndy.setOrgan(Organ.PANCREAS);
        Main.users.add(testUserAndy);

        testUserTest = new User(
            "Test", new String[]{"TEST"}, "test",
            LocalDate.of(1996, 8, 4),
            "test",
            "test@hotmail.com",
            "tester123");
        testUserTest.setGender(Gender.MALE);
        testUserTest.setRegion("Canterbury");

        Main.users.add(testUserTest);

    }

    //Each test checks for a correct response showing when results should be shown and with invalid input to have
    // nothing shown

    /**
     * Checks if the users shown in the table view are correct based on different inputs in the region field.
     */
    @Test
    public void searchFilterByRegion() {
        loginAsDefaultClinician();
        clickOn("#clinicianRegionField").write("A");
        userTableView = lookup("#profileTable").query();
        assertEquals(testUserAndy, userTableView.getItems().get(0));
        assertEquals(testUserBobby, userTableView.getItems().get(1));

        doubleClickOn("#clinicianRegionField").write("C");
        userTableView = lookup("#profileTable").query();
        assertEquals(testUserTest, userTableView.getItems().get(0));

        doubleClickOn("#clinicianRegionField").write("D");
        userTableView = lookup("#profileTable").query();
        assertTrue(userTableView.getItems().isEmpty());


    }

    /**
     * Checks if the users shown in the table view are correct based on different inputs in the gender combo box.
     */
    @Test
    public void searchFilterByGender() {
        loginAsDefaultClinician();

        clickOn("#profileSearchTextField").write("Z");
        clickOn("#clinicianGenderComboBox").clickOn("Male");
        doubleClickOn("#profileSearchTextField").push(KeyCode.BACK_SPACE);

        userTableView = lookup("#profileTable").query();
        assertEquals(testUserBobby, userTableView.getItems().get(0));
        assertEquals(testUserTest, userTableView.getItems().get(1));

        clickOn("#clinicianGenderComboBox").clickOn("Female");
        userTableView = lookup("#profileTable").query();
        assertEquals(testUserAndy, userTableView.getItems().get(0));

        clickOn("#clinicianGenderComboBox").clickOn("Non-Binary");
        userTableView = lookup("#profileTable").query();
        assertTrue(userTableView.getItems().isEmpty());

    }

    /**
     * Checks if the users shown in the table view are correct based on different inputs in the age field.
     */
    @Test
    public void searchFilterByAge() {
        loginAsDefaultClinician();
        clickOn("#clinicianAgeField").write("21");
        userTableView = lookup("#profileTable").query();
        assertEquals(testUserAndy, userTableView.getItems().get(0));
        assertEquals(testUserTest, userTableView.getItems().get(1));

        doubleClickOn("#clinicianAgeField").write("48");
        userTableView = lookup("#profileTable").query();
        assertEquals(testUserBobby, userTableView.getItems().get(0));

        doubleClickOn("#clinicianAgeField").write("100");
        userTableView = lookup("#profileTable").query();
        assertTrue(userTableView.getItems().isEmpty());
    }

    /**
     * Checks if the users shown in the table view are correct based on different inputs in the user type combo box.
     */
    @Test
    public void searchFilterByUserType() {
        loginAsDefaultClinician();

        clickOn("#profileSearchTextField").write("Z");
        clickOn("#clinicianUserTypeComboBox").clickOn("Donor");
        clickOn("#profileSearchTextField").push(KeyCode.BACK_SPACE);
        userTableView = lookup("#profileTable").query();
        assertEquals(testUserAndy, userTableView.getItems().get(0));
        assertEquals(testUserBobby, userTableView.getItems().get(1));

        clickOn("#clinicianUserTypeComboBox").clickOn("Neither");
        userTableView = lookup("#profileTable").query();
        assertEquals(testUserTest, userTableView.getItems().get(0));


    }

    /**
     * Checks if the users shown in the table view are correct based on different inputs in the organ combo box.
     */
    @Test
    public void searchFilterByOrgan() {
        loginAsDefaultClinician();

        clickOn("#clinicianOrganComboBox");
        sleep(300);
        push(KeyCode.DOWN).push(KeyCode.DOWN).push(KeyCode.DOWN);
        push(KeyCode.ENTER);
        userTableView = lookup("#profileTable").query();
        assertEquals(testUserAndy, userTableView.getItems().get(0));
        assertEquals(testUserBobby, userTableView.getItems().get(1));

        clickOn("#clinicianOrganComboBox");
        push(KeyCode.DOWN);
        push(KeyCode.ENTER);
        userTableView = lookup("#profileTable").query();
        assertEquals(testUserAndy, userTableView.getItems().get(0));
    }

    /**
     * Checks if the users shown in the table view are correct based on different inputs into all of
     * the different filtering fields.
     */
    @Test
    public void searchFilterByMultipleFields() {
        loginAsDefaultClinician();
        clickOn("#clinicianRegionField").write("A");
        clickOn("#profileSearchTextField").write("Z");
        clickOn("#clinicianGenderComboBox").clickOn("Male");
        doubleClickOn("#profileSearchTextField").push(KeyCode.BACK_SPACE);
        clickOn("#clinicianAgeField").write("48");
        clickOn("#profileSearchTextField").write("Z");
        clickOn("#clinicianUserTypeComboBox").clickOn("Donor");
        doubleClickOn("#profileSearchTextField").push(KeyCode.BACK_SPACE);
        clickOn("#clinicianOrganComboBox");
        push(KeyCode.DOWN).push(KeyCode.DOWN).push(KeyCode.DOWN);
        push(KeyCode.ENTER);

        userTableView = lookup("#profileTable").query();
        assertEquals(testUserBobby, userTableView.getItems().get(0));

    }


}
