package com.shark.dynamics.graphics.renderer.bars;

import android.util.Log;
import android.util.Pair;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.DefaultShader;
import com.shark.dynamics.graphics.renderer.r2d.Lines;
import com.shark.dynamics.graphics.renderer.r2d.Point;
import com.shark.dynamics.graphics.renderer.r2d.bezier.Bezier;
import com.shark.dynamics.graphics.shader.Shader;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 *  NO USING, JUST FOR TESTING ...
 *  NO USING, JUST FOR TESTING ...
 */
public class TripleOrderBezierCircle extends IBarsRenderer {

    protected int mSliceBorder;
    protected float mRadius;

    private Vector2f mCenter;

    private List<Bezier> mOuterBezier = new ArrayList<>();
    private List<Bezier> mInnerBezier = new ArrayList<>();
    private List<Vector2f> mOriginAxis = new ArrayList<>();
    private List<Vector2f> mOuterPoints = new ArrayList<>();
    private List<Vector2f> mInnerPoints = new ArrayList<>();
    private List<Pair<Vector2f, Vector2f>> mOuterControls = new ArrayList<>();
    private List<Pair<Vector2f, Vector2f>> mInnerControls = new ArrayList<>();
    private Vector2f mOuterStartPoint;
    private Vector2f mInnerStartPoint;
    private float mOuterScale = 0.6f;
    private float mInnerScale = -0.2f;
    private int mInnerBarOffset = 0;
    private float mOuterControlSize = 5;
    private float mInnerControlSize = 4;
    private float mOuterControlOffset = 0;

    private Lines mLines;
    private List<Vector2f> mLinePoints = new ArrayList<>();

    private Point mControlPoints;
    private List<Vector2f> mCtrlPointVertices = new ArrayList<>();

    public TripleOrderBezierCircle(int sliceBorder) {
        this(sliceBorder, 300);
    }

    public TripleOrderBezierCircle(int sliceBorder, float radius) {
        super();
        mShader = new Shader(DefaultShader.sDefaultVertexShader, DefaultShader.sDefaultFragmentShader);
        mSliceBorder = sliceBorder;
        mRadius = radius;
        init();
    }

    public TripleOrderBezierCircle(int sliceBorder, float radius, String vs, String fs) {
        super();
        mShader = new Shader(Director.getInstance().loaderShaderFromAssets(vs), Director.getInstance().loaderShaderFromAssets(fs));
        mSliceBorder = sliceBorder;
        mRadius = radius;
        init();
    }

    private void init() {
        super.initRenderer();
        mCenter = Director.getInstance().getDevice().getRealCenter();
        updateRadius(mRadius, 1,1,1);

        refreshLinePoints();
        mLines = new Lines(mLinePoints);
        mLines.setLineWidth(3);
        mLines.translateTo(mCenter.x-mRadius, mCenter.y-mRadius, 0);
    }

    private void refreshLinePoints() {
        mLinePoints.clear();
        List<Vector2f> innerPoints = mInnerPoints;
        List<Vector2f> outerPoints = mOuterPoints;
        if (mInnerPoints.isEmpty() || mOuterPoints.isEmpty()) {
            Vector2f placeholder = new Vector2f(1,1);
            for (int i = 0; i < mSliceBorder; i++) {
                mLinePoints.add(placeholder);
                mLinePoints.add(placeholder);
            }
        } else {
            for (int i = 0; i < mSliceBorder; i++) {
                mLinePoints.add(innerPoints.get(i));
                mLinePoints.add(outerPoints.get(i));
            }
        }
    }

    public void updateRadius(float radius, float r, float g, float b) {
        mWidth = radius*2;
        mHeight = radius*2;
        mOriginWidth = mWidth;
        mOriginHeight = mHeight;
        mRadius = radius;

        float centerX = mWidth/2;
        float centerY = mHeight/2;

        float itemAngel = 360.0f/ mSliceBorder;
        for (int i = 0; i < mSliceBorder; i++) {
            float angel = itemAngel * i + 90;
            float cos = (float) Math.cos(Math.toRadians(angel));
            float sin = (float) Math.sin(Math.toRadians(angel));
            float x = cos * radius + radius;
            float y = sin * radius + radius;
            mOriginAxis.add(new Vector2f(x, y));
        }

        mCtrlPointVertices.clear();
        for (int i = 0; i < mSliceBorder; i++) {
            float angel = itemAngel * i + itemAngel/2 + 90;
            float cos = (float) Math.cos(Math.toRadians(angel));
            float sin = (float) Math.sin(Math.toRadians(angel));

            int p0Idx = i;
            int p3Idx = i == mSliceBorder-1 ? 0 : i+1;
            Vector2f p0 = mOriginAxis.get(p0Idx);
            Vector2f p3 = mOriginAxis.get(p3Idx);
            Vector2f p1 = p0;
            Vector2f p2 = p3;

            Bezier bezier = new Bezier(p0, p1, p2, p3, 0.8f);
            bezier.translateTo(mCenter.x - mRadius, mCenter.y - mRadius, 0);
            mOuterBezier.add(bezier);

            Bezier innerBezier = new Bezier(p0, p1, p2, p3, 0.2f);
            innerBezier.translateTo(mCenter.x - mRadius, mCenter.y - mRadius, 0);
            mInnerBezier.add(innerBezier);

            //just for malloc memory
            mCtrlPointVertices.add(p0);
            mCtrlPointVertices.add(p0);
            mCtrlPointVertices.add(p3);
            mCtrlPointVertices.add(p3);
        }
        mControlPoints = new Point(mCtrlPointVertices);
        mControlPoints.translateTo(mCenter.x - mRadius, mCenter.y - mRadius, 0);
    }

    public void increaseAllRadius(float[] data, float scale) {
        float itemAngel = 360.0f/ mSliceBorder;

        for (int i = 1; i <= mSliceBorder+1; i++) {
            //discard 0 element in data, start from 1
            float radius = data[i] * scale;
            float angel = itemAngel * (i-1) + 90;
            float cos = (float) Math.cos(Math.toRadians(angel));
            float sin = (float) Math.sin(Math.toRadians(angel));
            float x = cos * radius;
            float y = sin * radius;
        }

        // update last Point ...
        float appendScale = 1;
        for (int i = 0; i < 15; i++) {
            //start from 1 and using sqrt function to smooth height and low
            float radius = data[i+1] * scale * (appendScale/ (float)Math.sqrt(i*1.0f+1.0f));
            float angel = itemAngel * (mSliceBorder - i) + 90;
            float cos = (float) Math.cos(Math.toRadians(angel));
            float sin = (float) Math.sin(Math.toRadians(angel));
            float x = cos * radius;
            float y = sin * radius;


        }
    }

    public float getRadius() {
        return mRadius;
    }

    @Override
    public void render(float delta) {
        if (mMCArray == null || mSGSArray == null) {
            return;
        }
        fallDownMC(mMCArray, 100);
        fallDownSGS(mSGSArray, 100);

        mOuterPoints.clear();
        mInnerPoints.clear();

        float appendScale = 1.0f;
        for (int i = 0; i < mSliceBorder/2; i++) {
            mDrawMCBars[mSliceBorder-1-i] = mDrawMCBars[i];// * (appendScale / (float) Math.sqrt(i * 1.0f + 1.0f));
            mDrawSGSBars[mSliceBorder-1-i] = mDrawSGSBars[i];// * (appendScale / (float) Math.sqrt(i * 1.0f + 1.0f));
        }

        float itemAngel = 360.0f/ mSliceBorder;
        for (int i = 0; i < mSliceBorder; i++) {
            float angel = itemAngel * i + 90;
            float currCos = (float) Math.cos(Math.toRadians(angel));
            float currSin = (float) Math.sin(Math.toRadians(angel));

            float currInc = mDrawSGSBars[i] * mOuterScale + mOuterControlOffset;
            Vector2f p0 = new Vector2f(mOriginAxis.get(i).x + currInc*currCos, mOriginAxis.get(i).y + currInc*currSin);
            mOuterPoints.add(p0);

            currInc = mDrawSGSBars[i+mInnerBarOffset] * mInnerScale;
            p0 = new Vector2f(mOriginAxis.get(i).x + currInc*currCos, mOriginAxis.get(i).y + currInc*currSin);
            mInnerPoints.add(p0);
        }

        getControls(mOuterControls, mOuterPoints);
        getControls(mInnerControls, mInnerPoints);
        if (mOuterControls.isEmpty() || mInnerControls.isEmpty()) {
            return;
        }
        mCtrlPointVertices.clear();

        for (int i = 0; i < mSliceBorder; i++) {
            Bezier outerBezier = mOuterBezier.get(i);
            float angel = itemAngel * i + 90;
            updateBezier(outerBezier, mOuterControls, angel, i, itemAngel, mOuterScale);

            Bezier innerBezier = mInnerBezier.get(i);
            updateBezier(innerBezier, mInnerControls, angel, i, itemAngel, mInnerScale);
        }

        if (mControlPoints != null) {
            //mControlPoints.updatePoints(mCtrlPointVertices);
            //mControlPoints.render(delta);
        }

//        refreshLinePoints();
//        mLines.updatePoints(mLinePoints);
//        mLines.render(delta);
    }

    private void updateBezier(Bezier bezier, List<Pair<Vector2f, Vector2f>> controls, float angel, int i, float itemAngel, float scale) {
        float currCos = (float) Math.cos(Math.toRadians(angel));
        float currSin = (float) Math.sin(Math.toRadians(angel));

        float nextCos = (float) Math.cos(Math.toRadians(angel+itemAngel));
        float nextSin = (float) Math.sin(Math.toRadians(angel+itemAngel));

        float centerCos = (float) Math.cos(Math.toRadians(angel+itemAngel/2));
        float centerSin = (float) Math.sin(Math.toRadians(angel+itemAngel/2));

        int barIdx = i;
        if (controls == mInnerControls) {
            barIdx += mInnerBarOffset;
        }
        float currInc = mDrawSGSBars[barIdx] * scale;
        float nextInc = mDrawSGSBars[barIdx+1] * scale;

        int p0Idx = i;
        int p3Idx = i == mSliceBorder-1 ? 0 : i+1;
        Vector2f p0 = new Vector2f(mOriginAxis.get(p0Idx).x + currInc*currCos, mOriginAxis.get(p0Idx).y + currInc*currSin);
        Vector2f p3 = new Vector2f(mOriginAxis.get(p3Idx).x + nextInc*nextCos, mOriginAxis.get(p3Idx).y + nextInc*nextSin);
        if (i == 0) {
            if (controls == mOuterControls) {
                mOuterStartPoint = p0;
            } else {
                mInnerStartPoint = p0;
            }
        }
        if (i == mSliceBorder-1) {
            if (controls == mOuterControls) {
                p3 = mOuterStartPoint;
            } else {
                p3 = mInnerStartPoint;
            }
        }
        Vector2f p1;
        Vector2f p2;
        if (angel >=0 && angel < 180) {
            p1 = controls.get(p0Idx).first;
            p2 = controls.get(p3Idx).second;
        } else {
            p1 = controls.get(p0Idx).second;
            p2 = controls.get(p3Idx).first;
        }

        bezier.updatePoints(p0, p1, p2, p3);
        bezier.render(0);
        mCtrlPointVertices.add(p1);
        mCtrlPointVertices.add(p2);
    }

    private void getControls(List<Pair<Vector2f, Vector2f>> controls, List<Vector2f> points) {
        if (points.size() <= 0) {
            return;
        }
        controls.clear();
        float itemAngel = 360.0f / mSliceBorder;
        for (int i = 0; i < mSliceBorder; i++) {
            float angel = itemAngel * i + 90;
            float currCos = (float) Math.cos(Math.toRadians(angel));
            float currSin = (float) Math.sin(Math.toRadians(angel));

            Vector2f pW = points.get(i);
            //Vector2f pC = new Vector2f(pW.x - mRadius, pW.y - mRadius);

            float xR = controls == mOuterControls ? mOuterControlSize : mInnerControlSize;
            float xL = -xR;

            float k = - (pW.x - mRadius)/(pW.y - mRadius);
            if (Math.abs(k) > 2.) {
                xR /= 20;
                xL = -xR;
            }

            //Log.i("Bezier",  "k : " + k);

            if (negTooSmall(k)) {
                float deltaX = xR/20;
                Vector2f pR = new Vector2f(pW.x, pW.y + deltaX);
                Vector2f pL = new Vector2f(pW.x, pW.y - deltaX);
                controls.add(new Pair<>(pL, pR));
                //Log.i("Bezier", "too small k : " + k);
            } else {
                float yL = k * (xL) + pW.y;
                Vector2f pL = new Vector2f(pW.x + xL, yL);

                float yR = k * (xR) + pW.y;
                Vector2f pR = new Vector2f(pW.x + xR, yR);

                controls.add(new Pair<>(pL, pR));
            }
        }
        Log.i("Bezier",  "\n\n");
    }

    private boolean negTooSmall(float num) {
        return Float.isInfinite(num);
    }

}
