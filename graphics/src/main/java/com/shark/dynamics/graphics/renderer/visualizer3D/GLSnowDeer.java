package com.shark.dynamics.graphics.renderer.visualizer3D;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.r3d.Camera;
import com.shark.dynamics.graphics.renderer.r3d.light.PointLight;
import com.shark.dynamics.graphics.renderer.r3d.model.Model;
import com.shark.dynamics.graphics.renderer.r3d.model.ModelLoader;
import com.shark.dynamics.graphics.renderer.r3d.particlesystem.ParticleSystem3D;
import com.shark.dynamics.graphics.renderer.r3d.particlesystem.ParticleType3D;
import com.shark.dynamics.graphics.renderer.visualizer.IGLVisualizer;
import com.shark.dynamics.graphics.shader.Shader;

import org.joml.Vector3f;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;

public class GLSnowDeer extends IGLVisualizer {

    private Model mDeer;
    private ParticleSystem3D mFlameParticle;
    private ParticleSystem3D mSnowParticle;
    private float mRotate;

    public GLSnowDeer() {
        mDeer = ModelLoader.loadModel(Director.getInstance().getDevice().getCachePath()+"/deer_r.obj");
        float scale = 1.002f;
        mDeer.scaleTo(scale, scale, scale);
        mDeer.translateTo(0, -2.5f, 0);
        mDeer.rotateTo(-30, 0, 1, 0);
        mDeer.addPointLight(new PointLight(new Vector3f(1.0f, 2.0f, 1.0f), new Vector3f(0.3f, 0.3f, 0.3f)));
        mDeer.addPointLight(new PointLight(new Vector3f(1.0f, 4.0f, 1.0f), new Vector3f(0.3f, 0.3f, 0.3f)));
        mDeer.setShowPointLights(false);

        mFlameParticle = new ParticleSystem3D();
        mFlameParticle.setParticleType(ParticleType3D.kFlame);
        mFlameParticle.setGenDuration(200);
        mFlameParticle.setGenParticleCount(1);
        mFlameParticle.setColorOverlay(true);
        mFlameParticle.setTintColor(new Vector3f(0.2f, 0.2f, 0.52f));
        mFlameParticle.setPosition(0.55f, -0.250f, 3.66f);

        mSnowParticle = new ParticleSystem3D();
        mSnowParticle.setParticleType(ParticleType3D.kSnow);
        mSnowParticle.setGenParticleCount(2);
        mSnowParticle.setGenDuration(300);
        mSnowParticle.setDisableDepthMask(false);

        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void render(float delta) {
        glClearColor(0.12f, 0.12f, 0.12f, 1.0f);
        glClear(GL_DEPTH_BUFFER_BIT|GL_COLOR_BUFFER_BIT);

        mRotate += delta;

        Director.getInstance().setLookAtOrigin(true);
        Camera camera = Director.getInstance().getCamera();
        Vector3f cameraPos = camera.getCameraPos();
        cameraPos.y = 0.5f;

        float radius = 1.5f;
        float angel = -mRotate*20;
        PointLight light = mDeer.getPointLights().get(0);
        light.position.x = (float)Math.cos(Math.toRadians(angel)) * radius;
        light.position.z = (float)Math.sin(Math.toRadians(angel)) * radius;

        Shader shader = mDeer.getShader();
        shader.use();
        shader.setUniformInt("openFog", 10);
        mDeer.render(delta);
        mSnowParticle.render(delta);
        mFlameParticle.render(delta);
    }
}
