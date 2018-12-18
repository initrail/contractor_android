package com.integrail.networkers.user_interface.createaccount;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.integrail.networkers.data_representations.account_representations.ContractorAccount;
import com.integrail.networkers.R;

/**
 * Created by Integrail on 7/18/2016.
 */

public class CreateAccountContractor2 extends Fragment implements View.OnClickListener{
    private ContractorAccount cF;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.create_tutor_account_continued, container, false);
        Button next = (Button) view.findViewById(R.id.coCoNextButton1);
        next.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.coCoNextButton1:
                parseContractorFields();
                CreateAccountContractor3 c = new CreateAccountContractor3();
                c.setContractorAccount(cF);
                getFragmentManager().beginTransaction().replace(R.id.fragment, c, "" ).addToBackStack("").commit();
                break;
        }
    }
    public void setContractorAccount(ContractorAccount c){
        cF = c;
    }
    public void parseContractorFields() {
        SwitchCompat switch1 = (SwitchCompat) view.findViewById(R.id.switch1);
        SwitchCompat switch2 = (SwitchCompat) view.findViewById(R.id.switch2);
        SwitchCompat switch3 = (SwitchCompat) view.findViewById(R.id.switch3);
        SwitchCompat switch4 = (SwitchCompat) view.findViewById(R.id.switch4);
        SwitchCompat switch5 = (SwitchCompat) view.findViewById(R.id.switch5);
        SwitchCompat switch6 = (SwitchCompat) view.findViewById(R.id.switch6);
        SwitchCompat switch7 = (SwitchCompat) view.findViewById(R.id.switch7);
        SwitchCompat switch8 = (SwitchCompat) view.findViewById(R.id.switch8);
        SwitchCompat switch9 = (SwitchCompat) view.findViewById(R.id.switch9);
        SwitchCompat switch10 = (SwitchCompat) view.findViewById(R.id.switch10);
        SwitchCompat switch11 = (SwitchCompat) view.findViewById(R.id.switch11);
        SwitchCompat switch12 = (SwitchCompat) view.findViewById(R.id.switch12);
        SwitchCompat switch13 = (SwitchCompat) view.findViewById(R.id.switch13);
        SwitchCompat switch14 = (SwitchCompat) view.findViewById(R.id.switch14);
        SwitchCompat switch15 = (SwitchCompat) view.findViewById(R.id.switch15);
        SwitchCompat switch16 = (SwitchCompat) view.findViewById(R.id.switch16);
        SwitchCompat switch17 = (SwitchCompat) view.findViewById(R.id.switch17);
        SwitchCompat[] sw0 = {
                switch1,
                switch2,
                switch3,
                switch4,
                switch5,
                switch6,
                switch7,
                switch8,
                switch9,
                switch10,
                switch11,
                switch12,
                switch13,
                switch14,
                switch15,
                switch16,
                switch17
        };
        int interiorSkillset = 0;
        for (int i = 0; i < sw0.length; i++) {
            if (sw0[i].isChecked()) {
                interiorSkillset |= (int) Math.pow(2, i);
            }
        }
        cF.setInteriorSkills(interiorSkillset);
    }
}
