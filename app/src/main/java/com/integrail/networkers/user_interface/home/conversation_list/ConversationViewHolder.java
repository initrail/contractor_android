package com.integrail.networkers.user_interface.home.conversation_list;

import android.support.v7.widget.RecyclerView.*;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.integrail.networkers.R;

/**
 * Created by Integrail on 7/21/2016.
 */

public class ConversationViewHolder extends ViewHolder implements View.OnLongClickListener, View.OnClickListener{
    protected ImageView picture;
    protected TextView contact;
    protected TextView messagePreview;
    protected TextView time;
    protected View container;
    protected int val;
    protected ConversationList context;
    public ConversationViewHolder(View view, ConversationList context){
        super(view);
        container = view;
        this.context = context;
        picture = (ImageView) view.findViewById(R.id.picture);
        contact = (TextView) view.findViewById(R.id.contact);
        messagePreview = (TextView) view.findViewById(R.id.messagePreview);
        time = (TextView) view.findViewById(R.id.messageTime);
    }
    public TextView getContact(){
        return contact;
    }
    public TextView getMessagePreview(){
        return messagePreview;
    }
    public TextView getTime(){
        return time;
    }
    public int getVal(){
        return val;
    }
    @Override
    public void onClick(View view){
        int index = getAdapterPosition();
        context.showMessageThread(index);
    }
    @Override
    public boolean onLongClick(View view){
        return true;
    }
}
