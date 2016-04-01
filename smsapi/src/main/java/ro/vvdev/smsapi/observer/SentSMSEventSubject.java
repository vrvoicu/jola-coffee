package ro.vvdev.smsapi.observer;

import ro.vvdev.smsapi.pojos.SMS;
import ro.vvdev.smsapi.pojos.SMSState;

/**
 * Created by victor on 10.01.2016.
 */
public interface SentSMSEventSubject {

    void addSMSEventObserver(SentSMSEventObserver smsEventObserver);
    void removeSMSEventObserver(SentSMSEventObserver smsEventObserver);
    void notifyObservers(SMS sms, SMSState smsState);
}
