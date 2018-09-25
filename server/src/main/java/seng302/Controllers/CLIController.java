package seng302.Controllers;

import com.google.gson.Gson;
import seng302.Logic.CommandLineInterface;
import seng302.Logic.Database.GeneralClinician;
import seng302.Model.Command;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class CLIController {

    private CommandLineInterface model;

    /**
     * Constructs a new CLIController
     */
    public CLIController() {
        model = new CommandLineInterface();
    }

    /**
     * Executes a query on the commandline and returns the result as a string
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return The output of the CLI
     */
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
                Server.getInstance().log.error(e.getMessage());
                response.status(500);
                return "Internal Server Error";
            }
        }
    }
}
