package seng302;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import seng302.Core.Donor;
import seng302.Core.Main;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple Main.
 */
public class MainTest {
    @Before
    public void setup() {
        Main.donors = new ArrayList<>();
        Main.recalculateNextId(true);
        Main.donors.add(new Donor("Andrew,Neil,Davidson", "01/02/1998", "01/11/4000", "male", 12.1, 50.45, "o+", "Canterbury", "1235 abc Street"));
        Main.donors.add(new Donor("Test Donor,Testperson", "01/04/1530", "31/01/1565", "other", 1.234, 1.11111, "a-", "Auckland", "street sample " +
                "text"));
        Main.donors.add(new Donor("Singlename", LocalDate.parse("12/06/1945", Donor.dateFormat)));
        Main.donors.add(new Donor("Donor 2,Person", "01/12/1990", "09/03/2090", "female", 2, 60, "b-", "Sample Region", "Sample Address"));
        Main.donors.add(new Donor("a,long,long,name", "01/11/3000", "01/11/4000", "other", 0.1, 12.4, "b-", "Example region", "Example Address " +
                "12345"));
    }

    @Test
    public void testGetById() {
        assertEquals(Main.donors.get(2), Main.getDonorById(2));
        Main.donors.remove(2);
        assertEquals(null, Main.getDonorById(2));
    }

    @Test
    public void testGetByName() {
        assertEquals(2, Main.getDonorByName(new String[]{"Donor", "Person"}).size());
        assertEquals(1, Main.getDonorByName(new String[]{"nglenam"}).size());
        assertEquals("Singlename", Main.getDonorByName(new String[]{"nglenam"}).get(0).getName());
        assertEquals(1, Main.getDonorByName(new String[]{"a", "on", "lo", "me"}).size());
        assertEquals(0, Main.getDonorByName(new String[]{"name", "long"}).size());
    }

    @Test
    public void testImportSave() {
        Main.donors.add(new Donor("extra", LocalDate.parse("01/01/1000", Donor.dateFormat)));
        Main.saveUsers("testsave", true);
        Main.donors.remove(5);
        assertEquals(5, Main.donors.size());
        Main.importUsers("testsave", true);
        assertEquals("extra", Main.donors.get(5).getName());
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
        assertEquals(true, Main.matches("aaa", "aaa"));
    }
    @Test
    public void testMatches_longerSearchTerm_returnsFalse(){
        assertEquals(false, Main.matches("aaa", "aaaa"));
    }
    @Test
    public void testMatches_longerStringShouldMatch_returnsTrue(){
        assertEquals(true, Main.matches("abcd", "abc"));
    }
    @Test
    public void testMatches_emptyStringAndTerm_returnsTrue(){
        assertEquals(true, Main.matches("", ""));
    }
    @Test
    public void testMatches_uppercaseTerm_returnsTrue(){
        assertEquals(true, Main.matches("aa", "AA"));
    }
    @Test
    public void testMatches_uppercaseString_returnsTrue(){
        assertEquals(true, Main.matches("AA", "aa"));
    }

    @Test
    public void testGetDonorsByNameAlternative_equallyMatched_SortedAlphabetically(){
        LocalDate dummyDate = LocalDate.of(2000, 1, 1);
        Main.donors.clear();
        Main.donors.add(new Donor("aad,coco", dummyDate));
        Main.donors.add(new Donor("aaa,coco", dummyDate));
        Main.donors.add(new Donor("aab,coco", dummyDate));
        Main.donors.add(new Donor("aac,coco", dummyDate));
        ArrayList<Donor> results = Main.getDonorsByNameAlternative("aa");
        assertEquals("aaa coco", results.get(0).getName());
        assertEquals("aab coco", results.get(1).getName());
        assertEquals("aac coco", results.get(2).getName());
        assertEquals("aad coco", results.get(3).getName());
    }
    @Test
    public void testGetDonorsByNameAlternative_differentlyMatched_sortedByMatchedAmount(){
        LocalDate dummyDate = LocalDate.of(2000, 1, 1);
        Main.donors.clear();
        Main.donors.add(new Donor("abcde", dummyDate));
        Main.donors.add(new Donor("ab", dummyDate));
        Main.donors.add(new Donor("abc", dummyDate));
        Main.donors.add(new Donor("abcd", dummyDate));
        ArrayList<Donor> results = Main.getDonorsByNameAlternative("a");
        System.out.println(results.get(0).getName());
        assertEquals("ab", results.get(0).getName());
        assertEquals("abc", results.get(1).getName());
        assertEquals("abcd", results.get(2).getName());
        assertEquals("abcde", results.get(3).getName());
    }
    @Test
    public void testGetDonorsByNameAlternative_doesNotMatch_noResults(){
        Main.donors.clear();
        LocalDate dummyDate = LocalDate.of(2000, 1, 1);
        Main.donors.add(new Donor("abc,bcd,cde", dummyDate));
        ArrayList<Donor> results = Main.getDonorsByNameAlternative("d");
        assertEquals(0, results.size());
    }
    @Test
    public void testGetDonorsByNameAlternative_longerSearchTerm_noResults(){
        Main.donors.clear();
        LocalDate dummyDate = LocalDate.of(2000, 1, 1);
        Main.donors.add(new Donor("a", dummyDate));
        Main.donors.add(new Donor("ab", dummyDate));
        ArrayList<Donor> results = Main.getDonorsByNameAlternative("abc");
        assertEquals(0, results.size());
    }


    @After
    public void tearDown() {
        Main.donors = new ArrayList<>();
    }
}
