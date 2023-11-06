package com.shark.dynamics.music.ui.rv;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RViewHolder extends RecyclerView.ViewHolder {

    private View mRootView;
    public Object tag;
    private RView.OnItemClickListener mItemClickListener;

    public RViewHolder(View itemView) {
        super(itemView);
        mRootView = itemView;
    }

    public View getRootView(){
        return mRootView;
    }

    public <V extends View> V getViewById(int resId){
        if(mRootView == null) return null;
        return (V) mRootView.findViewById(resId);
    }

    public void setText(int resId,CharSequence content){
        View view = getViewById(resId);
        if(view != null && view instanceof TextView){
            ((TextView)view).setText(content);
        }
    }

    public void setOnClickListener(int resId, View.OnClickListener listener) {
        View view = getViewById(resId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    @SuppressLint("NewApi")
    public void setBackground(int resId,Drawable drawable){

        View view = getViewById(resId);
        if(view == null) return;

        view.setBackground(drawable);
    }

    public void setImageBitmap(int resId,Bitmap bm){

        View view = getViewById(resId);
        if(view != null && view instanceof ImageView){
            ((ImageView)view).setImageBitmap(bm);
        }
    }

    public void setImageDrawable(int resId,Drawable drawable){

        View view = getViewById(resId);
        if(view != null && view instanceof ImageView){
            ((ImageView)view).setImageDrawable(drawable);
        }
    }

    public void hideView(int resId) {
        View view = getViewById(resId);
        if (view == null) return;
        view.setVisibility(View.GONE);
    }

    public void showView(int resId) {
        View view = getViewById(resId);
        if (view == null) return;
        view.setVisibility(View.VISIBLE);
    }

    public <T extends View> T findViewById(int resId) {
        return (T)itemView.findViewById(resId);
    }
}
