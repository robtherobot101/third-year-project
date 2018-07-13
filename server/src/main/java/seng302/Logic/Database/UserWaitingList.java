package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Attribute.Organ;
import seng302.Model.Procedure;
import seng302.Model.WaitingListItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class UserWaitingList {

    private Connection connection;
    private String currentDatabase;

    public UserWaitingList() {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        connection = databaseConfiguration.getConnection();
        currentDatabase = databaseConfiguration.getCurrentDatabase();
    }

    public ArrayList<WaitingListItem> getAllWaitingListItems() throws SQLException {
        ArrayList<WaitingListItem> allWaitingListItems = new ArrayList<>();
        String query = "SELECT * FROM " + currentDatabase + ".WAITING_LIST_ITEM";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()) {
            allWaitingListItems.add(getWaitingListItemFromResultSet(resultSet));
        }
        return allWaitingListItems;
    }

    public ArrayList<WaitingListItem> getAllUserWaitingListItems(int userId) throws SQLException {
        ArrayList<WaitingListItem> allWaitingListItems = new ArrayList<>();
        String query = "SELECT * FROM " + currentDatabase + ".WAITING_LIST_ITEM WHERE user_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, userId);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()) {
            allWaitingListItems.add(getWaitingListItemFromResultSet(resultSet));
        }
        return allWaitingListItems;
    }

    public WaitingListItem getWaitingListItemFromResultSet(ResultSet waitingListItemResultSet) throws SQLException {

        LocalDate deregisteredDate = waitingListItemResultSet.getDate("organ_deregistered_date") != null ?
                waitingListItemResultSet.getDate("organ_deregistered_date").toLocalDate() : null;

        return new WaitingListItem(
                Organ.parse(waitingListItemResultSet.getString("organ_type")),
                waitingListItemResultSet.getDate("organ_registered_date").toLocalDate(),
                waitingListItemResultSet.getInt("id"),
                waitingListItemResultSet.getInt("user_id"),
                deregisteredDate,
                waitingListItemResultSet.getInt("deregistered_code")

        );


    }

    public WaitingListItem getWaitingListItemFromId(int waitingListItemId, int userId) throws SQLException {
        // SELECT * FROM WAITING_LIST_ITEM id = id;
        String query = "SELECT * FROM " + currentDatabase + ".WAITING_LIST_ITEM WHERE id = ? AND user_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setInt(1, waitingListItemId);
        statement.setInt(2, userId);
        ResultSet resultSet = statement.executeQuery();

        //If response is empty then return null
        if (!resultSet.next()) {
            return null;
        } else {
            //If response is not empty then return a indication that fields arent empty
            return getWaitingListItemFromResultSet(resultSet);
        }

    }

    public void insertWaitingListItem(WaitingListItem waitingListItem, int userId) throws SQLException{

        String insert = "INSERT INTO " + currentDatabase + ".WAITING_LIST_ITEM (organ_type, organ_registered_date, organ_deregistered_date, deregistered_code, user_id) VALUES  (?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(insert);
        statement.setString(1, waitingListItem.getOrganType().toString());
        statement.setDate(2, java.sql.Date.valueOf(waitingListItem.getOrganRegisteredDate()));
        if(waitingListItem.getOrganDeregisteredDate() == null) {
            statement.setNull(3, Types.DATE);
        }else{
            statement.setDate(3, java.sql.Date.valueOf(waitingListItem.getOrganDeregisteredDate()));
        }
        statement.setInt(4, waitingListItem.getOrganDeregisteredCode());
        statement.setInt(5, userId);

        System.out.println("Inserting new waiting list item -> Successful -> Rows Added: " + statement.executeUpdate());
    }

    public void updateWaitingListItem(WaitingListItem waitingListItem, int waitingListItemId, int userId) throws SQLException {
        String insert = "UPDATE " + currentDatabase + ".WAITING_LIST_ITEM SET organ_type = ?, organ_registered_date = ?, organ_deregistered_date = ?, deregistered_code = ? WHERE user_id = ? AND id = ?";
        PreparedStatement statement = connection.prepareStatement(insert);
        statement.setString(1, waitingListItem.getOrganType().toString());
        statement.setDate(2, java.sql.Date.valueOf(waitingListItem.getOrganRegisteredDate()));
        statement.setDate(3, java.sql.Date.valueOf(waitingListItem.getOrganDeregisteredDate()));
        statement.setInt(4, waitingListItem.getOrganDeregisteredCode());
        statement.setInt(5, userId);
        statement.setInt(6, waitingListItemId);

        System.out.println("Update Waiting List Item - ID: " + waitingListItemId + " USERID: " + userId + " -> Successful -> Rows Updated: " + statement.executeUpdate());
    }

    public void removeWaitingListItem(int userId, int waitingListItemId) throws SQLException {
        String update = "DELETE FROM " + currentDatabase + ".WAITING_LIST_ITEM WHERE id = ? AND user_id = ?";
        PreparedStatement statement = connection.prepareStatement(update);
        statement.setInt(1, waitingListItemId);
        statement.setInt(2, userId);
        System.out.println("Deletion of Waiting List Item - ID: " + waitingListItemId + " USERID: " + userId + " -> Successful -> Rows Removed: " + statement.executeUpdate());
    }

}
