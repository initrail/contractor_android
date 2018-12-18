package com.integrail.networkers.data_representations.validate;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Integrail on 7/21/2016.
 */

public class ErrorMessageManager {
    public void removeErrorMessages(LinearLayout linearLayout, TextView[] error) {
        for (int i = 0; i < error.length; i++) {
            linearLayout.removeView(error[i]);
        }
    }
    public void initializeErrorMessages(TextView[] error, Activity getActivity)  {
        for (int i = 0; i < error.length; i++) {
            error[i] = new TextView(getActivity);
        }
    }
}
