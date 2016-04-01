package ro.vvdev.utilsapi;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by victor on 30.03.2016.
 */
public class SharedPrefsUtils {
    private static String PREF_NAME="app";

    public static void addKeyValue(Context context, String key, String value){
        SharedPreferences sharedPref = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getValue(Context context, String key, String defaultValue){
        SharedPreferences sharedPref = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        return sharedPref.getString(key, defaultValue);
    }
}
