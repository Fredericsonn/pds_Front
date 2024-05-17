package uiass.eia.gisiba.http;

import java.util.HashMap;
import java.util.Locale.Category;
import java.util.Map;

import uiass.eia.gisiba.http.dto.CategoryDto;
import uiass.eia.gisiba.http.dto.InventoryDto;
import uiass.eia.gisiba.http.dto.OrderDto;
import uiass.eia.gisiba.http.dto.ProductDto;
import uiass.eia.gisiba.http.dto.PurchaseDto;
import uiass.eia.gisiba.http.parsers.Parser;
import uiass.eia.gisiba.http.parsers.ProductParser;
import uiass.eia.gisiba.http.parsers.PurchaseParser;

public class AppTest {

    public static void main(String[] args) {

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> dateMap = new HashMap<String,Object>();

        Map<String,Object> supplierMap = new HashMap<String,Object>();

        //dateMap.put("startDate", "2017-05-12");

        //dateMap.put("endDate", "2020-05-12");

        /*supplierMap.put("supplierName", "Brett Robbins");

        supplierMap.put("supplierType", "Person");

        //map.put("date", dateMap);

        map.put("status", "PENDING");

        String json = Parser.jsonGenerator(map);

        System.out.println(PurchaseDto.purchasesFilter(json));*/


        System.out.println(ProductDto.getAllProducts());

    }

    
}
