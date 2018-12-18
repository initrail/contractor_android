package com.integrail.networkers.user_interface.home.create_project;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.integrail.networkers.data_representations.validate.ErrorMessageManager;
import com.integrail.networkers.data_representations.validate.InValid;
import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.user_interface.AfterTask;

/**
 * Created by Integrail on 11/24/2016.
 */

public class CreateProject3 extends Fragment implements View.OnClickListener, AfterTask {
    private View view;
    private Project project;
    private String[] budget;
    private String[] time;
    private Button button;
    private LinearLayout layout;
    private LinearLayout layout2;
    private TextView price;
    private TextView date;
    private EditText text;
    private TextView[] error;
    private ErrorMessageManager manage;
    private LinearLayout linearLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.create_project_3, container, false);
        prepareListData();
        button = (Button) view.findViewById(R.id.createProject3Button);
        button.setOnClickListener(this);
        layout = (LinearLayout)view.findViewById(R.id.popupBudget);
        layout2 = (LinearLayout)view.findViewById(R.id.popupDate);
        layout.setOnClickListener(this);
        layout2.setOnClickListener(this);
        price = (TextView)view.findViewById(R.id.projectPriceText);
        date = (TextView)view.findViewById(R.id.projectDateText);
        text = (EditText) view.findViewById(R.id.projectDescription);
        manage = new ErrorMessageManager();
        error = new TextView[2];
        manage.initializeErrorMessages(error,getActivity());
        linearLayout = (LinearLayout) view.findViewById(R.id.createProject3Layout);
        return view;
    }
    public void setProject(Project p){
        project = p;
    }

    @Override
    public void update(NetworkConnection connection, int index){
        InValid message = new GsonBuilder().create().fromJson(connection.getReturnData(), InValid.class);
        manage.removeErrorMessages(linearLayout, error);
        if (message != null) {
            inputErrorMessage(message);
        } else {
            CreateProject4 cP4 = new CreateProject4();
            cP4.setProject(project);
            getFragmentManager().beginTransaction().
                    replace(R.id.fragment, cP4, "").
                    addToBackStack("").
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                    commit();
        }
    }
    @Override
    public void onClick(View view) {
        ListDialogBox box = new ListDialogBox();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        Bundle args;
        switch(view.getId()) {
            case R.id.popupBudget:
                box = new ListDialogBox();
                box.setValues(budget);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                args = new Bundle();
                DialogFragment newFragment = box;
                newFragment.setArguments(args);
                newFragment.setTargetFragment(this, 1);
                newFragment.show(ft, "dialog");
                break;
            case R.id.popupDate:
                box.setValues(time);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.

                args = new Bundle();
                DialogFragment newFragment2 = box;
                newFragment2.setArguments(args);
                newFragment2.setTargetFragment(this, 1);
                newFragment2.show(ft, "dialog");
                break;
            case R.id.createProject3Button:
                String price = this.price.getText().toString();
                String date = this.date.getText().toString();
                if(price.equals("Please select one")){
                    price = "";
                }
                if(date.equals("Please select one")){
                    date = "";
                }
                String details = text.getText().toString();
                project = new Project(project.getExteriorProjectFlag(),
                        project.getInteriorProjectFlag(),
                        project.getFullName(), project.getPhoneNumber(),
                        project.getAddress(),project.getAptNumber(),
                        project.getLockBoxCode(), price, date, details);
                NetworkConnection connection = new NetworkConnection(NetworkConstants.CREATE_PROJECT, new Gson().toJson(project, Project.class), true, getActivity(), this, 0, 0);
                try {
                    connection.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    private void prepareListData() {
        // Adding child data
        budget = new String[]{"$1 - $500","$501 - $1,500","$1,501 - $5,000","$5,001 - $10,000","$10,000+"};

        time = new String[]{"next 24 hours","within a week","within a Month","3+ months"};
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bundle = data.getExtras();
        int val = bundle.getInt("val", 0);
        int type = bundle.getInt("type", 0);
        if(type==1){
            price.setText(budget[val]);
        } else {
            date.setText(time[val]);
        }
    }
    public void inputErrorMessage(InValid valid) {
        boolean stopScroll = true;
        String badBudget = valid.getProjectBudget();
        String badDate = valid.getProjectDate();
        String badAddress = valid.getProjectAddress();
        final ScrollView scroll = (ScrollView) view.findViewById(R.id.createProject3Scroll);
        if (!badBudget.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.popupBudget));
            error[0].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[0].setTextColor(Color.parseColor("#cc0000"));
            error[0].setGravity(Gravity.CENTER);
            error[0].setText(badBudget);
            linearLayout.addView(error[0], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.createProject3NameTextView).getTop());
                    }
                });
                stopScroll = false;
            }
        }
        if (!badDate.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.popupDate));
            error[1].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[1].setTextColor(Color.parseColor("#cc0000"));
            error[1].setGravity(Gravity.CENTER);
            error[1].setText(badDate);
            linearLayout.addView(error[1], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.createProject3PhoneTextView).getTop());
                    }
                });
                stopScroll = false;
            }
        }
    }
}
