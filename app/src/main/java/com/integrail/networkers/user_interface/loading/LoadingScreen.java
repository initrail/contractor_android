package com.integrail.networkers.user_interface.loading;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.integrail.networkers.R;


/**
 * Created by integrailwork on 5/19/17.
 */

public class LoadingScreen extends Fragment implements View.OnClickListener{
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.loading, container, false);
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.preventClicks);
        layout.setOnClickListener(this);
        ProgressBar bar = (ProgressBar) view.findViewById(R.id.progressBar);
        bar.setOnClickListener(this);
        bar.getIndeterminateDrawable().setColorFilter(Color.MAGENTA, PorterDuff.Mode.MULTIPLY);
        return view;
    }
    @Override
    public void onClick(View view){

    }
}