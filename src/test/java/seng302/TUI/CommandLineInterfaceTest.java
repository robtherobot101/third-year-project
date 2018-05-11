package seng302.TUI;

import org.junit.Before;
import org.junit.Test;
import seng302.GUI.CommandLineInterface;
import seng302.Generic.DataManager;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

//    @Test
//    public void deleteUser() {
//        commandLine.readCommand("addUser \"Bob Ross\" 10/10/2010");
//        commandLine.readCommand("removeUser 0");
//        assertTrue(DataManager.users.isEmpty());
//    }
}