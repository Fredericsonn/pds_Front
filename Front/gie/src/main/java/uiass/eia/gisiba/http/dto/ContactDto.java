package uiass.eia.gisiba.http.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import uiass.eia.gisiba.http.DataSender;
import uiass.eia.gisiba.http.parsers.ContactParser;
import uiass.eia.gisiba.http.parsers.Parser;

public class ContactDto {


//////////////////////////////////////////////////// GET METHODS /////////////////////////////////////////////////////////////

    // Find a contact by its id :
    public static List<String> getContactById(int id, String contactType) {

        String responseBody = DataSender.getDataSender("contacts/" + contactType + "/byId/" + id);

        if (!responseBody.equals("Server Error.")) return ContactParser.parseContact(responseBody, contactType);

        return null;
    }

    // Find a contact by its name :
    public static List<String> getContactByName(String name, String contactType) {

        String responseBody = DataSender.getDataSender("/contacts/" + contactType + "/byName/" + name);

        System.out.println(responseBody);

        if (!responseBody.equals("Server Error.")) return ContactParser.parseContact(responseBody, contactType);

        return null;
    }

    // Find all the contacts (either all persons or all enterprises) :
    public static List<List<String>> getAllContactsByType(String contactType) {

        String responseBody = DataSender.getDataSender("contacts/" + contactType);

        List<List<String>> contacts = new ArrayList<List<String>>();

        JsonArray contactsArray = new JsonParser().parse(responseBody).getAsJsonArray();

        contactsArray.forEach(elt -> contacts.add(ContactParser.parseContact(String.valueOf(elt.getAsJsonObject()), contactType)) );


        return contacts;
    }

    // Find all contacts (persons and enterprises) :
    public static List<List<String>> getAllContacts() {

        List<List<String>> persons = getAllContactsByType("Person");

        List<List<String>> enterprises = getAllContactsByType("Enterprise");

        persons.addAll(enterprises);

        return persons;
    }

    public static String getContactType(String name) {

        List<String> contact = getContactByName(name, "Person");

        if (contact != null) return "Person";

        return "Enterprise";
 
    }

//////////////////////////////////////////////////// POST METHODS /////////////////////////////////////////////////////////////

    // Create a new contact :
    public static String postContact(String json, String contactType) {

        return DataSender.postDataSender(json, "contacts/" + contactType + "/post");
    }

    // Send an email to a contact :
    public static String postEmail(String json) {
        
        return DataSender.postDataSender(json, "email/");
    }

//////////////////////////////////////////////////// Delete METHOD /////////////////////////////////////////////////////////////

    public static String deleteContact(int id, String contactType) {

        return DataSender.deleteDataSender("contacts/delete/" + contactType + "/" + id);
    }

//////////////////////////////////////////////////// Put METHOD /////////////////////////////////////////////////////////////

    public static String updateContact(int id, String contactType, String json) {

        if (json != null) return DataSender.putDataSender(json, "contacts/" + contactType + "/put" + "/" + id );

        return "Address Updated Successfully.";
    }
}
