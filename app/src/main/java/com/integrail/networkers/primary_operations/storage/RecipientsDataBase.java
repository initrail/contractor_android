package com.integrail.networkers.primary_operations.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.integrail.networkers.data_representations.message_representations.LatestConversationData;

import java.util.ArrayList;

public class RecipientsDataBase {
    private DataBaseManager manager;
    private Context ctx;
    public final static String TABLE_NAME="recipients";
    public final static String COLUMN_1 = "conversationId";
    public static final String COLUMN_2 = "userId";
    public static final String COLUMN_3 = "fullName";
    public RecipientsDataBase(Context context) {
        manager = new DataBaseManager(context);
        ctx = context;
    }
    public void insertConversations(LatestConversationData[] data){
        ConversationDataBase cDB = new ConversationDataBase(ctx);
        for(int i = 0; i<data.length; i++){
            insertRecipient(data[i].getConversationId(), data[i].getUserId(), data[i].getSenderName());
            cDB.insertConversation(data[i]);
        }
    }
    public String getRealName(long userId){
        SQLiteDatabase db = manager.getWritableDatabase();
        String name = "";
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT " + COLUMN_3 + " FROM " + TABLE_NAME + " WHERE " + COLUMN_2 + " = ?", new String[]{"" + userId});
            while (res.moveToNext()) {
                name = res.getString(res.getColumnIndex(COLUMN_3));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (res != null && !res.isClosed())
                res.close();
            db.close();
        }
        return name;
    }
    public void insertRecipient(long conversationId, long userId, String fullName){
        SQLiteDatabase db = manager.getWritableDatabase();
        try {
            ContentValues cV = new ContentValues();
            cV.put(COLUMN_1, conversationId);
            cV.put(COLUMN_2, userId);
            cV.put(COLUMN_3, fullName);
            db.insertOrThrow(TABLE_NAME, null, cV);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
    public Long[] getUsers() {
        SQLiteDatabase db = manager.getWritableDatabase();
        ArrayList<Long> users = new ArrayList<>();
        //String query = "SELECT CASE WHEN " + COLUMN_2 + "=? THEN " + COLUMN_4 + " WHEN " + COLUMN_4 + "=? THEN " + COLUMN_2 + " END AS \"user\" FROM " + TABLE_NAME;
        String query = "SELECT "+COLUMN_2+" FROM "+TABLE_NAME;
        Cursor res = null;
        try {
            res = db.rawQuery(query, null);
            while (res.moveToNext()) {
                users.add(res.getLong(res.getColumnIndex(COLUMN_2)));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (res != null && !res.isClosed())
                res.close();
            db.close();
        }
        return users.toArray(new Long[users.size()]);
    }
}
