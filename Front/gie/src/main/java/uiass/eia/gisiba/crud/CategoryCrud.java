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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import uiass.eia.gisiba.FX.ProductFX;
import uiass.eia.gisiba.controller.FXManager;
import uiass.eia.gisiba.http.dto.CategoryDto;
import uiass.eia.gisiba.http.parsers.Parser;
import uiass.eia.gisiba.http.parsers.ProductParser;

public class CategoryCrud {

    public static void create_category(Parent pane, Button button) {
        
        // ComboBoxes
        ComboBox categoryComboBox = FXManager.getComboBox(pane, "categoryComboBox");
        ComboBox brandComboBox = FXManager.getComboBox(pane, "brandComboBox");
        ComboBox modelComboBox = FXManager.getComboBox(pane, "modelComboBox");

        // TextField
        TextField brandTextField = FXManager.getTextField(pane, "brandTextField");
        TextField modelTextField = FXManager.getTextField(pane, "modelTextField");

        // We populate the brand and model combo boxes
        createCategoryPopulator(Arrays.asList(categoryComboBox,brandComboBox,modelComboBox));

        // We set the corresponding event listener to disable the non used nodes
        nodesHandler(brandComboBox, modelComboBox, brandTextField, modelTextField);

        // When comfirm is clicked
        button.setOnAction(event -> {

            String category = (String) categoryComboBox.getValue();

            if (category != null) {  // if a category is selected from the combo box

                if (categoryCreationValidator(brandComboBox, modelComboBox, brandTextField, modelTextField)) {
    
                    String brand = inputHandler(brandComboBox, brandTextField); // We get the entered brand
    
                    String model = inputHandler(modelComboBox, modelTextField); // We get the entered model
        
                    // we create a map that will be converted into json
                    Map<String, Object> map = ProductParser.categoryCreationMapGenerator(Arrays.asList(category,brand,model));
        
                    // we generate the json from the map
                    String json = Parser.jsonGenerator(map);
        
                    // we call send the json to the back end to create the new category
                    String categoryCreationResult = CategoryDto.postCategory(json);
        
                    // We display the creation result :
                    if (categoryCreationResult.equals("Brand - Model created successfully"))
        
                        FXManager.showAlert(AlertType.CONFIRMATION, "Confirmation", "Creation Status  :", categoryCreationResult);
            
                    else FXManager.showAlert(AlertType.ERROR, "ERROR", "Creation Status  :", "Internal Server Error");
        
                    whereToGoNext();
            
                    ((Stage) button.getScene().getWindow()).close(); // We close the create page after confirming the creation
                }
    
                else FXManager.showAlert(AlertType.ERROR, "ERROR", "Missing Values", "Please provide both a brand and a model.");
            }

            else FXManager.showAlert(AlertType.ERROR, "ERROR", "Missing Category", "Please provide a category.");
        });
    }

    public static void goToCreateCategoryPage() {

        // We create the stage that will contain the creation page
        Stage stage = new Stage();
        AnchorPane pane = new AnchorPane();
        Scene scene = new Scene(pane);

        // here we load the creation page fxml file
        FXManager.loadFXML("/uiass/eia/gisiba/inventory/catalog/create_category_pane.fxml", pane, CategoryCrud.class); 
        
        // We collect the confirm button from the fxml file
        Button confirm = FXManager.getButton(pane, "confirmBtn");

        // We add the corresponding event listener to the button
        create_category(pane, confirm);;
        
        // We add the stage info and show it
        String iconPath = "/uiass/eia/gisiba/imgs/brand-image.png";
        InputStream inputStream = CategoryCrud.class.getResourceAsStream(iconPath);
        Image icon = new Image(inputStream);

        stage.setScene(scene);
        stage.setTitle("Create Category / Brand");
        stage.setResizable(false);
        stage.getIcons().add(icon);
        stage.show();
    }

    // collects the right input and disables the non used nodes
    public static void nodesHandler(ComboBox categoryComboBox, ComboBox brandComboBox, 
    
        TextField categoryTextField, TextField brandTextField) {

        categoryComboBox.valueProperty().addListener(event -> {
            
            categoryTextField.setDisable(true);

        });

        categoryTextField.setOnKeyPressed(event -> {           

            if (event.getCode() == KeyCode.ENTER && !categoryTextField.getText().equals("")) {

                categoryComboBox.setDisable(true);
        }}); 

        brandComboBox.valueProperty().addListener(event -> {
            
            brandTextField.setDisable(true);

        });

        brandTextField.setOnKeyPressed(event -> {           

            if (event.getCode() == KeyCode.ENTER && !brandTextField.getText().equals("")) {

                brandComboBox.setDisable(true);
        }}); 

    }

    // collects the right input 
    public static String inputHandler(ComboBox comboBox, TextField textField) {

        String categroy;
    
        if (textField.isDisabled()) categroy = (String) comboBox.getValue();
        
        else categroy = textField.getText();
            
        return categroy;
    }

    public static void whereToGoNext() {

        // We ask the user for the confirmation before the delete :
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Question");
        alert.setHeaderText("Answer PLeae : ");
        alert.setContentText("Do you want to add a new product for this category ? ");
                    
        // Add "Yes" and "No" buttons
        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
                    
        // Show the dialog and wait for user input
        ButtonType result = alert.showAndWait().orElse(null);
        
        if (result == buttonTypeYes) {
            ProductCrud.goToCreateProductPage();
        }   
         
    }

    public static void searchCategoryPopulator(List<ComboBox> comboBoxes) {

        ComboBox categoryComboBox = comboBoxes.get(0);

        ComboBox brandComboBox = comboBoxes.get(1);

        ComboBox modelComboBox = comboBoxes.get(2);

        List<String> categoriesList = CategoryDto.getAllCategoryColumnNames("category"); // We get all the categories that we have 

        FXManager.populateComboBox(categoryComboBox, categoriesList); // We add the categories as the category combo box items

        // When a category is selected :
        categoryComboBox.valueProperty().addListener(event -> {

            brandComboBox.setPromptText("brand");  // set the brand combo box's prompt text to "brand"

            modelComboBox.setPromptText("model");  // set the brand combo box's prompt text to "model"

            String category = (String) categoryComboBox.getValue();  // we get the selected category

            // We get the corresponding data and populate the combo boxes
            String json = Parser.jsonGenerator(ProductParser.categoryFilter(Arrays.asList(category,null,null)));

            List<String> brandsByCategory = CategoryDto.categoryFilter("brandName", json);

            FXManager.populateComboBox(brandComboBox, brandsByCategory);

            brandComboBox.valueProperty().addListener(brand_event -> {

                modelComboBox.setPromptText("model");  // set the brand combo box's prompt text to "model"
    
                String brand = (String) brandComboBox.getValue();  // we get the selected brand
    
                // We get the corresponding data and populate the combo boxes
                String modelJson = Parser.jsonGenerator(ProductParser.categoryFilter(Arrays.asList(category,brand,null)));
    
                List<String> modelsByBrand = CategoryDto.categoryFilter("modelName", modelJson);
      
                FXManager.populateComboBox(modelComboBox, modelsByBrand);
    
    
            });

        });


    }

    public static void searchFilterRefresher(List<ComboBox> comboBoxes) {

        ComboBox categoryComboBox = comboBoxes.get(0);

        ComboBox brandComboBox = comboBoxes.get(1);

        ComboBox modelComboBox = comboBoxes.get(2);

        List<String> categoriesList = CategoryDto.getAllCategoryColumnNames("category"); // We get all the categories that we have 

        FXManager.populateComboBox(categoryComboBox, categoriesList); // We add the categories as the category combo box items

        brandComboBox.getItems().clear();

        modelComboBox.getItems().clear();

        brandComboBox.setPromptText("brand");

        modelComboBox.setPromptText("model");

        
    }

    public static void createCategoryPopulator(List<ComboBox> comboBoxes) {

        ComboBox categoryComboBox = comboBoxes.get(0);

        ComboBox brandComboBox = comboBoxes.get(1);

        ComboBox modelComboBox = comboBoxes.get(2);

        List<String> categoriesList = ProductFX.categories; // We get all the categories that we have 

        FXManager.populateComboBox(categoryComboBox, categoriesList); // We add the categories as the category combo box items

        // When a category is selected :
        categoryComboBox.valueProperty().addListener(event -> {

            brandComboBox.setPromptText("brand");  // set the brand combo box's prompt text to "brand"

            modelComboBox.setPromptText("model");  // set the brand combo box's prompt text to "model"

            String category = (String) categoryComboBox.getValue();  // we get the selected category

            // We get the corresponding data and populate the combo boxes
            String json = Parser.jsonGenerator(ProductParser.categoryFilter(Arrays.asList(category,null,null)));

            List<String> brandsByCategory = CategoryDto.categoryFilter("brandName", json);

            FXManager.populateComboBox(brandComboBox, brandsByCategory);

            brandComboBox.valueProperty().addListener(brand_event -> {

                modelComboBox.setPromptText("model");  // set the brand combo box's prompt text to "model"
    
                String brand = (String) brandComboBox.getValue();  // we get the selected brand
    
                // We get the corresponding data and populate the combo boxes
                String modelJson = Parser.jsonGenerator(ProductParser.categoryFilter(Arrays.asList(category,brand,null)));
    
                List<String> modelsByBrand = CategoryDto.categoryFilter("modelName", modelJson);

                List<String> models = CategoryDto.getAllCategoryColumnNames("model");

                models.retainAll(modelsByBrand);
      
                FXManager.populateComboBox(modelComboBox, models);
    
    
            });

        });


    }

    public static boolean categoryCreationValidator(ComboBox categoryComboBox, ComboBox brandComboBox,
    
    TextField categoryTextField, TextField brandTextField  ) {

        return (categoryComboBox.isDisabled() || categoryTextField.isDisabled()) && 
        
        (brandComboBox.isDisabled() || brandTextField.isDisabled());
    }

}
