package com.shark.dynamics.graphics.renderer.r3d.particlesystem;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.r2d.I2DRenderer;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.Particle;
import com.shark.dynamics.graphics.renderer.r3d.I3DRenderer;
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
import java.util.Collections;
import java.util.Comparator;
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
import static android.opengl.GLES20.GL_TRUE;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDepthMask;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glDrawElementsInstanced;
import static android.opengl.GLES30.glVertexAttribDivisor;

public class ParticleSystem3D extends I3DRenderer {

    private static final String TAG = "ParticleSystem";

    private long mTimeLapse;
    private int mGenDuration = 200;
    private ParticleType3D mType = ParticleType3D.kCylinder;
    private float mXPos;
    private float mYPos;
    private float mZPos;
    private int mGenParticleCount = 1;
    private boolean mUseDecreaseScale;
    private boolean mColorOverlay;
    private Vector3f mTintColor;
    private float mMaxAlpha = 1.0f;
    private long mMaxLifeTime = 5000;

    private List<Particle> mParticles = new ArrayList<>();
    private int mMaxParticles = 3000;

    private int mInstanceModelArrayHandle;
    private Matrix4f[] mInstanceModels;
    private FloatBuffer mInstanceModelBuffer;
    private float[] mInstanceModelArray;

    private int mInstanceAlphaArrayHandle;
    private FloatBuffer mInstanceAlphaBuffer;
    private float[] mInstanceAlphaArray;

    private Texture mTexture;

    private boolean mDisableDepth = true;

    public ParticleSystem3D() {
        String particleVSShader = Director.getInstance().loaderShaderFromAssets("shader/instance_vs.glsl");
        String particleFSShader = Director.getInstance().loaderShaderFromAssets("shader/texture_2d/instance_alpha_tex_fs.glsl");
        init(particleVSShader, particleFSShader, "images/particle.png");
    }

    public ParticleSystem3D(String particleTexPath) {
        this(particleTexPath, 3000);
    }

    public ParticleSystem3D(String particleTexPath, int maxParticles) {
        String particleVSShader = Director.getInstance().loaderShaderFromAssets("shader/instance_vs.glsl");
        String particleFSShader = Director.getInstance().loaderShaderFromAssets("shader/texture_2d/instance_alpha_tex_fs.glsl");
        mMaxParticles = maxParticles;
        init(particleVSShader, particleFSShader, particleTexPath);
    }

    public ParticleSystem3D(String vs, String fs, String particleTexPath) {
        init(vs, fs, particleTexPath);
    }

    private void init(String vs, String fs, String particleTexPath) {

        mShader = new Shader(vs, fs);
        initRenderer();
        mTexture = Director.getInstance().findTexture(particleTexPath);

        float[] vertices = {
                -1.0f, -1.0f, 0.0f,   1.0f, 0.0f, 0.0f,  0.0f, 0.0f,
                1.0f, -1.0f,  0.0f,   0.0f, 1.0f, 0.0f,  1.0f, 0.0f,
                1.0f,  1.0f,  0.0f,   0.0f, 0.0f, 1.0f,  1.0f, 1.0f,
                -1.0f, 1.0f,  0.0f,   1.0f, 1.0f, 0.0f,  0.0f, 1.0f,
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

        if (mDisableDepth) {
            glDepthMask(false);
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
                if (particle == null) {
                    return;
                }
                mParticles.add(particle);
            }
//            if (mType == ParticleType.kUniverseAsh) {
//                for (int i = 0; i < 5; i++) {
//                    mParticles.add(InternalParticleGenerator.genUniverseAsh(mXPos, mYPos, mRadius, 4));
//                }
//            }
        }

//        Collections.sort(mParticles, new Comparator<Particle>() {
//            @Override
//            public int compare(Particle o1, Particle o2) {
//                return Float.compare(o1.z, o2.z);
//            }
//        });

        int particleIndex = 0;
        Iterator<Particle> it = mParticles.iterator();
        while (it.hasNext() && particleIndex < mMaxParticles) {
            Particle particle = it.next();
            if (!particle.isAlive()) {
                it.remove();
                continue;
            }

            particle.update(delta);

            //translateTo(particle.x, particle.y, particle.z);
            particle.model.identity();
            particle.model.translate(particle.x, particle.y, particle.z);

            float xScale = 0.25f * (mUseDecreaseScale ? particle.getDecreaseScale() : particle.xScale);
            float yScale = 0.25f * (mUseDecreaseScale ? particle.getDecreaseScale() : particle.yScale);

            particle.model.rotate((float) Math.toRadians(particle.rotate), 0,0,1);
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

        glDepthMask(true);
        glDisable(GL_BLEND);
        glBindVertexArray(0);
    }

    @Override
    public boolean isCustomModelMatrix() {
        return true;
    }

    public void setParticleType(ParticleType3D type) {
        mType = type;
    }

    public void setPosition(float x, float y, float z) {
        mXPos = x;
        mYPos = y;
        mZPos = z;
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

    public void setDisableDepthMask(boolean depth) {
        mDisableDepth = depth;
    }

    public void setMaxAlpha(float alpha) {
        mMaxAlpha = alpha;
    }

    public void setMaxLifeTime(long time) {
        mMaxLifeTime = time;
    }

    private Particle genParticle() {
        if (mType == ParticleType3D.kCylinder) {
            return InternalParticleGenerator3D.genCylinderParticle(mXPos, mYPos, mZPos);
        } else if (mType == ParticleType3D.kMovingStar) {
            return InternalParticleGenerator3D.genMovingStar();
        } else if (mType == ParticleType3D.kFlame) {
            return InternalParticleGenerator3D.genFlameParticle(mXPos, mYPos, mZPos);
        } else if (mType == ParticleType3D.kSnow) {
            return InternalParticleGenerator3D.genSnowParticle(mMaxAlpha, mMaxLifeTime);
        }
        return null;
    }
}
