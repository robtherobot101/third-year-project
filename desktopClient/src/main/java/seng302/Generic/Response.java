package seng302.Generic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

public class Response {
    private String response;
    private JsonParser jp;
    public Response(String response){
        jp = new JsonParser();
        this.response = response;
    }

    public boolean isValidJson(){
        try{
            jp.parse(response).getAsJsonObject();
        }catch(JsonSyntaxException e){
            return false;
        }
        return true;
    }

    public JsonObject getAsJsonObject(){
        return jp.parse(response).getAsJsonObject();
    }

    public String getAsString(){
        return response;
    }
}
