package seng302.Controllers;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import seng302.Logic.Database.MapObjectModel;
import seng302.Model.MapObject;
import seng302.Model.User;
import seng302.Model.WaitingListItem;
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
}
