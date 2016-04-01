package ro.vvdev.smsapi.observer;

import ro.vvdev.smsapi.pojos.SMS;

/**
 * Created by victor on 30.03.2016.
 */
public interface ReceivedSmsEventSubject {

    void addReceivedSmsEventObserver(ReceivedSmsEventObserver receivedSmsEventObserver);
    void removeReceivedSmsEventObserver(ReceivedSmsEventObserver receivedSmsEventObserver);
    void notifyReceivedSmsEventObservers(SMS sms);
}
