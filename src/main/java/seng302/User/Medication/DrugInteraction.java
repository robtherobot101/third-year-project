package seng302.User.Medication;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import seng302.User.Attribute.Gender;

public class DrugInteraction {
    private Map<String, HashSet<String>> ageMap;
    private Map<String, HashSet<String>> genderMap;
    private Map<String, HashSet<String>> durationMap;
    private Map<String, String> invertedDurationMap;
    private Boolean error = false;
    private String errorMessage;
    private ArrayList<String> durations;

    /**
     * The constructor for the class. The json string will either be a valid json report, or an error string.
     * The constructor parses it and sets values accordingly.
     * @param json The result of the api call, either a json report or an error message.
     */
    public DrugInteraction(String json) {
        durations = new ArrayList<String>(Arrays.asList(
                "not specified",
                "< 1 month",
                "1 - 6 months",
                "6 - 12 months",
                "1 - 2 years",
                "2 - 5 years",
                "5 - 10 years",
                "10+ years"));
        try {
            JSONObject jsonObj = new JSONObject(json);
            ageMap = getOrCreateMap(jsonObj, "age_interaction");
            genderMap = getOrCreateMap(jsonObj, "gender_interaction");
            durationMap = getOrCreateMap(jsonObj, "duration_interaction");
            invertedDurationMap = invertDurationMap(durationMap);
            error = false;
        } catch (JSONException e) {
            error = true;
            errorMessage = json;
        }
    }

    /**
     * Returns the HashMap from the jsonObj denoted by the given key if it exists,
     * otherwise returns a new HashMap
     * @param jsonObj The drug interaction json object.
     * @param key The identifier of the HashMap to look for
     * @return The HashMap of strings to symptom sets
     */
    public HashMap<String, HashSet<String>> getOrCreateMap(JSONObject jsonObj, String key){
        if(jsonObj.keySet().contains(key)){
            return new Gson().fromJson(jsonObj.get(key).toString(),
                    new TypeToken<HashMap<String, HashSet<String>>>() {}.getType());
        }else{
            return new HashMap<String, HashSet<String>>();
        }
    }

    /**
     * Inverts a duration map so that symptoms map to durations.
     * @param durationMap The original duration map
     * @return The inverted duration map
     */
    public HashMap<String, String> invertDurationMap(Map<String, HashSet<String>> durationMap){
        HashMap<String, String> inverted = new HashMap<>();
        for(String durationKey : durationMap.keySet()){
            for(String symptom : durationMap.get(durationKey)){
                if(inverted.containsKey(symptom)){
                    String greaterDuration = greaterDuration(inverted.get(symptom), durationKey);
                    inverted.put(symptom, greaterDuration);
                }else{
                    inverted.put(symptom, durationKey);
                }
            }
        }
        return inverted;
    }

    /**
     * Returns the longer duration denoted by the duration keys given.
     * "not specified" is treated as the shortest duration.
     * If either of the duration keys is invalid, an IllegalArgumentException
     * is thrown.
     * @param durationA A duration key
     * @param durationB A second duration key
     * @return THe longer duration key
     */
    public String greaterDuration(String durationA, String durationB){
        if(durations.containsAll(Arrays.asList(durationA,durationB))){
            if(durations.indexOf(durationB) > durations.indexOf(durationA)){
                return durationB;
            }else{
                return durationA;
            }
        }else{
            throw new IllegalArgumentException("Invalid durations passed.");
        }
    }

    /**
     * Returns the duration of the given symptom. If the symptom is not known,
     * "not specified" is returned. "not specified" will also be
     * returned for symptoms which have durations explicitly defined
     * as "not specified".
     * @param symptom The given symptom
     * @return The duration of the symptom
     */
    public String getDuration(String symptom){
        if(invertedDurationMap.containsKey(symptom)){
            return invertedDurationMap.get(symptom);
        }else{
            return "not specified";
        }
    }

    /**
     * If the given age falls within a valid age range, the symptoms in that
     * range and all symptoms in the "nan" category are returned. If the symptoms
     * for the range are not defined, only symptoms in the "nan" category are returned.
     * If the given age does not fall within a valid range, an empty set of symptoms is returned.
     * @param age The age in years
     * @return The set of symptoms
     */
    public HashSet<String> ageInteraction(double age) {
        if (age >= 0 && age <= 1) {
            return ageRangeInteraction("0-1");
        } else if (age >= 2 && age <= 9) {
            return ageRangeInteraction("2-9");
        } else if (age >= 10 && age <= 19) {
            return ageRangeInteraction("10-19");
        } else if (age >= 20 && age <= 29) {
            return ageRangeInteraction("20-29");
        } else if (age >= 30 && age <= 39) {
            return ageRangeInteraction("30-39");
        } else if (age >= 40 && age <= 49) {
            return ageRangeInteraction("40-49");
        } else if (age >= 50 && age <= 59) {
            return ageRangeInteraction("50-59");
        } else if (age >= 60) {
            return ageRangeInteraction("60+");
        } else {
            return new HashSet<String>();
        }
    }

    /**
     * Takes a key representing an age range as a String and
     * returns all the drug symptoms for that age range and all
     * symptoms which do not have an associated age range.
     * @param key The key which denotes the age range
     * @return A set of symptoms
     */
    public HashSet<String> ageRangeInteraction(String key){
        if(ageMap.containsKey(key)){
            HashSet<String> results = ageMap.get(key);
            results.addAll(nanAgeInteraction());
            System.out.println("Age Interaction: "+key);
            return results;
        }else{
            return nanAgeInteraction();
        }
    }

    /**
     * Returns the symptoms which do not have an associated age. These symptoms are
     * taken from the "nan"category in the age_interaction map.
     * @return The set of symptoms
     */
    public HashSet<String> nanAgeInteraction(){
        if(ageMap.containsKey("nan")){
            return ageMap.get("nan");
        }else{
            return new HashSet<java.lang.String>();
        }
    }

    /**
     * Returns the set of all symptoms for to male and females
     * @return The set of symptoms
     */
    public HashSet<String> allGenderInteractions(){
        HashSet<String> maleInteractions =  genderInteraction(Gender.MALE);
        HashSet<String> femaleInteractions =  genderInteraction(Gender.FEMALE);
        Set<String> interactions = new HashSet<String>();
        interactions.addAll(maleInteractions);
        interactions.addAll(femaleInteractions);
        return new HashSet<String>(interactions);
    }

    /**
     * Returns the interaction symptoms for males
     * @return The set of symptoms
     */
    public HashSet<String> maleInteractions(){
        if(genderMap.containsKey("male")){
            return genderMap.get("male");
        }else{
            return new HashSet<String>();
        }
    }

    /**
     * Returns the set of interaction symptoms for females
     * @return The set of symptoms
     */
    public HashSet<String> femaleInteractions(){
        if(genderMap.containsKey("female")){
            return genderMap.get("female");
        }else{
            return new HashSet<String>();
        }
    }

    /**
     * Returns the set of symptoms for the given gender. If the gender is null or OTHER,
     * all symptoms for males and females are returned.
     * @param gender The given gender
     * @return The set of symptoms
     */
    public HashSet<String> genderInteraction(Gender gender){
        if(gender == null){
            //TODO
            // decide whether this is the correct way to handle a null gender
            return allGenderInteractions();
        }else if (gender.equals(Gender.MALE)) {
            return maleInteractions();
        } else if (gender.equals(Gender.FEMALE)) {
            return femaleInteractions();
        } else if(gender.equals(Gender.OTHER)){
            return allGenderInteractions();
        } else {
            return new HashSet<String>();
        }
    }

    /**
     * Returns the HashMap of symptoms grouped by duration.
     * @return The HashMap of symptoms
     */
    public HashMap<String, HashSet<String>> getDurationInteraction(){
        return new HashMap<>(durationMap);
    }


    /**
     * Returns true if there was an error passing or fetching the json
     * @return True if there was an error, otherwise false
     */
    public Boolean getError() {
        return error;
    }

    /**
     * Returns the error message if one was generated while passing or fetching the json.
     * @return The error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}