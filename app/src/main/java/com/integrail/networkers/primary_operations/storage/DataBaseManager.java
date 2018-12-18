package com.integrail.networkers.primary_operations.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Integrail on 7/22/2016.
 */

public class DataBaseManager extends SQLiteOpenHelper {
    public final static String DATA_BASE_NAME = "database.db";
    public DataBaseManager(Context context){
        super(context, DATA_BASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.beginTransaction();
        /*db.execSQL("CREATE TABLE " + Preferences.TABLE_NAME + "("
                + Preferences.USER_ID + " INTEGER, "
                + Preferences.SIGNED_IN + " INTEGER, "
                + Preferences.SESSION_ID + " TEXT, "
                + Preferences.ALLOW_MESSAGE_NOTIFICATIONS + " INTEGER DEFAULT 1, "
                + Preferences.RUNNING_RESENT + " INTEGER DEFAULT 0, "
                + Preferences.RUNNING_REREAD + " INTEGER DEFAULT 0, "
                + Preferences.RUNNING_MESSAGING + " INTEGER DEFAULT 0, "
                + Preferences.SERVICE_IS_RUNNING + " INTEGER DEFAULT 0"
                + ")");*/
        db.execSQL("CREATE TABLE "+RecipientsDataBase.TABLE_NAME+"("
                + RecipientsDataBase.COLUMN_2 + " INTEGER, "
                + RecipientsDataBase.COLUMN_1 + " INTEGER, "
                + RecipientsDataBase.COLUMN_3 + " TEXT, "
                + "PRIMARY KEY ("
                + RecipientsDataBase.COLUMN_2 + ", "
                + ConversationDataBase.COLUMN_1 + "))");
        db.execSQL("CREATE TABLE "+ConversationDataBase.TABLE_NAME+"("
                + ConversationDataBase.COLUMN_1 +" INTEGER PRIMARY KEY NOT NULL, "
                + ConversationDataBase.COLUMN_2 +" INTEGER)");
        db.execSQL("CREATE TABLE "+MessageDataBase.TABLE_NAME+"("
                + MessageDataBase.COLUMN_1+" TEXT PRIMARY KEY NOT NULL,"
                + MessageDataBase.COLUMN_2+" INTEGER, "
                + MessageDataBase.COLUMN_3+" TEXT, "
                + MessageDataBase.COLUMN_4+" TEXT, "
                + MessageDataBase.COLUMN_5+" INTEGER, "
                + MessageDataBase.COLUMN_6+" INTEGER DEFAULT 0 NOT NULL, "
                + MessageDataBase.COLUMN_7+" INTEGER DEFAULT 0 NOT NULL, "
                + MessageDataBase.COLUMN_8+" INTEGER DEFAULT 0 NOT NULL)");
        db.execSQL("CREATE TABLE "+ProjectDataBase.TABLE_NAME+"("
                +ProjectDataBase.COLUMN_1+ " INTEGER,"
                +ProjectDataBase.COLUMN_2+" INTEGER,"
                +ProjectDataBase.COLUMN_3+" TEXT, "
                +ProjectDataBase.COLUMN_4+" TEXT, "
                +ProjectDataBase.COLUMN_5+" TEXT, "
                +ProjectDataBase.COLUMN_6+" TEXT, "
                +ProjectDataBase.COLUMN_7+" TEXT, "
                +ProjectDataBase.COLUMN_8+" TEXT, "
                +ProjectDataBase.COLUMN_9+" TEXT, "
                +ProjectDataBase.COLUMN_10+" TEXT, "
                +ProjectDataBase.COLUMN_11+" TEXT, "
                +ProjectDataBase.COLUMN_12+" INTEGER)");
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int o, int n){
        db.beginTransaction();
        //db.execSQL("DROP TABLE IF EXISTS "+Preferences.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+RecipientsDataBase.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ConversationDataBase.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+MessageDataBase.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ProjectDataBase.TABLE_NAME);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }
    public void clearDB(){
        SQLiteDatabase db = getWritableDatabase();
        //db.delete(Preferences.TABLE_NAME, null, null);
        db.delete(RecipientsDataBase.TABLE_NAME, null, null);
        db.delete(ConversationDataBase.TABLE_NAME, null, null);
        db.delete(MessageDataBase.TABLE_NAME, null, null);
        db.delete(ProjectDataBase.TABLE_NAME, null, null);
        db.close();
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }
}//"select max(id), conversationId from conversations group by conversationId"
