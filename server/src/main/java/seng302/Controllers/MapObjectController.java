package seng302.Controllers;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import seng302.Logic.Database.MapObjectModel;
import seng302.Model.MapObject;
import seng302.Model.OrganTransfer;
import seng302.Model.User;
import seng302.Model.WaitingListItem;
import seng302.NotificationManager.PushAPI;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.ArrayList;

public class MapObjectController {
    private MapObjectModel model;

    /**
     * constructor method to create a new map object controller object
     * to handle all the map object list operation requests
     */
    public MapObjectController() {
        model = new MapObjectModel();
    }

    /**
     * method to get all map objects
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json object containing all map object items or a error message
     */
    public String getAllMapObjects(Request request, Response response) {

        ArrayList<MapObject> queriedMapObjects;
        try {
            queriedMapObjects = model.getAllMapObjects();
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialMapObjectItems = gson.toJson(queriedMapObjects);

        response.type("application/json");
        response.status(200);
        return serialMapObjectItems;
    }

    /**
     * gets all of the organ transfers
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json object containing all organ transfers or a error message
     */
    public String getAllTransfers(Request request, Response response) {
        ArrayList<OrganTransfer> transfers;
        try {
            transfers = model.getAllTransfers();
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serialMapObjectItems = gson.toJson(transfers);

        response.type("application/json");
        response.status(200);
        return serialMapObjectItems;
    }

    /**
     * inserts a new organ transfer
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json of whether or not the post was completed
     */
    public String postTransfer(Request request, Response response) {
        Gson gson = new Gson();

        OrganTransfer transfer = gson.fromJson(request.body(), OrganTransfer.class);
        if (transfer == null) {
            response.status(400);
            return "Missing Transfer Body";
        } else {
            try {
                model.insertTransfer(transfer);
                response.status(201);
                try {
                    PushAPI.getInstance().sendTextNotification((int)transfer.getReceiverId(), "Transfer started.",
                            "A new transfer has begun to transfer a " + transfer.getOrganType().toString() + "to you.");
                } catch (Exception e) {
                    Server.getInstance().log.error(e.getMessage());
                    Server.getInstance().log.error("Failed to insert notification for the start of the transfer process");
                }
                return "TRANSFER INSERTED FOR ORGAN ID: " + transfer.getId();
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    /**
     * Deletes a organTransfer with a given organ id
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json of whether or not the deletion was completed
     */
    public String deleteOrganTransfer(Request request, Response response) {
        int organId = Integer.parseInt(request.params(":organId"));

        try {
            model.removeDonationListItem(organId);
            response.status(201);
            return "TRANSFER WITH ID:" + organId + " DELETED";
        } catch (SQLException e) {
            response.status(500);
            return e.getMessage();
        }
    }

}
