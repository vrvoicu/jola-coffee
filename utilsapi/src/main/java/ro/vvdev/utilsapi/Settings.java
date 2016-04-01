package ro.vvdev.utilsapi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by victor on 14.12.2015.
 */
public class Settings {
    private static Settings settings;
    private Map<SettingsKeys, Object> settingsMap = new HashMap();

    private Settings(){}

    public synchronized static Settings getInstance(){
        if(settings == null)
            settings = new Settings();
        return settings;
    }

    public void setSetting(SettingsKeys key, Object value){
        settingsMap.put(key, value);
    }

    public Object getSetting(SettingsKeys key){
        return settingsMap.get(key);
    }

    /*public ArrayList<String> getSettings(){
        return new ArrayList<>(settingsMap.keySet());
    }*/
}
