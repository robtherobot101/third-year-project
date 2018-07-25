package seng302.Attribute;

import org.junit.Test;
import seng302.User.Attribute.Organ;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class OrganTest {

    @Test
    public void testValidLowerCaseParse() {
        assertEquals(Organ.parse("cornea"), Organ.CORNEA);
    }

    @Test
    public void testValidMixedCaseParse() {
        assertEquals(Organ.parse("kIdney"), Organ.KIDNEY);
    }

    @Test
    public void testAllCapsParse() {
        assertEquals(Organ.parse("LIVER"), Organ.LIVER);
    }

    @Test
    public void testCanParseEnumOutput() {
        try {
            Organ.parse(Organ.KIDNEY.toString());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testInvalidParse() {
        boolean invalidCaught = false;
        try {
            Organ.parse("invalid");
        } catch (IllegalArgumentException e) {
            invalidCaught = true;
        }
        assertTrue(invalidCaught);
    }
}