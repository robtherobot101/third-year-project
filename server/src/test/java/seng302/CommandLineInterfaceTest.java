package seng302;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import seng302.Logic.CommandLineInterface;
import seng302.Logic.Database.GeneralClinician;
import seng302.Logic.Database.GeneralCountriesTest;
import seng302.Logic.Database.GeneralUser;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CommandLineInterfaceTest {
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
        commandLine.readCommand("addUser bob_ross1234 ross123 \"Bob Ross\" 10/10/2010");
        assertNotEquals(beforeSize, generalUser.getUsers(new HashMap<>()).size());
    }

    @Test
    public void creationUserInvalidDOB() throws SQLException {
        int beforeSize = generalUser.getUsers(new HashMap<>()).size();
        commandLine.readCommand("addUser test1234 123p123 bobbo 111111111");
        assertEquals(beforeSize, generalUser.getUsers(new HashMap<>()).size());
    }

    @Test
    public void addWaitingListOrgan() throws SQLException {
        commandLine.readCommand("addUser tester2 testtest \"Bobby Ross\" 10/10/2010");
        Map<String, String> params = new HashMap<>();
        params.put("password", "'testtest'");
        int id = (int)generalUser.getUsers(params).get(0).getId();
        commandLine.readCommand("addWaitingListOrgan " + id + " heart");
        assertTrue(generalUser.getUserFromId(id).getWaitingListItems().size() > 0);
    }

    @Test
    public void addDonationOrgan() throws SQLException {
        commandLine.readCommand("addUser tester23 asdf1234 \"Bobby Ross\" 10/10/2010");
        Map<String, String> params = new HashMap<>();
        params.put("password", "'asdf1234'");
        int id = (int)generalUser.getUsers(params).get(0).getId();
        commandLine.readCommand("addDonationOrgan " + id + " heart");
        assertTrue(generalUser.getUserFromId(id).isDonor());
    }

    @Test
    public void removeDonationOrgan() throws SQLException {
        commandLine.readCommand("addUser mnbvvc zxcvb \"Bobbyb Ross\" 10/10/2010");
        Map<String, String> params = new HashMap<>();
        params.put("password", "'zxcvb'");
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

    @Ignore
    @Test
    public void cannotDeleteDefaultClinician() throws SQLException {
        int numberOfClinicians = generalClinician.getAllClinicians().size();
        commandLine.readCommand("deleteClinician "+0);
        commandLine.readCommand("y");
        assertEquals(numberOfClinicians, generalClinician.getAllClinicians().size());
    }


    @Test
    public void deleteUser() throws SQLException {
        int beforeSize = generalUser.getUsers(new HashMap<>()).size();
        commandLine.readCommand("addUser \"Bob Ross\" 10/10/2010");
        commandLine.readCommand("deleteUser 0");
        commandLine.readCommand("y");
        assertEquals(beforeSize, generalUser.getUsers(new HashMap<>()).size());
    }

/*
    @Ignore
    @Test
    public void removeWaitingListOrgan() {
        commandLine.readCommand("addUser \"Bob Ross\" 10/10/2010");
        commandLine.readCommand("addWaitingListOrgan 0 heart");
        commandLine.readCommand("removeWaitingListOrgan 0 heart");
        assertFalse(DataManager.users.get(0).isReceiver());
    }

    @Test
    public void addClinicianBadArguments(){
        int numberOfClinicians = DataManager.clinicians.size();
        commandLine.readCommand("addClinician \"bobbr45\" \"paint\" \"Bob Ross\" \"fourth arg\"");
        assertEquals(numberOfClinicians,DataManager.clinicians.size());
    }

    @Test
    public void updateUser(){
        int numberOfUsers = DataManager.users.size();
        commandLine.readCommand("addUser \"Bob Ross\" 10/10/2010");
        commandLine.readCommand("updateUser "+numberOfUsers+" \"region\" \"Chch\"");
        assertEquals("Chch",DataManager.users.get(numberOfUsers).getRegion());
    }

    @Test
    public void updateClinician(){
        int numberOfClinicians = DataManager.clinicians.size();
        commandLine.readCommand("addClinician \"bobbr45\" \"paint\" \"Bob Ross\"");
        commandLine.readCommand("updateClinician "+numberOfClinicians+" \"region\" \"Chch\"");
        assertEquals("Chch",DataManager.clinicians.get(numberOfClinicians).getRegion());
    }

    @Test
    public void deleteClinician(){
        int numberOfClinicians = DataManager.clinicians.size();
        commandLine.readCommand("addClinician \"bobbr45\" \"paint\" \"Bob Ross\"");
        commandLine.readCommand("deleteClinician " + numberOfClinicians);
        commandLine.readCommand("y");
        assertEquals(numberOfClinicians,DataManager.clinicians.size());
    }
*/
}