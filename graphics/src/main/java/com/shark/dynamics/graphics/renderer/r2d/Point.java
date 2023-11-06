package com.shark.dynamics.graphics.renderer.r2d;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.texture.Texture;
import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import org.joml.Vector2f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES32.GL_ARRAY_BUFFER;
import static android.opengl.GLES32.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES32.GL_FLOAT;
import static android.opengl.GLES32.GL_LINE_STRIP;
import static android.opengl.GLES32.GL_STATIC_DRAW;
import static android.opengl.GLES32.GL_TEXTURE0;
import static android.opengl.GLES32.GL_TEXTURE_2D;
import static android.opengl.GLES32.GL_TRIANGLES;
import static android.opengl.GLES32.GL_UNSIGNED_INT;
import static android.opengl.GLES32.glActiveTexture;
import static android.opengl.GLES32.glBindBuffer;
import static android.opengl.GLES32.glBindTexture;
import static android.opengl.GLES32.glBindVertexArray;
import static android.opengl.GLES32.glBufferData;
import static android.opengl.GLES32.glDrawArrays;
import static android.opengl.GLES32.glDrawElements;
import static android.opengl.GLES32.glEnableVertexAttribArray;
import static android.opengl.GLES32.glLineWidth;
import static android.opengl.GLES32.glUniform1i;
import static android.opengl.GLES32.glVertexAttribPointer;

public class Point extends I2DRenderer {

    private int mVertexArrayHandle;
    private float[] mVertexArray;
    private FloatBuffer mVertexBuffer;

    public Point(List<Vector2f> points) {
        mShader = new Shader(Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl"),
                Director.getInstance().loaderShaderFromAssets("shader/base_2d_fs.glsl"));

        init(points);
    }

    protected void init(List<Vector2f> points) {
        super.initRenderer();
        mWidth = 100;
        mHeight = 100;
        mOriginWidth = mWidth;
        mOriginHeight = mHeight;

        int stride = 6 * 4;

        mVertexArray = new float[6*points.size()];
        mVertexBuffer = BufferUtil.createFloatBuffer(mVertexArray.length, mVertexArray);

        updatePoints(points);

        mVertexArrayHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3*4);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    public void updatePoints(List<Vector2f> points) {
        for (int i = 0; i < points.size(); i++) {
            mVertexArray[i*6 + 0] = points.get(i).x;
            mVertexArray[i*6 + 1] = points.get(i).y;
            mVertexArray[i*6 + 2] = 0;

            mVertexArray[i*6 + 3] = 1.0f;
            mVertexArray[i*6 + 4] = 1.0f;
            mVertexArray[i*6 + 5] = 1.0f;
        }
        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexArray);
        mVertexBuffer.position(0);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        mShader.setUniformFloat("pointSize", 8);

        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_DYNAMIC_DRAW);

        glDrawArrays(GL_POINTS, 0, mVertexArray.length/6);
    }
}
