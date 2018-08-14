package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import seng302.Logic.Database.UserHistory;
import seng302.Logic.Database.UserMedications;
import seng302.Model.HistoryItem;
import seng302.Model.Medication.Medication;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.ArrayList;

public class HistoryController {
    private UserHistory model;

    /**
     * constructor method to create a new history controller object
     * to handle all history related requests
     */
    public HistoryController() {
        model = new UserHistory();
    }

    /**
     * method to handle getting a single users history items and their details
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-Data to the runtime
     * @return Json object containing all the history of a single User, or an error message
     */
    public String getUserHistoryItems(Request request, Response response) {

        int requestedUserId = Integer.parseInt(request.params(":id"));

        ArrayList<HistoryItem> queriedHistoryItems;
        try {
            queriedHistoryItems = model.getAllHistoryItems(requestedUserId);
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedHistoryItems = gson.toJson(queriedHistoryItems);

        response.type("application/json");
        response.status(200);
        return serialQueriedHistoryItems;
    }

    /**
     * method to handle creating a new history object on the Database
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-Data to the runtime
     * @return String whether the operation was completed successfully or not
     */
    public String addUserHistoryItem(Request request, Response response) {
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Gson gson = new Gson();

        HistoryItem receivedHistoryItem = gson.fromJson(request.body(), HistoryItem.class);
        if (receivedHistoryItem == null) {
            response.status(400);
            return "Missing History Body";
        } else {
            try {
                model.insertHistoryItem(receivedHistoryItem, requestedUserId);
                response.status(201);
                return "HISTORY ITEM INSERTED FOR USER ID: " + requestedUserId;
            } catch (SQLException e) {
                e.printStackTrace();
                response.status(500);
                return "Internal Server Error";
            }

        }
    }
}
