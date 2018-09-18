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
            System.out.println(device_id);
            System.out.println(token);
            String query = "INSERT INTO PUSH_DEVICE (device_id, user_token) VALUES(?, ?) ON DUPLICATE KEY UPDATE user_token=?";
            statement = connection.prepareStatement(query);
            statement.setString(1, device_id);
            statement.setString(2, token);
            statement.setString(3, device_id);
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
     * @param user_ids The ids of the users
     * @return A list of the device UUIDs on which the user has logged in
     * @throws SQLException When something goes wrong
     */
    public List<String> getDevices(List<String> user_ids) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            // Note that what follows is the only way to achieve this in MySQL!

            // Set up the ?,?,?,?, portion of the sql statement
            char[] markers = new char[user_ids.size() * 2 - 1];
            for (int i = 0; i < markers.length; i++)
                markers[i] = ((i & 1) == 0 ? '?' : ',');
            // Create the SQL query with ?,?,?,?, inserted
            String query = "SELECT device_id FROM PUSH_DEVICE JOIN TOKEN WHERE id in (" + Arrays.toString(markers) + ") AND user_token=token";
            statement = connection.prepareStatement(query);

            // Set the the user_id for each ? in the statement
            int id = 0;
            for (String user_id : user_ids) {
                statement.setString(id++, user_id);
            }

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
