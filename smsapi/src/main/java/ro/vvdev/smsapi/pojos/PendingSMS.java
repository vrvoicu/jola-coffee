package ro.vvdev.smsapi.pojos;

import android.app.PendingIntent;

import java.util.Date;

/**
 * Created by victor on 05.01.2016.
 */
public class PendingSMS {
    private String number;
    private String content;
    private Date date;
    private SMS sms;

    public PendingSMS(String number, String content){
        this.number = number;
        this.content = content;
        date = new Date();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId(){
        return date.toString().hashCode();
    }

    public SMS getSms() {
        return sms;
    }

    public void setSms(SMS sms) {
        this.sms = sms;
    }
}
