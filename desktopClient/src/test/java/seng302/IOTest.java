package seng302;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class IOTest {

/*
    @Before
    public void setup() {
        DataManager.users = FXCollections.observableArrayList();
        DataManager.recalculateNextId(ProfileType.USER);
        DataManager.users.add(new user("Andrew,Neil,Davidson", "01/02/1998", "01/11/4000", "male", 12.1, 50.45, "o+", "Canterbury", "1235 abc Street"));
        DataManager.users.add(new user("Test user,Testperson", "01/04/1530", "31/01/1565", "Non-Binary", 1.234, 1.11111, "a-", "Auckland", "street sample " +
                "text"));
        DataManager.users.add(new user("Singlename", LocalDate.parse("12/06/1945", user.dateFormat)));
        DataManager.users.add(new user("user 2,Person", "01/12/1990", "09/03/2090", "female", 2, 60, "b-", "Sample Region", "Sample Address"));
        DataManager.users.add(new user("a,long,long,name", "01/11/3000", "01/11/4000", "Non-Binary", 0.1, 12.4, "b-", "Example region", "Example Address " +
                "12345"));
    }

    @Test
    public void testImportSave() {
        DataManager.users.add(new user("extra", LocalDate.parse("01/01/1000", user.dateFormat)));
        IO.saveUsers("testsave.json", ProfileType.USER);
        DataManager.users.clear();
        IO.importProfiles("testsave.json", ProfileType.USER);
        assertEquals("extra", DataManager.users.get(5).getName());
        new File("testsave.json").delete();
    }

    @Test
    public void testImportSaveIntegrity() {
        user oldUser = new user("extra", LocalDate.parse("01/01/1000", user.dateFormat));
        oldUser.setOrgan(Organ.CORNEA);
        oldUser.setWeight(100);
        oldUser.setGender(Gender.MALE);
        DataManager.users.add(oldUser);
        IO.saveUsers("testsave.json", ProfileType.USER);
        DataManager.users.clear();
        IO.importProfiles("testsave.json", ProfileType.USER);
        assertEquals(DataManager.users.get(5).toString(), oldUser.toString());
        new File("testsave.json").delete();
    }

    @Test
    public void testImportIOException() {
        String invalidFile = "OrganDonation.jpg";
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        try {
            IO.importProfiles(invalidFile, ProfileType.USER);
        } catch (AssertionError e) {
            return;
        }
        fail();
    }

    */
/**
     * The method in this test is made to return false if and only if an IO exception occurs.
     *//*

    @Test
    public void testSaveIOException() {
        assertFalse(IO.saveUsers("", ProfileType.USER));
    }
*/
}
