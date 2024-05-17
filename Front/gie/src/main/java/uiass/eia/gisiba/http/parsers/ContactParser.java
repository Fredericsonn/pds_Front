package uiass.eia.gisiba.http.parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ContactParser extends Parser {


    private static Map<String, List<String>> attributes_by_type_map = new HashMap<String, List<String>>() {{
        put("Person", Arrays.asList("firstName","lastName","id","phoneNumber","email"));
        put("Enterprise", Arrays.asList("enterpriseName","type","id","phoneNumber","email"));
    }};

    public static Map<String, List<String>> contact_creation_columns_by_type_map = new HashMap<String, List<String>>() {{
        put("Person", Arrays.asList("firstName","lastName","phoneNumber","email","houseNumber","neighborhood","city","zipCode","country"));
        put("Enterprise", Arrays.asList("enterpriseName","phoneNumber","email","houseNumber","neighborhood","city","zipCode","country","type"));
    }};

    public static Map<String, List<String>> contact_update_columns_by_type_map = new HashMap<String, List<String>>() {{
        put("Person", Arrays.asList("firstName","lastName","phoneNumber","email"));
        put("Enterprise", Arrays.asList("enterpriseName","phoneNumber","email","type"));
    }};

    public static Map<String, List<String>> updateContactPaneAttributes = new HashMap<String, List<String>>() {{
        put("Person", Arrays.asList("firstName","lastName","phoneNumber","email","houseNumber","neighborhood","city","zipCode","country"));
        put("Enterprise", Arrays.asList("enterpriseName","phoneNumber","email","houseNumber","neighborhood","city","zipCode","country","type"));
    }};

    public static Map<String, List<String>> contactTextFieldAttributesMap = new HashMap<String, List<String>>() {{
        put("Person", Arrays.asList("first","second","phoneNumber","email","houseNumber","neighborhood","city","zipCode","country"));
        put("Enterprise", Arrays.asList("first","phoneNumber","email","houseNumber","neighborhood","city","zipCode","country","type"));
    }};

    public static List<String> addressAttributes = Arrays.asList("addressId","houseNumber","neighborhood","city","zipCode","country");

    public static List<String> update_address_attributes = Arrays.asList("houseNumber","neighborhood","city","zipCode","country");

    public static List<String> contactCreationTextFieldsReferences = Arrays.asList("first","second","phoneNumber","email","houseNumber","neighborhood","city","zipCode","country");

    public static List<String> contactUpdateTextFieldsReferences = Arrays.asList("first","second","phoneNumber","email");

    public static List<String> email_sending_attributes = Arrays.asList("receiver","subject","body");



//////////////////////////////////////////////// MAPS AND JSON GENERATORS ///////////////////////////////////////////////////////////

    public static Map<String,Object> contactCreationMapGenerator(List values, String contactType) {

        Map<String,Object> map = new HashMap<String,Object>();

        List<String> attributes = contact_creation_columns_by_type_map.get(contactType);

        
        for (int i=0 ; i < attributes.size() ; i++) {

            map.put(attributes.get(i), values.get(i));

        }

        return map;
    }

    public static Map<String,Object> contactUpdateMapGenerator(List values, String contactType) {

        Map<String,Object> map = new HashMap<String,Object>();

        List<String> attributes = contact_update_columns_by_type_map.get(contactType);

        for (int i=0 ; i < attributes.size() ; i++) {

            if (values.get(i) != null) 
            
            if (!((String) values.get(i)).equals("")) map.put(attributes.get(i), values.get(i));

        }

        return map;
    }

    public static Map<String,Object> addressMapGenerator(List values) {

        Map<String,Object> map = new HashMap<String,Object>();

        List<String> attributes = update_address_attributes;

        
        for (int i=0 ; i < attributes.size() ; i++) {
                        
            if (attributes.get(i).equals("houseNumber")) {

                String value = String.valueOf(values.get(i));

                if (Integer.parseInt(value) != 0) map.put(attributes.get(i), Integer.parseInt(value));
            }

            else if (!((String) values.get(i)).equals("")) map.put(attributes.get(i), values.get(i));
            
        }

        return map;
    }

    public static Map<String,Object> emailSendingMapGenerator(List values) {

        Map<String,Object> map = new HashMap<String,Object>();

        List attributes = email_sending_attributes;

        for (int i = 0 ; i < attributes.size() ; i++) {

            String attribute = String.valueOf(attributes.get(i));

            String value = String.valueOf(values.get(i));

            map.put(attribute, value);
        }

        return map;
    }

//////////////////////////////////////////////// PARSERS ///////////////////////////////////////////////////////////////////

    // Contact parser
    public static List<String> parseContact(String responseBody, String contactType) {

    	JsonObject contact = new JsonParser().parse(responseBody).getAsJsonObject();

        List<String> contactStringInfo = new ArrayList<String>();
        
        List<Integer> contactIntInfo = new ArrayList<Integer>();
 	
        List<String> attributes = attributes_by_type_map.get(contactType);

        for (String attribute : attributes) {

            if (attribute.equals("id")) {
                
                contactIntInfo.add(collectInt(contact, attribute));
            }
            else contactStringInfo.add(collectString(contact, attribute));
        }
    
        JsonObject addressObject = contact.has("address") ? contact.get("address").getAsJsonObject() : null;
            
        for (String attribute : addressAttributes) {

            if (attribute.equals("addressId") || attribute.equals("houseNumber")) {
                
                contactIntInfo.add(collectInt(addressObject, attribute));
            }
            else contactStringInfo.add(collectString(addressObject, attribute));
        }
        
        String first_or_enterprise_name = contactStringInfo.get(0); 
        String last_name_or_enterprise_type = contactStringInfo.get(1);
        String id = String.valueOf(contactIntInfo.get(0));
        String phoneNumber = contactStringInfo.get(2);
        String email = contactStringInfo.get(3);

        String addressId = String.valueOf(contactIntInfo.get(1));
        String houseNumber = String.valueOf(contactIntInfo.get(2));
        String neighborhood = String.valueOf(contactStringInfo.get(4));
        String city = String.valueOf(contactStringInfo.get(5));
        String zipCode = String.valueOf(contactStringInfo.get(6));
        String country = String.valueOf(contactStringInfo.get(7));

        return Arrays.asList(id,first_or_enterprise_name,last_name_or_enterprise_type,phoneNumber,email,addressId, houseNumber, neighborhood,
        
        city, zipCode ,country);
    }

    // Address parser
    public static List<String> parseAddress(String responseBody) {

    	JsonObject addressObject = new JsonParser().parse(responseBody).getAsJsonObject();

        List<String> addressStringInfo = new ArrayList<String>();

        List<Integer> addressIntInfo = new ArrayList<Integer>();
    
        for (String attribute : addressAttributes) {

            if (attribute.equals("addressId") || attribute.equals("houseNumber")) {
                
                addressIntInfo.add(collectInt(addressObject, attribute));
            }
            else addressStringInfo.add(collectString(addressObject, attribute));
        }

        String addressId = String.valueOf(addressIntInfo.get(0));
        String houseNumber = String.valueOf(addressIntInfo.get(1));
        String neighborhood = addressStringInfo.get(0);
        String city =  addressStringInfo.get(1);
        String zipCode = addressStringInfo.get(2);
        String country = addressStringInfo.get(3);

        return Arrays.asList(addressId,houseNumber,neighborhood,city,zipCode,country);
    }


}
