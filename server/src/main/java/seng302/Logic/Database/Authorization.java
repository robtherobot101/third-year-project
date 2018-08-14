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

    /**
     * Returns the User with a matching username/email and password if such a User exists, otherwise returns null
     * @param usernameEmail Either a username or an email address
     * @param password A password
     * @return The matched User
     * @throws SQLException If there is an error working with the Database
     */
    public User loginUser(String usernameEmail, String password) throws SQLException{

        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            //First needs to do a search to see if there is a unique User with the given inputs
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
                //If response is not empty then get the User object from the result set
                GeneralUser generalUser = new GeneralUser();
                return generalUser.getUserFromResultSet(resultSet);
            }
        }

    }


    /**
     * Returns the Clinician with a matching username and password if such a Clinician exists, otherwise returns null
     * @param username A username
     * @param password A password
     * @return The matched Clinician if it was found, otherwise null
     * @throws SQLException If there is an error working with the Database
     */
    public Clinician loginClinician(String username, String password) throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            //First needs to do a search to see if there is a unique Clinician with the given inputs
            String query = "SELECT * FROM CLINICIAN WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a new Clinican Object with the fields from the Database
                GeneralClinician generalClinician = new GeneralClinician();
                return generalClinician.getClinicianFromResultSet(resultSet);
            }
        }

    }


    /**
     * Returns the Admin with a matching username and password if such a Admin exists, otherwise returns null
     * @param username A username
     * @param password A password
     * @return The matched Admin if it was found, otherwise null
     * @throws SQLException If there is an error working with the Database
     */
    public Admin loginAdmin(String username, String password) throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            //First needs to do a search to see if there is a unique Admin with the given inputs
            String query = "SELECT * FROM ADMIN WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a new Admin Object with the fields from the Database
                GeneralAdmin generalAdmin = new GeneralAdmin();
                return generalAdmin.getAdminFromResultSet(resultSet);
            }
        }
    }

    /**
     * Adds a token to the Database for a new User login.
     *
     * @param id The id of the User/Admin/Clinician that is logging in
     * @param accessLevel The access level of the User
     * @return The token
     * @throws SQLException If there is an error communicating with the Database
     */
    public String generateToken(int id, int accessLevel) throws SQLException {
        String token = UUID.randomUUID().toString();
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO TOKEN(id, token, access_level) VALUES (?, ?, ?)");
            statement.setInt(1, id);
            statement.setString(2, token);
            statement.setInt(3, accessLevel);
            statement.execute();
        }
        return token;
    }

    /**
     * Removes the row containing the given token from the TOKEN table
     * @param token The token which will be discarded
     * @throws SQLException If there is an error communicating with the Database
     */
    public void logout(String token) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM TOKEN WHERE token = ?");
            statement.setString(1, token);
            statement.execute();
        }
    }
}
