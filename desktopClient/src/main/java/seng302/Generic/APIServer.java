package seng302.Generic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class APIServer {
    private String url;

    public APIServer(String url){
        this.url = url;
    }

    public boolean testConnection() throws IOException {
        URLConnection request = (new URL(url + "/hello")).openConnection();
        request.connect();

        // Convert to a JSON object to print data
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
        JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
        String version = rootobj.get("version").getAsString(); //just grab the zipcode
        System.out.println(version);
        return version.equals("1");
    }
}
