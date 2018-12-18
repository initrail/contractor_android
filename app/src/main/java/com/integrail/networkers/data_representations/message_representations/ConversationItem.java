package com.integrail.networkers.data_representations.message_representations;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.integrail.networkers.primary_operations.storage.LocalFileManager;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Integrail on 7/25/2016.
 */

public class ConversationItem {
    private long convId;
    private int id;
    private long other;
    private String msg;
    private long time1;
    private long time2;
    private int messageRead;
    private String name;
    private Uri image;
    private String date;
    public void setConvId(long convId) {
        this.convId = convId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOther(long other) {
        this.other = other;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTime() {
        return time1;
    }

    public void setTime1(long time1) {
        this.time1 = time1;
    }

    public long getTime2() {
        return time2;
    }

    public void setTime2(long time2) {
        this.time2 = time2;
    }

    public ConversationItem(long convId, int id, long other, String msg, String name, long time1, int messageRead, Context ctx){
        this.convId = convId;
        this.id = id;
        this.other = other;
        this.msg = msg;
        this.time1 = time1;
        this.name = name;
        this.messageRead = messageRead;
        File dir = ctx.getDir("UserImages", Context.MODE_PRIVATE);
        date = ""+new File(dir, other+".jpg").lastModified();
        image = Uri.parse(LocalFileManager.MAIN_URI_DIRECTORY+LocalFileManager.USER_IMAGE_DIRECTORY+"/"+other+".jpg");
    }
    public String getDate(){
        return date;
    }
    public String getName(){
        return name;
    }
    public Uri getImage(){
        return image;
    }
    public long getConvId(){
        return convId;
    }
    public int getId(){
        return id;
    }
    public long getOther(){
        return other;
    }
    public String getMsg(){
        return msg;
    }
    public int getMessageRead(){
        return messageRead;
    }
}
