package seng302.Attribute;

import org.junit.Test;
import seng302.User.Attribute.BloodType;

import static junit.framework.TestCase.*;

public class BloodTypeTest {

    @Test
    public void testValidLowerCaseParse() {
        assertEquals(BloodType.parse("a+"), BloodType.A_POS);
    }

    @Test
    public void testValidMixedCaseParse() {
        assertEquals(BloodType.parse("aB-"), BloodType.AB_NEG);
    }

    @Test
    public void testAllCapsParse() {
        assertEquals(BloodType.parse("O-"), BloodType.O_NEG);
    }

    @Test
    public void testCanParseEnumOutput() {
        try {
            BloodType.parse(BloodType.A_POS.toString());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testInvalidParse() {
        boolean invalidCaught = false;
        try {
            BloodType.parse("invalid");
        } catch (IllegalArgumentException e) {
            invalidCaught = true;
        }
        assertTrue(invalidCaught);
    }
}