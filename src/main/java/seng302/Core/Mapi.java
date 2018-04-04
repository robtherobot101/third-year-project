package seng302.Core;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Mapi {

    /**
     * Returns all the medicines that match the string passed in by user.
     * @param query The string to auto complete.
     * @return Returns an ArrayList of strings of the matching medicines.
     */
    private ArrayList<String> autocomplete(String query) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(String.format("https://iterar-mapi-us.p.mashape.com/api/autocomplete?query=%s",query))
                    .header("X-Mashape-Key", "yqCc8Xzox7mshwvnVGeVGRhqb5q7p1QFwldjsnkT3j48eJ4Zfj")
                    .header("Accept", "application/json")
                    .asJson();
            int n = response.getRawBody().available();
            byte[] bytes = new byte[n];
            response.getRawBody().read(bytes, 0, n);
            String s = new String(bytes, StandardCharsets.UTF_8);
            String[] t = s.split("\\[");
            s = t[1];
            if (s.length() > 4) {
                s = s.substring(1, s.length() - 3);
            } else {
                s = "";
            }
            t = s.split("\",\"");
            return new ArrayList<String>(Arrays.asList(t));
        } catch (UnirestException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Test function to test if the api works
     */
    public void main() {
        System.out.println(autocomplete("res").toString());
    }


}
