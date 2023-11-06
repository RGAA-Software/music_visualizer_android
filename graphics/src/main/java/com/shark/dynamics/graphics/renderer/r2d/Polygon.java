package com.shark.dynamics.graphics.renderer.r2d;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINE_STRIP;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.DefaultShader;
import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Polygon extends I2DRenderer {

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

    private Vector3f mColor = new Vector3f(1.0f, 1.0f, 1.0f);

    protected Random mRandom = new Random();

    public Polygon(int sliceBorder) {
        this(sliceBorder, 100);
    }

    public Polygon(int sliceBorder, float radius) {
        this(sliceBorder, radius, new Vector3f(1,1,1));
    }

    public Polygon(int sliceBorder, float radius, Vector3f color) {
        mShader = new Shader(DefaultShader.sDefaultVertexShader, DefaultShader.sDefaultFragmentShader);
        mSliceBorder = sliceBorder;
        mRadius = radius;
        mColor = color;
        init();
    }

    public Polygon(int sliceBorder, float radius, Vector3f color, float startAngel) {
        mShader = new Shader(DefaultShader.sDefaultVertexShader, DefaultShader.sDefaultFragmentShader);
        mSliceBorder = sliceBorder;
        mRadius = radius;
        mColor = color;
        mStartAngel = startAngel;
        init();
    }

    public Polygon(int sliceBorder, float radius, String vs, String fs) {
        mShader = new Shader(Director.getInstance().loaderShaderFromAssets(vs), Director.getInstance().loaderShaderFromAssets(fs));
        mSliceBorder = sliceBorder;
        mRadius = radius;
        init();
    }

    private void init() {
        super.initRenderer();
        mVertices = new ArrayList<>();
        updateRadius(mRadius, mColor.x, mColor.y, mColor.z);
        mVertexBuffer = BufferUtil.createFloatBuffer(mVertexArray.length, mVertexArray);

        mVertexArrayHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_DYNAMIC_DRAW);

        int stride = 8 * 4;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3*4);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6*4);
        glEnableVertexAttribArray(2);

        glBindVertexArray(0);
    }

    private void appendVertexLine(int lineIdx, float[] buffer, float[] line) {
        int lineItemCount = 8;
        int idx = lineItemCount * lineIdx;
        buffer[idx + 0] = line[0];
        buffer[idx + 1] = line[1];
        buffer[idx + 2] = line[2];

        buffer[idx + 3] = line[3];
        buffer[idx + 4] = line[4];
        buffer[idx + 5] = line[5];

        buffer[idx + 6] = line[6];
        buffer[idx + 7] = line[7];
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

    public void updateRadius(float radius) {
        updateRadius(radius, 1,1,1);
    }

    public void setDrawPoints(boolean points) {
        mDrawPoints = points;
    }

    public void updateRadius(float radius, float r, float g, float b) {
        mWidth = radius*2;
        mHeight = radius*2;
        mOriginWidth = mWidth;
        mOriginHeight = mHeight;
        mRadius = radius;

        float centerX = mWidth/2;
        float centerY = mHeight/2;

        if (mVertexArray == null) {
            mVertexArray = new float[8 * (mSliceBorder + 1 + 1)];
            mVertexArrayCopy = new float[8 * (mSliceBorder + 1 + 1)];
        }
        appendVertexLine(0, mVertexArray, new float[]{centerX, centerY, 0, r,g,b, 0.5f, 0.5f});

        float itemAngel = 360.0f/ mSliceBorder;
        for (int i = 0; i <= mSliceBorder; i++) {
            float angel = itemAngel * i + mStartAngel;
            float cos = (float) Math.cos(Math.toRadians(angel));
            float sin = (float) Math.sin(Math.toRadians(angel));
            float x = cos * radius + radius;
            float y = sin * radius + radius;
            float z = 0;

            //float r, g, b;
            //r = g = b = 1.0f;

            float u = (cos + 1)/2;
            float v = (sin + 1)/2;

            appendVertexLine(i+1, mVertexArray, new float[]{x,y,z, r,g,b, u,v});

            Vector2f point = new Vector2f(x, y);
            mVertices.add(point);
            mOriginVertices.add(point);
        }

        System.arraycopy(mVertexArray, 0, mVertexArrayCopy, 0, mVertexArray.length);
    }

    public void increaseAllRadius(float[] data, float scale) {
        float itemAngel = 360.0f/ mSliceBorder;

        for (int i = 1; i <= mSliceBorder+1; i++) {
            //discard 0 element in data, start from 1
            float radius = data[i] * scale;
            if (Math.abs(radius) < mIncreaseThreshold) {
                radius = 0;
            }
            float angel = itemAngel * (i-1) + mStartAngel;
            float cos = (float) Math.cos(Math.toRadians(angel));
            float sin = (float) Math.sin(Math.toRadians(angel));
            float x = cos * radius;
            float y = sin * radius;
            mVertexArray[i*8] = mVertexArrayCopy[i*8] + x;
            mVertexArray[i*8+1] = mVertexArrayCopy[i*8+1] + y;

            mVertices.get(i-1).x = mVertexArray[i*8];
            mVertices.get(i-1).y = mVertexArray[i*8+1];
        }

        if (mUpdateLastPoints) {
            // update last Point ...
            float appendScale = 1;
            for (int i = 0; i < 15; i++) {
                //start from 1 and using sqrt function to smooth height and low
                float radius = data[i + 1] * scale * (appendScale / (float) Math.sqrt(i * 1.0f + 1.0f));
                if (Math.abs(radius) < mIncreaseThreshold) {
                    radius = 0;
                }
                float angel = itemAngel * (mSliceBorder - i) + mStartAngel;
                float cos = (float) Math.cos(Math.toRadians(angel));
                float sin = (float) Math.sin(Math.toRadians(angel));
                float x = cos * radius;
                float y = sin * radius;

                mVertexArray[(mSliceBorder + 1 - i) * 8] = mVertexArrayCopy[(mSliceBorder + 1 - i) * 8] + x;
                mVertexArray[(mSliceBorder + 1 - i) * 8 + 1] = mVertexArrayCopy[(mSliceBorder + 1 - i) * 8 + 1] + y;

                mVertices.get(mSliceBorder - i).x = mVertexArray[(mSliceBorder + 1 - i) * 8];
                mVertices.get(mSliceBorder - i).y = mVertexArray[(mSliceBorder + 1 - i) * 8 + 1];
            }
        }
    }

    public float getRadius() {
        return mRadius;
    }

    public int getRandomIndex() {
        return mRandom.nextInt(mSliceBorder)+1;
    }

    public Vector2f getVertex(int idx) {
        float x = mVertexArray[idx*8];
        float y = mVertexArray[idx*8+1];
        return new Vector2f(x, y);
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

        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexArray);
        mVertexBuffer.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_DYNAMIC_DRAW);

        if (mDrawFill) {
            glDrawArrays(GL_TRIANGLE_FAN, 0, mSliceBorder + 2);
        }
        if (mDrawStroke) {
            glLineWidth(mLineWidth);
            glDrawArrays(GL_LINE_STRIP, 1, mSliceBorder+1);
        }

        if (mDrawPoints) {
            glDrawArrays(GL_POINTS, 0, mSliceBorder + 2);
        }
    }
}
