package seng302.User.Medication;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import java.nio.charset.StandardCharsets;

public class Mapi {

    /**
     * Returns all the medicines that match the string passed in by user.
     *
     * @param query The string to auto complete.
     * @return Returns an ArrayList of strings of the matching medicines.
     */
    public static String autocomplete(String query) {
            query = query.replace(" ", "+");
            query = query.replace("%", "%25");
            return apiRequest(String.format("https://iterar-mapi-us.p.mashape.com/api/autocomplete?query=%s", query));
    }

    /**
     * Gets all the active ingredients of a given medicine.
     *
     * @param medicine The medicine to get the active ingredients of.
     * @return Returns the active ingredients as a string arraylist
     */
    public static String activeIngredients(String medicine) {
        medicine = medicine.replace(" ", "+");
        medicine = medicine.replace("%", "%25");
        return apiRequest(String.format("https://iterar-mapi-us.p.mashape.com/api/%s/substances.json", medicine));
    }

    /**
     * Sends the api requests to MAPI.
     *
     * @param url The api url to call.
     * @return returns a String of the result of the api request.
     */
    private static String apiRequest(String url) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(url)
                    .header("X-Mashape-Key", "yqCc8Xzox7mshwvnVGeVGRhqb5q7p1QFwldjsnkT3j48eJ4Zfj")
                    .header("Accept", "application/json")
                    .asJson();
            int n;
            n = response.getRawBody().available();
            byte[] bytes = new byte[n];
            response.getRawBody().read(bytes, 0, n);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }
}
