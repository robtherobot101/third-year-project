package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Attribute.Organ;
import seng302.Model.WaitingListItem;
import seng302.NotificationManager.PushAPI;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDonations extends DatabaseMethods {

    public Set<Organ> getAllUserDonations(int userId) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            Set<Organ> organs = new HashSet<>();
            String query = "SELECT * FROM DONATION_LIST_ITEM WHERE user_id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                organs.add(getOrganFromResultSet(resultSet));
            }
            return organs;
        } finally {
            close(resultSet, statement);
        }
    }

    public Set<Organ> getAllDonations() throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            Set<Organ> organs = new HashSet<>();
            String query = "SELECT * FROM DONATION_LIST_ITEM";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                organs.add(getOrganFromResultSet(resultSet));
            }
            return organs;
        } finally {
            close(resultSet, statement);
        }
    }

    public void insertDonation(Organ organ, int userId, LocalDateTime deathDate) throws SQLException {
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String insertDonationQuery = "INSERT INTO DONATION_LIST_ITEM (name, user_id, timeOfDeath, expired) " +
                    "VALUES (?,?,?,?)";
            statement = connection.prepareStatement(insertDonationQuery);

            statement.setString(1, organ.toString());
            statement.setInt(2, userId);
            if (deathDate == null) {
                statement.setNull(3, Types.BIGINT);
                statement.setInt(4, 0);
            } else {
                statement.setLong(3, deathDate.toEpochSecond(OffsetDateTime.now().getOffset()));
                if (deathDate.plus(getExpiryDuration(organ)).isBefore(LocalDateTime.now())) {
                    statement.setInt(4, 1);
                } else {
                    statement.setInt(4, 0);
                }
            }

            System.out.println("Inserting new donation -> Successful -> Rows Added: " + statement.executeUpdate());
        } finally {
            close(statement);
        }
    }

    public Organ getOrganFromResultSet(ResultSet resultSet) throws SQLException {
        return Organ.parse(resultSet.getString("name").toUpperCase());
    }

    public Organ getDonationListItemFromName(String donationListItemName, int userId) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            // SELECT * FROM DONATION_LIST_ITEM id = id;
            String query = "SELECT * FROM DONATION_LIST_ITEM WHERE name = ? AND user_id = ?";
            statement = connection.prepareStatement(query);

            statement.setString(1, donationListItemName);
            statement.setInt(2, userId);
            resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a indication that fields arent empty
                return getOrganFromResultSet(resultSet);
            }
        } finally {
            close(resultSet, statement);
        }
    }

    public void removeDonationListItem(int userId, String donationListItemName) throws SQLException {
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM DONATION_LIST_ITEM WHERE name = ? AND user_id = ?";
            statement = connection.prepareStatement(update);
            statement.setString(1, donationListItemName);
            statement.setInt(2, userId);
            System.out.println("Deletion of Donation List Item - NAME: " + donationListItemName + " USERID: " + userId + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        } finally {
            close(statement);
        }
    }

    public void removeAllUserDonations(int userId) throws SQLException {
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM DONATION_LIST_ITEM WHERE user_id = ?";
            statement = connection.prepareStatement(update);
            statement.setInt(1, userId);
            System.out.println("Deletion of all Donation List Items for - " + " USERID: " + userId + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        } finally {
            close(statement);
        }
    }

    /**
     * Replace a user's organ donation list with a new list of organs.
     *
     * @param newOrgans   The list of organs to update to
     * @param userId      The id of the user to update
     * @param dateOfDeath the date of death of a user
     * @throws SQLException If there is errors communicating with the database
     */
    public void updateAllDonations(Set<Organ> newOrgans, int userId, LocalDateTime dateOfDeath) throws SQLException {
        List<Organ> oldDonationItems = new ArrayList<Organ>(getAllUserDonations(userId));
        List<Organ> newDonationItems = new ArrayList<Organ>(newOrgans);

        //Ignore all waiting list items that are already on the database and up to date
        for (int i = oldDonationItems.size() - 1; i >= 0; i--) {
            Organ found = null;
            for (Organ newOrgan : newDonationItems) {
                if (newOrgan == oldDonationItems.get(i)) {
                    found = newOrgan;
                    break;
                }
            }
            if (found == null) {
                //Patch edited donations
                for (Organ newOrgan : newDonationItems) {
                    if (newOrgan == oldDonationItems.get(i)) {
                        System.out.println("Exists: " + newOrgan);
                        found = newOrgan;
                        break;
                    }
                }
            }
            if (found != null) {
                newDonationItems.remove(found);
                oldDonationItems.remove(i);
            }
        }

        //Delete all waiting list items from the database that are no longer up to date
        for (Organ organ : oldDonationItems) {
            removeDonationListItem(userId, organ.toString());
        }

        //Upload all new waiting list items
        for (Organ organ : newDonationItems) {
            insertDonation(organ, userId, new GeneralUser().getUserFromId(userId).getDateOfDeath());
            PushAPI.getInstance().sendTextNotification(userId, "Organ added to your donation list.",
                    Organ.capitalise(organ.toString()) + " was added to your organ donation list.");
        }
    }

    /**
     * Returns a duration of how long the organ will last based on the organ type entered.
     *
     * @param organType The organ type being donated
     * @return How long the organ will last
     */
    public Duration getExpiryDuration(Organ organType) {
        Duration duration = null;
        switch (organType) {
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
