package com.shark.dynamics.audio.player;

public interface IMusicPlayerListener {

    void onPlaying(int duration, int pos);
    void onCompleted();

}
