package com.integrail.networkers.user_interface.home.create_project;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;

import com.integrail.networkers.R;

/**
 * Created by Integrail on 11/24/2016.
 */

public class ListDialogBox extends DialogFragment implements AdapterView.OnItemClickListener{
    private View view;
    private ListView list;
    private String[] values;
    private ArrayAdapter<String> adapter;
    public void setValues(String[] values){
        this.values = values;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.list_group, container, false);
        list = (ListView) view.findViewById(R.id.projectList);
        adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, R.id.lblListItem, values);
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setOnItemClickListener(this);
        return view;
    }
    @Override
    public void onItemClick(AdapterView <? > parent, View view, int position, long id) {
        ((RadioButton)view.findViewById(R.id.listRadio)).setChecked(true); // true to make r checked, false otherwise.
        int type;
        if(values.length==5){
            type = 1;
        } else {
            type = 2;
        }
        Intent intent = new Intent();
        intent.putExtra("val", position);
        intent.putExtra("type", type);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        dismiss();
    }
}
