package seng302;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import seng302.Logic.CommandLineInterface;
import seng302.Logic.Database.GeneralClinician;
import seng302.Logic.Database.GeneralCountriesTest;
import seng302.Logic.Database.GeneralUser;
import seng302.Logic.Database.GenericTest;
import seng302.Logic.SaltHash;
import sun.net.www.content.text.Generic;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CommandLineInterfaceTest extends GenericTest {
    private CommandLineInterface commandLine;
    private GeneralUser generalUser = new GeneralUser();
    private GeneralClinician generalClinician = new GeneralClinician();

    @Before
    public void setUp() {
        commandLine = new CommandLineInterface();
    }

    @Test
    public void creationUser() throws SQLException {
        int beforeSize = generalUser.getUsers(new HashMap<>()).size();
        commandLine.readCommand("addUser bob_ross1234 cgh5173 ross123 \"Bob Ross\" 10/10/2010");
        assertNotEquals(beforeSize, generalUser.getUsers(new HashMap<>()).size());
    }

    @Test
    public void creationUserInvalidDOB() throws SQLException {
        int beforeSize = generalUser.getUsers(new HashMap<>()).size();
        commandLine.readCommand("addUser test1234 ckt2749 123p123 bobbo 111111111");
        assertEquals(beforeSize, generalUser.getUsers(new HashMap<>()).size());
    }

    @Test
    public void addWaitingListOrgan() throws SQLException {
        commandLine.readCommand("addUser tester2 cga2314 testtest \"Bobby Ross\" 10/10/2010");
        Map<String, String> params = new HashMap<>();
        params.put("username", "'tester2'");
        int id = (int)generalUser.getUsers(params).get(0).getId();
        commandLine.readCommand("addWaitingListOrgan " + id + " heart");
        assertTrue(generalUser.getUserFromId(id).getWaitingListItems().size() > 0);
    }

    @Test
    public void addDonationOrgan() throws SQLException {
        commandLine.readCommand("addUser tester23 cft5432 asdf1234 \"Bobby Ross\" 10/10/2010");
        Map<String, String> params = new HashMap<>();
        params.put("username", "'tester23'");
        int id = (int)generalUser.getUsers(params).get(0).getId();
        commandLine.readCommand("addDonationOrgan " + id + " heart");
        assertTrue(generalUser.getUserFromId(id).isDonor());
    }

    @Test
    public void removeDonationOrgan() throws SQLException {
        System.out.println(commandLine.readCommand("addUser mnbvvc cbf5678 zxcvb \"Bobbyb Ross\" 10/10/2010").getResponse());
        Map<String, String> params = new HashMap<>();
        params.put("username", "'mnbvvc'");
        int id = (int)generalUser.getUsers(params).get(0).getId();
        commandLine.readCommand("addDonationOrgan " + id + " heart");
        commandLine.readCommand("removeDonationOrgan " + id + " heart");
        assertFalse(generalUser.getUserFromId(id).isDonor());
    }

    @Test
    public void addClinician() throws SQLException {
        int numberOfClinicians = generalClinician.getAllClinicians().size();
        commandLine.readCommand("addClinician \"bobbr451234\" \"p41nt\" \"Bob Ross\"");
        assertEquals(numberOfClinicians+1, generalClinician.getAllClinicians().size());
    }

    @Test
    public void deleteUser() throws SQLException {
        int beforeSize = generalUser.getUsers(new HashMap<>()).size();
        commandLine.readCommand("addUser \"Bob Ross\" 10/10/2010");
        commandLine.readCommand("deleteUser 0");
        commandLine.readCommand("y");
        assertEquals(beforeSize, generalUser.getUsers(new HashMap<>()).size());
    }
}