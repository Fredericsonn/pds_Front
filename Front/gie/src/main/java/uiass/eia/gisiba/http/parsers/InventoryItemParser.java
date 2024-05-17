package uiass.eia.gisiba.http.parsers;

import java.util.*;

import com.google.gson.*;

public class InventoryItemParser extends Parser {

    public static List<String> parseItem(String responseBody) {
    		
        JsonObject itemObject = new JsonParser().parse(responseBody).getAsJsonObject();

        String id = collectString(itemObject, "id");

        String productString = String.valueOf(itemObject.get("product").getAsJsonObject());

        List<String> product = ProductParser.parseProduct(productString);

        String category = product.get(2);

        String brand = product.get(3);

        String model = product.get(4);

        String name = product.get(5);

        String unitPrice = collectString(itemObject, "unitPrice");

        String quantity = collectString(itemObject, "quantity");
        
        String dateAdded = collectString(itemObject, "dateAdded");
    
        return Arrays.asList(id, category, brand, model, name, unitPrice, quantity, dateAdded);
                     
    }

    public static List<String> parseItemProduct(String responseBody) {
    		
        JsonObject itemObject = new JsonParser().parse(responseBody).getAsJsonObject();

        String productString = String.valueOf(itemObject.get("product").getAsJsonObject());

        List<String> product = ProductParser.parseProduct(productString);

        return product;
                     
    }

}
