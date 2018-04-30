package seng302;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import seng302.User.Attribute.LoginType;
import seng302.User.User;
import seng302.User.Attribute.Gender;
import seng302.Generic.Main;
import seng302.User.Attribute.Organ;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple Main.
 */
public class MainTest {
    @Before
    public void setup() {
        Main.users = new ArrayList<>();
        Main.recalculateNextId(LoginType.USER);
        Main.users.add(new User("Andrew,Neil,Davidson", "01/02/1998", "01/11/4000", "male", 12.1, 50.45, "o+", "Canterbury", "1235 abc Street"));
        Main.users.add(new User("Test User,Testperson", "01/04/1530", "31/01/1565", "other", 1.234, 1.11111, "a-", "Auckland", "street sample " +
                "text"));
        Main.users.add(new User("Singlename", LocalDate.parse("12/06/1945", User.dateFormat)));
        Main.users.add(new User("User 2,Person", "01/12/1990", "09/03/2090", "female", 2, 60, "b-", "Sample Region", "Sample Address"));
        Main.users.add(new User("a,long,long,name", "01/11/3000", "01/11/4000", "other", 0.1, 12.4, "b-", "Example region", "Example Address " +
                "12345"));
    }

    @Test
    public void testGetById() {
        assertEquals(Main.users.get(2), Main.getUserById(2));
        Main.users.remove(2);
        assertNull(Main.getUserById(2));
    }

    @Test
    public void testGetByName() {
        assertEquals(2, Main.getUserByName(new String[]{"User", "Person"}).size());
        assertEquals(1, Main.getUserByName(new String[]{"nglenam"}).size());
        assertEquals("Singlename", Main.getUserByName(new String[]{"nglenam"}).get(0).getName());
        assertEquals(1, Main.getUserByName(new String[]{"a", "on", "lo", "me"}).size());
        assertEquals(0, Main.getUserByName(new String[]{"name", "long"}).size());
    }

    @Test
    public void testImportSave() {
        Main.users.add(new User("extra", LocalDate.parse("01/01/1000", User.dateFormat)));
        Main.saveUsers("testsave", true);
        Main.users.remove(5);
        assertEquals(5, Main.users.size());
        Main.importUsers("testsave", true);
        assertEquals("extra", Main.users.get(5).getName());
    }

    @Test
    public void testImportSaveIntegrity() {
        User oldUser = new User("extra", LocalDate.parse("01/01/1000", User.dateFormat));
        oldUser.setOrgan(Organ.CORNEA);
        oldUser.setWeight(100);
        oldUser.setGender(Gender.MALE);
        Main.users.add(oldUser);
        Main.saveUsers("testsave", true);
        Main.users.remove(5);
        Main.importUsers("testsave", true);
        assertEquals(Main.users.get(5).toString(), oldUser.toString());
    }

    @Test
    public void testImportIOException(){
        String invalidFile = "OrganDonation.jpg";
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        Main.importUsers(invalidFile, true);
        String text = out.toString();
        String expected = "IOException on "+ invalidFile +": Check your inputs and permissions!";
        assertEquals(expected, text.trim());
    }

    /**
     * The method in this test is made to return false if and only if an IO exception occurs.
     */
    @Test
    public void testSaveIOException(){
        assertFalse(Main.saveUsers("", true));
    }

    @Test
    public void testLengthMatched_emptyString_returnsZero() {
        assertEquals(0, Main.lengthMatchedScore("", ""));
    }
    @Test
    public void testLengthMatched_longerSearchTerm_returnsZero() {
        assertEquals(0, Main.lengthMatchedScore("a", "aa"));
    }
    @Test
    public void testLengthMatched_shorterSearchTerm_returnsMatchLengthMinusNameLength() {
        assertEquals(-1, Main.lengthMatchedScore("aa", "a"));
    }
    @Test
    public void testLengthMatched_termEqualsString_returnsZero() {
        assertEquals(0, Main.lengthMatchedScore("aa", "aa"));
    }
    @Test
    public void testLengthMatched_upperCaseString_returnsZeroIgnoringCase() {
        assertEquals(0, Main.lengthMatchedScore("AA", "aa"));
    }
    @Test
    public void testLengthMatched_upperCaseTerm_returnsZeroIgnoringCase() {
        assertEquals(0, Main.lengthMatchedScore("aa", "AA"));
    }

    @Test
    public void testMatches_stringEqualsTerm_returnsTrue(){
        assertTrue(Main.matches("aaa", "aaa"));
    }
    @Test
    public void testMatches_longerSearchTerm_returnsFalse(){
        assertFalse(Main.matches("aaa", "aaaa"));
    }
    @Test
    public void testMatches_longerStringShouldMatch_returnsTrue(){
        assertTrue(Main.matches("abcd", "abc"));
    }
    @Test
    public void testMatches_emptyStringAndTerm_returnsTrue(){
        assertTrue(Main.matches("", ""));
    }
    @Test
    public void testMatches_uppercaseTerm_returnsTrue(){
        assertTrue(Main.matches("aa", "AA"));
    }
    @Test
    public void testMatches_uppercaseString_returnsTrue(){
        assertTrue(Main.matches("AA", "aa"));
    }

    @Test
    public void testGetUsersByNameAlternative_equallyMatched_SortedAlphabetically(){
        LocalDate dummyDate = LocalDate.of(2000, 1, 1);
        Main.users.clear();
        Main.users.add(new User("aad,coco", dummyDate));
        Main.users.add(new User("aaa,coco", dummyDate));
        Main.users.add(new User("aab,coco", dummyDate));
        Main.users.add(new User("aac,coco", dummyDate));
        ArrayList<User> results = Main.getUsersByNameAlternative("aa");
        assertEquals("aaa coco", results.get(0).getName());
        assertEquals("aab coco", results.get(1).getName());
        assertEquals("aac coco", results.get(2).getName());
        assertEquals("aad coco", results.get(3).getName());
    }
    @Test
    public void testGetUsersByNameAlternative_differentlyMatched_sortedByMatchedAmount(){
        LocalDate dummyDate = LocalDate.of(2000, 1, 1);
        Main.users.clear();
        Main.users.add(new User("abcde", dummyDate));
        Main.users.add(new User("ab", dummyDate));
        Main.users.add(new User("abc", dummyDate));
        Main.users.add(new User("abcd", dummyDate));
        ArrayList<User> results = Main.getUsersByNameAlternative("a");
        System.out.println(results.get(0).getName());
        assertEquals("ab", results.get(0).getName());
        assertEquals("abc", results.get(1).getName());
        assertEquals("abcd", results.get(2).getName());
        assertEquals("abcde", results.get(3).getName());
    }
    @Test
    public void testGetUsersByNameAlternative_doesNotMatch_noResults(){
        Main.users.clear();
        LocalDate dummyDate = LocalDate.of(2000, 1, 1);
        Main.users.add(new User("abc,bcd,cde", dummyDate));
        ArrayList<User> results = Main.getUsersByNameAlternative("d");
        assertEquals(0, results.size());
    }
    @Test
    public void testGetUsersByNameAlternative_longerSearchTerm_noResults(){
        Main.users.clear();
        LocalDate dummyDate = LocalDate.of(2000, 1, 1);
        Main.users.add(new User("a", dummyDate));
        Main.users.add(new User("ab", dummyDate));
        ArrayList<User> results = Main.getUsersByNameAlternative("abc");
        assertEquals(0, results.size());
    }


    @After
    public void tearDown() {
        Main.users = new ArrayList<>();
    }
}
