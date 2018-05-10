package seng302.TUI;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import seng302.Generic.DataManager;
import seng302.Generic.IO;
import seng302.Generic.WindowManager;

import javax.print.URIException;
import javax.xml.crypto.Data;
import java.io.*;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class CommandLineInterfaceTest {
    ByteArrayOutputStream outputStream;
    CommandLineInterface commandLine;

    @Before
    public void setUp() throws URISyntaxException {
//        String jarPath = new File(WindowManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();
//        IO.setJarPath(jarPath);
//
//        outputStream = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outputStream));
//        System.err.println("setOut done");
         commandLine = new CommandLineInterface();
//        commandLineInterface.run(System.in);
//        System.err.println("run loop done");
    }

    private String readFromCommandLine() {
        return outputStream.toString();
    }

    @Test
    public void creationUser() {
        System.err.println("set up done");
        String[] command;
        String input = "addUser \"Bob Ross\" 10/10/2010";
        command = commandLine.splitByQuotationThenSpace(input);

        commandLine.parseCommand(command);
        assertFalse(DataManager.users.isEmpty());
    }

    @Test
    public void creationUserInvalidParameters() {
        commandLine.parseCommand("addUser bobbo 111111111");
        assertTrue(DataManager.users.isEmpty());
    }

    @Test
    public void addWaitingListOrgan() {
        commandLine.parseCommand("addUser \"Bob Ross\" 10/10/2010");
        commandLine.parseCommand("addWaitingListOrgan 0 heart");
        assertTrue(DataManager.users.get(0).isReceiver());
    }

    @Test
    public void removeWaitingListOrgan() {
        commandLine.parseCommand("addUser \"Bob Ross\" 10/10/2010");
        commandLine.parseCommand("addWaitingListOrgan 0 heart");
        commandLine.parseCommand("removeWaitingListOrgan 0 heart");
        assertFalse(DataManager.users.get(0).isReceiver());
    }

    @Test
    public void deleteUser() {
        commandLine.parseCommand("addUser \"Bob Ross\" 10/10/2010");
        commandLine.parseCommand("removeUser 0");
        assertTrue(DataManager.users.isEmpty());
    }

    @Test
    public void helpText() {
        commandLine.parseCommand("help addWaitingListOrgan");
        String result = readFromCommandLine();
        assertEquals(result, "lol");
    }



}