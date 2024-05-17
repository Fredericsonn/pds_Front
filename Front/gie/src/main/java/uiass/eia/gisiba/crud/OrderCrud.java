package uiass.eia.gisiba.crud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import uiass.eia.gisiba.controller.FXManager;
import uiass.eia.gisiba.http.dto.OrderDto;
import uiass.eia.gisiba.http.dto.ProductDto;

public class OrderCrud {



    // a method that sets the order table's columns event listeners
    public static void orderTableEventHandler(TableView ordersTableView, List<Label> labels, Parent pane, AnchorPane refresh,
    
        Button view, Button stats) {

        ordersTableView.setOnMouseClicked(event -> {
            if (!ordersTableView.getSelectionModel().isEmpty()) {

                // We get the selected row and extract the values
                List<String> selectedItem = (List<String>) ordersTableView.getSelectionModel().getSelectedItem();
                int orderId = Integer.parseInt(selectedItem.get(0));
                int itemId = Integer.parseInt(selectedItem.get(1));
                String category = selectedItem.get(2);
                String brand = selectedItem.get(3);
                String model = selectedItem.get(4);
                String name = selectedItem.get(5);
                String unitPrice = selectedItem.get(6);
                String quantity = selectedItem.get(7);
                String orderDateTime = selectedItem.get(8);

                // We put all the values in one list that we'll use to fill the labels
                //List<String> values = Arrays.asList(category,brand,model,name,unitPrice,quantity,orderTime,purchaseDate);

                List<String> valuesToShow = Arrays.asList(category,brand + " " + model + " " + name,"Price : " + unitPrice 
                
                + "$", "Quantity Ordered : " + quantity, "Date : " + orderDateTime);

                // We use the extracted values to fill the labels
                FXManager.labelsFiller(labels, valuesToShow);

                // We finally show the right pane
                pane.setVisible(true);

                view.setOnAction(view_event -> {

                    goToViewOrderPane();
                });

                // When the stats button is clicked
                stats.setOnAction(stats_event -> {


                });
           
        } 
            
        });
    }

    public static void fillWithPurchaseOrders(TableView productsTable) {

        // We send an http get request to get all the orders 
        List<List<String>> data = OrderDto.getAllPurchaseOrders();  

        // We populate the table using those collected orders
        List<String> columns = FXManager.order_columns;
        
        FXManager.populateTableView(productsTable, columns, Arrays.asList("order id", "item id","category"), data);
    }

    public static void fillWithFilteredPurchasedOrders(TableView productsTable, List<List<String>> data) {
        
        // The columns we'll use for the table
        List<String> columns = FXManager.order_columns;
        
        FXManager.populateTableView(productsTable, columns, Arrays.asList("order id", "item id","category"), data);
    }

    public static void goToViewOrderPane() {

    }

    public static void goToOrderStatsPane() {

    }
}
