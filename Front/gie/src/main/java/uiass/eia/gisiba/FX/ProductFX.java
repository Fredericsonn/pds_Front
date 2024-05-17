package uiass.eia.gisiba.FX;

import java.util.Arrays;
import java.util.List;

public class ProductFX {

    public static List<String> categories = Arrays.asList("LAPTOP", "DESKTOP_PC", "PHONE", "PRINTER", "TABLET", "SMARTWATCH", "MONITOR", 
    "HEADPHONES", "SPEAKER", "CAMERA", "GAMING_CONSOLE", "SCANNER");

    public static boolean productCategoryUpdateValidator(List<String> categoryList) {
        
        for (String categoryType : categoryList) {

            if (categoryType == null) return false;

        }

        return true;
        
    }
}
