package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Notifications extends DatabaseMethods {

    /**
     * Register a mobile device against a user id
     *
     * @param device_id The Visual Studio App Center device UUID which identifies the device
     * @param token     The login token of the user
     * @throws SQLException When something goes wrong
     */
    public void register(String device_id, String token) throws SQLException {
        if (device_id == null) return;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "INSERT INTO PUSH_DEVICE (device_id, user_token) VALUES(?, ?) ON DUPLICATE KEY UPDATE user_token=?";
            statement = connection.prepareStatement(query);
            statement.setString(1, device_id);
            statement.setString(2, token);
            statement.setString(3, token);
            statement.execute();
        } finally {
            close(statement);
        }
    }

    /**
     * Removes a device from the database so it no longer receives notifications
     *
     * @param device_id The Visual Studio App Center device UUID which identifies the device
     * @throws SQLException When something goes wrong
     */
    public void unregister(String device_id) throws SQLException {
        if (device_id == null) return;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            //
            String query = "DELETE FROM PUSH_DEVICE WHERE device_id = ?";
            statement = connection.prepareStatement(query);

            statement.setString(1, device_id);
            statement.execute();
        } finally {
            close(statement);
        }
    }


    /**
     * Get all the devices on which a user is registered to receive push notifications
     *
     * @param user_id The id of the user
     * @return A list of the device UUIDs on which the user has logged in
     * @throws SQLException When something goes wrong
     */
    public List<String> getDevices(long user_id) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "SELECT device_id FROM PUSH_DEVICE JOIN TOKEN WHERE id = ? AND user_token=token";
            statement = connection.prepareStatement(query);
            statement.setLong(1, user_id);
            resultSet = statement.executeQuery();
            List<String> devices = new ArrayList<>();
            while (resultSet.next()) {
                devices.add(resultSet.getString("device_id"));
            }
            return devices;
        } finally {
            close(resultSet, statement);
        }
    }

}
