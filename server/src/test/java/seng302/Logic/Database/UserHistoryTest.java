package seng302.Logic.Database;

import org.junit.Test;
import seng302.HelperMethods;
import seng302.Model.HistoryItem;
import seng302.Model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.chrono.HijrahEra;

import static org.junit.Assert.*;

public class UserHistoryTest extends GenericTest {

    private UserHistory userHistory = new UserHistory();
    private GeneralUser generalUser = new GeneralUser();

    @Test
    public void getAllHistoryItems() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        user.getUserHistory().clear();
        user.getUserHistory().addAll(HelperMethods.makeHistory());
        generalUser.patchEntireUser(user, (int) user.getId());
        assertEquals(userHistory.getAllHistoryItems((int) user.getId()), user.getUserHistory());
    }

    @Test
    public void insertHistoryItem() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        HistoryItem h = new HistoryItem(LocalDateTime.of(1997, 8, 4, 23, 55), "An action", "A description", 1);
        userHistory.insertHistoryItem(h, (int) user.getId());
        assertTrue(userHistory.getAllHistoryItems((int) user.getId()).contains(h));
    }

    @Test
    public void removeHistoryItem() throws SQLException {
        User user = HelperMethods.insertUser(generalUser);
        user.getUserHistory().clear();
        user.getUserHistory().addAll(HelperMethods.makeHistory());
        generalUser.patchEntireUser(user, (int) user.getId());
        HistoryItem removed = userHistory.getAllHistoryItems((int) user.getId()).remove(5);
        userHistory.removeHistoryItem((int) user.getId(), removed.getId());
        assertFalse(userHistory.getAllHistoryItems((int) user.getId()).contains(removed));
    }
}