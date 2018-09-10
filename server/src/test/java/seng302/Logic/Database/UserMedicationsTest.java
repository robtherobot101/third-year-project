package seng302.Logic.Database;

import org.junit.Test;
import seng302.HelperMethods;
import seng302.Model.Medication.Medication;
import seng302.Model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UserMedicationsTest extends GenericTest {

    private GeneralUser generalUser = new GeneralUser();
    private UserMedications userMedications = new UserMedications();

    /**
     * Test getting all medications for a user
     * @throws SQLException catch sql execution errors
     */
    @Test
    public void getAllMedications() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);

        ArrayList<Medication> medications1 = HelperMethods.makeMedications();
        ArrayList<Medication> medications2 = HelperMethods.makeMedications();
        for(Medication m : medications2) m.stoppedTaking();
        ArrayList<Medication> allMedications = new ArrayList<>();
        allMedications.addAll(medications1);
        allMedications.addAll(medications2);

        user.setCurrentMedications(medications1);
        user.setHistoricMedications(medications2);

        userMedications.updateAllMedications(allMedications, (int) user.getId());

        assertEquals(allMedications, userMedications.getAllMedications((int) user.getId()));
    }

    /**
     * Test inserting a new medication item into the database
     * @throws SQLException catch sql execution errors
     */
    @Test
    public void insertMedication() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        Medication m = HelperMethods.makeMedications().get(0);
        user.getCurrentMedications().add(m);
        userMedications.insertMedication(m, (int) user.getId());

        assertTrue(userMedications.getAllMedications((int) user.getId()).contains(m));
    }

    /**
     * Test getting a medication for a given id
     * @throws SQLException catch sql execution errors
     */
    @Test
    public void getMedicationFromId() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        Medication m = HelperMethods.makeMedications().get(0);
        user.getCurrentMedications().add(m);
        userMedications.insertMedication(m, (int) user.getId());
        int id = userMedications.getAllMedications((int) user.getId()).get(0).getId();

        assertEquals(m, userMedications.getMedicationFromId(id, (int) user.getId()));
    }

    /**
     * Test trying to get a medication with a made up id
     * @throws SQLException catch sql execution errors
     */
    @Test
    public void badDiseaseId() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        assertNull(userMedications.getMedicationFromId(999999, (int) user.getId()));
    }

    /**
     * Test updating the attributes of a medication
     * @throws SQLException catch sql execution errors
     */
    @Test
    public void updateMedication() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        Medication m = HelperMethods.makeMedications().get(0);
        userMedications.insertMedication(m, (int) user.getId());
        int id = userMedications.getAllMedications((int) user.getId()).get(0).getId();

        String[] ingredients = {"A", "New", "set", "of", "ingredients"};
        m.setActiveIngredients(ingredients);
        m.setName("A different name");
        userMedications.updateMedication(m, id, (int) user.getId());

        assertEquals(m, userMedications.getMedicationFromId(id, (int) user.getId()));
    }

    /**
     * Test the removal of a medication item from the database
     * @throws SQLException catch sql execution errors
     */
    @Test
    public void removeMedication() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        user.setCurrentMedications(HelperMethods.makeMedications());
        userMedications.updateAllMedications(user.getCurrentMedications(), (int) user.getId());
        user.setCurrentMedications(userMedications.getAllMedications((int) user.getId()));
        Medication removed = user.getCurrentMedications().remove(0);
        userMedications.removeMedication((int) user.getId(), removed.getId());
        assertFalse(userMedications.getAllMedications((int) user.getId()).contains(removed));
    }

    @Test
    public void updateAllMedications() throws SQLException {
        String[] ingredients = {"water", "Air"};
        User user = HelperMethods.insertUser(generalUser);
        List<Medication> medications = HelperMethods.makeMedications();
        userMedications.updateAllMedications(medications, (int) user.getId());
        assertEquals(medications, generalUser.getUserFromId((int) user.getId()).getCurrentMedications());
    }
}