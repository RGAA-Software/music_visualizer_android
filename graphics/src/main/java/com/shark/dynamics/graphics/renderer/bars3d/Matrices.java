package com.shark.dynamics.graphics.renderer.bars3d;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.r2d.font.Character;
import com.shark.dynamics.graphics.renderer.r2d.font.Font;
import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
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

/**
 * Not efficient, for so many draw call
 */
public class Matrices extends IBars3DRenderer {

    class Piece {
        public long timelapses;
        public List<Character> chars = new ArrayList<>();
        public List<Integer> rotates = new ArrayList<>();
        public List<Float> rotateSpeed = new ArrayList<>();

        public Font font;

        public Vector3f translate;

        public float moving = 0;

        public Vector3f color = new Vector3f(0.2f, 0.9f, 0.3f);

        public Piece(Font font) {
            this.font = font;
            Random random = new Random();
            int num = random.nextInt(15);
            if (num < 8) {
                num = 8;
            }
            for (int i = 0; i < num; i++) {
                Character c = font.mapCharacters.get(33 + random.nextInt(60));
                if (c != null) {
                    if (c.id == 45 || c.id == 95) {
                        c = font.mapCharacters.get(36);
                    }
                }
                chars.add(c);
            }

            for (int i = 0; i < chars.size(); i++) {
                rotates.add(random.nextInt(4));
                rotateSpeed.add(random.nextFloat() * 5);
            }

            float directionX = 0.5f - random.nextFloat();
            float directionY = random.nextFloat() + 1.0f;
            float directionZ = 0.5f - random.nextFloat();
            translate = new Vector3f(directionX*3.5f, directionY*4.5f, directionZ*10);
            moving = random.nextFloat();
            if (moving < 0.5f) {
                moving = 0.5f;
            }
            moving /= 30;
        }

        public boolean isAlive() {
            return timelapses < 8*1000;
        }

        public void render(float delta) {
            timelapses += delta * 1000;
            translate.y -= moving;
            for (int i = 0; i < chars.size(); i++) {
                Character ch = chars.get(i);
                float texWidth = font.getTexWidth();
                // 88 is font base
                // 2400/1080 is aspect ratio
                // 3/2 is factor to extend gap
                // /1200 is scale height to 0-1, because full screen is [-1, 1], is 2
                float cursorOffset = -88 *i*(2400*1.0f/1080)/1200 * 3.0f/2;

                float s = ch.x * 1.0f/texWidth;
                float t = (texWidth-ch.y)*1.0f/texWidth;
                float ws = ch.width*1.0f/texWidth;
                float wt = ch.height*1.0f/texWidth;

                float commonScale = 0.05f;
                float xScale = ch.width*1.0f/ch.height * commonScale;
                float yScale = 1.0f * commonScale;

                //
                //vertices[1] = cursorOffset;
                vertices[3] = s;
                vertices[4] = t - wt;

                //
                //vertices[5*1 + 1] =
                vertices[5*1 + 3] = s+ws;
                vertices[5*1 + 4] = t-wt;

                //
                vertices[5*2 + 3] = s+ws;
                vertices[5*2 + 4] = t;

                //
                vertices[5*3 + 3] = s;
                vertices[5*3 + 4] = t;

                vertexBuffer.position(0);
                vertexBuffer.put(vertices);
                vertexBuffer.position(0);

                glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
                glBufferData(GL_ARRAY_BUFFER, vertices.length*4, vertexBuffer, GL_STATIC_DRAW);

                mModelMatrix.identity();
                mModelMatrix.translate(translate);
                mModelMatrix.translate(0, cursorOffset, 0);
                int rotate = rotates.get(i);
                float rs = rotateSpeed.get(i);
                if (rotate == 0) {
                    mModelMatrix.rotate(mRotate * rs, 1, 0, 0);
                } else if (rotate == 1) {
                    mModelMatrix.rotate(mRotate * rs, 0, 1, 0);
                } else {
                    mModelMatrix.rotate(mRotate * rs, 0, 0, 1);
                }
                mModelMatrix.scale(xScale, yScale, 1.0f);
                mShader.setUniformMatrix4fv("model", getModelBuffer());

                mShader.setUniformVec3("color", color.x, color.y, color.z);

                //float directionZ = 0.5f - random.nextFloat();
                // directionZ * 10 => range is [-5, 5]
                // +5 => [0, 10]
                // /8 => [0, 5/4] => used for alpha
                mShader.setUniformFloat("alpha", (translate.z + 5.0f) / 8.0f);

                glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
            }
        }

    }

    private Random mRandom = new Random();
    private List<Piece> mPieces = new ArrayList<>();
    private float[] vertices;
    private int mVertexArrayHandle;
    private FloatBuffer vertexBuffer;
    private float mRotate;
    private Font mFont;
    private long mGenTimeLapses;

    public Matrices() {
        String vs = Director.getInstance().loaderShaderFromAssets("shader/3d/base_vs.glsl");
        String fs = Director.getInstance().loaderShaderFromAssets("shader/3d/tex_color_alpha_fs.glsl");
        mShader = new Shader(vs, fs);
        initRenderer();

        mFont = Director.getInstance().getDefaultFont();

        int stride = 0;
        vertices = new float[] {
                -1.0f, -1.0f, 0.0f,   0.0f, 0.0f,
                1.0f, -1.0f,  0.0f,   1.0f, 0.0f,
                1.0f,  1.0f,  0.0f,   1.0f, 1.0f,
                -1.0f, 1.0f,  0.0f,   0.0f, 1.0f,
        };

        vertexBuffer = ByteBuffer
                .allocateDirect(vertices.length*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        int[] indices = {
                0, 1, 2,
                2, 3, 0
        };

        stride = 5 * 4;

        IntBuffer indicesBuffer = BufferUtil.createIntBuffer(indices.length, indices);

        int indexArray = GLUtil.glGenBuffer();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexArray);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.length*4, indicesBuffer, GL_STATIC_DRAW);

        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        //
        mVertexArrayHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, vertices.length*4, vertexBuffer, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 3*4);
        glEnableVertexAttribArray(2);

        glBindVertexArray(0);
    }

    @Override
    public boolean isCustomModelMatrix() {
        return true;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        mGenTimeLapses += delta * 1000;
        mRotate += delta;

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        mFont.texture.active(mShader, 1);

        Iterator<Piece> it = mPieces.iterator();
        while (it.hasNext()) {
            Piece piece = it.next();
            if (!piece.isAlive()) {
                it.remove();
                continue;
            }
            piece.render(delta);
        }

        if (mPieces.size() < 16 && mGenTimeLapses > 500) {
            mGenTimeLapses = 0;
            genPiece();
        }

        glDisable(GL_BLEND);
    }

    private void genPiece() {
        Piece p = new Piece(mFont);
        mPieces.add(p);
    }
}
