package seng302.Generic;

import java.util.*;

public class QueryBuilder {
    private Map<String,String> parameters;
    public QueryBuilder(){
        parameters = new HashMap<>();
    }

    public void addParameter(String key, String value){
        parameters.put(key,value);
    }

    @Override
    public String toString(){
        List<String> parameterList = new ArrayList<String>();
        for(String key:parameters.keySet()){
            parameterList.add(key + "=" + parameters.get(key));
        }
        return String.join("&",parameterList);
    }
}
