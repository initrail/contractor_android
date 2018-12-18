package com.integrail.networkers.user_interface.home.existing_projects_list;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import com.integrail.networkers.R;
import com.integrail.networkers.data_representations.project_representations.Project;
import com.integrail.networkers.primary_operations.storage.ProjectDataBase;
import com.integrail.networkers.user_interface.home.existing_projects_list.project_detail.ProjectDetail;

/**
 * Created by Integrail on 11/26/2016.
 */

public class ProjectList extends Fragment {
    private View view;
    private List<Project> list;
    private RecyclerView mRecyclerView;
    private ProjectListAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.manage_project_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_manage_project);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ProjectDataBase dataBase = new ProjectDataBase(getActivity().getApplicationContext());
        //Project test = new Project(1,0,"Daniel Kalam", "7747771234","3676 South Depew Street", "204", "77658", "$500", "Feb 12th, 2017", "Test");
        list = dataBase.getProjectList();
        adapter = new ProjectListAdapter(list, this);
        mRecyclerView.setAdapter(adapter);
        return view;
    }
    public void showMessageThread(int index){
        ProjectDetail detail = new ProjectDetail();
        detail.setProject(list.get(index));
        getFragmentManager().beginTransaction().
                replace(R.id.fragment, detail, "").
                addToBackStack("").
                setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                commit();
    }
}
