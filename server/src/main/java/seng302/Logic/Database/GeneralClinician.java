package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Logic.SaltHash;
import seng302.Model.Clinician;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GeneralClinician extends DatabaseMethods {

    /**
     * Takes a resultsSet, pulls out a clinician instance, and returns it.
     *
     * @param resultSet The given resultSet
     * @return A clinician instance
     * @throws SQLException If there is a problem working with the database.
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
     * Returns the clinician from the database whose ID matches the one given.
     *
     * @param id The given id
     * @return A clinician instance with the same ID as the one given.
     * @throws SQLException If there is a problem working with the database.
     */
    public Clinician getClinicianFromId(int id) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            // SELECT * FROM CLINICIAN id = id;
            String query = "SELECT * FROM CLINICIAN JOIN ACCOUNT WHERE staff_id = id AND staff_id = ?";
            statement = connection.prepareStatement(query);

            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a new clinician Object with the fields from the database
                return getClinicianFromResultSet(resultSet);
            }
        } finally {
            close(resultSet, statement);
        }
    }

    /**
     * Returns the clinician from the database from the database whose username matches the one given.
     *
     * @param username The given username.
     * @return A clinician instance.
     * @throws SQLException If there is a problem working with the database.
     */
    public int getClinicianIdFromUsername(String username) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "SELECT id FROM ACCOUNT WHERE username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt("id");
        } finally {
            close(resultSet, statement);
        }
    }

    /**
     * Inserts the given clinician into the database.
     *
     * @param clinician The given clinician which will be inserted
     * @throws SQLException If there is a problem working with the database.
     */
    public void insertClinician(Clinician clinician) throws SQLException {
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String insertAccount = "INSERT INTO ACCOUNT(username, password) VALUES(?, ?)";
            statement = connection.prepareStatement(insertAccount);
            statement.setString(1, clinician.getUsername());
            statement.setString(2, clinician.getPassword());
            statement.executeUpdate();
            statement.close();
            String insert = "INSERT INTO CLINICIAN(name, work_address, region, staff_id) VALUES(?, ?, ?, (SELECT id FROM ACCOUNT WHERE username = ?))";
            statement = connection.prepareStatement(insert);

            statement.setString(1, clinician.getName());
            statement.setString(2, clinician.getWorkAddress());
            statement.setString(3, clinician.getRegion());
            statement.setString(4, clinician.getUsername());
            System.out.println("Inserting new clinician -> Successful -> Rows Added: " + statement.executeUpdate());
        } finally {
            close(statement);
        }
    }

    /**
     * Returns an ArrayList of all Clinicians in the database.
     *
     * @return An ArrayList of all Clinicians in the database
     * @throws SQLException If there is a problem working with the database.
     */
    public ArrayList<Clinician> getAllClinicians() throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            ArrayList<Clinician> allClinicans = new ArrayList<>();
            String query = "SELECT * FROM CLINICIAN JOIN ACCOUNT WHERE staff_id = id";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allClinicans.add(getClinicianFromResultSet(resultSet));
            }

            return allClinicans;
        } finally {
            close(resultSet, statement);
        }
    }

    /**
     * Removes the clinician from the database whose ID matches that of the clinician given.
     *
     * @param clinician The given clinician
     * @throws SQLException If there is a problem working with the database.
     */
    public void removeClinician(Clinician clinician) throws SQLException {
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "DELETE FROM ACCOUNT WHERE username = ?";
            statement = connection.prepareStatement(update);
            statement.setString(1, clinician.getUsername());
            System.out.println("Deletion of clinician: " + clinician.getUsername() + " -> Successful -> Rows Removed: " + statement.executeUpdate());
        } finally {
            close(statement);
        }
    }

    /**
     * Updates the Clincian in the database whose ID matches the one given with the fields of the clinician given.
     *
     * @param clinician   The given clinician
     * @param clinicianId The ID of the
     * @throws SQLException If there is a problem working with the database.
     */
    public void updateClinicianDetails(Clinician clinician, int clinicianId, String password) throws SQLException {
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update;
            if(password == null) {
                update = "UPDATE CLINICIAN JOIN ACCOUNT SET name = ?, work_address = ?, region = ?, username = ? WHERE staff_id = id AND staff_id = ?";
            }
            update = "UPDATE CLINICIAN JOIN ACCOUNT SET name = ?, work_address = ?, region = ?, username = ?, password = ? WHERE staff_id = id AND staff_id = ?";
            statement = connection.prepareStatement(update);
            int index = 0;

            statement.setString(++index, clinician.getName());
            statement.setString(++index, clinician.getWorkAddress());
            statement.setString(++index, clinician.getRegion());
            statement.setString(++index, clinician.getUsername());
            if(password != null) statement.setString(++index, clinician.getPassword());
            statement.setInt(++index, clinicianId);
            System.out.println("Update clinician Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());
        } finally {
            close(statement);
        }
    }

    /**
     * Update account details
     * @param id The id of the account
     * @param username The new username to associate with the account
     * @param password The new password
     */
    public void updateAccount(long id, String username, String password) throws SQLException {
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            password = SaltHash.createHash(password);
            String update = "UPDATE ACCOUNT SET username = ?, password = ? WHERE id = ? ";
            statement = connection.prepareStatement(update);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setLong(3, id);
            statement.executeUpdate();
        } finally {
            close(statement);
        }
    }

    /**
     * Update account details
     * @param id The id of the account
     * @param username The new username to associate with the account
     */
    public void updateAccount(long id, String username) throws SQLException {
        PreparedStatement statement = null;
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String update = "UPDATE ACCOUNT SET username = ? WHERE id = ? ";
            statement = connection.prepareStatement(update);
            statement.setString(1, username);
            statement.setLong(2, id);
            statement.executeUpdate();
        } finally {
            close(statement);
        }
    }
}
