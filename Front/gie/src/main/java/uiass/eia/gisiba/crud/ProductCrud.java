package uiass.eia.gisiba.crud;

import java.io.InputStream;
import java.util.*;


import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import uiass.eia.gisiba.FX.ProductFX;
import uiass.eia.gisiba.controller.FXManager;
import uiass.eia.gisiba.http.dto.CategoryDto;
import uiass.eia.gisiba.http.dto.ProductDto;
import uiass.eia.gisiba.http.parsers.Parser;
import uiass.eia.gisiba.http.parsers.ProductParser;

public class ProductCrud {

    @SuppressWarnings("unchecked")
    // A method that extracts the data entered by the user and sends a post http request to the server :
    public static void create_product(Parent pane, Button button) {

        // The list of values we'll be using to generate the columns - values map
        List productValues = new ArrayList<>();

        List categoryValues = new ArrayList<>();

        // We put all the text fields in a list to check if all the fields got input :
        List<TextField> textFields = productTextFieldsHandler(pane, "create", productValues);

        // We put all the combo boxes in a list to check if an item was selected :
        List<ComboBox> comboBoxes = productComboBoxesHandler(pane, "create", productValues);

        List<String> categoriesList = CategoryDto.getAllCategoryColumnNames("category"); // We get the categories list that we have 

        ComboBox categoryComboBox = comboBoxes.get(0);
        ComboBox brandComboBox = comboBoxes.get(1);
        ComboBox modelComboBox = comboBoxes.get(2);

        FXManager.populateComboBox(categoryComboBox, categoriesList); // We add the categories as the category combo box items


        categoryComboBox.valueProperty().addListener((obs, oldCategory, newCategory) -> {
    
            if (newCategory != null) {
                    
                // We get all the corresponding brands for the newly selected category
                String json = Parser.jsonGenerator(ProductParser.categoryFilter(Arrays.asList((String) newCategory,null,null)));

                List<String> brandsByCategory = CategoryDto.categoryFilter("brandName", json);

                FXManager.populateComboBox(brandComboBox, brandsByCategory);

            }
        });

        brandComboBox.valueProperty().addListener((obs, oldbrand, newbrand) -> {
    
            if (newbrand != null) {

                String category = (String) categoryComboBox.getValue();
                    
                // We get all the corresponding brands for the newly selected brand
                String json = Parser.jsonGenerator(ProductParser.categoryFilter(Arrays.asList(category,(String) newbrand,null)));

                List<String> modelsByBrand = CategoryDto.categoryFilter("modelName", json);

                FXManager.populateComboBox(modelComboBox, modelsByBrand);
        
            }
        });

        

        // We extract the data from the text fields when the button is clicked
        button.setOnAction(event -> {
                                                    // Name Text Field
            if (productCreationValidator(comboBoxes, textFields.get(0))) {

                String category = String.valueOf(categoryComboBox.getValue());
                
                String brand = String.valueOf(brandComboBox.getValue());

                String model = String.valueOf(modelComboBox.getValue());

                categoryValues.add(category); categoryValues.add(brand); categoryValues.add(model);

                textFields.forEach(textField -> {

                    productValues.add(String.valueOf(textField.getText()));
                });

                // We generate the columns - values map using the product and category values list :
                Map<String,Object> map = ProductParser.productCreationMapGenerator(productValues);
                Map<String,Object> categoryMap = ProductParser.categoryUpdateMapGenerator(categoryValues);
      
                map.put("category", categoryMap); // We add the category map to the product map to create the nested json object 
    
                // We dynamically generate the corresponding json :
                String json = Parser.jsonGenerator(map);
    
                // We use the json to send an http post request to the server to create the new product with the entered values :
                String productCreationResult = ProductDto.postProduct(json);
    
                // We display the creation result :
                if (productCreationResult.equals("Product created successfully."))

                    FXManager.showAlert(AlertType.CONFIRMATION, "Confirmation", "Creation Status  :", productCreationResult);
    
                else FXManager.showAlert(AlertType.ERROR, "ERROR", "Creation Status  :", "Internal Server Error");
    
                ((Stage) button.getScene().getWindow()).close(); // We close the create page after confirming the creation

                FXManager.showWrappableAlert(AlertType.INFORMATION, "Information", "Item Creation  :", "A new inventory item was initialized in the inventory with a quantity of 0 and a price of 100, you may change the price manually in the inventory page.");
            
            }

            // When the user clicks on CONFIRM before provididing all the necessary data 
            else FXManager.showAlert(AlertType.ERROR, "ERROR", "Missing Data", "Please enter all the required information.");
        });
    }

        
    @SuppressWarnings("unchecked")

    // A method that extracts the data entered by the user and sends a put http request to the server :
    public static void update_product(Parent pane, Button button, String ref, int categoryId, List<String> originalValues) {

        // The list of values we'll be using to generate the coluns - values map
        List productValues = new ArrayList<>();

        List categoryValues = new ArrayList<>();

        // We put all the text fields in a list :
        List<TextField> textFields = productTextFieldsHandler(pane, "update", originalValues);

        // We put all the combo boxes in a list :
        List<ComboBox> comboBoxes = productComboBoxesHandler(pane, "update", originalValues);

        List<String> categoriesList = CategoryDto.getAllCategoryColumnNames("category"); // We get the categories list that we have 

        ComboBox categoryComboBox = comboBoxes.get(0);
        ComboBox brandComboBox = comboBoxes.get(1);
        ComboBox modelComboBox = comboBoxes.get(2);

        FXManager.populateComboBox(categoryComboBox, categoriesList); // We add the categories as the category combo box items

        // We fill the brands combo box once a category is selected
        categoryComboBox.valueProperty().addListener((obs, oldCategory, newCategory) -> {
    
            if (newCategory != null) {

                // We remove the original brand value once the user selects a category
                brandComboBox.setPromptText("brand");

                modelComboBox.setPromptText("model");

                // We get all the corresponding brands for the newly selected category
                String json = Parser.jsonGenerator(ProductParser.categoryFilter(Arrays.asList((String) newCategory,null,null)));

                List<String> brandsByCategory = CategoryDto.categoryFilter("brandName", json);

                FXManager.populateComboBox(brandComboBox, brandsByCategory);

                // We fill the models combo box once a category and a brand are selected
                brandComboBox.valueProperty().addListener((brandObs, oldbrand, newbrand) -> {
    
                    if (newbrand != null) {
                    
                        // We get all the corresponding models for the newly selected brand and category
                        String modelJson = Parser.jsonGenerator(ProductParser.categoryFilter(Arrays.asList((String) newCategory,(String) newbrand,null)));

                        List<String> modelsByBrand = CategoryDto.categoryFilter("modelName", modelJson);

                        FXManager.populateComboBox(modelComboBox, modelsByBrand); 

            }
        });

            }
        });

        // We extract the data from the text fields when the button is clicked
        button.setOnAction(event -> {

            if (productUpdateValidator(comboBoxes, textFields)) {

                String category = (String) categoryComboBox.getValue();
                
                String brand = (String) brandComboBox.getValue();

                String model = (String) modelComboBox.getValue();

                if (category == null) { // if the user doesn't select a new category

                    category = originalValues.get(0);

                    if (brand == null) brand = originalValues.get(1);

                    if (model == null) model = originalValues.get(2);
                }

                else {  // if the user selects a new category

                    if (brand == null) 

                        FXManager.showAlert(AlertType.ERROR, "Error", "Category Changed", "the product's category was changed, so a new brand must be selected as well.");

                    else if (model == null) {

                        FXManager.showAlert(AlertType.ERROR, "Error", "Category - Brand Changed", "the product's category and brand were changed, so a new model must be selected as well.");

                    }
                }
  
                categoryValues.add(category); categoryValues.add(brand); categoryValues.add(model);

                if (ProductFX.productCategoryUpdateValidator(categoryValues)) {

                    textFields.forEach(textField -> {

                        productValues.add(String.valueOf(textField.getText()));
                    });
    
                    // We generate the columns - values map using the values list :
                    Map<String,Object> productMap = ProductParser.productUpdateMapGenerator(productValues);
                    Map<String,Object> categoryMap = ProductParser.categoryUpdateMapGenerator(categoryValues);
        
                    // We dynamically generate the corresponding json :
                    String productJson = ProductParser.updateProductJsonGenerator(productMap, categoryMap);
                                    
                    // We use the json to send an http post request to the server to create the new product with the entered values :
                    String productUpdateResult = ProductDto.updateProduct(ref,productJson);
                        
                    // We display the update result :
                    if (productUpdateResult.equals("Product Updated successfully."))
                    
                    FXManager.showAlert(AlertType.CONFIRMATION, "Confirmation", "Update Status  :", productUpdateResult);
                        
                    else FXManager.showAlert(AlertType.ERROR, "ERROR", "Update Status  :", productUpdateResult);
                        
                    ((Stage) button.getScene().getWindow()).close(); // We close the create page after confirming the creation
                }

                else categoryValues.clear();
            
            }

            // When the user clicks on CONFIRM before provididing all the necessary data 
            else FXManager.showAlert(AlertType.ERROR, "ERROR", "Missing Data", "Please provide some new values for the update");
        });
    }

    // deleting a contact :
    public static void deleteProduct(String ref) {

        boolean associatedPurchasesChecker = ProductDto.checkForAssociatedPurchases(ref);

        if (!associatedPurchasesChecker) {

            String contactDeletionResult = ProductDto.deleteProduct(ref);

            // We display the deletion result :
            if (contactDeletionResult.equals("Product deleted successfully."))
            
            FXManager.showAlert(AlertType.CONFIRMATION, "Confirmation", "Deletion Status :", contactDeletionResult);
    
            else FXManager.showAlert(AlertType.ERROR, "ERROR", "Deletion Status :", contactDeletionResult);
        }

        else FXManager.showAlert(AlertType.ERROR, "ERROR", "Deletion Denied :", "There are purchases that depend on this product, deletion denied.");

    }

    // a method that sets the product table's columns event listeners
    public static void productTableEventHandler(TableView productsTable, List<Label> labels, Parent pane, AnchorPane refresh,
    
        Button update, Button delete) {

        productsTable.setOnMouseClicked(event -> {
            if (!productsTable.getSelectionModel().isEmpty()) {

                // We get the selected row and extract the values
                List<String> selectedItem = (List<String>) productsTable.getSelectionModel().getSelectedItem();
                String ref = selectedItem.get(0);
                int categoryId = Integer.parseInt(selectedItem.get(1));
                String category = selectedItem.get(2);
                String brand = selectedItem.get(3);
                String model = selectedItem.get(4);
                String name = selectedItem.get(5);
                String description = selectedItem.get(6);

                // We put all the values in one list that we'll use to fill the labels
                List<String> values = Arrays.asList(category,brand,model,name,description);
                List<String> valuesToShow = Arrays.asList(category,brand + " " + model + " " + name,description);

                // We use the extracted values to fill the labels
                FXManager.labelsFiller(labels, valuesToShow);

                // We finally show the right pane
                pane.setVisible(true);

                // When the update button is clicked
                update.setOnAction(update_event -> {

                    List<String> originalValues = new ArrayList<String>(values);

                    goToUpdateProductPage(ref,categoryId, originalValues);
                });

                // When the delete button is clicked
                delete.setOnAction(delete_event -> {

                    // We ask the user for the confirmation before the delete :
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText("Product Deletion");
                    alert.setContentText("The product " + brand + " " + model + " will be deleted, do you confirm this operation ?");
                    
                    // Add "Yes" and "No" buttons
                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");
                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
                    
                    // Show the dialog and wait for user input
                    ButtonType result = alert.showAndWait().orElse(null);
                    if (result == buttonTypeYes) {
                        // Call the deleteContact method if the user clicked "Yes"
                        ProductCrud.deleteProduct(ref);
                    }
                });

           
        } 
            
        });
    }

    public static void fillWithProducts(TableView productsTable) {

        // We send an http get request to get all the contacts of the given type
        List<List<String>> data = ProductDto.getAllProducts();  

        // We populate the table using those collected contacts
        List<String> columns = FXManager.catalog_columns;
        
        FXManager.populateTableView(productsTable, columns, Arrays.asList("ref", "category id"), data);
    }

    public static void fillWithFilteredProducts(TableView productsTable, List<List<String>> data) {
        
        // The columns we'll use for the table
        List<String> columns = FXManager.catalog_columns;
        
        FXManager.populateTableView(productsTable, columns, Arrays.asList("ref", "category id"), data);
    }

    public static void whatToCreate() {

        Stage stage = new Stage();
        AnchorPane pane = new AnchorPane();
        Scene scene = new Scene(pane);
        
        FXManager.loadFXML("/uiass/eia/gisiba/inventory/catalog/what_to_create_pane.fxml", pane, ProductCrud.class);

        AnchorPane categoryPane = FXManager.getAnchorPane(pane, "categoryPane");
        AnchorPane productPane = FXManager.getAnchorPane(pane, "productPane");

        categoryPane.setOnMouseClicked(event -> {

            CategoryCrud.goToCreateCategoryPage();

            ((Stage) categoryPane.getScene().getWindow()).close();

        } );

        productPane.setOnMouseClicked(event -> {

            goToCreateProductPage();

            ((Stage) productPane.getScene().getWindow()).close();

        });

        stage.setScene(scene);
        stage.setTitle("Creation choice");
        stage.show();
        
    }

    // A method that display the product creation pane
    public static void goToCreateProductPage() {

        // We create the stage that will contain the creation page
        Stage stage = new Stage();
        AnchorPane pane = new AnchorPane();
        Scene scene = new Scene(pane);

        // here we load the creation page fxml file
        FXManager.loadFXML("/uiass/eia/gisiba/inventory/catalog/create_update_catalog_pane.fxml", pane, ProductCrud.class); 
        
        // We collect the confirm button from the fxml file
        Button confirm = FXManager.getButton(pane, "confirmBtn");

        // We add the corresponding event listener to the button
        create_product(pane, confirm);;
        
        // We add the stage info and show it
        String iconPath = "/uiass/eia/gisiba/imgs/product.png";
        InputStream inputStream = ProductCrud.class.getResourceAsStream(iconPath);
        Image icon = new Image(inputStream);

        stage.setScene(scene);
        stage.setTitle("Create Product");
        stage.setResizable(false);
        stage.getIcons().add(icon);
        stage.show();

    }

    // A method that display the product update pane
    public static void goToUpdateProductPage(String ref, int categoryId, List<String> originalValues) {

        // We create the stage that will contain the update page
        Stage stage = new Stage();
        AnchorPane pane = new AnchorPane();
        Scene scene = new Scene(pane);

        // here we load the update page fxml file
        FXManager.loadFXML("/uiass/eia/gisiba/inventory/catalog/create_update_catalog_pane.fxml", pane, ProductCrud.class); 
        
        // We collect the confirm button from the fxml file
        Button confirm = FXManager.getButton(pane, "confirmBtn");

        // We add the corresponding event listener to the button
        update_product(pane, confirm, ref, categoryId, originalValues);

        // We add the stage info and show it
        String iconPath = "/uiass/eia/gisiba/imgs/product.png";
        InputStream inputStream = ProductCrud.class.getResourceAsStream(iconPath);
        Image icon = new Image(inputStream);

        stage.setScene(scene);
        stage.setTitle("Update Product");
        stage.setResizable(false);
        stage.getIcons().add(icon);
        stage.show();

    }

    @SuppressWarnings("unchecked")
    public static String filteredProductSearchJsonGenerator(List values) {

        return Parser.jsonGenerator(ProductParser.filteredProductSearchMapGenerator(values));
    }

    // A method that handles the text fields
    public static List<TextField> productTextFieldsHandler(Parent pane, String operation, List<String> originalValues) {

        List<TextField> textFields = new ArrayList<TextField>();

        // We collect the text fields from the pane and apply correspondind input rules : 
        TextField nameTextField= FXManager.getTextField(pane, "nameTextField");
        FXManager.setTextFieldAlphanumericFormatRule(nameTextField);
        TextField descriptionTextField = FXManager.getTextField(pane, "descriptionTextField");
        FXManager.setTextFieldAlphanumericFormatRule(descriptionTextField);


        if (operation.equals("update")) {   // if we want to update

            // We set the prompt text to be the original product's values : 
            nameTextField.setPromptText(originalValues.get(3));
            descriptionTextField.setPromptText(originalValues.get(4));
        }

        
        // We put all the corresponding text fields in a list to later check if all the fields got input :
        textFields.addAll(Arrays.asList(nameTextField,descriptionTextField));

        return textFields;
    }

    public static List<ComboBox> productComboBoxesHandler(Parent pane, String operation, List<String> originalValues) {

        List<ComboBox> comboBoxes = new ArrayList<ComboBox>();

        // We collect the text fields from the pane : 
        ComboBox category= FXManager.getComboBox(pane, "categoryComboBox");
        ComboBox brand = FXManager.getComboBox(pane, "brandComboBox");
        ComboBox model = FXManager.getComboBox(pane, "modelComboBox");

        if (operation.equals("update")) {   // if we want to update

            // We set the prompt text to be the original product's values : 
            category.setPromptText(originalValues.get(0));
            brand.setPromptText(originalValues.get(1));
            model.setPromptText(originalValues.get(2));

            /*// We fill the brand combo box with the corresponding brands :
            List<String> brands = CategoryDto.getAllColumnByFilterColumn("brand", "category", originalValues.get(1));
            FXManager.populateComboBox(brand, brands);

            // We fill the model combo box with the corresponding models :
            List<String> models = CategoryDto.getAllColumnByFilterColumn("model", "brand", originalValues.get(2));
            FXManager.populateComboBox(model, models);*/
        }

        
        // We put all the corresponding text fields in a list to later check if all the fields got input :
        comboBoxes.add(category);
        comboBoxes.add(brand);
        comboBoxes.add(model);

        return comboBoxes;
    }

    public static List<ComboBox> productSearchComboBoxesHandler(Parent pane) {

        ComboBox categoryComboBox = FXManager.getComboBox(pane, "categoryComboBox");
        ComboBox brandComboBox = FXManager.getComboBox(pane, "brandComboBox");
        ComboBox modelComboBox = FXManager.getComboBox(pane, "modelComboBox");

        CategoryCrud.searchCategoryPopulator(Arrays.asList(categoryComboBox,brandComboBox,modelComboBox));

        return Arrays.asList(categoryComboBox,brandComboBox,modelComboBox);
    }

    public static boolean productCreationValidator(List<ComboBox> comboBoxes, TextField textField) {

        for (ComboBox comboBox : comboBoxes) {

            if (comboBox.getValue() == null) {

                return false;
            }
        }

        return !textField.getText().equals("");
    }

    public static boolean productUpdateValidator(List<ComboBox> comboBoxes, List<TextField> textFields) {

        for (TextField textField : textFields) {

            if (!textField.getText().equals("")) {

                return true;
            }
        }

        for (ComboBox comboBox : comboBoxes) {

            if (comboBox.getValue() != null) {

                return true;
            }
        }

        return false;
    }

    public static boolean productSearchValidator(List<ComboBox> comboBoxs) {

        for (ComboBox comboBox : comboBoxs) {

            if (comboBox.getValue() != null) {

                return true;
            }
        }

        return false;
    }
    
}
