package seng302;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import seng302.Generic.WaitingListItem;
import seng302.User.User;
import seng302.Generic.Main;
import seng302.User.Attribute.Organ;

import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

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

    @Test
    public void testIsDonor_emptyOrganList_returnsFalse() {
        User user = Main.users.get(0);
        user.getOrgans().clear();
        assertFalse(user.isDonor());
    }

    @Test
    public void testIsDonor_nonEmptyOrganList_returnsTrue() {
        User user = Main.users.get(0);
        user.setOrgan(Organ.LIVER);
        assertTrue(user.isDonor());
    }

    @Test
    public void testIsReceiver_emptyWaitingList_returnsFalse() {
        User user = Main.users.get(0);
        user.getWaitingListItems().clear();
        assertFalse(user.isReceiver());
    }

    @Test
    public void testIsReceiver_registeredOrgansInWaitingList_returnsTrue() {
        User user = Main.users.get(0);
        WaitingListItem newItem = new WaitingListItem(Organ.LIVER);
        newItem.registerOrgan();
        user.getWaitingListItems().add(newItem);
        assertTrue(user.isReceiver());
    }

    @Test
    public void testIsReceiver_noRegisteredOrgansInWaitingList_returnsFalse() {
        User user = Main.users.get(0);
        WaitingListItem newItem = new WaitingListItem(Organ.LIVER);
        newItem.registerOrgan();
        newItem.deregisterOrgan();
        user.getWaitingListItems().add(newItem);
        assertFalse(user.isReceiver());
    }



    @After
    public void tearDown() {
        Main.users = new ArrayList<>();
    }
}
