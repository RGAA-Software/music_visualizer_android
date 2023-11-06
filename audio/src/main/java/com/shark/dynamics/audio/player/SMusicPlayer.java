package com.shark.dynamics.audio.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;


import com.shark.dynamics.audio.thread.SingleTask;
import com.shark.dynamics.audio.thread.SingleThread;

import java.io.File;

public class SMusicPlayer {

    private static final String TAG = "Player";
    private MediaPlayer mPlayer;
    private boolean mIsPlaying;
    private IMusicPlayerListener mPlayerListener;
    private boolean mDestroyed = false;
    private String mMusicPath;
    private boolean mListenerInit = false;
    private boolean mIsLoop;

    public SMusicPlayer() {
        mPlayer = new MediaPlayer();
    }

    public void setPlayerListener(IMusicPlayerListener listener) {
        mPlayerListener = listener;
    }

    public boolean setFilePath(String path) {
        if (TextUtils.isEmpty(path) || !new File(path).exists()) {
            return false;
        }
        mMusicPath = path;
        try {
            mPlayer.setDataSource(path);
            mPlayer.prepare();

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mPlayerListener != null) {
                        mPlayerListener.onCompleted();
                    }
                    if (mIsLoop) {
                        restart();
                    }
                }
            });

            if (!mListenerInit) {
                mListenerInit = true;
                new SingleThread<SingleTask>(new SingleTask() {
                    @Override
                    public void exec() {
                        while (!mDestroyed) {
                            SystemClock.sleep(500);
                            if (mDestroyed) {
                                break;
                            }
                            try {
                                int pos = mPlayer.getCurrentPosition();
                                int duration = mPlayer.getDuration();
                                if (mPlayerListener != null) {
                                    if (pos >= duration) {
                                        if (isPlaying()) {
                                            mPlayerListener.onCompleted();
                                        }
                                    } else {
                                        mPlayerListener.onPlaying(duration, pos);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "file not found : " + e.getMessage());
        }
        return false;
    }

    public void setLoop(boolean loop) {
        mIsLoop = loop;
    }

    public boolean isLoop() {
        return mIsLoop;
    }

    public void start() {
        mPlayer.start();
    }

    public void pause() {
        mPlayer.pause();
    }

    public void stop() {
        mPlayer.stop();
    }

    public void destroy() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.release();
        mDestroyed = true;
    }

    public void setPlaying(boolean playing) {
        mIsPlaying = playing;
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public void restart() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mIsPlaying = true;
        mPlayer.reset();
        if (!setFilePath(mMusicPath)) {
            return;
        }
        mPlayer.start();
        if (mPlayerListener != null) {
            mPlayerListener.onPlaying(mPlayer.getDuration(), 0);
        }
    }

    public int getDuration() {
        try {
            return mPlayer.getDuration();
        } catch (Exception e) {
            return -1;
        }
    }


}
