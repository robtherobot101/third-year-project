package seng302.Logic.Database;

import org.junit.Test;
import seng302.HelperMethods;
import seng302.Logic.SaltHash;
import seng302.Model.Admin;
import seng302.Model.Attribute.ProfileType;
import seng302.Model.Clinician;
import seng302.Model.User;
import spark.Request;

import java.sql.SQLException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AuthorizationTest extends GenericTest {
    private Authorization model = new Authorization();
    private ProfileUtils profileModel = new ProfileUtils();

    @Test
    public void testLogin() throws SQLException {
        User newUser = HelperMethods.insertUser(new GeneralUser(), "testpass");
        User r = model.loginUser(newUser.getUsername(), "testpass");
        assertEquals(newUser, r);
    }

    @Test
    public void testLoginToken() throws SQLException {
        Clinician newClin = HelperMethods.insertClinician(new GeneralClinician(), "pass1");
        Clinician r = model.loginClinician(newClin.getUsername(), "pass1");
        String token = model.generateToken((int)r.getStaffID(), ProfileType.CLINICIAN.getAccessLevel());
        assertEquals(ProfileType.CLINICIAN.getAccessLevel(), profileModel.checkToken(token));
    }

    @Test
    public void testTokenId() throws SQLException {
        Admin newAdmin = HelperMethods.insertAdmin(new GeneralAdmin(), "pas123");
        Admin r = model.loginAdmin(newAdmin.getUsername(), "pas123");
        String token = model.generateToken((int)r.getStaffID(), ProfileType.ADMIN.getAccessLevel());
        assertTrue(profileModel.checkTokenId(token, ProfileType.ADMIN, (int)newAdmin.getStaffID()));
    }

    @Test
    public void testLogout() throws SQLException {
        Admin newAdmin = HelperMethods.insertAdmin(new GeneralAdmin(), "testpass1");
        Admin r = model.loginAdmin(newAdmin.getUsername(), "testpass1");
        String token = model.generateToken((int)r.getStaffID(), ProfileType.ADMIN.getAccessLevel());
        model.logout(token);
        assertFalse(profileModel.checkTokenId(token, ProfileType.ADMIN, (int)newAdmin.getStaffID()));
    }

    @Test
    public void checkUserPasswordValid() throws SQLException {
        User newUser = HelperMethods.insertUser(new GeneralUser(), "testpass5");
        boolean matchedPassword = SaltHash.checkHash("testpass5", model.checkPasswordUser(newUser.getId()));
        assertTrue(matchedPassword);
    }

    @Test
    public void checkUserPasswordInvalid() throws SQLException {
        User newUser = HelperMethods.insertUser(new GeneralUser(), "testpass7");
        boolean matchedPassword = SaltHash.checkHash("testpass8", model.checkPasswordUser(newUser.getId()));
        assertFalse(matchedPassword);
    }

    @Test
    public void checkClinicianPasswordValid() throws SQLException {
        Clinician newClin = HelperMethods.insertClinician(new GeneralClinician(), "cLinp1");
        boolean matchedPassword = SaltHash.checkHash("cLinp1", model.checkPasswordClinician(newClin.getStaffID()));
        assertTrue(matchedPassword);
    }

    @Test
    public void checkClinicianPasswordInvalid() throws SQLException {
        Clinician newClin = HelperMethods.insertClinician(new GeneralClinician(), "clinp144");
        boolean matchedPassword = SaltHash.checkHash("cliNp144", model.checkPasswordClinician(newClin.getStaffID()));
        assertFalse(matchedPassword);
    }
}
