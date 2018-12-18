package com.integrail.networkers.user_interface.home.create_project;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.integrail.networkers.R;
import com.integrail.networkers.data_representations.project_representations.Project;
import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.data_representations.account_representations.SimpleCustomer;
import com.integrail.networkers.data_representations.validate.ErrorMessageManager;
import com.integrail.networkers.data_representations.validate.InValid;
import com.integrail.networkers.user_interface.AfterTask;

/**
 * Created by Integrail on 11/23/2016.
 */

public class CreateProject2 extends Fragment implements View.OnClickListener, AfterTask {
    private View view;
    private Project project;
    private EditText name;
    private EditText phone;
    private EditText address;
    private EditText aptNumber;
    private EditText lockBox;
    private Button button;
    private TextView[] error;
    private ErrorMessageManager manage;
    private LinearLayout linearLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.create_project_2, container, false);

        name = (EditText) view.findViewById(R.id.fullNameProject);

        phone = (EditText) view.findViewById(R.id.phoneNumberProject);

        address = (EditText) view.findViewById(R.id.addressProject);

        aptNumber = (EditText) view.findViewById(R.id.aptNumberProject);

        lockBox = (EditText) view.findViewById(R.id.lockBoxProject);

        button = (Button) view.findViewById(R.id.createProject2Button);
        button.setOnClickListener(this);

        manage = new ErrorMessageManager();
        error = new TextView[3];
        manage.initializeErrorMessages(error,getActivity());
        linearLayout = (LinearLayout) view.findViewById(R.id.createProject2Layout);

        NetworkConnection connection = new NetworkConnection(NetworkConstants.GET_USER_INFO, null, true, getActivity(), this, 0, 0);
        try {
            connection.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
    @Override
    public void update(NetworkConnection connection, int index){
        switch(index){
            case 0:
                SimpleCustomer user = new GsonBuilder().create().fromJson(connection.getReturnData(), SimpleCustomer.class);
                name.setText(user.getFirstName()+" "+user.getLastName());
                phone.setText(user.getPhone());
                break;
            case 1:
                InValid message = new GsonBuilder().create().fromJson(connection.getReturnData(), InValid.class);
                manage.removeErrorMessages(linearLayout, error);
                if (message != null) {
                    inputErrorMessage(message);
                } else {
                    CreateProject3 cP3 = new CreateProject3();
                    cP3.setProject(project);
                    getFragmentManager().beginTransaction().
                            replace(R.id.fragment, cP3, "").
                            addToBackStack("").
                            setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                            commit();
                }
                break;
        }
    }
    public void setProject(Project project){
        this.project = project;
    }
    @Override
    public void onClick(View view){
        String fullName = name.getText().toString();
        String phoneNumber = phone.getText().toString();
        String localAddress = address.getText().toString();
        String aptNum = aptNumber.getText().toString();
        String code = lockBox.getText().toString();
        project = new Project(project.getExteriorProjectFlag(),
                project.getInteriorProjectFlag(),
                fullName, phoneNumber, localAddress,aptNum, code, null, null, null);
        NetworkConnection connection = new NetworkConnection(NetworkConstants.CREATE_PROJECT, new Gson().toJson(project, Project.class), true, getActivity(), this, 1, 0);
        try {
            connection.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void inputErrorMessage(InValid valid) {
        boolean stopScroll = true;
        String badName = valid.getProjectName();
        String badPhone = valid.getProjectPhone();
        String badAddress = valid.getProjectAddress();
        final ScrollView scroll = (ScrollView) view.findViewById(R.id.createProject2Scroll);
        if (!badName.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.fullNameProject));
            error[0].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[0].setTextColor(Color.parseColor("#cc0000"));
            error[0].setGravity(Gravity.CENTER);
            error[0].setText(badName);
            linearLayout.addView(error[0], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.createProject2NameTextView).getTop());
                    }
                });
                stopScroll = false;
            }
        }
        if (!badAddress.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.addressProject));
            error[1].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[1].setTextColor(Color.parseColor("#cc0000"));
            error[1].setGravity(Gravity.CENTER);
            error[1].setText(badAddress);
            linearLayout.addView(error[1], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.createProject2AddressTextView).getTop());
                    }
                });
                stopScroll = false;
            }
        }
        if (!badPhone.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.phoneNumberProject));
            error[2].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[2].setTextColor(Color.parseColor("#cc0000"));
            error[2].setGravity(Gravity.CENTER);
            error[2].setText(badPhone);
            linearLayout.addView(error[2], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.createProject2PhoneTextView).getTop());
                    }
                });
                stopScroll = false;
            }
        }
    }
}
