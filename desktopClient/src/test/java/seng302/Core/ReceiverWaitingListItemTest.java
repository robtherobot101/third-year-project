package seng302.Core;


import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seng302.User.ReceiverWaitingListItem;
import seng302.User.Attribute.Organ;
import seng302.User.User;


public class ReceiverWaitingListItemTest {

    private User testUser;
    private ReceiverWaitingListItem item;
    private Organ heart;

    @Before
    public void setUp() {
        testUser = new User("Joe", LocalDate.parse("01/01/1999", User.dateFormat));
        heart = Organ.HEART;
        item = new ReceiverWaitingListItem(heart, Long.parseLong("-1"));
        testUser.getWaitingListItems().add(item);
    }

    @Test
    public void testNullDeregisteredDateOnRegister() {
        LocalDate date = null;
        item.deregisterOrgan(3);
        item.registerOrgan();
        for (ReceiverWaitingListItem listItem : testUser.getWaitingListItems()) {
            date = listItem.getOrganDeregisteredDate();
        }
        Assert.assertNull(date);
    }

    @Test
    public void testIsStillWaitingOnRegister() {
        boolean stillWaitingOn = false;
        item.deregisterOrgan(3);
        item.registerOrgan();
        for (ReceiverWaitingListItem listItem : testUser.getWaitingListItems()) {
            stillWaitingOn = listItem.getStillWaitingOn();
        }
        Assert.assertTrue(stillWaitingOn);
    }

    @Test
    public void testIsNotStillWaitingOnDeregister() {
        boolean stillWaitingOn = true;
        item.deregisterOrgan(3);
        for (ReceiverWaitingListItem listItem : testUser.getWaitingListItems()) {
            stillWaitingOn = listItem.getStillWaitingOn();
        }
        Assert.assertFalse(stillWaitingOn);
    }

    @Test
    public void isDonatingOrgan() {
        testUser.setOrgan(Organ.HEART);
        ReceiverWaitingListItem listItem = testUser.getWaitingListItems().get(0);
        System.out.println(listItem.getOrganType());
        Assert.assertTrue(listItem.isDonatingOrgan(testUser));
    }
}