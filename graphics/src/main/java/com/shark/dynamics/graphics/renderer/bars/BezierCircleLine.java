package com.shark.dynamics.graphics.renderer.bars;

import com.shark.dynamics.graphics.renderer.r2d.Circle;
import com.shark.dynamics.graphics.renderer.r2d.bezier.Bezier;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class BezierCircleLine extends IBarsRenderer {

    private Circle mCircle;
    private Bezier mBezier;
    private List<Vector2f> mBezierPoints = new ArrayList<>();

    private int mSliceBorder = 60;
    private float mRadiusScale = 0.9f;

    public BezierCircleLine(Vector2f center, float radius) {
        super();

        mCircle = new Circle(mSliceBorder, radius);
        mCircle.setUpdateLastPoints(true);
        List<Vector2f> points = mCircle.getVertices();

        mBezierPoints.add(new Vector2f(mCircle.getRadius()*2* mRadiusScale, mCircle.getRadius()* mRadiusScale));
        for (int i = 0; i < points.size(); i++) {
            Vector2f p = new Vector2f();
            p.x = points.get(i).x * mRadiusScale;
            p.y = points.get(i).y * mRadiusScale;
            mBezierPoints.add(p);
        }
        mBezierPoints.add(new Vector2f(mCircle.getRadius()*2*mRadiusScale, mCircle.getRadius()*mRadiusScale));

        mBezier = new Bezier(mBezierPoints);
        mBezier.translateTo(center.x - mCircle.getRadius()*mRadiusScale, center.y - mCircle.getRadius()*mRadiusScale, 0);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (mMCArray == null) {
            return;
        }

        fallDownMC(mMCArray, mSliceBorder+5);

        float itemAngel = 360.0f/mSliceBorder;
        float angel = 0;
        List<Vector2f> originVertices = mCircle.getOriginVertices();
        for (int i = 0; i < mSliceBorder; i++) {
            angel = i * itemAngel;
            mBezierPoints.get(i+1).x = (originVertices.get(i).x* mRadiusScale + mDrawMCBars[i] * (float)Math.cos(Math.toRadians(angel)));
            mBezierPoints.get(i+1).y = (originVertices.get(i).y* mRadiusScale + mDrawMCBars[i] * (float)Math.sin(Math.toRadians(angel)));
        }
        float appendScale = 1;
        Vector2f startPoint = new Vector2f(mCircle.getRadius()*2* mRadiusScale + mDrawMCBars[0],
                mCircle.getRadius()* mRadiusScale);
        mBezierPoints.get(0).x = startPoint.x;
        mBezierPoints.get(0).y = startPoint.y;

        for (int i = 0; i < 15; i++) {
            //start from 1 and using sqrt function to smooth height and low
            Vector2f rPoint = mBezierPoints.get(mBezierPoints.size()-2-i);
            float increaseSize = mDrawMCBars[i] * (appendScale / (float) Math.sqrt(i * 1.0f + 1.0f));
            angel = itemAngel * (mSliceBorder - i);

            float xInc = (float) (increaseSize * Math.cos(Math.toRadians(angel)));
            float yInc = (float) (increaseSize * Math.sin(Math.toRadians(angel)));

            rPoint.x = originVertices.get(mSliceBorder-1-i).x * mRadiusScale + xInc;
            rPoint.y = originVertices.get(mSliceBorder-1-i).y * mRadiusScale + yInc;
        }

        mBezierPoints.get(mBezierPoints.size()-1).x = startPoint.x;
        mBezierPoints.get(mBezierPoints.size()-1).y = startPoint.y;

        mBezier.updatePoints(mBezierPoints);

        mBezier.render(delta);
    }


}
