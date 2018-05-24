package seng302;

import javafx.collections.FXCollections;
import org.junit.Before;
import org.junit.Test;
import seng302.Generic.DataManager;
import seng302.Generic.IO;
import seng302.User.Attribute.Gender;
import seng302.User.Attribute.ProfileType;
import seng302.User.Attribute.Organ;
import seng302.User.User;

import java.io.File;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class IOTest {

    @Before
    public void setup() {
        DataManager.users = FXCollections.observableArrayList();
        DataManager.recalculateNextId(ProfileType.USER);
        DataManager.users.add(new User("Andrew,Neil,Davidson", "01/02/1998", "01/11/4000", "male", 12.1, 50.45, "o+", "Canterbury", "1235 abc Street"));
        DataManager.users.add(new User("Test User,Testperson", "01/04/1530", "31/01/1565", "Non-Binary", 1.234, 1.11111, "a-", "Auckland", "street sample " +
                "text"));
        DataManager.users.add(new User("Singlename", LocalDate.parse("12/06/1945", User.dateFormat)));
        DataManager.users.add(new User("User 2,Person", "01/12/1990", "09/03/2090", "female", 2, 60, "b-", "Sample Region", "Sample Address"));
        DataManager.users.add(new User("a,long,long,name", "01/11/3000", "01/11/4000", "Non-Binary", 0.1, 12.4, "b-", "Example region", "Example Address " +
                "12345"));
    }

    @Test
    public void testImportSave() {
        DataManager.users.add(new User("extra", LocalDate.parse("01/01/1000", User.dateFormat)));
        IO.saveUsers("testsave", ProfileType.USER);
        DataManager.users.remove(5);
        assertEquals(5, DataManager.users.size());
        IO.importUsers("testsave", ProfileType.USER);
        assertEquals("extra", DataManager.users.get(5).getName());
        new File("testsave").delete();
    }

    @Test
    public void testImportSaveIntegrity() {
        User oldUser = new User("extra", LocalDate.parse("01/01/1000", User.dateFormat));
        oldUser.setOrgan(Organ.CORNEA);
        oldUser.setWeight(100);
        oldUser.setGender(Gender.MALE);
        DataManager.users.add(oldUser);
        IO.saveUsers("testsave", ProfileType.USER);
        DataManager.users.remove(5);
        IO.importUsers("testsave", ProfileType.USER);
        assertEquals(DataManager.users.get(5).toString(), oldUser.toString());
        new File("testsave").delete();
    }

    @Test
    public void testImportIOException() {
        String invalidFile = "OrganDonation.jpg";
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        IO.importUsers(invalidFile, ProfileType.USER);
        String text = out.toString();
        String expected = "IOException on " + invalidFile + ": Check your inputs and permissions!";
        assertEquals(expected, text.trim());
    }

    /**
     * The method in this test is made to return false if and only if an IO exception occurs.
     */
    @Test
    public void testSaveIOException() {
        assertFalse(IO.saveUsers("", ProfileType.USER));
    }
}
