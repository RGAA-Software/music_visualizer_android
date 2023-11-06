package com.shark.dynamics.graphics.renderer.r2d;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;

public class Ring extends I2DRenderer {

    protected int mSliceBorder;
    protected boolean mDrawStroke = true;
    protected int mLineWidth = 6;
    protected boolean mDrawFill;
    protected float mRadius;
    protected boolean mDrawPoints = false;

    protected int mVertexArrayHandle;
    protected float[] mVertexArray;
    protected float[] mVertexArrayCopy;
    protected FloatBuffer mVertexBuffer;
    protected List<Vector2f> mVertices;
    private List<Vector2f> mOriginVertices = new ArrayList<>();

    protected float mIncreaseThreshold = 0;
    protected boolean mUpdateLastPoints = true;

    private float mStartAngel = 0;

    private Vector3f mOuterColor = new Vector3f(1.0f, 1.0f, 1.0f);
    private Vector3f mInnerColor = new Vector3f(0.5f, 0.5f, 0.5f);

    private float mRingWidth = 5;

    private boolean mEnableAlpha = true;
    private boolean mAlphaForward = true;

    protected Random mRandom = new Random();

    public Ring(int sliceBorder, float radius, float ringWidth, Vector3f innerColor, Vector3f outerColor) {
        mShader = new Shader(Director.getInstance().loaderShaderFromAssets("shader/alpha_in_position_vs.glsl"),
                Director.getInstance().loaderShaderFromAssets("shader/ring_color_alpha_fs.glsl"));
        mSliceBorder = sliceBorder;
        mRadius = radius;
        mRingWidth = ringWidth;
        mInnerColor = innerColor;
        mOuterColor = outerColor;
        init();
    }

    private void init() {
        super.initRenderer();
        mVertices = new ArrayList<>();
        updateRadius(mRadius);
        mVertexBuffer = BufferUtil.createFloatBuffer(mVertexArray.length, mVertexArray);

        mVertexArrayHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_DYNAMIC_DRAW);

        int stride = 9 * 4;

        glVertexAttribPointer(3, 4, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 4*4);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 7*4);
        glEnableVertexAttribArray(2);

        glBindVertexArray(0);
    }

    private void appendVertexLine(int lineIdx, float[] buffer, float[] line) {
        int lineItemCount = 9;
        int idx = lineItemCount * lineIdx;

        buffer[idx + 0] = line[0];
        buffer[idx + 1] = line[1];
        buffer[idx + 2] = line[2];
        buffer[idx + 3] = line[3];

        buffer[idx + 4] = line[4];
        buffer[idx + 5] = line[5];
        buffer[idx + 6] = line[6];

        buffer[idx + 7] = line[7];
        buffer[idx + 8] = line[8];
    }

    public void setDrawStroke(boolean stroke) {
        mDrawStroke = stroke;
    }

    public void setLineWidth(int width) {
        mLineWidth = width;
    }

    public void setDrawFill(boolean fill) {
        mDrawFill = fill;
    }

    public void setDrawPoints(boolean points) {
        mDrawPoints = points;
    }

    public void setEnableAlpha(boolean enable) {
        mEnableAlpha = enable;
    }

    public void setAlphaForward(boolean forward) {
        mAlphaForward = forward;
    }

    public void updateRadius(float radius) {
        mWidth = radius*2;
        mHeight = radius*2;
        mOriginWidth = mWidth;
        mOriginHeight = mHeight;
        mRadius = radius;

        float centerX = mWidth/2;
        float centerY = mHeight/2;

        if (mVertexArray == null) {
            mVertexArray = new float[9 * (mSliceBorder+1) * 2];
            mVertexArrayCopy = new float[9 * (mSliceBorder+1) * 2];
        }

        float outerRadius = radius;
        float innerRadius = radius - mRingWidth;

        float itemAngel = 360.0f/ mSliceBorder;
        for (int i = 0; i <= mSliceBorder*2; i += 2) {
            float angel = itemAngel * i + mStartAngel;
            CirclePoint outerPoint = calculateCirclePoint(outerRadius, angel, mOuterColor, 0, 1.0f);
            appendVertexLine(i, mVertexArray,
                    new float[]{outerPoint.x, outerPoint.y, outerPoint.z, outerPoint.alpha,
                            outerPoint.r, outerPoint.g, outerPoint.b,
                            outerPoint.u, outerPoint.v,});

            CirclePoint innerPoint = calculateCirclePoint(innerRadius, angel, mInnerColor, mRingWidth, 0.1f);
            appendVertexLine(i+1, mVertexArray,
                    new float[]{innerPoint.x, innerPoint.y, innerPoint.z, outerPoint.alpha,
                            innerPoint.r, innerPoint.g, innerPoint.b,
                            innerPoint.u, innerPoint.v});

            Vector2f point = new Vector2f(outerPoint.x, outerPoint.y);
            mVertices.add(point);
            mOriginVertices.add(point);
        }

        System.arraycopy(mVertexArray, 0, mVertexArrayCopy, 0, mVertexArray.length);
    }

    private CirclePoint calculateCirclePoint(float radius, float angel, Vector3f color, float delta, float alpha) {
        float cos = (float) Math.cos(Math.toRadians(angel));
        float sin = (float) Math.sin(Math.toRadians(angel));
        float x = cos * radius + radius + delta;
        float y = sin * radius + radius + delta;
        float z = 0;

        float u = (cos + 1)/2;
        float v = (sin + 1)/2;

        return new CirclePoint(x, y, z, color.x, color.y, color.z, u, v, alpha);
    }

    public float getRadius() {
        return mRadius;
    }

    public float getItemAngel() {
        return 360.0f/ mSliceBorder;
    }

    public void setIncreaseThreshold(float threshold) {
        mIncreaseThreshold = threshold;
    }

    public void setUpdateLastPoints(boolean update) {
        mUpdateLastPoints = update;
    }

    public List<Vector2f> getVertices() {
        return mVertices;
    }

    public List<Vector2f> getOriginVertices() {
        return mOriginVertices;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        float centerX = mTranslate.x + mRadius;
        float centerY = mTranslate.y + mRadius;

        mShader.setUniformInt("enableAlpha", mEnableAlpha ? 1 : 0);
        mShader.setUniformInt("alphaForward", mAlphaForward ? 1 : 0);
        mShader.setUniformVec2("center", centerX, centerY);
        mShader.setUniformFloat("innerRadius", mRadius-mRingWidth);
        mShader.setUniformFloat("ringWidth", mRingWidth);

        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexArray);
        mVertexBuffer.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_DYNAMIC_DRAW);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, (mSliceBorder+1) * 2);

        glDisable(GL_BLEND);
    }


    static class CirclePoint {
        public CirclePoint(float x, float y, float z, float r, float g, float b, float u, float v, float alpha) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.r = r;
            this.g = g;
            this.b = b;
            this.u = u;
            this.v = v;
            this.alpha = alpha;
        }
        float x;
        float y;
        float z;

        float r;
        float g;
        float b;

        float u;
        float v;

        float alpha;
    }
}
