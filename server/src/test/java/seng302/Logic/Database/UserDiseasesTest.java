package seng302.Logic.Database;

import org.junit.Test;
import seng302.HelperMethods;
import seng302.Model.Disease;
import seng302.Model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class UserDiseasesTest extends GenericTest {

    private UserDiseases userDiseases = new UserDiseases();
    private GeneralUser generalUser = new GeneralUser();

    /**
     * Create a user with cured, chrnoic and existing diseases and test they are all returned when requested
     * @throws SQLException catch sql execution errors
     */
    @Test
    public void getAllDiseases() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);

        ArrayList<Disease> diseases1 = HelperMethods.makeDiseases(true);
        ArrayList<Disease> diseases2 = HelperMethods.makeDiseases(false);
        ArrayList<Disease> allDiseases = new ArrayList<>();
        allDiseases.addAll(diseases1);
        allDiseases.addAll(diseases2);

        user.setCuredDiseases(diseases1);
        user.getCurrentDiseases().addAll(diseases2);

        userDiseases.updateAllDiseases(allDiseases, (int) user.getId());

        assertEquals(allDiseases, userDiseases.getAllDiseases((int) user.getId()));

    }

    /**
     * Test a single disease is inserted correctly
     * @throws SQLException catch sql execution errors
     */
    @Test
    public void insertDisease() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        Disease d = new Disease("An horrific disease", LocalDate.of(2005, 12, 7), false, true, 1);
        user.getCuredDiseases().add(d);
        userDiseases.insertDisease(d, (int) user.getId());

        assertTrue(userDiseases.getAllDiseases((int) user.getId()).contains(d));
    }

    /**
     * Test getting a disease by specifying id
     * @throws SQLException catch sql execution errors
     */
    @Test
    public void getDiseaseFromId() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        Disease d = new Disease("Another horrific disease", LocalDate.of(1997, 12, 7), true, false, 1);
        user.getCurrentDiseases().add(d);
        userDiseases.insertDisease(d, (int) user.getId());
        int id = userDiseases.getAllDiseases((int) user.getId()).get(0).getId();

        assertEquals(d, userDiseases.getDiseaseFromId(id, (int) user.getId()));
    }

    /**
     * Test trying to get a disease with a made up id
     * @throws SQLException catch sql execution errors
     */
    @Test
    public void badDiseaseId() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        assertNull(userDiseases.getDiseaseFromId(999999, (int) user.getId()));
    }

    /**
     * Test updating an existing disease to a cured disease
     * @throws SQLException catch sql execution errors
     */
    @Test
    public void updateDisease() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        Disease d = new Disease("Yet Another horrific disease", LocalDate.of(1988, 12, 17), false, false, 1);
        userDiseases.insertDisease(d, (int) user.getId());
        int id = userDiseases.getAllDiseases((int) user.getId()).get(0).getId();

        d.setCured(true);
        userDiseases.updateDisease(d, id, (int) user.getId());

        assertEquals(d, userDiseases.getDiseaseFromId(id, (int) user.getId()));
    }

    /**
     * Test the removal of a disease
     * @throws SQLException catch sql execution errors
     */
    @Test
    public void removeDisease() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        user.setCuredDiseases(HelperMethods.makeDiseases(true));
        userDiseases.updateAllDiseases(user.getCuredDiseases(), (int) user.getId());
        user.setCuredDiseases(userDiseases.getAllDiseases((int) user.getId()));
        Disease removed = user.getCuredDiseases().remove(0);
        userDiseases.removeDisease((int) user.getId(), removed.getId());
        assertFalse(userDiseases.getAllDiseases((int) user.getId()).contains(removed));
    }

    @Test
    public void updateAllDiseases() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);

        ArrayList<Disease> diseases = new ArrayList<>();
        diseases.add(new Disease("Bronchitis", LocalDate.now(), false, false, 1));
        userDiseases.updateAllDiseases(diseases, (int) user.getId());

        User user2 = generalUser.getUserFromId((int) user.getId());
        assertEquals(diseases, user2.getCurrentDiseases());

    }
}