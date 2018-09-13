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

public class Authorization extends DatabaseMethods {

    /**
     * method to get a password from a user with a given id
     * @param id Long the user id to get the password of
     * @return String the password of the user to check
     * @throws SQLException catch sql execution errors
     */
    public String checkPasswordUser(long id) throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "SELECT password FROM USER WHERE (id = ?)";
            statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            if (!resultSet.next()){
                return null;
            } else {
                return resultSet.getString("password");
            }
        }
        finally {
            close();
        }
    }

    /**
     * method to get a password from a user with a given id
     * @param id Long the user id to get the password of
     * @return String the password of the user to check
     * @throws SQLException catch sql execution errors
     */
    public String checkPasswordClinician(long id) throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            String query = "SELECT password FROM CLINICIAN WHERE (staff_id = ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()){
                return null;
            } else {
                return resultSet.getString("password");
            }
        }
    }


    /**
     * Returns the user with a matching username/email and password if such a user exists, otherwise returns null
     * @param usernameEmail Either a username or an email address
     * @return The matched user
     * @throws SQLException If there is an error working with the database
     */
    public User loginUser(String usernameEmail) throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            //First needs to do a search to see if there is a unique user with the given inputs
            // SELECT * FROM USER WHERE username = usernameEmail OR email = usernameEmail AND password = password
            String query = "SELECT * FROM USER WHERE (username = ? OR email = ?)";
            statement = connection.prepareStatement(query);

            statement.setString(1, usernameEmail);
            statement.setString(2, usernameEmail);
            resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then get the user object from the result set
                GeneralUser generalUser = new GeneralUser();
                return generalUser.getUserFromResultSet(resultSet);
            }
        }
        finally {
            close();
        }

    }


    /**
     * Returns the clinician with a matching username and password if such a clinician exists, otherwise returns null
     * @param username A username
     * @return The matched clinician if it was found, otherwise null
     * @throws SQLException If there is an error working with the database
     */
    public Clinician loginClinician(String username) throws SQLException{
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            //First needs to do a search to see if there is a unique clinician with the given inputs
            String query = "SELECT * FROM CLINICIAN WHERE username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a new Clinican Object with the fields from the database
                GeneralClinician generalClinician = new GeneralClinician();
                return generalClinician.getClinicianFromResultSet(resultSet);
            }
        }
        finally {
            close();
        }
    }


    /**
     * Returns the admin with a matching username and password if such a admin exists, otherwise returns null
     * @param username A username
     * @return The matched admin if it was found, otherwise null
     * @throws SQLException If there is an error working with the database
     */
    public Admin loginAdmin(String username) throws SQLException {
        try(Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            //First needs to do a search to see if there is a unique admin with the given inputs
            String query = "SELECT * FROM ADMIN WHERE username = ?";
            statement = connection.prepareStatement(query);

            statement.setString(1, username);
            resultSet = statement.executeQuery();

            //If response is empty then return null
            if (!resultSet.next()) {
                return null;
            } else {
                //If response is not empty then return a new admin Object with the fields from the database
                GeneralAdmin generalAdmin = new GeneralAdmin();
                return generalAdmin.getAdminFromResultSet(resultSet);
            }
        }
        finally {
            close();
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
            statement = connection.prepareStatement("INSERT INTO TOKEN(id, token, access_level) VALUES (?, ?, ?)");
            statement.setInt(1, id);
            statement.setString(2, token);
            statement.setInt(3, accessLevel);
            statement.execute();
        }
        finally {
            close();
        }
        return token;
    }

    /**
     * Removes the row containing the given token from the TOKEN table
     * @param token The token which will be discarded
     * @throws SQLException If there is an error communicating with the database
     */
    public void logout(String token) throws SQLException {
        try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
            statement = connection.prepareStatement("DELETE FROM TOKEN WHERE token = ?");
            statement.setString(1, token);
            statement.execute();
        }
        finally {
            close();
        }
    }
}
