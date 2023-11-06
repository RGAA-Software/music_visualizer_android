package com.shark.dynamics.graphics.renderer.r2d.font;

import android.util.Log;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.r2d.I2DRenderer;
import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
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
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;

public class Label extends I2DRenderer {

    private static final String TAG = "Label";

    private List<Character> mCharacters;
    private Font mFont;

    private FloatBuffer mVertexBuffer;
    private float[] mVertexArray;
    private int mVertexHandle;

    public Label(String text) {
        String vs = Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl");
        String fs = Director.getInstance().loaderShaderFromAssets("shader/texture_2d/base_2d_tex_fs.glsl");
        mShader = new Shader(vs, fs);
        initRenderer();

        mFont = Director.getInstance().getDefaultFont();

        mCharacters = mFont.getCharacters(text);

        mWidth = 100;
        mHeight = 100;
        mOriginWidth = mWidth;
        mOriginHeight = mHeight;

        mVertexArray = new float[] {
                0,  0,  0,          1.0f, 0.0f, 0.0f,   0.0f, 0.0f,
                mWidth, 0, 0,       0.0f, 1.0f, 0.0f,   1.0f, 0.0f,
                mWidth, mHeight, 0, 0.0f, 0.0f, 1.0f,   1.0f, 1.0f,
                0,  mHeight, 0,     1.0f, 1.0f, 0.0f,   0.0f, 1.0f,
                0,  0,  0,          1.0f, 0.0f, 0.0f,   0.0f, 0.0f,
        };

        int stride = 8 * 4;

        mVertexBuffer = BufferUtil.createFloatBuffer(mVertexArray);

        mVertexHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3*4);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6*4);
        glEnableVertexAttribArray(2);

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

        mFont.texture.active(mShader, 0);

        int cursor = 0;
        for (Character c : mCharacters) {
            updateSize(cursor, c.x, c.y, c.width, c.height, c.xOffset, c.yOffset, c.xAdvance);

            cursor += c.xAdvance;

            mVertexBuffer.position(0);
            mVertexBuffer.put(mVertexArray);
            mVertexBuffer.position(0);

            glBindBuffer(GL_ARRAY_BUFFER, mVertexHandle);
            glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_STATIC_DRAW);

            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        }
        glDisable(GL_BLEND);
    }

    public void updateText(String text) {
        mCharacters = mFont.getCharacters(text);
    }

    public float getWidth() {
        int length = 0;
        for (Character c : mCharacters) {
            length += c.xAdvance;
        }
        return length;
    }

    private void updateSize(int cursor, int u, int v, int width, int height, int xOffset, int yOffset, int xAdvance) {

        int startX = cursor + xOffset;
        int endX = startX + width;
        // todo parsing from file
        // 88 is base, see in .fnt file
        int endY = 88 - yOffset;
        int startY = -(height - endY);

        float texWidth = mFont.texture.getWidth()*1.0f;
        float s = u * 1.0f/texWidth;
        // todo parsing from file
        // 512 is texture size, change it if font changed
        float t = (512-v)*1.0f/texWidth;
        float ws = width*1.0f/texWidth;
        float wt = height*1.0f/texWidth;

        mVertexArray[0*8 + 0] = startX;
        mVertexArray[0*8 + 1] = startY;
        mVertexArray[0*8 + 6] = s;
        mVertexArray[0*8 + 7] = t-wt;

        mVertexArray[1*8 + 0] = endX;
        mVertexArray[1*8 + 1] = startY;
        mVertexArray[1*8 + 6] = s+ws;
        mVertexArray[1*8 + 7] = t-wt;

        mVertexArray[2*8 + 0] = endX;
        mVertexArray[2*8 + 1] = endY;
        mVertexArray[2*8 + 6] = s+ws;
        mVertexArray[2*8 + 7] = t;

        mVertexArray[3*8 + 0] = startX;
        mVertexArray[3*8 + 1] = endY;
        mVertexArray[3*8 + 6] = s;
        mVertexArray[3*8 + 7] = t;
    }
}
