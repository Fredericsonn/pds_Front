package uiass.eia.gisiba.http.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import uiass.eia.gisiba.http.DataSender;
import uiass.eia.gisiba.http.parsers.ContactParser;
import uiass.eia.gisiba.http.parsers.PurchaseParser;

public class PurchaseDto {

//////////////////////////////////////////////////// GET METHODS ///////////////////////////////////////////////////////////////////

    // Find a purchase by its ref :
    public static List getPurchaseById(int id) {

        String responseBody = DataSender.getDataSender("purchases/byId/" + id);

        if (!responseBody.equals("Server Error.")) return PurchaseParser.parsePurchase(responseBody);

        return null;
    }

    // Find all the purchases by supplier type :
    public static List<List<String>> getAllPurchasesByType(String contactType) {

        String responseBody = DataSender.getDataSender("purchases/" + contactType);;

        return PurchaseParser.parsePurchases(responseBody);

    }

    // Find all the purchases :
    public static List<List<String>> getAllSuppliersByType(String type) {

        String responseBody = DataSender.getDataSender("purchases/suppliers/" + type);;

        List<List<String>> suppliers = new ArrayList<List<String>>();

        JsonArray suppliersArray = new JsonParser().parse(responseBody).getAsJsonArray();

        suppliersArray.forEach(supplier -> suppliers.add(ContactParser.parseContact(String.valueOf(supplier.getAsJsonObject()), type)));

        return suppliers;

    }

    // Find all the purchases :
    public static List<List<String>> getAllSuppliers() {

        String personResponseBody = DataSender.getDataSender("purchases/suppliers/Person");

        String enterpriseResponseBody = DataSender.getDataSender("purchases/suppliers/Enterprise");

        List<List<String>> suppliers = new ArrayList<List<String>>();

        JsonArray personSuppliersArray = new JsonParser().parse(personResponseBody).getAsJsonArray();

        JsonArray enterpriseSuppliersArray = new JsonParser().parse(enterpriseResponseBody).getAsJsonArray();

        personSuppliersArray.forEach(supplier -> suppliers.add(ContactParser.parseContact(String.valueOf(supplier.getAsJsonObject()), "Person")));

        enterpriseSuppliersArray.forEach(supplier -> suppliers.add(ContactParser.parseContact(String.valueOf(supplier.getAsJsonObject()), "Enterprise")));

        return suppliers;

    }

    // Find all the purchases :
    public static List<List<String>> getAllPurchasesByStatus(String status) {

        String responseBody = DataSender.getDataSender("purchases/byStatus/" + status);;

        return PurchaseParser.parsePurchases(responseBody);

    }

    // Find all the purchases by supplier type :
    public static List<List<String>> getAllPurchasesBySupplier(String contactType, int id) {

        String responseBody = DataSender.getDataSender("purchases/bySupplier/" + contactType + "/" + id);;

        return PurchaseParser.parsePurchases(responseBody);

    }

    // Find all the purchases :
    public static List<List<String>> getAllPurchases() {

        String responseBody = DataSender.getDataSender("purchases");;

        return PurchaseParser.parsePurchases(responseBody);

    }
 
//////////////////////////////////////////////////// POST METHOD ////////////////////////////////////////////////////////////////

    // Create a new product :
    public static String postPurchase(String json, String supplierType) {

        return DataSender.postDataSender(json, "purchases/" + supplierType + "/post");
    }

    // Find all the purchases by supplier type :
    public static List<List<String>> purchasesFilter(String json) {

        String responseBody = DataSender.postDataSender(json, "purchases/filter");

        if (!responseBody.equals("Server Error.")) return PurchaseParser.parsePurchases(responseBody);

        return null;

    }

//////////////////////////////////////////////////// Delete METHOD /////////////////////////////////////////////////////////////

    public static String deletePurchase(int id) {

        return DataSender.deleteDataSender("purchases/delete/" + id);
    }

    public static String removePurchaseOrder(int purchaseId, int orderId) {

        return DataSender.deleteDataSender("purchases/delete/" + purchaseId + "/removeOrder/" + orderId);
    }

//////////////////////////////////////////////////// Put METHOD /////////////////////////////////////////////////////////////

    public static String updatePurchaseOrders(int id, String json) {

        if (json != null) return DataSender.putDataSender(json, "purchases/put/orders/" + id );

        return "Please provide some new values to update.";
    }

    public static String updatePurchaseStatus(int id, String json) {

        if (json != null) return DataSender.putDataSender(json, "purchases/put/status/" + id );

        return "Please provide some new values to update.";
    }
}
