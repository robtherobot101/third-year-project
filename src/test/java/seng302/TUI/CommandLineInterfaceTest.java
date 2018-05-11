package seng302.TUI;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import seng302.GUI.CommandLineInterface;
import seng302.GUI.Controllers.UserWindowController;
import seng302.Generic.DataManager;
import seng302.Generic.IO;
import seng302.Generic.WindowManager;

import javax.print.URIException;
import javax.xml.crypto.Data;
import java.io.*;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class CommandLineInterfaceTest {
    private CommandLineInterface commandLine;

    @Before
    public void setUp() throws URISyntaxException {
         commandLine = new CommandLineInterface();
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
        commandLine.readCommand("deleteClinician "+numberOfClinicians);
        commandLine.readCommand("y");
        assertEquals(numberOfClinicians,DataManager.clinicians.size());
    }

//    @Test
//    public void deleteUser() {
//        commandLine.readCommand("addUser \"Bob Ross\" 10/10/2010");
//        commandLine.readCommand("removeUser 0");
//        assertTrue(DataManager.users.isEmpty());
//    }
}