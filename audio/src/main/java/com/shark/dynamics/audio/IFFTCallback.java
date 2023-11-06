package com.shark.dynamics.audio;

public interface IFFTCallback {
    void onDataCallback(float[] mc, float[] sgs, float[] wa);
}
