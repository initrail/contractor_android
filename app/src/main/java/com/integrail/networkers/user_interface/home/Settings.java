package com.integrail.networkers.user_interface.home;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.integrail.networkers.R;
import com.integrail.networkers.primary_operations.storage.Preferences;

/**
 * Created by Integrail on 11/26/2016.
 */

public class Settings extends Fragment implements CompoundButton.OnCheckedChangeListener{
    private View view;
    private SwitchCompat messageNotifications;
    private Preferences preferences;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings, container, false);
        messageNotifications = (SwitchCompat) view.findViewById(R.id.messageNotifications);
        messageNotifications.setOnCheckedChangeListener(this);
        preferences = new Preferences(getActivity());
        messageNotifications.setChecked(preferences.allowMessageNotifications());
        return view;
    }
    @Override
    public void onCheckedChanged(CompoundButton sw, boolean checked){
        switch (sw.getId()){
            case R.id.messageNotifications:
                preferences.setAllowMessageNotifications(checked);
                break;
        }
    }
}
