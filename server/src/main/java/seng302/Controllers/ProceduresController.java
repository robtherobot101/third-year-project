package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import seng302.Logic.Database.UserMedications;
import seng302.Logic.Database.UserProcedures;
import seng302.Model.Medication.Medication;
import seng302.Model.Procedure;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.ArrayList;

public class ProceduresController {
    private UserProcedures model;

    public ProceduresController() {
        model = new UserProcedures();
    }

    public String getAllProcedures(Request request, Response response) {
        int requestedUserId = Integer.parseInt(request.params(":id"));

        ArrayList<Procedure> queriedProcedures;
        try {
            queriedProcedures = model.getAllProcedures(requestedUserId);
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedProcedures = gson.toJson(queriedProcedures);

        response.type("application/json");
        response.status(200);
        return serialQueriedProcedures;
    }

    public String getSingleProcedure(Request request, Response response) {

        Procedure queriedProcedure = queryProcedure(request, response);

        if (queriedProcedure == null) {
            return response.body();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedProcedure = gson.toJson(queriedProcedure);

        response.type("application/json");
        response.status(200);
        return serialQueriedProcedure;
    }

    public String addProcedure(Request request, Response response) {

        int requestedUserId = Integer.parseInt(request.params(":id"));

        Gson gson = new Gson();

        Procedure receivedProcedure = gson.fromJson(request.body(), Procedure.class);
        if (receivedProcedure == null) {
            response.status(400);
            return "Missing Procedure Body";
        } else {
            try {
                model.insertProcedure(receivedProcedure, requestedUserId);
                response.status(201);
                return "PROCEDURE INSERTED FOR USER ID: " + requestedUserId;
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    public String editProcedure(Request request, Response response) {
        int requestedProcedureId = Integer.parseInt(request.params(":procedureId"));
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Procedure queriedProcedure = queryProcedure(request, response);

        if (queriedProcedure == null) {
            return response.body();
        }

        Gson gson = new Gson();

        Procedure receivedProcedure = gson.fromJson(request.body(), Procedure.class);
        if (receivedProcedure == null) {
            response.status(400);
            return "Missing Procedure Body";
        } else {
            try {
                model.updateProcedure(receivedProcedure, requestedProcedureId, requestedUserId);
                response.status(201);
                return "PROCEDURE WITH ID: "+ requestedProcedureId +" FOR USER ID: "+ requestedUserId +" SUCCESSFULLY UPDATED";
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    public String deleteProcedure(Request request, Response response) {
        int requestedProcedureId = Integer.parseInt(request.params(":procedureId"));
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Procedure queriedProcedure = queryProcedure(request, response);

        if (queriedProcedure == null) {
            return response.body();
        }

        try {
            model.removeProcedure(requestedUserId, requestedProcedureId);
            response.status(201);
            return "PROCEDURE WITH ID: "+ requestedProcedureId +" FOR USER ID: "+ requestedUserId +" DELETED";
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        }
    }

    /**
     * Checks for the validity of the request ID, and returns a procedure obj
     * @param request HTTP request
     * @param response HTTP response
     * @return A valid Procedure object if the Procedure exists otherwise return null
     */
    private Procedure queryProcedure(Request request, Response response) {
        int requestedProcedureId = Integer.parseInt(request.params(":procedureId"));
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Procedure queriedProcedure;
        try {
            queriedProcedure = model.getProcedureFromId(requestedProcedureId, requestedUserId);
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            response.body("Internal server error");
            return null;
        }

        if (queriedProcedure == null) {
            Server.getInstance().log.warn(String.format("No procedure of ID: %d found", requestedProcedureId));
            response.status(404);
            response.body("Not found");
            return null;
        }
        return queriedProcedure;
    }
}
