package seng302.Controllers;

import com.google.gson.Gson;
import seng302.Model.Query;
import spark.Request;
import spark.Response;

public class SQLController {
    public String executeQuery(Request request, Response response) {
        Query query = new Gson().fromJson(request.body(), Query.class);
        if (query == null) {
            response.status(400);
            return "Missing Command Body";
        } else {
            try {
                // TODO try to execute the query - Return result
                return "sql output";
            } catch (Exception e) {
                response.status(500);
                return "Internal Server Error";
            }
        }
    }
}
