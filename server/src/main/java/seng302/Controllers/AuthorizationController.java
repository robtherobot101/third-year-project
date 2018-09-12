package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import seng302.Logic.Database.Authorization;
import seng302.Logic.SaltHash;
import seng302.Model.Admin;
import seng302.Model.Attribute.ProfileType;
import seng302.Model.Clinician;
import seng302.Model.User;
import spark.Request;
import spark.Response;

import java.sql.SQLException;

public class AuthorizationController {

    Authorization model = new Authorization();


    /**
     * method to handle the login requests
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return JSON object containing the information of the user logging in or a message saying why it failed, very nice
     */
    public String login(Request request, Response response) {

        String usernameEmail = request.queryParams("usernameEmail");
        String password = request.queryParams("password");
        if(usernameEmail == null || password == null) {
            response.status(400);
            return "Missing Parameters";
        }

        ProfileType typeMatched = null;
        String loginToken = null;

        User currentUser = null;
        Clinician currentClinician = null;
        Admin currentAdmin = null;

        // Check for a user match
        try {
            currentUser = model.loginUser(usernameEmail);
            if (currentUser != null) {
                if (SaltHash.checkHash(password, currentUser.getPassword())) {
                    loginToken = model.generateToken((int) currentUser.getId(), 0);
                    typeMatched = ProfileType.USER;
                    System.out.println("LoginController: Logging in as user...");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (loginToken == null) { //if user login was unsuccessful
            // Check for a clinician match
            try {
                currentClinician = model.loginClinician(usernameEmail);
                if (currentClinician != null) {
                    if (SaltHash.checkHash(password, currentClinician.getPassword())) {
                        loginToken = model.generateToken((int) currentClinician.getStaffID(), 1);
                        typeMatched = ProfileType.CLINICIAN;
                        System.out.println("LoginController: Logging in as clinician...");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (loginToken == null) { //if user login and clinician login was unsuccessful
            // Check for an admin match
            try {
                currentAdmin = model.loginAdmin(usernameEmail);
                if (currentAdmin != null) {
                    if (SaltHash.checkHash(password, currentAdmin.getPassword())) {
                        loginToken = model.generateToken((int) currentAdmin.getStaffID(), 2);
                        typeMatched = ProfileType.ADMIN;
                        System.out.println("LoginController: Logging in as admin...");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (typeMatched != null) {
            switch (typeMatched) {
                case ADMIN:
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String serialQueriedAdmin = gson.toJson(currentAdmin);
                    response.type("application/json");
                    response.status(200);
                    response.header("token", loginToken);
                    return serialQueriedAdmin;
                case CLINICIAN:
                    gson = new GsonBuilder().setPrettyPrinting().create();
                    String serialQueriedClinician = gson.toJson(currentClinician);
                    response.type("application/json");
                    response.status(200);
                    response.header("token", loginToken);
                    return serialQueriedClinician;
                case USER:
                    gson = new GsonBuilder().setPrettyPrinting().create();
                    String serialQueriedUser = gson.toJson(currentUser);
                    response.type("application/json");
                    response.status(200);
                    response.header("token", loginToken);
                    return serialQueriedUser;
            }

        } else {
            response.status(401);
            return "Invalid Username and Password";
        }
        response.status(500);
        return "Server Failure";
    }

    /**
     * method to handle logging out a user
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String containing information whether the logout was successful
     */
    public String logout(Request request, Response response) {
        try {
            String token = request.headers("token");
            if (token == null) {
                response.status(400);
                System.out.println("Received logout with no token");
                return "Invalid: logout with no token";
            } else {
                model.logout(token);
                response.status(200);
                return "Logged out successfully";
            }
        } catch (SQLException e) {
            response.status(400);
            System.out.println("Received logout with no token");
            return "Invalid: logout with no token";
        }
    }
}
