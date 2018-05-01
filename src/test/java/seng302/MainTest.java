package seng302;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
        Main.recalculateNextId(true);
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
        assertEquals(0, Main.lengthMatchedScore("aa", ""));
    }
    @Test
    public void testLengthMatched_emptySearchTerm_returnsZero() {
        assertEquals(0, Main.lengthMatchedScore("", "aa"));
    }
    @Test
    public void testLengthMatched_emptyStringAndSearchTerm_returnsZero() {
        assertEquals(0, Main.lengthMatchedScore("", ""));
    }

    @Test
    public void testLengthMatched_longerSearchTerm_returnsZero() {
        assertEquals(0, Main.lengthMatchedScore("a", "aa"));
    }
    @Test
    public void testLengthMatched_shorterSearchTerm_returnsMatchLengthMinusNameLength() {
        assertEquals(1, Main.lengthMatchedScore("aa", "a"));
    }
    @Test
    public void testLengthMatched_termEqualsString_returnsZero() {
        assertEquals(2, Main.lengthMatchedScore("aa", "aa"));
    }
    @Test
    public void testLengthMatched_upperCaseString_returnsZeroIgnoringCase() {
        assertEquals(2, Main.lengthMatchedScore("AA", "aa"));
    }
    @Test
    public void testLengthMatched_upperCaseTerm_returnsZeroIgnoringCase() {
        assertEquals(2, Main.lengthMatchedScore("aa", "AA"));
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
    public void testBestMatchingToken_emptyString_returnsEmptyString(){
        List<String> tokens = new ArrayList<String>(Arrays.asList("abc","def","egh"));
        assertEquals("",Main.bestMatchingToken("",tokens));
    }
    @Test
    public void testBestMatchingToken_noTokens_returnsEmptyString(){
        List<String> tokens = new ArrayList<String>(Arrays.asList());
        assertEquals("",Main.bestMatchingToken("name",tokens));
    }
    @Test
    public void testBestMatchingToken_multipleTokens_returnsBestMatchingToken(){
        List<String> tokens = new ArrayList<String>(Arrays.asList(
                "dan",
                "da",
                "danie",
                "shawn",
                "daniel",
                "dani"
        ));
        assertEquals("daniel",Main.bestMatchingToken("daniella",tokens));
    }
    @Test
    public void testBestMatchingToken_oneTokens_returnsOnlyToken(){
        List<String> tokens = new ArrayList<String>(Arrays.asList(
                "Potter"
        ));
        assertEquals("Potter",Main.bestMatchingToken("Potter",tokens));
    }


    @Test
    public void testScoreUserOnSearch_allNamesMatched_returnsCorrectScore(){
        User user = new User("Logan,Potter,Rambo", LocalDate.now());
        List tokens = new ArrayList<String>(Arrays.asList(
                "Logan","Potter","Rambo"
        ));
        assertEquals(22,Main.scoreUserOnSearch(user, tokens));
    }

    @After
    public void tearDown() {
        Main.users = new ArrayList<>();
    }
}
