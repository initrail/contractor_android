package com.integrail.networkers.primary_operations.messaging;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.primary_operations.storage.Preferences;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Integrail on 7/11/2016.
 */
public class StartMessagingService extends BroadcastReceiver {
    private Intent startServiceIntent;
    public static final String STOP_RESTART = "integrail.networkers.stoprestartservice";
    public static final int REQUEST_CODE = 12;
    public void onReceive(Context context, Intent intent){
        Toast.makeText(context, intent.getAction(), Toast.LENGTH_LONG).show();
        Preferences preferences = new Preferences(context.getApplicationContext());
        startServiceIntent = new Intent(context, MessagingService.class);
        if(preferences.signedIn()) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                context.startService(startServiceIntent);
            } else if (intent.getAction().equals(STOP_RESTART)) {
                if (serviceIsRunning(context, MessagingService.class))
                    context.stopService(new Intent(context, MessagingService.class));
                context.startService(startServiceIntent);
            }
        }
    }
    private boolean serviceIsRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
