package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import seng302.Logic.Database.Administration;
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

    public String login(Request request, Response response) {

        String usernameEmail = request.queryParams("usernameEmail");
        String password = request.queryParams("password");
        System.out.println(usernameEmail);
        System.out.println(password);
        if(usernameEmail == null || password == null) {
            response.status(400);
            return "Missing Parameters";
        }

        boolean identificationMatched = false;
        ProfileType typeMatched = null;

        // Check for a user match
        User currentUser = null;
        try {
            currentUser = model.loginUser(usernameEmail, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(currentUser != null) {
            typeMatched = ProfileType.USER;
            identificationMatched = true;
            System.out.println("LoginController: Logging in as user...");
        }


        // Check for a clinician match
        Clinician currentClinician = null;
        try {
            currentClinician = model.loginClinician(usernameEmail, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(currentClinician != null) {
            typeMatched = ProfileType.CLINICIAN;
            identificationMatched = true;
            System.out.println("LoginController: Logging in as clinician...");
        }

        // Check for an admin match
        Admin currentAdmin = null;
        try {
            currentAdmin = model.loginAdmin(usernameEmail, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Do a db search here
        if(currentAdmin != null) {
            typeMatched = ProfileType.ADMIN;
            identificationMatched = true;
            System.out.println("LoginController: Logging in as admin...");
        }


        if (identificationMatched) {

            switch (typeMatched) {
                case ADMIN:
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String serialQueriedAdmin = gson.toJson(currentAdmin);
                    response.type("application/json");
                    response.status(200);
                    response.header("token", "HARRO");
                    return serialQueriedAdmin;
                case CLINICIAN:
                    gson = new GsonBuilder().setPrettyPrinting().create();
                    String serialQueriedClinician = gson.toJson(currentClinician);
                    response.type("application/json");
                    response.status(200);
                    response.header("token", "HARRO");
                    return serialQueriedClinician;
                case USER:
                    gson = new GsonBuilder().setPrettyPrinting().create();
                    String serialQueriedUser = gson.toJson(currentUser);
                    response.type("application/json");
                    response.status(200);
                    response.header("token", "HARRO");
                    return serialQueriedUser;
            }

        } else {
            response.status(401);
            return "Invalid Username and Password";
        }
        response.status(500);
        return "Server Failure";

   }

    public String logout(Request request, Response response) {
        return "Hello";
    }
}
