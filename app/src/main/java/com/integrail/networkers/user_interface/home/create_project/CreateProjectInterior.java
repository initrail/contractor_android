package com.integrail.networkers.user_interface.home.create_project;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.integrail.networkers.R;
import com.integrail.networkers.data_representations.project_representations.Project;
import com.integrail.networkers.data_representations.validate.ErrorMessageManager;
import com.integrail.networkers.data_representations.validate.InValid;
import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.user_interface.AfterTask;

/**
 * Created by Initrail on 11/17/2016.
 */

public class CreateProjectInterior extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, AfterTask {
    private View view;
    private SwitchCompat switch1;
    private SwitchCompat switch2;
    private SwitchCompat switch3;
    private SwitchCompat switch4;
    private SwitchCompat switch5;
    private SwitchCompat switch6;
    private SwitchCompat switch7;
    private SwitchCompat switch8;
    private SwitchCompat switch9;
    private SwitchCompat switch10;
    private SwitchCompat switch11;
    private SwitchCompat switch12;
    private SwitchCompat switch13;
    private SwitchCompat switch14;
    private SwitchCompat switch15;
    private SwitchCompat switch16;
    private SwitchCompat switch17;
    private Button button;
    private SwitchCompat[] switchCompats = null;
    private TextView[] error;
    private ErrorMessageManager manage;
    private LinearLayout linearLayout;
    private Project project;
    @Override
    public void update(NetworkConnection connection, int index){
        InValid message = new GsonBuilder().create().fromJson(connection.getReturnData(), InValid.class);
        manage.removeErrorMessages(linearLayout, error);
        if (message != null) {
            inputErrorMessage5(message);
        } else {
            CreateProject2 c = new CreateProject2();
            c.setProject(project);
            getFragmentManager().beginTransaction().
                    replace(R.id.fragment, c, "").
                    addToBackStack("").
                    setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                    commit();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.create_project_interior, container, false);
        switch1 = (SwitchCompat) view.findViewById(R.id.switchip1);
        switch2 = (SwitchCompat) view.findViewById(R.id.switchip2);
        switch3 = (SwitchCompat) view.findViewById(R.id.switchip3);
        switch4 = (SwitchCompat) view.findViewById(R.id.switchip4);
        switch5 = (SwitchCompat) view.findViewById(R.id.switchip5);
        switch6 = (SwitchCompat) view.findViewById(R.id.switchip6);
        switch7 = (SwitchCompat) view.findViewById(R.id.switchip7);
        switch8 = (SwitchCompat) view.findViewById(R.id.switchip8);
        switch9 = (SwitchCompat) view.findViewById(R.id.switchip9);
        switch10 = (SwitchCompat) view.findViewById(R.id.switchip10);
        switch11 = (SwitchCompat) view.findViewById(R.id.switchip11);
        switch12 = (SwitchCompat) view.findViewById(R.id.switchip12);
        switch13 = (SwitchCompat) view.findViewById(R.id.switchip13);
        switch14 = (SwitchCompat) view.findViewById(R.id.switchip14);
        switch15 = (SwitchCompat) view.findViewById(R.id.switchip15);
        switch16 = (SwitchCompat) view.findViewById(R.id.switchip16);
        switch17 = (SwitchCompat) view.findViewById(R.id.switchip17);
        switchCompats = new SwitchCompat[]{
                switch1, switch2, switch3, switch4, switch5, switch6, switch7, switch8, switch9,
                switch10, switch11, switch12, switch13, switch14, switch15, switch16, switch17
        };
        button = (Button) view.findViewById(R.id.interiorProject);
        button.setOnClickListener(this);
        for(int i = 0; i<switchCompats.length; i++){
            switchCompats[i].setOnCheckedChangeListener(this);
        }
        manage = new ErrorMessageManager();
        error = new TextView[1];
        manage.initializeErrorMessages(error,getActivity());
        linearLayout = (LinearLayout) view.findViewById(R.id.createProjectIntLayout);
        return view;
    }
    @Override
    public void onClick(View view){
        project = new Project(0, setProjectFlag(),null, null, null, null, null, null, null, null);
        NetworkConnection connection = new NetworkConnection(NetworkConstants.CREATE_PROJECT, new Gson().toJson(project, Project.class), true, getActivity(), this, 0, 0);
        try {
            connection.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked){
        if(checked){
            for(int i = 0; i<switchCompats.length; i++) {
                if (button != switchCompats[i]) {
                    switchCompats[i].setOnCheckedChangeListener(null);
                    switchCompats[i].setChecked(false);
                    switchCompats[i].setOnCheckedChangeListener(this);
                }
            }
        } else {
            for(int i = 0; i<switchCompats.length;i++) {
                if(button == switchCompats[i])
                    switchCompats[i].setChecked(true);
            }
        }
    }
    public int setProjectFlag(){
        int projectFlag = 0;
        for (int i = 0; i < switchCompats.length; i++) {
            if (switchCompats[i].isChecked()) {
                projectFlag |= (int) Math.pow(2, i);
            }
        }
        return projectFlag;
    }
    public void inputErrorMessage5(InValid valid){
        boolean stopScroll = true;
        String emptyProjectFlag = valid.getInValidProjectFlag();
        final ScrollView scroll = (ScrollView) view.findViewById(R.id.interiorScroll);
        if (!emptyProjectFlag.equals("")) {
            final int find = linearLayout.indexOfChild(view.findViewById(R.id.wallsInt));
            error[0].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[0].setTextColor(Color.parseColor("#cc0000"));
            error[0].setGravity(Gravity.CENTER);
            error[0].setText(emptyProjectFlag);
            linearLayout.addView(error[0], find);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    scroll.smoothScrollTo(0, error[0].getTop());
                }
            });
        }
    }
}
