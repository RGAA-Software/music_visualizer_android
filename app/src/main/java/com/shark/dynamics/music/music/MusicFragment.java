package com.shark.dynamics.music.music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shark.dynamics.audio.AudioFFTMonitor;
import com.shark.dynamics.audio.SpectrumView;
import com.shark.dynamics.audio.player.IMusicPlayerListener;
import com.shark.dynamics.audio.player.SMusicPlayer;
import com.shark.dynamics.basic.thread.UI;
import com.shark.dynamics.basic.thread.Worker;
import com.shark.dynamics.music.R;
import com.shark.dynamics.music.test.TestSpectrumActivity;
import com.shark.dynamics.music.ui.BottomPlayView;
import com.shark.dynamics.music.ui.LoadingDialog;
import com.shark.dynamics.music.ui.fragment.BaseFragment;
import com.shark.dynamics.music.ui.rv.RView;
import com.shark.dynamics.music.util.WallpaperUtil;
import com.shark.dynamics.music.wallpaper.DynamicWallpaper;

import java.util.ArrayList;
import java.util.List;

public class MusicFragment extends BaseFragment {

    private LoadingDialog mLoadingDialog;

    private RecyclerView mMusicList;
    private List<Music> mMusics;
    private MusicAdapter mAdapter;
    private SpectrumView mSpectrumView;
    private SMusicPlayer mPlayer;

    private BottomPlayView mPlayView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayer = new SMusicPlayer();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLoadingDialog = new LoadingDialog(view.getContext());

        debug(view);

        mMusicList = view.findViewById(R.id.id_music_list);
        mMusicList.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mMusics = new ArrayList<>();
        mAdapter = new MusicAdapter(view.getContext(), mMusics);
        mMusicList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((rootView, position) -> {
            Music music = mMusics.get(position);
            if (music == null) {
                return;
            }

            mAdapter.setSelectedPosition(position);
            mAdapter.notifyDataSetChanged();

            mPlayer.setFilePath(music.path);
            mPlayer.setLoop(true);
            mPlayer.restart();
        });

        mPlayer.setPlayerListener(new IMusicPlayerListener() {
            @Override
            public void onPlaying(int duration, int pos) {
                mPlayView.setDuration(duration);
                mPlayView.setCurrentPos(pos);
            }

            @Override
            public void onCompleted() {

            }
        });

        mSpectrumView = view.findViewById(R.id.id_spectrum);

        loadMusic();
    }

    @Override
    public void onResume() {
        super.onResume();
        AudioFFTMonitor.getInstance().setFFTCallback((mc, sgs, wa) -> {
            mSpectrumView.updateValue(mc);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSpectrumView != null) {
            mSpectrumView.dispose();
        }
        if (mPlayer != null) {
            mPlayer.destroy();
        }
    }

    private void debug(View view) {
        view.findViewById(R.id.id_canvas_spectrum)
                .setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), TestSpectrumActivity.class);
                    getActivity().startActivity(intent);
                });

        view.findViewById(R.id.id_set_wallpaper)
                .setOnClickListener(v -> {
                    WallpaperUtil.clearWallpaper(getActivity());
                    WallpaperUtil.startDynamicWallpaper(getActivity(), DynamicWallpaper.class);
                });

        view.findViewById(R.id.id_load)
                .setOnClickListener(v -> {
                    mLoadingDialog.show();
                });
    }

    private void loadMusic() {
        Worker.getInstance().postLightTask(() -> {
            Context context = getContext();
            if (context == null) {
                return;
            }
            List<Music> musics = MusicLoader.getInstance().loadMusicData(context);
            if (musics == null || musics.isEmpty()) {
                return;
            }

            mMusics.removeAll(musics);
            mMusics.addAll(musics);
            UI.getInstance().post(() -> {
                mAdapter.notifyDataSetChanged();
            });
        });
    }

    public void setPlayView(BottomPlayView playView) {
        mPlayView = playView;
    }
}
