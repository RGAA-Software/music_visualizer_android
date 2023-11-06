package com.shark.dynamics.graphics.renderer.bars;

import com.shark.dynamics.graphics.renderer.r2d.I2DRenderer;

public class IBarsRenderer extends I2DRenderer {

    protected float[] mMCArray;
    protected float[] mSGSArray;

    protected float[] mDrawMCBars = new float[256];
    protected float[] mDrawSGSBars = new float[256];

    protected boolean mInverseBars;

    protected FilterType mFilterType = FilterType.kMonsterCat;

    protected float mXBarScale = 1.0f;
    protected float mYBarScale = 1.0f;

    public IBarsRenderer() {
        super();
    }

    public IBarsRenderer(String vs, String fs) {
        super(vs, fs);
    }

    public IBarsRenderer(String vs, String gs, String fs) {
        super(vs, gs, fs);
    }


    public void updateMCArray(float[] data) {
        mMCArray = data;
    }

    public void updateSGSArray(float[] data) {
        mSGSArray = data;
    }

    public void fallDownMC(float[] newBars, int drawCount) {
        fallDown(newBars, mDrawMCBars, drawCount);
    }

    public void fallDownSGS(float[] newBars, int drawCount) {
        fallDown(newBars, mDrawSGSBars, drawCount);
    }

    public void fallDown(float[] newBars, float[] targetBars, int drawCount) {
        for (int i = 0; i < drawCount; i++) {
            float diff = newBars[i] - targetBars[i];
            targetBars[i] += diff * 1.0f/3;
            if (targetBars[i] <= 0) {
                targetBars[i] = 0;
            }
        }
    }

    public void setInverseBars(boolean inverse) {
        mInverseBars = inverse;
    }

    public void setFilterType(FilterType type) {
        mFilterType = type;
    }

    public void setXBarScale(float scale) {
        mXBarScale = scale;
    }

    public void setYBarScale(float scale) {
        mYBarScale = scale;
    }

}
