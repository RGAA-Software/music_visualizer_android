package com.shark.dynamics.music.ui.rv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class RView extends RecyclerView {

    public RView(Context context) {
        super(context);
    }

    public RView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public interface OnItemClickListener {
        void onItemClick(View rootView, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View rootView, int position);
    }
}
