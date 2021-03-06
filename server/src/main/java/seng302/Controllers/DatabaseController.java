package seng302.Controllers;

import seng302.Logic.Database.Administration;
import seng302.Server;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseController {

    Administration model = new Administration();

    /**
     * method to handle the request to reset the database to its default tables with no entries
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether the operation was completed successfully
     */
    public String reset(Request request, Response response) {
        try {
            model.reset();
            return "RESET SUCCESSFUL";
        } catch (SQLException | IOException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return e.getMessage();
        }

    }

    /**
     * method to add default objects to the database, assumes tables exist already
     * note: reset the database before using this request
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether the operation was completed successfully or not
     */
    public String resample(Request request, Response response) {
        try {
            model.resample();
            return "RESAMPLE SUCCESS";
        } catch (SQLException | IOException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return "RESAMPLE FAILURE";
        }


    }

    /**
     * method to handle request to check if the database is online and a connection can be made
     * @param request Java request object, used to invoke correct methods
     * @param response Defines the contract between a returned instance and the runtime when an application needs to provide meta-data to the runtime
     * @return String whether the operation was completed successfully or not
     */
    public String status(Request request, Response response) {
        try {
            model.status();
            return "DATABASE ONLINE";
        } catch (SQLException e) {
            Server.getInstance().log.error(e.getMessage());
            response.status(500);
            return "DATABASE OFFLINE";
        }
    }
}
