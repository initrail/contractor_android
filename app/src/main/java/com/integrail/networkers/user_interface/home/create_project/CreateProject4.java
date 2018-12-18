package com.integrail.networkers.user_interface.home.create_project;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.integrail.networkers.user_interface.home.create_project.contractor_list.ContractorListCreateProjectFinal;

/**
 * Created by Integrail on 11/24/2016.
 */

public class CreateProject4 extends Fragment implements View.OnClickListener, AfterTask{
    private View view;
    private Project project;
    private Button button;
    private TextView[] error;
    private ErrorMessageManager manage;
    private LinearLayout linearLayout;
    @Override
    public void update(NetworkConnection connection, int index){
        InValid message = new GsonBuilder().create().fromJson(connection.getReturnData(), InValid.class);
        manage.removeErrorMessages(linearLayout, error);
        if (message != null) {
        } else {
            ContractorListCreateProjectFinal cPF = new ContractorListCreateProjectFinal();
            cPF.setProject(project);
            getFragmentManager().beginTransaction().
                    replace(R.id.fragment, cPF, "").
                    addToBackStack("").
                    setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                    commit();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.create_project_4, container, false);
        button = (Button) view.findViewById(R.id.createProject4Button);
        button.setOnClickListener(this);
        manage = new ErrorMessageManager();
        error = new TextView[2];
        manage.initializeErrorMessages(error,getActivity());
        linearLayout = (LinearLayout) view.findViewById(R.id.createProject3Layout);
        return view;
    }
    @Override
    public void onClick(View view){
        NetworkConnection connection = new NetworkConnection(NetworkConstants.CREATE_PROJECT, new Gson().toJson(project, Project.class), true, getActivity(), this, 0, 0);
        try {
            connection.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setProject(Project p){
        project = p;
    }
}
