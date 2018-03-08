package seng302;

import org.junit.Test;
import seng302.Core.Gender;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class GenderTest {
    @Test
    public void testValidParse() {
        assertEquals(Gender.parse("malE"), Gender.MALE);
        assertEquals(Gender.parse("fEmAlE"), Gender.FEMALE);
        assertEquals(Gender.parse("female"), Gender.FEMALE);
        assertEquals(Gender.parse("OTHER"), Gender.OTHER);
    }

    @Test
    public void testInvalidParse() {
        boolean invalidCaught = false;
        try {
            Gender.parse("invalid");
        } catch (IllegalArgumentException e) {
            invalidCaught = true;
        }
        assertTrue(invalidCaught);
    }
}
