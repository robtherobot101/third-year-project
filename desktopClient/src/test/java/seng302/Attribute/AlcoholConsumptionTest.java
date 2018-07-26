package seng302.Attribute;

import org.junit.Test;
import seng302.User.Attribute.AlcoholConsumption;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class AlcoholConsumptionTest {

    @Test
    public void testValidLowerCaseParse() {
        assertEquals(AlcoholConsumption.parse("low"), AlcoholConsumption.LOW);
    }

    @Test
    public void testValidMixedCaseParse() {
        assertEquals(AlcoholConsumption.parse("nONe"), AlcoholConsumption.NONE);
    }

    @Test
    public void testAllCapsParse() {
        assertEquals(AlcoholConsumption.parse("ALCOHOLIC"), AlcoholConsumption.ALCOHOLIC);
    }

    @Test
    public void testCanParseEnumOutput() {
        try {
            AlcoholConsumption.parse(AlcoholConsumption.ALCOHOLIC.toString());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testInvalidParse() {
        boolean invalidCaught = false;
        try {
            AlcoholConsumption.parse("invalid");
        } catch (IllegalArgumentException e) {
            invalidCaught = true;
        }
        assertTrue(invalidCaught);
    }
}
