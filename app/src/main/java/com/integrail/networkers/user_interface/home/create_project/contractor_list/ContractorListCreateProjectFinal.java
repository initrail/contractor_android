package com.integrail.networkers.user_interface.home.create_project.contractor_list;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

import com.integrail.networkers.R;
import com.integrail.networkers.data_representations.project_representations.Project;
import com.integrail.networkers.data_representations.account_representations.ContractorObjectSimple;
import com.integrail.networkers.data_representations.project_representations.ProjectFlags;
import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.user_interface.AfterTask;

/**
 * Created by Integrail on 11/24/2016.
 */

public class ContractorListCreateProjectFinal extends Fragment implements View.OnClickListener, AfterTask {
    private View view;
    private Project project;
    private Button finished;
    private RecyclerView mRecyclerView;
    private List<ContractorObjectSimple> list;
    private ContractorListAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.create_project_final, container, false);
        NetworkConnection connection = new NetworkConnection(NetworkConstants.GET_CONTRACTOR_LIST, new Gson().toJson(new ProjectFlags(project.getExteriorProjectFlag(), project.getInteriorProjectFlag()), ProjectFlags.class), true, getActivity(), this, 0, 0);
        try{
            connection.execute();
        } catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }
    @Override
    public void update(NetworkConnection connection, int index){
        ContractorObjectSimple[] c = new GsonBuilder().create().fromJson(connection.getReturnData(), ContractorObjectSimple[].class);
        if(c!=null) {
            list = Arrays.asList(c);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_project);
            adapter = new ContractorListAdapter(list, project, getActivity());
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }
    public void setProject(Project p){
        project = p;
    }
    @Override
    public void onClick(View view){
        project.setFinished(true);
    }
}
