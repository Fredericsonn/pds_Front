package uiass.eia.gisiba.http.dto;

import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import uiass.eia.gisiba.http.DataSender;
import uiass.eia.gisiba.http.parsers.ProductParser;
import uiass.eia.gisiba.http.parsers.PurchaseParser;

public class OrderDto {


//////////////////////////////////////////////////// PURCHASE ORDERS ///////////////////////////////////////////////////////////////////

                              //////////////// GET METHODS //////////////// 

    // Find a purchase by its ref :
    public static List<String> getOrderById(int id) {

        String responseBody = DataSender.getDataSender("orders/purchaseOrders/byId/" + id);

        if (!responseBody.equals("Server Error.")) return PurchaseParser.parsePurchaseOrder(responseBody);

        return null;
    }

    // Find all the purchases by supplier type :
    public static List<List<String>> getAllPurchaseOrders() {

        String responseBody = DataSender.getDataSender("orders/purchaseOrders");;

        return PurchaseParser.parsePurchaseOrders(responseBody);

    }

    // Find all the purchases by supplier type :
    public static List<List<String>> getAllOrdersByPurchase(int purchaseId) {

        String responseBody = DataSender.getDataSender("orders/purchaseOrders/byPurchase/" + purchaseId);;

        return PurchaseParser.parsePurchaseOrders(responseBody);

    }

                              //////////////// POST METHODS //////////////// 

    // Find all the filtered products :
    public static List<List<String>> getFilteredPurchaseOrders(String json) {

        String responseBody = DataSender.postDataSender(json,"orders/purchaseOrders/filter");

        List<List<String>> orders = new ArrayList<List<String>>();

        JsonArray ordersArray = new JsonParser().parse(responseBody).getAsJsonArray();

        ordersArray.forEach(order -> orders.add(PurchaseParser.parsePurchaseOrder(String.valueOf(order.getAsJsonObject()))));

        return orders;

    }

                              //////////////// PUT METHODS //////////////// 

    // Find all the filtered products :
    public static String updateOrder(String json, int orderId) {

        if (json != null) return DataSender.putDataSender(json,"orders/purchaseOrders/put/" + orderId);

        return "Please provide a quantity to update";



    }

                                  //////////////// DELETE METHODS //////////////// 

    // Find all the filtered products :
    public static String deleteOrder(int orderId) {

        System.out.println(DataSender.deleteDataSender("orders/purchaseOrders/delete/" + orderId));
        return DataSender.deleteDataSender("orders/purchaseOrders/delete/" + orderId);
        
    }


}
