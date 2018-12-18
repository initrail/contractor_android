package com.integrail.networkers.user_interface.home.conversation_list.messagethread;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.integrail.networkers.R;

/**
 * Created by Integrail on 7/28/2016.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {
    protected View view;
    protected MessageList context;
    protected TextView message;
    protected TextView time;
    protected ImageView userImage;
    protected ImageView mms;
    public MessageViewHolder(View view, MessageList context, boolean mms){
        super(view);
        this.view = view;
        this.context = context;
        if(!mms)
            message = (TextView) view.findViewById(R.id.messageContent);
        time = (TextView) view.findViewById(R.id.dateOfMessage);
        userImage = (ImageView) view.findViewById(R.id.picture);
        if(mms)
            this.mms = (ImageView) view.findViewById(R.id.messageImage);
    }
}
