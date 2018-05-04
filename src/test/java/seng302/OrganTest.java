package seng302;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import org.junit.Test;
import seng302.User.Attribute.Organ;

public class OrganTest {

    @Test
    public void testValidParse() {
        assertEquals(Organ.parse("kIdney"), Organ.KIDNEY);
        assertEquals(Organ.parse("cornea"), Organ.CORNEA);
        assertEquals(Organ.parse("LIVER"), Organ.LIVER);
        assertEquals(Organ.parse("bone-Marrow"), Organ.BONE);
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