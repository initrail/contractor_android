package com.integrail.networkers.user_interface.home.conversation_list.messagethread;

/*import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.integrail.networkers.R;

public class PhotoWindow extends PopupWindow {
    private Context mContext;
    private View view;
    private PhotoView photoView;
    private ProgressBar loading;
    private Bitmap bitmap;
    public PhotoWindow(Context ctx, int layout, View v, String imageUrl, Bitmap bitmap){
        super(((LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.photo_window, null), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if(Build.VERSION.SDK_INT >= 21)
            setElevation(5.0f);
        mContext = ctx;
        view = v;
        ImageButton exit = (ImageButton) view.findViewById(R.id.ib_close);
        setOutsideTouchable(true);
        setFocusable(true);
        exit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dismiss();
            }
        });
        photoView = (PhotoView) view.findViewById(R.id.image);
        loading = (ProgressBar) view.findViewById(R.id.loading);
        photoView.setMaximumScale(6);
        parent = (ViewGroup) photoView.getParent();
        if(bitmap!=null){
            loading.setVisibility(View.GONE);
            onPalette(Palette.from(bitmap).generate());
            photoView.setImageBitmap(bitmap);
        }
    }
    public void onPalette(Palette palette) {
        if (null != palette) {
            ViewGroup parent = (ViewGroup) photoView.getParent().getParent();
            parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY));
        }
    }
}*/
