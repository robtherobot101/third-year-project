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
        JSONObject jsonObj = new JSONObject(json);
        ageMap = new Gson().fromJson(jsonObj.get("age_interaction").toString(),
                new TypeToken<HashMap<String, HashSet<String>>>() {}.getType());
        genderMap = new Gson().fromJson(jsonObj.get("age_interaction").toString(),
                new TypeToken<HashMap<String, HashSet<String>>>() {}.getType());
        durationMap = new Gson().fromJson(jsonObj.get("duration_interaction").toString(),
                new TypeToken<HashMap<String, HashSet<String>>>() {}.getType());

    }

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

    public HashSet<String> ageRangeInteraction(String key){
        if(ageMap.containsKey(key)){
            return ageMap.get(key);
        }else{
            return new HashSet<String>();
        }
    }

    public HashSet<String> intersexInteractions(){
        HashSet<String> maleInteractions =  genderInteraction(Gender.MALE);
        HashSet<String> femaleInteractions =  genderInteraction(Gender.FEMALE);
        Set<String> interactions = new HashSet<String>();
        interactions.addAll(maleInteractions);
        interactions.addAll(femaleInteractions);
        return new HashSet<String>(interactions);
    }

    public HashSet<String> maleInteractions(){
        return (genderMap.containsKey("male")) ? genderMap.get("male") : new HashSet<String>();
    }

    public HashSet<String> femaleInteractions(){
        return (genderMap.containsKey("female+")) ? genderMap.get("female") : new HashSet<String>();
    }

    public HashSet<String> genderInteraction(Gender gender){
        if(gender == null){
            //TODO
            // decide whether this is the correct way to handle a null gender
            return intersexInteractions();
        }else if (gender.equals(Gender.MALE)) {
            return maleInteractions();
        } else if (gender.equals(Gender.FEMALE)) {
            return femaleInteractions();
        } else if(gender.equals(Gender.OTHER)){
            return intersexInteractions();
        } else {
            return new HashSet<String>();
        }
    }

    public HashMap<String, HashSet<String>> getDurationInteraction(){
        return new HashMap<>(durationMap);
    }
}