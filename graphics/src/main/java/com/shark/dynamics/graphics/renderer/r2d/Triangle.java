package com.shark.dynamics.graphics.renderer.r2d;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.ProjectionType;
import com.shark.dynamics.graphics.renderer.texture.Texture;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINE_STRIP;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;

public class Triangle extends I2DRenderer {

    private Texture mTexture;
    private int mLineWidth = 6;
    private boolean mDrawStroke = true;
    private boolean mDrawFill;

    public Triangle() {
        super();
    }

    public Triangle(String vs, String fs) {
        super(vs, fs);
    }

    public Triangle(String vs, String gs, String fs) {
        super(vs, gs, fs);
    }

    @Override
    protected void initRenderer() {
        super.initRenderer();
        mType = ProjectionType.kProj2D;

        mWidth = 100;
        mHeight = (float) (mWidth * Math.pow(3, 1.0f/2)) / 2;
        mOriginWidth = mWidth;
        mOriginHeight = mHeight;

        float[] vertices = {
            0,  0,  0,              1.0f, 1.0f, 1.0f,   0.0f, 0.0f,
            mWidth, 0,  0,          1.0f, 1.0f, 1.0f,   1.0f, 0.0f,
            mWidth/2, mHeight, 0,   1.0f, 1.0f, 1.0f,   0.5f, 1.0f,
            0,  0,  0,              1.0f, 1.0f, 1.0f,   0.0f, 0.0f,
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

        glLineWidth(9);

        glBindVertexArray(0);
    }

    public void setTexture(Texture texture) {
        mTexture = texture;
    }

    public void setDrawStroke(boolean line) {
        mDrawStroke = line;
    }

    public void setLineWidth(int width) {
        mLineWidth = width;
    }

    public void setDrawFill(boolean fill) {
        mDrawFill = fill;
    }

    @Override
    public void render(float delta) {
        glBindVertexArray(mRenderVAO);
        mShader.use();

        glUniformMatrix4fv(mShader.getUniformLocation("view"),
                1,
                false,
                Director.getInstance().getViewMatrix());

        glUniformMatrix4fv(mShader.getUniformLocation("projection"),
                1,
                false,
                Director.getInstance().getOrthographicMatrix());

        if (mTexture != null) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, mTexture.getTextureId());
            glUniform1i(mShader.getUniformLocation("image"), 0);
        }

        mModelMatrix = mModelMatrix.identity();
        mModelMatrix = mModelMatrix.translate(mTranslate);

        float yOffset = (float) (Math.tan(Math.toRadians(30.0f)) * mWidth/2);
        mModelMatrix = mModelMatrix.translate(mWidth/2, yOffset, 0);
        mModelMatrix = mModelMatrix.rotate((float)Math.toRadians(mRotateDegree), 0, 0, 1);
        mModelMatrix = mModelMatrix.translate(-mWidth/2, -yOffset, 0);

        mModelMatrix = mModelMatrix.scale(mScale);

        glUniformMatrix4fv(mShader.getUniformLocation("model"),
                1,
                false,
                getModelBuffer());

        if (mDrawStroke) {
            glLineWidth(mLineWidth);
            glDrawArrays(GL_LINE_STRIP, 0, 4);
        }
        if (mDrawFill) {
            glDrawArrays(GL_TRIANGLES, 0, 3);
        }
    }
}
