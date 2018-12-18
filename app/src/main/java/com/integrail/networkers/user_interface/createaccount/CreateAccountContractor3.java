package com.integrail.networkers.user_interface.createaccount;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.integrail.networkers.data_representations.account_representations.ContractorAccount;
import com.integrail.networkers.R;
import com.integrail.networkers.data_representations.validate.ErrorMessageManager;
import com.integrail.networkers.data_representations.validate.InValid;
import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.user_interface.AfterTask;

public class CreateAccountContractor3 extends Fragment implements View.OnClickListener, AfterTask{
    private ContractorAccount contractorAccountFinal;
    private ScrollView scroll;
    private TextView[] error;
    private View view;
    private LinearLayout linearLayout;
    private ErrorMessageManager manage;
    private Button coCrFinish;
    @Override
    public void update(NetworkConnection connection, int index){
        manage.removeErrorMessages(linearLayout, error);
        InValid message = new GsonBuilder().create().fromJson(connection.getReturnData(), InValid.class);
        if (message != null) {
            inputErrorMessage4(message);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.create_contractor_account_finished, container, false);
        linearLayout = (LinearLayout) view.findViewById(R.id.finishedDynamic);
        scroll = (ScrollView) view.findViewById(R.id.finalScroll);
        error = new TextView[1];
        manage = new ErrorMessageManager();
        manage.initializeErrorMessages(error, getActivity());
        coCrFinish = (Button) view.findViewById(R.id.coCrFinish);
        coCrFinish.setOnClickListener(this);
        return view;
    }
    public void setContractorAccount(ContractorAccount c){
        contractorAccountFinal = c;
    }
    @Override
    public void onClick(View view){
        ContractorAccount coAccountF = parseContractorFields();
        NetworkConnection connection = new NetworkConnection(NetworkConstants.CREATE_ACCOUNT, new Gson().toJson(coAccountF), true, getActivity(), this, 0, 0);
        try {
            connection.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ContractorAccount parseContractorFields() {
        SwitchCompat switch18 = (SwitchCompat) view.findViewById(R.id.switch18);
        SwitchCompat switch19 = (SwitchCompat) view.findViewById(R.id.switch19);
        SwitchCompat switch20 = (SwitchCompat) view.findViewById(R.id.switch20);
        SwitchCompat switch21 = (SwitchCompat) view.findViewById(R.id.switch21);
        SwitchCompat switch22 = (SwitchCompat) view.findViewById(R.id.switch22);
        SwitchCompat switch23 = (SwitchCompat) view.findViewById(R.id.switch23);
        SwitchCompat switch24 = (SwitchCompat) view.findViewById(R.id.switch24);
        SwitchCompat switch25 = (SwitchCompat) view.findViewById(R.id.switch25);
        SwitchCompat switch26 = (SwitchCompat) view.findViewById(R.id.switch26);
        SwitchCompat switch27 = (SwitchCompat) view.findViewById(R.id.switch27);
        SwitchCompat switch28 = (SwitchCompat) view.findViewById(R.id.switch28);
        SwitchCompat[] sw1 = {
                switch18,
                switch19,
                switch20,
                switch21,
                switch22,
                switch23,
                switch24
        };
        SwitchCompat[] sw2 = {
                switch25,
                switch26,
                switch27,
                switch28
        };
        byte exteriorSkillset = 0;
        for (int i = 0; i < sw1.length; i++) {
            if (sw1[i].isChecked()) {
                exteriorSkillset |= (int) Math.pow(2, i);
            }
        }
        byte licenses = 0;
        for (int i = 0; i < sw2.length; i++) {
            if (sw2[i].isChecked()) {
                licenses |= (int) Math.pow(2, i);
            }
        }
        RadioButton yes = (RadioButton) view.findViewById(R.id.radioButton);
        boolean insurance = false;
        if (yes.isChecked()) {
            insurance = true;
        }
        return new ContractorAccount(contractorAccountFinal, exteriorSkillset, insurance, licenses, true);
    }
    public void inputErrorMessage4(InValid valid) {
        boolean stopScroll = true;
        String noSkillz = valid.getInValidSkills();


        if (!noSkillz.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.walls));
            error[0].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[0].setTextColor(Color.parseColor("#cc0000"));
            error[0].setGravity(Gravity.CENTER);
            error[0].setText(noSkillz);
            linearLayout.addView(error[0], find-1);
            final ScrollView scroll = (ScrollView) view.findViewById(R.id.finalScroll);
            final int val = view.findViewById(R.id.textView24).getBottom();
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    scroll.smoothScrollTo(0, val);
                }
            });
        }
    }
}
