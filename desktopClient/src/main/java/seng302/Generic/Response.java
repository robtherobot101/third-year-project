package seng302.Generic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;

public class Response {
    private String response;
    private JsonParser jp;
    public Response(String response){
        jp = new JsonParser();
        this.response = response;
    }

    public boolean isValidJson(){
        try{
            new JSONObject(response);
        } catch (JSONException oe){
            try {
                new JSONArray(response);
            } catch (JSONException ae){
                return false;
            }
        }
        return true;
    }

    public JsonObject getAsJsonObject(){
        return jp.parse(response).getAsJsonObject();
    }

    public JsonArray getAsJsonArray(){
        return jp.parse(response).getAsJsonArray();
    }

    public String getAsString(){
        return response;
    }
}
