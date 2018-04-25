package seng302;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import seng302.Core.Main;
import seng302.Files.History;

import java.io.File;
import java.net.URISyntaxException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class HistoryTest {
    private static String jarpath;

    @Before
    public void setup() throws URISyntaxException {
        jarpath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();
        Main.setJarPath(jarpath);
    }


    @Test
    public void initTest() {
        History.init();
        File actionHistory = new File(jarpath + File.separatorChar + "actionHistory.txt");
        assertTrue(actionHistory.isFile());

    }

    @Test
    public void prepareFileStringTest() {
        String[] testCommand = new String[]{"list"};
        String result = History.prepareFileStringCLI(testCommand);
        assertEquals(result.substring(25), "list null null null [Listed all users.]");
    }

    @After
    public void tearDown() {
        Main.setJarPath(null);
    }
}
