package ro.vvdev.utilsapi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by victor on 02.02.2016.
 */
public class EventLogger {
    public static int COMPLETE, WARNING, INFO;
    private static Map<String, Integer> apilogType;
    private static Map<String, Boolean> apiEnabled;

    private static void initMaps(){
        if(apilogType == null) {
            apilogType = new HashMap<>();
            apiEnabled = new HashMap<>();
        }
    }

    public static void setLogType(String api, int logType){
        initMaps();

        apilogType.put(api, logType);
    }

    public static void log(String api,int logType, String message){
        initMaps();

        if(!apiEnabled.keySet().contains(api) || !apiEnabled.get(api))
            return;

        if(apilogType.get(api)!=logType && apilogType.get(api) != COMPLETE)
            return;

        System.out.println(api+": "+message);
    }

    public static void setLoggingState(String forApi, boolean enabled){
        initMaps();

        apiEnabled.put(forApi, enabled);
        apilogType.put(forApi, COMPLETE);
    }
}
