package com.shark.dynamics.graphics.renderer.shaderit;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.r2d.I2DRenderer;
import com.shark.dynamics.graphics.renderer.r2d.Rectangle;
import com.shark.dynamics.graphics.shader.Shader;
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
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;

public class ShaderIt extends I2DRenderer {

    private float mTimeLapses;

    private Vector3f mResolution;

    public ShaderIt(int width, int height) {
        String vs = Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl");
        String fs = Director.getInstance().loaderShaderFromAssets("shader/test_shaderit_fs.glsl");
        mShader = new Shader(vs, fs);
        initRenderer();

        mResolution = new Vector3f(width, height, 0);

        mWidth = mResolution.x;
        mHeight = mResolution.y;
        mOriginWidth = mWidth;
        mOriginHeight = mHeight;

        float[] vertices = {
                0,  0,  0,          1.0f, 0.0f, 0.0f,   0.0f, 0.0f,
                mWidth, 0, 0,       0.0f, 1.0f, 0.0f,   1.0f, 0.0f,
                mWidth, mHeight, 0, 0.0f, 0.0f, 1.0f,   1.0f, 1.0f,
                0,  mHeight, 0,     1.0f, 1.0f, 0.0f,   0.0f, 1.0f,
                0,  0,  0,          1.0f, 0.0f, 0.0f,   0.0f, 0.0f,
        };

        int[] indices = {
                0, 1, 2,
                2, 3, 0
        };

        int stride = 8 * 4;

        FloatBuffer verticesBuffer = BufferUtil.createFloatBuffer(vertices.length, vertices);

        int vertexArray = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, vertexArray);
        glBufferData(GL_ARRAY_BUFFER, vertices.length*4, verticesBuffer, GL_STATIC_DRAW);

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

    @Override
    public void render(float delta) {
        super.render(delta);

        mTimeLapses += delta;

        if (mWidth != 0 && mHeight != 0) {
            mResolution.x = mWidth;
            mResolution.y = mHeight;
        }
        //uniform vec3 iResolution;
        //uniform float iTime;
        mShader.setUniformFloat("iTime", mTimeLapses);
        mShader.setUniformVec3("iResolution", mResolution);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
    }
}
