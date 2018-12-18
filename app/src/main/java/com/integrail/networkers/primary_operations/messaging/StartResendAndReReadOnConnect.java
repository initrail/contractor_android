package com.integrail.networkers.primary_operations.messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import com.integrail.networkers.data_representations.message_representations.Message;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.primary_operations.storage.ConversationDataBase;
import com.integrail.networkers.primary_operations.storage.MessageDataBase;
import com.integrail.networkers.primary_operations.storage.Preferences;
import com.integrail.networkers.user_interface.AfterTask;

/**
 * Created by integrailwork on 6/25/17.
 */

public class StartResendAndReReadOnConnect extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Preferences preferences = new Preferences(context);
        try {
            Thread.sleep(2000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        if(connectedToInternet(context)){
            if(!preferences.reReadRunning()){
                Intent startServiceIntent = new Intent(context, ReReadOnConnect.class);
                context.startService(startServiceIntent);
            }
            if(!preferences.resendRunning()){
                Intent startServiceIntent = new Intent(context, ResendOnConnect.class);
                context.startService(startServiceIntent);
            }
        } else {
            preferences.setReReadRunning(false);
            preferences.setResendRunning(false);
            preferences.setServiceShouldBeRunning(false);
        }
    }
    private boolean connectedToInternet(Context activity) {
        if (activity != null) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return true;
    }
}
