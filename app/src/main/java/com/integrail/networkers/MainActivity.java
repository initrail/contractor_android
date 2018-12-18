package com.integrail.networkers;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.Toast;

import com.google.gson.Gson;
import com.integrail.networkers.data_representations.message_representations.Message;
import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.messaging.MessagingService;
import com.integrail.networkers.primary_operations.messaging.StartMessagingService;
import com.integrail.networkers.primary_operations.messaging.ToggleAlarm;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.primary_operations.storage.DataBaseManager;
import com.integrail.networkers.primary_operations.storage.MessageDataBase;
import com.integrail.networkers.primary_operations.storage.Preferences;
import com.integrail.networkers.user_interface.home.HomeDrawer;
import com.integrail.networkers.user_interface.home.conversation_list.ConversationList;
import com.integrail.networkers.user_interface.home.conversation_list.messagethread.MessageList;
import com.integrail.networkers.user_interface.signin.SignInFragment;

/**
 * Created by Integrail on 7/20/2016.
 */

public class MainActivity extends AppCompatActivity implements MessagingService.OnServiceListener{
    public static final String CONVERSATION_ID = "conversationId";
    public static final String MESSAGE_TO = "messageTo";
    public static int INVALIDATE = 0;
    private boolean mHasCameraPermissions;
    private DataBaseManager db;
    private MessagingService messagingService;
    private boolean bound = false;
    private Intent startServiceIntent;
    private boolean mHasStoragePermissions;
    private static final int PERMISSION_REQUEST_CODE = 42;
    @Override
    public void onDestroy(){
        super.onDestroy();
        Preferences preferences = new Preferences(getApplicationContext());
        if(preferences.signedIn()) {
            sendBroadcast(new Intent(ToggleAlarm.SET_ALARM));
        }
    }
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            sendBroadcast(new Intent(ToggleAlarm.STOP_ALARM));
            MessagingService.MyBinder myBinder = (MessagingService.MyBinder) iBinder;
            messagingService = myBinder.getService();
            messagingService.setOnServiceListener(MainActivity.this);
            bound = true;
            FragmentManager fM = getFragmentManager();
            FragmentTransaction fT = fM.beginTransaction();
            HomeDrawer home;
            MessageList list;
            Intent intent = getIntent();
            long conversationId = intent.getLongExtra(CONVERSATION_ID, 0);
            String messageTo = intent.getStringExtra(MESSAGE_TO);
            if(conversationId!=0&&messageTo!=null) {
                list = new MessageList();
                Bundle args = new Bundle();
                args.putLong(CONVERSATION_ID,conversationId);
                args.putLong(MESSAGE_TO, Long.valueOf(messageTo));
                args.putString("otherName", intent.getStringExtra("otherName"));
                intent.removeExtra(CONVERSATION_ID);
                intent.removeExtra(MESSAGE_TO);
                intent.removeExtra("otherName");
                list.setArguments(args);
                fT.replace(R.id.fragment, list);
                fT.commit();
            } else {
                home = new HomeDrawer();
                home.setMessagingService(messagingService);
                fT.replace(R.id.fragment, home);
                fT.commit();
            }
            if(!messagingService.isRunning()) {
                getApplicationContext().startService(startServiceIntent);
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; ++i) {
            if (permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)||permissions[i].equals(Manifest.permission.CAMERA)) {
                mHasStoragePermissions = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                mHasCameraPermissions = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
            if(mHasStoragePermissions){
                if(getFragmentManager().findFragmentById(R.id.fragment) instanceof AfterTaskImplementation){
                    ((AfterTaskImplementation) getFragmentManager().findFragmentById(R.id.fragment)).openImage();
                } else if(getFragmentManager().findFragmentById(R.id.homeFragment) instanceof AfterTaskImplementation){
                    ((AfterTaskImplementation)getFragmentManager().findFragmentById(R.id.homeFragment)).openImage();
                }
            }
        }
    }
    public boolean hasStoragePermissions(){
        return mHasStoragePermissions;
    }
    public void requestStoragePermissions() {
        if (mHasStoragePermissions) {
            return;
        }
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, PERMISSION_REQUEST_CODE);
    }
    public void requestCameraPermissions() {
        if (mHasCameraPermissions) {
            return;
        }
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, PERMISSION_REQUEST_CODE);
    }
    public void unBind(){
        getApplicationContext().unbindService(mServiceConnection);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        Preferences preferences = new Preferences(getApplicationContext());
        if(preferences.firstRun()){
            db = new DataBaseManager(this);
            db.getWritableDatabase();
            requestStoragePermissions();
            preferences.notFirstRun();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        FragmentManager fM = getFragmentManager();
        FragmentTransaction fT = fM.beginTransaction();
        if(!preferences.signedIn()||preferences.session().equals(Preferences.DEFAULT_SESSION)){
            preferences.setResendRunning(false);
            Fragment cA = new SignInFragment();
            fT.replace(R.id.fragment, cA);
            fT.commit();
        } else {
            bindService();
        }
        mHasCameraPermissions = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        mHasStoragePermissions = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onBackPressed(){
        Fragment f = getFragmentManager().findFragmentById(R.id.fragment);
        if (f instanceof HomeDrawer){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.my_home_drawer);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        } else if(f instanceof MessageList) {
            if(getFragmentManager().getBackStackEntryCount()==0) {
                HomeDrawer home = new HomeDrawer();
                home.setLoadConversationList(true);
                getFragmentManager().beginTransaction().replace(R.id.fragment, home).commit();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
    public void bindService(){
        startServiceIntent = new Intent(getApplicationContext(), MessagingService.class);
        getApplicationContext().bindService(startServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }
    public static float convertPixelsToDp(float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }
    public int getWidth(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
    @Override
    public void onDataReceived(final Message m){
        if(getFragmentManager().findFragmentById(R.id.fragment)instanceof MessageList){
            final long id = ((MessageList) getFragmentManager().findFragmentById(R.id.fragment)).getConvId();
            final long conversationId = Long.valueOf(m.getMessageId().replaceAll("-\\d+$", ""));
            Preferences preferences = new Preferences(getApplicationContext());
            if(m.getMessageFrom() != preferences.userId() && conversationId == id) {
                NetworkConnection connection = new NetworkConnection(NetworkConstants.SET_MESSAGE_READ, new Gson().toJson(new String[]{m.getMessageId()}, String[].class), false, this, null, 0, 0);
                connection.setRunningOnUIThread(false);
                try {
                    connection.execute();
                    new MessageDataBase(this).updateMsgRead(conversationId, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String session = preferences.session().replace(Preferences.DEFAULT_SESSION, "");
            if(!m.getjSessionId().equals(session)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (conversationId==id) {
                            ((MessageList) getFragmentManager().findFragmentById(R.id.fragment)).updateMessageThread(m);
                            new MessageDataBase(getApplicationContext()).updateIndividualMsgRead(m.getMessageId());
                        }
                    }
                });
            }
        }
    }
    public boolean hasCameraPermissions(){
        return mHasCameraPermissions;
    }
    @Override
    public void onConversationsReceived(){
         if(getFragmentManager().findFragmentById(R.id.homeFragment) instanceof ConversationList) {
             NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
             manager.cancelAll();
             runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     ((ConversationList) getFragmentManager().findFragmentById(R.id.homeFragment)).updateConversationPage();
                 }
             });
         }
    }
}
