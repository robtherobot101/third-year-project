package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import seng302.Logic.Database.Authorization;
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
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-Data to the runtime
     * @return JSON object containing the information of the User logging in or a message saying why it failed, very nice
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

        // Check for a User match
        try {
            currentUser = model.loginUser(usernameEmail, password);
            if (currentUser != null) {
                loginToken = model.generateToken((int) currentUser.getId(), 0);
                typeMatched = ProfileType.USER;
                System.out.println("LoginController: Logging in as User...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (loginToken == null) { //if User login was unsuccessful
            // Check for a Clinician match
            try {
                currentClinician = model.loginClinician(usernameEmail, password);
                if (currentClinician != null) {
                    loginToken = model.generateToken((int) currentClinician.getStaffID(), 1);
                    typeMatched = ProfileType.CLINICIAN;
                    System.out.println("LoginController: Logging in as Clinician...");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (loginToken == null) { //if User login and Clinician login was unsuccessful
            // Check for an Admin match
            try {
                currentAdmin = model.loginAdmin(usernameEmail, password);
                if (currentAdmin != null) {
                    loginToken = model.generateToken((int) currentAdmin.getStaffID(), 2);
                    typeMatched = ProfileType.ADMIN;
                    System.out.println("LoginController: Logging in as Admin...");
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
     * method to handle logging out a User
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-Data to the runtime
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
