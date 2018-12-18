package com.integrail.networkers.primary_operations.networking;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Integrail on 7/23/2016.
 */
public class ValidateCookie extends AsyncTask<Void, Void, String> {
    private String cookie;
    public ValidateCookie(String cookie){
        this.cookie=cookie;
    }
    @Override
    public String doInBackground(Void... args){
        String cookieFromSite = signOut();
        return cookieFromSite;
    }
    private String signOut(){
        String cookieFromSite = "";
        HttpURLConnection conn = null;
        try {
            URL url = new URL("http://10.0.0.213:9090/networkers/validatecookie");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setRequestProperty("Cookie", cookie);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();
            cookieFromSite = conn.getHeaderField("Set-Cookie");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            conn.disconnect();
        }
        if(cookieFromSite==null||cookieFromSite.equals("")) {
            cookieFromSite= cookie;
        }
        return cookieFromSite;
    }
}
