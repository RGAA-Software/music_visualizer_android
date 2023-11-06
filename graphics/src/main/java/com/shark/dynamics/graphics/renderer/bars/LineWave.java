package com.shark.dynamics.graphics.renderer.bars;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.r2d.Line;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class LineWave extends IBarsRenderer {

    private Line mLine1;
    private Line mLine2;
    private List<Vector2f> mPoints = new ArrayList<>();
    private List<Vector2f> mReversePoints = new ArrayList<>();

    private int mPointSize;
    private int mOffsetX = 50;
    private int mOffsetY = 50;

    private Vector2f mScreenSize;
    private Vector3f mLineColor = new Vector3f(0xb9*1.0f/255, 0xb9*1.0f/255, 0xFF*1.0f/255);

    public LineWave(int size, int offsetX, int offsetY) {
        mPointSize = size;
        mOffsetX = offsetX;
        mOffsetY = offsetY;
        mScreenSize = Director.getInstance().getDevice().getScreenRealSize();
        for (int i = 0; i < size; i++) {
            mPoints.add(new Vector2f(0f, 0f));
            mReversePoints.add(new Vector2f(0f, 0f));
        }
        mLine1 = new Line(mPoints, mLineColor);
        mLine2 = new Line(mPoints, mLineColor);
    }

    @Override
    public void render(float delta) {
        if (mSGSArray == null || mMCArray == null) {
            return;
        }

        fallDownSGS(mSGSArray, mPointSize);
        fallDownMC(mMCArray, mPointSize);

        updatePoints();

        mLine1.render(delta);
        mLine2.render(delta);
    }

    private void updatePoints() {
        float step = (mScreenSize.x - 2*mOffsetX)/mPointSize;
        for (int i = 0; i < mPointSize; i++) {
            mPoints.get(i).x = i * step + mOffsetX;
            mPoints.get(i).y = mDrawMCBars[i]*2 + mOffsetY;

            mReversePoints.get(mPointSize-1 - i).x = (mPointSize-1-i)*step + mOffsetX;
            mReversePoints.get(mPointSize-1 - i).y = mDrawMCBars[i]*2 + mOffsetY;
        }
        mLine1.updatePoints(mPoints);
        mLine2.updatePoints(mReversePoints);
    }
}
