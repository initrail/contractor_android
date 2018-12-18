package com.integrail.networkers.user_interface.home.conversation_list.messagethread;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.integrail.networkers.R;

/**
 * Created by integrailwork on 6/30/17.
 */

public class ImageListAdapter extends RecyclerView.Adapter<ImageViewHolder> {
    private List<String> fileLocation;
    public ImageListAdapter(List<String> list){
        fileLocation = list;
    }
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup group, int i){
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.get_media_view, null);
        ImageViewHolder imageViewHolder = new ImageViewHolder(view);
        return imageViewHolder;
    }
    @Override
    public void onBindViewHolder(ImageViewHolder image, int i){

    }
    @Override
    public int getItemCount(){
        return fileLocation.size();
    }
}
