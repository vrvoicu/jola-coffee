package ro.vvdev.smsapi.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.HashMap;

import ro.vvdev.smsapi.broadcastreceivers.ReceivedSmsBroadcastReceiver;
import ro.vvdev.smsapi.observer.ReceivedSmsEventObserver;
import ro.vvdev.smsapi.observer.ReceivedSmsEventSubject;
import ro.vvdev.smsapi.observer.SentSMSEventObserver;
import ro.vvdev.smsapi.observer.SentSMSEventSubject;
import ro.vvdev.smsapi.pojos.PendingSMS;
import ro.vvdev.smsapi.pojos.SMS;
import ro.vvdev.smsapi.pojos.SMSServiceIntentInfo;
import ro.vvdev.smsapi.broadcastreceivers.SmsApiBroadcastReceiver;
import ro.vvdev.smsapi.pojos.SMSState;
import ro.vvdev.utilsapi.SSBRUtils;

/**
 * Created by victor on 05.01.2016.
 */
public class SMSService extends Service implements Runnable, SentSMSEventSubject, ReceivedSmsEventSubject{

    public class SMSServiceBinder extends Binder {
        public SMSService getService(){
            return SMSService.this;
        }
    }

    private IBinder socketServiceBinder = new SMSServiceBinder();

    private ArrayList<SMS> smss;

    private SmsApiBroadcastReceiver smsApiBroadcastReceiver;
    private ReceivedSmsBroadcastReceiver receivedSmsBroadcastReceiver;

    //private IntentFilter smsFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");

    @Override
    public void onCreate() {
        super.onCreate();

        smss = new ArrayList<SMS>();
        smsApiBroadcastReceiver = new SmsApiBroadcastReceiver(SMSService.this);
        receivedSmsBroadcastReceiver = new ReceivedSmsBroadcastReceiver(SMSService.this);

        SSBRUtils.registerReceiver(SMSService.this, smsApiBroadcastReceiver, new String[]{SMSServiceIntentInfo.SENT, SMSServiceIntentInfo.DELIVERED}, false);
        SSBRUtils.registerReceiver(SMSService.this, receivedSmsBroadcastReceiver, new String[]{SMSServiceIntentInfo.RECEIVED}, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SSBRUtils.unregisterReceiver(SMSService.this, smsApiBroadcastReceiver);
        SSBRUtils.unregisterReceiver(SMSService.this, receivedSmsBroadcastReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return socketServiceBinder;
    }

    @Override
    public void run() {

    }

    public ArrayList<SMS> getMessages(String condition){
        smss.clear();

        String cols[] = new String[]{
                Telephony.Sms._ID, Telephony.Sms.THREAD_ID, Telephony.Sms.ADDRESS, Telephony.Sms.PERSON, Telephony.Sms.DATE,
                Telephony.Sms.DATE_SENT, Telephony.Sms.PROTOCOL, Telephony.Sms.READ, Telephony.Sms.STATUS, Telephony.Sms.TYPE, Telephony.Sms.REPLY_PATH_PRESENT,
                Telephony.Sms.SUBJECT, Telephony.Sms.BODY, Telephony.Sms.SERVICE_CENTER, Telephony.Sms.LOCKED, Telephony.Sms.SEEN
        };

        //HashMap<String, ArrayList<String>> colsValues = getInfo(Uri.parse("content://sms"), cols, null, Telephony.Sms._ID + ">? AND "+Telephony.Sms.TYPE+"=1", new String[]{lastSMSId + ""}, null);
        HashMap<String, ArrayList<String>> colsValues = getInfo(Uri.parse("content://sms"), cols, null, condition, null, null);

        int size = 0;
        if(colsValues.get(cols[0]) != null)
            size = colsValues.get(cols[0]).size();

        SMS sms;
        HashMap<String, String> smsInfo;
        for (int index = 0; index < size; index++) {
            smsInfo = new HashMap<String, String>();
            for (String key : cols) {
                smsInfo.put(key, colsValues.get(key).get(index));
            }
            sms = new SMS(smsInfo);
            smss.add(sms);
        }

        return smss;

    }

    public HashMap<String, ArrayList<String>> getInfo(Uri uri, String[] cols, String[] projection, String selection, String[] selectionArgs, String order) {
        HashMap<String, ArrayList<String>> colsValues = new HashMap<String, ArrayList<String>>();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, order);
        while (cursor.moveToNext()) {
            for (String key : cols)
                if (colsValues.containsKey(key))
                    colsValues.get(key).add(cursor.getString(cursor.getColumnIndex(key)));
                else {
                    colsValues.put(key, new ArrayList<String>());
                    colsValues.get(key).add(cursor.getString(cursor.getColumnIndex(key)));
                }
        }
        cursor.close();
        return colsValues;
    }

    private int requestCode;
    private void sendSMS(PendingSMS pendingSMS){
        PendingIntent sentPI, deliveredPI;

        requestCode = pendingSMS.getId();

        Intent sentIntent=new Intent(SMSServiceIntentInfo.SENT);
        sentIntent.putExtra(SMSServiceIntentInfo.IDENTIFIER, requestCode);
        sentPI = PendingIntent.getBroadcast(SMSService.this, requestCode, sentIntent, PendingIntent.FLAG_ONE_SHOT);

        Intent deliveredIntent=new Intent(SMSServiceIntentInfo.DELIVERED);
        deliveredIntent.putExtra(SMSServiceIntentInfo.IDENTIFIER, requestCode);
        deliveredPI = PendingIntent.getBroadcast(SMSService.this, requestCode, deliveredIntent, PendingIntent.FLAG_ONE_SHOT);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(pendingSMS.getNumber(), null, pendingSMS.getContent(), sentPI, deliveredPI);
    }

    private ArrayList<PendingSMS> pendingSms = new ArrayList<>();
    private PendingSMS pendingSMS;
    public void sendSMS(String number, String content){
        pendingSMS = new PendingSMS(number, content);
        pendingSms.add(pendingSMS);
        sendSMS(pendingSMS);
    }

    public ArrayList<PendingSMS> getPendingSms() {
        return pendingSms;
    }

    @Override
    public void addSMSEventObserver(SentSMSEventObserver smsEventObserver) {
        smsApiBroadcastReceiver.addSMSEventObserver(smsEventObserver);
    }

    @Override
    public void removeSMSEventObserver(SentSMSEventObserver smsEventObserver) {
        smsApiBroadcastReceiver.removeSMSEventObserver(smsEventObserver);
    }

    @Override
    public void notifyObservers(SMS sms, SMSState smsState) {
        smsApiBroadcastReceiver.notifyObservers(sms, smsState);
    }

    @Override
    public void addReceivedSmsEventObserver(ReceivedSmsEventObserver receivedSmsEventObserver) {
        receivedSmsBroadcastReceiver.addReceivedSmsEventObserver(receivedSmsEventObserver);
    }

    @Override
    public void removeReceivedSmsEventObserver(ReceivedSmsEventObserver receivedSmsEventObserver) {
        receivedSmsBroadcastReceiver.removeReceivedSmsEventObserver(receivedSmsEventObserver);
    }

    @Override
    public void notifyReceivedSmsEventObservers(SMS sms) {
        receivedSmsBroadcastReceiver.notifyReceivedSmsEventObservers(sms);
    }
}
