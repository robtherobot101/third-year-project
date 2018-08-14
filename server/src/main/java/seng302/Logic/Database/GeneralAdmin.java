package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GeneralAdmin {

    /**
     * Takes a resultSet, pulls out an Admin instance, and returns it.
     * @param resultSet The resultSet
     * @return An Admin
     * @throws SQLException If there is an error communicating with the Database
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
     * Takes an Admin instance and inserts it into the Admin table on the Database.
     * @param admin The Admin which will be inserted into the Database.
     * @throws SQLException If there is an error working with the Database
     */
    public void insertAdmin(Admin admin) throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String insert = "INSERT INTO ADMIN(username, password, name, work_address, region) " +
                    "VALUES(?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insert);

            statement.setString(1, admin.getUsername());
            statement.setString(2, admin.getPassword());
            statement.setString(3, admin.getName());
            statement.setString(4, admin.getWorkAddress());
            statement.setString(5, admin.getRegion());
            System.out.println("Inserting new Admin -> Successful -> Rows Added: " + statement.executeUpdate());
        }

    }

    /**
     * Returns the if of the Admin with the username which matches the one given.
     * @param username The username to query
     * @return The id of the matched Admin
     * @throws SQLException If there is a problem working with the Database.
     */
    public int getAdminIdFromUsername(String username) throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "SELECT staff_id FROM ADMIN WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("staff_id");
        }
    }

    /**
     * Returns the Admin with the id which matches the one given. If no such Admin is fond,
     * null is returned.
     * @param id The id which will be matched
     * @return An Admin instance.
     * @throws SQLException If there is a problem working with the Database.
     */
    public Admin getAdminFromId(int id) throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            // SELECT * FROM ADMIN id = id;
            String query = "SELECT * FROM ADMIN WHERE staff_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a new Clinician Object with the fields from the Database
                return getAdminFromResultSet(resultSet);
            }
        }
    }

    /**
     * Returns an ArrayList of Admins from the Database.
     * @return The ArrayList of Admins from the Database
     * @throws SQLException If there is a problem working with the Database.
     */
    public ArrayList<Admin> getAllAdmins() throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            ArrayList<Admin> allAdmins = new ArrayList<>();
            String query = "SELECT * FROM ADMIN";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allAdmins.add(getAdminFromResultSet(resultSet));
            }

            return allAdmins;
        }
    }

    /**
     * Removes the given Admin from the Database by matching their ID's
     * @param admin The Admin which will be removed
     * @throws SQLException If there is a problem working with the Database.
     */
    public void removeAdmin(Admin admin) throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM ADMIN WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(update);
            statement.setString(1, admin.getUsername());
            System.out.println("Deletion of Admin: " + admin.getUsername() + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        }
    }

    /**
     * Updates the Admin on the Database with the same ID as the one given with the fields of the given Admin.
     * @param admin The given Admin
     * @throws SQLException If there is a problem working with the Database.
     */
    public void updateAdminDetails(Admin admin) throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "UPDATE ADMIN SET name = ?, work_address = ?, region = ? WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(update);

            statement.setString(1, admin.getName());
            statement.setString(2, admin.getWorkAddress());
            statement.setString(3, admin.getRegion());
            statement.setString(4, admin.getUsername());
            System.out.println("Update Admin Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());
        }

    }

}
