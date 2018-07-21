package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GeneralAdmin {

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
                //If response is not empty then return a new Clinician Object with the fields from the database
                return getAdminFromResultSet(resultSet);
            }
        }
    }

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

    public void removeAdmin(Admin admin) throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM ADMIN WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(update);
            statement.setString(1, admin.getUsername());
            System.out.println("Deletion of Admin: " + admin.getUsername() + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        }
    }

    public void updateAdminDetails(Admin admin) throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "UPDATE ADMIN SET name = ?, work_address = ? WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(update);

            statement.setString(1, admin.getName());
            statement.setString(2, admin.getWorkAddress());
            // statement.setString(3, admin.getegion()); -- No Region for an Admin!
            statement.setString(3, admin.getUsername());
            System.out.println("Update Admin Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());
        }

    }

}
