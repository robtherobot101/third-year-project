package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Attribute.Organ;
import seng302.Model.WaitingListItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserWaitingList {

    public ArrayList<WaitingListItem> getAllWaitingListItems() throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            ArrayList<WaitingListItem> allWaitingListItems = new ArrayList<>();
            String query =
                    "SELECT * FROM WAITING_LIST_ITEM " +
                            "LEFT JOIN (SELECT 'conflicting' AS is_conflicting, WL.id " +
                                "FROM WAITING_LIST_ITEM AS WL JOIN DONATION_LIST_ITEM AS DL " +
                                    "ON (WL.user_id = DL.user_id AND WL.organ_type = DL.name)) AS CONFLICTING " +
                            "ON WAITING_LIST_ITEM.id = CONFLICTING.id";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allWaitingListItems.add(getWaitingListItemFromResultSet(resultSet, true));
            }

            return allWaitingListItems;
        }
    }

    public ArrayList<WaitingListItem> getAllUserWaitingListItems(int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            ArrayList<WaitingListItem> allWaitingListItems = new ArrayList<>();
            String query = "SELECT * FROM WAITING_LIST_ITEM WHERE user_id = ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allWaitingListItems.add(getWaitingListItemFromResultSet(resultSet, false));
            }
            return allWaitingListItems;
        }
    }

    public WaitingListItem getWaitingListItemFromResultSet(ResultSet waitingListItemResultSet, boolean allItems) throws SQLException {

        LocalDate deregisteredDate = waitingListItemResultSet.getDate("organ_deregistered_date") != null ?
                waitingListItemResultSet.getDate("organ_deregistered_date").toLocalDate() : null;

        if(allItems) {
            return new WaitingListItem(
                    Organ.parse(waitingListItemResultSet.getString("organ_type")),
                    waitingListItemResultSet.getDate("organ_registered_date").toLocalDate(),
                    waitingListItemResultSet.getInt("id"),
                    waitingListItemResultSet.getInt("user_id"),
                    deregisteredDate,
                    waitingListItemResultSet.getInt("deregistered_code"),
                    waitingListItemResultSet.getString("is_conflicting") != null
            );
        } else {
            return new WaitingListItem(
                    Organ.parse(waitingListItemResultSet.getString("organ_type")),
                    waitingListItemResultSet.getDate("organ_registered_date").toLocalDate(),
                    waitingListItemResultSet.getInt("id"),
                    waitingListItemResultSet.getInt("user_id"),
                    deregisteredDate,
                    waitingListItemResultSet.getInt("deregistered_code"),
                    false //Calculated on client side.
            );
        }
    }


    public WaitingListItem getWaitingListItemFromId(int waitingListItemId, int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            // SELECT * FROM WAITING_LIST_ITEM id = id;
            String query = "SELECT * FROM WAITING_LIST_ITEM WHERE id = ? AND user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, waitingListItemId);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a indication that fields arent empty
                return getWaitingListItemFromResultSet(resultSet, false);
            }
        }
    }

    public void insertWaitingListItem(WaitingListItem waitingListItem, int userId) throws SQLException{
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String insert = "INSERT INTO WAITING_LIST_ITEM (organ_type, organ_registered_date, organ_deregistered_date, deregistered_code, user_id) VALUES  (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insert);
            statement.setString(1, waitingListItem.getOrganType().toString());
            statement.setDate(2, java.sql.Date.valueOf(waitingListItem.getOrganRegisteredDate()));
            if(waitingListItem.getOrganDeregisteredDate() == null) {
                statement.setNull(3, java.sql.Types.DATE);
            }else{
                statement.setDate(3, java.sql.Date.valueOf(waitingListItem.getOrganDeregisteredDate()));
            }
            statement.setInt(4, waitingListItem.getOrganDeregisteredCode());
            statement.setInt(5, userId);

            System.out.println("Inserting new waiting list item -> Successful -> Rows Added: " + statement.executeUpdate());
        }
    }

    public void updateWaitingListItem(WaitingListItem waitingListItem, int waitingListItemId, int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String insert = "UPDATE WAITING_LIST_ITEM SET organ_type = ?, organ_registered_date = ?, organ_deregistered_date = ?, deregistered_code = ? WHERE user_id = ? AND id = ?";
            PreparedStatement statement = connection.prepareStatement(insert);
            statement.setString(1, waitingListItem.getOrganType().toString());
            statement.setDate(2, java.sql.Date.valueOf(waitingListItem.getOrganRegisteredDate()));
            statement.setDate(3, java.sql.Date.valueOf(waitingListItem.getOrganDeregisteredDate()));
            statement.setInt(4, waitingListItem.getOrganDeregisteredCode());
            statement.setInt(5, userId);
            statement.setInt(6, waitingListItemId);

            System.out.println("Update Waiting List Item - ID: " + waitingListItemId + " USERID: " + userId + " -> Successful -> Rows Updated: " + statement.executeUpdate());
        }
    }

    public void removeWaitingListItem(int userId, int waitingListItemId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM WAITING_LIST_ITEM WHERE id = ? AND user_id = ?";
            PreparedStatement statement = connection.prepareStatement(update);
            statement.setInt(1, waitingListItemId);
            statement.setInt(2, userId);
            System.out.println("Deletion of Waiting List Item - ID: " + waitingListItemId + " USERID: " + userId + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        }
    }

    public void removeWaitingListItem(int userId, Organ organ) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "UPDATE WAITING_LIST_ITEM SET organ_deregistered_date = ?, deregistered_code = ? WHERE user_id = ? AND organ_type = ? AND organ_deregistered_date = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setDate(1,java.sql.Date.valueOf(LocalDate.now()));
            statement.setInt(2,5);
            System.out.println("Deletion of Waiting List Item - Organ: " + organ.toString() + " USERID: " + userId + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        }
    }


    /**
     * Replace a user's waiting list items on the database with a new set of waiting list items.
     *
     * @param newWaitingListItems The list of waiting list items to replace the old one with
     * @param userId The id of the user to replace waiting list items of
     * @throws SQLException If there is errors communicating with the database
     */
    public void updateAllWaitingListItems(List<WaitingListItem> newWaitingListItems, int userId) throws SQLException {
        List<WaitingListItem> oldWaitingListItems = getAllUserWaitingListItems(userId);

        //Remove all procedures that are already on the database
        for (int i = oldWaitingListItems.size() - 1; i >= 0; i--) {
            WaitingListItem found = null;
            for (WaitingListItem newWaitingListItem: newWaitingListItems) {
                if (newWaitingListItem.equals(oldWaitingListItems.get(i))) {
                    found = newWaitingListItem;
                    break;
                }
            }
            if (found == null) {
                //Patch edited medications
                for (WaitingListItem newWaitingListItem: newWaitingListItems) {
                    if (newWaitingListItem.getId() == oldWaitingListItems.get(i).getId()) {
                        updateWaitingListItem(oldWaitingListItems.get(i), oldWaitingListItems.get(i).getId(), userId);
                        found = newWaitingListItem;
                        break;
                    }
                }
            }
            if (found != null) {
                newWaitingListItems.remove(found);
                oldWaitingListItems.remove(i);
            }
        }

        //Delete all medications from the database that are no longer up to date
        for (WaitingListItem waitingListItem: oldWaitingListItems) {
            removeWaitingListItem(userId, waitingListItem.getId());
        }

        //Upload all new medications
        for (WaitingListItem waitingListItem: newWaitingListItems) {
            insertWaitingListItem(waitingListItem, userId);
        }
    }
}
