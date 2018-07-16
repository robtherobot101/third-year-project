package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Clinician;
import seng302.Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GeneralClinician {

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

    public Clinician getClinicianFromId(int id) throws SQLException {
        // SELECT * FROM CLINICIAN id = id;
        String query = "SELECT * FROM CLINICIAN WHERE staff_id = ?";
        PreparedStatement statement = DatabaseConfiguration.getInstance().getConnection().prepareStatement(query);

        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        //If response is empty then return null
        if(!resultSet.next()) {
            return null;
        } else {
            //If response is not empty then return a new Clinician Object with the fields from the database
            return getClinicianFromResultSet(resultSet);
        }

    }

    public void insertClinician(Clinician clinician) throws SQLException {
        String insert = "INSERT INTO CLINICIAN(username, password, name, work_address, region) " +
                "VALUES(?, ?, ?, ?, ?)";
        PreparedStatement statement = DatabaseConfiguration.getInstance().getConnection().prepareStatement(insert);

        statement.setString(1, clinician.getUsername());
        statement.setString(2, clinician.getPassword());
        statement.setString(3, clinician.getName());
        statement.setString(4, clinician.getWorkAddress());
        statement.setString(5, clinician.getRegion());
        System.out.println("Inserting new Clinician -> Successful -> Rows Added: " + statement.executeUpdate());

    }

    public ArrayList<Clinician> getAllClinicians() throws SQLException{
        ArrayList<Clinician> allClinicans = new ArrayList<>();
        String query = "SELECT * FROM CLINICIAN";
        PreparedStatement statement = DatabaseConfiguration.getInstance().getConnection().prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()) {
            allClinicans.add(getClinicianFromResultSet(resultSet));
        }

        return allClinicans;
    }

    public void removeClinician(Clinician clinician) throws SQLException {
        String update = "DELETE FROM CLINICIAN WHERE username = ?";
        PreparedStatement statement = DatabaseConfiguration.getInstance().getConnection().prepareStatement(update);
        statement.setString(1, clinician.getUsername());
        System.out.println("Deletion of Clinician: " + clinician.getUsername() + " -> Successful -> Rows Removed: " + statement.executeUpdate());
    }

    public void updateClinicianDetails(Clinician clinician, int clinicianId) throws SQLException {
        String update = "UPDATE CLINICIAN SET name = ?, work_address = ?, region = ?, username = ?, password = ? WHERE staff_id = ?";
        PreparedStatement statement = DatabaseConfiguration.getInstance().getConnection().prepareStatement(update);

        statement.setString(1, clinician.getName());
        statement.setString(2, clinician.getWorkAddress());
        statement.setString(3, clinician.getRegion());
        statement.setString(4, clinician.getUsername());
        statement.setString(5, clinician.getPassword());
        statement.setInt(6, clinicianId);
        System.out.println("Update Clinician Attributes -> Successful -> Rows Updated: " + statement.executeUpdate());

    }
}
