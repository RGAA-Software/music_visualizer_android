package com.shark.dynamics.graphics.renderer.bars;

import com.shark.dynamics.graphics.renderer.r2d.Circle;
import com.shark.dynamics.graphics.renderer.r2d.Lines;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class DoubleLineCircle extends IBarsRenderer {

    private Vector2f mCenter;

    private Circle mOuterCircle;
    private Circle mInnerCircle;
    private Lines mLines;
    private List<Vector2f> mLinePoints;

    private int mSliceBorder;
    private float mRadius;


    public DoubleLineCircle(Vector2f center, float radius) {
        super();
        mLinePoints = new ArrayList<>();
        mCenter = center;
        mRadius = radius;

        mSliceBorder = 65;
        mOuterCircle = new Circle(mSliceBorder, mRadius);
        mOuterCircle.setLineWidth(3);
        mOuterCircle.translateTo(mCenter.x-mRadius, mCenter.y-mRadius, 0);
        mOuterCircle.setIncreaseThreshold(5);

        mInnerCircle = new Circle(mSliceBorder, mRadius);
        mInnerCircle.setLineWidth(3);
        mInnerCircle.translateTo(mCenter.x-mRadius, mCenter.y-mRadius, 0);
        mInnerCircle.setIncreaseThreshold(5f);

        refreshLinePoints();
        mLines = new Lines(mLinePoints);
        mLines.setLineWidth(3);
        mLines.translateTo(mCenter.x-mRadius, mCenter.y-mRadius, 0);
    }

    private void refreshLinePoints() {
        mLinePoints.clear();
        List<Vector2f> innerPoints = mInnerCircle.getVertices();
        List<Vector2f> outerPoints = mOuterCircle.getVertices();
        for (int i = 0; i < mSliceBorder; i++) {
            mLinePoints.add(innerPoints.get(i));
            mLinePoints.add(outerPoints.get(i));
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (mSGSArray == null || mMCArray == null) {
            return;
        }

        fallDownSGS(mSGSArray, mSliceBorder);

        mOuterCircle.increaseAllRadius(mDrawSGSBars, 0.35f);
        mOuterCircle.render(delta);

        mInnerCircle.increaseAllRadius(mDrawSGSBars, -0.35f);
        mInnerCircle.render(delta);

        refreshLinePoints();
        mLines.updatePoints(mLinePoints);
        mLines.render(delta);
    }
}
