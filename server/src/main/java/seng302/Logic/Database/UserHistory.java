package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.HistoryItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserHistory {

    public ArrayList<HistoryItem> getAllHistoryItems(int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            ArrayList<HistoryItem> allHistoryItems = new ArrayList<>();
            String query = "SELECT * FROM HISTORY_ITEM WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allHistoryItems.add(getHistoryItemFromResultSet(resultSet));
            }
            return allHistoryItems;
        }
    }

    public void insertHistoryItem(HistoryItem historyItem, int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String insertHistoryItemQuery = "INSERT INTO HISTORY_ITEM (dateTime, action, description, user_id) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement insertHistoryItemStatement = connection.prepareStatement(insertHistoryItemQuery);

            insertHistoryItemStatement.setTimestamp(1, java.sql.Timestamp.valueOf(historyItem.getDateTime()));
            insertHistoryItemStatement.setString(2, historyItem.getAction());
            insertHistoryItemStatement.setString(3, historyItem.getDescription());
            insertHistoryItemStatement.setInt(4, userId);

            System.out.println("Inserting new history item -> Successful -> Rows Added: " + insertHistoryItemStatement.executeUpdate());
        }
    }

    public void removeHistoryItem(int userId, int historyItemId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM HISTORY_ITEM WHERE id = ? AND user_id = ?";
            PreparedStatement statement = connection.prepareStatement(update);
            statement.setInt(1, historyItemId);
            statement.setInt(2, userId);
            System.out.println("Deletion of History Item - ID: " + historyItemId + " USERID: " + userId + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        }
    }

    public HistoryItem getHistoryItemFromResultSet(ResultSet historyItemsResultSet) throws SQLException {

        return new HistoryItem(
                historyItemsResultSet.getTimestamp("dateTime").toLocalDateTime(),
                historyItemsResultSet.getString("action"),
                historyItemsResultSet.getString("description"),
                historyItemsResultSet.getInt("id")
        );

    }

    /**
     * Updates a user's history on the database to a new history list.
     *
     * @param newHistory The list of history to update to
     * @param userId The id of the user to update
     * @throws SQLException If there is issues connecting to the database
     */
    public void updateHistory(List<HistoryItem> newHistory, int userId) throws SQLException {
        List<HistoryItem> oldHistory = getAllHistoryItems(userId);
        int sameUntil = 0;
        while (sameUntil < newHistory.size() && sameUntil < oldHistory.size() && newHistory.get(sameUntil).informationEqual(oldHistory.get(sameUntil))) {
            sameUntil++;
        }

        newHistory = newHistory.subList(sameUntil, newHistory.size());
        oldHistory = oldHistory.subList(sameUntil, oldHistory.size());

        for (HistoryItem oldItem: oldHistory) {
            removeHistoryItem(userId, oldItem.getId());
        }
        for (HistoryItem newItem: newHistory) {
            insertHistoryItem(newItem, userId);
        }
    }
}
