package com.integrail.networkers.user_interface.createaccount;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.integrail.networkers.R;

/**
 * Created by Integrail on 7/18/2016.
 */

public class CreateAccount extends Fragment implements View.OnClickListener{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.create_account, container, false);
        Button customer = (Button) view.findViewById(R.id.createAccountCustomerButton);
        Button contractor = (Button) view.findViewById(R.id.createAccountContractorButton);
        customer.setOnClickListener(this);
        contractor.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.createAccountCustomerButton:
                getFragmentManager().beginTransaction().replace(R.id.fragment, new CreateAccountCustomer(), "" ).addToBackStack("").commit();
                break;
            case R.id.createAccountContractorButton:
                getFragmentManager().beginTransaction().replace(R.id.fragment, new CreateAccountContractor(), "" ).addToBackStack("").commit();
                break;
        }
    }
}