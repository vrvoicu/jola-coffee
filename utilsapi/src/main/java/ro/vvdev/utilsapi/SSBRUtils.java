package ro.vvdev.utilsapi;

import android.app.Activity;
import android.content.BroadcastReceiver;

import android.content.Context;

import android.content.Intent;

import android.content.IntentFilter;

import android.content.ServiceConnection;

import android.hardware.Sensor;

import android.hardware.SensorEventListener;

import android.hardware.SensorManager;

import java.util.Map;


public class SSBRUtils {

    public static void startActivity(Context context, Class<?> cls, Map<String, String> extra){
        Intent intent = new Intent(context, cls);
        if(extra != null)
            for(String key: extra.keySet())
                intent.putExtra(key, extra.get(key));
        context.startActivity(intent);
    }

    /*public static void startActivityForResult(Context context, Class<?> cls, Map<String, String> extra, int requestCode){
        Intent intent = new Intent(context, cls);
        if(extra != null)
            for(String key: extra.keySet())
                intent.putExtra(key, extra.get(key));
        ((Activity)context).startActivityForResult(intent, requestCode);
    }*/

    public static void startActivityForResult(Context context, Class<?> cls, Intent intent, int requestCode){
        if(intent == null)
            intent = new Intent(context, cls);
        ((Activity)context).startActivityForResult(intent, requestCode);
    }

    public static void startService(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startService(intent);
    }


    public static void bindService(Context context, Class<?> cls, ServiceConnection serviceConnection, boolean isBound) {
        if (isBound)
            return;

        Intent intent = new Intent(context, cls);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    public static void registerReceiver(Context context, BroadcastReceiver broadcastReceiver, String[] actions, boolean isRegistered) {
        if (isRegistered)
            return;

        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);//WifiManager.WIFI_STATE_CHANGED_ACTION
            if(action.equals("android.provider.Telephony.SMS_RECEIVED"))
                filter.setPriority(999);
        }
        context.registerReceiver(broadcastReceiver, filter);
    }


    public static void registerSensor(SensorManager sensorManager, SensorEventListener sensorListener, Sensor sensor, int rate, boolean isSensorRegistered) {
        if (isSensorRegistered)
            return;

        sensorManager.registerListener(sensorListener, sensor, rate);
    }


    public static boolean unbindService(Context context, ServiceConnection serviceConnection, boolean isServiceBound) {
        if (isServiceBound) {
            context.unbindService(serviceConnection);
            return !isServiceBound;
        }

        return isServiceBound;
    }


    public static void unregisterReceiver(Context context, BroadcastReceiver broadcastReceiver) {
        context.unregisterReceiver(broadcastReceiver);
    }


}
