package com.integrail.networkers.primary_operations.messaging;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.google.gson.Gson;
import com.integrail.networkers.primary_operations.storage.RecipientsDataBase;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.integrail.networkers.MainActivity;
import com.integrail.networkers.R;
import com.integrail.networkers.data_representations.message_representations.Message;
import com.integrail.networkers.primary_operations.storage.ConversationDataBase;
import com.integrail.networkers.primary_operations.storage.LocalFileManager;
import com.integrail.networkers.primary_operations.storage.MessageDataBase;
import com.integrail.networkers.primary_operations.storage.Preferences;

/**
 * Created by Integrail on 7/14/2016.
 */

public class PushNotification extends IntentService {
    private long user;
    public PushNotification(){
        super("");
    }
    @Override
    public void onHandleIntent(Intent intent){
        user = new Preferences(getApplicationContext()).userId();
        String message = intent.getStringExtra("Message");
        if(message!=null) {
            System.out.println("PushNotification is running...");
            ArrayList<Long> conversationIds = new ArrayList<>();
            ArrayList<String> senders = new ArrayList<>();
            Message[] msg = new Gson().fromJson(message, Message[].class);
            for (int i = 0; i < msg.length; i++) {
                conversationIds.add(Long.valueOf(msg[i].getMessageId().replaceAll("-\\d+$", "")));
                if(msg[i].getMessageFrom() != user)
                    senders.add(""+msg[i].getMessageFrom());
            }
            Set<Long> hs = new HashSet<>();
            hs.addAll(conversationIds);
            conversationIds.clear();
            conversationIds.addAll(hs);
            Set<String> names = new HashSet<>();
            names.addAll(senders);
            senders.clear();
            senders.addAll(names);
            System.out.println("Getting ready to create notification...");
            System.out.println("conversationIds: "+conversationIds);
            System.out.println("senders: "+senders);
            RecipientsDataBase cDB= new RecipientsDataBase(getApplicationContext());
            ArrayList<String> userIds = new ArrayList<>();
            for(int i = 0; i<senders.size(); i++){
                userIds.add(senders.get(i));
                senders.set(i, cDB.getRealName(Long.valueOf(senders.get(i))));
            }
            for(int i = 0; i<conversationIds.size(); i++){
                if(senders.size()>0) {
                    String messages = new MessageDataBase(getApplicationContext()).getUnreadMessagesFromThread(conversationIds.get(i), senders.get(i));
                    if (!messages.equals("") && !senders.get(i).equals(""))
                        showNotification(conversationIds.get(i), messages, senders.get(i), userIds.get(i));
                }
            }
        }
    }
    public void showNotification(long conversationId, String message, String sender, String email){
        RemoteInput remoteInput = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            remoteInput = new RemoteInput.Builder(SendMessageNoUI.KEY_INPUT).setLabel("Reply").build();
        Intent rIntent = new Intent(getApplicationContext(), SendMessageNoUI.class);
        rIntent.putExtra(MainActivity.CONVERSATION_ID, conversationId);
        rIntent.putExtra(MainActivity.MESSAGE_TO, email);
        NotificationCompat.Action action = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), (int)conversationId, rIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            action = new NotificationCompat.Action.Builder(R.drawable.send, "Reply", pIntent).addRemoteInput(remoteInput).build();
        }
        File image = new LocalFileManager(getApplicationContext()).createDirectoryAndGetWritableFile(LocalFileManager.USER_IMAGE_DIRECTORY+"/"+email+".jpg");
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(sender);
        builder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        builder.setContentText(message);
        if(action!=null)
            builder.addAction(action);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        if(image.exists()) {
            try {
                builder.setLargeIcon(Picasso.with(getApplicationContext()).load(image).get());
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        else
            builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.blank));
        builder.setPriority(Notification.PRIORITY_HIGH);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(MainActivity.CONVERSATION_ID, conversationId);
        intent.putExtra(MainActivity.MESSAGE_TO, email);
        intent.putExtra("otherName", sender);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent i = stackBuilder.getPendingIntent((int)conversationId, PendingIntent.FLAG_UPDATE_CURRENT);;
        builder.setContentIntent(i);
        NotificationManager nM = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setAutoCancel(true);
        nM.notify((int)conversationId, builder.build());
        System.out.println("Notification should be ready...");
    }
}
