package com.integrail.networkers.user_interface.home.create_project;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.integrail.networkers.R;
import com.integrail.networkers.user_interface.home.create_project.CreateProjectExterior;
import com.integrail.networkers.user_interface.home.create_project.CreateProjectInterior;

/**
 * Created by Initrail on 11/17/2016.
 */

public class CreateProject extends Fragment implements View.OnClickListener{
    private Button interior;
    private Button exterior;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.create_project, container, false);
        interior = (Button) view.findViewById(R.id.createInteriorProject);
        exterior = (Button) view.findViewById(R.id.createExteriorProject);
        interior.setOnClickListener(this);
        exterior.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.createExteriorProject:
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment, new CreateProjectExterior(), "").
                        addToBackStack("").
                        setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                        commit();
                break;
            case R.id.createInteriorProject:
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment, new CreateProjectInterior(), "").
                        addToBackStack("").
                        setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                        commit();
                break;
        }
    }
}
