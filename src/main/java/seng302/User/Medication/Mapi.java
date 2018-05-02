package seng302.User.Medication;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Mapi {

    /**
     * Returns all the medicines that match the string passed in by user.
     * @param query The string to auto complete.
     * @return Returns an ArrayList of strings of the matching medicines.
     */
    public static ArrayList<String> autocomplete(String query) {
        try {
            query = query.replace(" ", "+");
            query = query.replace("%", "%25");
            String result = apiRequest(String.format("https://iterar-mapi-us.p.mashape.com/api/autocomplete?query=%s", query));
            String[] temp = result.split("\\[");
            result = temp[1];
            if (result.length() > 4) {
                result = result.substring(1, result.length() - 3);
            } else {
                result = "";
            }
            temp = result.split("\",\"");
            System.out.println(Arrays.toString(temp));

            return new ArrayList<String>(Arrays.asList(temp));
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("aioobe");
            return new ArrayList<String>();
        }
    }

    /**
     * Gets all the active ingredients of a given medicine.
     * @param medicine The medicine to get the active ingredients of.
     * @return Returns the active ingredients as a string arraylist
     */
    public static ArrayList<String> activeIngredients(String medicine) {
        medicine = medicine.replace(" ", "+");
        medicine = medicine.replace("%", "%25");
        String result = apiRequest(String.format("https://iterar-mapi-us.p.mashape.com/api/%s/substances.json", medicine));
        if (result.length() > 4) {
            result = result.substring(2, result.length() - 2);
        } else {
            result = "";
        }
        String[] temp = result.split("\",\"");
        return new ArrayList<String>(Arrays.asList(temp));
    }

    /**
     * Sends the api requests to MAPI.
     * @param url The api url to call.
     * @return returns a String of the result of the api request.
     */
    private static String apiRequest(String url) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(url)
                    .header("X-Mashape-Key", "yqCc8Xzox7mshwvnVGeVGRhqb5q7p1QFwldjsnkT3j48eJ4Zfj")
                    .header("Accept", "application/json")
                    .asJson();
            int n = 0;
            n = response.getRawBody().available();
            byte[] bytes = new byte[n];
            response.getRawBody().read(bytes, 0, n);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Test function to test if the api works
     */
    public void main() {
        //System.out.println(autocomplete("res").toString());
        //System.out.println(activeIngredients("reserpine"));
    }


}
