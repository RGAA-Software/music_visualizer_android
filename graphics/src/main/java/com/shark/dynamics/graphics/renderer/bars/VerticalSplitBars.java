package com.shark.dynamics.graphics.renderer.bars;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.ColorUtil;
import com.shark.dynamics.graphics.util.GLUtil;
import com.shark.dynamics.graphics.util.MatrixUtil;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glDrawElementsInstanced;
import static android.opengl.GLES30.glVertexAttribDivisor;

public class VerticalSplitBars extends IBarsRenderer {
    private static final String TAG = "VertInstance";

    private final int mBarCount = 45;

    protected Vector3f mBottomColor;
    protected Vector3f mTopColor;

    protected int mVertexArray;
    protected FloatBuffer mVertexBuffer;

    protected int mBarWidth;
    protected int mBarGap;

    protected int mXPos;
    protected int mYPos;

    private int mInstanceArrayHandle;
    private Matrix4f[] mInstanceModels;
    private FloatBuffer mInstanceModelBuffer;
    private float[] mInstanceModelArray;

    private int mInstanceColorHandle;
    private Vector3f[] mInstanceColorVectors;
    private FloatBuffer mInstanceColorBuffer;
    private float[] mInstanceColors;

    private int mInstanceAlphaHandle;
    private FloatBuffer mInstanceAlphaBuffer;

    private int mMaxSquarePerLine = 15;

    private Vector3f mFromColor = new Vector3f(0.2f, 0.9f, 0.3f);
    private Vector3f mToColor = new Vector3f(0.0f, 0.5f, 0.9f);;

    public VerticalSplitBars(String vs, String fs) {
        super(vs, fs);
        init();
    }


    private void init() {
        super.initRenderer();
        initData();
    }

    private void initData() {
        mBarWidth = Director.getInstance()
                .getDevice().getPixelSize(5);
        mBarGap = Director.getInstance()
                .getDevice().getPixelSize(2);

        mBottomColor = new Vector3f(1.0f, 0.1f, 0.1f);
        mTopColor = new Vector3f(1.0f, 0.1f, 0.1f);

        float tw = mBarWidth;
        float th = 5;
        float[] vertices = {
            0, 0, 1.0f, mBottomColor.x, mBottomColor.y, mBottomColor.z, 0.0f, 0.0f,
            tw, 0, 1.0f, mBottomColor.x, mBottomColor.y, mBottomColor.z, 1.0f, 0.0f,
            tw, tw, 1.0f, mTopColor.x, mTopColor.y, mTopColor.z, 1.0f, 1.0f,
            0, tw, 1.0f, mTopColor.x, mTopColor.y, mTopColor.z, 0.0f, 1.0f
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

        mInstanceModels = new Matrix4f[mBarCount * mMaxSquarePerLine];
        for (int i = 0; i < mInstanceModels.length; i++) {
            mInstanceModels[i] = new Matrix4f();
        }
        mInstanceModelArray = new float[mInstanceModels.length * 16];

        mInstanceModelBuffer = ByteBuffer
                .allocateDirect(mInstanceModels.length*16*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        //
        mInstanceArrayHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mInstanceArrayHandle);
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

        // colors
        mInstanceColorVectors = new Vector3f[mBarCount*mMaxSquarePerLine];
        for (int i = 0; i < mInstanceColorVectors.length; i++) {
            mInstanceColorVectors[i] = new Vector3f(1,1,1);
        }
        mInstanceColors = new float[mInstanceColorVectors.length*3];
        mInstanceColorBuffer = ByteBuffer
                .allocateDirect(mInstanceColorVectors.length*3*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        mInstanceColorHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mInstanceColorHandle);
        glBufferData(GL_ARRAY_BUFFER, mInstanceColorVectors.length*3*4, mInstanceColorBuffer, GL_DYNAMIC_DRAW);

        glEnableVertexAttribArray(8);
        glVertexAttribPointer(8, 3, GL_FLOAT, false, 0, 0);
        glVertexAttribDivisor(8, 1);

        // alphas
        mInstanceAlphaBuffer = ByteBuffer
                .allocateDirect(mBarCount*mMaxSquarePerLine*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        mInstanceAlphaHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mInstanceAlphaHandle);
        glBufferData(GL_ARRAY_BUFFER, mBarCount*mMaxSquarePerLine*4, mInstanceAlphaBuffer, GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(7);
        glVertexAttribPointer(7, 1, GL_FLOAT, false, 0, 0);
        glVertexAttribDivisor(7, 1);

        glBindVertexArray(0);
    }

    private void drawBars(boolean up, boolean enableAlpha) {
        int totalSquare = 0;
        mInstanceAlphaBuffer.position(0);
        for (int i = 0; i < mBarCount; i++) {
            int squareSize = (int) (mDrawMCBars[i]/mBarWidth);
            squareSize = Math.min(squareSize, mMaxSquarePerLine)+1;
            for (int sqIdx = 0; sqIdx < squareSize; sqIdx++) {

                float yOffset = sqIdx*(mBarWidth+mBarGap);
                yOffset = up ? yOffset : -yOffset;
                translateTo(i * (mBarWidth + mBarGap) + mXPos, mYPos + yOffset, 0);

                mModelMatrix.identity();
                mModelMatrix.translate(mTranslate);

                mModelMatrix.translate(mWidth/2, mHeight/2, 0);
                mModelMatrix.rotate((float)Math.toRadians(mRotateDegree), 0, 0, 1);
                mModelMatrix.translate(-mWidth/2, -mHeight/2, 0);

                mModelMatrix.scale(mScale.x, mScale.y, 0);
                mModelMatrix.get(mInstanceModels[totalSquare]);

                mInstanceColorVectors[totalSquare] = ColorUtil.LinearGradient(mFromColor, mToColor, mMaxSquarePerLine/2, sqIdx);

                float alpha = 1.0f - (sqIdx+1)*1.0f*1.2f/mMaxSquarePerLine;
                if (enableAlpha) {
                    mInstanceAlphaBuffer.put(alpha);
                } else {
                    mInstanceAlphaBuffer.put(1.0f);
                }

                totalSquare++;
            }
        }

        // models
        MatrixUtil.matrixArrayToFloatArray(mInstanceModels, mInstanceModelArray);
        mInstanceModelBuffer.position(0);
        mInstanceModelBuffer.put(mInstanceModelArray);
        mInstanceModelBuffer.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, mInstanceArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mInstanceModels.length*16*4, mInstanceModelBuffer, GL_DYNAMIC_DRAW);

        // colors
        MatrixUtil.vectorArrayToFloatArray(mInstanceColorVectors, mInstanceColors);
        mInstanceColorBuffer.position(0);
        mInstanceColorBuffer.put(mInstanceColors);
        mInstanceColorBuffer.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, mInstanceColorHandle);
        glBufferData(GL_ARRAY_BUFFER, mInstanceColorVectors.length*3*4, mInstanceColorBuffer, GL_DYNAMIC_DRAW);

        // alphas
        mInstanceAlphaBuffer.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mInstanceAlphaHandle);
        glBufferData(GL_ARRAY_BUFFER, mBarCount*mMaxSquarePerLine*4/*mInstanceAlphaBuffer.capacity()*4*/, mInstanceAlphaBuffer, GL_DYNAMIC_DRAW);

        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, totalSquare);
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
        if (mMCArray == null || mInstanceModels.length <= 0) {
            return;
        }

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        fallDownMC(mMCArray, mBarCount);

        drawBars(true, false);
        drawBars(false, true);

        glBindVertexArray(0);
    }
}
