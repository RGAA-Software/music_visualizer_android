package com.shark.dynamics.graphics.renderer.visualizer3D;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars3d.Matrices;
import com.shark.dynamics.graphics.renderer.r3d.Camera;
import com.shark.dynamics.graphics.renderer.r3d.light.PointLight;
import com.shark.dynamics.graphics.renderer.r3d.model.Model;
import com.shark.dynamics.graphics.renderer.r3d.model.ModelLoader;
import com.shark.dynamics.graphics.renderer.visualizer.IGLVisualizer;
import com.shark.dynamics.graphics.shader.Shader;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Random;

import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glEnable;

public class GLEarth extends IGLVisualizer {

    private Model mEarth;

    private float mTimeLapses;

    private Model mRocks;
    private Matrix4f[] mMatrices;
    private float[] mRadius;
    private float[] mAngels;
    private float[] mAngelForward;
    private float[] mRotate;
    private float[] mScale;
    private float[] mYOffset;

    private long mLoopCount;

    private Random mRandom = new Random();

    public GLEarth() {

        String modelPath = Director.getInstance().getDevice().getCachePath()+"/earth.obj";
        String vs = Director.getInstance().loaderShaderFromAssets("shader/3d/base_vs.glsl");
        String fs = Director.getInstance().loaderShaderFromAssets("shader/3d/model_tex_light_fs.glsl");
        Shader shader = new Shader(vs, fs);
        mEarth = ModelLoader.loadModel(modelPath, shader);
        mEarth.scaleTo(0.0029f);
        mEarth.addPointLight(new PointLight(new Vector3f(1.0f, 2.5f, 1.0f), new Vector3f(1f, 1f, 1f)));
        mEarth.addPointLight(new PointLight(new Vector3f(1.0f, 2.5f, 1.0f), new Vector3f(1f, 1f, 1f)));
        mEarth.addPointLight(new PointLight(new Vector3f(1.0f, 2.5f, 1.0f), new Vector3f(1f, 1f, 1f)));
        mEarth.setShowPointLights(false);

        int rockSize = 600;
        float radius = 3.0f;
        float radiusRange = 1.6f;
        mMatrices = new Matrix4f[rockSize];
        mRadius = new float[rockSize];
        mAngels = new float[rockSize];
        mAngelForward = new float[rockSize];
        mRotate = new float[rockSize];
        mScale = new float[rockSize];
        mYOffset = new float[rockSize];

        for (int i = 0; i < rockSize; i++) {
            Matrix4f model = new Matrix4f();

            mAngelForward[i] = mRandom.nextFloat();

            float angel = mRandom.nextFloat() * 360;
            mAngels[i] = angel;

            float cos = (float)Math.cos(Math.toRadians(angel));
            float sin = (float)Math.sin(Math.toRadians(angel));

            float targetRadius = radius + mRandom.nextFloat() * radiusRange;
            mRadius[i] = targetRadius;

            float x = cos * targetRadius;
            float z = sin * targetRadius;
            float y = 0.5f - mRandom.nextFloat();
            mYOffset[i] = y;
            model.translate(x, mYOffset[i], z);

            float rotateAngel = mRandom.nextFloat() * 360;
            mRotate[i] = (float)Math.toRadians(rotateAngel);
            model.rotate(mRotate[i], 0, 1, 0);

            mScale[i] = 0.05f * (mRandom.nextFloat())/2.0f;
            model.scale(mScale[i]);

            mMatrices[i] = model;
        }

        vs = Director.getInstance().loaderShaderFromAssets("shader/3d/instance_model_vs.glsl");
        fs = Director.getInstance().loaderShaderFromAssets("shader/3d/model_tex_light_fs.glsl");
        Shader rockShader = new Shader(vs, fs);
        String rockPath = Director.getInstance().getDevice().getCachePath()+"/rock.obj";
        mRocks = ModelLoader.loadModel(rockPath, rockShader, mMatrices);
        mRocks.addPointLight(new PointLight(new Vector3f(1.0f, 2.5f, 1.0f), new Vector3f(1f, 1f, 1f)));

        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void render(float delta) {
        glClear(GL_DEPTH_BUFFER_BIT);

        mTimeLapses += delta;

        Director.getInstance().setLookAtOrigin(true);
        Camera camera = Director.getInstance().getCamera();
        Vector3f cameraPos = camera.getCameraPos();
        cameraPos.y = 6.5f;

        Shader shader = mEarth.getShader();
        shader.use();
        shader.setUniformFloat("ambientFactor", 0.3f);
        mEarth.rotateTo(mTimeLapses*10, 0, 1, 0);
        mEarth.render(delta);

        for (int i = 0; i < mAngelForward.length; i++) {
            Matrix4f matrix = mMatrices[i];
            matrix.identity();

            float angel = mAngels[i] - mLoopCount * mAngelForward[i]/5;

            float cos = (float) Math.cos(Math.toRadians(angel));
            float sin = (float) Math.sin(Math.toRadians(angel));

            float targetRadius = mRadius[i];

            float x = cos * targetRadius;
            float z = sin * targetRadius;
            matrix.translate(x, mYOffset[i], z);

            matrix.rotate(mRotate[i], 0, 1, 0);

            matrix.scale(mScale[i]);
        }

        Shader rockShader = mRocks.getShader();
        rockShader.use();
        rockShader.setUniformFloat("ambientFactor", 0.35f);
        mRocks.render(delta);

        mLoopCount++;
    }
}
