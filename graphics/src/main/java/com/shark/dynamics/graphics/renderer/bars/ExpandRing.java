package com.shark.dynamics.graphics.renderer.bars;

import com.shark.dynamics.graphics.renderer.r2d.Circle;
import com.shark.dynamics.graphics.shader.Shader;

import org.joml.Vector2f;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glEnable;

public class ExpandRing {

    private Circle mOuterCircle;
    private Circle mTipCircle;

    private float mOutRadius;
    private float mTipRadius;

    private Vector2f mCenter;
    private int mStartIdx;
    private float mRotateAngel;
    private float mOuterCircleRadius;

    private long mLifeTime;
    private long mRunningTime;

    public ExpandRing(Vector2f center, float outRadius, float tipRadius) {
        mCenter = center;
        mOutRadius = outRadius;
        mTipRadius = tipRadius;

        mOuterCircle = new Circle(80, mOutRadius, "shader/base_2d_vs.glsl", "shader/alpha_fs.glsl");
        mOuterCircle.updateRadius(mOutRadius);
        mOuterCircle.setLineWidth(5);
        mOuterCircle.setDrawStroke(true);
        mOuterCircle.setDrawFill(false);
        mOuterCircle.translateTo(center.x-mOuterCircle.getRadius(), center.y-mOuterCircle.getRadius(), 0);
        mOuterCircleRadius = mOuterCircle.getRadius();

        mTipCircle = new Circle(20, mOutRadius, "shader/base_2d_vs.glsl", "shader/alpha_fs.glsl");
        mTipCircle.updateRadius(mTipRadius);
        mStartIdx = mOuterCircle.getRandomIndex();

        mRotateAngel = mStartIdx* mOuterCircle.getItemAngel();
        Vector2f randomVertex = mOuterCircle.getVertex(mStartIdx);

        mTipCircle.translateTo(mCenter.x-mOuterCircleRadius+randomVertex.x-mTipRadius,
                mCenter.y-mOuterCircleRadius+randomVertex.y-mTipRadius, 0);

        mTipCircle.setDrawStroke(false);
        mTipCircle.setDrawFill(true);

        mLifeTime = 1000 * 3;
    }

    public void render(float delta) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        mRunningTime += delta*1000;
        mOuterCircleRadius += delta * 100;

        float alpha = 1.0f-mRunningTime*1.0f/mLifeTime - 0.2f;

        Shader shader = mOuterCircle.getShader();
        shader.use();
        shader.setUniformFloat("alpha", alpha);
        mOuterCircle.updateRadius(mOuterCircleRadius);
        mOuterCircle.translateTo(mCenter.x-mOuterCircleRadius, mCenter.y-mOuterCircleRadius, 0);
        mOuterCircle.render(delta);

        mRotateAngel += delta*35;
        mOuterCircleRadius = mOuterCircle.getRadius();
        float x = (float) (mOuterCircleRadius * Math.cos(Math.toRadians(mRotateAngel))) + mOuterCircleRadius;
        float y = (float) (mOuterCircleRadius * Math.sin(Math.toRadians(mRotateAngel))) + mOuterCircleRadius;
        mTipCircle.translateTo(mCenter.x-mOuterCircleRadius+x-mTipRadius,
                mCenter.y-mOuterCircleRadius+y-mTipRadius, 0);

        Shader tipShader = mTipCircle.getShader();
        tipShader.use();
        tipShader.setUniformFloat("alpha", alpha);
        mTipCircle.render(delta);
    }

    public boolean isAlive() {
        return mRunningTime <= mLifeTime;
    }

}
