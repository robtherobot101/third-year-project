package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Logic.OrganMatching;
import seng302.Model.Attribute.Organ;
import seng302.Model.DonatableOrgan;
import seng302.Model.MapObject;
import seng302.Model.OrganTransfer;

import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Set;

public class MapObjectModel extends DatabaseMethods {

    public ArrayList<MapObject> getAllMapObjects() throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            ArrayList<MapObject> allMapObjects = new ArrayList<>();
            String query =
                    "SELECT first_name, middle_names, last_name, gender, id, current_address, region, cityOfDeath, " +
                            "regionOfDeath, countryOfDeath, date_of_death " +
                            "FROM USER " +
                            "WHERE date_of_death IS NOT NULL";

            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allMapObjects.add(getMapObjectFromResultSet(resultSet));
            }

            return allMapObjects;
        } finally {
            close(resultSet, statement);
        }
    }

    public MapObject getMapObjectFromResultSet(ResultSet mapObjectResultSet) throws SQLException {

        MapObject mapObject = new MapObject();

        PreparedStatement organsStatement = null;
        ResultSet organsResultSet = null;

        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            //Get all the organs donated for the dead user
            String organsQuery = "SELECT * FROM DONATION_LIST_ITEM WHERE user_id = ?";
            organsStatement = connection.prepareStatement(organsQuery);

            organsStatement.setInt(1, mapObjectResultSet.getInt("id"));
            organsResultSet = organsStatement.executeQuery();

            mapObject.organs = new ArrayList<>();

            OrganMatching organMatching = new OrganMatching();

            while (organsResultSet.next()) {

                boolean expired = true;
                if (organsResultSet.getInt("expired") == 0){
                    expired = false;
                }

                DonatableOrgan organ = new DonatableOrgan(
                        LocalDateTime.ofEpochSecond(organsResultSet.getLong("timeOfDeath"),0, ZoneOffset.ofHours(+12)),
                        Organ.parse(organsResultSet.getString("name")),
                        mapObjectResultSet.getInt("id"),
                        expired,
                        organsResultSet.getInt("inTransfer")

                );

                organ.setId(organsResultSet.getInt("id"));


                organ.setTopReceivers(organMatching.getTop5Matches(organ, ""));

                mapObject.getOrgans().add(organ);
            }
            mapObject.firstName = mapObjectResultSet.getString("first_name");
            mapObject.middleName = mapObjectResultSet.getString("middle_names");
            mapObject.lastName = mapObjectResultSet.getString("last_name");
            mapObject.gender = mapObjectResultSet.getString("gender");
            mapObject.id = mapObjectResultSet.getInt("id");
            mapObject.currentAddress = mapObjectResultSet.getString("current_address");
            mapObject.region = mapObjectResultSet.getString("region");
            mapObject.cityOfDeath = mapObjectResultSet.getString("cityOfDeath");
            mapObject.regionOfDeath = mapObjectResultSet.getString("regionOfDeath");
            mapObject.countryOfDeath = mapObjectResultSet.getString("countryOfDeath");
            mapObject.dateOfDeath = mapObjectResultSet.getTimestamp("date_of_death").toLocalDateTime();
        } finally {
            close(organsResultSet, organsStatement);
        }

        return mapObject;

    }

    /**
     * gets all the organTransfers from the database
     * @return a  List of all the organTransfer objects
     * @throws SQLException throws if cannot connect to database
     */
    public ArrayList<OrganTransfer> getAllTransfers() throws SQLException{
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            ArrayList<OrganTransfer> allTransfers = new ArrayList<>();
            String query =
                    "SELECT * FROM TRANSFERS ";

            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allTransfers.add(getOrganTransferFromResultSet(resultSet));
            }

            return allTransfers;
        }
        finally {
            close(resultSet, statement);
        }
    }

    /**
     * inserts an OrganTransfer into the database
     * @param transfer OrganTransfer to insert into the database
     * @throws SQLException throws if cannot connect to database
     */
    public void insertTransfer(OrganTransfer transfer) throws SQLException{
        PreparedStatement statement = null;
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()){
            String query = "INSERT INTO TRANSFERS (StartLat, StartLon, EndLat, EndLon, ArrivalTime, OrganId, ReceiverId, OrganType) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setDouble(1, transfer.getStartLat());
            statement.setDouble(2, transfer.getStartLon());
            statement.setDouble(3, transfer.getEndLat());
            statement.setDouble(4, transfer.getEndLon());
            statement.setTimestamp(5, Timestamp.valueOf(transfer.getArrivalTime()));
            statement.setInt(6, transfer.getId());
            statement.setLong(7, transfer.getReceiverId());
            statement.setString(8, transfer.getOrganType().toString());


            System.out.println("Inserting new transfer  -> Successful -> Rows Added: " + statement.executeUpdate());
        }
        finally {
            close(statement);
        }
    }

    /**
     * gets an OrganTransfer from a ResultSet
     * @param organTransferResultSet ResultSet to get OrganTransfer from
     * @return an OrganTransfer
     * @throws SQLException throws if cannot convert ResultSet to OrganTransfer
     */
    private OrganTransfer getOrganTransferFromResultSet(ResultSet organTransferResultSet) throws SQLException{
        OrganTransfer organTransfer = new OrganTransfer(
                organTransferResultSet.getDouble("StartLat"),
                organTransferResultSet.getDouble("StartLon"),
                organTransferResultSet.getDouble("EndLat"),
                organTransferResultSet.getDouble("EndLon"),
                organTransferResultSet.getTimestamp("ArrivalTime") != null ? organTransferResultSet.getTimestamp("ArrivalTime").toLocalDateTime() : null,
                organTransferResultSet.getInt("OrganId"),
                organTransferResultSet.getLong("ReceiverId"),
                Organ.parse(organTransferResultSet.getString("OrganType")));

        return  organTransfer;
    }

    /**
     * Deletes a OrganTransfer with a given OrganId fromt eh database
     * @param organId OrganId of OrganTransfer to be deleted
     * @throws SQLException throws if cannot connect to database
     */
    public void removeDonationListItem(int organId) throws SQLException {
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String deleteTransfer = "DELETE FROM TRANSFERS WHERE OrganId = ?";
            statement = connection.prepareStatement(deleteTransfer);
            statement.setInt(1, organId);
            System.out.println("Deletion of Organ Transfer - OrganId: " + organId + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        }
        finally {
            close(statement);
        }
    }

}


