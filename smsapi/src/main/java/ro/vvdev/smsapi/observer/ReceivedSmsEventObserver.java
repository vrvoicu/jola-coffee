package ro.vvdev.smsapi.observer;

import ro.vvdev.smsapi.pojos.SMS;

/**
 * Created by victor on 30.03.2016.
 */
public interface ReceivedSmsEventObserver {

    void onSmsReceived(SMS sms);
}
