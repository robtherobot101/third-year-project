package seng302.User.Medication;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import seng302.generic.Cache;
import seng302.generic.Debugger;

import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Requests interactions from the medication interaction API
 */
public class InteractionApi{
    private static InteractionApi instance = null;
    private static Cache cache;
    private static String serverErr = "Could not retrieve interaction symptoms (an error occurred on the server).";
    private static String emptyReportErr = "Could not retrieve interaction symptoms (no information available).";

    private static String endpoint = "https://www.ehealthme.com/api/v1/drug-interaction/";

    private InteractionApi() {
    }

    public static InteractionApi getInstance() {
        if(instance == null) {
            instance = new InteractionApi();
        }
        return instance;
    }

    /**
     * Sets the cache for the API to the given cache
     *
     * @param cacheArg The given cache.
     */
    public static void setCache(Cache cacheArg){
        cache =  cacheArg;
    }

    /**
     * Takes two drug names as Strings and returns a Json String from the
     * eHealthMe API which contains information about the drug interactions
     *
     * The cache must be set before calling this method
     *
     * @param drugA The name of the first drug
     * @param drugB The name of the other drug
     * @return The Json String
     */
    public static DrugInteraction interactions(String drugA, String drugB) {
        List<String> drugs = Arrays.asList(drugA, drugB);
        java.util.Collections.sort(drugs);

        String query = String.format("%s/%s/", drugs.get(0), drugs.get(1));
        String reversedQuery = String.format("%s/%s/", drugs.get(1), drugs.get(0));

        String apiResponse;
        if(cache.contains(query)){
            System.out.println("Response taken from cache");
            return new DrugInteraction(cache.get(query));
        } else {
            System.out.println("Response not in cache. Taken from API");
            apiResponse = apiRequest(String.format(endpoint + "%s", query));
            if (apiResponse.equals(serverErr) || apiResponse.equals(emptyReportErr)) {
                apiResponse = apiRequest(String.format(endpoint + "%s", reversedQuery));
            }
        }

        DrugInteraction interactions = new DrugInteraction(apiResponse);
        if(!interactions.getError()){
            cache.put(query,apiResponse);
            Debugger.log(String.format("API response added to cache with key: %s/%s/",drugA,drugB));
            cache.save();
        }
        return interactions;
    }

    /**
     * Takes an API request URI as a parameter and returns a Json String.
     * Also includes error handling for if the drugs are invalid or if there is an internal server error.
     *
     * @param url The api request
     * @return The Json String
     */
    private static String apiRequest(String url) {
        try {
            HttpResponse<String> response = Unirest.get(url).asString();
            int n;
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

                    case 502:
                        result = serverErr;
                        break;

                    case 401:
                        result = "Too many requests. Check back later.";
                        break;

                    case 404:
                        result = "Invalid comparison.";
                        break;
                }
            }
            return result;
        } catch (UnirestException e) {
            if (e.getCause() instanceof UnknownHostException || e.getCause() instanceof TimeoutException) {
                return "Host could not be reached.";
            }
            return "";
        } catch (Exception e) {

            return "";
        }
    }
}
