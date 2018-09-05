package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Notifications {

    /**
     * Register a mobile device against a user id
     * @param device_id The Visual Studio App Center device UUID which identifies the device
     * @param token The login token of the user
     * @throws SQLException When something goes wrong
     */
    public void register(String device_id, String token) throws SQLException {
        if(device_id == null) return;
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            //
            String query = "INSERT INTO PUSH_DEVICE (device_id, user_token) VALUES(?, ?) ON DUPLICATE KEY UPDATE user_id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, device_id);
            statement.setString(2, token);
            statement.setString(3, device_id);
            statement.execute();
        }
    }

    /**
     * Removes a device from the database so it no longer receives notifications
     * @param device_id The Visual Studio App Center device UUID which identifies the device
     * @throws SQLException When something goes wrong
     */
    public void unregister(String device_id) throws SQLException {
        if(device_id == null) return;
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            //
            String query = "DELETE FROM PUSH_DEVICE WHERE device_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, device_id);
            statement.execute();
        }
    }


    /**
     * Get all the devices on which a user is registered to receive push notifications
     * @param token The login token of the user
     * @return A list of the device UUIDs on which the user has logged in
     * @throws SQLException When something goes wrong
     */
    public List<String> getDevices(String token) throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            //
            String query = "SELECT device_id FROM PUSH_DEVICE WHERE user_token = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, token);
            ResultSet resultSet = statement.executeQuery();
            List<String> devices = new ArrayList<>();
            while(resultSet.next()) {
                devices.add(resultSet.getString("device_id"));
            }
            return devices;
        }
    }

}
