package uiass.eia.gisiba.http;

import com.google.gson.Gson;

public class GetGson {

    private static volatile Gson gson;

    private GetGson() {}

    public static Gson getGson() {
        if (gson == null) {
            synchronized (GetGson.class) {
                if (gson == null) {
                    gson = new Gson();
                }
            }
        }
        return gson;
    }
}
