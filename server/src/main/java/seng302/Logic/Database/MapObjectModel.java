package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Attribute.Organ;
import seng302.Model.MapObject;
import seng302.Model.User;
import seng302.Model.WaitingListItem;
import seng302.Model.DonatableOrgan;

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
        }
        finally {
            close();
        }
    }

    public MapObject getMapObjectFromResultSet(ResultSet mapObjectResultSet) throws SQLException {

        MapObject mapObject = new MapObject();

        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            //Get all the organs donated for the dead user
            String organsQuery = "SELECT * FROM DONATION_LIST_ITEM WHERE user_id = ?";
            PreparedStatement organsStatement = connection.prepareStatement(organsQuery);

            organsStatement.setInt(1, mapObjectResultSet.getInt("id"));
            ResultSet organsResultSet = organsStatement.executeQuery();

            mapObject.organs = new ArrayList<>();

            boolean expired = true;
            if (organsResultSet.getInt("expired") == 0){
                expired = false;
            }

            while (organsResultSet.next()) {
                DonatableOrgan organ = new DonatableOrgan(
                        LocalDateTime.ofEpochSecond(organsResultSet.getLong("timeOfDeath"),0, ZoneOffset.ofHours(+12)),
                        Organ.parse(organsResultSet.getString("name")),
                        mapObjectResultSet.getInt("id"),
                        expired

                );

                mapObject.getOrgans().add(organ);
            }
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

        return mapObject;

    }
}
