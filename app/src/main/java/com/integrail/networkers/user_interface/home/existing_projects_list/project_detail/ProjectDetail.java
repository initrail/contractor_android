package com.integrail.networkers.user_interface.home.existing_projects_list.project_detail;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.integrail.networkers.R;
import com.integrail.networkers.user_interface.home.existing_projects_list.project_detail.contractor_list_project.ContractorProjectList;
import com.integrail.networkers.data_representations.project_representations.Project;

/**
 * Created by Integrail on 11/27/2016.
 */

public class ProjectDetail extends Fragment implements View.OnClickListener{
    private View view;
    private TextView budget_project;
    private TextView endDate_project;
    private TextView address;
    private Project project;
    private Button showBindedContractors;
    private Button delete;
    private Button managePictures;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.project_details, container, false);
        budget_project = (TextView) view.findViewById(R.id.budget_project);
        endDate_project = (TextView) view.findViewById(R.id.endDate_project);
        address = (TextView) view.findViewById(R.id.address);
        showBindedContractors = (Button) view.findViewById(R.id.showContractors);
        managePictures = (Button) view.findViewById(R.id.managePictures);
        delete = (Button) view.findViewById(R.id.deleteProject);
        delete.setOnClickListener(this);
        managePictures.setOnClickListener(this);
        showBindedContractors.setOnClickListener(this);
        budget_project.setText(project.getBudget());
        endDate_project.setText(project.getDateOfCompletion());;
        address.setText(project.getAddress());
        return view;
    }
    public void setProject(Project p) {
        project = p;
    }
    public void onClick(View view){
        switch (view.getId()){
            case R.id.deleteProject:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to delete this project?");
                builder.setCancelable(false);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.showContractors:
                getFragmentManager().beginTransaction().
                        replace(R.id.fragment, new ContractorProjectList(), "").
                        addToBackStack("").
                        setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                        commit();
                break;
            case R.id.managePictures:
                break;
        }
    }
}
