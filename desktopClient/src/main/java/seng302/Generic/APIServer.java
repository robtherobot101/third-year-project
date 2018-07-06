package seng302.Generic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.Map;

public class APIServer {
    private String url;
    private Client client = ClientBuilder.newClient();
    private JsonParser jp = new JsonParser();

    public APIServer(String url){
        this.url = url;
    }

    /**
     * Perform a get request to the given endpoint and return the results as a JsonObject
     * @param path The endopint to query
     * @return The result as a JsonObject
     */
    private JsonObject getRequest(String path) {
        //TODO query params, headers and catch exceptions
        return jp.parse(client.target(url)
                // Specifies the endpoint to query
                .path(path)
                // Specifies the data content-type
                .request(MediaType.APPLICATION_JSON)
                // Performs the GET request and converts to an JsonObject
                .get(String.class)).getAsJsonObject();
    }

    // DISCLAIMER - I have no idea if this works or not ~jma326
    public Response postRequest(String path, JsonObject body, Map<String, String> queryParams){
        // Creates a pointer to the end-point
        WebTarget target = client.target(url).path(path);

        // Adds all the query parameters
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            target = target.queryParam(entry.getKey(), entry.getValue());
        }

        return new Response(target.request(MediaType.APPLICATION_JSON)
                // Send the data in the post request as JSON -
                .post(Entity.entity(body.toString(), MediaType.APPLICATION_JSON))
                // Parse the JSON response
                .readEntity(String.class));
    }

    /**
     * Queries the 'hello' endpoint to test connection
     * @return A string containing the version of the queries server
     */
    public String testConnection() {
        return getRequest("hello").get("version").toString();
    }
}
