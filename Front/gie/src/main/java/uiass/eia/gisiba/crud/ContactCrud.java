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
import uiass.eia.gisiba.controller.FXManager;
import uiass.eia.gisiba.http.dto.AddressDto;
import uiass.eia.gisiba.http.dto.ContactDto;
import uiass.eia.gisiba.http.parsers.ContactParser;
import uiass.eia.gisiba.http.parsers.Parser;

public class ContactCrud {


    @SuppressWarnings("unchecked")

    // A method that extracts the data entered by the user and sends a post http request to the server :
    public static void create_contact(Parent pane, Button button, String contactType) {

        // The list of values we'll be using to generate the coluns - values map
        List values = new ArrayList<>();

        // The list of attributes that reference the text fields' ids :
        List<String> attributes = new ArrayList<String>(ContactParser.contactCreationTextFieldsReferences);


        // We collect all the text fields in a list and set the corresponding prompt text :
        List<TextField> textFields = contactCreationTextFieldsHandler(pane, contactType);

        ComboBox typeComboBox = null;

        if (contactType.equals("Enterprise")) {

            attributes.remove(1);

            typeComboBox = FXManager.getComboBox(pane, "enterpriseTypeComboBox");
            
            List<String> types = new ArrayList<>(Arrays.asList("SA","SAS","SARL","SNC"));

            FXManager.populateComboBox(typeComboBox, types);
            
        }

        List<ComboBox> comboBoxes = new ArrayList<ComboBox>();

        if (typeComboBox != null) comboBoxes.add(typeComboBox);

        // We extract the data from the text fields when the button is clicked
        button.setOnAction(event -> {

            if (FXManager.textFieldsCreationInputChecker(textFields) && (FXManager.comboBoxesCreationInputChecker(comboBoxes)
            
            || comboBoxes.isEmpty())) {

                attributes.forEach(attribute -> {

                    String value = FXManager.getTextField(pane, attribute + "TextField").getText(); // We collect the value of the text field entered by the user
    
                    if (attribute.equals("houseNumber")) values.add(value != "" ? Integer.parseInt(value) : 0); // We must save the house number as an integer.
                    
                    else values.add(value); // for every other attribute we just save the value as string
                });

                if (contactType.equals("Enterprise")) values.add(comboBoxes.get(0).getValue());
    
                // We generate the columns - values map using the values list :
                Map<String,Object> map = ContactParser.contactCreationMapGenerator(values, contactType);
    
                // We dynamically generate the corresponding json :
                String json = Parser.jsonGenerator(map);
    
                // We use the json to send an http post request to the server to create the new contact with the entered values :
                String contactCreationResult = ContactDto.postContact(json, contactType);
    
                // We display the creation result :
                if (contactCreationResult.equals("Contact created successfully."))
                FXManager.showAlert(AlertType.CONFIRMATION, "Creation Status", "Result :", contactCreationResult);
    
                else FXManager.showAlert(AlertType.ERROR, "Creation Status", "Result :", contactCreationResult);
    
                ((Stage) button.getScene().getWindow()).close(); // We close the create page after confirming the creation
            
            }

            else FXManager.showAlert(AlertType.ERROR, "Empty Fields", "Missing Data", "Please enter all the required information.");
        });
    }

        
    @SuppressWarnings("unchecked")

    // A method that extracts the data entered by the user and sends a put http request to the server :
    public static void update_contact(Parent pane, Button button, String contactType, int contactId, 
    
    int addressId, List<String> originalValues) {

        // lists containing each object's attributes (excluding ids) :
        List<String> contactAttributes = new ArrayList<String>(ContactParser.contactUpdateTextFieldsReferences);

        List<String> addressAttributes = ContactParser.update_address_attributes;

        // lists to store each attribute's entered value :
        List contactValues = new ArrayList<>();

        List addressValues = new ArrayList<>();


        // We collect all the text fields in a list and set the corresponding prompt text :
        List<TextField> textFields = contactUpdateTextFieldsHandler(pane, contactType, originalValues);

        ComboBox typeComboBox = null;

        if (contactType.equals("Enterprise")) {

            contactAttributes.remove(1);

            typeComboBox = FXManager.getComboBox(pane, "enterpriseTypeComboBox");
            
            List<String> types = new ArrayList<>(Arrays.asList("SA","SAS","SARL","SNC"));

            String originalEnterpriseType = originalValues.get(1);

            FXManager.populateComboBox(typeComboBox, types);

            typeComboBox.setPromptText(originalEnterpriseType);
            
        }

        List<ComboBox> comboBoxes = new ArrayList<ComboBox>();

        if (typeComboBox != null) comboBoxes.add(typeComboBox);

        // We extract the data from the text fields when the button is clicked
        button.setOnAction(event -> {

            if (FXManager.textFieldsUpdateInputChecker(textFields) || FXManager.comboBoxesUpdateInputChecker(comboBoxes)) {

                contactAttributes.forEach(attribute -> {

                    String value = FXManager.getTextField(pane, attribute + "TextField").getText(); // We collect the value of the text field entered by the user
    
                    contactValues.add(value); // We add it to the contact values list
                });
    
                addressAttributes.forEach(attribute -> {
    
                    String value = FXManager.getTextField(pane, attribute + "TextField").getText(); // We collect the value of the text field entered by the user
    
                    // We must save the house number as an integer :
                    if (attribute.equals("houseNumber")) addressValues.add(!value.equals("") ? Integer.parseInt(value) : 0); 
    
                    else addressValues.add(value); // for every other attribute we just save the value as string
                    
    
                });

                if (contactType.equals("Enterprise")) contactValues.add(comboBoxes.get(0).getValue());
    
                // We create the columns - values to update maps :
                Map<String,Object> contact_attributes_map = ContactParser.contactUpdateMapGenerator(contactValues, contactType);
                Map<String,Object> address_attributes_map = ContactParser.addressMapGenerator(addressValues);
    
                // We create their corresponding jsons :
                String contactJson = Parser.jsonGenerator(contact_attributes_map);
                String addressJson = Parser.jsonGenerator(address_attributes_map);
    
                // We send http put requests to update the contact or the address :
                String contactUpdateResult = ContactDto.updateContact(contactId, contactType, contactJson);
                String addressUpdateResult = AddressDto.updateAddress(addressId, addressJson);
    
                // We display the update result :
                if (contactUpdateResult.equals("Contact updated successfully.") || 
                
                addressUpdateResult.equals("Address updated successfully."))

                FXManager.showAlert(AlertType.CONFIRMATION, "Update Status", "Result :", contactUpdateResult);
    
                else if (!contactUpdateResult.equals("Address Updated Successfully.")) {

                FXManager.showAlert(AlertType.ERROR, "Update Status", "Result :", contactUpdateResult);

                }

                ((Stage) button.getScene().getWindow()).close(); // We close the update page after confirming the update
            }

            else FXManager.showAlert(AlertType.ERROR, "Empty Fields", "Missing Data", "At least one value is required for the update"); 
        });
    }

    // deleting a contact :
    public static void deleteContact(String contactType, int id) {

        String contactDeletionResult = ContactDto.deleteContact(id, contactType);

        // We display the deletion result :
        if (contactDeletionResult.equals("Contact deleted successfully."))
        
        FXManager.showAlert(AlertType.CONFIRMATION, "Deletion Status", "Result :", contactDeletionResult);

        else FXManager.showAlert(AlertType.ERROR, "Deletion Status", "Result :", contactDeletionResult);

    }

    // sending an email to a contact :
    public static void sendEmail(Parent pane, Button button, String receiverEmail) {

        button.setOnAction(event -> {

            // We get the email subject and body entered by the user :
            String subject = FXManager.getTextField(pane, "subjectTextField").getText();
            String emailBody = FXManager.getTextArea(pane, "bodyTextArea").getText();

            // if both subject and body were provided :
            if (subject != "" && emailBody != "") {

                // We put the receiver, subject and body in a list 
                List<String> values = Arrays.asList(receiverEmail, subject, emailBody); 

                // We call the Parser class to generate the corresponding map
                Map<String,Object> emailSendingMap = ContactParser.emailSendingMapGenerator(values);

                // We convert the map to json
                String json = Parser.jsonGenerator(emailSendingMap);

                // Finally we send the json in a post request to the corresponding endpoint 
                String emailSendingResult = ContactDto.postEmail(json);
    
                // We display the sending result :
                if (emailSendingResult.equals("Email Sent Successfully."))
            
                FXManager.showAlert(AlertType.CONFIRMATION, "Sending Status", "Result :", emailSendingResult);
        
                else FXManager.showAlert(AlertType.ERROR, "Sending Status", "Result :", emailSendingResult);

                ((Stage) button.getScene().getWindow()).close(); // We close the sending page after sending the email
            }

            // if one field or more are missing
            else FXManager.showAlert(AlertType.ERROR, "Missing Data", "Empty Fields :", "Please fill all the required fields.");
        });
    }

        // A method that display the contact creation pane
    public static void goToCreateContactPage(String contactType) {

        // We create the stage that will contain the creation page
        Stage stage = new Stage();
        AnchorPane pane = new AnchorPane();
        Scene scene = new Scene(pane);
        String type = contactType.toLowerCase();
        FXManager.loadFXML("/uiass/eia/gisiba/crm/contact/" + type + "/" + "create_" + type + "_pane.fxml", pane, ContactCrud.class);  // here we load the creation page fxml file
        
        // We collect the confirm button from the fxml file
        Button confirm = FXManager.getButton(pane, "confirmBtn");

        // We add the corresponding event listener to the button
        ContactCrud.create_contact(pane, confirm, contactType);
        
        // We add the stage info and show it
        String iconPath = "/uiass/eia/gisiba/imgs/" + (contactType.equals("Person") ? "man" : "office-building") + ".png";
        InputStream inputStream = ContactCrud.class.getResourceAsStream(iconPath);
        Image icon = new Image(inputStream);

        stage.setScene(scene);
        stage.setTitle(contactType.equals("Person") ? "Create Person" : "Create Enterprise");
        stage.setResizable(false);
        stage.getIcons().add(icon);
        stage.show();

    }

    // A method that display the contact update pane
    public static void goToUpdateContactPage(String contactType, int contactId, int addressId, List<String> originalValues) {

        // We create the stage that will contain the update page
        Stage stage = new Stage();
        AnchorPane pane = new AnchorPane();
        Scene scene = new Scene(pane);
        String type = contactType.toLowerCase();
        FXManager.loadFXML("/uiass/eia/gisiba/crm/contact/" + type + "/" + "update_" + type + "_pane.fxml", pane, ContactCrud.class); // here we load the update page fxml file
        
        // We collect the confirm button from the fxml file
        Button confirm = FXManager.getButton(pane, "confirmBtn");

        // We add the corresponding event listener to the button
        ContactCrud.update_contact(pane, confirm, contactType, contactId, addressId, originalValues);

        // We add the stage info and show it
        String iconPath = "/uiass/eia/gisiba/imgs/" + (contactType.equals("Person") ? "man" : "office-building") + ".png";
        InputStream inputStream = ContactCrud.class.getResourceAsStream(iconPath);
        Image icon = new Image(inputStream);

        stage.setScene(scene);
        stage.setTitle(contactType.equals("Person") ? "Update Person" : "Update Enterprise");
        stage.setResizable(false);
        stage.getIcons().add(icon);
        stage.show();

    }

    // A method that display the email sending pane
    public static void goToSendEmailPage(String receiverEmail) {

        // We create the stage that will contain the email sending page
        Stage stage = new Stage();
        AnchorPane pane = new AnchorPane();
        Scene scene = new Scene(pane);
        FXManager.loadFXML("/uiass/eia/gisiba/crm/contact/send_email_pane.fxml", pane, ContactCrud.class); // here we load the email sending page fxml file

        // We collect the send button from the fxml file
        Button send = FXManager.getButton(pane, "sendEmailBtn");

        // We add the corresponding event listener to the button
        ContactCrud.sendEmail(pane, send, receiverEmail);

        // We add the stage info and show it
        stage.setScene(scene);
        stage.setTitle("Email Sending");
        stage.setResizable(false);
        stage.show();
    }
    public static List<TextField> contactCreationTextFieldsHandler(Parent pane, String contactType) {

        List<TextField> textFields = new ArrayList<TextField>();

        TextField firstTextField= FXManager.getTextField(pane, "firstTextField");
        FXManager.setTextFieldPureAlphabeticFormatRule(firstTextField);
        TextField phoneNumberTextField = FXManager.getTextField(pane, "phoneNumberTextField");
        FXManager.setTextFieldAlphanumericFormatRule(phoneNumberTextField);
        TextField emailTextField = FXManager.getTextField(pane, "emailTextField");
        FXManager.setTextFieldEmailFormatRule(emailTextField);
        TextField houseNumberTextField = FXManager.getTextField(pane, "houseNumberTextField");
        FXManager.setTextFieldNumericFormatRule(houseNumberTextField);
        TextField neighborhoodTextField = FXManager.getTextField(pane, "neighborhoodTextField");
        FXManager.setTextFieldAlphanumericFormatRule(neighborhoodTextField);
        TextField cityTextField = FXManager.getTextField(pane, "cityTextField");
        FXManager.setTextFieldPureAlphabeticFormatRule(cityTextField);
        TextField zipCodeTextField = FXManager.getTextField(pane, "zipCodeTextField");
        FXManager.setTextFieldNumericFormatRule(zipCodeTextField);
        TextField countryTextField = FXManager.getTextField(pane, "countryTextField");
        FXManager.setTextFieldAlphabeticFormatRule(countryTextField);

        if (contactType.equals("Person")) {
            TextField secondTextField = FXManager.getTextField(pane, "secondTextField");
            FXManager.setTextFieldPureAlphabeticFormatRule(secondTextField);

            textFields.addAll(Arrays.asList(firstTextField,secondTextField,phoneNumberTextField,emailTextField,houseNumberTextField,
            
            neighborhoodTextField,cityTextField,zipCodeTextField,countryTextField));
            
        }

        else textFields.addAll(Arrays.asList(firstTextField,phoneNumberTextField,emailTextField,houseNumberTextField,
            
        neighborhoodTextField,cityTextField,zipCodeTextField,countryTextField));

        return textFields;
    }

    public static List<TextField> contactUpdateTextFieldsHandler(Parent pane, String contactType, List<String> originalValues) {

        List<TextField> contactTextFields = new ArrayList<TextField>();

        List<TextField> textFields = new ArrayList<TextField>();

                // We collect the text fields from the pane : 
                TextField firstTextField= FXManager.getTextField(pane, "firstTextField");
                FXManager.setTextFieldAlphabeticFormatRule(firstTextField);
                /*TextField secondTextField = FXManager.getTextField(pane, "secondTextField");
                FXManager.setTextFieldAlphabeticFormatRule(secondTextField);*/
                TextField phoneNumberTextField = FXManager.getTextField(pane, "phoneNumberTextField");
                FXManager.setTextFieldAlphanumericFormatRule(phoneNumberTextField);
                TextField emailTextField = FXManager.getTextField(pane, "emailTextField");
                FXManager.setTextFieldEmailFormatRule(emailTextField);
                TextField houseNumberTextField = FXManager.getTextField(pane, "houseNumberTextField");
                FXManager.setTextFieldNumericFormatRule(houseNumberTextField);
                TextField neighborhoodTextField = FXManager.getTextField(pane, "neighborhoodTextField");
                FXManager.setTextFieldAlphanumericFormatRule(neighborhoodTextField);
                TextField cityTextField = FXManager.getTextField(pane, "cityTextField");
                FXManager.setTextFieldAlphabeticFormatRule(cityTextField);
                TextField zipCodeTextField = FXManager.getTextField(pane, "zipCodeTextField");
                FXManager.setTextFieldNumericFormatRule(zipCodeTextField);
                TextField countryTextField = FXManager.getTextField(pane, "countryTextField");
                FXManager.setTextFieldAlphabeticFormatRule(countryTextField);

                // Here we select the text fields to consider depending on the contact type :
                if (contactType.equals("Person")) {

                    TextField secondTextField = FXManager.getTextField(pane, "secondTextField");
                    secondTextField.setPromptText(originalValues.get(1)); // We set its original value
                    FXManager.setTextFieldAlphabeticFormatRule(secondTextField); 

                    textFields.addAll(Arrays.asList(firstTextField,secondTextField,phoneNumberTextField,emailTextField,
        
                    houseNumberTextField,neighborhoodTextField,cityTextField,zipCodeTextField,countryTextField));
                }

                else textFields.addAll(Arrays.asList(firstTextField,phoneNumberTextField,emailTextField,
        
                houseNumberTextField,neighborhoodTextField,cityTextField,zipCodeTextField,countryTextField));
        
                // We set the prompt text to be the original contact's values : 
                firstTextField.setPromptText(originalValues.get(0));
                phoneNumberTextField.setPromptText(originalValues.get(2));
                emailTextField.setPromptText(originalValues.get(3));
                //////// We skip the address value as it is just used to fill the right panel ////////
                houseNumberTextField.setPromptText(originalValues.get(5));
                neighborhoodTextField.setPromptText(originalValues.get(6));
                cityTextField.setPromptText(originalValues.get(7));
                zipCodeTextField.setPromptText(originalValues.get(8));
                countryTextField.setPromptText(originalValues.get(9));
        
                // We put all the corresponding text fields in a list to later check if all the fields got input :
                contactTextFields.addAll(textFields);

                return contactTextFields;
    }

    public static void contactsTableEventHandler(TableView table, List<Label> labels, AnchorPane pane, String contactType,
    
    Button update, Button delete) {

        table.setOnMouseClicked(event -> {
            if (!table.getSelectionModel().isEmpty()) {

                // We get the selected row and extract the values
                List<String> selectedItem = (List<String>) table.getSelectionModel().getSelectedItem();
                int contactId = Integer.parseInt(selectedItem.get(0));
                String firstAttribute = selectedItem.get(1);
                String secondAttribute = selectedItem.get(2);
                String phoneNumber = selectedItem.get(3);
                String email = selectedItem.get(4);
                int addressId = Integer.parseInt(selectedItem.get(5));
                String houseNumber = selectedItem.get(6);
                String neighborhood = selectedItem.get(7);
                String city = selectedItem.get(8);
                String zipCode = selectedItem.get(9);
                String country = selectedItem.get(10);

                String address = houseNumber + " " + neighborhood + " " +
                
                city + " " + zipCode + " " + country; // We formulate the full address using its id

                // We put all the values in one list that we'll use to fill the labels
                List<String> values = Arrays.asList(firstAttribute,secondAttribute,phoneNumber,email,address);

                // We use the extracted values to fill the labels
                contactLabelsFiller(labels, values, contactType);

                // We finally show the right pane
                pane.setVisible(true);

                // When the update button is clicked
                update.setOnAction(update_event -> {
                    // We collect ll the original values to be passed as the text fields prompt text
                    List<String> originalValues = new ArrayList<String>(values);
                    originalValues.addAll(Arrays.asList(houseNumber,neighborhood,city,zipCode,country));
                    ContactCrud.goToUpdateContactPage(contactType, contactId, addressId, originalValues);
                });

                // When the delete button is clicked
                delete.setOnAction(delete_event -> {

                    // We define the contact name based on its type
                    String contactName = (contactType.equals("Person")) ? firstAttribute + " " + secondAttribute : firstAttribute;

                    // Show a confirmation dialog
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation");
                    alert.setHeaderText("Contact Deletion");
                    alert.setContentText("The contact " + contactName + " will be deleted, do you confirm this operation ?");
                
                    // Add "Yes" and "No" buttons
                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");
                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
                
                    // Show the dialog and wait for user input
                    ButtonType result = alert.showAndWait().orElse(null);
                    if (result == buttonTypeYes) {
                        // Call the deleteContact method if the user clicked "Yes"
                        ContactCrud.deleteContact(contactType, contactId);
                    }
                });
           
        } 
            
        });
    }

    public static void contactSearchButtonHandler(Button search, TextField textField, List<Label> labels,
    
        String contactType, Parent pane, Button update, Button delete) {

                // When we press the search button
                search.setOnAction(event -> {
            
                    // We collect the entered name 
                    String contactName = textField.getText();
        
                    if (!contactName.equals("")) {
        
                        // We get the contact using that id
                        List<String> info = ContactDto.getContactByName(contactName, contactType);
        
                        if (info != null) {  // if there is a contact with the given name
        
                            // We extract each attribute's value from the retrieved contact
                            int contactId = Integer.parseInt(info.get(0));
                            String firstAttribute = info.get(1);
                            String secondAttribute = info.get(2);
                            String phoneNumber = info.get(3);
                            String email = info.get(4);
                            int addressId = Integer.parseInt(info.get(5));
                            String houseNumber = info.get(6);
                            String neighborhood = info.get(7);
                            String city = info.get(8);
                            String zipCode = info.get(9);
                            String country = info.get(10);    
                            String address = houseNumber + " " + neighborhood + " " +
                        
                            city + " " + zipCode + " " + country; 
        
                            // We put all the values in one list that we'll use to fill the labels
                            List<String> values = Arrays.asList(firstAttribute,secondAttribute,phoneNumber,email,address);
            
                            // We use the extracted values to fill the labels
                            ContactCrud.contactLabelsFiller(labels, values, contactType);
        
                            // We finally show the right pane
                            pane.setVisible(true);
        
                            // When the update button is clicked
                            update.setOnAction(update_event -> {
                                // We collect ll the original values to be passed as the text fields prompt text
                                List<String> originalValues = new ArrayList<String>(values);
                                originalValues.addAll(Arrays.asList(houseNumber,neighborhood,city,zipCode,country));
                                ContactCrud.goToUpdateContactPage(contactType, contactId, addressId, originalValues);
                            });
        
                            // When the delete button is clicked
                            delete.setOnAction(delete_event -> {
                                // We ask the user for the confirmation before the delete :
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("Confirmation");
                                alert.setHeaderText("Contact Deletion");
                                alert.setContentText("The contact " + contactName + " will be deleted, do you confirm this operation ?");
                            
                                // Add "Yes" and "No" buttons
                                ButtonType buttonTypeYes = new ButtonType("Yes");
                                ButtonType buttonTypeNo = new ButtonType("No");
                                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
                            
                                // Show the dialog and wait for user input
                                ButtonType result = alert.showAndWait().orElse(null);
                                if (result == buttonTypeYes) {
                                    // Call the deleteContact method if the user clicked "Yes"
                                    ContactCrud.deleteContact(contactType, contactId);
                                }
                            });
                                
                        }
                        
                        // if no contact corresponds to the provided name we show an error alert
                        else FXManager.showAlert(AlertType.ERROR, "ERROR", "Contact Not Found", contactName + " doesn't correspond to any existing contact.");
                         
                    }
        
                    // if the text field is empty and the search button is clicked
                    else FXManager.showAlert(AlertType.ERROR, "ERROR", "Empty Name Field", "Please provide a contact name.");
        
                });
    }

    // Used to dynamically set each label's corresponding text given a contact values list and a contact type
    public static void contactLabelsFiller(List<Label> labels,  List<String> values, String contactType) {

        labels.get(labels.size() - 1).setWrapText(true); // Allow the label to have multi-line text

        // We use the extracted values to fill the labels
        for (int i = 0 ; i < values.size() ; i++) {

            if (contactType.equals("Person")) {

                // this if statement is responsible for filling the Full Name label in case the contact is a person
                if (i == 0) { 

                    labels.get(i).setText(values.get(i) + " " + values.get(i + 1)); // We concatenate the first and last names

                }

                else { if (i != 1)
                labels.get(i-1).setText(values.get(i)); // if it's another attribute we just set the value directly
                }
            }

            // if it's an enterprise we just set the value directly
            else labels.get(i).setText(values.get(i)); 
        }
    }



}
