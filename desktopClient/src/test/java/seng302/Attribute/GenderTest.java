package seng302.Attribute;

import org.junit.Test;
import seng302.User.Attribute.Gender;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class GenderTest {

    @Test
    public void testValidLowerCaseParse() {
        assertEquals(Gender.parse("male"), Gender.MALE);
    }

    @Test
    public void testValidMixedCaseParse() {
        assertEquals(Gender.parse("fEmaLE"), Gender.MALE);
    }

    @Test
    public void testAllCapsParse() {
        assertEquals(Gender.parse("NON-BINARY"), Gender.NONBINARY);
    }

    @Test
    public void testCanParseEnumOutput() {
        try {
            Gender.parse(Gender.MALE.toString());
        } catch (IllegalArgumentException e) {
            fail();
        }
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
