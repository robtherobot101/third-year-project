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
        Main.users.add(new User("Test User,Testperson", "01/04/1530", "31/01/1565", "Non-Binary", 1.234, 1.11111, "a-", "Auckland", "street sample " +
                "text"));
        Main.users.add(new User("Singlename", LocalDate.parse("12/06/1945", User.dateFormat)));
        Main.users.add(new User("User 2,Person", "01/12/1990", "09/03/2090", "female", 2, 60, "b-", "Sample Region", "Sample Address"));
        Main.users.add(new User("a,long,long,name", "01/11/3000", "01/11/4000", "Non-Binary", 0.1, 12.4, "b-", "Example region", "Example Address " +
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
    public void testNameMatchesOneOf_matchesNoneOf_returnsFalse(){
        String name = "daniel";
        List tokens = new ArrayList<String>(Arrays.asList("Logan","Paul"));
        assertFalse(Main.nameMatchesOneOf(name, tokens));
    }
    @Test
    public void testNameMatchesOneOf_matchesOneOf_returnsTrue(){
        String name = "daniel";
        List tokens = new ArrayList<String>(Arrays.asList("daniel","Paul"));
        assertTrue(Main.nameMatchesOneOf(name, tokens));
    }
    @Test
    public void testNameMatchesOneOf_emptyStringNonEmptyTokens_returnsFalse(){
        String name = "";
        List tokens = new ArrayList<String>(Arrays.asList("daniel","dan"));
        assertFalse(Main.nameMatchesOneOf(name, tokens));
    }



    @Test
    public void testScoreNames_singleNameMatched_returnsWeight(){
        User user = new User("Logan", LocalDate.now());
        List tokens = new ArrayList<String>(Arrays.asList("Logan"));
        assertEquals(1,Main.scoreNames(user.getNameArray(), tokens,0, 1, 1));
    }
    @Test
    public void testScoreNames_singleNameUnmatched_returnsZero(){
        User user = new User("Logan", LocalDate.now());
        List tokens = new ArrayList<String>(Arrays.asList("Logana"));
        assertEquals(0,Main.scoreNames(user.getNameArray(), tokens,0, 1, 1));
    }
    @Test
    public void testScoreNames_multipleNamesMatchedInMiddle_returnsNumberOfMiddleNamesTimesWeight(){
        User user = new User("a,b,c,d,e,f,g,h", LocalDate.now());
        List tokens = new ArrayList<String>(Arrays.asList("a","b","c","d","e","f","g","h"));
        assertEquals(6,Main.scoreNames(user.getNameArray(), tokens,1, 7, 1));
    }
    @Test
    public void testScoreNames_someNamesMatched_returnsNumberOfMatchedNamesTimesWeight(){
        User user = new User("a,w,c,x,e,y,g,z", LocalDate.now());
        List tokens = new ArrayList<String>(Arrays.asList("a","b","c","d","e","f","g","h"));
        assertEquals(4,Main.scoreNames(user.getNameArray(), tokens,0, 8, 1));
    }
    @Test
    public void testScoreNames_startIndexSameAsEndIndex_returnsZero(){
        User user = new User("a,w,c,x,e,y,g,z", LocalDate.now());
        List tokens = new ArrayList<String>(Arrays.asList("a","b","c","d","e","f","g","h"));
        assertEquals(0,Main.scoreNames(user.getNameArray(), tokens,4, 4, 1));
    }


    @Test
    public void testAllTokensMatched_zeroTokens_returnsTrue(){
        User user = new User("a,w,c,x,e,y,g,z", LocalDate.now());
        List tokens = new ArrayList<String>(Arrays.asList());
        assertTrue(Main.allTokensMatched(user, tokens));
    }
    @Test
    public void testAllTokensMatched_notAllTokensMatched_returnsFalse(){
        User user = new User("a,b,c", LocalDate.now());
        List tokens = new ArrayList<String>(Arrays.asList("a","b"));
        assertTrue(Main.allTokensMatched(user, tokens));
    }
    @Test
    public void testAllTokensMatched_allTokensMatched_returnsTrue(){
        User user = new User("a,b,c", LocalDate.now());
        user.setPreferredNameArray(new String[]{"c","d","e"});
        List tokens = new ArrayList<String>(Arrays.asList("a","b","c","d","e"));
        assertTrue(Main.allTokensMatched(user, tokens));
    }

    @Test
    public void testScoreUserOnSearch_firstMiddleAndLastMatched_returnsCorrectScore(){
        User user = new User("Logan,Potter,Robbie,Rambo", LocalDate.now());
        List tokens = new ArrayList<String>(Arrays.asList(
                "Logan","Potter","Robbie","Rambo"
        ));
        assertEquals(6+1+1+2,Main.scoreUserOnSearch(user, tokens));
    }
    @Test
    public void testScoreUserOnSearch_onlyMatchedFirstName_returnsScoreForMatchedFirstName(){
        User user = new User("Logan", LocalDate.now());
        List tokens = new ArrayList<String>(Arrays.asList(
                "Logan"
        ));
        assertEquals(2,Main.scoreUserOnSearch(user, tokens));
    }
    @Test
    public void testScoreUserOnSearch_onlyMatchedFirstAndLastNames_returnsCorrectScore(){
        User user = new User("Logan,Rambo", LocalDate.now());
        List tokens = new ArrayList<String>(Arrays.asList(
                "Logan","Rambo"
        ));
        assertEquals(6 + 2,Main.scoreUserOnSearch(user, tokens));
    }
    @Test
    public void testScoreUserOnSearch_firstMatchedLastUnmatched_returnsCorrectScore(){
        User user = new User("Logan,Rambo", LocalDate.now());
        List tokens = new ArrayList<String>(Arrays.asList(
                "Logan"
        ));
        assertEquals(2,Main.scoreUserOnSearch(user, tokens));
    }
    @Test
    public void testScoreUserOnSearch_preferredFirstAndLastNamesMatch_returnsCorrectScore(){
        User user = new User("Logan,Potter,Robbie,Rambo", LocalDate.now());
        user.setPreferredNameArray(new String[]{"Herman","Walter","Benjamin"});
        List tokens = new ArrayList<String>(Arrays.asList(
                "Herman","Walter","Benjamin"
        ));
        assertEquals(5+4+3,Main.scoreUserOnSearch(user, tokens));
    }
    @Test
    public void testScoreUserOnSearch_unchangedPreferredNameMatched_returnsScoreForNameMatched(){
        User user = new User("Logan,Potter,Robbie,Rambo", LocalDate.now());
        user.setPreferredNameArray(new String[]{"Logan","Potter","Robbie","Rambo"});
        List tokens = new ArrayList<String>(Arrays.asList(
                "Logan","Potter","Robbie","Rambo"
        ));
        assertEquals(2+1+1+6,Main.scoreUserOnSearch(user, tokens));
    }
    @Test
    public void testScoreUserOnSearch_preferredAndNamePartiallyMatched_returnsScoreOfMatchedNames(){
        User user = new User("Logan,Potter,Robbie,Rambo", LocalDate.now());
        user.setPreferredNameArray(new String[]{"Herman","Walter","Benjamin"});

        List tokens = new ArrayList<String>(Arrays.asList(
                "Logan","Ra","B","Potter"
        ));
        assertEquals(2 + 6 + 5 + 1,Main.scoreUserOnSearch(user, tokens));
    }
    @Test
    public void testScoreUserOnSearch_unmatchedName_returnsZero(){
        User user = new User("Logan,Potter,Robbie,Rambo", LocalDate.now());
        user.setPreferredNameArray(new String[]{"Herman","Walter","Benjamin"});

        List tokens = new ArrayList<String>(Arrays.asList(
                "Logan","Potter","Crumb"
        ));
        assertEquals(0,Main.scoreUserOnSearch(user, tokens));
    }

    public void createTestUsersForSearchingTests(){
        Main.users.clear();
        Main.users.add(new User("Andy,Barry,Zeta", LocalDate.now()));
        Main.users.add(new User("Barry,Arnold,Francis", LocalDate.now()));
        Main.users.add(new User("Cole,Sam,Paul", LocalDate.now()));
        Main.users.add(new User("Doug,Arthur,Garry", LocalDate.now()));
        Main.users.add(new User("Ellie,Rose,Dwight", LocalDate.now()));
        Main.users.add(new User("Francis,Brian,Crumb", LocalDate.now()));
        Main.users.add(new User("Garry,Cody,Rojo", LocalDate.now()));
        Main.users.add(new User("Garry,Beta,Rambo", LocalDate.now()));

    }

    @Test
    public void testGetUsersByNameAlternative_equallyWeightedNames_sortedAlphabetically(){
        User u1 = new User("Garry,Cody,Rojo", LocalDate.now());
        User u2 = new User("Garry,Beta,Rambo", LocalDate.now());
        Main.users.add(u1);
        Main.users.add(u2);
        List<User> results = Main.getUsersByNameAlternative("garry");
        assertEquals(0,results.indexOf(u2));
        assertEquals(1,results.indexOf(u1));
    }
    @Test
    public void testGetUsersByNameAlternative_someUsersMatched_matchedUsersReturned(){
        User u1 = new User("Andy,Barry,Zeta", LocalDate.now());
        User u2 = new User("Barry,Arnold,Francis", LocalDate.now());
        User u3 = new User("Garry,Beta,Rambo", LocalDate.now());
        Main.users.add(u1);
        Main.users.add(u2);
        Main.users.add(u3);
        List<User> results = Main.getUsersByNameAlternative("barry");
        assertTrue(results.contains(u1));
        assertTrue(results.contains(u2));
        assertFalse(results.contains(u3));
    }
    @Test
    public void testGetUsersByNameAlternative_zeroUsersMatched_zeroUsersReturned(){
        User u1 = new User("Andy,Barry,Zeta", LocalDate.now());
        User u2 = new User("Barry,Arnold,Francis", LocalDate.now());
        User u3 = new User("Garry,Beta,Rambo", LocalDate.now());
        Main.users.add(u1);
        Main.users.add(u2);
        Main.users.add(u3);
        List<User> results = Main.getUsersByNameAlternative("victor");
        assertEquals(0, results.size());
    }
    @Test
    public void testGetUsersByNameAlternative_allUsersMatched_allUsersReturned(){
        User u1 = new User("Doug,Arthur,Garry", LocalDate.now());
        User u2 = new User("Garry,Cody,Rojo", LocalDate.now());
        User u3 = new User("Garry,Beta,Rambo", LocalDate.now());
        Main.users.add(u1);
        Main.users.add(u2);
        Main.users.add(u3);
        List<User> results = Main.getUsersByNameAlternative("garry");
        assertTrue(results.contains(u1));
        assertTrue(results.contains(u2));
        assertTrue(results.contains(u3));
    }






    @After
    public void tearDown() {
        Main.users = new ArrayList<>();
    }
}
