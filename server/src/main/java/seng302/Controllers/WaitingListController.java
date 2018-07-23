package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import seng302.Logic.Database.GeneralUser;
import seng302.Logic.Database.UserWaitingList;
import seng302.Model.Procedure;
import seng302.Model.WaitingListItem;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.ArrayList;

public class WaitingListController {
    private UserWaitingList model;

    public WaitingListController() {
        model = new UserWaitingList();
    }

    public String getAllWaitingListItems(Request request, Response response) {

        ArrayList<WaitingListItem> queriedWaitingListItems;
        try {
            queriedWaitingListItems = model.getAllWaitingListItems();
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedWaitingListItems = gson.toJson(queriedWaitingListItems);

        response.type("application/json");
        response.status(200);
        return serialQueriedWaitingListItems;
    }

    public String getAllUserWaitingListItems(Request request, Response response) {
        int requestedUserId = Integer.parseInt(request.params(":id"));

        ArrayList<WaitingListItem> queriedUserWaitingListItems;
        try {
            queriedUserWaitingListItems = model.getAllUserWaitingListItems(requestedUserId);
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedUserWaitingListItems = gson.toJson(queriedUserWaitingListItems);

        response.type("application/json");
        response.status(200);
        return serialQueriedUserWaitingListItems;
    }

    public String getSingleUserWaitingListItem(Request request, Response response) {
        WaitingListItem queriedWaitingListItem = queryWaitingListItem(request, response);

        if (queriedWaitingListItem == null) {
            return response.body();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedWaitingListItem = gson.toJson(queriedWaitingListItem);

        response.type("application/json");
        response.status(200);
        return serialQueriedWaitingListItem;
    }

    public String addNewUserWaitingListItem(Request request, Response response) {

        int requestedUserId = Integer.parseInt(request.params(":id"));

        Gson gson = new Gson();

        WaitingListItem receivedWaitingListItem = gson.fromJson(request.body(), WaitingListItem.class);
        if (receivedWaitingListItem == null) {
            response.status(400);
            return "Missing Waiting List Item Body";
        } else {
            try {
                model.insertWaitingListItem(receivedWaitingListItem, requestedUserId);
                response.status(201);
                return "WAITING LIST ITEM INSERTED FOR USER ID: " + requestedUserId;
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    public String editWaitingListItem(Request request, Response response) {
        int requestedWaitingListItemId = Integer.parseInt(request.params(":waitingListItemId"));
        int requestedUserId = Integer.parseInt(request.params(":id"));

        WaitingListItem queriedWaitingListItem = queryWaitingListItem(request, response);

        if (queriedWaitingListItem == null) {
            return response.body();
        }

        Gson gson = new Gson();

        WaitingListItem receivedWaitingListItem = gson.fromJson(request.body(), WaitingListItem.class);
        if (receivedWaitingListItem == null) {
            response.status(400);
            return "Missing Waiting List Item Body";
        } else {
            try {
                model.updateWaitingListItem(receivedWaitingListItem, requestedWaitingListItemId, requestedUserId);
                response.status(201);
                return "WAITING LIST ITEM WITH ID: "+ requestedWaitingListItemId +" FOR USER ID: "+ requestedUserId +" SUCCESSFULLY UPDATED";
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    public String deleteWaitingListItem(Request request, Response response) {
        int requestedWaitingListItemId = Integer.parseInt(request.params(":waitingListItemId"));
        int requestedUserId = Integer.parseInt(request.params(":id"));

        WaitingListItem queriedWaitingListItem = queryWaitingListItem(request, response);

        if (queriedWaitingListItem == null) {
            return response.body();
        }

        try {
            model.removeWaitingListItem(requestedUserId, requestedWaitingListItemId);
            response.status(201);
            return "WAITING LIST ITEM WITH ID: "+ requestedWaitingListItemId +" FOR USER ID: "+ requestedUserId +" DELETED";
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        }
    }

    /**
     * Checks for the validity of the request ID, and returns a WaitingListItem obj
     * @param request HTTP request
     * @param response HTTP response
     * @return A valid WaitingListItem object if the WaitingListItem exists otherwise return null
     */
    private WaitingListItem queryWaitingListItem(Request request, Response response) {
        int requestedWaitingListItemId = Integer.parseInt(request.params(":waitingListItemId"));
        int requestedUserId = Integer.parseInt(request.params(":id"));

        WaitingListItem queriedWaitingListItem;
        try {
            queriedWaitingListItem = model.getWaitingListItemFromId(requestedWaitingListItemId, requestedUserId);
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            response.body("Internal server error");
            return null;
        }

        if (queriedWaitingListItem == null) {
            Server.getInstance().log.warn(String.format("No waiting list item of ID: %d found", requestedWaitingListItemId));
            response.status(404);
            response.body("Not found");
            return null;
        }
        return queriedWaitingListItem;
    }


}
