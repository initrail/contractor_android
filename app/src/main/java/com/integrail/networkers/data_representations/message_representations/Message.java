package com.integrail.networkers.data_representations.message_representations;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.sql.Timestamp;
import java.sql.Date;

import com.bumptech.glide.Glide;
import com.integrail.networkers.primary_operations.storage.LocalFileManager;

/**
 * Created by Integrail on 7/13/2016.
 */

public class Message {
    private String messageId;
    private long id;
    private long messageFrom;
    private long messageTo;
    private String message;
    private long messageRead;
    private long timeSent;
    private long timeReceived;
    private boolean mms;
    private String jSessionId;
    private int height;
    private int width;
    private float aspectRatio;
    public boolean isMms() {
        return mms;
    }
    public void setMms(boolean mms, String location){
        this.mms = mms;
        if(mms) {
            Uri loc = Uri.parse(LocalFileManager.MAIN_URI_DIRECTORY + LocalFileManager.MMS_DIRECTORY + "/" + location);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(new File(loc.getPath()).getAbsolutePath(), options);
            height = options.outHeight;
            width = options.outWidth;
            aspectRatio = (float) width / (float) height;
        }
    }
    public void setMms(boolean mms) {
        this.mms = mms;
        if(mms) {
            long conversationId = Long.valueOf(messageId.replaceAll("-\\d+$", ""));
            Uri loc = Uri.parse(LocalFileManager.MAIN_URI_DIRECTORY + LocalFileManager.MMS_DIRECTORY + "/" + conversationId + "/" + message);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(new File(loc.getPath()).getAbsolutePath(), options);
            height = options.outHeight;
            width = options.outWidth;
            aspectRatio = (float) width / (float) height;
        }
    }
    public float getAspectRatio(){
        return aspectRatio;
    }
    public String getjSessionId() {
        return jSessionId;
    }

    public void setjSessionId(String jSessionId) {
        this.jSessionId = jSessionId;
    }

    public Message(String message, long to){
        this.message = message;
        messageTo = to;
    }
    public Message(String message, long to, String convId){
        this.message = message;
        messageTo = to;
        messageId = convId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long isMessageRead() {
        return messageRead;
    }

    public long getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(long timeSent) {
        this.timeSent = timeSent;
    }

    public long getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(long timeReceived) {
        this.timeReceived = timeReceived;
    }

    public Message(String cId, long id, long mF, String m, long mR, long t, long t2){
        messageId = cId;
        this.id = id;
        messageFrom = mF;
        message = m;
        messageRead = mR;
        timeSent = t;
        timeReceived = t2;

    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setMessageFrom(long messageFrom) {
        this.messageFrom = messageFrom;
    }

    public void setMessageTo(long messageTo) {
        this.messageTo = messageTo;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public int getHeight(){
        return height;
    }
    public int getWidth(){
        return width;
    }
    public void setMessageRead(long messageRead) {
        this.messageRead = messageRead;
    }

    public String getMessageId(){

        return messageId;
    }
    public long getId(){
        return id;
    }
    public long getMessageFrom(){
        return messageFrom;
    }
    public long getMessageTo(){
        return messageTo;
    }
    public String getMessage(){
        return message;
    }
    public long getMessageRead(){
        return messageRead;
    }
}
