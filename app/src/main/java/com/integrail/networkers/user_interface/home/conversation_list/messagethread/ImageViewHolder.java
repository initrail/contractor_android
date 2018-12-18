package com.integrail.networkers.user_interface.home.conversation_list.messagethread;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.integrail.networkers.R;

/**
 * Created by integrailwork on 6/30/17.
 */

public class ImageViewHolder extends RecyclerView.ViewHolder {
    protected ImageView fromSystem;
    protected ImageButton sendImage;
    public ImageViewHolder(View view){
        super(view);
        fromSystem = (ImageView) view.findViewById(R.id.imageFromSystem);
        sendImage = (ImageButton) view.findViewById(R.id.sendImage);
    }
}
