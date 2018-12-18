package com.integrail.networkers.user_interface.home.existing_projects_list.project_detail.contractor_list_project;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.integrail.networkers.R;
import com.integrail.networkers.data_representations.account_representations.ContractorItemSimple;

/**
 * Created by Integrail on 7/22/2016.
 */

public class ContractorProjectListAdapter extends RecyclerView.Adapter<ContractorProjectViewHolder>{
    private List<ContractorItemSimple> list;
    public ContractorProjectListAdapter(List<ContractorItemSimple> list){
        this.list=list;
    }
    @Override
    public ContractorProjectViewHolder onCreateViewHolder(ViewGroup group, int i){
        View view;
        ContractorProjectViewHolder c;
        view = LayoutInflater.from(group.getContext()).inflate(R.layout.contractor_detail, group, false);
        c = new ContractorProjectViewHolder(view);
        return c;
    }
    @Override
    public void onBindViewHolder(ContractorProjectViewHolder c, int i) {
        ContractorItemSimple co = list.get(i);
        c.contractorName.setText(co.getName());
        c.email.setText(co.getEmail());
        c.phone.setText(co.getPhone());
    }
    @Override
    public int getItemCount(){
        return list.size();
    }
}
