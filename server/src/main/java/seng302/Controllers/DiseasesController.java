package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import seng302.Logic.Database.UserDiseases;
import seng302.Logic.Database.UserProcedures;
import seng302.Model.Disease;
import seng302.Model.Medication.Medication;
import seng302.Model.Procedure;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.ArrayList;

public class DiseasesController {
    private UserDiseases model;

    public DiseasesController() {
        model = new UserDiseases();
    }

    public String getAllDiseases(Request request, Response response) {
        int requestedUserId = Integer.parseInt(request.params(":id"));

        ArrayList<Disease> queriedDiseases;
        try {
            queriedDiseases = model.getAllDiseases(requestedUserId);
        } catch (SQLException e) {
            Server.log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedDiseases = gson.toJson(queriedDiseases);

        response.type("application/json");
        response.status(200);
        return serialQueriedDiseases;
    }

    public String getSingleDisease(Request request, Response response) {
        Disease queriedDisease = queryDisease(request, response);

        if (queriedDisease == null) {
            return response.body();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedDisease = gson.toJson(queriedDisease);

        response.type("application/json");
        response.status(200);
        return serialQueriedDisease;
    }

    public String addDisease(Request request, Response response) {
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Gson gson = new Gson();

        Disease receivedDisease = gson.fromJson(request.body(), Disease.class);
        if (receivedDisease == null) {
            response.status(400);
            return "Missing Disease Body";
        } else {
            try {
                model.insertDisease(receivedDisease, requestedUserId);
                response.status(201);
                return "DISEASE INSERTED FOR USER ID: " + requestedUserId;
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    public String editDisease(Request request, Response response) {
        int requestedDiseaseId = Integer.parseInt(request.params(":diseaseId"));
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Disease queriedDisease = queryDisease(request, response);

        if (queriedDisease == null) {
            return response.body();
        }

        Gson gson = new Gson();

        Disease receivedDisease = gson.fromJson(request.body(), Disease.class);
        if (receivedDisease == null) {
            response.status(400);
            return "Missing Disease Body";
        } else {
            try {
                model.updateDisease(receivedDisease, requestedDiseaseId, requestedUserId);
                response.status(201);
                return "DISEASE WITH ID: "+ requestedDiseaseId +" FOR USER ID: "+ requestedUserId +" SUCCESSFULLY UPDATED";
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    public String deleteDisease(Request request, Response response) {
        int requestedDiseaseId = Integer.parseInt(request.params(":diseaseId"));
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Disease queriedDisease = queryDisease(request, response);

        if (queriedDisease == null) {
            return response.body();
        }

        try {
            model.removeDisease(requestedUserId, requestedDiseaseId);
            response.status(201);
            return "DISEASE WITH ID: "+ requestedDiseaseId +" FOR USER ID: "+ requestedUserId +" DELETED";
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        }
    }

    /**
     * Checks for the validity of the request ID, and returns a disease obj
     * @param request HTTP request
     * @param response HTTP response
     * @return A valid Disease object if the Disease exists otherwise return null
     */
    private Disease queryDisease(Request request, Response response) {
        int requestedDiseaseId = Integer.parseInt(request.params(":diseaseId"));
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Disease queriedDisease;
        try {
            queriedDisease = model.getDiseaseFromId(requestedDiseaseId, requestedUserId);
        } catch (SQLException e) {
            Server.log.error(e.getMessage());
            response.status(500);
            response.body("Internal server error");
            return null;
        }

        if (queriedDisease == null) {
            Server.log.warn(String.format("No disease of ID: %d found", requestedDiseaseId));
            response.status(404);
            response.body("Not found");
            return null;
        }
        return queriedDisease;
    }
}
