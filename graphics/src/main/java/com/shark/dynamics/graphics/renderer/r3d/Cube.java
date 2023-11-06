package com.shark.dynamics.graphics.renderer.r3d;

import android.opengl.Matrix;
import android.util.Log;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.texture.CubeTexture;
import com.shark.dynamics.graphics.renderer.texture.Texture;
import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;
import com.shark.dynamics.graphics.util.MatrixUtil;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_FALSE;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;

public class Cube extends I3DRenderer {

    private int mVertexArrayHandle;
    private float[] mVertexArray;
    private FloatBuffer mVertexBuffer;

    private Texture mTexture;
    private CubeTexture mCubeTexture;

    private int[] mUpdateLines;

    private boolean mIsSkyBox;

    private Vector3f mColor = new Vector3f(1,1,1);

    private final FloatBuffer mViewBuffer3D = ByteBuffer.allocateDirect(16*4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();

    private Matrix3f mDestMatrix = new Matrix3f();
    private Matrix4f mNewView = new Matrix4f();

    public Cube() {
        init(Director.getInstance().loaderShaderFromAssets("shader/3d/base_vs.glsl"),
                Director.getInstance().loaderShaderFromAssets("shader/3d/base_color_fs.glsl"));
    }

    public Cube(Vector3f color) {
        mColor = color;
        init(Director.getInstance().loaderShaderFromAssets("shader/3d/base_vs.glsl"),
                Director.getInstance().loaderShaderFromAssets("shader/3d/base_color_fs.glsl"));
    }

    public Cube(String path, boolean isCube, boolean isSkyBox) {
        if (!isCube) {
            mTexture = Director.getInstance().findTexture(path);
            init(Director.getInstance().loaderShaderFromAssets("shader/3d/base_vs.glsl"),
                    Director.getInstance().loaderShaderFromAssets("shader/3d/base_tex_fs.glsl"));
        } else {
            mCubeTexture = new CubeTexture(path);
            if (isSkyBox) {
                mIsSkyBox = true;
                init(Director.getInstance().loaderShaderFromAssets("shader/3d/skybox_vs.glsl"),
                        Director.getInstance().loaderShaderFromAssets("shader/3d/base_cube_tex_fs.glsl"));
            } else {
                init(Director.getInstance().loaderShaderFromAssets("shader/3d/base_vs.glsl"),
                        Director.getInstance().loaderShaderFromAssets("shader/3d/base_cube_tex_fs.glsl"));
            }
        }
    }
    
    public Cube(String vs, String fs) {
        init(vs, fs);
    }

    public Cube(Vector3f color, String vs, String fs) {
        mColor = color;
        init(vs, fs);
    }
    
    private void init(String vs, String fs) {
        mShader = new Shader(vs, fs);
        initRenderer();

        // [11*36]
        mVertexArray = new float[] {
                -0.5f, -0.5f, -0.5f,mColor.x, mColor.y, mColor.z,      0.0f, 0.0f,    0.0f,  0.0f, -1.0f,
                0.5f, -0.5f, -0.5f, mColor.x, mColor.y, mColor.z,     1.0f, 0.0f,   0.0f,  0.0f, -1.0f,
                0.5f, 0.5f, -0.5f,  mColor.x, mColor.y, mColor.z,     1.0f, 1.0f,   0.0f,  0.0f, -1.0f,
                0.5f, 0.5f, -0.5f,  mColor.x, mColor.y, mColor.z,     1.0f, 1.0f,   0.0f,  0.0f, -1.0f,
                -0.5f, 0.5f, -0.5f, mColor.x, mColor.y, mColor.z,      0.0f, 1.0f,    0.0f,  0.0f, -1.0f,
                -0.5f, -0.5f, -0.5f,mColor.x, mColor.y, mColor.z,      0.0f, 0.0f,    0.0f,  0.0f, -1.0f,

                -0.5f, -0.5f, 0.5f, mColor.x, mColor.y, mColor.z,      0.0f, 0.0f,    0.0f,  0.0f,  1.0f,
                0.5f, -0.5f, 0.5f,  mColor.x, mColor.y, mColor.z,     1.0f, 0.0f,   0.0f,  0.0f,  1.0f,
                0.5f, 0.5f, 0.5f,   mColor.x, mColor.y, mColor.z,     1.0f, 1.0f,   0.0f,  0.0f,  1.0f,
                0.5f, 0.5f, 0.5f,   mColor.x, mColor.y, mColor.z,     1.0f, 1.0f,   0.0f,  0.0f,  1.0f,
                -0.5f, 0.5f, 0.5f,  mColor.x, mColor.y, mColor.z,      0.0f, 1.0f,    0.0f,  0.0f,  1.0f,
                -0.5f, -0.5f, 0.5f, mColor.x, mColor.y, mColor.z,      0.0f, 0.0f,    0.0f,  0.0f,  1.0f,

                -0.5f, 0.5f, 0.5f,  mColor.x, mColor.y, mColor.z,      1.0f, 0.0f,  -1.0f,  0.0f,  0.0f,
                -0.5f, 0.5f, -0.5f, mColor.x, mColor.y, mColor.z,      1.0f, 1.0f,  -1.0f,  0.0f,  0.0f,
                -0.5f, -0.5f, -0.5f,mColor.x, mColor.y, mColor.z,      0.0f, 1.0f,  -1.0f,  0.0f,  0.0f,
                -0.5f, -0.5f, -0.5f,mColor.x, mColor.y, mColor.z,      0.0f, 1.0f,  -1.0f,  0.0f,  0.0f,
                -0.5f, -0.5f, 0.5f, mColor.x, mColor.y, mColor.z,      0.0f, 0.0f,  -1.0f,  0.0f,  0.0f,
                -0.5f, 0.5f, 0.5f,  mColor.x, mColor.y, mColor.z,      1.0f, 0.0f,  -1.0f,  0.0f,  0.0f,

                0.5f, 0.5f, 0.5f,   mColor.x, mColor.y, mColor.z,      1.0f, 0.0f,   1.0f,  0.0f,  0.0f,
                0.5f, 0.5f, -0.5f,  mColor.x, mColor.y, mColor.z,      1.0f, 1.0f,   1.0f,  0.0f,  0.0f,
                0.5f, -0.5f, -0.5f, mColor.x, mColor.y, mColor.z,      0.0f, 1.0f,   1.0f,  0.0f,  0.0f,
                0.5f, -0.5f, -0.5f, mColor.x, mColor.y, mColor.z,      0.0f, 1.0f,   1.0f,  0.0f,  0.0f,
                0.5f, -0.5f, 0.5f,  mColor.x, mColor.y, mColor.z,      0.0f, 0.0f,   1.0f,  0.0f,  0.0f,
                0.5f, 0.5f, 0.5f,   mColor.x, mColor.y, mColor.z,      1.0f, 0.0f,   1.0f,  0.0f,  0.0f,

                -0.5f, -0.5f, -0.5f,mColor.x, mColor.y, mColor.z,      0.0f, 1.0f,    0.0f, -1.0f,  0.0f,
                0.5f, -0.5f, -0.5f, mColor.x, mColor.y, mColor.z,     1.0f, 1.0f,   0.0f, -1.0f,  0.0f,
                0.5f, -0.5f, 0.5f,  mColor.x, mColor.y, mColor.z,     1.0f, 0.0f,   0.0f, -1.0f,  0.0f,
                0.5f, -0.5f, 0.5f,  mColor.x, mColor.y, mColor.z,     1.0f, 0.0f,   0.0f, -1.0f,  0.0f,
                -0.5f, -0.5f, 0.5f, mColor.x, mColor.y, mColor.z,      0.0f, 0.0f,    0.0f, -1.0f,  0.0f,
                -0.5f, -0.5f, -0.5f,mColor.x, mColor.y, mColor.z,      0.0f, 1.0f,    0.0f, -1.0f,  0.0f,

                -0.5f, 0.5f, -0.5f, mColor.x, mColor.y, mColor.z,     0.0f, 1.0f,    0.0f,  1.0f,  0.0f,
                0.5f, 0.5f, -0.5f,  mColor.x, mColor.y, mColor.z,    1.0f, 1.0f,   0.0f,  1.0f,  0.0f,
                0.5f, 0.5f, 0.5f,   mColor.x, mColor.y, mColor.z,    1.0f, 0.0f,   0.0f,  1.0f,  0.0f,
                0.5f, 0.5f, 0.5f,   mColor.x, mColor.y, mColor.z,    1.0f, 0.0f,   0.0f,  1.0f,  0.0f,
                -0.5f, 0.5f, 0.5f,  mColor.x, mColor.y, mColor.z,     0.0f, 0.0f,    0.0f,  1.0f,  0.0f,
                -0.5f, 0.5f, -0.5f, mColor.x, mColor.y, mColor.z,    0.0f, 1.0f,    0.0f,  1.0f,  0.0f,
        };

        mVertexBuffer = BufferUtil.createFloatBuffer(mVertexArray.length, mVertexArray);

        mVertexArrayHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_DYNAMIC_DRAW);

        int stride = 11 * 4;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3*4);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6*4);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, 3, GL_FLOAT, false, stride, 8*4);
        glEnableVertexAttribArray(3);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        //glBindVertexArray(0);
        mUpdateLines = new int[]{2,3,4,8,9,10,12,13,17,18,19,23,30,31,32,33,34,35};
    }

    public void updateYValue(float value) {
        for (int idx : mUpdateLines) {
            mVertexBuffer.position(idx * 11 + 1);
            mVertexBuffer.put(value);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        mVertexBuffer.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_DYNAMIC_DRAW);

        if (mTexture != null) {
            mTexture.active(mShader, 1);
        }
        if (mCubeTexture != null) {
            mCubeTexture.active(mShader, 1);
        }

        if (mIsSkyBox) {
            glDepthFunc(GL_LEQUAL);
            Matrix4f view = Director.getInstance().getViewMatrix3D();
            view.get3x3(mDestMatrix);

            mNewView.set(mDestMatrix);

            mViewBuffer3D.position(0);
            mNewView.get(mViewBuffer3D);
            mViewBuffer3D.position(0);
            mShader.setUniformMatrix4fv("view", mViewBuffer3D);
        }

        glDrawArrays(GL_TRIANGLES, 0, 36);

        if (mIsSkyBox) {
            glDepthFunc(GL_LESS);
        }
        glBindVertexArray(0);
    }

    @Override
    public void renderShadow(float delta) {
        super.renderShadow(delta);

        mVertexBuffer.position(0);
        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_DYNAMIC_DRAW);

        glDrawArrays(GL_TRIANGLES, 0, 36);
    }
}
