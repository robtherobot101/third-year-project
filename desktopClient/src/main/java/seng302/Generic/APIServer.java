package seng302.Generic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

public class APIServer {
    private String url;
    private Client client = ClientBuilder.newClient().property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
    private JsonParser jp = new JsonParser();

    /**
     * constructor method to create a new apiserver object
     * object contains the information needed to connect to the api server specified
     * @param url String the url of the api server to be used
     */
    public APIServer(String url){
        this.url = url;
    }

    /**
     * Perform a get request to the given endpoint and return the results as a JsonObject
     * @param path The endpoint to query
     * @param queryParams The query parameters
     * @param token the token used to secure the payload
     * @return The result as a response object
     */
    public APIResponse getRequest(Map<String, String> queryParams, String token, String... path) {
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

        return new APIResponse(target.request(MediaType.APPLICATION_JSON).header("token", token).get());
    }


    /**
     * Perform a post to the given endpoint and return the results as a JsonObject
     * @param path The endopint to query
     * @param queryParams The query parameters
     * @param body The body of the request as a JsonObject
     * @param token the token used to secure the payload
     * @return The result as a response object
     */
    public APIResponse postRequest(JsonObject body, Map<String, String> queryParams, String token, String... path){
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

        return new APIResponse(target.request(MediaType.APPLICATION_JSON).header("token", token)
                // Send the data in the post request as JSON -
                .post(Entity.entity(body.toString(), MediaType.APPLICATION_JSON)));
    }


    /**
     * Perform a post to the given endpoint and return the results as a JsonObject
     * @param path The endopint to query
     * @param queryParams The query parameters
     * @param body The body of the request as a JsonObject
     * @param token the token used to secure the payload
     * @return The result as a response object
     */
    public APIResponse patchRequest(JsonElement body, Map<String, String> queryParams, String token, String... path){
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

        return new APIResponse(target.request(MediaType.APPLICATION_JSON).header("token", token)
                // Send the data in the patch request as JSON -
                .method("PATCH", Entity.entity(body.toString(), MediaType.APPLICATION_JSON)));
    }

    /**
     * perform a delete request on a given endpoint and return a response as a json object
     * @param queryParams The query parameters
     * @param token the token used to secure the payload
     * @param path The endopint to query
     * @return The result as a response object
     */
    public APIResponse deleteRequest(Map<String, String> queryParams, String token, String... path) {
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

        return new APIResponse(target.request(MediaType.APPLICATION_JSON).header("token", token)
                // Send the data in the post request as JSON -
                .delete());
    }


    /**
     * Queries the 'hello' endpoint to test connection
     * @return A string containing the version of the queries server
     */
    public String testConnection() {
        return getRequest(new HashMap<>(),"hello").getAsJsonObject().get("version").toString();
    }
}
