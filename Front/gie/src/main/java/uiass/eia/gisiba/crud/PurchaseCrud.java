package uiass.eia.gisiba.crud;


import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import uiass.eia.gisiba.FX.PurchaseFX;
import uiass.eia.gisiba.controller.FXManager;
import uiass.eia.gisiba.http.dto.ContactDto;
import uiass.eia.gisiba.http.dto.OrderDto;
import uiass.eia.gisiba.http.dto.PurchaseDto;
import uiass.eia.gisiba.http.parsers.Parser;
import uiass.eia.gisiba.http.parsers.PurchaseParser;

public class PurchaseCrud {

    public static void create_orders(Parent pane) {

        // We collect the buttons from the fxml file
        Button search = FXManager.getButton(pane, "searchBtn");
        Button next = FXManager.getButton(pane, "nextBtn");
        Button addOrder = FXManager.getButton(pane, "addOrderBtn");
        AnchorPane refresh = FXManager.getAnchorPane(pane, "refreshBtn");

        // Table
        TableView productsTable = FXManager.getTableView(pane, "productsTable");

        // Hbox
        HBox addHbox = FXManager.getHBox(pane, "addHbox");

        // cart anchor pane
        AnchorPane cart = FXManager.getAnchorPane(addHbox, "cartContainer");

        // number of cart items image
        ImageView itemNumberImg = FXManager.getImageView(cart, "itemNumberImg");

        ImageView cartImg = FXManager.getImageView(cart, "cartImg");


        // Text Field
        TextField quantityTextField = FXManager.getTextField(pane, "quantityTextField");
        FXManager.setTextFieldNumericFormatRule(quantityTextField); // numeric input only rule

        // Combo Box
        List<ComboBox> comboBoxes = ProductCrud.productSearchComboBoxesHandler(pane); // We get and fill the combo boxes

        InventoryItemCrud.fillWithItemsForPurchase(productsTable); // we fill the table with products

        // a list to store the items ids and quantities
        List<Map<String, Object>> orders = new ArrayList<>();

        // When we press the search button
        PurchaseFX.productSelectionSearchHandler(search, productsTable, comboBoxes);

        PurchaseFX.productSelectionSavingHandler(productsTable, addHbox, quantityTextField, addOrder, cart, itemNumberImg, cartImg, orders);

        // We set the refresh button to refresh the table when clicked
        refresh.setOnMouseClicked(imageClicked -> ProductCrud.fillWithProducts(productsTable));

        next.setOnAction(event -> {

            if (!orders.isEmpty()) {

                goToSupplierSelectionPane(orders);

                ((Stage) pane.getScene().getWindow()).close(); // We close the products selection page after pressing the next button
            }

            else FXManager.showAlert(AlertType.ERROR, "Error", "No item was selected", "Please select atleast one item.");

       });

       cartImg.setOnMouseClicked(event -> {

            viewCurentOrders(orders);
       });

    }

    @SuppressWarnings("unchecked")
    public static void select_supplier(Parent pane, List<Map<String,Object>>  orders) {

        // ComboBox
        ComboBox supplierTypeComboBox = FXManager.getComboBox(pane, "supplierTypeComboBox");

        // Text Field
        TextField enterNameTextField = FXManager.getTextField(pane, "enterNameTextField");
        FXManager.setTextFieldAlphabeticFormatRule(enterNameTextField);
        TextField selectedSupplier = FXManager.getTextField(pane, "selectedSupplierTextField");

        // Button
        Button search = FXManager.getButton(pane, "searchBtn");
        Button next = FXManager.getButton(pane, "nextBtn");
        AnchorPane refresh = FXManager.getAnchorPane(pane, "refreshBtn");

        // Table
        TableView suppliersTable = FXManager.getTableView(pane, "suppliersTable");

        // We fill the comboBox with supplier types
        PurchaseFX.fillTypeComboBox(supplierTypeComboBox);

        // When the combobox is clicked
        supplierTypeComboBox.valueProperty().addListener((obs, oldType, newType) -> {

            if (newType != null) { // When a value is chosen

                String type = String.valueOf(newType); // we get the value

                PurchaseFX.fillWithSuppliersByType(suppliersTable, type); // we fill the table with the corresponding suppliers

                FXManager.showWrappableAlert(AlertType.INFORMATION, "Information", "Tip", "You can either select an already existing supplier from the table, or enter a contact's name in the text field and they will be selected as a supplier for the current purchase, make sure to select a supplier type first though.");

            }
        });

        // A method that handles all event listeners in the pane :
        PurchaseFX.supplierSelectionPaneButtonsHandler(suppliersTable, search, refresh, next, 
        
        supplierTypeComboBox, enterNameTextField, selectedSupplier, orders);

    }

    public static void confirm_purchase(Parent pane, String supplierType, String supplierName, List<Map<String,Object>> orders) {

        // we get the supplier hbox to load the appropriate fxml
        HBox supplierHbox = FXManager.getHBox(pane, "supplierHBox");

        // the fxml file's path
        String path = "/uiass/eia/gisiba/purchase/purchase/" + supplierType.toLowerCase() + "_supplier_HBox.fxml";

        // we load the fxml file
        FXManager.loadFXML(path, supplierHbox, PurchaseCrud.class);

        // we get the supplier vbox 
        VBox ordersVbox = FXManager.getVBox(pane, "ordersVBox");

        // we get the left vbox
        VBox leftVbox = FXManager.getVBox(pane, "leftVBox");

        // Confirm Purchase Button
        Button confirm = FXManager.getButton(pane, "confirmPurchaseBtn");

        // The map we'll use to generate the json
        Map<String,Object> purchaseMap = new HashMap<String,Object>();

        // here we get the purchase's total amount
        String total = PurchaseFX.getPurchaseTotalAmmount(orders);

        // a method that takes care of populating the orders table using the orders list
        PurchaseFX.ordersTableFiller(ordersVbox, orders);

        // a method that takes care of filling the labels in the pane 
        PurchaseFX.purchaseConfirmingLabelsHandler(supplierType, supplierName, total, leftVbox);

        confirm.setOnAction(event -> {

            String purchaseDate = String.valueOf(Date.valueOf(LocalDate.now()));

            String ordersJsonArray = Parser.jsonGenerator(orders);

            // we get the supplier object (list) from the backend and take just the id
            int suplierId = Integer.parseInt(ContactDto.getContactByName(supplierName, supplierType).get(0));

            purchaseMap.put("supplierId", suplierId);

            purchaseMap.put("orders", ordersJsonArray);

            purchaseMap.put("purchaseDate", purchaseDate);

            purchaseMap.put("total", total);   

            // a method that handles asks the user for the purchase status and performs the creation accordingly  
            PurchaseFX.purchaseConfirmingDialogBoxHandler(purchaseMap, supplierType);

            ((Stage) pane.getScene().getWindow()).close(); // We close the products selection page after pressing the confirm button
         
        });

        

    }

    @SuppressWarnings("unchecked")
    public static void purchasesTableHandler(TableView purchaseTable, Parent pane, Button create) {

            HBox supplierHbox = FXManager.getHBox(pane, "supplierHbox");

            HBox ordersHbox = FXManager.getHBox(pane, "ordersHbox");

        purchaseTable.setOnMouseClicked(event -> {

            if (!purchaseTable.getSelectionModel().isEmpty()) {

                // We get the selected row and extract the values
                List<String> selectedItem = (List<String>) purchaseTable.getSelectionModel().getSelectedItem();

                int purchaseId = Integer.parseInt(selectedItem.get(0));

                int supplierId = Integer.parseInt(selectedItem.get(1));
                
                String supplierType = selectedItem.get(3);

                PurchaseFX.showPurchaseSupplier(supplierId, supplierType, supplierHbox);

                PurchaseFX.showPurchaseOrders(purchaseId, ordersHbox);

            }
        });

        
    }
    public static List<List<String>> purchaseSearchFilter(String supplierName, String status, List<String> dates_values) {

        Map<String,Object> filter_map = new HashMap<String,Object>();

        if (dates_values != null) {

            String date_filter_type = PurchaseFX.datesFilterTypeGetter(dates_values);

            Map<String,Object> dates_filter_map = PurchaseParser.purchasesDatesFilterMapGenerator(date_filter_type, dates_values);
    
            filter_map.put("date", dates_filter_map);

        }

        if (supplierName != null) {

            String supplierType = ContactDto.getContactType(supplierName);

            Map<String,Object> supplierMap = PurchaseParser.purchasesSupplierFilterMapGenerator(supplierName, supplierType);

            filter_map.put("supplier", supplierMap);
        }

        if (status != null) filter_map.put("status", status);

        List<List<String>> purchases = PurchaseDto.purchasesFilter(Parser.jsonGenerator(filter_map));

        return purchases;
        
    }

    public static void editPurchaseOrdersPane(Parent pane, String purchaseId) {

        // Table View
        TableView ordersTable = FXManager.getTableView(pane, "ordersTableView");

        // Buttons
        ImageView confirm = FXManager.getImageView(pane, "confirmBtn");

        PurchaseFX.editOrdersTableFiller(ordersTable, purchaseId);

        PurchaseFX.purchaseOrdersTableHandler(ordersTable, pane, purchaseId);

        confirm.setOnMouseClicked(event -> {

            ((Stage) pane.getScene().getWindow()).close(); // We close the orders update page after pressing the confirm button

        });      
    
    }

    public static void goToCreatePurchasePane() {

        goToItemsSelectionPane();

    }

    public static void goToItemsSelectionPane() {

        // We create the stage that will contain the creation page
        Stage stage = new Stage();
        AnchorPane pane = new AnchorPane();
        Scene scene = new Scene(pane);

        // here we load the creation page fxml file

        String path = "/uiass/eia/gisiba/purchase/purchase/create_purchase_products_selection_pane.fxml";
        FXManager.loadFXML(path, pane, PurchaseCrud.class); 

        // We call the method that handles the creation
        create_orders(pane);
        
        // We add the stage info and show it
        String iconPath = "/uiass/eia/gisiba/imgs/carts.png";
        InputStream inputStream = PurchaseCrud.class.getResourceAsStream(iconPath);
        Image icon = new Image(inputStream);

        stage.setScene(scene);
        stage.setTitle("Select Items To Order");
        stage.setResizable(false);
        stage.getIcons().add(icon);
        stage.show();

    }

    public static void viewCurentOrders(List<Map<String,Object>> orders) {

        // We create the stage that will contain the view page
        Stage stage = new Stage();
        AnchorPane pane = new AnchorPane();
        Scene scene = new Scene(pane);

        // here we load the creation page fxml file
        String path = "/uiass/eia/gisiba/purchase/purchase/create_purchase_products_selection_view_orders_pane.fxml";
        FXManager.loadFXML(path, pane, PurchaseCrud.class); 

        VBox vbox = FXManager.getVBox(pane, "mainVbox");

        // We call the method that handles the creation
        PurchaseFX.ordersTableFiller(vbox, orders);
        
        // We add the stage info and show it
        String iconPath = "/uiass/eia/gisiba/imgs/completed-task.png";
        InputStream inputStream = PurchaseCrud.class.getResourceAsStream(iconPath);
        Image icon = new Image(inputStream);

        stage.setScene(scene);
        stage.setTitle("Current orders");
        stage.setResizable(false);
        stage.getIcons().add(icon);
        stage.show();
    }

    public static void goToSupplierSelectionPane(List<Map<String,Object>>  orders) {

        // We create the stage that will contain the creation page
        Stage stage = new Stage();
        AnchorPane pane = new AnchorPane();
        Scene scene = new Scene(pane);

        // here we load the creation page fxml file

        String path = "/uiass/eia/gisiba/purchase/purchase/create_purchase_supplier_selection_pane.fxml";
        FXManager.loadFXML(path, pane, PurchaseCrud.class); 

        // We call the method that handles the creation
        select_supplier(pane, orders);
        
        // We add the stage info and show it
        String iconPath = "/uiass/eia/gisiba/imgs/supplier.png";
        InputStream inputStream = PurchaseCrud.class.getResourceAsStream(iconPath);
        Image icon = new Image(inputStream);

        stage.setScene(scene);
        stage.setTitle("Select Supplier");
        stage.setResizable(false);
        stage.getIcons().add(icon);
        stage.show();
    }

    public static void goToPurchaseSummaryPane(String supplierType, String supplierName, List<Map<String,Object>>  orders) {

        // We create the stage that will contain the creation page
        Stage stage = new Stage();
        AnchorPane pane = new AnchorPane();
        Scene scene = new Scene(pane);

        // here we load the creation page fxml file
        String path = "/uiass/eia/gisiba/purchase/purchase/purchase_summary_pane.fxml";
        FXManager.loadFXML(path, pane, PurchaseCrud.class); 

        // We call the method that handles the creation
        confirm_purchase(pane, supplierType, supplierName, orders);
        
        // We add the stage info and show it
        String iconPath = "/uiass/eia/gisiba/imgs/check.png";
        InputStream inputStream = PurchaseCrud.class.getResourceAsStream(iconPath);
        Image icon = new Image(inputStream);

        stage.setScene(scene);
        stage.setTitle("Confirm Purchase");
        stage.setResizable(false);
        stage.getIcons().add(icon);
        stage.show();
    }

}
