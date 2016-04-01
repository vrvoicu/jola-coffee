package ro.vvdev.smsapi.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;

import java.util.ArrayList;

import ro.vvdev.smsapi.observer.SentSMSEventObserver;
import ro.vvdev.smsapi.observer.SentSMSEventSubject;
import ro.vvdev.smsapi.pojos.PendingSMS;
import ro.vvdev.smsapi.pojos.SMS;
import ro.vvdev.smsapi.pojos.SMSServiceIntentInfo;
import ro.vvdev.smsapi.pojos.SMSState;
import ro.vvdev.smsapi.services.SMSService;

/**
 * Created by victor on 05.01.2016.
 */
public class SmsApiBroadcastReceiver extends BroadcastReceiver implements SentSMSEventSubject {

    private ArrayList<SentSMSEventObserver> observers;
    private SMSService smsService;

    public SmsApiBroadcastReceiver(SMSService smsService){
        this.smsService = smsService;
        observers = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() == SMSServiceIntentInfo.SENT){
            int requestCode = intent.getExtras().getInt(SMSServiceIntentInfo.IDENTIFIER);
            PendingSMS pendingSMS = getPendingSMSById(requestCode);

            if(pendingSMS == null)
                return;

            SMS sms = getSMSfromPendingSMS(pendingSMS);

            pendingSMS.setSms(sms);
            notifyObservers(sms, SMSState.SENT);

        }
        else if(intent.getAction() == SMSServiceIntentInfo.DELIVERED){
            int requestCode = intent.getExtras().getInt(SMSServiceIntentInfo.IDENTIFIER);

            PendingSMS pendingSMS = getPendingSMSById(requestCode);

            if(pendingSMS == null)
                return;

            notifyObservers(pendingSMS.getSms(), SMSState.DELIVERED);
            smsService.getPendingSms().remove(pendingSMS);
        }
    }

    private PendingSMS getPendingSMSById(int id){
        for(PendingSMS pendingSms: smsService.getPendingSms()) {
            if(pendingSms.getId() == id)
                return pendingSms;
        }
        return null;
    }

    private SMS getSMSfromPendingSMS(PendingSMS pendingSms){
        String sentContent, pendingContent = pendingSms.getContent();
        long currentTime = System.currentTimeMillis();
        ArrayList<SMS> smss = smsService.getMessages(Telephony.Sms.DATE + " > " + (currentTime - 1000 * 60));

        for(SMS sms: smss) {
            sentContent = sms.getSmsInfo().get(Telephony.Sms.BODY);
            if (sentContent.equals(pendingContent)) {
                return sms;
            }
        }
        return null;
    }

    @Override
    public void addSMSEventObserver(SentSMSEventObserver smsEventObserver) {
        observers.add(smsEventObserver);
    }

    @Override
    public void removeSMSEventObserver(SentSMSEventObserver smsEventObserver) {
        observers.remove(smsEventObserver);
    }

    @Override
    public void notifyObservers(SMS sms, SMSState smsState) {
        for(SentSMSEventObserver observer: observers)
            observer.onSentSMSEvent(sms, smsState);
    }
}
