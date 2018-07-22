package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomQuerying {

    public ResultSet executeQuery(String query) throws SQLException {
        PreparedStatement statement = DatabaseConfiguration.getInstance().getConnection().prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        return resultSet;
    }

    /*public void removeDonationListItem(int userId, String donationListItemName) throws SQLException {
        String update = "DELETE FROM DONATION_LIST_ITEM WHERE name = ? AND user_id = ?";
        PreparedStatement statement = DatabaseConfiguration.getInstance().getConnection().prepareStatement(update);
        statement.setString(1, donationListItemName);
        statement.setInt(2, userId);
        System.out.println("Deletion of Donation List Item - NAME: " + donationListItemName + " USERID: " + userId + " -> Successful -> Rows Removed: " + statement.executeUpdate());
    }
*/
}
