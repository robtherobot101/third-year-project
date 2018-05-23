package seng302;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;
import seng302.User.Attribute.Gender;
import seng302.User.Medication.DrugInteraction;

public class DrugInteractionTest {

    private static DrugInteraction drugInteraction;

    @Before
    public void setUp() {
        try {
            String json = new String(Files.readAllBytes(Paths.get("src/test/java/seng302/DrugInteractionTestingJson")));
            drugInteraction = new DrugInteraction(json);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void ageInteraction_negativeAge_returnsEmptyHashSet() {
        assertEquals(new HashSet<String>(), drugInteraction.ageInteraction(-1));
    }

    @Test
    public void ageInteraction_lowerBoundaryAge_returnsNANSymptomsAndSymptomsInRange() {
        HashSet<String> symptoms = drugInteraction.ageInteraction(0);
        assertEquals(new HashSet<>(Arrays.asList("a", "h")), symptoms);
    }

    @Test
    public void ageInteraction_upperBoundaryAge_returnsNANSymptomsAndSymptomsInRange() {
        HashSet<String> symptoms = drugInteraction.ageInteraction(1);
        assertEquals(new HashSet<>(Arrays.asList("a", "h")), symptoms);
    }

    @Test
    public void ageInteraction_ageAtLeastSixty_returnsNanSymptomsAndSymptomsInRange() {
        HashSet<String> symptoms = drugInteraction.ageInteraction(60);
        assertEquals(new HashSet<>(Arrays.asList("g", "h")), symptoms);
    }

    @Test
    public void ageRangeInteraction_bottomRange_returnsSymptomsFromNANAndBottomRange() {
        HashSet<String> symptoms = drugInteraction.ageRangeInteraction("0-1");
        assertEquals(new HashSet<>(Arrays.asList("a", "h")), symptoms);
    }

    @Test
    public void ageRangeInteraction_topRange_returnsSymptomsFromNANAndTopRange() {
        HashSet<String> symptoms = drugInteraction.ageRangeInteraction("60+");
        assertEquals(new HashSet<>(Arrays.asList("g", "h")), symptoms);
    }

    @Test
    public void ageRangeInteraction_undefinedRange_returnsSymptomsFromNAN() {
        HashSet<String> symptoms = drugInteraction.ageRangeInteraction("SomeUndefinedRangeKey");
        assertEquals(new HashSet<>(Arrays.asList("h")), symptoms);
    }

    @Test
    public void nanAgeInteraction_called_returnsSymptomsFromNAN() {
        HashSet<String> symptoms = drugInteraction.nanAgeInteraction();
        assertEquals(new HashSet<>(Arrays.asList("h")), symptoms);
    }

    @Test
    public void allGenderInteractions_called_returnsMaleAndFemaleSymtoms() {
        HashSet<String> symptoms = drugInteraction.allGenderInteractions();
        assertEquals(new HashSet<>(Arrays.asList("a", "b", "c", "d", "e")), symptoms);
    }

    @Test
    public void maleInteractions_called_returnsMaleSymtoms() {
        HashSet<String> symptoms = drugInteraction.maleInteractions();
        assertEquals(new HashSet<>(Arrays.asList("c", "d", "e")), symptoms);
    }

    @Test
    public void femaleInteractions_called_returnsMaleSymtoms() {
        HashSet<String> symptoms = drugInteraction.femaleInteractions();
        assertEquals(new HashSet<>(Arrays.asList("a", "b", "c")), symptoms);
    }

    @Test
    public void genderInteraction_validGender_returnsGenderSympmtoms() {
        HashSet<String> symptoms = drugInteraction.genderInteraction(Gender.NONBINARY);
        assertEquals(new HashSet<String>(Arrays.asList("a", "b", "c", "d", "e")), symptoms);
    }

    @Test
    public void genderInteraction_nullGender_returnsAllGenderSymtoms() {
        HashSet<String> symptoms = drugInteraction.genderInteraction(null);
        assertEquals(new HashSet<>(Arrays.asList("a", "b", "c", "d", "e")), symptoms);
    }

    @Test
    public void invertDurationMap_givenNonEmptyDurationMap_returnsInverse() {
        HashMap<String, HashSet<String>> originalDurationMap = drugInteraction.getDurationInteraction();
        HashMap<String, String> invertedDurationMap =
            drugInteraction.invertDurationMap(originalDurationMap);
        assertEquals("1 - 2 years", invertedDurationMap.get("a"));
        assertEquals("1 - 6 months", invertedDurationMap.get("b"));
        assertEquals("10+ years", invertedDurationMap.get("c"));
        assertEquals("2 - 5 years", invertedDurationMap.get("d"));
        assertEquals("5 - 10 years", invertedDurationMap.get("e"));
        assertEquals("6 - 12 months", invertedDurationMap.get("f"));
        assertEquals("< 1 month", invertedDurationMap.get("g"));
        assertEquals("not specified", invertedDurationMap.get("h"));
    }

    @Test
    public void invertDurationMap_givenEmptyDurationMap_returnsEmptyInvertedDurationMap() {
        HashMap<String, HashSet<String>> originalDurationMap = new HashMap<>();
        HashMap<String, String> invertedDurationMap =
            drugInteraction.invertDurationMap(originalDurationMap);
        assertEquals(0, invertedDurationMap.keySet().size());
    }


    @Test
    public void getDuration_definedKey_returnsIntendedDuration() {
        assertEquals("1 - 2 years", drugInteraction.getDuration("a"));
    }

    @Test
    public void getDuration_undefinedKey_returnsNotSpecified() {
        assertEquals("not specified", drugInteraction.getDuration("SomeUndefinedKey"));
    }

    @Test
    public void greaterDuration_shorterDurationPassedFirst_returnsGreaterDuration() {
        assertEquals("< 1 month", drugInteraction.greaterDuration("not specified", "< 1 month"));
    }

    @Test
    public void greaterDuration_greaterDurationPassedFirst_returnsGreaterDuration() {
        assertEquals("< 1 month", drugInteraction.greaterDuration("< 1 month", "not specified"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void greaterDuration_invalidDurationKeysPassed_throwsException() {
        drugInteraction.greaterDuration("bad key", "another bad key");
    }
}