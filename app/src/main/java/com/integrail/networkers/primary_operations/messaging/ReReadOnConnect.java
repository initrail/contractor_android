package com.integrail.networkers.primary_operations.messaging;

import android.app.IntentService;
import android.content.Intent;

import com.google.gson.Gson;

import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.primary_operations.storage.MessageDataBase;
import com.integrail.networkers.primary_operations.storage.Preferences;

/**
 * Created by integrailwork on 6/27/17.
 */

public class ReReadOnConnect extends IntentService {
    public ReReadOnConnect(){
        super("");
    }
    @Override
    public void onHandleIntent(Intent intent){
        Preferences preferences = new Preferences(getApplicationContext());
        preferences.setReReadRunning(true);
        MessageDataBase messageDataBase = new MessageDataBase(getApplicationContext());
        String[] smsIds = messageDataBase.readButNotUpdatedMessages();
        try{
            Thread.sleep(2000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        NetworkConnection connection = new NetworkConnection(NetworkConstants.SET_MESSAGE_READ, new Gson().toJson(smsIds, String[].class), false, getApplicationContext(), null, 0, 0);
        connection.setSession(preferences.session());
        connection.connectToURL();
        preferences.setSession(connection.getSession());
        messageDataBase.updateToRead();
        preferences.setReReadRunning(false);
    }
}
