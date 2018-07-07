package seng302.Generic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

public class APIServer {
    private String url;
    private Client client = ClientBuilder.newClient().property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
    private JsonParser jp = new JsonParser();

    public APIServer(String url){
        this.url = url;
    }

    /**
     * Perform a get request to the given endpoint and return the results as a JsonObject
     * @param path The endpoint to query
     * @param queryParams The query parameters
     * @return The result as a response object
     */
    public Response getRequest(String path, Map<String,String> queryParams) {
        // Creates a pointer to the api
        WebTarget target = client.target(url).path(path);

        // Adds all the query parameters
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            target = target.queryParam(entry.getKey(), entry.getValue());
        }

        return new Response(target.request(MediaType.APPLICATION_JSON)
                .get(String.class));
    }


    /**
     * Perform a post to the given endpoint and return the results as a JsonObject
     * @param path The endopint to query
     * @param queryParams The query parameters
     * @param body The body of the request as a JsonObject
     * @return The result as a response object
     */
    public Response postRequest(JsonObject body, Map<String, String> queryParams, String... path){
        // Creates a pointer to the api
        WebTarget target = client.target(url);

        // Adds all the query parameters
        for(String pathParam:path){
            target = target.path(pathParam);
        }

        // Adds all the query parameters
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            target = target.queryParam(entry.getKey(), entry.getValue());
        }

        System.out.println(target);

        return new Response(target.request(MediaType.APPLICATION_JSON)
                // Send the data in the post request as JSON -
                .post(Entity.entity(body.toString(), MediaType.APPLICATION_JSON))
                // Parse the JSON response
                .readEntity(String.class));
    }


    /**
     * Perform a post to the given endpoint and return the results as a JsonObject
     * @param path The endopint to query
     * @param queryParams The query parameters
     * @param body The body of the request as a JsonObject
     * @return The result as a response object
     */
    public Response patchRequest(JsonObject body, Map<String, String> queryParams, String... path){
        // Creates a pointer to the api
        WebTarget target = client.target(url);

        // Adds all the query parameters
        for(String pathParam:path){
            target = target.path(pathParam);
        }

        // Adds all the query parameters
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            target = target.queryParam(entry.getKey(), entry.getValue());
        }

        System.out.println(target);

        return new Response(target.request(MediaType.APPLICATION_JSON)
                // Send the data in the post request as JSON -
                .method("PATCH", Entity.entity(body.toString(), MediaType.APPLICATION_JSON)).toString());
    }


    /**
     * Queries the 'hello' endpoint to test connection
     * @return A string containing the version of the queries server
     */
    public String testConnection() {
        return getRequest("hello", new HashMap<>()).getAsJsonObject().get("version").toString();
    }
}
