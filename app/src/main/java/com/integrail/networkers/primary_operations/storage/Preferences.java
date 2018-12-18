package com.integrail.networkers.primary_operations.storage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by integrailwork on 5/23/17.
 */

public class Preferences {
    public final static String SIGNED_IN = "signedIn";
    public final static String SESSION_ID = "sessionId";
    public final static String USER_ID = "user";
    public final static String ALLOW_MESSAGE_NOTIFICATIONS = "allowMessageNotifications";
    public final static String STORAGE_NAME = "preferences";
    public final static String DEFAULT_SESSION = "JSESSIONID=";
    public final static String RUNNING_RESENT = "ResendOnConnect";
    public final static String RUNNING_REREAD = "ReReadOnConnect";
    public final static String DEFAULT_SESSION_FROM_SERVER = DEFAULT_SESSION+"\"\"";
    public final static String RUNNING_MESSAGING = "messageServiceShouldBeRunning";
    public final static String FIRST_RUN = "firstRun";
    public final static String SERVICE_IS_RUNNING = "serviceIsRunning";
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;

    public Preferences(Activity activity){
        pref = activity.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        edit = pref.edit();
    }
    public boolean firstRun(){
        return pref.getBoolean(FIRST_RUN, true);
    }
    public Preferences(Context context){
        pref = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        edit = pref.edit();
    }
    public boolean resendRunning(){
        return pref.getBoolean(RUNNING_RESENT, false);
    }
    public boolean reReadRunning(){
        return pref.getBoolean(RUNNING_REREAD, false);
    }
    public boolean serviceShouldBeRunning(){
        return pref.getBoolean(RUNNING_MESSAGING, false);
    }
    public String session(){
        return pref.getString(SESSION_ID, DEFAULT_SESSION);
    }
    public long userId(){
        return pref.getLong(USER_ID, 0);
    }
    public boolean signedIn(){
        return pref.getBoolean(SIGNED_IN, false);
    }
    public boolean allowMessageNotifications(){
        return pref.getBoolean(ALLOW_MESSAGE_NOTIFICATIONS, false);
    }
    /*public boolean serviceIsRunning(){
        return pref.getBoolean(SERVICE_IS_RUNNING, false);
    }
    public void setServiceIsRunning(boolean serviceIsRunning){
        edit.putBoolean(SERVICE_IS_RUNNING, serviceIsRunning);
        edit.commit();
    }*/



    public void setPreferences(String session, long user, boolean signedIn){
        edit.putLong(USER_ID, user);
        edit.putBoolean(SIGNED_IN, signedIn);
        edit.putBoolean(ALLOW_MESSAGE_NOTIFICATIONS, true);
        setSession(session);
        edit.commit();
    }
    public void setOtherCredentials(boolean signedIn, long userId){
        edit.putLong(USER_ID, userId);
        edit.putBoolean(SIGNED_IN, signedIn);
        edit.putBoolean(ALLOW_MESSAGE_NOTIFICATIONS, true);
        edit.commit();
    }
    public void setAllowMessageNotifications(boolean allowNotifications){
        edit.putBoolean(ALLOW_MESSAGE_NOTIFICATIONS, allowNotifications);
        edit.commit();
    }
    public void clearPreferences(String session){
        edit.putBoolean(RUNNING_REREAD, false);
        edit.putBoolean(RUNNING_RESENT, false);
        edit.putBoolean(RUNNING_MESSAGING, false);
        edit.putString(SESSION_ID, session);
        edit.putLong(USER_ID, 0);
        edit.putBoolean(SIGNED_IN, false);
        edit.putBoolean(ALLOW_MESSAGE_NOTIFICATIONS, false);
        edit.commit();
    }
    public void setSession(String session){
        if(session!=null) {
            if (session.equals("")||session.equals(DEFAULT_SESSION_FROM_SERVER)) {
                clearPreferences(session);
            } else {
                edit.putString(SESSION_ID, session);
                edit.commit();
            }
        } else {
            clearPreferences(session);
        }
    }
    public void setSignedIn(boolean signedIn){
        edit.putBoolean(SIGNED_IN, signedIn);
        edit.commit();
    }
    public void setReReadRunning(boolean reReadRunning){
        edit.putBoolean(RUNNING_REREAD, reReadRunning);
        edit.commit();
    }
    public void setServiceShouldBeRunning(boolean messagingService){
        edit.putBoolean(RUNNING_MESSAGING, messagingService);
        edit.commit();
    }
    public void setResendRunning(boolean resendRunning){
        edit.putBoolean(RUNNING_RESENT, resendRunning);
        edit.commit();
    }
    public void notFirstRun(){
        edit.putBoolean(FIRST_RUN, false);
        edit.commit();
    }
}


/*import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.integrail.networkers.data_representations.message_representations.LatestConversationData;
import com.integrail.networkers.user_interface.home.conversation_list.ConversationList;

import java.util.ArrayList;

/**
 * Created by integrailwork on 5/23/17.
 */

/*public class Preferences {
    public final static String TABLE_NAME = "UserPreferences";
    public final static String SIGNED_IN = "signedIn";
    public final static String SESSION_ID = "sessionId";
    public final static String USER_ID = "userId";
    public final static String ALLOW_MESSAGE_NOTIFICATIONS = "allowMessageNotifications";
    public final static String STORAGE_NAME = "preferences";
    public final static String DEFAULT_SESSION = "JSESSIONID=";
    public final static String RUNNING_RESENT = "ResendOnConnect";
    public final static String RUNNING_REREAD = "ReReadOnConnect";
    public final static String DEFAULT_SESSION_FROM_SERVER = DEFAULT_SESSION+"\"\"";
    public final static String RUNNING_MESSAGING = "messageServiceShouldBeRunning";
    public final static String FIRST_RUN = "firstRun";
    public final static String SERVICE_IS_RUNNING = "serviceIsRunning";
    private SharedPreferences pref;
    private SharedPreferences.Editor edit;
    private DataBaseManager manager;
    private Context ctx;
    private long userId;
    public Preferences(Activity activity){
        manager = new DataBaseManager(activity);
        pref = activity.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        edit = pref.edit();
    }
    public long selectUserId(){
        return getLong(USER_ID);
    }
    public Preferences(Context context){
        manager = new DataBaseManager(context);
        pref = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
        edit = pref.edit();
    }
    private void putBoolean(String column, boolean value){
        SQLiteDatabase db = manager.getWritableDatabase();
        try {
            ContentValues cV = new ContentValues();
            if(value)
                cV.put(column, 1);
            else
                cV.put(column, 0);
            db.update(TABLE_NAME, cV, USER_ID + " = ?", new String[]{""+userId});
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            //db.close();
        }
    }
    private void putLong(String column, long value){
        SQLiteDatabase db = manager.getWritableDatabase();
        try {
            ContentValues cV = new ContentValues();
            cV.put(column, value);
            db.update(TABLE_NAME, cV, USER_ID + " = ?", new String[]{""+userId});
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            //db.close();
        }
    }
    private void putString(String column, String value){
        SQLiteDatabase db = manager.getWritableDatabase();
        try {
            ContentValues cV = new ContentValues();
            cV.put(column, value);
            db.update(TABLE_NAME, cV, USER_ID + " = ?", new String[]{""+userId});
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            //db.close();
        }
    }
    private String getString(String column){
        SQLiteDatabase db = manager.getWritableDatabase();
        Cursor res = null;
        String ret = "";
        try {
            res = db.rawQuery("SELECT " + column + " FROM " + TABLE_NAME, null);
            while (res.moveToNext()) {
                ret = res.getString(res.getColumnIndex(column));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (res != null && !res.isClosed())
                res.close();
            //db.close();
        }
        return ret;
    }
    private long getLong(String column){
        SQLiteDatabase db = manager.getWritableDatabase();
        Cursor res = null;
        long ret = 0;
        try {
            res = db.rawQuery("SELECT " + column + " FROM " + TABLE_NAME, null);
            while (res.moveToNext()) {
                ret = res.getLong(res.getColumnIndex(column));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (res != null && !res.isClosed())
                res.close();
            //db.close();
        }
        return ret;
    }
    private boolean getBoolean(String column){
        SQLiteDatabase db = manager.getWritableDatabase();
        Cursor res = null;
        long ret = -1;
        try {
            res = db.rawQuery("SELECT " + column + " FROM " + TABLE_NAME, null);
            while (res.moveToNext())
                ret = res.getLong(res.getColumnIndex(column));
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (res != null && !res.isClosed())
                res.close();
            //db.close();
        }
        if(ret == 1)
            return true;
        else
            return false;
    }
    public boolean resendRunning(){
        //return pref.getBoolean(RUNNING_RESENT, false);
        return getBoolean(RUNNING_RESENT);
    }
    public boolean reReadRunning(){
        //return pref.getBoolean(RUNNING_REREAD, false);
        return getBoolean(RUNNING_REREAD);
    }
    public boolean serviceShouldBeRunning(){
        //return pref.getBoolean(RUNNING_MESSAGING, false);
        return getBoolean(RUNNING_MESSAGING);
    }
    public String session(){
        //return pref.getString(SESSION_ID, DEFAULT_SESSION);
        return getString(SESSION_ID);
    }
    public long userId(){
        //return pref.getLong(USER_ID, 0);
        return getLong(USER_ID);
    }
    public boolean signedIn(){
        //return pref.getBoolean(SIGNED_IN, false);
        return getBoolean(SIGNED_IN);
    }
    public boolean allowMessageNotifications(){
        //return pref.getBoolean(ALLOW_MESSAGE_NOTIFICATIONS, false);
        return getBoolean(ALLOW_MESSAGE_NOTIFICATIONS);
    }
    public boolean serviceIsRunning(){
        //return pref.getBoolean(SERVICE_IS_RUNNING, false);
        return getBoolean(SERVICE_IS_RUNNING);
    }
    public void setServiceIsRunning(boolean serviceIsRunning){
        userId = selectUserId();
        putBoolean(SERVICE_IS_RUNNING, serviceIsRunning);
        /*edit.putBoolean(SERVICE_IS_RUNNING, serviceIsRunning);
        edit.commit();*/
    /*}

    public void setOtherCredentials(boolean signedIn, long id){
        /*db.execSQL("CREATE TABLE " + Preferences.TABLE_NAME + "("
                + Preferences.USER_ID + " INTEGER, "
                + Preferences.SIGNED_IN + " INTEGER, "
                + Preferences.SESSION_ID + " TEXT, "
                + Preferences.ALLOW_MESSAGE_NOTIFICATIONS + " INTEGER, "
                + Preferences.RUNNING_RESENT + " INTEGER, "
                + Preferences.RUNNING_REREAD + " INTEGER, "
                + Preferences.RUNNING_MESSAGING + " INTEGER, "
                + Preferences.SERVICE_IS_RUNNING + " INTEGER"
                + ")");*/
        /*SQLiteDatabase db = manager.getWritableDatabase();
        try {
            ContentValues cV = new ContentValues();
            cV.put(USER_ID, id);
            if(signedIn)
                cV.put(SIGNED_IN, 1);
            else
                cV.put(SIGNED_IN, 0);
            cV.put(SESSION_ID, "");
            cV.put(ALLOW_MESSAGE_NOTIFICATIONS, 1);
            cV.put(RUNNING_RESENT, 0);
            cV.put(RUNNING_REREAD, 0);
            cV.put(RUNNING_MESSAGING, 0);
            cV.put(SERVICE_IS_RUNNING, 0);
            long result = db.insert(TABLE_NAME, null, cV);
            System.out.println(result);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        } finally {
            //db.close();
        }
        /*edit.putLong(USER_ID, userId);
        edit.putBoolean(SIGNED_IN, signedIn);
        edit.putBoolean(ALLOW_MESSAGE_NOTIFICATIONS, true);
        edit.commit();*/
    /*}
    public void setAllowMessageNotifications(boolean allowNotifications){
        userId = selectUserId();
        putBoolean(ALLOW_MESSAGE_NOTIFICATIONS, allowNotifications);
        /*edit.putBoolean(ALLOW_MESSAGE_NOTIFICATIONS, allowNotifications);
        edit.commit();*/
    /*}
    public void clearPreferences(String session){
        userId = selectUserId();
        SQLiteDatabase db = manager.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM "+ TABLE_NAME);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        } finally {
            //db.close();
        }
        /*edit.putBoolean(RUNNING_REREAD, false);
        edit.putBoolean(RUNNING_RESENT, false);
        edit.putBoolean(RUNNING_MESSAGING, false);
        edit.putString(SESSION_ID, session);
        edit.putLong(USER_ID, 0);
        edit.putBoolean(SIGNED_IN, false);
        edit.putBoolean(ALLOW_MESSAGE_NOTIFICATIONS, false);
        edit.putBoolean(SERVICE_IS_RUNNING, false);
        edit.commit();*/
    /*}
    public void setSession(String session){
        if(session!=null) {
            if (session.equals("")||session.equals(DEFAULT_SESSION_FROM_SERVER)) {
                clearPreferences(session);
            } else {
                userId = selectUserId();
                putString(SESSION_ID, session);
                /*edit.putString(SESSION_ID, session);
                edit.commit();*/
            /*}
        } else {
            clearPreferences(session);
        }
    }
    public void setReReadRunning(boolean reReadRunning){
        userId = selectUserId();
        putBoolean(RUNNING_REREAD, reReadRunning);
        /*edit.putBoolean(RUNNING_REREAD, reReadRunning);
        edit.commit();*/
    /*}
    public void setServiceShouldBeRunning(boolean messagingService){
        userId = selectUserId();
        putBoolean(RUNNING_MESSAGING, messagingService);
        /*edit.putBoolean(RUNNING_MESSAGING, messagingService);
        edit.commit();*/
    /*}
    public void setResendRunning(boolean resendRunning){
        userId = selectUserId();
        putBoolean(RUNNING_RESENT, resendRunning);
        /*edit.putBoolean(RUNNING_RESENT, resendRunning);
        edit.commit();*/
    /*}
    public boolean firstRun(){
        return pref.getBoolean(FIRST_RUN, true);
    }
    public void notFirstRun(){
        edit.putBoolean(FIRST_RUN, false);
        edit.commit();
    }
}*/