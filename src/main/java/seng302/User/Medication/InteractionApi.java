package seng302.User.Medication;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class InteractionApi {
    private String serverErr = "Internal server error.";
    private String emptyReportErr = "No report comparing the two medications exists.";

    /**
     * Takes two drug names as Strings and returns a Json String from the
     * eHealthMe API which contains information about the drug interactions
     * @param drugA The name of the first drug
     * @param drugB THe name of the other drug
     * @return The Json String
     */
    public String interactions(String drugA, String drugB) {
        String result = apiRequest(String.format("https://www.ehealthme.com/api/v1/drug-interaction/%s/%s/",drugA, drugB));
        if (result.equals(serverErr) || result.equals(emptyReportErr)){
            result = apiRequest(String.format("https://www.ehealthme.com/api/v1/drug-interaction/%s/%s/",drugB, drugA));
        }
        return result;
    }

    /**
     * Takes an API request URI as a parameter and returns a Json String.
     * Also includes error handling for if the drugs are invalid or if there is an internal server error.
     * @param url The api request
     * @return The Json String
     */
    private String apiRequest(String url) {
        try {
            HttpResponse<String> response = Unirest.get(url).asString();
            int n = 0;
            int statusCode = response.getStatus();
            String result = "";
            if (statusCode == 200 || statusCode == 301) {
                n = response.getRawBody().available();
                byte[] bytes = new byte[n];
                response.getRawBody().read(bytes, 0, n);
                result = new String(bytes, StandardCharsets.UTF_8);
            } else {
                switch (statusCode) {
                    case 202:
                        result = emptyReportErr;
                        break;

                    case 404:
                        result = "Invalid comparison.";
                        break;

                    case 502:
                        result = serverErr;
                        break;
                }
            }
            return result;
        } catch (UnirestException e) {
            if(e.getCause() instanceof UnknownHostException || e.getCause() instanceof TimeoutException){
                return "Host could not be reached.";
            }
            return "";
        } catch(Exception e){

            return "";
        }
    }
}
