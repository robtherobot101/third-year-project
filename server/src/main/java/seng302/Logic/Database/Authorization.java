package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Admin;
import seng302.Model.Clinician;
import seng302.Model.User;
import seng302.Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Authorization {

    private Connection connection;
    private String currentDatabase;

    public Authorization() {
        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
        connection = databaseConfiguration.getConnection();
        currentDatabase = databaseConfiguration.getCurrentDatabase();
    }

    public User loginUser(String usernameEmail, String password) throws SQLException{

        //First needs to do a search to see if there is a unique user with the given inputs
        // SELECT * FROM USER WHERE username = usernameEmail OR email = usernameEmail AND password = password
        String query = "SELECT * FROM " + currentDatabase + ".USER WHERE (username = ? OR email = ?) AND password = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setString(1, usernameEmail);
        statement.setString(2, usernameEmail);
        statement.setString(3, password);
        ResultSet resultSet = statement.executeQuery();

        //If response is empty then return null
        if (!resultSet.next()) {
            return null;
        } else {
            //If response is not empty then get the user object from the result set
            GeneralUser generalUser = new GeneralUser();
            return generalUser.getUserFromResultSet(resultSet);
        }

    }

    public Clinician loginClinician(String username, String password) throws SQLException{
        //First needs to do a search to see if there is a unique clinician with the given inputs
        String query = "SELECT * FROM " + currentDatabase + ".CLINICIAN WHERE username = ? AND password = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, password);
        ResultSet resultSet = statement.executeQuery();

        //If response is empty then return null
        if(!resultSet.next()) {
            return null;
        } else {
            //If response is not empty then return a new Clinican Object with the fields from the database
            GeneralClinician generalClinician = new GeneralClinician();
            return generalClinician.getClinicianFromResultSet(resultSet);
        }

    }

    public Admin loginAdmin(String usernameEmail, String password) throws SQLException {
        //First needs to do a search to see if there is a unique admin with the given inputs
        String query = "SELECT * FROM " + currentDatabase + ".ADMIN WHERE username = ? AND password = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setString(1, usernameEmail);
        statement.setString(2, password);
        ResultSet resultSet = statement.executeQuery();

        //If response is empty then return null
        if(!resultSet.next()) {
            return null;
        } else {
            //If response is not empty then return a new Admin Object with the fields from the database
            GeneralAdmin generalAdmin = new GeneralAdmin();
            return generalAdmin.getAdminFromResultSet(resultSet);
        }

    }

    public void logout() {

    }
}
