package uiass.eia.gisiba.controller;

import java.io.IOException;
import java.util.*;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import uiass.eia.gisiba.Main;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class FXManager {

    public static Map<String, List<String>> labels_ids_per_contact_type_map =  new HashMap<String, List<String>>() {{
        put("Person", Arrays.asList("fullNameLabel","phoneNumberLabel","emailLabel","addressLabel"));
        put("Enterprise", Arrays.asList("enterpriseNameLabel","enterpriseTypeLabel","phoneNumberLabel","emailLabel","addressLabel"));
    }};

    public static List<String> catalog_labels_ids = Arrays.asList("categoryLabel","brandModelNameLabel", "descriptionLabel");

    public static List<String> inventory_labels_ids = Arrays.asList("categoryLabel","brandModelNameLabel","unitPriceLabel","quantityLabel", "dateAddedLabel");

    public static List<String> order_labels_ids = Arrays.asList("categoryLabel","brandModelNameLabel","quantityLabel","unitPriceLabel" ,"timeDateLabel");

    public static Map<String, List<String>> columns_names_per_contact_type = new HashMap<String, List<String>>() {{
        put("Person", Arrays.asList("id","first name","last name","phone number", "email", "address id","house number","neighborhood","city","zip code","country"));
        put("Enterprise", Arrays.asList("id","enterprise name","type","phone number", "email", "address id","house number","neighborhood","city","zip code","country"));
    }};

    public static List<String> catalog_columns = Arrays.asList("ref","category id","category","brand","model","name","description");

    public static List<String> inventory_columns = Arrays.asList("id","category","brand","model","name","unit price","quantity", "date added");

    public static List<String> order_columns = Arrays.asList("order id","item id","category","brand","model","name","unit price","quantity", "order date");

    public static List<String> purchase_columns = Arrays.asList("purchase id","supplier id","supplier","supplierType","purchase date","total","status");



    // A method that loads an fxml file into a pane
    public static void loadFXML(String fxmlFile, AnchorPane pane, Class c) {
        
        try {
            FXMLLoader loader = new FXMLLoader(c.getResource(fxmlFile));
            Parent content = loader.load();
            pane.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFXML(String fxmlFile, HBox pane, Class c) {
        
        try {
            FXMLLoader loader = new FXMLLoader(c.getResource(fxmlFile));
            Parent content = loader.load();
            pane.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFXML(String fxmlFile, VBox pane, Class c) {
        
        try {
            FXMLLoader loader = new FXMLLoader(c.getResource(fxmlFile));
            Parent content = loader.load();
            pane.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public static void switchToScene(String fxml) throws IOException {
        // Call the method to switch to the "Person_Search_Page" scene
        Main.setRoot(fxml);
    }

    @FXML 
    public static AnchorPane switchScene(Node node, Class c, String fxml) {

        Stage currentStage = (Stage) node.getScene().getWindow();

        AnchorPane pane = new AnchorPane();

        FXManager.loadFXML(fxml, pane, c);

        Scene scene = new Scene(pane); 

        currentStage.setScene(scene);

        return pane;
    }

    // Used to collect labels in a pane given a list of ids
    public static List<Label> labelsCollector(Parent pane, List<String> labelsIds) {

        List<Label> labels = new ArrayList<Label>();

        for (String id : labelsIds) {

            labels.add(getLabel(pane, id));
        }

        return labels;
    }

    public static void labelsFiller(List<Label> labels,  List<String> values) {

        // We use the extracted values to fill the labels
        for (int i = 0 ; i < values.size() ; i++) {

                labels.get(i).setText(values.get(i)); 

        }
    }

    // Used to dynamically populate tables given the data and the contact type  
    public static void populateTableView(TableView<List<String>> tableView, List<String> columns, List<String> exclusions, List<List<String>> data) {

        // Clear existing columns and items
        tableView.getColumns().clear();
        tableView.getItems().clear();

        // Create columns
        for (int i = 0; i < columns.size(); i++) {

            final int columnIndex = i;

            TableColumn<List<String>, String> column = new TableColumn<>(columns.get(i));

            column.setCellValueFactory(cellData -> {

                List<String> row = cellData.getValue();

                if (row != null && columnIndex < row.size()) {

                    return new SimpleStringProperty(row.get(columnIndex));

                } else {

                    return new SimpleStringProperty(""); // Return an empty property for empty cells
                }
            });

            if (exclusions != null) if (exclusions.contains(columns.get(i))) column.setVisible(false);

            tableView.getColumns().add(column);
        }

        // Add data
        tableView.getItems().addAll(data);
    }

    @SuppressWarnings("unchecked")
    // Populate a combo box using a list of items
    public static void populateComboBox(ComboBox comboBox, List<String> itemsList) {

        comboBox.getItems().clear();

        ObservableList items = FXCollections.observableArrayList(itemsList);

        comboBox.setItems(items);
    }

    // validate a creation :
    public static boolean textFieldsCreationInputChecker(List<TextField> textFields) {

        for (TextField textField : textFields) {

            if (textField.getText().equals("")) return false;

        }

        return true;
    }

    public static boolean comboBoxesCreationInputChecker(List<ComboBox> comboBoxes) {

        for (ComboBox comboBox : comboBoxes) {

            if (comboBox.getValue() == null) return false;

        }

        return true;
    }

    // validate an update :
    public static boolean textFieldsUpdateInputChecker(List<TextField> textFields) {

        for (TextField textField : textFields) {

            if (!textField.getText().equals("")) return true;

        }

        return false;
    }

    public static boolean comboBoxesUpdateInputChecker(List<ComboBox> comboBoxes) {

        for (ComboBox comboBox : comboBoxes) {

            if (comboBox.getValue() != null) return true;

        }

        return false;
    }
    
    public static void textFieldsEmptier(List<TextField> textFields) {

        for (TextField textField : textFields) {
            
            textField.setText("");
        }
    }

    public static String convertDateFormat(String inputDate) {
        if (!inputDate.equals("null")) {
            // Split the input date string into year, month, and day components
            String[] parts = inputDate.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
    
            // Create a StringBuilder to build the formatted date
            StringBuilder sb = new StringBuilder();
    
            // Append year
            sb.append(year);
            sb.append("-");
    
            // Append month with padding if needed
            if (month < 10) {
                sb.append("0");
            }
            sb.append(month);
            sb.append("-");
    
            // Append day with padding if needed
            if (day < 10) {
                sb.append("0");
            }
            sb.append(day);
    
            return sb.toString();
        }
        return null;
    }
    // numeric only text field rule
    public static void setTextFieldNumericFormatRule(TextField numericTextField) {

        numericTextField.setTextFormatter(new TextFormatter<>(change -> {

        String newText = change.getControlNewText();

        if (newText.matches("\\d*")) { // Allow only digits

            return change;

        } else return null; // Reject the change
            
        }));
    }

    // alphanumeric only text field rule
    public static void setTextFieldAlphanumericFormatRule(TextField alphanumericTextField) {

        alphanumericTextField.setTextFormatter(new TextFormatter<>(change -> {

            String newText = change.getControlNewText();

            // Allow only alphanumeric characters and a max length of 6
            if (newText.matches("[a-zA-Z0-9\\s]*")) { 

                return change;

            } else {

                return null; // Reject the change

            }
        }));
    }

    // alphabetic only text field rule
    public static void setTextFieldPureAlphabeticFormatRule(TextField alphabeticTextField) {
        alphabeticTextField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            // Allow only alphabetic characters, spaces, and "-" characters
            if (newText.matches("[a-zA-Z]*")) {
                return change;
            } else {
                return null; // Reject the change
            }
        }));
    }
    public static void setTextFieldAlphabeticFormatRule(TextField alphabeticTextField) {
        alphabeticTextField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            // Allow only alphabetic characters, spaces, and "-" characters
            if (newText.matches("[a-zA-Z -]*")) {
                return change;
            } else {
                return null; // Reject the change
            }
        }));
    }

    // Rule for only alphanumeric characters, dots, and at symbols
    public static void setTextFieldEmailFormatRule(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            // Allow only alphanumeric characters, dots, and at symbols
            if (newText.matches("[a-zA-Z0-9.@]*")) {
                return change;
            } else {
                return null; // Reject the change
            }
        }));
    }

    public static void setTextFieldFloatFormatRule(TextField floatTextField) {
        floatTextField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*\\.?\\d*")) { // Allow digits, optional decimal point
                return change;
            } else {
                return null; // Reject the change
            }
        }));
    }


    // A method that generates alerts : 
    public static void showAlert(AlertType type, String title, String header, String message) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header); 
        alert.setContentText(message);
        alert.getDialogPane().setContentText(alert.getContentText());
        alert.showAndWait();
    }

    public static void showWrappableAlert(AlertType type, String title, String header, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);

        // Create a Text node for the content
        Text text = new Text(message);

        // Set the wrapping width for text
        text.setWrappingWidth(400);

        // Create a GridPane to hold the content
        GridPane contentGrid = new GridPane();
        contentGrid.setMaxWidth(Double.MAX_VALUE);
        contentGrid.add(text, 0, 0);

        // Set the GridPane as the content of the dialog
        alert.getDialogPane().setContent(contentGrid);

        alert.showAndWait();
    }

    // A list of getters that return different nodes using their id and the parent containing them
    public static TextField getTextField(Parent pane, String id) {
        return (TextField) pane.lookup("#"+id);
    }

    public static Button getButton(Parent pane, String id) {
        return (Button) pane.lookup("#"+id);
    }

    public static Label getLabel(Parent pane, String id) {
        return (Label) pane.lookup("#"+id);
    }

    public static TableView<List<String>> getTableView(Parent pane, String id) {
        return (TableView<List<String>>) pane.lookup("#"+id);
    }
    public static ListView getListView(Parent pane, String id) {
        return (ListView) pane.lookup("#" + id);
    }

    public static RadioButton getRadioButton(Parent pane, String id) {
        return (RadioButton) pane.lookup("#" + id);
    } 

    public static TextArea getTextArea(Parent pane, String id) {

        return (TextArea) pane.lookup("#" + id);
    } 

    public static ComboBox getComboBox(Parent pane, String id) {

        return (ComboBox) pane.lookup("#" + id);
    } 

    public static HBox getHBox(Parent pane, String id) {

        return (HBox) pane.lookup("#" + id);
    } 

    public static VBox getVBox(Parent pane, String id) {

        return (VBox) pane.lookup("#" + id);
    } 

    public static ImageView getImageView(Parent pane, String id) {

        return (ImageView) pane.lookup("#" + id);
    } 

    public static AnchorPane getAnchorPane(Parent pane, String id) {

        return (AnchorPane) pane.lookup("#" + id);
    }
    
    public static DatePicker getDatePicker(Parent pane, String id) {

        return (DatePicker) pane.lookup("#" + id);
    } 

}
