package uiass.eia.gisiba.http.dto;

import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import uiass.eia.gisiba.http.DataSender;
import uiass.eia.gisiba.http.parsers.ProductParser;

public class ProductDto {

//////////////////////////////////////////////////// GET METHODS ///////////////////////////////////////////////////////////////////

    // Find a product by its ref :
    public static List<String> getProductByRef(String ref) {

        String responseBody = DataSender.getDataSender("products/byRef/" + ref);

        if (!responseBody.equals("Server Error.")) return ProductParser.parseProduct(responseBody);

        return null;
    }

    public static boolean checkForAssociatedPurchases(String ref) {

        String responseBody = DataSender.getDataSender("products/checker/byRef/" + ref);

        if (!responseBody.equals("Server Error.")) return Boolean.valueOf(responseBody);

        return false;
    }

    // Find all the products :
    public static List<List<String>> getAllProducts() {

        String responseBody = DataSender.getDataSender("products");

        List<List<String>> products = new ArrayList<List<String>>();

        JsonArray productsArray = new JsonParser().parse(responseBody).getAsJsonArray();

        productsArray.forEach(product -> products.add(ProductParser.parseProduct(String.valueOf(product.getAsJsonObject()))));

        return products;

    }

    // Find all the filtered products :
    public static List<List<String>> getFilteredProducts(String json) {

        String responseBody = DataSender.postDataSender(json,"products/filter");

        List<List<String>> products = new ArrayList<List<String>>();

        JsonArray productsArray = new JsonParser().parse(responseBody).getAsJsonArray();

        productsArray.forEach(product -> products.add(ProductParser.parseProduct(String.valueOf(product.getAsJsonObject()))));

        return products;

    }
 
//////////////////////////////////////////////////// POST METHOD ////////////////////////////////////////////////////////////////

    // Create a new product :
    public static String postProduct(String json) {

        return DataSender.postDataSender(json, "products/post");
    }

//////////////////////////////////////////////////// Delete METHOD /////////////////////////////////////////////////////////////

    public static String deleteProduct(String ref) {

        return DataSender.deleteDataSender("products/delete/" + ref);
    }

//////////////////////////////////////////////////// Put METHOD /////////////////////////////////////////////////////////////

    public static String updateProduct(String ref, String json) {

        if (json != null) return DataSender.putDataSender(json, "products/put/" + ref );

        return "Please provide some new values to update.";
    }
}
