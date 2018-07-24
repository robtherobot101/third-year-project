package seng302.Controllers;

import com.google.gson.Gson;
import seng302.Logic.CommandLineInterface;
import seng302.Logic.Database.GeneralClinician;
import seng302.Model.Command;
import spark.Request;
import spark.Response;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class CLIController {

    private CommandLineInterface model;

    public CLIController() {
        model = new CommandLineInterface();
    }

    public String executeQuery(Request request, Response response) {
        Command command = new Gson().fromJson(request.body(), Command.class);
        if (command == null) {
            response.status(400);
            response.body("Missing Command Body");
            return null;
        } else {
            try {
                String result = model.readCommand(command.toString()).getResponse();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                response.status(500);
                return "Internal Server Error";
            }
        }
    }


/*    public String resample(Request request, Response response) {
        try {
            model.resample();
            return "RESAMPLE SUCCESS";
        } catch (SQLException e) {
            e.printStackTrace();
            response.status(500);
            return "RESAMPLE FAILURE";
        }


    }*/
}
