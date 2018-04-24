package seng302;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import seng302.Core.User;
import seng302.Core.Main;
import seng302.Core.Organ;

import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;

public class UserTest {

    @Before
    public void setup() {
        Main.users = new ArrayList<>();
        Main.users.add(new User("Andrew,Neil,Davidson", "01/02/1998", "01/11/4000", "male", 12.1, 50.45, "o+", "Canterbury", "1235 abc Street"));
    }

    @Test
    public void testAddOrgan() {
        User toSet = Main.users.get(0);
        toSet.setOrgan(Organ.KIDNEY);
        assertTrue(toSet.getOrgans().contains(Organ.KIDNEY));
    }

    @Test
    public void testRemoveOrgan() {
        User toSet = Main.users.get(0);
        toSet.setOrgan(Organ.KIDNEY);
        toSet.removeOrgan(Organ.KIDNEY);
        assertTrue(toSet.getOrgans().isEmpty());
    }

    @After
    public void tearDown() {
        Main.users = new ArrayList<>();
    }
}
