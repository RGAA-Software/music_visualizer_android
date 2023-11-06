package com.shark.dynamics.graphics.renderer.bars3d;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars.IBarsRenderer;
import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;
import com.shark.dynamics.graphics.util.MatrixUtil;

import org.joml.Matrix4f;
import org.joml.Vector2f;
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

public class CircleBars3D extends IBars3DRenderer {

    private int mBarCount;

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

    public CircleBars3D() {
        String vs = Director.getInstance()
                .loaderShaderFromAssets("shader/instance_vs.glsl");
        String fs = Director.getInstance()
                .loaderShaderFromAssets("shader/base_2d_fs.glsl");
        mShader = new Shader(vs, fs);

        initData();
    }

    private void initData() {
        super.initRenderer();
        mBarCount = 80;
        mBarWidth = Director.getInstance()
                .getDevice().getPixelSize(3);
        mBarGap = Director.getInstance()
                .getDevice().getPixelSize(2);

        mBottomColor = new Vector3f(1.0f, 0.6f, 0.5f);
        mTopColor = new Vector3f(0.2f, 0.5f, 0.8f);

        float tw = 0.02f;
        float th = 0.1f;
        float[] vertices = {
                0, 0, 0.0f, mBottomColor.x, mBottomColor.y, mBottomColor.z, 0.0f, 0.0f,
                th, 0, 0.0f, mTopColor.x, mTopColor.y, mTopColor.z, 1.0f, 0.0f,
                th, tw, 0.0f, mTopColor.x, mTopColor.y, mTopColor.z, 1.0f, 1.0f,
                0, tw, 0.0f, mBottomColor.x, mBottomColor.y, mBottomColor.z, 0.0f, 1.0f
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

        //
        mInstanceModels = new Matrix4f[mBarCount];
        for (int i = 0; i < mBarCount; i++) {
            mInstanceModels[i] = new Matrix4f();
        }
        mInstanceModelArray = new float[mInstanceModels.length * 16];

        mInstanceModelBuffer = ByteBuffer
                .allocateDirect(mInstanceModels.length*16*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

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
        if (mMCArray == null) {
            return;
        }

        fallDownMC(mMCArray, mBarCount);

        float radius = 1.45f;
        float itemAngel = 360.0f/mBarCount;
        for (int i = 0; i < mBarCount; i++) {
            float angle = itemAngel * i;
            float th = 1 + mDrawMCBars[i];
            float tw = mBarWidth;

            mModelMatrix.identity();
            float angelRadius = (float)Math.toRadians(angle);
            float cos = (float) Math.cos(angelRadius);
            float sin = (float) Math.sin(angelRadius);
            //mModelMatrix.translate(mTranslate);
            mModelMatrix.translate(mTranslate.x + cos*radius, mTranslate.y+sin*radius, mTranslate.z);
            mModelMatrix.rotate(angelRadius, 0, 0, 1);
            mModelMatrix.scale(mScale.x * th/12.0f, mScale.y, 1);

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
