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

    public String interactions(String drugA, String drugB) {
        //String result = apiRequest(String.format("https://www.ehealthme.com/api/v1/drug-interaction/%s/%s",drugA, drugB));
        String result = apiRequest("https://www.ehealthme.com/api/v1/drug-interaction/digoxin/amiodarone-hydrochloride/");
        //System.out.println("Querying "+ String.format("https://www.ehealthme.com/api/v1/drug-interaction/%s/%s",drugA, drugB));
        String[] temp = result.split("\\[");
        //result = temp[1];
        //if (result.length() > 4) {
        //    result = result.substring(1, result.length() - 3);
        //} else {
        //    result = "";
        //}
        //temp = result.split("\",\"");
        //return new ArrayList<String>(Arrays.asList(temp));
        return result;
    }


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
