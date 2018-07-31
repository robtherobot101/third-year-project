package seng302.Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import seng302.Logic.Database.OrgansDatabase;
import seng302.Model.DonatableOrgan;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrgansController {

    private OrgansDatabase model;

    public OrgansController() {
        model = new OrgansDatabase();
    }

    /**
     * gets all the DonatableOrgans and parses it into a json string
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return Json object containing all the DonatableOrgan objects, or an error message
     */
    public String getAllDonatableOrgans(Request request, Response response) {
        List<DonatableOrgan> allDonatableOrgans;
        try {
            allDonatableOrgans = model.getAllDonatableOrgans();
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String serializedOrgans = gson.toJson(allDonatableOrgans);

        response.type("application/json");
        response.status(200);
        return serializedOrgans;
    }

    /**
     * parses a donatable organ from a json string and inserts it into the database
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String response if the operation was successful
     */
    public String insertOrgan(Request request, Response response) {
        Gson gson = new Gson();

        DonatableOrgan organ = gson.fromJson(request.body(), DonatableOrgan.class);
        if (organ == null) {
            response.status(400);
            return "Missing Organ Body";
        } else {
            try {
                model.insertOrgan(organ);
                response.status(201);
                return "ORGAN INSERTED FOR USER ID: " + organ.getDonorId();
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    /**
     * parses a donatable organ from a json string and removes it from the database
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String response if the operation was successful
     */
    public String removeOrgan(Request request, Response response) {
        Gson gson = new Gson();

        DonatableOrgan organ = gson.fromJson(request.body(), DonatableOrgan.class);
        if (organ == null) {
            response.status(400);
            return "Missing Organ Body";
        } else {
            try {
                model.removeOrgan(organ);
                response.status(201);
                return "ORGAN REMOVED FOR USER ID: " + organ.getDonorId();
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

    /**
     * parses a donatable organ from a json string and updates it into the database
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String response if the operation was successful
     */
    public String updateOrgan(Request request, Response response) {
        Gson gson = new Gson();

        DonatableOrgan organ = gson.fromJson(request.body(), DonatableOrgan.class);
        if (organ == null) {
            response.status(400);
            return "Missing Organ Body";
        } else {
            try {
                model.updateOrgan(organ);
                response.status(201);
                return "ORGAN UPDATED FOR USER ID: " + organ.getDonorId();
            } catch (SQLException e) {
                response.status(500);
                return "Internal Server Error";
            }

        }
    }

}
