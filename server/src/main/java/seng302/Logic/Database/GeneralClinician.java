package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Clinician;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GeneralClinician {

    /**
     * Takes a resultsSet, pulls out a Clinician instance, and returns it.
     * @param resultSet The given resultSet
     * @return A Clinician instance
     * @throws SQLException If there is a problem working with the Database.
     */
    public Clinician getClinicianFromResultSet(ResultSet resultSet) throws SQLException {
        Clinician clinician = new Clinician(
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("name")
        );
        clinician.setWorkAddress(resultSet.getString("work_address"));
        clinician.setRegion(resultSet.getString("region"));
        clinician.setStaffID(resultSet.getInt("staff_id"));

        return clinician;
    }

    /**
     * Returns the Clinician from the Database whose ID matches the one given.
     * @param id The given id
     * @return A Clinician instance with the same ID as the one given.
     * @throws SQLException If there is a problem working with the Database.
     */
    public Clinician getClinicianFromId(int id) throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            // SELECT * FROM CLINICIAN id = id;
            String query = "SELECT * FROM CLINICIAN WHERE staff_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a new Clinician Object with the fields from the Database
                return getClinicianFromResultSet(resultSet);
            }
        }
    }

    /**
     * Returns the Clinician from the Database from the Database whose username matches the one given.
     * @param username The given username.
     * @return A Clinician instance.
     * @throws SQLException If there is a problem working with the Database.
     */
    public int getClinicianIdFromUsername(String username) throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "SELECT staff_id FROM CLINICIAN WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("staff_id");
        }
    }

    /**
     * Inserts the given Clinician into the Database.
     * @param clinician The given Clinician which will be inserted
     * @throws SQLException If there is a problem working with the Database.
     */
    public void insertClinician(Clinician clinician) throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String insert = "INSERT INTO CLINICIAN(username, password, name, work_address, region) " +
                    "VALUES(?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insert);

            statement.setString(1, clinician.getUsername());
            statement.setString(2, clinician.getPassword());
            statement.setString(3, clinician.getName());
            statement.setString(4, clinician.getWorkAddress());
            statement.setString(5, clinician.getRegion());
            System.out.println("Inserting new Clinician -> Successful -> Rows Added: " + statement.executeUpdate());
        }

    }

    /**
     * Returns an ArrayList of all Clinicians in the Database.
     * @return An ArrayList of all Clinicians in the Database
     * @throws SQLException If there is a problem working with the Database.
     */
    public ArrayList<Clinician> getAllClinicians() throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            ArrayList<Clinician> allClinicans = new ArrayList<>();
            String query = "SELECT * FROM CLINICIAN";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allClinicans.add(getClinicianFromResultSet(resultSet));
            }

            return allClinicans;
        }
    }

    /**
     * Removes the Clinician from the Database whose ID matches that of the Clinician given.
     * @param clinician The given Clinician
     * @throws SQLException If there is a problem working with the Database.
     */
    public void removeClinician(Clinician clinician) throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM CLINICIAN WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(update);
            statement.setString(1, clinician.getUsername());
            System.out.println("Deletion of Clinician: " + clinician.getUsername() + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        }
    }

    /**
     * Updates the Clincian in the Database whose ID matches the one given with the fields of the Clinician given.
      *@param clinician The given Clinician
     * @param clinicianId The ID of the
     * @throws SQLException If there is a problem working with the Database.
     */
    public void updateClinicianDetails(Clinician clinician, int clinicianId) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "UPDATE CLINICIAN SET name = ?, work_address = ?, region = ?, username = ?, password = ? WHERE staff_id = ?";
            PreparedStatement statement = connection.prepareStatement(update);

            statement.setString(1, clinician.getName());
            statement.setString(2, clinician.getWorkAddress());
            statement.setString(3, clinician.getRegion());
            statement.setString(4, clinician.getUsername());
            statement.setString(5, clinician.getPassword());
            statement.setInt(6, clinicianId);
            System.out.println("Update Clinician Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());
        }
    }
}
