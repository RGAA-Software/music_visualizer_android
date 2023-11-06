package com.shark.dynamics.music.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shark.dynamics.music.R;

public class BottomBar {

    private View mRoot;

    private ViewGroup mEffect;
    private ImageView mEffectIcon;
    private TextView mEffectText;

    private ViewGroup mMusic;
    private ImageView mMusicIcon;
    private TextView mMusicText;

    private BottomPlayView mPlayView;

    private Context mContext;

    private int mCurrentSelected = 0;

    private IBottomBarClickListener mClickListener;

    public BottomBar(View view) {
        mContext = view.getContext();
        mRoot = view;

        mEffect = mRoot.findViewById(R.id.id_bottom_bar_effect_parent);
        mEffectIcon = mRoot.findViewById(R.id.id_bottom_bar_effect_icon);
        mEffectText = mRoot.findViewById(R.id.id_bottom_bar_effect_text);

        mMusic = mRoot.findViewById(R.id.id_bottom_bar_music_parent);
        mMusicIcon = mRoot.findViewById(R.id.id_bottom_bar_music_icon);
        mMusicText = mRoot.findViewById(R.id.id_bottom_bar_music_text);

        mPlayView = mRoot.findViewById(R.id.id_play_view_btn);

        mEffect.setOnClickListener(v -> {
            select(0);
        });

        mMusic.setOnClickListener(v -> {
            select(1);
        });
    }

    public void setOnBarClickListener(IBottomBarClickListener listener) {
        mClickListener = listener;
    }

    public void select(int index) {
        mCurrentSelected = index;
        int mainColor = mContext.getResources().getColor(R.color.main_color);
        int mainTextColor = mContext.getResources().getColor(R.color.main_text_color);

        if (index == 0) {
            mEffectIcon.getDrawable().setTint(mainColor);
            mEffectText.setTextColor(mainColor);

            mMusicIcon.getDrawable().setTint(mainTextColor);
            mMusicText.setTextColor(mainTextColor);
        } else if (index == 1) {
            mMusicIcon.getDrawable().setTint(mainColor);
            mMusicText.setTextColor(mainColor);

            mEffectIcon.getDrawable().setTint(mainTextColor);
            mEffectText.setTextColor(mainTextColor);
        }

        if (mClickListener != null) {
            mClickListener.onBarClicked(mCurrentSelected);
        }
    }

    public void startPlayViewRotate() {
        mPlayView.startRotate();
    }

    public void stopPlayViewRotate() {
        mPlayView.stopRotate();
    }

    public BottomPlayView getPlayView() {
        return mPlayView;
    }

    public static interface IBottomBarClickListener {
        void onBarClicked(int idx);
    }

}
