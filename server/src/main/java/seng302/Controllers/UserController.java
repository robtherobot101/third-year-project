package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import seng302.Logic.Database.GeneralUser;
import seng302.Model.Clinician;
import seng302.Model.User;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.*;

public class UserController {
    private GeneralUser model;

    public UserController() {
        model = new GeneralUser();
    }


    public String getUsers(Request request, Response response) {
        Map<String, String> params = new HashMap<String, String>();
        List<String> possibleParams = new ArrayList<String>(Arrays.asList(
                "name","password","userType","age","gender","region","organ",
                "startIndex","count"
        ));
        for(String param:possibleParams){
            if(request.queryParams(param) != null){
                params.put(param,request.queryParams(param));
            }
        }
        System.out.println("Params: "+params);


        ArrayList<User> queriedUsers;
        try {
            queriedUsers = model.getUsers(params);
        } catch (SQLException e) {
            Server.log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedUsers = gson.toJson(queriedUsers);

        response.type("application/json");
        response.status(200);
        return serialQueriedUsers;
    }

    /**
     * Checks for the validity of the request ID, and returns a user obj
     * @param request HTTP request
     * @param response HTTP response
     * @return A valid User object if the user exists otherwise return null
     */
    private User queryUser(Request request, Response response) {
        int requestedUserId = Integer.parseInt(request.params(":id"));

        User queriedUser;
        try {
            queriedUser = model.getUserFromId(requestedUserId);
        } catch (SQLException e) {
            Server.log.error(e.getMessage());
            response.status(500);
            response.body("Internal server error");
            return null;
        }

        if (queriedUser == null) {
            Server.log.warn(String.format("No user of ID: %d found", requestedUserId));
            response.status(404);
            response.body("Not found");
            return null;
        }
        return queriedUser;
    }

    public String addUser(Request request, Response response) {
        Gson gson = new Gson();
        User receivedUser;

        // Attempt to parse received JSON
        try {
            receivedUser = gson.fromJson(request.body(), User.class);
        } catch (JsonSyntaxException jse) {
            Server.log.warn(String.format("Malformed JSON:\n%s", request.body()));
            response.status(400);
            return "Bad Request";
        }

        if (receivedUser == null) {
            Server.log.warn("Empty request body");
            response.status(400);
            return "Missing User Body";
        } else {
            //TODO make model.insertUser return token
            try {
                model.insertUser(receivedUser);
                response.status(201);
                return "placeholder token";
            } catch (SQLException e) {
                Server.log.error(e.getMessage());
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    public String getUser(Request request, Response response) {
        User queriedUser = queryUser(request, response);

        if (queriedUser == null) {
            return response.body();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedUser = gson.toJson(queriedUser);

        response.type("application/json");
        response.status(200);
        return serialQueriedUser;
    }

    public String editUser(Request request, Response response) {
        User queriedUser = queryUser(request, response);

        if (queriedUser == null) {
            return response.body();
        }

        Gson gson = new Gson();

        User receivedUser = gson.fromJson(request.body(), User.class);
        if (receivedUser == null) {
            response.status(400);
            return "Missing User Body";
        } else {
            try {
                model.updateUserAttributes(receivedUser, Integer.parseInt(request.params(":id")));
                response.status(201);
                return "USER SUCCESSFULLY UPDATED";
            } catch (SQLException e) {
                Server.log.error(e.getMessage());
                response.status(500);
                return "Internal Server Error";
            }
        }
    }

    public String deleteUser(Request request, Response response) {
        User queriedUser = queryUser(request, response);

        if (queriedUser == null) {
            return response.body();
        }

        try {
            model.removeUser(queriedUser);
            response.status(201);
            return "USER DELETED";
        } catch (SQLException e) {
            Server.log.error(e.getMessage());
            response.status(500);
            return "Internal Server Error";
        }
    }
}
