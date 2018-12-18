package com.integrail.networkers.user_interface.home.conversation_list;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.integrail.networkers.primary_operations.storage.Preferences;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import com.integrail.networkers.R;
import com.integrail.networkers.data_representations.message_representations.ConversationItem;
import com.integrail.networkers.primary_operations.storage.ConversationDataBase;

/**
 * Created by Integrail on 7/22/2016.
 */

public class ConversationListAdapter extends RecyclerView.Adapter<ConversationViewHolder>{
    private List<ConversationItem> list;
    private ConversationList context;
    private Preferences preferences;
    public static final String DATE_FORMAT = "M/d/yyyy h:mm a";
    public ConversationListAdapter(List<ConversationItem> list, ConversationList context){
        this.context = context;
        this.list=list;
    }
    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup group, int i){
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.conversation, null);
        ConversationViewHolder conv = new ConversationViewHolder(view, context);
        conv.val = i;
        return conv;
    }
    @Override
    public void onBindViewHolder(ConversationViewHolder conv, int i){

        RequestOptions req = new RequestOptions().signature(new ObjectKey(String.valueOf(list.get(i).getDate()))).placeholder(R.drawable.blank).dontAnimate();
        conv.contact.setText(list.get(i).getName());
        conv.messagePreview.setText(list.get(i).getMsg());
        long userId = new Preferences(context.getActivity().getApplicationContext()).userId();
        if(list.get(i).getMessageRead()==0 && list.get(i).getOther() != userId ){
            conv.messagePreview.setTypeface(conv.messagePreview.getTypeface(), Typeface.BOLD);
            conv.messagePreview.setTextColor(Color.BLACK);
        }
        SimpleDateFormat monthDayYear = new SimpleDateFormat("MMM d, yyyy");
        SimpleDateFormat hourMinute = new SimpleDateFormat("h:mm a");
        String current = monthDayYear.format(System.currentTimeMillis());
        String time = monthDayYear.format(list.get(i).getTime());
        Glide.with(context.getActivity())
                .load(list.get(i).getImage())
                .apply(req)
                .into(conv.picture);
        if(list.get(i).getTime()==ConversationDataBase.INFINITE_TIME)
            conv.time.setText("sending...");
        else if(!current.equals(time))
            conv.time.setText(monthDayYear.format(new Timestamp(list.get(i).getTime())));
        else
            conv.time.setText(hourMinute.format(new Timestamp(list.get(i).getTime())));
        conv.container.setOnClickListener(conv);
    }
    @Override
    public int getItemCount(){
        return (null!=list?list.size():0);
    }
}