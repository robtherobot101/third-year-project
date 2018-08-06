package seng302.Logic.Database;

import com.mchange.util.AssertException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import seng302.Config.DatabaseConfiguration;
import seng302.HelperMethods;
import seng302.Model.Admin;
import seng302.Model.Clinician;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import static org.junit.Assert.*;

public class GeneralClinicianTest extends GenericTest {

    private GeneralClinician generalClinician = new GeneralClinician();

    @Test
    public void getClinicianFromResultSet() {
    }

    /**
     * Test insertion of a valid Clinicianistrator
     * @throws SQLException
     */
    @Test
    public void insertClinician() throws SQLException {
        Clinician clinician = HelperMethods.insertClinician(generalClinician);
        assertEquals(clinician, generalClinician.getClinicianFromId(generalClinician.getClinicianIdFromUsername(clinician.getUsername())));
    }

    /**
     * Test insertion of an cliniciand with a non-unique username
     * @throws SQLException
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
     * @throws SQLException
     */
    @Test(expected = Exception.class)
    public void insertClinicianNoUsername() throws SQLException {
        Clinician clinician = new Clinician(null, "password", "Full Name");
        generalClinician.insertClinician(clinician);
    }

    /**
     * Test getting an ID for a valid username
     * @throws SQLException
     */
    @Test
    public void getClinicianIdFromUsername() throws SQLException {
        Clinician clinician = generalClinician.getClinicianFromId(1);
        assertEquals(clinician.getStaffID(), generalClinician.getClinicianIdFromUsername(clinician.getUsername()));
    }

    /**
     * Test getting an ID for an invalid username
     * @throws SQLException
     */
    @Test(expected = Exception.class)
    public void getClinicianIdFromBadUsername() throws SQLException {
        generalClinician.getClinicianIdFromUsername("This is not a real username");
    }


    /**
     * Test getting clinician with id 1
     * @throws SQLException
     */
    @Test
    public void getClinicianFromId() throws SQLException {
        Clinician clinician = generalClinician.getClinicianFromId(1);
        assertEquals(1, clinician.getStaffID());
    }

    /**
     * Test getting clinician with id 9999
     * @throws SQLException
     */
    @Test
    public void getClinicianFromBadId() throws SQLException {
        Clinician clinician = generalClinician.getClinicianFromId(9999);
        assertNull(clinician);
    }

    /**
     * Test the getAllClinicians method
     * @throws SQLException
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
        generalClinician.updateClinicianDetails(clinician, (int) clinician.getStaffID());
        Clinician clinician2 = generalClinician.getClinicianFromId((int) clinician.getStaffID());
        assertEquals(clinician, clinician2);
    }
}