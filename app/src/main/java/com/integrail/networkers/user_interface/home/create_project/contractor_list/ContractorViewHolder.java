package com.integrail.networkers.user_interface.home.create_project.contractor_list;

import android.app.Activity;
import android.support.v7.widget.RecyclerView.*;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.integrail.networkers.R;
import com.integrail.networkers.data_representations.project_representations.Project;
import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;
import com.integrail.networkers.primary_operations.storage.ProjectDataBase;

/**
 * Created by Integrail on 7/21/2016.
 */

public class ContractorViewHolder extends ViewHolder implements View.OnLongClickListener, View.OnClickListener{
    protected ImageView picture;
    protected TextView contractorName;
    protected View container;
    protected int val;
    protected Button getAttributes;
    protected Button select;
    protected Button finish;
    private Project p;
    private Activity activity;
    public ContractorViewHolder(View view, int val, Project p, Activity activity){
        super(view);
        switch(val){
            case 1:
                container = view;
                picture = (ImageView) view.findViewById(R.id.pictureContractor);
                contractorName = (TextView) view.findViewById(R.id.contractorName);
                getAttributes = (Button) view.findViewById(R.id.info);
                select = (Button) view.findViewById(R.id.select);
                getAttributes.setOnClickListener(this);
                select.setOnClickListener(this);
                break;
            case 2:
                container = view;
                finish = (Button) container.findViewById(R.id.finishedProjectButton);
                finish.setOnClickListener(this);
                this.activity = activity;
                this.p = p;
                break;
        }
    }

    public TextView getContractorName(){
        return contractorName;
    }
    public int getVal(){
        return val;
    }
    @Override
    public boolean onLongClick(View view){
        return true;
    }
    @Override
    public void onClick(View view){
        switch(view.getId()) {
            case R.id.select:
                break;
            case R.id.info:
                break;
            case R.id.finishedProjectButton:
                p.setFinished(true);
                NetworkConnection connection = new NetworkConnection(NetworkConstants.CREATE_PROJECT, p.toJSON(), true, activity, null, 1, 0);
                ProjectDataBase projectDataBase = new ProjectDataBase(activity.getApplicationContext());
                projectDataBase.insertProject(p);
                try {
                    connection.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
