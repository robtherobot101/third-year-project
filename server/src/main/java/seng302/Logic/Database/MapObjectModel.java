package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Attribute.Organ;
import seng302.Model.MapObject;
import seng302.Model.WaitingListItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class MapObjectModel {

    public ArrayList<MapObject> getAllMapObjects() throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            ArrayList<MapObject> allMapObjects = new ArrayList<>();
            String query =
                    "SELECT name, user_id, timeOfDeath, current_address, region, cityOfDeath, " +
                            "regionOfDeath, countryOfDeath " +
                            "FROM DONATION_LIST_ITEM " +
                            "JOIN USER ON DONATION_LIST_ITEM.user_id = USER.id";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allMapObjects.add(getMapObjectFromResultSet(resultSet));
            }

            return allMapObjects;
        }
    }

    public MapObject getMapObjectFromResultSet(ResultSet mapObjectResultSet) throws SQLException {

        return new MapObject(
                mapObjectResultSet.getString("name"),
                mapObjectResultSet.getInt("user_id"),
                mapObjectResultSet.getLong("timeOfDeath"),
                mapObjectResultSet.getString("current_address"),
                mapObjectResultSet.getString("region"),
                mapObjectResultSet.getString("cityOfDeath"),
                mapObjectResultSet.getString("regionOfDeath"),
                mapObjectResultSet.getString("countryOfDeath")
        );

    }
}
