package com.integrail.networkers.primary_operations.storage;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.integrail.networkers.data_representations.message_representations.LatestConversationData;
import com.integrail.networkers.data_representations.message_representations.Message;
import com.integrail.networkers.data_representations.message_representations.ConversationItem;
import com.integrail.networkers.data_representations.project_representations.Project;

/**
 * Created by Integrail on 11/25/2016.
 */

public class ProjectDataBase {
    private DataBaseManager manager;
    public final static String TABLE_NAME="projects";
    public final static String COLUMN_1="exteriorProjectFlag";
    public final static String COLUMN_2="interiorProjectFlag";
    public final static String COLUMN_3="fullName";
    public final static String COLUMN_4="phone";
    public final static String COLUMN_5="address";
    public final static String COLUMN_6="aptNumber";
    public final static String COLUMN_7="lockBox";
    public final static String COLUMN_8="budget";
    public final static String COLUMN_9="dateOfCompletion";
    public final static String COLUMN_10="projectDescription";
    public final static String COLUMN_11="creator";
    public final static String COLUMN_12="id";
    public ProjectDataBase(Context context){
        manager = new DataBaseManager(context);
    }
    public void insertProjects(Project[] projects){
        for(int i = 0; i<projects.length; i++){
            insertProject(projects[i]);
        }
    }
    public void insertProject(Project project){
        SQLiteDatabase db = manager.getWritableDatabase();
        try {
            ContentValues cV = new ContentValues();
            cV.put(COLUMN_1, project.getExteriorProjectFlag());
            cV.put(COLUMN_2, project.getInteriorProjectFlag());
            cV.put(COLUMN_3, project.getFullName());
            cV.put(COLUMN_4, project.getPhoneNumber());
            cV.put(COLUMN_5, project.getAddress());
            cV.put(COLUMN_6, project.getAptNumber());
            cV.put(COLUMN_7, project.getLockBoxCode());
            cV.put(COLUMN_8, project.getBudget());
            cV.put(COLUMN_9, project.getDateOfCompletion());
            cV.put(COLUMN_10, project.getProjectDescription());
            cV.put(COLUMN_11, project.getCreator());
            cV.put(COLUMN_12, project.getId());
            db.insertOrThrow(TABLE_NAME, null, cV);
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
    public ArrayList<Project> getProjectList(){
        SQLiteDatabase db = manager.getWritableDatabase();
        Cursor res = null;
        ArrayList<Project> list = new ArrayList<Project>();
        try {
            res = db.rawQuery("select * from " + TABLE_NAME, null);
            while (res.moveToNext()) {
                Project project = new Project();
                project.setInteriorProjectFlag(res.getInt(res.getColumnIndex(ProjectDataBase.COLUMN_1)));
                project.setExteriorProjectFlag(res.getInt(res.getColumnIndex(ProjectDataBase.COLUMN_2)));
                project.setFullName(res.getString(res.getColumnIndex(ProjectDataBase.COLUMN_3)));
                project.setPhoneNumber(res.getString(res.getColumnIndex(ProjectDataBase.COLUMN_4)));
                project.setAddress(res.getString(res.getColumnIndex(ProjectDataBase.COLUMN_5)));
                project.setAptNumber(res.getString(res.getColumnIndex(ProjectDataBase.COLUMN_6)));
                project.setLockBoxCode(res.getString(res.getColumnIndex(ProjectDataBase.COLUMN_7)));
                project.setBudget(res.getString(res.getColumnIndex(ProjectDataBase.COLUMN_8)));
                project.setDateOfCompletion(res.getString(res.getColumnIndex(ProjectDataBase.COLUMN_9)));
                project.setProjectDescription(res.getString(res.getColumnIndex(ProjectDataBase.COLUMN_10)));
                project.setCreator(res.getString(res.getColumnIndex(ProjectDataBase.COLUMN_11)));
                project.setId(res.getInt(res.getColumnIndex(ProjectDataBase.COLUMN_12)));
                list.add(project);
            }
        } catch (SQLiteException e){
            e.printStackTrace();
        } finally {
            if(res!=null &&!res.isClosed()){
                res.close();
            }
            db.close();
        }
        return list;
    }
}
