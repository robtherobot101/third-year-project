package seng302.Controllers;

import seng302.Config.DatabaseConfiguration;
import seng302.Model.Attribute.ProfileType;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static spark.Spark.halt;

/**
 * Utility class for profiles
 */
public class ProfileUtils {



    /**
     * Checks the authorisation level of a token.
     *
     * @param token The token to check for
     * @return The authorisation level of the token, or -1 if the token is not found or the database could not be contacted
     */
    public int checkToken(String token) {
        try {
            try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM TOKEN WHERE token != 'masterToken' AND date_time < UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 1 DAY))");
                statement.execute();

                statement = connection.prepareStatement(
                        "SELECT access_level FROM TOKEN WHERE token = ?");
                statement.setString(1, token);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    statement = connection.prepareStatement(
                            "UPDATE TOKEN SET date_time = NOW() WHERE token = ?");
                    statement.setString(1, token);
                    statement.execute();
                    return resultSet.getInt("access_level");
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            return -1;
        } catch (NullPointerException npe) {
            return -1;
        }
    }

    /**
     * Checks the id associated with a token.
     *
     * @param token The token to check for
     * @param profileType The type of profile associated with that token
     * @param id The id to check for
     * @return Whether the token is associated with that id or false if the database could not be contacted
     */
    public boolean checkTokenId(String token, ProfileType profileType, int id) {
        try {
            try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT access_level FROM TOKEN WHERE token = ? AND access_level = ? AND id = ?");
                statement.setString(1, token);
                switch (profileType) {
                    case USER:
                        statement.setInt(2, 0);
                        break;
                    case CLINICIAN:
                        statement.setInt(2, 1);
                        break;
                    case ADMIN:
                        statement.setInt(2, 2);
                        break;
                }
                statement.setInt(3, id);

                ResultSet resultSet = statement.executeQuery();
                return resultSet.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Checks if the requester is authorized to access all users.
     *
     * @param request Spark HTTP request obj
     * @param response Spark HTTP response obj
     * @return Whether they are authorised
     */
    public boolean hasAccessToAllUsers(Request request, Response response) {
        String failure = "Unauthorised: access denied to all user access request ";

        String token = request.headers("token");
        int accessLevel = checkToken(token);
        if (accessLevel == -1) {
            Server.getInstance().log.warn(failure + "(token not found)");
            halt(401, "Unauthorized");
            return false; //Token was not found
        } else if (accessLevel == 0) {
            Server.getInstance().log.warn(failure + "because they only have single user level access");
            halt(401, "Unauthorized");
            return false; //Token was not found
        } else {
            return true; //User has clinician or admin level access
        }
    }

    /**
     * Checks if the requester is authorized to access/modify a user.
     *
     * @param request Spark HTTP request obj
     * @param response Spark HTTP response obj
     * @return Whether they are authorised
     */
    public boolean hasUserLevelAccess(Request request, Response response) {
        String failure = "Unauthorised: access denied to single user access ";

        String token = request.headers("token");
        int accessLevel = checkToken(token);
        if (accessLevel == -1) {
            Server.getInstance().log.warn(failure + "(token not found)");
            halt(401, "Unauthorized");
            return false; //Token was not found
        } else if (accessLevel == 0) {
            int id = getId(request.params(":id"));
            if (id == -1) {
                Server.getInstance().log.warn(failure + "(invalid id supplied)");
                halt(401, "Unauthorized");
                return false;
            }
            if (checkTokenId(token, ProfileType.USER, id)) {
                return true; //User is logged on and supplied their token
            } else {
                Server.getInstance().log.warn(failure + "(token does not match user id)");
                halt(401, "Unauthorized");
                return false;
            }
        } else {
            return true; //User has clinician or admin level access
        }
    }

    /**
     * Checks if the requester is authorized to access/modify a clinician.
     *
     * @param request Spark HTTP request obj
     * @param response Spark HTTP response obj
     * @return Whether they are authorised
     */
    public boolean hasClinicianLevelAccess(Request request, Response response) {
        String failure = "Unauthorised: access denied to single clinician access ";
        String token = request.headers("token");

        int accessLevel = checkToken(token);
        if (accessLevel == -1) {
            Server.getInstance().log.warn(failure + "(token not found).");
            halt(401, "Unauthorized");
            return false; //Token was not found
        } else if (accessLevel == 0) {
            Server.getInstance().log.warn(failure + "(access level too low)");
            halt(401, "Unauthorized");
            return false;
        } else if (accessLevel == 1) {
            int id = getId(request.params(":id"));
            if (id == -1) {
                Server.getInstance().log.warn(failure + "(invalid id supplied).");
                halt(401, "Unauthorized");
                return false;
            }
            if (checkTokenId(token, ProfileType.CLINICIAN, id)) {
                return true; //User is logged on and supplied their token
            } else {
                Server.getInstance().log.warn(failure + "(token does not match clinician id)");
                halt(401, "Unauthorized");
                return false;
            }
        } else {
            return true; //User has clinician or admin level access
        }
    }

    /**
     * Checks if the requester is authorized to access all users.
     *
     * @param request Spark HTTP request obj
     * @param response Spark HTTP response obj
     * @return Whether they are authorised
     */
    public boolean hasAdminAccess(Request request, Response response) {
        String failure = "Unauthorised: access denied to admin level request ";

        String token = request.headers("token");
        int accessLevel = checkToken(token);
        if (accessLevel == -1) {
            Server.getInstance().log.warn(failure + "(token not found)");
            halt(401, "Unauthorized");
            return false; //Token was not found
        } else if (accessLevel == 0) {
            Server.getInstance().log.warn(failure + "because they only have single user level access");
            halt(401, "Unauthorized");
            return false; //Token was not found
        } else if (accessLevel == 1) {
            Server.getInstance().log.warn(failure + "because they only have single clinician level access");
            halt(401, "Unauthorized");
            return false; //Token was not found
        } else {
            return true; //User has admin level access
        }
    }

    /**
     * Checks the validity of the ":id" HTTP request param
     * @param request Spark HTTP request obj
     * @param response Spark HTTP response obj
     * @return True if ID is valid, false otherwise
     */
    public boolean checkId(Request request, Response response) {
        return getId(request.params(":id")) != -1;
    }

    /**
     * Checks whether a raw (string) id is a valid id.
     *
     * @param rawId The raw id
     * @return The id if it is valid, or -1 otherwise
     */
    private int getId(String rawId) {
        int id;
        System.out.println("CHECK ID CALLED");

        try {
            id = Integer.parseInt(rawId);
        } catch (NumberFormatException nfe) {
            id = -1;
        }
        if (id == -1) {
            Server.getInstance().log.warn(String.format("Invalid ID: %s", rawId));
            halt(400, "Bad Request");
            return -1;
        } else {
            return id;
        }
    }

    /**
     * Checks whether the query param identifier is unique against all users, clinicians, and admins.
     *
     * @param request Spark HTTP request obj
     * @param response Spark HTTP response obj
     * @return Whether the identifier is unique
     */
    public boolean isUniqueIdentifier(Request request, Response response) {
        String usernameEmail = request.queryParams("usernameEmail");
        if (usernameEmail == null || usernameEmail.isEmpty()) {
            Server.getInstance().log.warn("Received unique identifier request that did not contain an identifier to check.");
            halt(400, "Bad Request");
            return false;
        }

        try {
            try (Connection connection = DatabaseConfiguration.getInstance().getConnection()) {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM USER WHERE username = ? OR email = ?");
                statement.setString(1, usernameEmail);
                statement.setString(2, usernameEmail);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    response.status(200);
                    return false;
                }
                statement = connection.prepareStatement("SELECT * FROM CLINICIAN WHERE username = ?");
                statement.setString(1, usernameEmail);
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    response.status(200);
                    return false;
                }
                statement = connection.prepareStatement("SELECT * FROM ADMIN WHERE username = ?");
                statement.setString(1, usernameEmail);
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    response.status(200);
                    return false;
                }
                response.status(200);
                return true;
            }
        } catch (SQLException e) {
            halt(500, "Internal server error");
            return false;
        }
    }
}
