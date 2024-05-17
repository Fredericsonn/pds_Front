package uiass.eia.gisiba.http.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import uiass.eia.gisiba.http.DataSender;
import uiass.eia.gisiba.http.parsers.ContactParser;

public class AddressDto {


//////////////////////////////////////////////////// GET METHODS /////////////////////////////////////////////////////////////

    public static List<String> getAddressById(int id) {

        String responseBody = DataSender.getDataSender("addresses/" + id);

        if (!responseBody.equals("Server Error.")) return ContactParser.parseAddress(responseBody);

        return null;
    }

        // Find a contact by its address id :
        public static String addressFormulator(int id, String contactType) {

            String responseBody = DataSender.getDataSender("contacts/" + contactType + "/byAddressId/" + id);

            if (!responseBody.equals("Internal Server Error")) {

                List contact = ContactParser.parseContact(responseBody, contactType);
    
                return String.valueOf(contact.get(6));
            }
            return null;
        }

            
    
    public static List<List<String>> getAllAddresses() {

        List<List<String>> parsedAddresses = new ArrayList<List<String> >();

        String responseBody = DataSender.getDataSender("addresses");

        JsonArray contacts = new JsonParser().parse(responseBody).getAsJsonArray();

        contacts.forEach(elt -> parsedAddresses.add(ContactParser.parseAddress(String.valueOf(elt.getAsJsonObject()))) );


        return parsedAddresses;
    }

//////////////////////////////////////////////////// PUT METHOD /////////////////////////////////////////////////////////////

    public static String updateAddress(int id, String json) {

        if (json != null) return DataSender.putDataSender(json, "addresses/put/" + id );

        return "no Update";
    }
}
