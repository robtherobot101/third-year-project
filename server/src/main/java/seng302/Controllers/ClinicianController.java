package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import seng302.Logic.Database.GeneralClinician;
import seng302.Model.Clinician;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.ArrayList;

public class ClinicianController {
    private GeneralClinician model;

    public ClinicianController() {
        model = new GeneralClinician();
    }

    /**
     * Retrieves a Clinician object from the HTTP request ":id" param
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

    public String addClinician(Request request, Response response) {
        Gson gson = new Gson();
        Clinician receivedClinician;

        // Attempt to parse received JSON
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
            return "Missing Clinician Body";
        } else {
            //TODO make model.insertClinician return token
            try {
                model.insertClinician(receivedClinician);
                response.status(201);
                return "placeholder token";
            } catch (SQLException e) {
                Server.getInstance().log.error(e.getMessage());
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

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

    public String editClinician(Request request, Response response) {
        Clinician queriedClinician = queryClinician(request, response);

        if (queriedClinician == null) {
            return response.body();
        }

        Gson gson = new Gson();

        Clinician receivedClinician = gson.fromJson(request.body(), Clinician.class);
        if (receivedClinician == null) {
            response.status(400);
            return "Missing Clinician Body";
        } else {
            try {
                model.updateClinicianDetails(receivedClinician, Integer.parseInt(request.params(":id")));
                response.status(201);
                return "CLINICIAN SUCCESSFULLY UPDATED";
            } catch (SQLException e) {
                Server.getInstance().log.error(e.getMessage());
                response.status(500);
                return "Internal Server Error";
            }
        }
    }

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
