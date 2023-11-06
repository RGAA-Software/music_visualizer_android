package com.shark.dynamics.graphics.renderer.r2d.particlesystem;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.r2d.I2DRenderer;
import com.shark.dynamics.graphics.renderer.texture.Texture;
import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;
import com.shark.dynamics.graphics.util.MatrixUtil;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_ONE;
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
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glDrawElementsInstanced;
import static android.opengl.GLES30.glVertexAttribDivisor;

public class ParticleSystem extends I2DRenderer {

    private static final String TAG = "ParticleSystem";

    private long mTimeLapse;
    private int mGenDuration = 200;
    private ParticleType mType = ParticleType.kSnow;
    private float mXPos;
    private float mYPos;
    private int mGenParticleCount = 1;
    private boolean mUseDecreaseScale;
    private boolean mColorOverlay;
    private Vector3f mTintColor;

    private List<Particle> mParticles = new ArrayList<>();
    private int mMaxParticles = 300;

    private int mInstanceModelArrayHandle;
    private Matrix4f[] mInstanceModels;
    private FloatBuffer mInstanceModelBuffer;
    private float[] mInstanceModelArray;

    private int mInstanceAlphaArrayHandle;
    private FloatBuffer mInstanceAlphaBuffer;
    private float[] mInstanceAlphaArray;

    private float mRadius = 300;
    private float mScreenScale = 1.0f;
    private float mBaseScale = 1.0f;
    private Texture mTexture;

    public ParticleSystem() {
        String particleVSShader = Director.getInstance().loaderShaderFromAssets("shader/instance_vs.glsl");
        String particleFSShader = Director.getInstance().loaderShaderFromAssets("shader/texture_2d/instance_alpha_tex_fs.glsl");
        init(particleVSShader, particleFSShader, "images/particle.png");
    }

    public ParticleSystem(String particleTexPath) {
        this(particleTexPath, 300);
    }

    public ParticleSystem(String particleTexPath, int maxParticles) {
        String particleVSShader = Director.getInstance().loaderShaderFromAssets("shader/instance_vs.glsl");
        String particleFSShader = Director.getInstance().loaderShaderFromAssets("shader/texture_2d/instance_alpha_tex_fs.glsl");
        mMaxParticles = maxParticles;
        init(particleVSShader, particleFSShader, particleTexPath);
    }

    public ParticleSystem(String vs, String fs, String particleTexPath) {
        init(vs, fs, particleTexPath);
    }

    private void init(String vs, String fs, String particleTexPath) {

        mShader = new Shader(vs, fs);
        initRenderer();
        mTexture = Director.getInstance().findTexture(particleTexPath);

        mWidth = mTexture.getWidth();
        mHeight = mTexture.getHeight();
        mOriginWidth = mWidth;
        mOriginHeight = mHeight;

        float[] vertices = {
                0,  0,  0,          1.0f, 0.0f, 0.0f,   0.0f, 0.0f,
                mWidth, 0, 0,       0.0f, 1.0f, 0.0f,   1.0f, 0.0f,
                mWidth, mHeight, 0, 0.0f, 0.0f, 1.0f,   1.0f, 1.0f,
                0,  mHeight, 0,     1.0f, 1.0f, 0.0f,   0.0f, 1.0f
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

        //
        mInstanceModels = new Matrix4f[mMaxParticles];
        for (int i = 0; i < mMaxParticles; i++) {
            mInstanceModels[i] = new Matrix4f();
        }
        mInstanceModelArray = new float[mInstanceModels.length * 16];

        mInstanceModelBuffer = ByteBuffer
                .allocateDirect(mInstanceModels.length*16*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        //
        mInstanceModelArrayHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mInstanceModelArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mInstanceModels.length*16*4, mInstanceModelBuffer, GL_DYNAMIC_DRAW);

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

        //
        mInstanceAlphaArray = new float[mMaxParticles];
        mInstanceAlphaBuffer = ByteBuffer
                .allocateDirect(mInstanceAlphaArray.length*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        mInstanceAlphaArrayHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mInstanceAlphaArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mInstanceAlphaArray.length*4, mInstanceAlphaBuffer, GL_DYNAMIC_DRAW);

        glEnableVertexAttribArray(7);
        glVertexAttribPointer(7, 1, GL_FLOAT, false, 0, 0);
        glVertexAttribDivisor(7, 1);

        glBindVertexArray(0);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        glEnable(GL_BLEND);
        if (mColorOverlay) {
            glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        } else {
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        }

        if (mTintColor != null) {
            mShader.setUniformInt("enableTintColor", 1);
            mShader.setUniformVec3("tintColor", mTintColor.x, mTintColor.y, mTintColor.z);
        }

        mTexture.active(mShader, 0);

        mTimeLapse += delta*1000;

        if (mTimeLapse > mGenDuration && mParticles.size() < mMaxParticles) {
            mTimeLapse = 0;
            for (int i = 0; i < mGenParticleCount; i++) {
                Particle particle = genParticle();
                mParticles.add(particle);
            }
//            if (mType == ParticleType.kUniverseAsh) {
//                for (int i = 0; i < 5; i++) {
//                    mParticles.add(InternalParticleGenerator.genUniverseAsh(mXPos, mYPos, mRadius, 4));
//                }
//            }
        }

        int particleIndex = 0;
        Iterator<Particle> it = mParticles.iterator();
        while (it.hasNext() && particleIndex < mMaxParticles) {
            Particle particle = it.next();
            if (!particle.isAlive()) {
                it.remove();
                continue;
            }

            particle.update(delta);

            translateTo(particle.x, particle.y, 0);
            particle.model.identity();
            particle.model.translate(particle.x, particle.y, 0);

            float xScale = 0.25f * (mUseDecreaseScale ? particle.getDecreaseScale() : particle.xScale);
            float yScale = 0.25f * (mUseDecreaseScale ? particle.getDecreaseScale() : particle.yScale);

            // move to local center
            //particle.model.translate(-mWidth / 2 * xScale, -mHeight / 2 * yScale, 0);
            //particle.model.translate(mWidth / 2 * xScale, mHeight / 2 * yScale, 0);

            particle.model.rotate((float) Math.toRadians(particle.rotate), 0,0,1);
            particle.model.translate(-mWidth / 2 * xScale, -mHeight / 2 * yScale, 0);

            particle.model.scale(mScale.x*xScale, mScale.y*yScale, 0);

            mInstanceAlphaArray[particleIndex] = particle.getDecreaseAlpha();
            mInstanceModels[particleIndex++] = particle.model;

        }

        // Models
        MatrixUtil.matrixArrayToFloatArray(mInstanceModels, mInstanceModelArray);

        mInstanceModelBuffer.position(0);
        mInstanceModelBuffer.put(mInstanceModelArray);
        mInstanceModelBuffer.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, mInstanceModelArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mInstanceModels.length*16*4, mInstanceModelBuffer, GL_DYNAMIC_DRAW);

        // Alphas
        mInstanceAlphaBuffer.position(0);
        mInstanceAlphaBuffer.put(mInstanceAlphaArray);
        mInstanceAlphaBuffer.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, mInstanceAlphaArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mInstanceAlphaArray.length*4, mInstanceAlphaBuffer, GL_DYNAMIC_DRAW);

        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, particleIndex);

        glDisable(GL_BLEND);
        glBindVertexArray(0);
    }

    @Override
    public boolean isCustomModelMatrix() {
        return true;
    }

    public void setParticleType(ParticleType type) {
        mType = type;
    }

    public void setPosition(float x, float y) {
        mXPos = x;
        mYPos = y;
    }

    public void setGenParticleCount(int count) {
        mGenParticleCount = count;
    }

    public void setGenDuration(int duration) {
        mGenDuration = duration;
    }

    public void setUseDecreaseScale(boolean use) {
        mUseDecreaseScale = use;
    }

    public void setColorOverlay(boolean overlay) {
        mColorOverlay = overlay;
    }

    public void setTintColor(Vector3f color) {
        mTintColor = color;
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }

    public void setScreenScale(float screenScale) {
        mScreenScale = screenScale;
    }

    public void setBaseScale(float scale) {
        mBaseScale = scale;
    }

    private Particle genParticle() {
        if (mType == ParticleType.kSnow) {
            return InternalParticleGenerator.genSnowParticle(mBaseScale);
        } else if (mType == ParticleType.kSpark) {
            return InternalParticleGenerator.genSparkParticle(mXPos, mYPos);
        } else if (mType == ParticleType.kTriangle) {
            return InternalParticleGenerator.genTriangleParticle(mXPos, mYPos);
        } else if (mType == ParticleType.kUniverseAsh) {
            return InternalParticleGenerator.genUniverseAsh(mXPos, mYPos, mRadius);
        } else if (mType == ParticleType.kBubble) {
            return InternalParticleGenerator.genBubbleParticle();
        } else if (mType == ParticleType.kRain) {
            return InternalParticleGenerator.genRainParticle(mScreenScale);
        } else if (mType == ParticleType.kGravity) {
            return InternalParticleGenerator.genGravityParticle(mScreenScale);
        } else if (mType == ParticleType.kMovingCenter) {
            return InternalParticleGenerator.genCenterParticle(mXPos, mYPos, mRadius);
        }
        return InternalParticleGenerator.genSnowParticle(mBaseScale);
    }
}
