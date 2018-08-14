package seng302.Generic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.core.Response;

public class APIResponse{
    private JsonParser jp;
    private String body;
    private String token;
    private int status;

    /**
     * constructor method to create a new apiResponse object
     * is used to parse the responses from the server that contain requested Data
     * @param response Response the response from the server
     */
    APIResponse(Response response){
        jp = new JsonParser();
        this.body = response.readEntity(String.class);
        this.status = response.getStatus();
        try {
            this.token = (String) response.getHeaders().get("token").get(0);
        } catch (NullPointerException e) {
            this.token = null;
        }
    }

    /**
     * method to check if the response is in a valid JSON format
     * @return boolean if the apiResponse is JSON format
     */
    public boolean isValidJson(){
        try{
            new JSONObject(body);
        } catch (JSONException oe){
            try {
                new JSONArray(body);
            } catch (JSONException ae){
                return false;
            }
        }
        return true;
    }

    /**
     * gets the token attribute of the Response
     * @return String the token used to secure the payload
     */
    public String getToken() {
        return token;
    }

    /**
     * gets the response body as a JSON object
     * @return JsonObject the response body formatted
     */
    public JsonObject getAsJsonObject(){
        return jp.parse(body).getAsJsonObject();
    }

    /**
     * gets the response body as a JSON array
     * @return JsonArray the response body formatted
     */
    public JsonArray getAsJsonArray(){
        return jp.parse(body).getAsJsonArray();
    }

    /**
     * gets the response body as a string
     * @return String the response body formatted
     */
    public String getAsString(){
        return body;
    }

    /**
     * gets the response status code
     * @return int the response status code
     */
    public int getStatusCode() {
        return status;
    }
}
