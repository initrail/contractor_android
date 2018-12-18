package com.integrail.networkers.primary_operations.messaging;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.integrail.networkers.MainActivity;
import com.integrail.networkers.R;
import com.integrail.networkers.data_representations.message_representations.Message;
import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.primary_operations.storage.ConversationDataBase;
import com.integrail.networkers.primary_operations.storage.MessageDataBase;
import com.integrail.networkers.data_representations.message_representations.LatestConversationData;
import com.integrail.networkers.primary_operations.storage.Preferences;
import com.integrail.networkers.primary_operations.storage.RecipientsDataBase;
import com.integrail.networkers.user_interface.home.HomeDrawer;
import com.integrail.networkers.user_interface.home.conversation_list.ConversationList;

/**
 * Created by Integrail on 7/15/2016.
 */

public class MessagingService extends Service{
    private static final String CHECK_MESSAGES = "checkMessagesTable";
    private static final String UPDATE_READ = "updateRead";
    private static final String UPDATE_PIC= "someoneUpdatedPic";
    private static final String STOP_SERVLET = "killingServlet";
    private static final String TIME_OUT = "servletTimedOut";
    public static final String PUSHNOTIFICATION = "integrail.theconnection.pushnotification";
    public static final String DOWNLOAD_IMAGE = "integrail.theconnection.downloadimage";
    private Thread receiveMessage;
    private MessageDataBase db;
    private ConversationDataBase cDB;
    private LatestConversationData[] info;
    private IBinder mBinder;
    private boolean isRunning;
    private StartResendAndReReadOnConnect resendAndReReadOnConnect;
    private OnServiceListener listener = null;
    private boolean lostConnection;
    public void setOnServiceListener(OnServiceListener l){
        listener = l;
    }
    @Override
    public void onCreate(){
        resendAndReReadOnConnect = new StartResendAndReReadOnConnect();
        mBinder = new MyBinder();
        db = new MessageDataBase(getApplicationContext());
        cDB = new ConversationDataBase(getApplicationContext());
    }
    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        lostConnection = false;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            getApplicationContext().registerReceiver(resendAndReReadOnConnect, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        Preferences preferences = new Preferences(getApplicationContext());
        receiveMessage = new Thread(){
                @Override
                public void run(){
                    waitForMessage();
                }
            };
        if(preferences.signedIn()) {
            receiveMessage.start();
            isRunning = true;
            //preferences.setServiceIsRunning(true);
        }
        return Service.START_STICKY;
    }
    @Override
    public void onDestroy() {
        System.out.println("onDestroy() WAS CALLED!!!");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            getApplicationContext().unregisterReceiver(resendAndReReadOnConnect);
        }
        Preferences preferences = new Preferences(getApplicationContext());
        super.onDestroy();
    }
    public void downloadImage(long email){
        Intent intent = new Intent(getApplicationContext(), DownloadImage.class);
        intent.putExtra(Preferences.USER_ID, email);
        getApplicationContext().startService(intent);
    }
    private void pushMessage(String message){
        Intent intent = new Intent(getApplicationContext(), PushNotification.class);
        intent.putExtra("Message", message);
        getApplicationContext().startService(intent);
    }
    public void killServlets(){
        Preferences preferences = new Preferences(getApplicationContext());
        NetworkConnection connection = new NetworkConnection(NetworkConstants.RESET_CHECK_MESSAGES_TABLE, null, false, null, null, 0, 0);
        connection.setSession(preferences.session());
        connection.connectToURL();
    }
    public void waitForMessage() {
            killServlets();
            Preferences preferences = new Preferences(getApplicationContext());
            while (!preferences.session().equals(Preferences.DEFAULT_SESSION_FROM_SERVER) && preferences.signedIn()&&(preferences.allowMessageNotifications()||listener!=null)) {
                if(lostConnection) {
                    killServlets();
                    lostConnection = false;
                }
                System.out.println("Top of messaging");
                preferences = new Preferences(getApplicationContext());
                if(!(preferences.resendRunning()||preferences.reReadRunning())&&connectedToInternet(getApplicationContext())&&!preferences.serviceShouldBeRunning()) {
                    preferences.setServiceShouldBeRunning(true);
                    System.out.println("MESSAGING SERVICE\nChanging the service so that it should run...");
                } else {
                    System.out.println("MESSAGING SERVICE\nCan't change settings...");
                }
                if (preferences.serviceShouldBeRunning()) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    System.out.println("Running messaging");
                    NetworkConnection connection = new NetworkConnection(NetworkConstants.CHECK_MESSAGES_TABLE, null, true, null, null, 0, 0);
                    connection.setSession(preferences.session());
                    connection.connectToURL();
                    if(connection.getError()!=null)
                        lostConnection = true;
                    if (connection.getReturnData() != null) {
                        if (connection.getReturnData().equals(CHECK_MESSAGES)) {
                            preferences = new Preferences(getApplicationContext());
                            preferences.setSession(connection.getSession());
                            info = cDB.getData();

                            connection = new NetworkConnection(NetworkConstants.GET_NEW_CONVERSATIONS, new Gson().toJson(info, LatestConversationData[].class), true, null, null, 0, 0);
                            connection.setSession(preferences.session());
                            connection.connectToURL();
                            String convs = connection.getReturnData();
                            preferences = new Preferences(getApplicationContext());
                            preferences.setSession(connection.getSession());

                            info = new Gson().fromJson(convs, LatestConversationData[].class);
                            if (info != null) {
                                connection = new NetworkConnection(NetworkConstants.SEND_MESSAGE, convs, true, null, null, 0, 0);
                                connection.setSession(preferences.session());
                                connection.connectToURL();
                                String messages = connection.getReturnData();
                                preferences = new Preferences(getApplicationContext());
                                preferences.setSession(connection.getSession());

                                insertMessagesIntoDB(messages);
                                new RecipientsDataBase(getApplicationContext()).insertConversations(info);
                                preferences = new Preferences(getApplicationContext());
                                if(preferences.allowMessageNotifications()) {
                                    if (listener != null) {
                                        boolean instance = ((MainActivity) listener).getFragmentManager().findFragmentById(R.id.homeFragment) instanceof ConversationList;
                                        boolean fragment = ((MainActivity) listener).getFragmentManager().findFragmentById(R.id.fragment) instanceof HomeDrawer;
                                        if (!(instance && fragment))
                                            pushMessage(messages);
                                    } else
                                        pushMessage(messages);
                                }
                                updateConversationPage();
                            } else {
                                connection = new NetworkConnection(NetworkConstants.CORRECT_CHECK_MESSAGES, null, false, null, null, 0, 0);
                                connection.setSession(preferences.session());
                                connection.connectToURL();
                                preferences = new Preferences(getApplicationContext());
                                preferences.setSession(connection.getSession());
                            }
                        } else if (connection.getReturnData().equals(UPDATE_READ)) {
                            String[] unRead = db.getUnreadMessages();
                            connection = new NetworkConnection(NetworkConstants.DETERMINE_READ_MESSAGES, new Gson().toJson(unRead, String[].class), true, null, null, 0,0);
                            connection.setSession(preferences.session());
                            connection.connectToURL();
                            if (!connection.getReturnData().equals("doNothing")) {
                                String[] read = new Gson().fromJson(connection.getReturnData(), String[].class);
                                if (read != null) {
                                    if (read.length > 0) {
                                        ArrayList<String> conversationIds = new ArrayList<>();
                                        for (int i = 0; i < read.length; i++) {
                                            conversationIds.add(read[i].replaceAll("/\\d+$", ""));
                                        }
                                        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                        Set<String> hs = new HashSet<>();
                                        hs.addAll(conversationIds);
                                        conversationIds.clear();
                                        conversationIds.addAll(hs);
                                        for (int i = 0; i < conversationIds.size(); i++) {
                                            manager.cancel(conversationIds.get(i).hashCode());
                                        }
                                        for (int i = 0; i < read.length; i++) {
                                            db.updateIndividualMsgRead(read[i]);
                                        }
                                        updateConversationPage();
                                    }
                                }
                            }
                            preferences = new Preferences(getApplicationContext());
                            preferences.setSession(connection.getSession());
                        } else if (connection.getReturnData().equals(UPDATE_PIC)) {
                            connection = new NetworkConnection(NetworkConstants.WHICH_USER_UPDATED_PIC, null, true, null, null, 0, 0);
                            connection.setSession(preferences.session());
                            connection.connectToURL();
                            Long[] updated = new Gson().fromJson(connection.getReturnData(), Long[].class);
                            if (updated != null) {
                                for (int i = 0; i < updated.length; i++) {
                                    downloadImage(updated[i]);
                                }
                            }
                            preferences = new Preferences(getApplicationContext());
                            preferences.setSession(connection.getSession());
                        } else if(connection.getReturnData().equals(STOP_SERVLET)){
                            break;
                        }
                    }
                    /*preferences = new Preferences(getApplicationContext());
                    if (preferences.session() == null) {
                        stopSelf();
                        break;
                    }*/
                }
            }
        isRunning = false;
            lostConnection = false;
        try {
            receiveMessage.join();
        } catch(InterruptedException e){
            e.printStackTrace();
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
    public void updateConversationPage(){
        cDB.insertConversations(info);
        if(listener!=null)
            listener.onConversationsReceived();
    }
    public void insertMessagesIntoDB(Message m){
        db.insertMessage(m, true);
        if(listener!=null){
            listener.onDataReceived(m);
        }
    }
    public void insertMessagesIntoDB(String message){
        Message[] msgs = new Gson().fromJson(message, Message[].class);
        db.insertMessages(msgs, true);
        if(listener!=null){
            if(msgs!=null) {
                for (int i = 0; i < msgs.length; i++) {
                    listener.onDataReceived(msgs[i]);
                }
            }
        }
    }
    public class MyBinder extends Binder{
        public MessagingService getService(){
            return MessagingService.this;
        }
    }
    public interface OnServiceListener{
        void onDataReceived(Message m);
        void onConversationsReceived();
    }
    public boolean isRunning(){
        return isRunning;
    }
}
