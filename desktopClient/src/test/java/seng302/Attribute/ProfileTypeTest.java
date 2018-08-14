package seng302.Attribute;

import org.junit.Test;
import seng302.User.Attribute.ProfileType;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class ProfileTypeTest {

    @Test
    public void testValidLowerCaseParse() {
        assertEquals(ProfileType.parse("User"), ProfileType.USER);
    }

    @Test
    public void testValidMixedCaseParse() {
        assertEquals(ProfileType.parse("ADmiN"), ProfileType.ADMIN);
    }

    @Test
    public void testAllCapsParse() {
        assertEquals(ProfileType.parse("CLINICIAN"), ProfileType.CLINICIAN);
    }

    @Test
    public void testCanParseEnumOutput() {
        try {
            ProfileType.parse(ProfileType.CLINICIAN.toString());
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testInvalidParse() {
        boolean invalidCaught = false;
        try {
            ProfileType.parse("invalid");
        } catch (IllegalArgumentException e) {
            invalidCaught = true;
        }
        assertTrue(invalidCaught);
    }
}
