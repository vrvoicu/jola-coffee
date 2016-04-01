package ro.vvdev.smsapi.pojos;

import android.provider.Telephony;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by victor on 05.01.2016.
 */
public class SMS {
    private Map<String, String> smsInfo;

    public SMS(){

        String cols[] = new String[]{
                Telephony.Sms._ID, Telephony.Sms.THREAD_ID, Telephony.Sms.ADDRESS, Telephony.Sms.PERSON, Telephony.Sms.DATE,
                Telephony.Sms.DATE_SENT, Telephony.Sms.PROTOCOL, Telephony.Sms.READ, Telephony.Sms.STATUS, Telephony.Sms.TYPE, Telephony.Sms.REPLY_PATH_PRESENT,
                Telephony.Sms.SUBJECT, Telephony.Sms.BODY, Telephony.Sms.SERVICE_CENTER, Telephony.Sms.LOCKED, Telephony.Sms.SEEN
        };

        smsInfo = new HashMap<>();

        for(String key: cols)
            smsInfo.put(key, "");
    }

    public SMS(Map<String, String> smsInfo){
        this.smsInfo = smsInfo;
    }

    public ArrayList<String> getSMSKeySet(){
        return new ArrayList<>(smsInfo.keySet());
    }

    public Map<String, String> getSmsInfo() {
        return smsInfo;
    }
}
