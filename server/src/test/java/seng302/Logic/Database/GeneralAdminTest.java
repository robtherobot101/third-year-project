package seng302.Logic.Database;

import com.mchange.util.AssertException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import seng302.Config.DatabaseConfiguration;
import seng302.HelperMethods;
import seng302.Model.Admin;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import static org.junit.Assert.*;

public class GeneralAdminTest {

    private GeneralAdmin generalAdmin = new GeneralAdmin();


    @BeforeClass
    @AfterClass
    public static void reset() throws SQLException {
        Administration administration = new Administration();
        administration.reset();
        administration.resample();
    }

    @Test
    public void getAdminFromResultSet() {
    }

    /**
     * Test insertion of a valid Administrator
     * @throws SQLException
     */
    @Test
    public void insertAdmin() throws SQLException {
        Admin admin = HelperMethods.insertAdmin(generalAdmin);
        assertEquals(admin, generalAdmin.getAdminFromId((int) admin.getStaffID()));
    }

    /**
     * Test insertion of an admind with a non-unique username
     * @throws SQLException
     */
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void insertDuplicateAdmin() throws SQLException {
        int adminsBefore = generalAdmin.getAllAdmins().size();
        Admin admin = HelperMethods.insertAdmin(generalAdmin);
        generalAdmin.insertAdmin(admin);
        assertEquals(adminsBefore + 1, generalAdmin.getAllAdmins().size());
    }

    /**
     * Test insertion of an admin with no username
     * @throws SQLException
     */
    @Test(expected = Exception.class)
    public void insertAdminNoUsername() throws SQLException {
        Admin admin = new Admin("", "password", "Full Name");
        generalAdmin.insertAdmin(admin);
    }

    /**
     * Test getting an ID for a valid username
     * @throws SQLException
     */
    @Test
    public void getAdminIdFromUsername() throws SQLException {
        Admin admin = HelperMethods.insertAdmin(generalAdmin);
        assertEquals(admin.getStaffID(), generalAdmin.getAdminIdFromUsername(admin.getUsername()));
    }

    /**
     * Test getting an ID for an invalid username
     * @throws SQLException
     */
    @Test(expected = Exception.class)
    public void getAdminIdFromBadUsername() throws SQLException {
        generalAdmin.getAdminIdFromUsername("This is not a real username");
    }


    /**
     * Test getting admin with id 1
     * @throws SQLException
     */
    @Test
    public void getAdminFromId() throws SQLException {
        Admin admin = generalAdmin.getAdminFromId(1);
        assertEquals(1, admin.getStaffID());
    }

    /**
     * Test getting admin with id 9999
     * @throws SQLException
     */
    @Test(expected = Exception.class)
    public void getAdminFromBadId() throws SQLException {
        Admin admin = generalAdmin.getAdminFromId(9999);
        assertNull(admin);
    }

    /**
     * Test the getAllAdmins method
     * @throws SQLException
     */
    @Test
    public void getAllAdmins() throws SQLException {
        List<Admin> adminsBefore = generalAdmin.getAllAdmins();
        Admin admin = HelperMethods.insertAdmin(generalAdmin);
        List<Admin> adminsAfter = generalAdmin.getAllAdmins();
        assertTrue(adminsAfter.containsAll(adminsBefore));
        assertEquals(adminsBefore.size() + 1, adminsAfter.size());
    }

    @Test
    public void removeAdmin() throws SQLException {
        Admin admin = HelperMethods.insertAdmin(generalAdmin);
        assertTrue(generalAdmin.getAllAdmins().contains(admin));
        generalAdmin.removeAdmin(admin);
        assertFalse(generalAdmin.getAllAdmins().contains(admin));
    }

    @Test
    public void updateAdminDetails() throws SQLException {
        Admin admin = HelperMethods.insertAdmin(generalAdmin);
        admin.setStaffID(generalAdmin.getAdminIdFromUsername(admin.getUsername()));
        admin.setRegion("The Moon");
        admin.setName("Linus Torvalds");
        admin.setWorkAddress("140 Maidstone Rd");
        generalAdmin.updateAdminDetails(admin);
        Admin admin2 = generalAdmin.getAdminFromId(generalAdmin.getAdminIdFromUsername(admin.getUsername()));
        System.out.println(admin2.getRegion());
        assertEquals(admin, admin2);
    }
}