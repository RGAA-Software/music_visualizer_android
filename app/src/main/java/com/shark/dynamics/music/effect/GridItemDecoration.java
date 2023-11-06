package com.shark.dynamics.music.effect;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int mSC;
    private int mSpace;

    public GridItemDecoration(Context context, int spanCount) {
        this.spanCount = spanCount;
        mSC = context.getResources().getDisplayMetrics().widthPixels;
        float density = Resources.getSystem().getDisplayMetrics().density;
        float itemWidth = density * 145;
        mSpace = (int) (mSC - 2*itemWidth) / 3;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int index = parent.getChildAdapterPosition(view);
        int total = parent.getChildCount();

        if (index % spanCount == 1) {
            outRect.left = mSpace/2;
            outRect.right = mSpace;
            outRect.top = mSpace/2;
            outRect.bottom = mSpace/2;
        } else {
            outRect.right = mSpace/2;
            outRect.top = mSpace/2;
            outRect.left = mSpace;
            outRect.bottom = mSpace/2;
        }
    }
}
