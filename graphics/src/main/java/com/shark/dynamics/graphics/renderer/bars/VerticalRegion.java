package com.shark.dynamics.graphics.renderer.bars;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import org.joml.Vector3f;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;

public class VerticalRegion extends IBarsRenderer {

    private int mBarCount;

    private Vector3f mBottomColor = new Vector3f(1.0f, 0.6f, 0.5f);
    private Vector3f mTopColor = new Vector3f(0.2f, 0.5f, 0.8f);

    private int mVertexArrayHandle;

    private FloatBuffer mVertexBuffer;
    private float[] mVertexArray;

    private int mBarWidth;
    private int mBarGap;

    private int mXPos;
    private int mYPos;

    public VerticalRegion(String vs, String fs) {
        super(vs, fs);
    }

    @Override
    protected void initRenderer() {
        super.initRenderer();

        initData();
    }

    private void initData() {
        mBarCount = 50;
        mBottomColor = new Vector3f(1.0f, 0.6f, 0.5f);
        mTopColor = new Vector3f(0.2f, 0.5f, 0.8f);
        mBarWidth = Director.getInstance()
                .getDevice().getPixelSize(3);
        mBarGap = Director.getInstance()
                .getDevice().getPixelSize(5);

        mXPos = 100;
        mYPos = 100;

        mVertexArray = new float[6*2*mBarCount];
        updateVertexArray(mVertexArray);
        mVertexBuffer = BufferUtil.createFloatBuffer(mVertexArray.length, mVertexArray);

        int stride = 6 * 4;

        mVertexArrayHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3*4);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    private void updateVertexArray(float[] data) {
        for (int i = 0; i < mBarCount; i++) {
            mVertexArray[i * 12 + 0] =  mXPos + i*mBarGap;
            mVertexArray[i * 12 + 1] =  mYPos;
            mVertexArray[i * 12 + 2] = 0;

            mVertexArray[i * 12 + 3] = mBottomColor.x;
            mVertexArray[i * 12 + 4] = mBottomColor.y;
            mVertexArray[i * 12 + 5] = mBottomColor.z;

            mVertexArray[i * 12 + 6] =  mXPos + i*mBarGap;
            mVertexArray[i * 12 + 7] =  mYPos + data[i] * (mInverseBars ? -1 : 1);
            mVertexArray[i * 12 + 8] = 0;

            mVertexArray[i * 12 + 9] = mTopColor.x;
            mVertexArray[i * 12 + 10] = mTopColor.y;
            mVertexArray[i * 12 + 11] = mTopColor.z;
        }
    }

    private void drawBars() {
        if (mMCArray == null || mSGSArray == null) {
            return;
        }

        if (mFilterType == FilterType.kMonsterCat) {
            fallDown(mMCArray, mDrawMCBars, mBarCount);
            updateVertexArray(mDrawMCBars);
        } else if (mFilterType == FilterType.kSGS) {
            fallDown(mSGSArray, mDrawSGSBars, mBarCount);
            updateVertexArray(mDrawSGSBars);
        }

        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexArray);
        mVertexBuffer.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_STATIC_DRAW);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, mBarCount*2);
        glBindVertexArray(0);
    }

    public int getTotalWidth() {
        return (mBarCount-1) * mBarGap;
    }

    /**
     * left-bottom
     */
    public void setPosition(int x, int y) {
        mXPos = x;
        mYPos = y;
    }

    public void setXPosition(int x) {
        mXPos = x;
    }

    public void setYPosition(int y) {
        mYPos = y;
    }

    public void setCenterHorizontal() {
        int sc = (int) Director.getInstance().getDevice().getScreenRealSize().x;
        mXPos = (sc - getTotalWidth())/2;
    }

    public void setCenterVertical() {
        int sc = (int) Director.getInstance().getDevice().getScreenRealSize().y;
        mYPos = sc/2;
    }

    public int getLeftBottomX() {
        return mXPos;
    }

    public int getLeftBottomY() {
        return mYPos;
    }

    @Override
    public boolean isCustomModelMatrix() {
        return false;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        drawBars();
    }
}
