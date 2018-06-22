package seng302;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import seng302.Generic.History;
import seng302.Generic.IO;
import seng302.Generic.WindowManager;

import java.io.File;
import java.net.URISyntaxException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class HistoryTest {

    private static String jarpath;

    @Before
    public void setup() throws URISyntaxException {
        jarpath = new File(WindowManager.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getAbsolutePath();
        IO.setJarPath(jarpath);
    }


    @Test
    public void initTest() {
        History.init();
        File actionHistory = new File(jarpath + File.separatorChar + "actionHistory.txt");
        assertTrue(actionHistory.isFile());

    }

    @Test
    public void prepareFileStringTest() {
        String[] testCommand = new String[]{"listusers"};
        String result = History.prepareFileStringCLI(testCommand);

        assertEquals(result.substring(28), "listusers [-Admin- Listed all users.]");
    }

    @After
    public void tearDown() {
        IO.setJarPath(null);
    }
}
