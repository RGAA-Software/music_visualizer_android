package com.shark.dynamics.graphics.renderer.visualizer;

import com.shark.dynamics.graphics.renderer.bars.IBarsRenderer;
import com.shark.dynamics.graphics.renderer.bars3d.IBars3DRenderer;

public class IGLVisualizer {

    private static final int kArrayLength = 256;

    protected float[] mMCArray = new float[kArrayLength];
    protected float[] mSGSArray = new float[kArrayLength];

    protected IBarsRenderer mBarsRenderer;
    protected IBars3DRenderer mBars3DRenderer;

    protected boolean mPause;

    protected VisualizerParams mParams = new VisualizerParams();

    protected int mWidth;
    protected int mHeight;

    public IGLVisualizer() {

    }

    public void updateMCArray(float[] data) {
        if (data == null
                || data.length != kArrayLength) {
            return;
        }
        System.arraycopy(data, 0, mMCArray, 0, data.length);
        if (mBarsRenderer != null){
            mBarsRenderer.updateMCArray(mMCArray);
        }
        if (mBars3DRenderer != null) {
            mBars3DRenderer.updateMCArray(mMCArray);
        }
    }

    public void updateSGSArray(float[] data) {
        if (data == null
                || data.length != kArrayLength) {
            return;
        }
        System.arraycopy(data, 0, mSGSArray, 0, data.length);
        if (mBarsRenderer != null){
            mBarsRenderer.updateSGSArray(mSGSArray);
        }
        if (mBars3DRenderer != null) {
            mBars3DRenderer.updateSGSArray(mSGSArray);
        }
    }

    public void onResume() {
        mPause = false;
    }

    public void onPause() {
        mPause = true;
    }

    public void render(float delta) {

    }

    public void setSavedParams(VisualizerParams params) {
        mParams = params;
    }

    public VisualizerParams getSavedInstance() {
        return mParams;
    }

    public void updateSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

}
