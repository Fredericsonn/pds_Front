package uiass.eia.gisiba.http.parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uiass.eia.gisiba.http.GetGson;

// This class contain all the diverse parsing and data structures generating methods 
public class Parser {

 

    public static String jsonGenerator(Map<String,Object> attributes) {

        Gson gson = GetGson.getGson();

        if (!attributes.isEmpty()) return gson.toJson(attributes);

        return null;
    }

    public static String jsonGenerator(List<Map<String,Object>> attributes) {

        Gson gson = GetGson.getGson();

        if (!attributes.isEmpty()) return gson.toJson(attributes);

        return null;
    }
    
    public static String collectString(JsonObject jsObj, String attribute) {
        JsonElement element = jsObj.get(attribute);

        return element != null ? String.valueOf(element.getAsString()) : null;
    }

    public static int collectInt(JsonObject jsObj, String attribute) {
        JsonElement element = jsObj.get(attribute);

        return element != null ? Integer.valueOf(element.getAsString()) : null;
    }

    public static double collectDouble(JsonObject jsObj, String attribute) {

        JsonElement element = jsObj.get(attribute);

        return element != null ? Double.valueOf(element.getAsString()) : null;
    }



    



}
