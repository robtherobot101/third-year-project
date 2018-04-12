package seng302.Core;

import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class InteractionApi {

    /**
     * Takes two drug names as Strings and returns a Json String from the
     * eHealthMe API which contains information about the drug interactions
     * @param drugA The name of the first drug
     * @param drugB THe name of the other drug
     * @return The Json String
     */
    public String interactions(String drugA, String drugB) {
        String result = apiRequest(String.format("https://www.ehealthme.com/api/v1/drug-interaction/%s/%s/",drugA, drugB));
        return result;
    }

    /**
     * Takes an API request URI as a parameter and returns a Json String
     * from the API
     * @param url The api request
     * @return The Json String
     */
    private String apiRequest(String url) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(url)
                .asJson();
            int n = 0;
            n = response.getRawBody().available();
            byte[] bytes = new byte[n];
            response.getRawBody().read(bytes, 0, n);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println(e);

            return "";
        }
    }
}
