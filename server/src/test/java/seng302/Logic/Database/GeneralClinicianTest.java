package seng302.Logic.Database;

import org.junit.Test;
import seng302.Config.DatabaseConfiguration;
import seng302.HelperMethods;
import seng302.Model.Clinician;

import java.sql.*;
import java.util.List;

import static org.junit.Assert.*;

public class GeneralClinicianTest extends GenericTest {

    private GeneralClinician generalClinician = new GeneralClinician();

    /**
     * Test the function to get clinicians from a raw resultset object from the database
     * @throws SQLException catch sql execution exceptions
     */
    @Test
    public void getClinicianFromResultSet() throws SQLException {
        Clinician clinician = HelperMethods.insertClinician(generalClinician);
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "SELECT * FROM CLINICIAN JOIN ACCOUNT WHERE staff_id = id AND username = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, clinician.getUsername());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                assertEquals(clinician, generalClinician.getClinicianFromResultSet(resultSet));
                return;
            }
        }
        fail();
    }

    /**
     * Test insertion of a valid Clinicianistrator
     * @throws SQLException catch sql execution exceptions
     */
    @Test
    public void insertClinician() throws SQLException {
        Clinician clinician = HelperMethods.insertClinician(generalClinician);
        assertEquals(clinician, generalClinician.getClinicianFromId(generalClinician.getClinicianIdFromUsername(clinician.getUsername())));
    }

    /**
     * Test insertion of an cliniciand with a non-unique username
     * @throws SQLException catch sql execution exceptions
     */
    @Test(expected = SQLIntegrityConstraintViolationException.class)
    public void insertDuplicateClinician() throws SQLException {
        int cliniciansBefore = generalClinician.getAllClinicians().size();
        Clinician clinician = HelperMethods.insertClinician(generalClinician);
        generalClinician.insertClinician(clinician);
        assertEquals(cliniciansBefore + 1, generalClinician.getAllClinicians().size());
    }

    /**
     * Test insertion of an clinician with no username
     * @throws SQLException catch sql execution exceptions
     */
    @Test(expected = Exception.class)
    public void insertClinicianNoUsername() throws SQLException {
        Clinician clinician = new Clinician(null, "password", "Full Name");
        generalClinician.insertClinician(clinician);
    }

    /**
     * Test getting an ID for a valid username
     * @throws SQLException catch sql execution exceptions
     */
    @Test
    public void getClinicianIdFromUsername() throws SQLException {
        Clinician clinician = generalClinician.getClinicianFromId(2);
        assertEquals(clinician.getStaffID(), generalClinician.getClinicianIdFromUsername(clinician.getUsername()));
    }

    /**
     * Test getting an ID for an invalid username
     * @throws SQLException catch sql execution exceptions
     */
    @Test(expected = Exception.class)
    public void getClinicianIdFromBadUsername() throws SQLException {
        generalClinician.getClinicianIdFromUsername("This is not a real username");
    }


    /**
     * Test getting clinician with id 1
     * @throws SQLException catch sql execution exceptions
     */
    @Test
    public void getClinicianFromId() throws SQLException {
        Clinician clinician = generalClinician.getClinicianFromId(2);
        assertEquals(2, clinician.getStaffID());
    }

    /**
     * Test getting clinician with id 9999
     * @throws SQLException catch sql execution exceptions
     */
    @Test
    public void getClinicianFromBadId() throws SQLException {
        Clinician clinician = generalClinician.getClinicianFromId(9999);
        assertNull(clinician);
    }

    /**
     * Test the getAllClinicians method
     * @throws SQLException catch sql execution exceptions
     */
    @Test
    public void getAllClinicians() throws SQLException {
        List<Clinician> cliniciansBefore = generalClinician.getAllClinicians();
        HelperMethods.insertClinician(generalClinician);
        List<Clinician> cliniciansAfter = generalClinician.getAllClinicians();
        assertTrue(cliniciansAfter.containsAll(cliniciansBefore));
        assertEquals(cliniciansBefore.size() + 1, cliniciansAfter.size());
    }

    @Test
    public void removeClinician() throws SQLException {
        Clinician clinician = HelperMethods.insertClinician(generalClinician);
        assertTrue(generalClinician.getAllClinicians().contains(clinician));
        generalClinician.removeClinician(clinician);
        assertFalse(generalClinician.getAllClinicians().contains(clinician));
    }

    @Test
    public void updateClinicianDetails() throws SQLException {
        Clinician clinician = HelperMethods.insertClinician(generalClinician);
        clinician.setRegion("The Moon");
        clinician.setName("Linus Torvalds");
        clinician.setWorkAddress("140 Maidstone Rd");
        generalClinician.updateClinicianDetails(clinician, (int) clinician.getStaffID(), clinician.getPassword());
        Clinician clinician2 = generalClinician.getClinicianFromId((int) clinician.getStaffID());
        assertEquals(clinician, clinician2);
    }
}