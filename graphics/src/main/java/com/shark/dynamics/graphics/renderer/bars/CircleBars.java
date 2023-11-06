package com.shark.dynamics.graphics.renderer.bars;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import org.joml.Vector2f;
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

public class CircleBars extends IBarsRenderer {

    private int mBarCount = 80;

    private Vector3f mBottomColor = new Vector3f(1.0f, 0.6f, 0.5f);
    private Vector3f mTopColor = new Vector3f(0.2f, 0.5f, 0.8f);

    private int mVertexArray;
    private FloatBuffer mVertexBuffer;

    private int mBarWidth;
    private int mBarGap;

    private int mXPos;
    private int mYPos;

    public CircleBars(String vs, String fs) {
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

        float radius = 200;
        float itemAngel = 360.0f/mBarCount;
        for (int i = 0; i < mBarCount; i++) {
            float angle = itemAngel * i;
            float th = 1 + mDrawMCBars[i];
            float tw = mBarWidth;

            float[] vertices = {
                0, 0, 1.0f, mBottomColor.x, mBottomColor.y, mBottomColor.z, 0.0f, 0.0f,
                th, 0, 1.0f, mTopColor.x, mTopColor.y, mTopColor.z, 1.0f, 0.0f,
                th, tw, 1.0f, mTopColor.x, mTopColor.y, mTopColor.z, 1.0f, 1.0f,
                0, tw, 1.0f, mBottomColor.x, mBottomColor.y, mBottomColor.z, 0.0f, 1.0f
            };
            mVertexBuffer.position(0);
            mVertexBuffer.put(vertices);
            mVertexBuffer.position(0);

            glBindBuffer(GL_ARRAY_BUFFER, mVertexArray);
            glBufferData(GL_ARRAY_BUFFER, vertices.length*4, mVertexBuffer, GL_STATIC_DRAW);

            translateTo(mXPos, mYPos, 0);

            mModelMatrix = mModelMatrix.identity();
            float angelRadius = (float)Math.toRadians(angle);
            float cos = (float) Math.cos(angelRadius);
            float sin = (float) Math.sin(angelRadius);
            mModelMatrix = mModelMatrix.translate(mTranslate);
            mModelMatrix = mModelMatrix.translate(cos*radius, sin*radius, 0);
            mModelMatrix = mModelMatrix.rotate(angelRadius, 0, 0, 1);
            mModelMatrix = mModelMatrix.scale(mScale);

            glUniformMatrix4fv(mShader.getUniformLocation("model"),
                    1,
                    false,
                    getModelBuffer());

            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        }
        glBindVertexArray(0);
    }

    public void setPosition(int x, int y) {
        mXPos = x;
        mYPos = y;
    }

    public void setCenter() {
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        mXPos = (int) (sc.x/2);
        mYPos = (int) (sc.y/2);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        drawBars();
    }
}
