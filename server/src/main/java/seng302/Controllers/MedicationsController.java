package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import seng302.Logic.Database.GeneralUser;
import seng302.Logic.Database.UserMedications;
import seng302.Model.Clinician;
import seng302.Model.Medication.Medication;
import seng302.Model.User;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.ArrayList;

public class MedicationsController {
    private UserMedications model;

    public MedicationsController() {
        model = new UserMedications();
    }

    public String getAllMedications(Request request, Response response) {

        int requestedUserId = Integer.parseInt(request.params(":id"));

        ArrayList<Medication> queriedMedications;
        try {
            queriedMedications = model.getAllMedications(requestedUserId);
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedMedications = gson.toJson(queriedMedications);

        response.type("application/json");
        response.status(200);
        return serialQueriedMedications;
    }

    public String getSingleMedication(Request request, Response response) {

        Medication queriedMedication = queryMedication(request, response);

        if (queriedMedication == null) {
            return response.body();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedMedication = gson.toJson(queriedMedication);

        response.type("application/json");
        response.status(200);
        return serialQueriedMedication;
    }

    public String addMedication(Request request, Response response) {
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Gson gson = new Gson();

        Medication receivedMedication = gson.fromJson(request.body(), Medication.class);
        if (receivedMedication == null) {
            response.status(400);
            return "Missing Medication Body";
        } else {
            try {
                model.insertMedication(receivedMedication, requestedUserId);
                response.status(201);
                return "MEDICATION INSERTED FOR USER ID: " + requestedUserId;
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    public String editMedication(Request request, Response response) {
        int requestedMedicationId = Integer.parseInt(request.params(":medicationId"));
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Medication queriedMedication = queryMedication(request, response);

        if (queriedMedication == null) {
            return response.body();
        }

        Gson gson = new Gson();

        Medication receivedMedication = gson.fromJson(request.body(), Medication.class);
        if (receivedMedication == null) {
            response.status(400);
            return "Missing Medication Body";
        } else {
            try {
                model.updateMedication(receivedMedication, requestedMedicationId, requestedUserId);
                response.status(201);
                return "MEDICATION WITH ID: "+ requestedMedicationId +" FOR USER ID: "+ requestedUserId +" SUCCESSFULLY UPDATED";
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }
        }
    }

    public String deleteMedication(Request request, Response response) {
        int requestedMedicationId = Integer.parseInt(request.params(":medicationId"));
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Medication queriedMedication = queryMedication(request, response);

        if (queriedMedication == null) {
            return response.body();
        }

        try {
            model.removeMedication(requestedUserId, requestedMedicationId);
            response.status(201);
            return "MEDICATION WITH ID: "+ requestedMedicationId +" FOR USER ID: "+ requestedUserId +" DELETED";
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        }
    }

    /**
     * Checks for the validity of the request ID, and returns a medication obj
     * @param request HTTP request
     * @param response HTTP response
     * @return A valid Medication object if the Medication exists otherwise return null
     */
    private Medication queryMedication(Request request, Response response) {
        int requestedMedicationId = Integer.parseInt(request.params(":medicationId"));
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Medication queriedMedication;
        try {
            queriedMedication = model.getMedicationFromId(requestedMedicationId, requestedUserId);
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            response.body("Internal server error");
            return null;
        }

        if (queriedMedication == null) {
            Server.getInstance().log.warn(String.format("No medication of ID: %d found", requestedMedicationId));
            response.status(404);
            response.body("Not found");
            return null;
        }
        return queriedMedication;
    }

}
