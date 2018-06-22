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

    public HistoryController() {
        model = new UserHistory();
    }

    public String getUserHistoryItems(Request request, Response response) {

        int requestedUserId = Integer.parseInt(request.params(":id"));

        ArrayList<HistoryItem> queriedHistoryItems;
        try {
            queriedHistoryItems = model.getAllHistoryItems(requestedUserId);
        } catch (SQLException e) {
            Server.log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedHistoryItems = gson.toJson(queriedHistoryItems);

        response.type("application/json");
        response.status(200);
        return serialQueriedHistoryItems;
    }

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
