package com.integrail.networkers.user_interface.home.existing_projects_list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.integrail.networkers.R;
import com.integrail.networkers.data_representations.project_representations.Project;

/**
 * Created by Integrail on 7/22/2016.
 */

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectViewHolder>{
    private List<Project> list;
    private ProjectList context;
    public ProjectListAdapter(List<Project> list, ProjectList context){
        this.list=list;
        this.context = context;
    }
    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup group, int i){
        View view;
        ProjectViewHolder proj;
        view = LayoutInflater.from(group.getContext()).inflate(R.layout.project_item, null);
        proj = new ProjectViewHolder(view, context);
        return proj;
    }
    @Override
    public void onBindViewHolder(ProjectViewHolder projectView, int i) {
            Project project = list.get(i);
            projectView.projectAddress.setText(project.getAddress());
            projectView.budget.setText(project.getBudget());
    }
    @Override
    public int getItemCount(){
        return list.size();
    }
}
