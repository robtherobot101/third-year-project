package seng302.Core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.util.*;

public class DrugInteraction {
    private Map<String, HashSet<String>> ageMap;
    private Map<String, HashSet<String>> genderMap;
    private Map<String, HashSet<String>> durationMap;

    public DrugInteraction(String json) {
        Gson gson = new Gson();
        JSONObject jsonObj = new JSONObject(json);
        Map<String, HashSet<String>> ageMap = new Gson().fromJson(jsonObj.get("age_interaction").toString(), new TypeToken<HashMap<String, HashSet<String>>>() {}.getType());
        ageMap = new Gson().fromJson(jsonObj.get("age_interaction").toString(), new TypeToken<HashMap<String, HashSet<String>>>() {}.getType());
        genderMap = new Gson().fromJson(jsonObj.get("age_interaction").toString(), new TypeToken<HashMap<String, HashSet<String>>>() {}.getType());
        genderMap = new Gson().fromJson(jsonObj.get("age_interaction").toString(), new TypeToken<HashMap<String, HashSet<String>>>() {}.getType());

    }

    public HashSet<String> ageInteraction(int age) {
        if (age >= 0 && age <= 1) {
            return (ageMap.containsKey("0-1")) ? ageMap.get("0-1") : new HashSet<String>();
        } else if (age >= 2 && age <= 9) {
            return (ageMap.containsKey("2-9")) ? ageMap.get("2-9") : new HashSet<String>();
        } else if (age >= 10 && age <= 19) {
            return (ageMap.containsKey("10-19")) ? ageMap.get("10-19") : new HashSet<String>();
        } else if (age >= 20 && age <= 29) {
            return (ageMap.containsKey("20-29")) ? ageMap.get("20-29") : new HashSet<String>();
        } else if (age >= 30 && age <= 39) {
            return (ageMap.containsKey("30-39")) ? ageMap.get("30-39") : new HashSet<String>();
        } else if (age >= 40 && age <= 49) {
            return (ageMap.containsKey("40-49")) ? ageMap.get("40-49") : new HashSet<String>();
        } else if (age >= 50 && age <= 59) {
            return (ageMap.containsKey("50-59")) ? ageMap.get("50-59") : new HashSet<String>();
        } else if (age >= 60) {
            return (ageMap.containsKey("60+")) ? ageMap.get("60+") : new HashSet<String>();
        } else {
            return new HashSet<String>();
        }
    }

    public HashSet<String> genderInteraction(Gender gender){
        if (gender.equals(Gender.MALE)) {
            return (genderMap.containsKey("male")) ? genderMap.get("male") : new HashSet<String>();
        } else if (gender.equals(Gender.FEMALE)) {
            return (genderMap.containsKey("female+")) ? genderMap.get("female") : new HashSet<String>();
        } else if(gender.equals(Gender.OTHER)){
            HashSet<String> maleInteractions =  genderInteraction(Gender.MALE);
            HashSet<String> femaleInteractions =  genderInteraction(Gender.FEMALE);
            Set<String> interactions = new HashSet<String>();
            interactions.addAll(maleInteractions);
            interactions.addAll(femaleInteractions);
            return new HashSet<String>(interactions);
        }else{
            return new HashSet<String>();
        }
    }

    public HashSet<String> durationInteraction(int months){
        HashSet<String> durationInteractions = new HashSet<String>();
        if (months < 1) {
            durationInteractions = (durationMap.containsKey("< 1 month")) ? durationMap.get("< 1 month") : new HashSet<String>();
        } else if (months >=1 && months < 6) {
            durationInteractions = (durationMap.containsKey("1 - 6 months")) ? durationMap.get("1 - 6 months") : new HashSet<String>();
        } else if (months >= 6 && months < 12) {
            durationInteractions = (durationMap.containsKey("6 - 12 months")) ? durationMap.get("6 - 12 months") : new HashSet<String>();
        } else if (months >= 12 && months < 24) {
            durationInteractions = (durationMap.containsKey("1 - 2 years")) ? durationMap.get("1 - 2 years") : new HashSet<String>();
        } else if (months >= 24 && months < 60) {
            durationInteractions = (durationMap.containsKey("2 - 5 years")) ? durationMap.get("2 - 5 years") : new HashSet<String>();
        } else if (months >= 60 && months < 120) {
            durationInteractions = (durationMap.containsKey("5 - 10 years")) ? durationMap.get("5 - 10 years") : new HashSet<String>();
        } else if (months >= 120) {
            durationInteractions = (durationMap.containsKey("10+ years")) ? durationMap.get("10+ years") : new HashSet<String>();
        }
        
        if(durationMap.containsKey("not specified")){
            durationInteractions.addAll(durationMap.get("not specified"));
        }
        return durationInteractions;
    }
}