package com.integrail.networkers.primary_operations.messaging;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.storage.Preferences;

import static android.content.Context.ALARM_SERVICE;

public class ToggleAlarm extends BroadcastReceiver {
    public static final String STOP_ALARM = "com.integrail.networkers.stopalarm";
    public static final String SET_ALARM = "com.integrail.networkers.setalarm";
    @Override
    public void onReceive(Context context, Intent intent){
        //Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show();
        Preferences preferences = new Preferences(context);
        if(preferences.signedIn()&&intent.getAction().equals(SET_ALARM))
            registerAlarm(context);
        else if(preferences.signedIn()&&intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
            registerAlarm(context);
        else if(preferences.signedIn()&&intent.getAction().equals(STOP_ALARM))
            unregisterAlarm(context);
    }

    public void registerAlarm(Context context){
        Intent restart = new Intent(StartMessagingService.STOP_RESTART);
        //Toast.makeText(context, "Registering alarm!", Toast.LENGTH_SHORT).show();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, StartMessagingService.REQUEST_CODE, restart, 0);
        int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(alarmType, SystemClock.elapsedRealtime()+ NetworkConstants.SIX_HOURS, NetworkConstants.SIX_HOURS, pendingIntent);
    }
    public void unregisterAlarm(Context context){
        Intent restart = new Intent(StartMessagingService.STOP_RESTART);
        //Toast.makeText(context, "Un registering alarm!", Toast.LENGTH_SHORT).show();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, StartMessagingService.REQUEST_CODE, restart, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
