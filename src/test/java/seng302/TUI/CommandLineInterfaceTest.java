package seng302.TUI;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import seng302.GUI.CommandLineInterface;
import seng302.Generic.DataManager;
import seng302.Generic.IO;
import seng302.User.Clinician;

import java.net.URISyntaxException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class CommandLineInterfaceTest {
    private CommandLineInterface commandLine;
    private List<String> output;

    @Before
    public void setUp() {
        commandLine = new CommandLineInterface();
        output = new ArrayList<>();
        commandLine.setOutput(output);
        IO.streamOut = new PrintStream(System.out);
        DataManager.users.clear();
    }

    @Test
    public void creationUser() {
        commandLine.readCommand("addUser \"Bob Ross\" 10/10/2010");
        assertFalse(DataManager.users.isEmpty());
    }

    @Test
    public void creationUserInvalidDOB() {
        commandLine.readCommand("addUser bobbo 111111111");
        assertTrue(DataManager.users.isEmpty());
    }

    @Test
    public void addWaitingListOrgan() {
        commandLine.readCommand("addUser \"Bob Ross\" 10/10/2010");
        commandLine.readCommand("addWaitingListOrgan 0 heart");
        assertTrue(DataManager.users.get(0).isReceiver());
    }

    @Test
    public void removeWaitingListOrgan() {
        commandLine.readCommand("addUser \"Bob Ross\" 10/10/2010");
        commandLine.readCommand("addWaitingListOrgan 0 heart");
        commandLine.readCommand("removeWaitingListOrgan 0 heart");
        assertFalse(DataManager.users.get(0).isReceiver());
    }
    
    @Test
    public void addDonationOrgan() {
        commandLine.readCommand("addUser \"Bob Ross\" 10/10/2010");
        commandLine.readCommand("addDonationOrgan 0 heart");
        assertTrue(DataManager.users.get(0).isDonor());
    }

    @Test
    public void removeDonationOrgan() {
        commandLine.readCommand("addUser \"Bob Ross\" 10/10/2010");
        commandLine.readCommand("addDonationOrgan 0 heart");
        commandLine.readCommand("removeDonationOrgan 0 heart");
        assertFalse(DataManager.users.get(0).isDonor());
    }

    @Test
    public void addClinician(){
        int numberOfClinicians = DataManager.clinicians.size();
        commandLine.readCommand("addClinician \"bobbr45\" \"paint\" \"Bob Ross\"");
        assertEquals(numberOfClinicians+1,DataManager.clinicians.size());
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

    @Test
    public void cannotDeleteDefaultClinician(){
        int numberOfClinicians = DataManager.clinicians.size();
        commandLine.readCommand("deleteClinician "+0);
        commandLine.readCommand("y");
        assertEquals(numberOfClinicians,DataManager.clinicians.size());
    }


    @Test
    public void deleteUser() {
        commandLine.readCommand("addUser \"Bob Ross\" 10/10/2010");
        commandLine.readCommand("deleteUser 0");
        commandLine.readCommand("y");
        assertTrue(DataManager.users.isEmpty());
    }
}