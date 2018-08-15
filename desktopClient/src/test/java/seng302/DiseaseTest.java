package seng302;

import org.junit.Before;
import org.junit.Test;
import seng302.User.Disease;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;
import static junit.framework.TestCase.assertEquals;

public class DiseaseTest {
    private Disease d1, d2, d3;

    @Before
    public void setup() {
        d1 = new Disease("a", LocalDate.now(), false, false);
        d2 = new Disease("bsc", LocalDate.now().minus(1, DAYS), false, true);
        d3 = new Disease("cawg", LocalDate.now().minus(100, DAYS), true, false);
    }

    @Test
    public void testAscNameComparatorFalse() {
        assertEquals(-1, Disease.ascNameComparator.compare(d1, d2));
    }

    @Test
    public void testAscNameComparatorTrue() {
        assertEquals(1, Disease.ascNameComparator.compare(d2, d1));
    }

    @Test
    public void testDescNameComparatorFalse() {
        assertEquals(0, Disease.descNameComparator.compare(d3, d2));
    }

    @Test
    public void testDescNameComparatorTrue() {
        assertEquals(1, Disease.descNameComparator.compare(d2, d3));
    }

    @Test
    public void testAscDateComparatorFalse() {
        assertEquals(0, Disease.ascDateComparator.compare(d1, d2));
    }

    @Test
    public void testAscDateComparatorTrue() {
        assertEquals(1, Disease.ascDateComparator.compare(d2, d1));
    }

    @Test
    public void testDescDateComparatorFalse() {
        assertEquals(0, Disease.descDateComparator.compare(d3, d2));
    }

    @Test
    public void testDescDateComparatorTrue() {
        assertEquals(1, Disease.descDateComparator.compare(d2, d3));
    }
}
