package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import seng302.Logic.Database.GeneralClinician;
import seng302.Logic.SaltHash;
import seng302.Model.Clinician;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ClinicianController {
    private GeneralClinician model;

    /**
     * method to handle the request processing related to clinicians
     */
    public ClinicianController() {
        model = new GeneralClinician();
    }

    /**
     * Retrieves a clinician object from the HTTP request ":id" param
     * @param request Spark HTTP request obj
     * @param response Spark HTTP response obj
     * @return A valid clinician obj if the clinician exists otherwise return null
     */
    private Clinician queryClinician(Request request, Response response) {
        int requestedClinicianId = Integer.parseInt(request.params(":id"));

        Clinician queriedClinician;
        try {
            queriedClinician = model.getClinicianFromId(requestedClinicianId);
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            response.body("Internal server error");
            return null;
        }

        if (queriedClinician == null) {
            Server.getInstance().log.warn(String.format("No clinician of ID: %d found", requestedClinicianId));
            response.status(404);
            response.body("Not found");
            return null;
        }
        return queriedClinician;
    }

    /**
     * method to get all registered clinicans
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return JSON object containing all the clinicians and their information
     */
    public String getAllClinicians(Request request, Response response) {
        ArrayList<Clinician> queriedClinicians;
        try {
            queriedClinicians = model.getAllClinicians();
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedClinicians = gson.toJson(queriedClinicians);

        response.type("application/json");
        response.status(200);
        return serialQueriedClinicians;
    }

    /**
     * method to handle the request to create a new clinician
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String output whether the request was processed correctly or not
     */
    public String addClinician(Request request, Response response) {
        Gson gson = new Gson();
        Clinician receivedClinician;

        // Attempt to executeFile received JSON
        try {
            receivedClinician = gson.fromJson(request.body(), Clinician.class);
        } catch (JsonSyntaxException jse) {
            Server.getInstance().log.warn(String.format("Malformed JSON:\n%s", request.body()));
            response.status(400);
            return "Bad Request";
        }

        if (receivedClinician == null) {
            Server.getInstance().log.warn("Empty request body");
            response.status(400);
            return "Missing clinician Body";
        } else {
            try {
                receivedClinician.setPassword(SaltHash.createHash(receivedClinician.getPassword()));
                model.insertClinician(receivedClinician);
                response.status(201);
                return "success";
            } catch (SQLException e) {
                Server.getInstance().log.error(e.getMessage());
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    /**
     * method to get a specific clinician
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return JSON object containing the requested clinicians information
     */
    public String getClinician(Request request, Response response) {
        Clinician queriedClinician = queryClinician(request, response);

        if (queriedClinician == null) {
            return response.body();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedClinician = gson.toJson(queriedClinician);

        response.type("application/json");
        response.status(200);
        return serialQueriedClinician;
    }

    /**
     * method to edit a clinicians details
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String containing whether the request was processed correctly or not
     */
    public String editClinician(Request request, Response response) {
        Clinician queriedClinician = queryClinician(request, response);

        if (queriedClinician == null) {
            return response.body();
        }

        Gson gson = new Gson();

        Clinician receivedClinician = gson.fromJson(request.body(), Clinician.class);
        String password = null;
        if (receivedClinician.getPassword() != null) {
            password = receivedClinician.getPassword();
            password = SaltHash.createHash(password);
        }
        if (receivedClinician == null) {
            response.status(400);
            return "Missing clinician Body";
        } else {
            try {
                model.updateClinicianDetails(receivedClinician, Integer.parseInt(request.params(":id")), password);
                response.status(201);
                return "CLINICIAN SUCCESSFULLY UPDATED";
            } catch (SQLException e) {
                Server.getInstance().log.error(e.getMessage());
                response.status(500);
                return "Internal Server Error";
            }
        }
    }

    /**
     * method to process the editing of a specific account
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether the editing of the user was successful or not
     */
    public String editAccount(Request request, Response response) {
        Map content = new Gson().fromJson(request.body(), Map.class);
        try {
            if (!content.keySet().containsAll(Arrays.asList("username"))) {
                throw new JsonSyntaxException("Missing parameters from JSON body");
            }
            if(content.keySet().contains("password")){
                model.updateAccount(Long.parseLong(request.params().get(":id")),
                        (String) content.get("username"),
                        (String) content.get("password"));

            } else {
                model.updateAccount(Long.parseLong(request.params().get(":id")),
                        (String) content.get("username"));
            }
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return "Internal Server Error";
        } catch (JsonSyntaxException e) {
            Server.getInstance().log.error(e.getMessage());
            Server.getInstance().log.error(e.getMessage());
            response.status(400);
            return "Request body not correct";
        }
        response.status(201);
        return "Account updated";
    }

    /**
     * method to process the deletion of a clinician
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether or not the clinician was deleted or not
     */
    public String deleteClinician(Request request, Response response) {
        Clinician queriedClinician = queryClinician(request, response);

        if (queriedClinician == null) {
            return response.body();
        }

        try {
            model.removeClinician(queriedClinician);
            response.status(201);
            return "CLINICIAN DELETED";
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return "Internal Server Error";
        }
    }
}
