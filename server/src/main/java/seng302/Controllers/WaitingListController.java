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
import java.util.*;

public class WaitingListController {
    private UserWaitingList model;

    /**
     * constructor method to create a new waiting list controller object
     * to handle all the waiting list operation requests
     */
    public WaitingListController() {
        model = new UserWaitingList();
    }

    /**
     * method to get all waiting list items
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json object containing all waiting list items or a error message
     */
    public String getAllWaitingListItems(Request request, Response response) {

        Map<String, String> params = new HashMap<String, String>();
        List<String> possibleParams = new ArrayList<String>(Arrays.asList(
                "organ","region"
        ));

        for(String param:possibleParams){
            if(request.queryParams(param) != null){
                params.put(param,request.queryParams(param));
            }
        }

        List<WaitingListItem> queriedWaitingListItems;
        try {
            queriedWaitingListItems = model.queryWaitingListItems(params);
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



    /**
     * method to get all waiting list items of a single user
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json object containing all the waiting list items of a user or an error message
     */
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

    /**
     * method to handle getting a single waiting list object from a specific user
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json object containing the waiting list item objects information, or an error message
     */
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

    /**
     * method to handle adding a new user waiting list item
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether the operation was completed successfully
     */
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

    /**
     * method to edit an existing waiting list item
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether he operation was completed successfully
     */
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

    /**
     * method to delete a specific waiting list item
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether the operation was completed successfully or not
     */
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
