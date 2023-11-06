package com.shark.dynamics.audio;

import android.content.Context;

import com.app.spectrum.Spectrum;

public class AudioFFTMonitor {

    private static AudioFFTMonitor sInstance = new AudioFFTMonitor();

    private float[] mMCArray = new float[256];
    private float[] mSGSArray = new float[256];
    private float[] mWAArray = new float[256];

    private VisualizerWrapper mVisualizerWrapper;
    private IFFTCallback mFFTCallback;

    public static AudioFFTMonitor getInstance() {
        return sInstance;
    }

    private AudioFFTMonitor() {
    }

    public void init(Context context) {
        mVisualizerWrapper = new VisualizerWrapper(context,
                0, new VisualizerWrapper.OnFftDataCaptureListener() {
            @Override
            public void onFftDataCapture(byte[] fft) {
                for (int i = 0; i < mMCArray.length; i+=2) {
                    float value = (float) Math.hypot(fft[i], fft[i+1]) + 2;
                    mMCArray[i/2] = (Math.abs(value));
                    mSGSArray[i/2] = mMCArray[i];
                }

                Spectrum.filterMonsterCat(mMCArray);
                System.arraycopy(Spectrum.sMCArray,0, mMCArray, 0, mMCArray.length);

                if (mFFTCallback != null) {
                    mFFTCallback.onDataCallback(mMCArray, mSGSArray, mWAArray);
                }
            }
        });

        mVisualizerWrapper.setEnabled(true);
    }

    public void setFFTCallback(IFFTCallback callback) {
        mFFTCallback = callback;
    }

    public void enable() {
        //mVisualizerWrapper.setEnabled(true);
    }

    public void disable() {
        //mVisualizerWrapper.setEnabled(false);
    }

    public void dispose() {
        mVisualizerWrapper.release();
    }

}
