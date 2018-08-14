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
import java.util.List;
import java.util.Map;

public class UserWaitingList {

    /**
     * gets all the organs from the database
     * @return returns a list of all the organs in the database
     * @throws SQLException throws if cannot connect to the database
     */
    public List<WaitingListItem> queryWaitingListItems(Map<String, String> params) throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            List<WaitingListItem> waitingListItems = new ArrayList<>();
            String query = buildWaitingListItemQuery(params);
            System.out.println(query);
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                waitingListItems.add(getWaitingListItemFromResultSet(resultSet, true));
            }
            return waitingListItems;
        }
    }

    public String buildWaitingListItemQuery(Map<String, String> params) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM WAITING_LIST_ITEM " +
                "LEFT JOIN (SELECT 'conflicting' AS is_conflicting, WL.id " +
                "FROM WAITING_LIST_ITEM AS WL JOIN DONATION_LIST_ITEM AS DL " +
                "ON (WL.user_id = DL.user_id AND WL.organ_type = DL.name)) AS CONFLICTING " +
                "ON WAITING_LIST_ITEM.id = CONFLICTING.id JOIN USER ON " +
                "WAITING_LIST_ITEM.user_id = USER.id " +
                "WHERE 1=1 ");

        if(params.keySet().contains("region")) {
            queryBuilder.append("AND (USER.region LIKE '" + params.get("region") + "%' OR (USER.region IS NULL AND '" + params.get("region") +"' = '')) ");
        }

        if(params.keySet().contains("organ")) {
            queryBuilder.append("AND WAITING_LIST_ITEM.organ_type = '" + params.get("organ") + "' ");
        }
        System.out.println(queryBuilder.toString());
        return queryBuilder.toString();
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
}
