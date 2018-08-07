package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import seng302.Logic.Database.UserDonations;
import seng302.Model.Attribute.Organ;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.Set;

public class DonationsController {

    private UserDonations model;

    /**
     * Class builder to create a new donations controller
     */
    public DonationsController() {model = new UserDonations();}

    /**
     * method to process the request for getting all of one users donations
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json object containing all the user donation details, or an error message
     */
    public String getAllUserDonations(Request request, Response response) {
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Set<Organ> queriedDonations;
        try {
            queriedDonations = model.getAllUserDonations(requestedUserId);
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedDonations = gson.toJson(queriedDonations);

        response.type("application/json");
        response.status(200);
        return serialQueriedDonations;
    }

    /**
     * method to handle the adding of a new organ donation
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String response if the operation was successful
     */
    public String addDonation(Request request, Response response) {
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Gson gson = new Gson();

        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(request.body()).getAsJsonObject();
        String organName = obj.get("name").getAsString();
        Organ organ = Organ.valueOf(organName.toUpperCase());

        try {
            model.insertDonation(organ, requestedUserId, null);
            response.status(201);
            return "DONATION INSERTED FOR USER ID: " + requestedUserId;
        } catch (SQLException e) {
            response.status(500);
            return "Internal Server Error";
        }

    }

    /**
     * method to handle the request to get all donations
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json object containing all donations and their details
     */
    public String getAllDonations(Request request, Response response) {

        Set<Organ> queriedDonations;
        try {
            queriedDonations = model.getAllDonations();
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedDonations = gson.toJson(queriedDonations);

        response.type("application/json");
        response.status(200);
        return serialQueriedDonations;
    }

    /**
     * method to handle getting a single donation from one user
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json object containing the donation information
     */
    public String getSingleUserDonationItem(Request request, Response response) {
        Organ queriedDonationListItem = queryDonationListItem(request, response);

        if (queriedDonationListItem == null) {
            return response.body();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialQueriedWaitingListItem = gson.toJson(queriedDonationListItem);

        response.type("application/json");
        response.status(200);
        return serialQueriedWaitingListItem;
    }

    /**
     * method to handle the deletion of a donation request
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String Whether the operation was successful or not
     */
    public String deleteUserDonationItem(Request request, Response response) {
        String requestedDonationItemName = request.params(":donationListItemName");
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Organ queriedDonationListItem = queryDonationListItem(request, response);

        if (queriedDonationListItem == null) {
            return response.body();
        }

        try {
            model.removeDonationListItem(requestedUserId, requestedDonationItemName);
            response.status(201);
            return "DONATION LIST ITEM WITH NAME: "+ requestedDonationItemName +" FOR USER ID: "+ requestedUserId +" DELETED";
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        }
    }

    /**
     * method to handle the deletion of all user donations (only when a death occurs)
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether the operation was successful or not
     */
    public String deleteAllUserDonations(Request request, Response response) {
        int requestedUserId = Integer.parseInt(request.params(":id"));

        try {
            model.removeAllUserDonations(requestedUserId);
            response.status(201);
            return "ALL DONATION LIST ITEMS FOR USER ID: "+ requestedUserId +" DELETED";
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        }
    }

    /**
     * Checks for the validity of the request ID, and returns a DonationListItem obj
     * @param request HTTP request
     * @param response HTTP response
     * @return A valid DonationListItem object if the DonationListItem exists otherwise return null
     */
    private Organ queryDonationListItem(Request request, Response response) {
        String requestedDonationItemName = request.params(":donationListItemName");
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Organ queriedDonationListItem;
        try {
            queriedDonationListItem = model.getDonationListItemFromName(requestedDonationItemName, requestedUserId);
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            response.body("Internal server error");
            return null;
        }

        if (queriedDonationListItem == null) {
            Server.getInstance().log.warn(String.format("No donation list item of NAME: %s found", requestedDonationItemName));
            response.status(404);
            response.body("Not found");
            return null;
        }
        return queriedDonationListItem;
    }


}
