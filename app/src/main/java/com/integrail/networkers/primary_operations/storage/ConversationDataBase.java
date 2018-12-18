package com.integrail.networkers.primary_operations.storage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;

import com.integrail.networkers.data_representations.message_representations.ConversationItem;
import com.integrail.networkers.data_representations.message_representations.LatestConversationData;
import com.integrail.networkers.primary_operations.messaging.DownloadImage;

/**
 * Created by integrailwork on 6/12/17.
 */
//TODO, rethink insertConversation()
public class ConversationDataBase {
    public final static String TABLE_NAME = "conversations";
    public final static String COLUMN_1 = "conversationId";
    public final static String COLUMN_2 = "count";
    private DataBaseManager manager;
    public final static long INFINITE_TIME = 1000000000000000000L;
    public static final String DOWNLOAD_IMAGE = "integrail.theconnection.downloadimage";
    private Context ctx;

    public ConversationDataBase(Context context) {
        manager = new DataBaseManager(context);
        ctx = context;
    }

    public void insertConversations(LatestConversationData[] data) {
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                insertConversation(data[i]);
            }
        }
    }

    /*public String[] getUsers() {
        SQLiteDatabase db = manager.getWritableDatabase();
        ArrayList<String> users = new ArrayList<>();
        String query = "SELECT CASE WHEN " + COLUMN_2 + "=? THEN " + COLUMN_4 + " WHEN " + COLUMN_4 + "=? THEN " + COLUMN_2 + " END AS \"user\" FROM " + TABLE_NAME;
        Cursor res = null;
        try {
            res = db.rawQuery(query, new String[]{user, user});
            while (res.moveToNext()) {
                users.add(res.getString(res.getColumnIndex("user")));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (res != null && !res.isClosed())
                res.close();
            db.close();
        }
        return users.toArray(new String[users.size()]);
    }*/

    public void insertConversation(LatestConversationData data) {
        SQLiteDatabase db = manager.getWritableDatabase();
        try {
            ContentValues cV = new ContentValues();
            cV.put(COLUMN_1, data.getConversationId());
            cV.put(COLUMN_2, data.getCount());
            db.insertOrThrow(TABLE_NAME, null, cV);
            downloadImage(data.getUserId());
        } catch (SQLiteConstraintException e) {
            updateCount(data.getCount(), data.getConversationId());
        } finally {
            db.close();
        }
    }

    public void downloadImage(long userId) {
        Intent intent = new Intent(ctx, DownloadImage.class);
        intent.putExtra(Preferences.USER_ID, userId);
        ctx.startService(intent);
    }

    public int getLatestId(long conversationId) {
        SQLiteDatabase db = manager.getWritableDatabase();
        int count = 0;
        Cursor res = null;
        try {
            res = db.query(TABLE_NAME, new String[]{COLUMN_2}, COLUMN_1 + " = ?", new String[]{""+conversationId}, null, null, null);
            while (res.moveToNext()) {
                count = res.getInt(res.getColumnIndex(COLUMN_2));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (res != null && !res.isClosed())
                res.close();
            db.close();
        }
        return count;
    }

    public void updateCount(long count, long conversationId) {
        SQLiteDatabase db = manager.getWritableDatabase();
        try {
            ContentValues cV = new ContentValues();
            cV.put(COLUMN_2, count);
            db.update(TABLE_NAME, cV, COLUMN_1 + " = ?", new String[]{""+conversationId});
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    /*public String getRealName(long conversationId, String email) {
        SQLiteDatabase db = manager.getWritableDatabase();
        String name = "";
        Cursor res = null;
        try {
            res = db.rawQuery("SELECT CASE WHEN ogSender=? THEN senderName WHEN ogReceiver=? THEN receiverName END AS \"name\" FROM " + TABLE_NAME + " WHERE conversationId = ?", new String[]{email, email, ""+conversationId});
            while (res.moveToNext()) {
                name = res.getString(res.getColumnIndex("name"));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (res != null && !res.isClosed())
                res.close();
            db.close();
        }
        return name;
    }*/

    public ArrayList<ConversationItem> buildConversationPage() {
        SQLiteDatabase db = manager.getWritableDatabase();
        //SELECT c.conversationId AS conversationId, c.count AS id, m.message AS message FROM conversations c, messages m WHERE m.id = c.count AND m.messageId = CONCAT(c.conversationId, '/', c.count);
        String select = "SELECT c." + COLUMN_1
                + " AS " + COLUMN_1
                + ", c." + COLUMN_2
                + " AS " + COLUMN_2
                + ", m." + MessageDataBase.COLUMN_4
                + " AS " + MessageDataBase.COLUMN_4
                + ", CASE WHEN m." + MessageDataBase.COLUMN_3
                + "= ?"
                + " THEN m." + MessageDataBase.COLUMN_6
                + " ELSE m." + MessageDataBase.COLUMN_7
                + " END AS \"time\""
                + ", " + MessageDataBase.COLUMN_3
                + ", " + MessageDataBase.COLUMN_5
                + ", " + RecipientsDataBase.COLUMN_3
                + ", r." + RecipientsDataBase.COLUMN_2
                + " AS " + RecipientsDataBase.COLUMN_2
                + ", " + MessageDataBase.COLUMN_8
                + " FROM "+MessageDataBase.TABLE_NAME
                + " m, " + TABLE_NAME
                + " c JOIN " + RecipientsDataBase.TABLE_NAME
                + " r ON (c." + COLUMN_1
                + " = r." + COLUMN_1
                + ")"
                + " WHERE m." + MessageDataBase.COLUMN_2
                + " = c." + COLUMN_2
                + " AND m." + MessageDataBase.COLUMN_1
                + " = (c."
                + COLUMN_1 + "||'-'||c."
                + COLUMN_2 + ") ORDER BY time DESC, conversationId DESC";
        Cursor res = null;
        ArrayList<ConversationItem> list = new ArrayList<ConversationItem>();
        long user = new Preferences(ctx).userId();
        String userId = ""+user;
        try {
            res = db.rawQuery(select, new String[]{userId});
            while (res.moveToNext()) {
                long conversationId = res.getLong(res.getColumnIndex(COLUMN_1));
                int id = res.getInt(res.getColumnIndex(COLUMN_2));
                String message = res.getString(res.getColumnIndex(MessageDataBase.COLUMN_4));
                long other = res.getLong(res.getColumnIndex(RecipientsDataBase.COLUMN_2));
                long time = res.getLong(res.getColumnIndex("time"));
                String name = res.getString(res.getColumnIndex(RecipientsDataBase.COLUMN_3));
                int messageRead = res.getInt(res.getColumnIndex(MessageDataBase.COLUMN_5));
                int mms = res.getInt(res.getColumnIndex(MessageDataBase.COLUMN_8));
                long userSentLast = res.getLong(res.getColumnIndex(MessageDataBase.COLUMN_3));
                if(mms == 1) {
                    if(userSentLast == user)
                        message = "You sent an image.";
                    else
                        message = name + " sent an image.";
                }
                ConversationItem item = new ConversationItem(conversationId, id, other, message, name, time, messageRead, ctx);
                list.add(item);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (res != null && !res.isClosed())
                res.close();
            db.close();
        }
        return list;
    }

    public LatestConversationData[] getData() {
        SQLiteDatabase db = manager.getWritableDatabase();
        //"select conversationId, max(id) from conversations group by conversationId"
        Cursor res = null;
        ArrayList<LatestConversationData> list = new ArrayList<LatestConversationData>();
        try {
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_1, null);
            while (res.moveToNext()) {
                long convId = res.getLong(res.getColumnIndex(ConversationDataBase.COLUMN_1));
                long id = res.getInt(res.getColumnIndex(ConversationDataBase.COLUMN_2));
                LatestConversationData conv =  new LatestConversationData(convId, id);
                list.add(conv);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (res != null && !res.isClosed())
                res.close();
            db.close();
        }
        return list.toArray(new LatestConversationData[list.size()]);
    }
}
