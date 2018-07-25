package seng302.Logic.Database;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Admin;
import seng302.Model.Clinician;
import seng302.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Authorization {

    public User loginUser(String usernameEmail, String password) throws SQLException{

        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            //First needs to do a search to see if there is a unique user with the given inputs
            // SELECT * FROM USER WHERE username = usernameEmail OR email = usernameEmail AND password = password
            String query = "SELECT * FROM USER WHERE (username = ? OR email = ?) AND password = ?";
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

    }

    public Clinician loginClinician(String username, String password) throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            //First needs to do a search to see if there is a unique clinician with the given inputs
            String query = "SELECT * FROM CLINICIAN WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a new Clinican Object with the fields from the database
                GeneralClinician generalClinician = new GeneralClinician();
                return generalClinician.getClinicianFromResultSet(resultSet);
            }
        }

    }

    public Admin loginAdmin(String usernameEmail, String password) throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            //First needs to do a search to see if there is a unique admin with the given inputs
            String query = "SELECT * FROM ADMIN WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, usernameEmail);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a new Admin Object with the fields from the database
                GeneralAdmin generalAdmin = new GeneralAdmin();
                return generalAdmin.getAdminFromResultSet(resultSet);
            }
        }

    }

    /**
     * Adds a token to the database for a new user login.
     *
     * @param id The id of the user/admin/clinician that is logging in
     * @param accessLevel The access level of the user
     * @return The token
     * @throws SQLException If there is an error communicating with the database
     */
    public String generateToken(int id, int accessLevel) throws SQLException {
        String token = UUID.randomUUID().toString();
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO TOKEN VALUES (?, ?, ?)");
            statement.setInt(1, id);
            statement.setString(2, token);
            statement.setInt(3, accessLevel);
            statement.execute();
        }
        return token;
    }

    public void logout(String token) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM TOKEN WHERE token = ?");
            statement.setString(1, token);
            statement.execute();
        }
    }
}
