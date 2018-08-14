package seng302.generic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.scene.control.Alert;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.Map;

public class APIServer {
    private String url;
    private Client client = ClientBuilder.newClient().property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
    private String tokenString = "token";

    /**
     * constructor method to create a new apiserver object
     * object contains the information needed to connect to the api server specified
     * @param url String the url of the api server to be used
     */
    APIServer(String url){
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
        try {
            return new APIResponse(target.request(MediaType.APPLICATION_JSON).header(tokenString, token).get());
        }
        catch(Exception e) {
            connectivityError();
            return null;
        }
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
        try {
            return new APIResponse(target.request(MediaType.APPLICATION_JSON).header(tokenString, token)
                    // Send the data in the post request as JSON -
                    .post(Entity.entity(body.toString(), MediaType.APPLICATION_JSON)));
        }
        catch(Exception e) {
            connectivityError();
            return null;
        }
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

        try {
            return new APIResponse(target.request(MediaType.APPLICATION_JSON).header(tokenString, token)
                    // Send the data in the patch request as JSON -
                    .method("PATCH", Entity.entity(body.toString(), MediaType.APPLICATION_JSON)));
        }
        catch(Exception e) {
            connectivityError();
            return null;
        }
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
        try {
            return new APIResponse(target.request(MediaType.APPLICATION_JSON).header(tokenString, token)
                    // Send the data in the post request as JSON -
                    .delete());
        }
        catch(Exception e){
            connectivityError();
            return null;
        }
    }


    /**
     * Display an error message if the request fails
     */
    private void connectivityError(){
        WindowManager.createAlert(Alert.AlertType.ERROR, "Error", "Connection failed", "Unable to connect to server. Are you connected to the internet?" ).showAndWait();

    }
}
