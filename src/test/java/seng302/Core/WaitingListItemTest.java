package seng302.Core;


import java.time.LocalDate;
import org.junit.Before;
import seng302.Generic.ReceiverWaitingListItem;
import seng302.User.Attribute.Organ;
import seng302.User.User;

public class WaitingListItemTest {

    private User testUser;
    private ReceiverWaitingListItem item;
    private Organ heart;

    @Before
    public void setUp() {
        testUser = new User("Joe", LocalDate.parse("01/01/1999", User.dateFormat));
        heart = Organ.HEART;
        item = new ReceiverWaitingListItem(heart);
        testUser.getWaitingListItems().add(item);
    }
    /*
    @Test
    public void testNullDeregisteredDateOnRegister() {
        String date = "notNull";
        item.deregisterOrgan();
        item.registerOrgan();
        for (WaitingListItem listItem : testUser.getWaitingListItems()) {
            date = listItem.getOrganDeregisteredDate();
        }
        assertTrue(date == null);
    }

    @Test
    public void testIsStillWaitingOnRegister() {
        boolean stillWaitingOn = false;
        item.deregisterOrgan();
        item.registerOrgan();
        for (WaitingListItem listItem : testUser.getWaitingListItems()) {
            stillWaitingOn = listItem.getStillWaitingOn();
        }
        assertTrue(stillWaitingOn);
    }

    @Test
    public void testIsNotStillWaitingOnDeregister() {
        boolean stillWaitingOn = true;
        item.deregisterOrgan();
        for (WaitingListItem listItem : testUser.getWaitingListItems()) {
            stillWaitingOn = listItem.getStillWaitingOn();
        }
        assertFalse(stillWaitingOn);
    }

    @Test
    public void isDonatingOrgan() {
        testUser.setOrgan(Organ.HEART);
        WaitingListItem listItem = testUser.getWaitingListItems().get(0);
        System.out.println(listItem.getOrganType());
        assertTrue(listItem.isDonatingOrgan(testUser));
    }
    */
}