package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import seng302.Logic.Database.GeneralAdmin;
import seng302.Model.Admin;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.ArrayList;

public class AdminController {
    private GeneralAdmin model;

    /**
     * constructor method to create a new admin controller object
     * that handles all admin related requests
     */
    public AdminController() {
        model = new GeneralAdmin();
    }

    /**
     * Retrieves an admin object from the HTTP request ":id" param
     * @param request Spark HTTP request obj
     * @param response Spark HTTP response obj
     * @return A valid admin obj if the admin exists otherwise return null
     */
    private Admin queryAdmin(Request request, Response response) {
        int requestedAdminId = Integer.parseInt(request.params(":id"));

        Admin queriedAdmin;
        try {
            queriedAdmin = model.getAdminFromId(requestedAdminId);
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            response.body("Internal server error");
            return null;
        }

        if (queriedAdmin == null) {
            Server.getInstance().log.warn(String.format("No user of ID: %d found", requestedAdminId));
            response.status(404);
            response.body("Not found");
            return null;
        }
        return queriedAdmin;
    }

    /**
     * method to handle the request to get all the admins from the database
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json object containing all admins and their information, or an error message
     */
    public String getAllAdmins(Request request, Response response) {
        ArrayList<Admin> queriedAdmins;
        try {
            queriedAdmins = model.getAllAdmins();
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedAdmins = gson.toJson(queriedAdmins);

        response.type("application/json");
        response.status(200);
        return serialQueriedAdmins;
    }

    /**
     * method to handle the request to create a new admin on the database
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether the operation was completed successfully
     */
    public String addAdmin(Request request, Response response) {

        Gson gson = new Gson();
        Admin receivedAdmin;

        // Attempt to parse received JSON
        try {
            receivedAdmin = gson.fromJson(request.body(), Admin.class);
        } catch (JsonSyntaxException jse) {
            Server.getInstance().log.warn(String.format("Malformed JSON:\n%s", request.body()));
            response.status(400);
            return "Bad Request";
        }
        if (receivedAdmin == null) {
            Server.getInstance().log.warn("Empty request body");
            response.status(400);
            return "Missing admin Body";
        } else {
            //TODO make model.insertAdmin return token
            try {
                model.insertAdmin(receivedAdmin);
                response.status(201);
                return "placeholder token";
            } catch (SQLException e) {
                Server.getInstance().log.error(e.getMessage());
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    /**
     * method to handle the request to get a specific admin and all its details
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json object containing all the information of the requested admin
     */
    public String getAdmin(Request request, Response response) {
        Admin queriedAdmin = queryAdmin(request, response);

        if (queriedAdmin == null) {
            return response.body();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedAdmin = gson.toJson(queriedAdmin);

        response.type("application/json");
        response.status(200);
        return serialQueriedAdmin;
    }

    /**
     * method to handle the request to edit an existing admin in the database
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether the operation was completed successfully
     */
    public String editAdmin(Request request, Response response) {
        Admin queriedAdmin = queryAdmin(request, response);

        if (queriedAdmin == null) {
            return response.body();
        }

        Gson gson = new Gson();

        Admin receivedAdmin = gson.fromJson(request.body(), Admin.class);
        if (receivedAdmin == null) {
            response.status(400);
            return "Missing admin Body";
        } else {
            try {
                model.updateAdminDetails(receivedAdmin);
                response.status(201);
                return "ADMIN SUCCESSFULLY UPDATED";
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    /**
     * method to handle the request to deleted an admin
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether the operation was completed successfully
     */
    public String deleteAdmin(Request request, Response response) {
        Admin queriedAdmin = queryAdmin(request, response);
        System.out.println(queriedAdmin.getUsername() + queriedAdmin.getStaffID());
        if (queriedAdmin == null) {
            return response.body();
        }

        try {
            model.removeAdmin(queriedAdmin);
            response.status(201);
            return "ADMIN DELETED";
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        }
    }
}

