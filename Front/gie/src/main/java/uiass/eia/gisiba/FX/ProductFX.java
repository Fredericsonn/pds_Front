package uiass.eia.gisiba.FX;

import java.util.List;

public class ProductFX {

    public static boolean productCategoryUpdateValidator(List<String> categoryList) {
        
        for (String categoryType : categoryList) {

            if (categoryType == null) return false;

        }

        return true;
        
    }
}
