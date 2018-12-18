package com.integrail.networkers.user_interface.home.existing_projects_list;

import android.support.v7.widget.RecyclerView.*;
import android.view.View;
import android.widget.TextView;

import com.integrail.networkers.R;

/**
 * Created by Integrail on 7/21/2016.
 */

public class ProjectViewHolder extends ViewHolder implements View.OnLongClickListener, View.OnClickListener{
    protected TextView projectAddress;
    protected TextView budget;
    protected TextView status;
    protected TextView startDate;
    protected TextView endDate;
    private ProjectList context;
    public ProjectViewHolder(View view, ProjectList context){
        super(view);
        this.context = context;
        view.setOnClickListener(this);
        projectAddress = (TextView) view.findViewById(R.id.projectAddress);
        budget = (TextView) view.findViewById(R.id.budget);
        status = (TextView) view.findViewById(R.id.status);
        startDate = (TextView) view.findViewById(R.id.startDate);
        endDate = (TextView) view.findViewById(R.id.endDate);
    }
    @Override
    public boolean onLongClick(View view){
        return true;
    }
    @Override
    public void onClick(View view){
        int index = getAdapterPosition();
        context.showMessageThread(index);
    }
}
