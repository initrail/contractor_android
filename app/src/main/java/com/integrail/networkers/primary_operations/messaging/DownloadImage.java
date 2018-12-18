package com.integrail.networkers.primary_operations.messaging;

import android.app.IntentService;
import android.content.Intent;

import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.primary_operations.storage.ConversationDataBase;
import com.integrail.networkers.primary_operations.storage.LocalFileManager;
import com.integrail.networkers.primary_operations.storage.MessageDataBase;
import com.integrail.networkers.primary_operations.storage.Preferences;

/**
 * Created by integrailwork on 6/24/17.
 */

public class DownloadImage extends IntentService {
    public DownloadImage(){
        super("");
    }
    @Override
    public void onHandleIntent(Intent intent){
        long email = intent.getLongExtra(Preferences.USER_ID, 0);
        Long conversationId = intent.getLongExtra(ConversationDataBase.COLUMN_1, 0);
        String message = intent.getStringExtra(MessageDataBase.COLUMN_5);
        String messages = intent.getStringExtra("Messages");
        NetworkConnection connection = null;
        if(conversationId!=0&&message!=null) {

            connection = new NetworkConnection(NetworkConstants.GET_MMS, message, true, getApplicationContext(), null, 0,0);
            connection.setDirectory(LocalFileManager.MMS_DIRECTORY+"/"+conversationId+"/");
        }
        else if(email>0) {
            connection = new NetworkConnection(NetworkConstants.GET_USER_IMAGE, ""+email, true, getApplicationContext(), null, 0, 0);
            connection.setDirectory(LocalFileManager.USER_IMAGE_DIRECTORY);
        }
        connection.setExpectingImage(true);
        connection.setRunningOnUIThread(false);
        connection.connectToURL();
    }
}
