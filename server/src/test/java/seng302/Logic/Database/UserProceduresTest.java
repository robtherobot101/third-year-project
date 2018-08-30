package seng302.Logic.Database;

import org.junit.Test;
import seng302.HelperMethods;
import seng302.Model.Attribute.Organ;
import seng302.Model.Procedure;
import seng302.Model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class UserProceduresTest extends GenericTest {
    
    private GeneralUser generalUser = new GeneralUser();
    private UserProcedures userProcedures = new UserProcedures();

    /**
     * Test inserting a new procedure item into the database
     * @throws SQLException
     */
    @Test
    public void insertProcedure() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        Procedure p = HelperMethods.makeProcedures().get(0);

        userProcedures.insertProcedure(p, (int) user.getId());

        assertTrue(userProcedures.getAllProcedures((int) user.getId()).contains(p));
    }

    /**
     * Test getting a procedure for a given id
     * @throws SQLException
     */
    @Test
    public void getProcedureFromId() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        Procedure p = HelperMethods.makeProcedures().get(0);

        userProcedures.insertProcedure(p, (int) user.getId());
        int id = userProcedures.getAllProcedures((int) user.getId()).get(0).getId();

        assertEquals(p, userProcedures.getProcedureFromId(id, (int) user.getId()));
    }

    /**
     * Test trying to get a procedure with a made up id
     * @throws SQLException
     */
    @Test
    public void badDiseaseId() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        assertNull(userProcedures.getProcedureFromId(999999, (int) user.getId()));
    }

    /**
     * Test updating the attributes of a procedure
     * @throws SQLException
     */
    @Test
    public void updateProcedure() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        Procedure p = HelperMethods.makeProcedures().get(0);
        userProcedures.insertProcedure(p, (int) user.getId());
        int id = userProcedures.getAllProcedures((int) user.getId()).get(0).getId();

        p.setDate(LocalDate.of(1956, 7, 18));
        p.setDescription("A new description");
        p.setOrgansAffected(new ArrayList<>(Arrays.asList(Organ.LIVER, Organ.BONE, Organ.TISSUE)));
        p.setSummary("A new summary");
        userProcedures.updateProcedure(p, id, (int) user.getId());

        assertEquals(p, userProcedures.getProcedureFromId(id, (int) user.getId()));
    }

    /**
     * Test the removal of a procedure item from the database
     * @throws SQLException
     */
    @Test
    public void removeProcedure() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        user.setPreviousProcedures(HelperMethods.makeProcedures());
        userProcedures.updateAllProcedures(user.getPreviousProcedures(), (int) user.getId());
        user.setPreviousProcedures(userProcedures.getAllProcedures((int) user.getId()));
        Procedure removed = user.getPreviousProcedures().remove(0);
        userProcedures.removeProcedure((int) user.getId(), removed.getId());
        assertFalse(userProcedures.getAllProcedures((int) user.getId()).contains(removed));
    }

    /**
     * Test getting all procedures for a user
     * @throws SQLException
     */
    @Test
    public void getAllProcedures() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);

        ArrayList<Procedure> procedures = HelperMethods.makeProcedures();
        for(Procedure p : procedures) {
            if(p.getDate().isBefore(LocalDate.now())) user.getPreviousProcedures().add(p);
            else user.getPendingProcedures().add(p);
        }

        userProcedures.updateAllProcedures(procedures, (int) user.getId());

        assertEquals(procedures, userProcedures.getAllProcedures((int) user.getId()));
    }

    @Test
    public void updateAllProcedures() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        ArrayList<Organ> organs = new ArrayList<Organ>();
        organs.add(Organ.LUNG);
        ArrayList<Procedure> procedures = new ArrayList<>();
        procedures.add(new Procedure("Trachiotomy", "Removed oesophagus entirely", LocalDate.now(), organs, 1));
        userProcedures.updateAllProcedures(procedures, (int) user.getId());
        User user2 = generalUser.getUserFromId((int) user.getId());
        assertEquals(procedures, user2.getPreviousProcedures());
    }
}