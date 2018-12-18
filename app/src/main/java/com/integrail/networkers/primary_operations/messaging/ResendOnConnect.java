package com.integrail.networkers.primary_operations.messaging;

import android.app.IntentService;
import android.content.Intent;

import com.integrail.networkers.primary_operations.networking.ResendOnConnectCore;

/**
 * Created by integrailwork on 6/26/17.
 */

public class ResendOnConnect extends IntentService {
    public ResendOnConnect(){
        super("");
    }
    @Override
    public void onHandleIntent(Intent intent) {
        ResendOnConnectCore resend = new ResendOnConnectCore(getApplicationContext());
        resend.resend();
    }
}
