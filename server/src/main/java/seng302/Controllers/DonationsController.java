package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import seng302.Logic.Database.UserDiseases;
import seng302.Logic.Database.UserDonations;
import seng302.Model.Attribute.Organ;
import seng302.Model.Disease;
import seng302.Model.WaitingListItem;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

public class DonationsController {

    private UserDonations model;

    public DonationsController() {model = new UserDonations();}

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

    public String addDonation(Request request, Response response) {
        int requestedUserId = Integer.parseInt(request.params(":id"));

        Gson gson = new Gson();

        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(request.body()).getAsJsonObject();
        String organName = obj.get("name").getAsString();
        Organ organ = Organ.valueOf(organName.toUpperCase());

        try {
            model.insertDonation(organ, requestedUserId);
            response.status(201);
            return "DONATION INSERTED FOR USER ID: " + requestedUserId;
        } catch (SQLException e) {
            response.status(500);
            return "Internal Server Error";
        }

    }

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
