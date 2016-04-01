package ro.vvdev.jolacoffee;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ro.vvdev.smsapi.observer.ReceivedSmsEventObserver;
import ro.vvdev.smsapi.observer.SentSMSEventObserver;
import ro.vvdev.smsapi.pojos.SMS;
import ro.vvdev.smsapi.pojos.SMSState;
import ro.vvdev.smsapi.services.SMSService;
import ro.vvdev.utilsapi.SSBRUtils;
import ro.vvdev.utilsapi.SharedPrefsUtils;

/**
 * Created by victor on 30.03.2016.
 */
public class ControlActivity extends AppCompatActivity implements ServiceConnection, SentSMSEventObserver, ReceivedSmsEventObserver, View.OnClickListener{

    private Button power, steam, coffee;
    private TextView power_counter, steam_counter, coffee_counter;
    private Button power_state, steam_state, coffee_state;

    private int powerNr = 0, coffeeNr = 0, steamNr = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        power = (Button)findViewById(R.id.power);
        power.setOnClickListener(ControlActivity.this);
        steam = (Button)findViewById(R.id.steam);
        steam.setOnClickListener(ControlActivity.this);
        coffee = (Button)findViewById(R.id.coffee);
        coffee.setOnClickListener(ControlActivity.this);

        power_state = (Button)findViewById(R.id.power_state);
        coffee_state = (Button)findViewById(R.id.coffee_state);
        steam_state = (Button)findViewById(R.id.steam_state);

        power_state.setBackgroundColor(Color.RED);
        coffee_state.setBackgroundColor(Color.RED);
        steam_state.setBackgroundColor(Color.RED);

        power_counter = (TextView)findViewById(R.id.power_counter);
        coffee_counter = (TextView)findViewById(R.id.coffee_counter);
        steam_counter = (TextView)findViewById(R.id.steam_counter);

        powerNr = Integer.parseInt(SharedPrefsUtils.getValue(ControlActivity.this, "power", "0"));
        coffeeNr = Integer.parseInt(SharedPrefsUtils.getValue(ControlActivity.this, "coffee", "0"));
        steamNr = Integer.parseInt(SharedPrefsUtils.getValue(ControlActivity.this, "steam", "0"));

        power_counter.setText(powerNr+"");
        coffee_counter.setText(coffeeNr+"");
        steam_counter.setText(steamNr+"");

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(isSmsServiceBound && smsService != null) {
            SSBRUtils.unbindService(ControlActivity.this, ControlActivity.this, true);
            smsService = null;
            isSmsServiceBound = false;
        }
    }

    private boolean isSmsServiceBound = false;

    @Override
    protected void onResume() {
        super.onResume();

        SSBRUtils.bindService(ControlActivity.this, SMSService.class, ControlActivity.this, isSmsServiceBound);
    }

    private SMSService smsService;

    @Override
    public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
        if(serviceBinder instanceof SMSService.SMSServiceBinder){
            smsService = ((SMSService.SMSServiceBinder) serviceBinder).getService();

            smsService.addSMSEventObserver(ControlActivity.this);
            smsService.addReceivedSmsEventObserver(ControlActivity.this);

            isSmsServiceBound = true;

            smsService.sendSMS(phoneNumber,"state");
            //dialog = ProgressDialog.show(ControlActivity.this, "Se preia starea", "Va rugam asteptati", true);

        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @Override
    public void onSentSMSEvent(SMS sms, SMSState smsState) {

    }

    @Override
    public void onSmsReceived(SMS sms) {
        String body = sms.getSmsInfo().get("body");
        String address = sms.getSmsInfo().get("address");

        if(address.indexOf(phoneNumber) > -1){

            if(body.indexOf("steamStart") > -1){
                steam_state.setBackgroundColor(Color.GREEN);
                steam_state.setText("ON");
                dialog.dismiss();
            }
            else if(body.indexOf("steamStop") > -1){
                steam_state.setBackgroundColor(Color.RED);
                steam_state.setText("OFF");
                SharedPrefsUtils.addKeyValue(ControlActivity.this, "steam", (++steamNr) + "");
                steam_counter.setText(steamNr + "");
                dialog.dismiss();
            }
            else if(body.indexOf("powerOn") > -1){
                power_state.setBackgroundColor(Color.GREEN);
                power_state.setText("ON");
                if(dialog != null)
                    dialog.dismiss();
            }
            else if(body.indexOf("powerOff") > -1){
                power_state.setBackgroundColor(Color.RED);
                SharedPrefsUtils.addKeyValue(ControlActivity.this, "power", (++powerNr) + "");
                power_counter.setText(powerNr + "");
                power_state.setText("OFF");
                dialog.dismiss();
            }
            else if(body.indexOf("coffeeOn") > -1){
                coffee_state.setBackgroundColor(Color.GREEN);
                coffee_state.setText("ON");
                dialog.dismiss();
            }
            else if(body.indexOf("coffeeOff") > -1) {
                coffee_state.setBackgroundColor(Color.RED);
                SharedPrefsUtils.addKeyValue(ControlActivity.this, "coffee", (++coffeeNr) +"");
                coffee_counter.setText(coffeeNr+"");
                coffee_state.setText("OFF");
                dialog.dismiss();
            }
            else if(body.indexOf("statePowerOff") > -1){
                dialog.dismiss();
            }




        }

        //SharedPrefsUtils.addKeyValue(ControlActivity.this, "power", (powerNr++)+"");
        //SharedPrefsUtils.addKeyValue(ControlActivity.this, "coffee", (coffeeNr++)+"");
        //SharedPrefsUtils.addKeyValue(ControlActivity.this, "steam", (steamNr++)+"");
    }

    private String phoneNumber = "0786589389";

    private ProgressDialog dialog;

    @Override
    public void onClick(View v) {

        if(smsService == null)
            return;

        if(v == power){
            smsService.sendSMS(phoneNumber, "power");
            dialog = ProgressDialog.show(ControlActivity.this, "Comanda trimisa", "Va rugam asteptati", true);
        }
        else if(v == coffee){
            smsService.sendSMS(phoneNumber, "coffee");
            dialog = ProgressDialog.show(ControlActivity.this, "Comanda trimisa", "Va rugam asteptati", true);
        }
        else if(v == steam){
            smsService.sendSMS(phoneNumber, "steam");
            dialog = ProgressDialog.show(ControlActivity.this, "Comanda trimisa", "Va rugam asteptati", true);
        }
    }


}
