package com.integrail.networkers.primary_operations.storage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.integrail.networkers.data_representations.message_representations.Message;
import com.integrail.networkers.constants.NetworkConstants;
import com.integrail.networkers.primary_operations.messaging.DownloadImage;
import com.integrail.networkers.primary_operations.networking.NetworkConnection;

/**
 * Created by integrailwork on 5/31/17.
 */

public class MessageDataBase {
    private Context ctx;
    private boolean ui;
    public final static String TABLE_NAME="messages";
    public final static String COLUMN_1="messageId";
    public final static String COLUMN_2="id";
    public final static String COLUMN_3="messageFrom";
    public final static String COLUMN_4="message";
    public final static String COLUMN_5="messageRead";
    public final static String COLUMN_6="timeSent";
    public final static String COLUMN_7="timeReceived";
    public final static String COLUMN_8="mms";
    private DataBaseManager manager;
    public MessageDataBase(Context context){
        manager = new DataBaseManager(context);
        ctx = context;
    }
    public void insertMessages(Message[] messages, boolean serviceCall){
        if(messages!=null) {
            for (int i = 0; i < messages.length; i++) {
                insertMessage(messages[i], serviceCall);
            }
        }
    }
    public void insertMessage(Message message, boolean serviceCall){
        long user = new Preferences(ctx).userId();
        SQLiteDatabase db = manager.getWritableDatabase();
        try {
            ContentValues cV = new ContentValues();
            cV.put(COLUMN_1, message.getMessageId());
            cV.put(COLUMN_2, message.getId());
            cV.put(COLUMN_3, message.getMessageFrom());
            cV.put(COLUMN_4, message.getMessage());
            if (message.getMessageRead() == 1) {
                cV.put(COLUMN_5, 1);
            } else {
                if (message.getMessageFrom() == user)
                    cV.put(COLUMN_5, 1);
                else
                    cV.put(COLUMN_5, 0);
            }
            cV.put(COLUMN_6, message.getTimeSent());
            cV.put(COLUMN_7, message.getTimeReceived());
            if (message.isMms()) {
                cV.put(COLUMN_8, 1);
                downloadImage(message.getMessageId(), message.getMessage(), serviceCall);
            } else {
                cV.put(COLUMN_8, 0);
            }
            db.insert(TABLE_NAME, null, cV);
        } catch(SQLiteException e){
            e.printStackTrace();
        } finally{
            db.close();
        }
    }
    public void downloadImage(String conversationId, String message, boolean serviceCall){
        Matcher m = Pattern.compile("^\\d+-").matcher(conversationId);
        if(m.find()){
            conversationId = m.group().replaceAll("-", "");
        }
        if(!serviceCall) {
            Intent intent = new Intent(ctx, DownloadImage.class);
            intent.putExtra(ConversationDataBase.COLUMN_1, Long.valueOf(conversationId));
            intent.putExtra(COLUMN_5, message);
            ctx.startService(intent);
        } else {
            long cId=Long.valueOf(conversationId);
            NetworkConnection connection = null;
            if(cId!=0&&message!=null) {
                connection = new NetworkConnection(NetworkConstants.GET_MMS, message, true, ctx, null, 0,0);
                connection.setDirectory(LocalFileManager.MMS_DIRECTORY+"/"+conversationId+"/");
            }
            connection.setExpectingImage(true);
            connection.setRunningOnUIThread(false);
            connection.connectToURL();
        }
    }
    public void runningOnUI(boolean ui){
        this.ui = ui;
    }
    public void deleteMessage(String messageId){
        SQLiteDatabase db = manager.getWritableDatabase();
        try {
            db.delete(TABLE_NAME, COLUMN_1 + "=?", new String[]{messageId});
            ContentValues cV = new ContentValues();
            long conversationId = Long.valueOf(messageId.replaceAll("-\\d+$", ""));
            cV.put(ConversationDataBase.COLUMN_2, new ConversationDataBase(ctx).getLatestId(conversationId) - 1);
            db.update(ConversationDataBase.TABLE_NAME, cV, ConversationDataBase.COLUMN_1 + "=?", new String[]{""+conversationId});
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
    public Message[] notSent(){
        SQLiteDatabase db = manager.getWritableDatabase();
        ArrayList<Message> notSent = new ArrayList<>();
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_6 + " = ? ORDER BY " + COLUMN_2, new String[]{""+ConversationDataBase.INFINITE_TIME});
            while (res.moveToNext()) {
                String conversationId = res.getString(res.getColumnIndex(COLUMN_1));
                long id = res.getInt(res.getColumnIndex(COLUMN_2));
                long messageFrom = res.getLong(res.getColumnIndex(COLUMN_3));
                String message = res.getString(res.getColumnIndex(COLUMN_4));
                long msgRead = res.getInt(res.getColumnIndex(COLUMN_5));
                long timeSent = res.getLong(res.getColumnIndex(COLUMN_6));
                long timeReceived = res.getLong(res.getColumnIndex(COLUMN_7));
                int mms = res.getInt(res.getColumnIndex(COLUMN_8));
                Message msg = new Message(conversationId, id, messageFrom, message, msgRead, timeSent, timeReceived);
                if(mms == 1)
                    msg.setMms(true);
                notSent.add(msg);
            }
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            if(res!=null &&!res.isClosed()){
                res.close();
            }
            db.close();
        }
        return notSent.toArray(new Message[notSent.size()]);
    }
    public void updateToRead(){
        SQLiteDatabase db = manager.getWritableDatabase();
        try {
            ContentValues cV = new ContentValues();
            cV.put(COLUMN_5, 1);
            db.update(TABLE_NAME, cV, COLUMN_5 + "=2", null);
        } catch (SQLiteException e){
            db.close();
        }
    }
    public String[] setReadMessages(long conversationId){
        SQLiteDatabase db = manager.getWritableDatabase();
        ArrayList<String> messageIds = new ArrayList<>();
        Cursor res = null;
        try {
            res = db.query(TABLE_NAME, new String[]{COLUMN_1}, COLUMN_5 + "=0 AND "+COLUMN_1+" LIKE (?||'-%')", new String[]{""+conversationId}, null, null, null);
            while (res.moveToNext()) {
                messageIds.add(res.getString(res.getColumnIndex(COLUMN_1)));
            }
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            if(res!=null &&!res.isClosed()){
                res.close();
            }
            db.close();
        }
        return messageIds.toArray(new String[messageIds.size()]);
    }
    public String[] readButNotUpdatedMessages(){
        SQLiteDatabase db = manager.getWritableDatabase();
        ArrayList<String> messageIds = new ArrayList<>();
        Cursor res = null;
        try {
            res = db.query(TABLE_NAME, new String[]{COLUMN_1}, COLUMN_5 + "=2", null, null, null, null);
            while (res.moveToNext()) {
                messageIds.add(res.getString(res.getColumnIndex(COLUMN_1)));
            }
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            if(res!=null &&!res.isClosed()){
                res.close();
            }
            db.close();
        }
        return messageIds.toArray(new String[messageIds.size()]);
    }
    public void updateMsgRead(long conversationId, int readType){
        SQLiteDatabase db = manager.getWritableDatabase();
        try {
            ContentValues cV = new ContentValues();
            cV.put(COLUMN_5, readType);
            db.update(TABLE_NAME, cV, COLUMN_1 + " LIKE (?||'-%') AND " + COLUMN_5 + " = ?", new String[]{""+conversationId, String.valueOf(0)});
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
    public void updateIndividualMsgRead(String messageId){
        SQLiteDatabase db = manager.getWritableDatabase();
        try {
            ContentValues cV = new ContentValues();
            cV.put(COLUMN_5, 1);
            db.update(TABLE_NAME, cV, COLUMN_1 + " = ?", new String[]{messageId});
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
    public String getUnreadMessagesFromThread(long conversationId, String realName){
        SQLiteDatabase db = manager.getWritableDatabase();
        String msgs = "";
        Cursor res = null;
        try {
            String select = "SELECT CASE WHEN mms = 0 THEN "+COLUMN_4+" WHEN mms = 1 THEN (?||' sent an image.') END AS "+COLUMN_4+" FROM "+TABLE_NAME+" WHERE "+COLUMN_1+" LIKE(?||'-%') AND "+COLUMN_5+" = 0 ORDER BY "+COLUMN_2+" DESC";
            res = db.rawQuery(select, new String[]{realName, ""+conversationId});
            while (res.moveToNext()) {
                msgs += res.getString(res.getColumnIndex(COLUMN_4)) + "\n\n";
            }
            if (!msgs.equals(""))
                msgs = msgs.substring(0, msgs.length() - 2);
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            if(res!=null &&!res.isClosed()){
                res.close();
            }
            db.close();
        }
        return msgs;
    }
    public String[] getUnreadMessages(){
        SQLiteDatabase db = manager.getWritableDatabase();
        ArrayList<String> msgs = new ArrayList<>();
        Cursor res = null;
        try {
            res = db.query(TABLE_NAME, new String[]{COLUMN_1}, COLUMN_5 + "=?", new String[]{String.valueOf(0)}, null, null, null);
            while (res.moveToNext()) {
                msgs.add(res.getString(res.getColumnIndex(COLUMN_1)));
            }
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally{
            if(res!=null&&!res.isClosed()){
                res.close();
            }
            db.close();
        }
        return msgs.toArray(new String[msgs.size()]);
    }
    public List<Message> getMessageThread(long convId){
        SQLiteDatabase db = manager.getWritableDatabase();
        //"select * from conversations where conversationId = '"+convId+"'"
        //Cursor res = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_1+" LIKE ('"+convId+"'||'/%') ORDER BY "+COLUMN_2+"",null);
        Cursor res = null;
        ArrayList<Message> thread = new ArrayList<Message>();
        try {
            res = db.query(TABLE_NAME,
                    new String[]{COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8},
                    COLUMN_1 + " LIKE(?||'-%')",
                    new String[]{""+convId},
                    null, null, COLUMN_2);
            while (res.moveToNext()) {
                String conversationId = res.getString(res.getColumnIndex(COLUMN_1));
                int id = res.getInt(res.getColumnIndex(COLUMN_2));
                String message = res.getString(res.getColumnIndex(COLUMN_4));
                long messageFrom = res.getLong(res.getColumnIndex(COLUMN_3));
                int msgRead = res.getInt(res.getColumnIndex(COLUMN_5));
                long timeSent = res.getLong(res.getColumnIndex(COLUMN_6));
                long timeReceived = res.getLong(res.getColumnIndex(COLUMN_7));
                long messageRead = 0;
                if (msgRead >= 1) {
                    messageRead = 1;
                }
                int mms = res.getInt(res.getColumnIndex(COLUMN_8));
                Message msg = new Message(conversationId, id, messageFrom, message, messageRead, timeSent, timeReceived);
                if(mms>0) {
                    msg.setMms(true);
                }
                thread.add(msg);
            }
        } catch(SQLiteException e){
            e.printStackTrace();
        } finally{
            if(res!=null&&!res.isClosed())
                res.close();
            db.close();
        }
        return thread;
    }
}
