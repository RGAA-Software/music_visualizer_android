package com.shark.dynamics.graphics.renderer.bars;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.texture.Texture;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;
import com.shark.dynamics.graphics.util.MatrixUtil;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glDrawElementsInstanced;
import static android.opengl.GLES30.glVertexAttribDivisor;

public class VerticalBarsTexture extends IBarsRenderer {
    private static final String TAG = "VertInstance";

    private final int mBarCount = 36;

    private Vector3f mBottomColor;
    private Vector3f mTopColor;

    private int mVertexArray;
    private FloatBuffer mVertexBuffer;

    private int mBarWidth;
    private int mBarGap;

    private int mXPos;
    private int mYPos;

    private int mInstanceArray;
    private Matrix4f[] mInstanceModels;
    private FloatBuffer mInstanceModelBuffer;
    private float[] mInstanceModelArray;

    private Texture mTexture;

    public VerticalBarsTexture(String vs, String fs, String texPath) {
        super(vs, fs);
        mTexture = Director.getInstance().findTexture(texPath);
        initData();
    }

    private void initData() {
        mBarWidth = Director.getInstance()
                .getDevice().getPixelSize(8);
        mBarGap = Director.getInstance()
                .getDevice().getPixelSize(0);

        mBottomColor = new Vector3f(1.0f, 0.1f, 0.1f);
        mTopColor = new Vector3f(0.9f, 0.8f, 0.1f);

        float tw = mBarWidth;
        float th = 5;
        float[] vertices = {
            0, 0, 1.0f, mBottomColor.x, mBottomColor.y, mBottomColor.z, 0.0f, 0.0f,
            tw, 0, 1.0f, mBottomColor.x, mBottomColor.y, mBottomColor.z, 1.0f, 0.0f,
            tw, th, 1.0f, mTopColor.x, mTopColor.y, mTopColor.z, 1.0f, 1.0f,
            0, th, 1.0f, mTopColor.x, mTopColor.y, mTopColor.z, 0.0f, 1.0f
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

        mInstanceModels = new Matrix4f[mBarCount];
        for (int i = 0; i < mBarCount; i++) {
            mInstanceModels[i] = new Matrix4f();
        }
        mInstanceModelArray = new float[mInstanceModels.length * 16];

        mInstanceModelBuffer = ByteBuffer
                .allocateDirect(mInstanceModels.length*16*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        //
        mInstanceArray = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mInstanceArray);
        glBufferData(GL_ARRAY_BUFFER, mInstanceModels.length*16*4, mInstanceModelBuffer, GL_DYNAMIC_DRAW);

        int vec4Size = 4*4;
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 4, GL_FLOAT, false, 4 * vec4Size, 0);
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 4, GL_FLOAT, false, 4 * vec4Size, (vec4Size));
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(5, 4, GL_FLOAT, false, 4 * vec4Size, (2 * vec4Size));
        glEnableVertexAttribArray(6);
        glVertexAttribPointer(6, 4, GL_FLOAT, false, 4 * vec4Size, (3 * vec4Size));

        glVertexAttribDivisor(3, 1);
        glVertexAttribDivisor(4, 1);
        glVertexAttribDivisor(5, 1);
        glVertexAttribDivisor(6, 1);

        glBindVertexArray(0);
    }

    private void drawBars() {
        if (mMCArray == null || mInstanceModels.length <= 0) {
            return;
        }

        fallDownMC(mMCArray, mBarCount);

        for (int i = 0; i < mBarCount; i++) {
            float th = 1 + mDrawMCBars[i];
            float tw = mBarWidth;

            translateTo(i * (tw + mBarGap) + mXPos, mYPos, 0);

            mModelMatrix.identity();
            mModelMatrix.translate(mTranslate);

            mModelMatrix.scale(mScale.x*mXBarScale, mScale.y*th/2*mYBarScale, 0);
            mModelMatrix.get(mInstanceModels[i]);
        }

        MatrixUtil.matrixArrayToFloatArray(mInstanceModels, mInstanceModelArray);

        mInstanceModelBuffer.position(0);
        mInstanceModelBuffer.put(mInstanceModelArray);
        mInstanceModelBuffer.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, mInstanceArray);
        glBufferData(GL_ARRAY_BUFFER, mInstanceModels.length*16*4, mInstanceModelBuffer, GL_DYNAMIC_DRAW);

        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, mBarCount);

        glBindVertexArray(0);
    }

    public int getTotalWidth() {
        return (mBarCount-1)*(mBarWidth+mBarGap) + (int)(mBarWidth*mXBarScale);
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
        mTexture.active(mShader, 0);
        drawBars();
    }
}
