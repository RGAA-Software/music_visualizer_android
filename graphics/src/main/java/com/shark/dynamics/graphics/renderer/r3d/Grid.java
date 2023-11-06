package com.shark.dynamics.graphics.renderer.r3d;



import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.texture.Texture;
import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;
import com.shark.dynamics.graphics.util.MatrixUtil;

import org.joml.Matrix4f;

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
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glDrawElementsInstanced;
import static android.opengl.GLES30.glVertexAttribDivisor;

public class Grid extends I3DRenderer {

    private int mHHalfSize = 10;
    private int mVHalfSize = 10;
    private int mZLevel = 8;
    private float mStep = 0.8f;
    private float mScale = 0.05f;

    private int mVertexHandle;
    private Matrix4f[] mModelMatrices;
    private FloatBuffer mModelBuffer;

    private int mInstanceAlphaArrayHandle;
    private FloatBuffer mInstanceAlphaBuffer;

    private Texture mTexture;

    public Grid(int xSize, int ySize, float step) {
        mHHalfSize = xSize/2;
        mVHalfSize = ySize/2;
        mStep = step;
        String vs = Director.getInstance().loaderShaderFromAssets("shader/instance_vs.glsl");
        String fs = Director.getInstance().loaderShaderFromAssets("shader/3d/instance_alpha_tex_fs.glsl");
        mShader = new Shader(vs, fs);
        mTexture = Director.getInstance().findTexture("images/particle.png");
        initRenderer();

        // model matrices
        mModelMatrices = new Matrix4f[mHHalfSize*mVHalfSize*2*2*mZLevel];
        for (int i = 0; i < mModelMatrices.length; i++) {
            mModelMatrices[i] = new Matrix4f();
        }

        mInstanceAlphaBuffer = BufferUtil.createFloatBuffer(mModelMatrices.length);

        float xPos = 0;
        float yPos = 0;
        float zPos = 0;
        int index = 0;
        for (int z = 0; z < mZLevel; z++) {
            zPos = -z*mStep;
            for (int y = 0; y < mVHalfSize*2; y++) {
                yPos = y*mStep;
                for (int x = 0; x < mHHalfSize; x++) {
                    float yP = yPos;
                    if (y % 2 == 0) {
                        yP = -(y/2)*mStep;
                    } else {
                        yP = (y/2) * mStep;
                    }

                    float zP = zPos;
                    if (z % 2 == 0) {
                        //zP = -zP;
                    }

                    float alpha = 1.0f - z*1.0f/mZLevel + 0.1f;

                    xPos = x*mStep;
                    Matrix4f model = mModelMatrices[index];
                    model.identity();
                    model.translate(xPos, yP, zP);
                    model.scale(mScale, mScale, mScale);
                    mInstanceAlphaBuffer.position(index);
                    mInstanceAlphaBuffer.put(alpha);

                    index++;

                    xPos = -x*mStep;
                    model = mModelMatrices[index];
                    model.identity();
                    model.translate(xPos, yP, zP);
                    model.scale(mScale, mScale, mScale);
                    mInstanceAlphaBuffer.position(index);
                    mInstanceAlphaBuffer.put(alpha);

                    index++;
                }
            }
        }

        mModelBuffer = MatrixUtil.matrixArrayToFloatBuffer(mModelMatrices);

        mVertexHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, mModelMatrices.length*16*4, mModelBuffer, GL_DYNAMIC_DRAW);

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

        // alpha
        //

        mInstanceAlphaBuffer.position(0);
        mInstanceAlphaArrayHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mInstanceAlphaArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mModelMatrices.length*4, mInstanceAlphaBuffer, GL_DYNAMIC_DRAW);

        glEnableVertexAttribArray(7);
        glVertexAttribPointer(7, 1, GL_FLOAT, false, 0, 0);
        glVertexAttribDivisor(7, 1);

        // vertices
        float[] vertices = new float[] {
                -1.0f , -1.0f , 0.0f,   1.0f, 0.0f, 0.0f,  0.0f, 0.0f,
                1.0f , -1.0f ,  0.0f,   0.0f, 1.0f, 0.0f,  1.0f , 0.0f,
                1.0f ,  1.0f ,  0.0f,   0.0f, 0.0f, 1.0f,  1.0f , 1.0f ,
                -1.0f , 1.0f ,  0.0f,   1.0f, 1.0f, 0.0f,  0.0f, 1.0f ,
        };
        int stride = 8 * 4;

        FloatBuffer vertexBuffer = ByteBuffer
                .allocateDirect(vertices.length*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        int vertexHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertices.length*4, vertexBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3*4);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6*4);
        glEnableVertexAttribArray(2);

        // indices
        int[] indices = {
                0, 1, 2,
                2, 3, 0
        };

        IntBuffer indicesBuffer = BufferUtil.createIntBuffer(indices.length, indices);
        int indexArray = GLUtil.glGenBuffer();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexArray);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.length*4, indicesBuffer, GL_STATIC_DRAW);

        
        glBindVertexArray(0);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        mTexture.active(mShader, 0);
        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, mModelMatrices.length);

        glDisable(GL_BLEND);
        glBindVertexArray(0);
    }
}
