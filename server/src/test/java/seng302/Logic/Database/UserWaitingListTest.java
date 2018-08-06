package seng302.Logic.Database;

import org.junit.Test;
import seng302.HelperMethods;
import seng302.Model.Attribute.Organ;
import seng302.Model.User;
import seng302.Model.WaitingListItem;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class UserWaitingListTest extends GenericTest {

    private GeneralUser generalUser = new GeneralUser();
    private UserWaitingList userWaitingList = new UserWaitingList();

    @Test
    public void getAllWaitingListItems() {
    }

    @Test
    public void getAllUserWaitingListItems() {
    }

    @Test
    public void getWaitingListItemFromId() {
    }

    @Test
    public void insertWaitingListItem() {
    }

    @Test
    public void updateWaitingListItem() {
    }

    @Test
    public void removeWaitingListItem() {
    }

    @Test
    public void removeWaitingListItem1() {
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
    }
}