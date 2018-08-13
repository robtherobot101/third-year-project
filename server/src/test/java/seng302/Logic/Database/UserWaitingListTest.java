package seng302.Logic.Database;

import org.junit.Test;
import seng302.HelperMethods;
import seng302.Model.Attribute.Organ;
import seng302.Model.User;
import seng302.Model.WaitingListItem;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UserWaitingListTest extends GenericTest {

    private GeneralUser generalUser = new GeneralUser();
    private UserWaitingList userWaitingList = new UserWaitingList();

    @Test
    public void getAllWaitingListItems() throws SQLException {
        User user1 = HelperMethods.insertUser(generalUser);
        user1.getWaitingListItems().addAll(HelperMethods.makeWaitingListItems((int)user1.getId(), 1));
        generalUser.patchEntireUser(user1, (int)user1.getId(), true);

        List<WaitingListItem> all = user1.getWaitingListItems();
        assertEquals(all, userWaitingList.getAllUserWaitingListItems((int) user1.getId()));
    }/*

    @Test
    public void getAllUserWaitingListItems() throws SQLException {
        User user1 = HelperMethods.insertUser(generalUser);
        user1.getWaitingListItems().addAll(HelperMethods.makeWaitingListItems((int)user1.getId(), 1));
        generalUser.patchEntireUser(user1, (int)user1.getId(), true);

        User user2 = HelperMethods.insertUser(generalUser);
        user2.getWaitingListItems().addAll(HelperMethods.makeWaitingListItems((int)user2.getId(), 4));
        generalUser.patchEntireUser(user2, (int)user2.getId(), true);

        List<WaitingListItem> all = user1.getWaitingListItems();
        all.addAll(user2.getWaitingListItems());
        assertEquals(all, userWaitingList.getAllWaitingListItems());
    }

    @Test
    public void getWaitingListItemFromId() throws SQLException {
        User user1 = HelperMethods.insertUser(generalUser);
        WaitingListItem test = HelperMethods.makeWaitingListItems((int)user1.getId(), 1).get(0);
        userWaitingList.insertWaitingListItem(test, (int)user1.getId());
        assertEquals(test, userWaitingList.getWaitingListItemFromId(test.getId(), (int)user1.getId()));
    }

    @Test
    public void insertWaitingListItem() throws SQLException {
        User user1 = HelperMethods.insertUser(generalUser);
        WaitingListItem test = HelperMethods.makeWaitingListItems((int)user1.getId(), 1).get(0);
        userWaitingList.insertWaitingListItem(test, (int)user1.getId());
        assertEquals(test, generalUser.getUserFromId((int)user1.getId()).getWaitingListItems().get(0));
    }

    @Test
    public void updateWaitingListItem() throws SQLException {
        User user1 = HelperMethods.insertUser(generalUser);
        WaitingListItem test = HelperMethods.makeWaitingListItems((int)user1.getId(), 1).get(0);
        WaitingListItem test2 = HelperMethods.makeWaitingListItems((int)user1.getId(), 0).get(1);
        userWaitingList.insertWaitingListItem(test, (int)user1.getId());
        userWaitingList.updateWaitingListItem(test2, test.getId(), (int)user1.getId());
        assertEquals(test2, userWaitingList.getWaitingListItemFromId(test.getId(), (int)user1.getId()));
    }

    @Test
    public void removeWaitingListItem() throws SQLException {
        User user1 = HelperMethods.insertUser(generalUser);
        WaitingListItem test = HelperMethods.makeWaitingListItems((int)user1.getId(), 1).get(0);
        userWaitingList.insertWaitingListItem(test, (int)user1.getId());
        userWaitingList.removeWaitingListItem((int)user1.getId(), test.getId());
        assertEquals(0, userWaitingList.getAllWaitingListItems().size());
    }

    @Test
    public void updateWaitingListItems() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);

        ArrayList<WaitingListItem> waitingListItems = new ArrayList<>();
        WaitingListItem waitingListItem = new WaitingListItem(Organ.HEART, LocalDate.ofYearDay(2005, 100), 1, (int) user.getId(), null, 0);
        waitingListItems.add(waitingListItem);

        userWaitingList.updateAllWaitingListItems(waitingListItems, (int) user.getId());

        User user2 = generalUser.getUserFromId((int) user.getId());
        assertEquals(waitingListItems, user2.getWaitingListItems());
    }*/
}