package uiass.eia.gisiba.http.parsers;

import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PurchaseParser extends Parser {

    public static List<String> parsePurchase(String json) {

        JsonObject purchaseObject = new JsonParser().parse(json).getAsJsonObject();

        String purchaseId = collectString(purchaseObject, "purchaseId");
        String purchaseDate = collectString(purchaseObject, "purchaseDate");
        String total = collectString(purchaseObject, "total");
        String status = collectString(purchaseObject, "status");

        JsonObject supplierObject = purchaseObject.get("supplier").getAsJsonObject();

        String supplierType = supplierObject.has("type") ? "Enterprise" : "Person";

        List<String> supplier = ContactParser.parseContact(String.valueOf(supplierObject), supplierType);

        String supplierName = supplier.get(1);

        if (supplierType.equals("Person"))  supplierName += " " + supplier.get(2);

        String supplierId = supplier.get(0);

        return Arrays.asList(purchaseId, supplierId, supplierName, supplierType, purchaseDate, total, status);
    }

    public static List<String> parsePurchaseOrder(String json) {

        JsonObject orderObject = new JsonParser().parse(json).getAsJsonObject();

        String orderId = collectString(orderObject, "orderId");

        String quantity = collectString(orderObject, "quantity");

        String orderTime = collectString(orderObject, "orderTime");

        String purchaseDate = collectString(orderObject, "purchaseDate");

        JsonObject itemOject = orderObject.get("item").getAsJsonObject();

        List<String> item = InventoryItemParser.parseItem(String.valueOf(itemOject));

        String itemId = item.get(0);

        String category = item.get(1);

        String brand = item.get(2);

        String model = item.get(3);

        String name = item.get(4);

        String unitPrice = item.get(5);

        return Arrays.asList(orderId, itemId, category, brand, model, name, unitPrice, quantity, purchaseDate + ", " + orderTime);
    }
    public static List<List<String>> parsePurchaseOrders(String json) {

        JsonArray ordersArray = new JsonParser().parse(json).getAsJsonArray();

        List<List<String>> orders = new ArrayList<>();
    
        ordersArray.forEach(order -> {

            JsonObject orderObject = order.getAsJsonObject();

            orders.add(parsePurchaseOrder(orderObject.toString()));

        });
    
        return orders;
    }
    
    public static List<List<String>> parsePurchases(String json) {
        
        List<List<String>> purchases = new ArrayList<List<String>>();

        JsonArray purchasesArray = new JsonParser().parse(json).getAsJsonArray();

        purchasesArray.forEach(purchase -> purchases.add(PurchaseParser.parsePurchase(String.valueOf(purchase.getAsJsonObject()))));

        return purchases;
    }

    public static Map<String,Object> purchasesDatesFilterMapGenerator(String type, List<String> values) {

        Map<String,Object> map = new HashMap<String,Object>();

        if (type.equals("after")) {

            String beforeDate = values.get(0);

            map.put("afterDate", beforeDate);
        }

        else if (type.equals("before")) {

            String afterDate = values.get(1);

            map.put("beforeDate", afterDate);
        }

        else if (type.equals("between")) {

            String startDate = values.get(0);

            String endDate = values.get(1);

            map.put("startDate", startDate);

            map.put("endDate", endDate);
        }

        return map;
    }

    public static Map<String,Object> purchasesSupplierFilterMapGenerator(String supplierName, String supplierType) {

        Map<String,Object> supplierMap = new HashMap<String,Object>();

        supplierMap.put("supplierName", supplierName);

        supplierMap.put("supplierType", supplierType);

        return supplierMap;
    }

    
}
