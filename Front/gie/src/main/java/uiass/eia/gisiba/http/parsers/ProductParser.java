package uiass.eia.gisiba.http.parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ProductParser extends Parser {

    private static List<String> productAttributes = Arrays.asList("productRef","category","name","description");

    private static List<String> product_creation__columns = Arrays.asList("name","description");

    private static List<String> product_update__columns = Arrays.asList("name","description");

    private static List<String> category_columns = Arrays.asList("categoryName","brandName", "modelName");

    private static List<String> search_columns = Arrays.asList("categoryName","brandName", "modelName");


//////////////////////////////////////////////// MAPS AND JSON GENERATORS ///////////////////////////////////////////////////////////

    public static Map<String,String> productCreationMapGenerator(List<String> values) {

        Map<String,String> map = new HashMap<String,String>();

        List<String> attributes = product_creation__columns;
        
        for (int i=0 ; i < attributes.size() ; i++) {

            map.put(attributes.get(i), values.get(i));

        }

        return map;
    }

    public static Map<String,Object> categoryCreationMapGenerator(List<String> values) {

        Map<String,Object> map = new HashMap<String,Object>();

        List<String> attributes = category_columns;
        
        for (int i=0 ; i < attributes.size() ; i++) {

            map.put(attributes.get(i), values.get(i));

        }

        return map;
    }

    public static Map<String,Object> productUpdateMapGenerator(List<String> values) {

        Map<String,Object> map = new HashMap<String,Object>();

        List<String> attributes = product_update__columns;

        for (int i=0 ; i < attributes.size() ; i++) {

            String attribute = attributes.get(i);

            String value = values.get(i);

            if (value != "null" && !value.equals("")) map.put(attribute, value);

        }

        return map;
    }

    public static Map<String,Object> categoryUpdateMapGenerator(List<String> values) {

        Map<String,Object> map = new HashMap<String,Object>();

        List<String> attributes = category_columns;

        for (int i=0 ; i < attributes.size() ; i++) {

            String attribute = attributes.get(i);

            String value = values.get(i);

            if (value != null && !value.equals("")) map.put(attribute, value);

        }

        return map;
    }

    public static Map<String,Object> filteredProductSearchMapGenerator(List<String> values) {

        Map<String,Object> map = new HashMap<String,Object>();

        List<String> attributes = search_columns;

        for (int i=0 ; i < attributes.size() ; i++) {

            String attribute = attributes.get(i);

            String value = values.get(i);

            if (value != null) map.put(attribute, value);

        }

        return map;
    }

    public static Map<String,Object> categoryFilter(List<String> values) {

        Map<String,Object> map = new HashMap<String,Object>();

        List<String> attributes = search_columns;

        for (int i=0 ; i < attributes.size() ; i++) {

            String attribute = attributes.get(i);

            String value = values.get(i);

            if (value != null) map.put(attribute, value);

        }

        return map;

    }

    public static String updateProductJsonGenerator(Map<String,Object> productMap, Map<String,Object> categoryMap) {

        if (!categoryMap.isEmpty()) productMap.put("category", categoryMap);

        return Parser.jsonGenerator(productMap);
    }
    
//////////////////////////////////////////////////////// PARSERS /////////////////////////////////////////////  ////////////////////////

    public static List<String> parseProduct(String responseBody) {
    		
        JsonObject productObject = new JsonParser().parse(responseBody).getAsJsonObject();
    
        List<String> productStringInfo = new ArrayList<String>();
        
        List<String> categoryInfo = new ArrayList<String>();
    
        // The list containing attributes names used to parse the json
        List<String> attributes = productAttributes;
    
        // We iterate through our attributes and call the collectors to collect the values from the json
        for (String attribute : attributes) {

            if (attribute.equals("category")) {  // if we reach the nested category json object 

                JsonObject categoryObject = productObject.get("category").getAsJsonObject();

                categoryInfo.add(String.valueOf(collectInt(categoryObject, "id")));

                categoryInfo.add(collectString(categoryObject, "categoryName"));

                categoryInfo.add(collectString(categoryObject, "brandName"));

                categoryInfo.add(collectString(categoryObject, "modelName"));
            }
            
            else productStringInfo.add(collectString(productObject, attribute));
        }
    
        String ref = productStringInfo.get(0); 

        String categoryId = categoryInfo.get(0);

        String category = categoryInfo.get(1);
    
        String brand = categoryInfo.get(2);
    
        String model = categoryInfo.get(3);

        String name = productStringInfo.get(1);
    
        String description = productStringInfo.get(2);
    
        return Arrays.asList(ref, categoryId, category, brand, model, name, description);
                     
    }


    public static List<String> parseCategory(String responseBody) {

        JsonObject categoryObject = new JsonParser().parse(responseBody).getAsJsonObject();

        List<String> attributes = category_columns;

        List<String> category = new ArrayList<String>();

        attributes.forEach(attribute -> category.add(collectString(categoryObject, attribute)));

        return category;

    }
}
