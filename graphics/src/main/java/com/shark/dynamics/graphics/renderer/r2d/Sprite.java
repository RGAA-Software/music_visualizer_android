package com.shark.dynamics.graphics.renderer.r2d;

import android.content.res.Resources;

import com.shark.dynamics.graphics.renderer.DefaultShader;
import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.texture.Texture;
import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import org.joml.Vector2f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;

public class Sprite extends I2DRenderer {

    private Texture mTexture;

    public enum SpriteType {
        kRect,
        kCircle,
    }

    private SpriteType mType = SpriteType.kRect;

    public Sprite(String path) {
        mShader = new Shader(DefaultShader.sDefaultVertexShader, DefaultShader.sDefaultImageFragmentShader);
        mTexture = Director.getInstance().findTexture(path);
        initSprite();
    }

    public Sprite(String path, SpriteType type) {
        this(path, type, DefaultShader.sDefaultVertexShader, DefaultShader.sDefaultImageFragmentShader);
    }

    public Sprite(String path, SpriteType type, String vs, String fs) {
        mShader = new Shader(vs, fs);
        mTexture = Director.getInstance().findTexture(path);
        mType = type;
        initForType();
    }

    public Sprite(Texture texture, SpriteType type) {
        this(texture, type, DefaultShader.sDefaultVertexShader, DefaultShader.sDefaultImageFragmentShader);
    }

    public Sprite(Texture texture, SpriteType type, String vs, String fs) {
        mShader = new Shader(vs, fs);
        mTexture = texture;
        mType = type;
        initForType();
    }

    private void initSprite() {
        super.initRenderer();
        initWithScale(1, 1);
    }

    public void setAsBackground() {
        int texWidth = mTexture.getWidth();
        int texHeight = mTexture.getHeight();

        // for full screen rendering, include status bar, navigation bar
        // use Usable screen size for specify size rendering.
        Vector2f screenSize = Director.getInstance().getDevice().getScreenRealSize();
        float screenWidth = screenSize.x;
        float screenHeight = screenSize.y;

        float scale = Math.max(screenWidth*1.0f/texWidth, screenHeight*1.0f/texHeight);

        float targetWidth = texWidth*scale;
        float targetHeight = texHeight*scale;
        float xOffset = -((targetWidth-screenWidth)/2);
        float yOffset = -((targetHeight-screenHeight)/2);

        translateTo(xOffset, yOffset, 0);

        initWithScale(scale, scale);
    }

    private void initWithScale(float xScale, float yScale) {
        glBindVertexArray(mRenderVAO);
        mWidth = mTexture.getWidth();
        mHeight = mTexture.getHeight();
        mOriginWidth = mWidth;
        mOriginHeight = mHeight;

        float[] vertices = {
            0,  0,  0,          1.0f, 0.0f, 0.0f,   0.0f, 0.0f,
            mWidth*xScale, 0, 0,       0.0f, 1.0f, 0.0f,   1.0f, 0.0f,
            mWidth*xScale, mHeight*yScale, 0, 0.0f, 0.0f, 1.0f,   1.0f, 1.0f,
            0,  mHeight*yScale, 0,     1.0f, 1.0f, 0.0f,   0.0f, 1.0f
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

    private void initForType() {
        super.initRenderer();

        float[] buffer = null;
        if (mType == SpriteType.kCircle) {
            int slice = 50;
            mWidth = mTexture.getWidth();
            mHeight = mTexture.getHeight();
            mOriginWidth = mWidth;
            mOriginHeight = mHeight;

            float radius = mWidth/2;
            float centerX = mWidth/2;
            float centerY = mHeight/2;

            buffer = new float[8 * (slice+1+1)];
            appendVertexLine(0, buffer, new float[]{centerX, centerY, 0, 1,1,1, 0.5f, 0.5f});

            float itemAngel = 360.0f/slice;
            for (int i = 0; i <= slice; i++) {
                float angel = itemAngel * i;
                float cos = (float) Math.cos(Math.toRadians(angel));
                float sin = (float) Math.sin(Math.toRadians(angel));
                float x = cos * radius + radius;
                float y = sin * radius + radius;
                float z = 0;

                float r, g, b;
                r = g = b = 1.0f;

                float u = (cos + 1)/2;
                float v = (sin + 1)/2;

                appendVertexLine(i+1, buffer, new float[]{x,y,z, r,g,b, u,v});
            }

        } else if (mType == SpriteType.kRect) {
            // use default ...
            initSprite();
        }

        if (buffer == null) {
            return;
        }

        FloatBuffer data = BufferUtil.createFloatBuffer(buffer.length, buffer);

        int verticesArray = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, verticesArray);
        glBufferData(GL_ARRAY_BUFFER, buffer.length*4, data, GL_STATIC_DRAW);

        int stride = 8 * 4;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3*4);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6*4);
        glEnableVertexAttribArray(2);

        glBindVertexArray(0);
    }

    /**
     * @param lineIdx
     * @param buffer
     * @param line {xyz, rgb, uv}
     */
    private void appendVertexLine(int lineIdx, float[] buffer, float[] line) {
        int lineItemCount = 8;
        int idx = lineItemCount * lineIdx;
        buffer[idx + 0] = line[0];
        buffer[idx + 1] = line[1];
        buffer[idx + 2] = line[2];

        buffer[idx + 3] = line[3];
        buffer[idx + 4] = line[4];
        buffer[idx + 5] = line[5];

        buffer[idx + 6] = line[6];
        buffer[idx + 7] = line[7];
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (mTexture == null) {
            return;
        }
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTexture.getTextureId());
        glUniform1i(mShader.getUniformLocation("image"), 0);

        if (mType == SpriteType.kRect) {
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        } else if (mType == SpriteType.kCircle) {

            glDrawArrays(GL_TRIANGLE_FAN, 0, 52);
        }
        glBindVertexArray(0);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (mTexture != null) {
            mTexture.dispose();
        }
    }
}
