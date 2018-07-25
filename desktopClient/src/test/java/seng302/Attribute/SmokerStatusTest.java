package seng302.Attribute;

import org.junit.Test;
import seng302.User.Attribute.SmokerStatus;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class SmokerStatusTest {

    @Test
    public void testValidLowerCaseParse() {
        assertEquals(SmokerStatus.parse("current"), SmokerStatus.CURRENT);
    }

    @Test
    public void testValidMixedCaseParse() {
        assertEquals(SmokerStatus.parse("paST"), SmokerStatus.PAST);
    }

    @Test
    public void testAllCapsParse() {
        assertEquals(SmokerStatus.parse("NEVER"), SmokerStatus.NEVER);
    }

    @Test
    public void testCanParseEnumOutput() {
        try {
            SmokerStatus.parse(SmokerStatus.NEVER.toString());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testInvalidParse() {
        boolean invalidCaught = false;
        try {
            SmokerStatus.parse("invalid");
        } catch (IllegalArgumentException e) {
            invalidCaught = true;
        }
        assertTrue(invalidCaught);
    }
}
