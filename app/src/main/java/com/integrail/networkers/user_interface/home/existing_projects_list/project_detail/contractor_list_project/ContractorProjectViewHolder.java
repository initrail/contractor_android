package com.integrail.networkers.user_interface.home.existing_projects_list.project_detail.contractor_list_project;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.TextView;

import com.integrail.networkers.R;

/**
 * Created by Integrail on 7/21/2016.
 */

public class ContractorProjectViewHolder extends ViewHolder implements View.OnLongClickListener, View.OnClickListener{
    protected TextView email;
    protected TextView phone;
    protected TextView contractorName;
    public ContractorProjectViewHolder(View view){
        super(view);
        view.setOnClickListener(this);
        phone = (TextView) view.findViewById(R.id.phone);
        email = (TextView) view.findViewById(R.id.email);
        contractorName = (TextView) view.findViewById(R.id.contractorName);
    }
    @Override
    public boolean onLongClick(View view){
        return true;
    }
    @Override
    public void onClick(View view){
    }
}
