package com.integrail.networkers.primary_operations.networking;

import android.content.Context;

import com.google.gson.Gson;
import com.integrail.networkers.data_representations.message_representations.Message;
import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.storage.LocalFileManager;
import com.integrail.networkers.primary_operations.storage.MessageDataBase;
import com.integrail.networkers.primary_operations.storage.Preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by integrailwork on 7/24/17.
 */

public class ResendOnConnectCore {
    private Context context;
    public ResendOnConnectCore(Context context){
        this.context = context;
    }
    public void resend() {
        Preferences preferences = new Preferences(context);
        if (!preferences.resendRunning()) {
            LocalFileManager fileManager = new LocalFileManager(context);
            NetworkConnection connection = null;
            preferences.setResendRunning(true);
            MessageDataBase messageDataBase = new MessageDataBase(context);
            Message[] unSent = messageDataBase.notSent();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < unSent.length; i++) {
                String messageId = unSent[i].getMessageId();
                long conversationId = Long.valueOf(unSent[i].getMessageId().replaceAll("-\\d+$", ""));
                unSent[i].setMessageId("" + conversationId);
                unSent[i].setMessageRead(0);
                if (unSent[i].isMms()) {
                    try {
                        FileInputStream fileInputStream = new FileInputStream(fileManager.createDirectoryAndGetWritableFile(LocalFileManager.MMS_DIRECTORY + "/" + conversationId + "/" + unSent[i].getMessage()));
                        connection = new NetworkConnection(NetworkConstants.SEND_MMS, null, true, context, null, 3, 0);
                        connection.openFile(fileInputStream);
                        connection.setRunningOnUIThread(false);
                        Matcher m = Pattern.compile("^\\d+-\\d+-").matcher(unSent[i].getMessage());
                        String fileName = null;
                        if (m.find()) {
                            fileName = unSent[i].getMessage().substring(m.group().length());
                        }
                        connection.setExistingFileName(conversationId + "-" + fileName);
                        connection.connectToURL();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    connection = new NetworkConnection(NetworkConstants.RECEIVE_MESSAGE, new Gson().toJson(unSent[i], Message.class), false, null, null, 0, 0);
                    unSent[i].setMessageId(messageId);
                    System.out.println("FROM RESEND ON CONNECT: " + new Gson().toJson(unSent[i], Message.class));
                    connection.setSession(preferences.session());
                    connection.connectToURL();
                    preferences.setSession(connection.getSession());
                }
                if (connection.getError() == null) {
                    if(unSent[i].isMms()){
                        if(connection.getReturnData()!=null){
                            File image = fileManager.createDirectoryAndGetWritableFile(LocalFileManager.MMS_DIRECTORY + "/" + conversationId + "/" + unSent[i].getMessage());
                            File file = new File("Test.jpg");
                            image.renameTo(file);
                        }
                    }
                    messageDataBase.deleteMessage(messageId);
                }
                System.out.println("ERROR STATUS: " + connection.getError());
            }
            preferences.setResendRunning(false);
            //preferences.setServiceShouldBeRunning(true);
        }
    }
}
