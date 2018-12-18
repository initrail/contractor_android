package com.integrail.networkers.user_interface.home;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.integrail.networkers.AndroidDatabaseManager;
import com.integrail.networkers.R;
import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.messaging.StartMessagingService;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Integrail on 7/21/2016.
 */

public class Home extends Fragment implements View.OnClickListener{
    private View view;
    private Button alarmToggle;
    private boolean isOn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        isOn = false;
        view = inflater.inflate(R.layout.customer_home, container, false );
        TextView tv = (TextView) view.findViewById(R.id.dataBase);
        alarmToggle = (Button) view.findViewById(R.id.toggleAlarmManager);
        alarmToggle.setOnClickListener(this);
        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent dbmanager = new Intent(getActivity(),AndroidDatabaseManager.class);
                startActivity(dbmanager);
            }
        });
        return view;
    }
    @Override
    public void onClick(View view){
        if(!isOn){
            isOn = true;
            registerAlarm();
        } else {
            isOn = false;
            unregisterAlarm();
        }
    }
    public void registerAlarm(){
        Intent restart = new Intent("integrail.networkers.stoprestartservice");
        if (PendingIntent.getBroadcast(getActivity().getApplicationContext(), StartMessagingService.REQUEST_CODE, restart, PendingIntent.FLAG_NO_CREATE) == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Registering alarm!", Toast.LENGTH_SHORT).show();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), StartMessagingService.REQUEST_CODE, restart, 0);
            int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
            AlarmManager alarmManager = (AlarmManager) getActivity().getApplicationContext().getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(alarmType, SystemClock.elapsedRealtime()+ NetworkConstants.SIX_HOURS, NetworkConstants.SIX_HOURS, pendingIntent);
        }
    }
    public void unregisterAlarm(){
        Intent restart = new Intent("integrail.networkers.stoprestartservice");
        if ((PendingIntent.getBroadcast(getActivity().getApplicationContext(), StartMessagingService.REQUEST_CODE, restart, PendingIntent.FLAG_NO_CREATE) != null)) {
            Toast.makeText(getActivity().getApplicationContext(), "Un registering alarm!", Toast.LENGTH_SHORT).show();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), StartMessagingService.REQUEST_CODE, restart, 0);
            //int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
            AlarmManager alarmManager = (AlarmManager) getActivity().getApplicationContext().getSystemService(ALARM_SERVICE);
            //alarmManager.setRepeating(alarmType, SystemClock.elapsedRealtime()+ NetworkConstants.FOUR_HOURS, NetworkConstants.FOUR_HOURS, pendingIntent);
            alarmManager.cancel(pendingIntent);
        }
    }
}
