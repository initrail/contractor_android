package com.integrail.networkers.primary_operations.messaging;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;

import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.integrail.networkers.MainActivity;
import com.integrail.networkers.data_representations.message_representations.Message;
import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.primary_operations.storage.ConversationDataBase;
import com.integrail.networkers.primary_operations.storage.MessageDataBase;
import com.integrail.networkers.primary_operations.storage.Preferences;

/**
 * Created by integrailwork on 7/4/17.
 */

public class SendMessageNoUI extends IntentService {
    public static final String KEY_INPUT = "key_input";
    public SendMessageNoUI(){
        super("");
    }
    @Override
    public void onHandleIntent(Intent intent){
        System.out.println("SendMessageNoUI was created.");
        long conversationId = intent.getLongExtra(MainActivity.CONVERSATION_ID, 0);
        String messageTo = intent.getStringExtra(MainActivity.MESSAGE_TO);
        long messageFrom = new Preferences(getApplicationContext()).userId();
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if(remoteInput!=null){
            String msg = remoteInput.getCharSequence(KEY_INPUT).toString();
            //Matcher m = Pattern.compile("^\\s+$").matcher(msg);
            /*if (m.find()) {
                msg = "";
            }*/
            if (!msg.equals("")) {
                ConversationDataBase cDB = new ConversationDataBase(getApplicationContext());
                MessageDataBase mDB = new MessageDataBase(getApplicationContext());
                String[] smsIds = mDB.setReadMessages(conversationId);
                if (smsIds.length > 0) {
                    NetworkConnection connection = new NetworkConnection(NetworkConstants.SET_MESSAGE_READ, new Gson().toJson(smsIds, String[].class), false, getApplicationContext(), null, 0, 0);
                    connection.setRunningOnUIThread(false);
                    connection.connectToURL();
                    if (connection.getError() != null) {
                        mDB.updateMsgRead(conversationId, 2);
                    } else {
                        mDB.updateMsgRead(conversationId, 1);
                    }
                }
                long latest = cDB.getLatestId(conversationId) + 1;
                Message sms = new Message(""+conversationId, latest, messageFrom, msg, 0, System.currentTimeMillis(), 0);
                NetworkConnection connection = new NetworkConnection(NetworkConstants.RECEIVE_MESSAGE, new Gson().toJson(sms, Message.class), false, getApplicationContext(), null, 0,0);
                connection.setRunningOnUIThread(false);
                connection.connectToURL();
                if (connection.getError() != null) {
                    latest = cDB.getLatestId(conversationId) + 1;
                    cDB.updateCount(latest, conversationId);
                    sms.setMessageId(conversationId + "-" + latest);
                    sms.setMessageRead(1);
                    sms.setTimeSent(ConversationDataBase.INFINITE_TIME);
                    mDB.insertMessage(sms, false);
                }
                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel((int)conversationId);
            }
        }
    }
}
