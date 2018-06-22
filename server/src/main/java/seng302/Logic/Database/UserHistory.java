package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.HistoryItem;
import seng302.Model.Medication.Medication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserHistory {
    private Connection connection;
    private String currentDatabase;

    public UserHistory() {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        connection = databaseConfiguration.getConnection();
        currentDatabase = databaseConfiguration.getCurrentDatabase();
    }

    public ArrayList<HistoryItem> getAllHistoryItems(int userId) throws SQLException {
        ArrayList<HistoryItem> allHistoryItems = new ArrayList<>();
        String query = "SELECT * FROM " + currentDatabase + ".HISTORY_ITEM WHERE user_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, userId);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()) {
            allHistoryItems.add(getHistoryItemFromResultSet(resultSet));
        }
        return allHistoryItems;
    }

    public void insertHistoryItem(HistoryItem historyItem, int userId) throws SQLException {
        String insertHistoryItemQuery = "INSERT INTO " + currentDatabase + ".HISTORY_ITEM (dateTime, action, description, user_id) " +
                "VALUES (?, ?, ?, ?)";
        PreparedStatement insertHistoryItemStatement = connection.prepareStatement(insertHistoryItemQuery);

        insertHistoryItemStatement.setTimestamp(1, java.sql.Timestamp.valueOf(historyItem.getDateTime()));
        insertHistoryItemStatement.setString(2, historyItem.getAction());
        insertHistoryItemStatement.setString(3, historyItem.getDescription());
        insertHistoryItemStatement.setInt(4, userId);

        System.out.println("Inserting new history item -> Successful -> Rows Added: " + insertHistoryItemStatement.executeUpdate());
    }

    public HistoryItem getHistoryItemFromResultSet(ResultSet historyItemsResultSet) throws SQLException {

        return new HistoryItem(
                historyItemsResultSet.getTimestamp("dateTime").toLocalDateTime(),
                historyItemsResultSet.getString("action"),
                historyItemsResultSet.getString("description"),
                historyItemsResultSet.getInt("id")
        );

    }
}
