package com.guido.smsdispatch;

/**
 * Created by Guido on 21/08/2017.
 */

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Guido on 16/08/2017.
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {

    SharedPreferences prefs;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        ShowToast(context,"Received: " + intent.getAction());

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            // the mainActivity starts on boot
            Intent myStarterIntent = new Intent(context, MainActivity.class);
            myStarterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myStarterIntent);
            return;
        }


        //---get the SMS message passed in---
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null)
        {
            //---retrieve the SMS messages received---
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++){
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                str = "SMS from " + msgs[i].getOriginatingAddress();
                str += " :";
                str += msgs[i].getMessageBody().toString();
                addListLog(str);
                ShowToast(context, str);
                // process SMS and take the proper actions
                processSMS(context, msgs[i].getOriginatingAddress(),msgs[i].getMessageBody().toString());
            }
        }else
            addListLog("SMS Received but bundle was null");


    }

    static String toasttext;

    public static void ShowToast(final Context context, String text){

        toasttext = text;
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(context,toasttext,Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void processSMS(Context context, String from,String msg)
    {
        // get the profile rules
        ArrayList<ProfileItem> mDataset = ProfileActivity.getMyDataset(context);
        boolean send = false;
        for (ProfileItem pi : mDataset
             ) {
            switch(pi.Type){
                case ItemType.From:
                    if(pi.Enabled){
                        if(pi.Text.equals(from)){
                            // the sender is OK
                            send = true;
                        }
                    }
                    break;

                case ItemType.To:
                    if(pi.Enabled){
                        if(pi.Text.length()>0 && send){
                            SmsManager smsManager = SmsManager.getDefault();
                            String myUniqueActionIntent = "Msg: " + msg + "\r\nTo:" + pi.Text;
                            Intent intent = new Intent(myUniqueActionIntent);
                            PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0,
                                    intent, 0);
                            context.getApplicationContext().registerReceiver(
                                    new MessageSentListener(),
                                    new IntentFilter(myUniqueActionIntent));
                            smsManager.sendTextMessage(pi.Text, null,
                                    msg, sentIntent, null);
                            addListLog("Sending " + myUniqueActionIntent);
                        }
                    }
                    break;
            }
        }
    }

    public void addListLog(String addlog) {
        // get current date time
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
        String currentDateandTime = sdf.format(currentTime);
        // Retrieve the log values stored in shared preferences "prefs"
        Gson gson = new Gson();
        String jsonText = prefs.getString("myLogStrings", "");
        ArrayList<String> logStrings = gson.fromJson(jsonText, new TypeToken<ArrayList<String>>(){}.getType());
        if(logStrings == null)
            logStrings = new ArrayList<String>();
        // add the new entry "addLog"
        logStrings.add(0, currentDateandTime + " " + addlog);
        jsonText = gson.toJson(logStrings);
        // save to Preferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("myLogStrings", jsonText);
        editor.commit();
    }
}
