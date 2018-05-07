package seng302;

import javafx.collections.FXCollections;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import seng302.Generic.DataManager;
import seng302.Generic.ReceiverWaitingListItem;
import seng302.User.Attribute.Organ;
import seng302.User.User;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class UserTest {

    @Before
    public void setup() {
        DataManager.users = FXCollections.observableArrayList();
        DataManager.users.add(new User("Andrew,Neil,Davidson", "01/02/1998", "01/11/4000", "male", 12.1, 50.45, "o+", "Canterbury", "1235 abc Street"));
    }

    @Test
    public void testAddOrgan() {
        User toSet = DataManager.users.get(0);
        toSet.setOrgan(Organ.KIDNEY);
        assertTrue(toSet.getOrgans().contains(Organ.KIDNEY));
    }

    @Test
    public void testRemoveOrgan() {
        User toSet = DataManager.users.get(0);
        toSet.setOrgan(Organ.KIDNEY);
        toSet.removeOrgan(Organ.KIDNEY);
        assertTrue(toSet.getOrgans().isEmpty());
    }

    @Test
    public void testIsDonor_emptyOrganList_returnsFalse() {
        User user = DataManager.users.get(0);
        user.getOrgans().clear();
        assertFalse(user.isDonor());
    }

    @Test
    public void testIsDonor_nonEmptyOrganList_returnsTrue() {
        User user = DataManager.users.get(0);
        user.setOrgan(Organ.LIVER);
        assertTrue(user.isDonor());
    }

    @Test
    public void testIsReceiver_emptyWaitingList_returnsFalse() {
        User user = DataManager.users.get(0);
        user.getWaitingListItems().clear();
        assertFalse(user.isReceiver());
    }

    @Test
    public void testIsReceiver_registeredOrgansInWaitingList_returnsTrue() {
        User user = DataManager.users.get(0);
        ReceiverWaitingListItem newItem = new ReceiverWaitingListItem(Organ.LIVER);
        newItem.registerOrgan();
        user.getWaitingListItems().add(newItem);
        assertTrue(user.isReceiver());
    }

    @Ignore
    @Test
    public void testIsReceiver_noRegisteredOrgansInWaitingList_returnsFalse() {
        User user = DataManager.users.get(0);
        ReceiverWaitingListItem newItem = new ReceiverWaitingListItem(Organ.LIVER);
        newItem.registerOrgan();
        //TODO fix this test
        //newItem.deregisterOrgan();
        user.getWaitingListItems().add(newItem);
        assertFalse(user.isReceiver());
    }


    @After
    public void tearDown() {
        DataManager.users = FXCollections.observableArrayList();
    }
}
