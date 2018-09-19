package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GeneralAdmin extends DatabaseMethods {

    /**
     * Takes a resultSet, pulls out an admin instance, and returns it.
     *
     * @param resultSet The resultSet
     * @return An admin
     * @throws SQLException If there is an error communicating with the database
     */
    public Admin getAdminFromResultSet(ResultSet resultSet) throws SQLException {
        Admin admin = new Admin(
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("name")
        );
        admin.setWorkAddress(resultSet.getString("work_address"));
        admin.setRegion(resultSet.getString("region"));
        admin.setStaffID(resultSet.getInt("staff_id"));

        return admin;
    }

    /**
     * Takes an admin instance and inserts it into the admin table on the database.
     *
     * @param admin The admin which will be inserted into the database.
     * @throws SQLException If there is an error working with the database
     */
    public void insertAdmin(Admin admin) throws SQLException {
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String insert = "INSERT INTO ADMIN(username, password, name, work_address, region) " +
                    "VALUES(?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(insert);

            statement.setString(1, admin.getUsername());
            statement.setString(2, admin.getPassword());
            statement.setString(3, admin.getName());
            statement.setString(4, admin.getWorkAddress());
            statement.setString(5, admin.getRegion());
            System.out.println("Inserting new admin -> Successful -> Rows Added: " + statement.executeUpdate());
        } finally {
            close(statement);
        }
    }

    /**
     * Returns the if of the admin with the username which matches the one given.
     *
     * @param username The username to query
     * @return The id of the matched admin
     * @throws SQLException If there is a problem working with the database.
     */
    public int getAdminIdFromUsername(String username) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "SELECT staff_id FROM ADMIN WHERE username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("staff_id");
        } finally {
            close(resultSet, statement);
        }
    }

    /**
     * Returns the admin with the id which matches the one given. If no such admin is fond,
     * null is returned.
     *
     * @param id The id which will be matched
     * @return An admin instance.
     * @throws SQLException If there is a problem working with the database.
     */
    public Admin getAdminFromId(int id) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            // SELECT * FROM ADMIN id = id;
            String query = "SELECT * FROM ADMIN WHERE staff_id = ?";
            statement = connection.prepareStatement(query);

            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a new clinician Object with the fields from the database
                return getAdminFromResultSet(resultSet);
            }
        } finally {
            close(resultSet, statement);
        }
    }

    /**
     * Returns an ArrayList of Admins from the database.
     *
     * @return The ArrayList of Admins from the database
     * @throws SQLException If there is a problem working with the database.
     */
    public ArrayList<Admin> getAllAdmins() throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            ArrayList<Admin> allAdmins = new ArrayList<>();
            String query = "SELECT * FROM ADMIN";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allAdmins.add(getAdminFromResultSet(resultSet));
            }

            return allAdmins;
        } finally {
            close(resultSet, statement);
        }
    }

    /**
     * Removes the given admin from the database by matching their ID's
     *
     * @param admin The admin which will be removed
     * @throws SQLException If there is a problem working with the database.
     */
    public void removeAdmin(Admin admin) throws SQLException {
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM ADMIN WHERE username = ?";
            statement = connection.prepareStatement(update);
            statement.setString(1, admin.getUsername());
            System.out.println("Deletion of admin: " + admin.getUsername() + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        } finally {
            close(statement);
        }
    }

    /**
     * Updates the admin on the database with the same ID as the one given with the fields of the given admin.
     *
     * @param admin The given admin
     * @throws SQLException If there is a problem working with the database.
     */
    public void updateAdminDetails(Admin admin) throws SQLException {
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "UPDATE ADMIN SET name = ?, work_address = ?, region = ? WHERE username = ?";
            statement = connection.prepareStatement(update);

            statement.setString(1, admin.getName());
            statement.setString(2, admin.getWorkAddress());
            statement.setString(3, admin.getRegion());
            statement.setString(4, admin.getUsername());
            System.out.println("Update admin Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());
        } finally {
            close(statement);
        }
    }
}
