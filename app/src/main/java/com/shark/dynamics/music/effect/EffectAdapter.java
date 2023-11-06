package com.shark.dynamics.music.effect;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shark.dynamics.music.R;
import com.shark.dynamics.music.ui.rv.RViewAdapter;
import com.shark.dynamics.music.ui.rv.RViewHolder;

import java.util.List;

public class EffectAdapter extends RViewAdapter<EffectItem> {

    private static final String TAG = "Effect";

    public EffectAdapter(Context context, List<EffectItem> data) {
        super(context, data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_effect;
    }

    @Override
    public void bindDataToView(RViewHolder holder, int position) {
        ImageView coverView = holder.findViewById(R.id.id_wallpaper_cover);
        TextView wallpaperNameView = holder.findViewById(R.id.id_wallpaper_name);

        EffectItem item = mData.get(position);
        if (item == null) {
            return;
        }

        wallpaperNameView.setText(item.name);

        Glide.with(mContext)
                .load("file:///android_asset/" + item.cover)
                .into(coverView);

    }
}
