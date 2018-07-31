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
