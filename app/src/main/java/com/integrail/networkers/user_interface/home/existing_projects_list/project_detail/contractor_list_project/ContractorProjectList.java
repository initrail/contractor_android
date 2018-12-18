package com.integrail.networkers.user_interface.home.existing_projects_list.project_detail.contractor_list_project;

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
import com.integrail.networkers.data_representations.account_representations.ContractorItemSimple;

/**
 * Created by Integrail on 11/27/2016.
 */

public class ContractorProjectList extends Fragment {
    private View view;
    private List<ContractorItemSimple> list;
    private RecyclerView mRecyclerView;
    private ContractorProjectListAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.project_contractor_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_project_contractors);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ContractorItemSimple test = new ContractorItemSimple("Daniel Kalam", "oneofuser25@gmail.com", "7747771234");
        list = Arrays.asList(test);
        adapter = new ContractorProjectListAdapter(list);
        mRecyclerView.setAdapter(adapter);
        return view;
    }
}
