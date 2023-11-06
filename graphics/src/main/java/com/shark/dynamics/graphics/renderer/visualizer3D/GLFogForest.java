package com.shark.dynamics.graphics.renderer.visualizer3D;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.r3d.Camera;
import com.shark.dynamics.graphics.renderer.r3d.Plane;
import com.shark.dynamics.graphics.renderer.r3d.light.PointLight;
import com.shark.dynamics.graphics.renderer.r3d.model.Model;
import com.shark.dynamics.graphics.renderer.r3d.model.ModelLoader;
import com.shark.dynamics.graphics.renderer.r3d.particlesystem.ParticleSystem3D;
import com.shark.dynamics.graphics.renderer.r3d.particlesystem.ParticleType3D;
import com.shark.dynamics.graphics.renderer.visualizer.IGLVisualizer;
import com.shark.dynamics.graphics.shader.Shader;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;

public class GLFogForest extends IGLVisualizer {

    //private Model mTree1;
    //private Matrix4f[] mTree1Models;

    private Model mTree2;
    private Matrix4f[] mTree2Models;

    private Random mRandom = new Random();
    private Vector3f mSkyColor = new Vector3f(0.2f, 0.2f, 0.25f);
    private Plane mPlane;
    private ParticleSystem3D mSnowParticle;

    public GLFogForest() {

        String vs = Director.getInstance().loaderShaderFromAssets("shader/3d/instance_model_vs.glsl");
        String fs = Director.getInstance().loaderShaderFromAssets("shader/3d/uniform_color_light_fs.glsl");

        Shader shader = new Shader(vs, fs);
        String path = Director.getInstance().getDevice().getCachePath()+"/tree.obj";

        //int tree1Size = 168;
        //mTree1Models = new Matrix4f[tree1Size];
        //for (int i = 0; i < tree1Size; i++) {
        //    Matrix4f matrix = new Matrix4f();
        //    float x = (0.5f - mRandom.nextFloat()) * 10.0f;
        //    float y = (-mRandom.nextFloat()) * 0.7f;
        //    float z = -(mRandom.nextFloat()) * 18.0f + 8.0f;

        //    matrix.translate(x, y, z);
        //    matrix.rotate((float)Math.toRadians(mRandom.nextFloat()*360), 0, 1, 0);
        //    matrix.scale(0.2f * (mRandom.nextFloat()+1) * 0.006f/3);

        //    mTree1Models[i] = matrix;
        //}
        //mTree1 = ModelLoader.loadModel(path, shader, mTree1Models);

        List<PointLight> pointLights = new ArrayList<>();
        pointLights.add(new PointLight(new Vector3f(1.0f, 4.5f, 3.0f), new Vector3f(0.7f, 0.7f, 0.8f)));
        pointLights.add(new PointLight(new Vector3f(1.0f, 2.5f, 8.0f), new Vector3f(0.9f, 0.7f, 0.8f)));
        pointLights.add(new PointLight(new Vector3f(-1.0f, 2.5f, 0.0f), new Vector3f(0.7f, 0.9f, 0.8f)));
        pointLights.add(new PointLight(new Vector3f(-2.0f, 1.5f, -5.0f), new Vector3f(0.7f, 0.7f, 0.8f)));
        pointLights.add(new PointLight(new Vector3f(-2.0f, 2.5f, -10.0f), new Vector3f(0.9f, 0.6f, 0.8f)));
        pointLights.add(new PointLight(new Vector3f(-2.0f, 2.5f, -15.0f), new Vector3f(0.7f, 0.7f, 0.8f)));
        pointLights.add(new PointLight(new Vector3f(-3.0f, 2.5f, -25.0f), new Vector3f(0.9f, 0.3f, 0.5f)));
        pointLights.add(new PointLight(new Vector3f(3.0f, 2.5f, -35.0f), new Vector3f(0.5f, 0.8f, 0.8f)));
        pointLights.add(new PointLight(new Vector3f(3.0f, 2.5f, -45.0f), new Vector3f(0.7f, 0.9f, 0.3f)));

        float planeScale = 25.0f;
        vs = Director.getInstance().loaderShaderFromAssets("shader/3d/base_vs.glsl");
        fs = Director.getInstance().loaderShaderFromAssets("shader/3d/uniform_color_light_fs.glsl");
        mPlane = new Plane(planeScale, planeScale*2, vs, fs, new Vector3f(0.2f, 0.9f, 0.2f));
        mPlane.translateTo(0, -3.3f, 0);
        mPlane.addPointLight(pointLights.get(0));
        mPlane.addPointLight(pointLights.get(1));
        mPlane.addPointLight(pointLights.get(2));

        int tree2Size = 128;
        mTree2Models = new Matrix4f[tree2Size];
        for (int i = 0; i < tree2Size; i++) {
            Matrix4f matrix = new Matrix4f();
            float x = (0.5f - mRandom.nextFloat()) * 10.0f;
            float y = (-mRandom.nextFloat()) * 0.3f;
            float z = -(mRandom.nextFloat()) * 23.0f + 8.0f;

            matrix.translate(x, y, z);
            matrix.rotate((float)Math.toRadians(mRandom.nextFloat()*360), 0, 1, 0);
            matrix.scale(0.12f * (mRandom.nextFloat()+1));

            mTree2Models[i] = matrix;
        }
        mTree2 = ModelLoader.loadModel(Director.getInstance().getDevice().getCachePath()+"/tree1.obj", shader, mTree2Models);
        mTree2.addPointLights(pointLights);

        mSnowParticle = new ParticleSystem3D();
        mSnowParticle.setParticleType(ParticleType3D.kSnow);
        mSnowParticle.setGenParticleCount(2);
        mSnowParticle.setGenDuration(300);
        mSnowParticle.setDisableDepthMask(false);
        mSnowParticle.setMaxAlpha(0.6f);
        mSnowParticle.setMaxLifeTime(25*1000);

        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void render(float delta) {
        glClearColor(mSkyColor.x, mSkyColor.y, mSkyColor.z, 1.0f);
        glClear(GL_DEPTH_BUFFER_BIT|GL_COLOR_BUFFER_BIT);

        Director.getInstance().setLookAtOrigin(true);
        Camera camera = Director.getInstance().getCamera();
        Vector3f cameraPos = camera.getCameraPos();
        cameraPos.y = 3.5f;

        float fog_density = 0.050f;
        float fog_gradient = 1.5f;

        Shader planeShader = mPlane.getShader();
        planeShader.use();
        planeShader.setUniformFloat("ambientFactor", 0.15f);
        planeShader.setUniformInt("openFog", 1);
        planeShader.setUniformVec3("skyColor", mSkyColor);
        planeShader.setUniformFloat("density", fog_density);
        planeShader.setUniformFloat("gradient", fog_gradient);
        mPlane.render(delta);

        //Shader shader = mTree1.getShader();
        //shader.use();
        //shader.setUniformFloat("ambientFactor", 0.35f);
        //shader.setUniformInt("openFog", 1);
        //shader.setUniformVec3("skyColor", mSkyColor);
        //shader.setUniformFloat("density", fog_density);
        //shader.setUniformFloat("gradient", fog_gradient);
        //mTree1.render(delta);

        Shader shader = mTree2.getShader();
        shader.use();
        shader.setUniformFloat("ambientFactor", 0.35f);
        shader.setUniformInt("openFog", 1);
        shader.setUniformVec3("skyColor", mSkyColor);
        shader.setUniformFloat("density", fog_density);
        shader.setUniformFloat("gradient", fog_gradient);
        mTree2.render(delta);

        mSnowParticle.render(delta);
    }

}
