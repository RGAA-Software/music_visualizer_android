package com.shark.dynamics.graphics.renderer.bars;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.DefaultShader;
import com.shark.dynamics.graphics.renderer.r2d.bezier.Bezier;
import com.shark.dynamics.graphics.shader.Shader;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.opengl.GLES20.glVertexAttribPointer;

public class SecondOrderBezierCircle extends IBarsRenderer {

    protected int mSliceBorder;
    protected float mRadius;

    private Vector2f mCenter;

    private List<Bezier> mBeziers = new ArrayList<>();
    private List<Vector2f> mOriginAxis = new ArrayList<>();


    public SecondOrderBezierCircle(int sliceBorder) {
        this(sliceBorder, 300);
    }

    public SecondOrderBezierCircle(int sliceBorder, float radius) {
        super();
        mShader = new Shader(DefaultShader.sDefaultVertexShader, DefaultShader.sDefaultFragmentShader);
        mSliceBorder = sliceBorder;
        mRadius = radius;
        init();
    }

    public SecondOrderBezierCircle(int sliceBorder, float radius, String vs, String fs) {
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
    }

    public void updateRadius(float radius, float r, float g, float b) {
        mWidth = radius*2;
        mHeight = radius*2;
        mOriginWidth = mWidth;
        mOriginHeight = mHeight;
        mRadius = radius;

        float itemAngel = 360.0f/ mSliceBorder;
        for (int i = 0; i < mSliceBorder; i++) {
            float angel = itemAngel * i;
            float cos = (float) Math.cos(Math.toRadians(angel));
            float sin = (float) Math.sin(Math.toRadians(angel));
            float x = cos * radius + radius;
            float y = sin * radius + radius;
            mOriginAxis.add(new Vector2f(x, y));
        }

        for (int i = 0; i < mSliceBorder; i++) {

            int p0Idx = i;
            int p2Idx = i == mSliceBorder-1 ? 0 : i+1;
            Vector2f p0 = mOriginAxis.get(p0Idx);
            Vector2f p2 = mOriginAxis.get(p2Idx);
            Vector2f p1 = new Vector2f((p0.x+p2.x)/2, (p0.y+p2.y)/2);

            Bezier bezier = new Bezier(p0, p1, p2);
            bezier.translateTo(mCenter.x - mRadius, mCenter.y - mRadius, 0);
            mBeziers.add(bezier);
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

        float itemAngel = 360.0f/ mSliceBorder;
        for (int i = 0; i < mSliceBorder; i++) {
            Bezier bezier = mBeziers.get(i);
            float angel = itemAngel * i;

            float centerCos = (float) Math.cos(Math.toRadians(angel+itemAngel/2));
            float centerSin = (float) Math.sin(Math.toRadians(angel+itemAngel/2));

            float currInc = mDrawMCBars[i];
            float nextInc = mDrawMCBars[i+1];
            int p0Idx = i;
            int p2Idx = i == mSliceBorder-1 ? 0 : i+1;
            Vector2f p0 = mOriginAxis.get(p0Idx);
            Vector2f p2 = mOriginAxis.get(p2Idx);
            Vector2f p1 = new Vector2f((p0.x+p2.x)/2 + currInc*centerCos*1.3f , (p0.y+p2.y)/2+currInc*centerSin*1.3f);

            bezier.updatePoints(p0, p1, p2);
            bezier.render(delta);
        }

    }
}
