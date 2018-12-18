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

public class CreateProjectExterior extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, AfterTask{
    private View view;
    private SwitchCompat switch18;
    private SwitchCompat switch19;
    private SwitchCompat switch20;
    private SwitchCompat switch21;
    private SwitchCompat switch22;
    private SwitchCompat switch23;
    private SwitchCompat switch24;
    private SwitchCompat[] switchCompats;
    private Button button;
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
        view = inflater.inflate(R.layout.create_project_exterior, container, false);
        switch18 = (SwitchCompat) view.findViewById(R.id.switchep18);
        switch19 = (SwitchCompat) view.findViewById(R.id.switchep19);
        switch20 = (SwitchCompat) view.findViewById(R.id.switchep20);
        switch21 = (SwitchCompat) view.findViewById(R.id.switchep21);
        switch22 = (SwitchCompat) view.findViewById(R.id.switchep22);
        switch23 = (SwitchCompat) view.findViewById(R.id.switchep23);
        switch24 = (SwitchCompat) view.findViewById(R.id.switchep24);
        switchCompats = new SwitchCompat[]{
                switch18, switch19, switch20, switch21, switch22, switch23, switch24
        };
        button = (Button) view.findViewById(R.id.exteriorProject);
        button.setOnClickListener(this);
        for(int i = 0; i<switchCompats.length;i++){
            switchCompats[i].setOnCheckedChangeListener(this);
        }
        manage = new ErrorMessageManager();
        error = new TextView[1];
        manage.initializeErrorMessages(error,getActivity());
        linearLayout = (LinearLayout) view.findViewById(R.id.createProjectExt);
        return view;
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
    @Override
    public void onClick(View view){
        project = new Project(setProjectFlag(), 0, null,null, null, null, null, null, null, null);
        NetworkConnection connection = new NetworkConnection(NetworkConstants.CREATE_PROJECT, new Gson().toJson(project, Project.class), true, getActivity(), this, 0, 0);
        try {
            connection.execute();
        } catch (Exception e) {
            e.printStackTrace();
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
        final ScrollView scroll = (ScrollView) view.findViewById(R.id.exteriorScroll);
        if (!emptyProjectFlag.equals("")) {
            final int find = linearLayout.indexOfChild(view.findViewById(R.id.wallsExt));
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