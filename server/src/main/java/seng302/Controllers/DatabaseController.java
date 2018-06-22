package seng302.Controllers;

import seng302.Logic.Database.Administration;
import spark.Request;
import spark.Response;

import java.sql.SQLException;

public class DatabaseController {

    Administration model = new Administration();
    public String reset(Request request, Response response) {
        try {
            model.reset();
            return "RESET SUCCESSFUL";
        } catch (SQLException e) {
            e.printStackTrace();
            response.status(500);
            return "RESET FAILURE";
        }

    }
    public String resample(Request request, Response response) {
        try {
            model.resample();
            return "RESAMPLE SUCCESS";
        } catch (SQLException e) {
            e.printStackTrace();
            response.status(500);
            return "RESAMPLE FAILURE";
        }


    }
}
