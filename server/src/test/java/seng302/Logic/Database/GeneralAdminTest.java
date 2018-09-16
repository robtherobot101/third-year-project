package seng302.Logic.Database;

import org.junit.Test;
import seng302.Config.DatabaseConfiguration;
import seng302.HelperMethods;
import seng302.Model.Admin;

import java.sql.*;
import java.util.List;

import static org.junit.Assert.*;

public class GeneralAdminTest extends GenericTest {

    private GeneralAdmin generalAdmin = new GeneralAdmin();

    /**
     * Test the function to get admins from a raw resultset object from the database
     * @throws SQLException catch sql execution exceptions
     */
    @Test
    public void getAdminFromResultSet() throws SQLException {
        Admin admin = HelperMethods.insertAdmin(generalAdmin);
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "SELECT * FROM ADMIN WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, admin.getUsername());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                assertEquals(admin, generalAdmin.getAdminFromResultSet(resultSet));
                return;
            }
        }
        fail();
    }

    /**
     * Test insertion of a valid Administrator
     * @throws SQLException catch sql execution exceptions
     */
    @Test
    public void insertAdmin() throws SQLException {
        Admin admin = HelperMethods.insertAdmin(generalAdmin);
        assertEquals(admin, generalAdmin.getAdminFromId((int) admin.getStaffID()));
    }

    /**
     * Test insertion of an admind with a non-unique username
     * @throws SQLException catch sql execution exceptions
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
     * @throws SQLException catch sql execution exceptions
     */
    @Test(expected = Exception.class)
    public void insertAdminNoUsername() throws SQLException {
        Admin admin = new Admin(null, "password", "Full Name");
        generalAdmin.insertAdmin(admin);
    }

    /**
     * Test getting an ID for a valid username
     * @throws SQLException catch sql execution exceptions
     */
    @Test
    public void getAdminIdFromUsername() throws SQLException {
        Admin admin = HelperMethods.insertAdmin(generalAdmin);
        assertEquals(admin.getStaffID(), generalAdmin.getAdminIdFromUsername(admin.getUsername()));
    }

    /**
     * Test getting an ID for an invalid username
     * @throws SQLException catch sql execution exceptions
     */
    @Test(expected = Exception.class)
    public void getAdminIdFromBadUsername() throws SQLException {
        generalAdmin.getAdminIdFromUsername("This is not a real username");
    }


    /**
     * Test getting admin with id 1
     * @throws SQLException catch sql execution exceptions
     */
    @Test
    public void getAdminFromId() throws SQLException {
        Admin admin = generalAdmin.getAdminFromId(1);
        assertEquals(1, admin.getStaffID());
    }

    /**
     * Test getting admin with id 9999
     * @throws SQLException catch sql execution exceptions
     */
    @Test
    public void getAdminFromBadId() throws SQLException {
        Admin admin = generalAdmin.getAdminFromId(9999);
        assertNull(admin);
    }

    /**
     * Test the getAllAdmins method
     * @throws SQLException catch sql execution exceptions
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