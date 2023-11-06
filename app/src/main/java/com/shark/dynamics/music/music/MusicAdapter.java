package com.shark.dynamics.music.music;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.shark.dynamics.music.R;
import com.shark.dynamics.music.ui.rv.RViewAdapter;
import com.shark.dynamics.music.ui.rv.RViewHolder;

import java.util.List;

public class MusicAdapter extends RViewAdapter<Music> {

    private int mSelectedPosition = -1;

    private int mSelectedColor = Color.parseColor("#77dddddd");
    private int mNormalColor = Color.parseColor("#33eeeeee");

    public MusicAdapter(Context context, List<Music> data) {
        super(context, data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_music;
    }

    @Override
    public void bindDataToView(RViewHolder holder, int position) {
        Music music = mData.get(position);
        if (music == null) {
            return;
        }

        View background = holder.findViewById(R.id.id_item_music);
        if (position == mSelectedPosition) {
            background.setBackgroundColor(mSelectedColor);
        } else {
            background.setBackgroundColor(mNormalColor);
        }

        holder.setText(R.id.id_music_position, String.valueOf(position+1));
        holder.setText(R.id.id_music_name, music.title);
        holder.setText(R.id.id_music_author, music.author + " - " + music.album);
        holder.setText(R.id.id_music_duration, formatMusicDuration(music.duration));
    }

    private String formatMusicDuration(long duration) {
        int totalSeconds = (int) (duration/1000);
        int minute = totalSeconds/60;
        int seconds = totalSeconds%60;
        return minute + ":" + seconds;
    }

    public void setSelectedPosition(int position) {
        mSelectedPosition = position;
    }
}
