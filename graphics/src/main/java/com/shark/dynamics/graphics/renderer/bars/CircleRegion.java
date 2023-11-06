package com.shark.dynamics.graphics.renderer.bars;

import com.shark.dynamics.graphics.renderer.r2d.Circle;
import com.shark.dynamics.graphics.renderer.r2d.Polygon;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Vector;

public class CircleRegion extends IBarsRenderer {

    private int mSliceBorder = 60;
    private Circle mCircle1;
    private Polygon mCircle2;

    private Vector2f mCenter;
    private float mRadius;

    public CircleRegion(Vector2f center, float radius) {
        super();

        mCenter = center;
        mRadius = radius;
        mCircle1 = new Circle(mSliceBorder, mRadius);
        mCircle1.setDrawFill(true);
        mCircle1.setDrawStroke(false);
        mCircle1.setIncreaseThreshold(0);
        mCircle1.setUpdateLastPoints(true);
        mCircle1.setDrawPoints(true);
        mCircle1.translateTo(mCenter.x-mRadius, mCenter.y-mRadius, 0);

        mCircle2 = new Polygon(mSliceBorder, mRadius, new Vector3f(0,0,0), 180.0f);
        mCircle2.setDrawFill(true);
        mCircle2.setDrawStroke(false);
        mCircle2.setIncreaseThreshold(0);
        mCircle2.setUpdateLastPoints(true);
        mCircle2.setDrawPoints(true);
        mCircle2.translateTo(mCenter.x-mRadius, mCenter.y-mRadius, 0);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (mSGSArray == null) {
            return;
        }

        fallDownSGS(mSGSArray, mSliceBorder);

        mCircle1.increaseAllRadius(mDrawSGSBars, 1.2f);
        mCircle1.render(delta);

        mCircle2.increaseAllRadius(mDrawSGSBars, 1.2f);
        mCircle2.render(delta);
    }
}
