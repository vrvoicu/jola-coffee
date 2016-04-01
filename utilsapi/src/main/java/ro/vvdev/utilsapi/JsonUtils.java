package ro.vvdev.utilsapi;

import org.json.JSONObject;

/**
 * Created by victor on 05.03.2016.
 */
public class JsonUtils {

    public static JSONObject jsonObjectFromString(String string){
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(string);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return jsonObject;
    }

}
