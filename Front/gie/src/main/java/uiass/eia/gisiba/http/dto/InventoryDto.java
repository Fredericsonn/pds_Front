package uiass.eia.gisiba.http.dto;

import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import uiass.eia.gisiba.http.DataSender;
import uiass.eia.gisiba.http.parsers.InventoryItemParser;
import uiass.eia.gisiba.http.parsers.ProductParser;

public class InventoryDto {

    public static List<String> getItemById(int id) {

        String responseBody = DataSender.getDataSender("inventoryItems/byId/" + id);

        if (!responseBody.equals("Server Error.")) return InventoryItemParser.parseItem(responseBody);

        return null;
    }

    public static List<String> getProductByItemId(int id) {

        String responseBody = DataSender.getDataSender("inventoryItems/byId/" + id);

        if (!responseBody.equals("Server Error.")) return InventoryItemParser.parseItemProduct(responseBody);

        return null;
    }

    public static List<String> getItemByProduct(String ref) {

        String responseBody = DataSender.getDataSender("inventoryItems/byProduct/" + ref);

        if (!responseBody.equals("Server Error.")) return InventoryItemParser.parseItemProduct(responseBody);

        return null;
    }

    public static List<List<String>> getAllItems() {

        List<List<String>> items = new ArrayList<List<String>>();

        String json = DataSender.getDataSender("inventoryItems");

        JsonArray itemsArray = new JsonParser().parse(json).getAsJsonArray();

        itemsArray.forEach(itemJson -> {

            items.add(InventoryItemParser.parseItem(String.valueOf(itemJson.getAsJsonObject())));
        });

        return items;
    }

    // Find all the products :
    public static List<List<String>> getFilteredItems(String json) {

        String responseBody = DataSender.postDataSender(json,"inventoryItems/filter");

        List<List<String>> items = new ArrayList<List<String>>();

        JsonArray itemsArray = new JsonParser().parse(responseBody).getAsJsonArray();

        itemsArray.forEach(item -> items.add(InventoryItemParser.parseItem(String.valueOf(item.getAsJsonObject()))));

        return items;

    }

//////////////////////////////////////////////////// Put METHOD /////////////////////////////////////////////////////////////

public static String updateItemPrice(int id, String json) {

    if (json != null) return DataSender.putDataSender(json, "inventoryItem/put/unitPrice/" + id );

    return "Please provide a price.";
}
}
