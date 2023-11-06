package com.shark.dynamics.graphics.renderer.r3d;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.texture.Texture;
import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;

public class Sprite3D extends I3DRenderer {

    private Texture mTexture;
    private Sprite.SpriteType mSpriteType;

    public Sprite3D(String texPath) {
        this(texPath, Sprite.SpriteType.kRect);
    }

    public Sprite3D(String texPath, Sprite.SpriteType type) {
        this(texPath, type, 1.0f, 1.0f);
    }

    public Sprite3D(String texPath, Sprite.SpriteType type, float xScale, float yScale) {
        String vs = Director.getInstance().loaderShaderFromAssets("shader/3d/base_vs.glsl");
        String fs = Director.getInstance().loaderShaderFromAssets("shader/3d/base_tex_fs.glsl");
        mShader = new Shader(vs, fs);
        initRenderer();
        mSpriteType = type;
        mTexture = Director.getInstance().findTexture(texPath);

        init(type, xScale, yScale);
    }

    private void init(Sprite.SpriteType type, float x_size, float y_size) {
        float[] vertices = null;
        int stride = 0;
        if (type == Sprite.SpriteType.kRect) {
            vertices = new float[] {
                    -1.0f * x_size, -1.0f * y_size, 0.0f,   1.0f, 0.0f, 0.0f,  0.0f, 0.0f,                        0,0,1,
                    1.0f * x_size, -1.0f * y_size,  0.0f,   0.0f, 1.0f, 0.0f,  1.0f * x_size, 0.0f,              0,0,1,
                    1.0f * x_size,  1.0f * y_size,  0.0f,   0.0f, 0.0f, 1.0f,  1.0f * x_size, 1.0f * y_size,    0,0,1,
                    -1.0f * x_size, 1.0f * y_size,  0.0f,   1.0f, 1.0f, 0.0f,  0.0f, 1.0f * y_size,              0,0,1,
            };

            int[] indices = {
                    0, 1, 2,
                    2, 3, 0
            };

            stride = 11 * 4;
            //
            IntBuffer indicesBuffer = BufferUtil.createIntBuffer(indices.length, indices);

            int indexArray = GLUtil.glGenBuffer();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexArray);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.length*4, indicesBuffer, GL_STATIC_DRAW);

        } else if (type == Sprite.SpriteType.kCircle) {
            stride = 8 * 4;
            int slice = 50;
            int line_float_size = 8;
            int vertices_size = (slice + 1) * line_float_size;
            vertices = new float[vertices_size];

            addCircleVertices(vertices, 0, 0, 0, 0, 1, 1, 1, 0.5f, 0.5f);

            float item_angel = 360.0f/(slice - 1);
            for (int i = 0; i < slice; i++) {
                float angel = i * item_angel;
                float cos = (float) Math.cos(Math.toRadians(angel));
                float sin = (float) Math.sin(Math.toRadians(angel));

                float x = cos;
                float y = sin;
                float z = 0;

                float u = (cos + 1)/2;
                float v = (sin + 1)/2;

                addCircleVertices(vertices, i + 1, x, y, z, 1, 1, 1, u, v);
            }

        }

        assert vertices != null;

        FloatBuffer vertexBuffer = ByteBuffer
                .allocateDirect(vertices.length*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        //
        int vertexHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
        glBufferData(GL_ARRAY_BUFFER, vertices.length*4, vertexBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3*4);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6*4);
        glEnableVertexAttribArray(2);

        if (mSpriteType == Sprite.SpriteType.kRect) {
            glVertexAttribPointer(3, 3, GL_FLOAT, false, stride, 8 * 4);
            glEnableVertexAttribArray(3);
        }

        glBindVertexArray(0);
    }

    private void addCircleVertices(float[] vertices, int line, float x, float y, float z, float r, float g, float b, float u, float v) {
        vertices[line*8 + 0] = x;
        vertices[line*8 + 1] = y;
        vertices[line*8 + 2] = z;

        vertices[line*8 + 3] = r;
        vertices[line*8 + 4] = g;
        vertices[line*8 + 5] = b;

        vertices[line*8 + 6] = u;
        vertices[line*8 + 7] = v;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (mTexture != null) {
            mTexture.active(mShader, 0);
        }

        if (mSpriteType == Sprite.SpriteType.kRect) {
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        } else if (mSpriteType == Sprite.SpriteType.kCircle) {
            glDrawArrays(GL_TRIANGLE_FAN, 0, 51);
        }

        glBindVertexArray(0);
    }
}
