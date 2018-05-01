package seng302.TestFX;

import javafx.scene.control.TableView;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import seng302.Generic.Main;
import seng302.User.Attribute.Gender;
import seng302.User.Attribute.Organ;
import seng302.User.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.testfx.api.FxAssert.verifyThat;

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

    @Before
    public void setUp() {
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
                LocalDate.of(1997, 8, 4),
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

    @Test
    public void searchFilterByGender() {
        loginAsDefaultClinician();
        sleep(3000);

    }

    @Test
    public void searchFilterByAge() {
        loginAsDefaultClinician();
        sleep(3000);

    }

    @Test
    public void searchFilterByUserType() {
        loginAsDefaultClinician();
        sleep(3000);

    }

    @Test
    public void searchFilterByOrgan() {
        loginAsDefaultClinician();
        sleep(3000);

    }

    @Test
    public void searchFilterByMultipleFields() {
        loginAsDefaultClinician();
        sleep(3000);

    }







}
