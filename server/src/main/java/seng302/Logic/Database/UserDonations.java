package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Attribute.Organ;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class UserDonations {

    public Set<Organ> getAllUserDonations(int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            Set<Organ> organs = new HashSet<>();
            String query = "SELECT * FROM DONATION_LIST_ITEM WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                organs.add(getOrganFromResultSet(resultSet));
            }
            return organs;
        }
    }

    public Set<Organ> getAllDonations() throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            Set<Organ> organs = new HashSet<>();
            String query = "SELECT * FROM DONATION_LIST_ITEM";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                organs.add(getOrganFromResultSet(resultSet));
            }
            return organs;
        }
    }

    public void insertDonation(Organ organ, int userId, LocalDateTime deathDate) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String insertDonationQuery = "INSERT INTO DONATION_LIST_ITEM (name, user_id, timeOfDeath, expired) " +
                    "VALUES (?,?,?,?)";
            PreparedStatement insertDonationStatement = connection.prepareStatement(insertDonationQuery);

            insertDonationStatement.setString(1, organ.toString());
            insertDonationStatement.setInt(2, userId);
            if (deathDate == null) {
                insertDonationStatement.setNull(3, Types.BIGINT);
                insertDonationStatement.setInt(4,0);
            } else {
                insertDonationStatement.setLong(3, deathDate.toEpochSecond(OffsetDateTime.now().getOffset()));
                if (deathDate.plus(getExpiryDuration(organ)).isBefore(LocalDateTime.now())){
                    insertDonationStatement.setInt(4, 1);
                } else {
                    insertDonationStatement.setInt(4,0);
                }
            }

            System.out.println("Inserting new donation -> Successful -> Rows Added: " + insertDonationStatement.executeUpdate());
        }
    }

    public Organ getOrganFromResultSet(ResultSet resultSet) throws SQLException {
        return Organ.parse(resultSet.getString("name").toUpperCase());
    }

    public Organ getDonationListItemFromName(String donationListItemName, int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            // SELECT * FROM DONATION_LIST_ITEM id = id;
            String query = "SELECT * FROM DONATION_LIST_ITEM WHERE name = ? AND user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, donationListItemName);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a indication that fields arent empty
                return getOrganFromResultSet(resultSet);
            }
        }
    }

    public void removeDonationListItem(int userId, String donationListItemName) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM DONATION_LIST_ITEM WHERE name = ? AND user_id = ?";
            PreparedStatement statement = connection.prepareStatement(update);
            statement.setString(1, donationListItemName);
            statement.setInt(2, userId);
            System.out.println("Deletion of Donation List Item - NAME: " + donationListItemName + " USERID: " + userId + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        }
    }

    public void removeAllUserDonations(int userId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM DONATION_LIST_ITEM WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(update);
            statement.setInt(1, userId);
            System.out.println("Deletion of all Donation List Items for - " + " USERID: " + userId + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        }
    }

    /**
     * Replace a user's organ donation list with a new list of organs.
     *
     * @param newOrgans The list of organs to update to
     * @param userId The id of the user to update
     * @param dateOfDeath the date of death of a user
     * @throws SQLException If there is errors communicating with the database
     */
    public void updateAllDonations(Set<Organ> newOrgans, int userId, LocalDateTime dateOfDeath) throws SQLException {
        removeAllUserDonations(userId);
        for (Organ organ: newOrgans) {
            insertDonation(organ, userId, dateOfDeath);
        }
    }
    /**
     * Returns a duration of how long the organ will last based on the organ type entered.
     * @param organType The organ type being donated
     * @return How long the organ will last
     */
    public Duration getExpiryDuration(Organ organType) {
        Duration duration = null;
        switch(organType){
            case LUNG:
                duration = Duration.parse("PT6H");
                break;
            case HEART:
                duration = Duration.parse("PT6H");
                break;
            case PANCREAS:
                duration = Duration.parse("PT24H");
                break;
            case LIVER:
                duration = Duration.parse("PT24H");
                break;
            case KIDNEY:
                duration = Duration.parse("PT72H");
                break;
            case INTESTINE:
                duration = Duration.parse("PT10H");
                break;
            case CORNEA:
                duration = Duration.parse("P7D");
                break;
            case EAR:
                duration = Duration.parse("P3650D");//Todo this is unknown and is a place holder
                break;
            case TISSUE:
                duration = Duration.parse("P1825D");
                break;
            case SKIN:
                duration = Duration.parse("P3650D");
                break;
            case BONE:
                duration = Duration.parse("P3650D");
                break;

        }
        return duration;
    }
}
