package seng302;

import org.junit.Before;
import org.junit.Test;
import seng302.Data.Local.UsersM;
import seng302.User.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class UsersMTest {
    
    UsersM usersM;
    
    @Before
    public void setup() {
        usersM = new UsersM();
    }

    @Test
    public void testLengthMatched_emptyString_returnsZero() {
        assertEquals(0, usersM.lengthMatchedScore("aa", ""));
    }

    @Test
    public void testLengthMatched_emptySearchTerm_returnsZero() {
        assertEquals(0, usersM.lengthMatchedScore("", "aa"));
    }

    @Test
    public void testLengthMatched_emptyStringAndSearchTerm_returnsZero() {
        assertEquals(0, usersM.lengthMatchedScore("", ""));
    }

    @Test
    public void testLengthMatched_longerSearchTerm_returnsZero() {
        assertEquals(0, usersM.lengthMatchedScore("a", "aa"));
    }

    @Test
    public void testLengthMatched_shorterSearchTerm_returnsMatchLengthMinusNameLength() {
        assertEquals(1, usersM.lengthMatchedScore("aa", "a"));
    }

    @Test
    public void testLengthMatched_termEqualsString_returnsZero() {
        assertEquals(2, usersM.lengthMatchedScore("aa", "aa"));
    }

    @Test
    public void testLengthMatched_upperCaseString_returnsZeroIgnoringCase() {
        assertEquals(2, usersM.lengthMatchedScore("AA", "aa"));
    }

    @Test
    public void testLengthMatched_upperCaseTerm_returnsZeroIgnoringCase() {
        assertEquals(2, usersM.lengthMatchedScore("aa", "AA"));
    }

    @Test
    public void testNameMatchesOneOf_matchesNoneOf_returnsFalse() {
        String name = "daniel";
        List tokens = new ArrayList<>(Arrays.asList("Logan", "Paul"));
        assertFalse(usersM.nameMatchesOneOf(name, tokens));
    }

    @Test
    public void testNameMatchesOneOf_matchesOneOf_returnsTrue() {
        String name = "daniel";
        List tokens = new ArrayList<>(Arrays.asList("daniel", "Paul"));
        assertTrue(usersM.nameMatchesOneOf(name, tokens));
    }

    @Test
    public void testNameMatchesOneOf_emptyStringNonEmptyTokens_returnsFalse() {
        String name = "";
        List tokens = new ArrayList<>(Arrays.asList("daniel", "dan"));
        assertFalse(usersM.nameMatchesOneOf(name, tokens));
    }


    @Test
    public void testScoreNames_singleNameMatched_returnsWeight() {
        User user = new User("Logan", LocalDate.now());
        List tokens = new ArrayList<>(Arrays.asList("Logan"));
        assertEquals(1, usersM.scoreNames(user.getNameArray(), tokens, 0, 1, 1));
    }

    @Test
    public void testScoreNames_singleNameUnmatched_returnsZero() {
        User user = new User("Logan", LocalDate.now());
        List tokens = new ArrayList<>(Arrays.asList("Logana"));
        assertEquals(0, usersM.scoreNames(user.getNameArray(), tokens, 0, 1, 1));
    }

    @Test
    public void testScoreNames_multipleNamesMatchedInMiddle_returnsNumberOfMiddleNamesTimesWeight() {
        User user = new User("a,b,c,d,e,f,g,h", LocalDate.now());
        List tokens = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));
        assertEquals(6, usersM.scoreNames(user.getNameArray(), tokens, 1, 7, 1));
    }

    @Test
    public void testScoreNames_someNamesMatched_returnsNumberOfMatchedNamesTimesWeight() {
        User user = new User("a,w,c,x,e,y,g,z", LocalDate.now());
        List tokens = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));
        assertEquals(4, usersM.scoreNames(user.getNameArray(), tokens, 0, 8, 1));
    }

    @Test
    public void testScoreNames_startIndexSameAsEndIndex_returnsZero() {
        User user = new User("a,w,c,x,e,y,g,z", LocalDate.now());
        List tokens = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"));
        assertEquals(0, usersM.scoreNames(user.getNameArray(), tokens, 4, 4, 1));
    }


    @Test
    public void testAllTokensMatched_zeroTokens_returnsTrue() {
        User user = new User("a,w,c,x,e,y,g,z", LocalDate.now());
        List tokens = new ArrayList<String>(Arrays.asList());
        assertTrue(usersM.allTokensMatched(user, tokens));
    }

    @Test
    public void testAllTokensMatched_notAllTokensMatched_returnsFalse() {
        User user = new User("a,b,c", LocalDate.now());
        List tokens = new ArrayList<>(Arrays.asList("a", "b"));
        assertTrue(usersM.allTokensMatched(user, tokens));
    }

    @Test
    public void testAllTokensMatched_allTokensMatched_returnsTrue() {
        User user = new User("a,b,c", LocalDate.now());
        user.setPreferredNameArray(new String[]{"c", "d", "e"});
        List tokens = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e"));
        assertTrue(usersM.allTokensMatched(user, tokens));
    }

    @Test
    public void testScoreUserOnSearch_firstMiddleAndLastMatched_returnsCorrectScore() {
        User user = new User("Logan,Potter,Robbie,Rambo", LocalDate.now());
        List tokens = new ArrayList<>(Arrays.asList(
                "Logan", "Potter", "Robbie", "Rambo"
        ));
        assertEquals(6 + 1 + 1 + 2, usersM.scoreUserOnSearch(user, tokens));
    }

    @Test
    public void testScoreUserOnSearch_onlyMatchedFirstName_returnsScoreForMatchedFirstName() {
        User user = new User("Logan", LocalDate.now());
        List tokens = new ArrayList<>(Arrays.asList(
                "Logan"
        ));
        assertEquals(2, usersM.scoreUserOnSearch(user, tokens));
    }

    @Test
    public void testScoreUserOnSearch_onlyMatchedFirstAndLastNames_returnsCorrectScore() {
        User user = new User("Logan,Rambo", LocalDate.now());
        List tokens = new ArrayList<>(Arrays.asList(
                "Logan", "Rambo"
        ));
        assertEquals(6 + 2, usersM.scoreUserOnSearch(user, tokens));
    }

    @Test
    public void testScoreUserOnSearch_firstMatchedLastUnmatched_returnsCorrectScore() {
        User user = new User("Logan,Rambo", LocalDate.now());
        List tokens = new ArrayList<>(Arrays.asList(
                "Logan"
        ));
        assertEquals(2, usersM.scoreUserOnSearch(user, tokens));
    }

    @Test
    public void testScoreUserOnSearch_preferredFirstAndLastNamesMatch_returnsCorrectScore() {
        User user = new User("Logan,Potter,Robbie,Rambo", LocalDate.now());
        user.setPreferredNameArray(new String[]{"Herman", "Walter", "Benjamin"});
        List tokens = new ArrayList<>(Arrays.asList(
                "Herman", "Walter", "Benjamin"
        ));
        assertEquals(5 + 4 + 3, usersM.scoreUserOnSearch(user, tokens));
    }

    @Test
    public void testScoreUserOnSearch_unchangedPreferredNameMatched_returnsScoreForNameMatched() {
        User user = new User("Logan,Potter,Robbie,Rambo", LocalDate.now());
        user.setPreferredNameArray(new String[]{"Logan", "Potter", "Robbie", "Rambo"});
        List tokens = new ArrayList<>(Arrays.asList(
                "Logan", "Potter", "Robbie", "Rambo"
        ));
        assertEquals(2 + 1 + 1 + 6, usersM.scoreUserOnSearch(user, tokens));
    }

    @Test
    public void testScoreUserOnSearch_preferredAndNamePartiallyMatched_returnsScoreOfMatchedNames() {
        User user = new User("Logan,Potter,Robbie,Rambo", LocalDate.now());
        user.setPreferredNameArray(new String[]{"Herman", "Walter", "Benjamin"});

        List tokens = new ArrayList<>(Arrays.asList(
                "Logan", "Ra", "B", "Potter"
        ));
        assertEquals(2 + 6 + 5 + 1, usersM.scoreUserOnSearch(user, tokens));
    }

    @Test
    public void testScoreUserOnSearch_unmatchedName_returnsZero() {
        User user = new User("Logan,Potter,Robbie,Rambo", LocalDate.now());
        user.setPreferredNameArray(new String[]{"Herman", "Walter", "Benjamin"});

        List tokens = new ArrayList<>(Arrays.asList(
                "Logan", "Potter", "Crumb"
        ));
        assertEquals(0, usersM.scoreUserOnSearch(user, tokens));
    }
}
