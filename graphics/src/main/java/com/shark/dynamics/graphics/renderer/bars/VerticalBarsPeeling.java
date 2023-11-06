package com.shark.dynamics.graphics.renderer.bars;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import org.joml.Vector3f;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;

public class VerticalBarsPeeling extends IBarsRenderer {

    private int mBarCount = 50;

    protected Vector3f mBottomColor = new Vector3f(1.0f, 0.6f, 0.5f);
    protected Vector3f mTopColor = new Vector3f(0.2f, 0.5f, 0.8f);

    protected int mVertexArrayHandle;
    private float[] mVertexArray;
    protected FloatBuffer mVertexBuffer;

    protected int mBarWidth;
    protected int mBarGap;

    protected int mXPos;
    protected int mYPos;

    public VerticalBarsPeeling(String vs, String fs) {
        super(vs, fs);

        initData();
    }


    private void initData() {
        mBarWidth = Director.getInstance()
                .getDevice().getPixelSize(5);
        mBarGap = Director.getInstance()
                .getDevice().getPixelSize(1);

        mVertexArray = new float[6*6*mBarCount];


        int stride = 6 * 4;

        mVertexBuffer = BufferUtil.createFloatBuffer(mVertexArray.length, mVertexArray);

        mVertexArrayHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3*4);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    private void drawBars() {
        if (mMCArray == null) {
            return;
        }

        fallDownMC(mMCArray, mBarCount);
        fallDownSGS(mSGSArray, mBarCount);

        for (int i = 0; i < mBarCount; i++) {
            float th = mDrawSGSBars[i]*2;
            float nextHeight = mDrawSGSBars[i+1]*2;
            float nextOffsetHeight = nextHeight + mYPos;

            float x = i * (mBarWidth + mBarGap) + mXPos;
            float y = mYPos + th;
            float[] vertices = {
                x, mYPos, 1.0f, mBottomColor.x, mBottomColor.y, mBottomColor.z,
                x+mBarWidth, mYPos, 1.0f, mBottomColor.x, mBottomColor.y, mBottomColor.z,
                x+mBarWidth, nextOffsetHeight, 1.0f, mTopColor.x, mTopColor.y, mTopColor.z,

                x+mBarWidth, nextOffsetHeight, 1.0f, mTopColor.x, mTopColor.y, mTopColor.z,
                x, y, 1.0f, mTopColor.x, mTopColor.y, mTopColor.z,
                x, mYPos, 1.0f, mBottomColor.x, mBottomColor.y, mBottomColor.z,
            };

            System.arraycopy(vertices, 0, mVertexArray, i*vertices.length, vertices.length);
        }

        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexArray);
        mVertexBuffer.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_STATIC_DRAW);

        glDrawArrays(GL_TRIANGLES, 0, mBarCount*6);

        glBindVertexArray(0);
    }

    public int getTotalWidth() {
        return mBarCount * (mBarWidth + mBarGap);
    }

    /**
     * left-bottom
     */
    public void setPosition(int x, int y) {
        mXPos = x;
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


    @Override
    public void render(float delta) {
        super.render(delta);

        drawBars();
    }
}
