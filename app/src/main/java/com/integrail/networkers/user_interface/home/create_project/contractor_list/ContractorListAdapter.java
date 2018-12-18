package com.integrail.networkers.user_interface.home.create_project.contractor_list;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.integrail.networkers.R;
import com.integrail.networkers.data_representations.account_representations.ContractorObjectSimple;
import com.integrail.networkers.data_representations.project_representations.Project;
import com.integrail.networkers.primary_operations.storage.Preferences;

/**
 * Created by Integrail on 7/22/2016.
 */

public class ContractorListAdapter extends RecyclerView.Adapter<ContractorViewHolder>{
    private List<ContractorObjectSimple> list;
    private Project project;
    private Activity activity;
    public ContractorListAdapter(List<ContractorObjectSimple> list, Project project, Activity activity){
        this.list=list;
        this.project = project;
        this.activity = activity;
    }
    @Override
    public ContractorViewHolder onCreateViewHolder(ViewGroup group, int i){
        View view;
        ContractorViewHolder conv;
        if(i==1) {
            view = LayoutInflater.from(group.getContext()).inflate(R.layout.contractor_project, null);
        } else {
            view = LayoutInflater.from(group.getContext()).inflate(R.layout.create_project_final_button, group, false);
        }
        conv = new ContractorViewHolder(view, i, project, activity);
        conv.val = i;
        return conv;
    }
    @Override
    public void onBindViewHolder(ContractorViewHolder contractor, int i) {
        if (i == list.size()) {
        } else {
            ContractorObjectSimple contractorItem = list.get(i);
            contractor.contractorName.setText(contractorItem.getName());
        }
    }
    @Override
    public int getItemCount(){
        return list.size()+1;
    }
    @Override
    public int getItemViewType(int position){
        int val = 1;
        if(position==list.size()){
               val = 2;
        }
        return val;
    }
}
