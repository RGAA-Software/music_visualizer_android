package com.shark.dynamics.graphics.renderer.r2d;

import com.shark.dynamics.graphics.renderer.texture.Texture;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;
import static android.opengl.GLES32.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Rectangle extends I2DRenderer {

    protected Texture mTexture;
    private int mLineWidth = 6;
    private boolean mDrawStroke = true;
    private boolean mDrawFill;

    public Rectangle() {
        super();
    }

    public Rectangle(String vs, String fs) {
        super(vs, fs);
    }

    public Rectangle(String vs, String gs, String fs) {
        super(vs, gs, fs);
    }

    @Override
    protected void initRenderer() {
        super.initRenderer();
        mWidth = 100;
        mHeight = 100;
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

        IntBuffer indicesBuffer = BufferUtil.createIntBuffer(indices.length, indices);

        int indexArray = GLUtil.glGenBuffer();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexArray);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.length*4, indicesBuffer, GL_STATIC_DRAW);

        glBindVertexArray(0);
    }

    public void setTexture(Texture texture) {
        mTexture = texture;
    }

    public void setDrawStroke(boolean draw) {
        mDrawStroke = draw;
    }

    public void setLineWidth(int width) {
        mLineWidth = width;
    }

    public void setDrawFill(boolean fill) {
        mDrawFill = fill;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (mTexture != null) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, mTexture.getTextureId());
            glUniform1i(mShader.getUniformLocation("image"), 0);
        }

        if (mDrawStroke) {
            glLineWidth(mLineWidth);
            glDrawArrays(GL_LINE_STRIP, 0, 5);
        }
        if (mDrawFill) {
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        }
    }
}
