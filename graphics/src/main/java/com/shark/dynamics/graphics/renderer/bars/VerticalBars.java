package com.shark.dynamics.graphics.renderer.bars;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;

public class VerticalBars extends IBarsRenderer {

    private int mBarCount = 60;

    protected Vector3f mBottomColor = new Vector3f(1.0f, 0.6f, 0.5f);
    protected Vector3f mTopColor = new Vector3f(0.2f, 0.5f, 0.8f);

    protected int mVertexArray;
    protected FloatBuffer mVertexBuffer;

    protected int mBarWidth;
    protected int mBarGap;

    protected int mXPos;
    protected int mYPos;

    public VerticalBars(String vs, String fs) {
        super(vs, fs);

        mBarWidth = Director.getInstance()
                .getDevice().getPixelSize(3);
        mBarGap = Director.getInstance()
                .getDevice().getPixelSize(2);
    }

    @Override
    protected void initRenderer() {
        super.initRenderer();

        initData();
    }

    private void initData() {
        float tw = 0;
        float th = 0;
        float[] vertices = {
            0,  0,  1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f,
            tw, 0,  1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,
            tw, th, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
            0,  th, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f
        };

        int[] indices = {
            0, 1, 2,
            2, 3, 0
        };

        int stride = 8 * 4;

        mVertexBuffer = BufferUtil.createFloatBuffer(vertices.length, vertices);

        mVertexArray = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mVertexArray);
        glBufferData(GL_ARRAY_BUFFER, vertices.length*4, mVertexBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3*4);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6*4);
        glEnableVertexAttribArray(2);

        //
        IntBuffer indicesBuffer = BufferUtil.createIntBuffer(indices.length, indices);

        int indexArray = GLUtil.glGenBuffer();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexArray);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.length*4, indicesBuffer, GL_STATIC_DRAW);

        glBindVertexArray(0);
    }

    private void drawBars() {
        if (mMCArray == null) {
            return;
        }

        fallDownMC(mMCArray, mBarCount);

        for (int i = 0; i < mBarCount; i++) {
            float th = 1 + mDrawMCBars[i];
            float tw = mBarWidth;

            float[] vertices = {
                0, 0, 1.0f, mBottomColor.x, mBottomColor.y, mBottomColor.z, 0.0f, 0.0f,
                tw, 0, 1.0f, mBottomColor.x, mBottomColor.y, mBottomColor.z, 1.0f, 0.0f,
                tw, th, 1.0f, mTopColor.x, mTopColor.y, mTopColor.z, 1.0f, 1.0f,
                0, th, 1.0f, mTopColor.x, mTopColor.y, mTopColor.z, 0.0f, 1.0f
            };
            mVertexBuffer.position(0);
            mVertexBuffer.put(vertices);
            mVertexBuffer.position(0);

            glBindBuffer(GL_ARRAY_BUFFER, mVertexArray);
            glBufferData(GL_ARRAY_BUFFER, vertices.length*4, mVertexBuffer, GL_STATIC_DRAW);

            translateTo(i * (tw + mBarGap) + mXPos, mYPos, 0);

            mModelMatrix = mModelMatrix.identity();
            mModelMatrix = mModelMatrix.translate(mTranslate);

            mModelMatrix = mModelMatrix.translate(mWidth/2, mHeight/2, 0);
            mModelMatrix = mModelMatrix.rotate((float)Math.toRadians(mRotateDegree), 0, 0, 1);
            mModelMatrix = mModelMatrix.translate(-mWidth/2, -mHeight/2, 0);

            mModelMatrix = mModelMatrix.scale(mScale);

            glUniformMatrix4fv(mShader.getUniformLocation("model"),
                    1,
                    false,
                    getModelBuffer());

            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        }

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
    public boolean isCustomModelMatrix() {
        return true;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        drawBars();
    }
}
