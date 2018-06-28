package seng302.Controllers;

import seng302.Server;
import spark.Request;
import spark.Response;

import static spark.Spark.halt;

/**
 * Utility class for profiles
 */
public class ProfileUtils {

    /**
     * Checks the validity of the ":id" HTTP request param
     * @param request Spark HTTP request obj
     * @param response Spark HTTP response obj
     * @return True if ID is valid, false otherwise
     */
    public boolean checkId(Request request, Response response) {
        int requestedUserId;
        System.out.println("CHECK ID CALLED");

        try {
            requestedUserId = Integer.parseInt(request.params(":id"));
        } catch (NumberFormatException nfe) {
            Server.getInstance().log.warn(String.format("Invalid ID: %s", request.params(":id")));
            halt(400, "Bad Request");
            return false;
        }

        // Check for an invalid ID extracted from the URL param
        if (requestedUserId <= 0) {
            Server.getInstance().log.warn(String.format("Non-positive ID: %d", requestedUserId));
            halt(400, "Bad Request");
            return false;
        }
        return true;
    }
}
