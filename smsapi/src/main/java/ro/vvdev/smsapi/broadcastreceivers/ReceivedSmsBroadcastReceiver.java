package ro.vvdev.smsapi.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import java.util.ArrayList;

import ro.vvdev.smsapi.observer.ReceivedSmsEventObserver;
import ro.vvdev.smsapi.observer.ReceivedSmsEventSubject;
import ro.vvdev.smsapi.observer.SentSMSEventObserver;
import ro.vvdev.smsapi.pojos.SMS;
import ro.vvdev.smsapi.services.SMSService;

/**
 * Created by victor on 30.03.2016.
 */
public class ReceivedSmsBroadcastReceiver extends BroadcastReceiver implements ReceivedSmsEventSubject{

    private SMSService smsService;
    private ArrayList<ReceivedSmsEventObserver> observers;
    public String SMS_EXTRA_NAME ="pdus";

    public ReceivedSmsBroadcastReceiver(SMSService smsService){
        this.smsService = smsService;
        observers = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle extras = intent.getExtras();
            Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );

            SmsMessage smsMessage;

            if (Build.VERSION.SDK_INT >= 19) { //KITKAT
                SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                smsMessage = msgs[0];
            } else {
                Object pdus[] = (Object[]) extras.get("pdus");
                smsMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
            }

            String body = smsMessage.getMessageBody().toString();
            String address = smsMessage.getOriginatingAddress();

            SMS sms = new SMS();
            sms.getSmsInfo().put("body", body);
            sms.getSmsInfo().put("address", address);

            notifyReceivedSmsEventObservers(sms);

        }

    }

    @Override
    public void addReceivedSmsEventObserver(ReceivedSmsEventObserver receivedSmsEventObserver) {
        observers.add(receivedSmsEventObserver);
    }

    @Override
    public void removeReceivedSmsEventObserver(ReceivedSmsEventObserver receivedSmsEventObserver) {
        observers.remove(receivedSmsEventObserver);
    }

    @Override
    public void notifyReceivedSmsEventObservers(SMS sms) {
        for(ReceivedSmsEventObserver observer: observers) {
            observer.onSmsReceived(sms);
        }
    }
}
