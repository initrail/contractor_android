package com.integrail.networkers.user_interface.home.conversation_list.messagethread;
import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

import com.bumptech.glide.Glide;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.integrail.networkers.MainActivity;
import com.integrail.networkers.R;
import com.integrail.networkers.data_representations.message_representations.Message;
import com.integrail.networkers.primary_operations.storage.ConversationDataBase;
import com.integrail.networkers.primary_operations.storage.LocalFileManager;
import com.integrail.networkers.primary_operations.storage.Preferences;

/**
 * Created by Integrail on 7/22/2016.
 */

public class MessageListAdapter extends RecyclerView.Adapter<MessageViewHolder>{
    private List<Message> list;
    private MessageList context;
    private Preferences preferences;
    private long userId;
    private String userImage;
    private String otherImage;
    private String imageDirectory;
    private LocalFileManager fileManager;
    private String otherImageTime;
    private String userImageTime;
    private int onCreateIndex;
    public MessageListAdapter(List<Message> list, MessageList context, String userImage, String otherImage, Activity app, long conversationId){
        this.context = context;
        this.list=list;
        preferences = new Preferences(app);
        userId = preferences.userId();
        this.userImage = LocalFileManager.MAIN_URI_DIRECTORY+LocalFileManager.USER_IMAGE_DIRECTORY+"/"+userImage;
        this.otherImage = LocalFileManager.MAIN_URI_DIRECTORY+LocalFileManager.USER_IMAGE_DIRECTORY+"/"+otherImage;
        if(conversationId > 0)
            imageDirectory = LocalFileManager.MAIN_URI_DIRECTORY+LocalFileManager.MMS_DIRECTORY+"/"+conversationId+"/";
        else
            imageDirectory = LocalFileManager.MAIN_URI_DIRECTORY+LocalFileManager.MMS_DIRECTORY+"/";
        fileManager = new LocalFileManager(context.getActivity());
        userImageTime = ""+fileManager.createDirectoryAndGetWritableFile(LocalFileManager.USER_IMAGE_DIRECTORY+"/"+userImage).lastModified();
        otherImageTime = ""+fileManager.createDirectoryAndGetWritableFile(LocalFileManager.USER_IMAGE_DIRECTORY+"/"+otherImage).lastModified();
    }
    public void setConvId(long conversationId){
        imageDirectory+=conversationId+"/";
    }
    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup group, int i){
        View view = null;
        boolean mms = false;
        if(i==1){
            view = LayoutInflater.from(group.getContext()).inflate(R.layout.multimedia_message, null);
            mms = true;
        } else if(i==2) {
            view = LayoutInflater.from(group.getContext()).inflate(R.layout.message_right, null);
        } else if(i==3){
            view = LayoutInflater.from(group.getContext()).inflate(R.layout.multimedia_message_left, null);
            mms = true;
        } else if(i==4){
            view = LayoutInflater.from(group.getContext()).inflate(R.layout.message, null);
        }
        MessageViewHolder message = new MessageViewHolder(view, context, mms);
        return message;
    }
    @Override
    public void onBindViewHolder(MessageViewHolder message, int i){
        Message msg = list.get(i);
        if(list.get(i).getMessageFrom() == userId) {
            RequestOptions req = new RequestOptions().signature(new ObjectKey(userImageTime)).placeholder(R.drawable.blank).dontAnimate();
            Glide.with(context.getActivity())
                    .load(userImage)
                    .apply(req)
                    .into(message.userImage);
        } else {
            RequestOptions req = new RequestOptions().signature(new ObjectKey(otherImageTime)).placeholder(R.drawable.blank).dontAnimate();
            Glide.with(context.getActivity())
                    .load(LocalFileManager.MAIN_URI_DIRECTORY+LocalFileManager.USER_IMAGE_DIRECTORY+"/"+msg.getMessageFrom()+".jpg")
                    .apply(req)
                    .into(message.userImage);
        }
        String current = new SimpleDateFormat("MMM d").format(System.currentTimeMillis());
        String received = String.format(new SimpleDateFormat("MMM d").format(list.get(i).getTimeReceived()));
        String sent = String.format(new SimpleDateFormat("MMM d").format(list.get(i).getTimeSent()));
        SimpleDateFormat monthDayYear = new SimpleDateFormat("MMM d, yyyy");
        SimpleDateFormat hourMinute = new SimpleDateFormat("h:mm a");
        if(msg.getMessageFrom() == userId) {
            if(list.get(i).getTimeSent()== ConversationDataBase.INFINITE_TIME)
                message.time.setText("sending...");
            else if(!current.equals(sent))
                message.time.setText(monthDayYear.format(new Timestamp(list.get(i).getTimeSent())));
            else
                message.time.setText(hourMinute.format(new Timestamp(list.get(i).getTimeSent())));
        }
        else {
            if(!current.equals(received))
                message.time.setText(monthDayYear.format(new Timestamp(list.get(i).getTimeReceived())));
            else
                message.time.setText(hourMinute.format(new Timestamp(list.get(i).getTimeReceived())));
        }
        if(!msg.isMms())
            message.message.setText(msg.getMessage());
        else {
            TableRow.LayoutParams params;
            int maxDim = (int) MainActivity.convertDpToPixel(250);
            Uri loc = Uri.parse(imageDirectory+list.get(i).getMessage());
            Glide.with(context)
                    .load(loc)
                    .into(message.mms);
            if(list.get(i).getHeight()<list.get(i).getWidth()) {
                int height = (int)((float)maxDim/list.get(i).getAspectRatio());
                params = new TableRow.LayoutParams(maxDim, height);
                message.mms.setLayoutParams(params);
            } else {
                params = new TableRow.LayoutParams(maxDim, maxDim);
                message.mms.setLayoutParams(params);
            }
        }
    }
    @Override
    public int getItemCount(){
        return (null!=list?list.size():0);
    }
    @Override
    public int getItemViewType(int position) {
        onCreateIndex = position;
        Message msg = list.get(position);
        if(msg.getMessageFrom() == preferences.userId()){
            if(msg.isMms())
                return 1;
            else
                return 2;
        } else {
            if(msg.isMms())
                return 3;
            else
                return 4;
        }
    }
}