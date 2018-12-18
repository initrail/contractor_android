package com.integrail.networkers.user_interface.createaccount;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
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
import com.integrail.networkers.data_representations.account_representations.ContractorAccount;
import com.integrail.networkers.data_representations.validate.ErrorMessageManager;
import com.integrail.networkers.data_representations.validate.InValid;
import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.user_interface.AfterTask;

/**
 * Created by Integrail on 7/18/2016.
 */

public class CreateAccountContractor extends Fragment implements View.OnClickListener, AfterTask {
    private TextView[] error;
    private LinearLayout linearLayout;
    private View view;
    private EditText eText;
    private ContractorAccount contractorAccount;
    private ErrorMessageManager manage;
    @Override
    public void update(NetworkConnection connection, int index){
        manage.removeErrorMessages(linearLayout, error);
        InValid message = new GsonBuilder().create().fromJson(connection.getReturnData(), InValid.class);
        if (message != null) {
            inputErrorMessage(message);
        } else {
            CreateAccountContractor2 continued = new CreateAccountContractor2();
            continued.setContractorAccount(contractorAccount);
            getFragmentManager().beginTransaction().
                    replace(R.id.fragment, continued, "").
                    addToBackStack("").
                    setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                    commit();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.create_tutor_account, container, false);
        Button carryOn = (Button) view.findViewById(R.id.coCreateAccNext);
        error = new TextView[8];
        linearLayout = (LinearLayout) view.findViewById(R.id.dynamicContractor);
        carryOn.setOnClickListener(this);
        manage = new ErrorMessageManager();
        manage.initializeErrorMessages(error, getActivity());
        return view;
    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.coCreateAccNext:
                contractorAccount = parseContractorFields();
                NetworkConnection connection = new NetworkConnection(NetworkConstants.CREATE_ACCOUNT, new Gson().toJson(contractorAccount), true, getActivity(), this, 0, 0);
                try {
                    connection.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    public void inputErrorMessage(InValid valid) {
        boolean stopScroll = true;
        String badPassword1 = valid.getInValidPassword();
        String badPhoneNumber = valid.getInValidPhone();
        String badZip = valid.getInValidZip();
        String badFirstName = valid.getInValidFirstName();
        String badEmail = valid.getInValidEmail();
        String badLastName = valid.getInValidLastName();
        String badAddress = valid.getInValidAddress();
        final ScrollView scroll = (ScrollView) view.findViewById(R.id.coAcScroll);
        if (!badFirstName.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.coCr0));
            error[0].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[0].setTextColor(Color.parseColor("#cc0000"));
            error[0].setGravity(Gravity.CENTER);
            error[0].setText(badFirstName);
            linearLayout.addView(error[0], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.firstName).getTop());
                    }
                });
                stopScroll = false;
            }
        }
        if (!badLastName.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.coCr1));
            error[1].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[1].setTextColor(Color.parseColor("#cc0000"));
            error[1].setGravity(Gravity.CENTER);
            error[1].setText(badLastName);
            linearLayout.addView(error[1], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.lastName).getTop());
                    }
                });
                stopScroll = false;
            }
        }
        if (!badEmail.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.coCr2));
            error[2].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[2].setTextColor(Color.parseColor("#cc0000"));
            error[2].setGravity(Gravity.CENTER);
            error[2].setText(badEmail);
            linearLayout.addView(error[2], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.textView17).getTop());
                    }
                });
                stopScroll = false;
            }
        }
        if (!badPhoneNumber.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.coCr3));
            error[3].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            error[3].setTextColor(Color.parseColor("#cc0000"));
            error[3].setGravity(Gravity.CENTER);
            error[3].setText(badPhoneNumber);
            linearLayout.addView(error[3], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.textView18).getTop());
                    }
                });
                stopScroll = false;
            }
        }
        if (!badZip.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.coCr4));
            error[4].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[4].setTextColor(Color.parseColor("#cc0000"));
            error[4].setGravity(Gravity.CENTER);
            error[4].setText(badZip);
            linearLayout.addView(error[4], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.textView19).getTop());
                    }
                });
                stopScroll = false;
            }
        }
        if (!badPassword1.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.coCr5));
            error[5].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));// in pixels (left, top, right, bottom)
            error[5].setTextColor(Color.parseColor("#cc0000"));
            error[5].setGravity(Gravity.CENTER);
            error[5].setText(badPassword1);
            linearLayout.addView(error[5], find + 1);
            eText = (EditText) view.findViewById(R.id.coCr5);
            eText.setText("");
            eText = (EditText) view.findViewById(R.id.coCr6);
            eText.setText("");
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.textView20).getTop());
                    }
                });
                stopScroll = false;
            }
        }
        if (badPassword1.equals("Required field.")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.coCr6));
            error[6].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            error[6].setTextColor(Color.parseColor("#cc0000"));
            error[6].setGravity(Gravity.CENTER);
            error[6].setText(badPassword1);
            linearLayout.addView(error[6], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.textView22).getTop());
                    }
                });
                stopScroll = false;
            }
        }
        if (!badAddress.equals("")) {
            int find = linearLayout.indexOfChild(view.findViewById(R.id.coCr7));
            error[7].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            error[7].setTextColor(Color.parseColor("#cc0000"));
            error[7].setGravity(Gravity.CENTER);
            error[7].setText(badAddress);
            linearLayout.addView(error[7], find + 1);
            if (stopScroll) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.smoothScrollTo(0, view.findViewById(R.id.textView22).getTop());
                    }
                });
                stopScroll = false;
            }
        }
    }
    public ContractorAccount parseContractorFields() {
        String[] c = new String[9];
        EditText coEtext = (EditText) view.findViewById(R.id.coCr0);
        EditText coEtext1 = (EditText) view.findViewById(R.id.coCr1);
        EditText coEtext2 = (EditText) view.findViewById(R.id.coCr2);
        EditText coEtext3 = (EditText) view.findViewById(R.id.coCr3);
        EditText coEtext4 = (EditText) view.findViewById(R.id.coCr4);
        EditText coEtext5 = (EditText) view.findViewById(R.id.coCr5);
        EditText coEtext6 = (EditText) view.findViewById(R.id.coCr6);
        EditText coEtext7 = (EditText) view.findViewById(R.id.coCr7);
        EditText coEtext8 = (EditText) view.findViewById(R.id.coCr8);
        EditText[] cu = {coEtext, coEtext1, coEtext2, coEtext3, coEtext4, coEtext5, coEtext6, coEtext7, coEtext8};
        for (int i = 0; i < cu.length; i++) {
            c[i] = cu[i].getText().toString();
        }
        return new ContractorAccount(c[0], c[1], c[2], c[3], c[4], c[5], c[6], c[7], c[8], 0, (byte)0, false, (byte)0, false);
    }
}
