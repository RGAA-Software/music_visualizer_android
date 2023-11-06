package com.shark.dynamics.graphics.renderer.visualizer3D;

import android.opengl.GLES30;
import android.opengl.GLES32;
import android.util.Log;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars3d.IBars3DRenderer;
import com.shark.dynamics.graphics.renderer.framebuffer.DepthFrameBuffer;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.r3d.Camera;
import com.shark.dynamics.graphics.renderer.r3d.Cube;
import com.shark.dynamics.graphics.renderer.r3d.Plane;
import com.shark.dynamics.graphics.renderer.r3d.light.PointLight;
import com.shark.dynamics.graphics.renderer.r3d.model.Model;
import com.shark.dynamics.graphics.renderer.r3d.model.ModelLoader;
import com.shark.dynamics.graphics.renderer.r3d.particlesystem.ParticleSystem3D;
import com.shark.dynamics.graphics.renderer.r3d.particlesystem.ParticleType3D;
import com.shark.dynamics.graphics.renderer.texture.Texture;
import com.shark.dynamics.graphics.renderer.visualizer.IGLVisualizer;
import com.shark.dynamics.graphics.shader.Shader;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glViewport;
import static gln.vertexArray.VertexArrayKt.glBindVertexArray;

public class GLShadow extends IGLVisualizer {

    private Cube mColorCube;
    private Model mDeer;
    private Plane mPlane;

    private DepthFrameBuffer mDepthFrameBuffer;
    private Sprite mDepthPreview;
    private Vector2f mScreenSize;

    private float mRotate;

    private PointLight mPointLight;

    private Camera mShadowCamera;
    private Matrix4f mShadowProj;

    private int mShadowmapWidth;
    private int mShadowmapHeight;

    private int mCubeSize = 30;
    private float mRadius = 2.0f;
    private List<Cube> mCubes;

    private ParticleSystem3D mFlameParticle;

    private boolean mDebug = false;

    public GLShadow() {

        mFlameParticle = new ParticleSystem3D();
        mFlameParticle.setParticleType(ParticleType3D.kFlame);
        mFlameParticle.setGenDuration(20);
        mFlameParticle.setGenParticleCount(15);
        mFlameParticle.setPosition(-1, 1.5f, 0);
        mFlameParticle.setColorOverlay(true);
        mFlameParticle.setTintColor(new Vector3f(0.9f, 0.2f, 0.3f));

        mPointLight = new PointLight(new Vector3f(0.2f, 0.8f, -1.5f), new Vector3f(2,2f,2));

        mShadowCamera = new Camera(mPointLight.position,
                new Vector3f(0, 0, -1),
                new Vector3f(0, 1, 0),
                0, 270, 0);

        mShadowProj = new Matrix4f();
        mShadowProj = mShadowProj.ortho(-9.6f, 9.6f, -9.6f, 9.6f, 0.0f, 7.5f);

        mScreenSize = Director.getInstance().getDevice().getScreenRealSize();
        mShadowmapWidth = (int) mScreenSize.x * 2;
        mShadowmapHeight = (int) mScreenSize.x * 2;
        mDepthFrameBuffer = new DepthFrameBuffer();
        mDepthFrameBuffer.init(mShadowmapWidth, mShadowmapHeight);
        String vs = Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl");
        String fs = Director.getInstance().loaderShaderFromAssets("shader/texture_2d/tex_one_channel_fs.glsl");
        mDepthPreview = new Sprite(new Texture(mDepthFrameBuffer.getTexId(), mShadowmapWidth, mShadowmapHeight), Sprite.SpriteType.kRect, vs, fs);

        vs = Director.getInstance().loaderShaderFromAssets("shader/3d/base_vs.glsl");
        fs = Director.getInstance().loaderShaderFromAssets("shader/3d/light_fs.glsl");
        mColorCube = new Cube(vs, fs);
        mColorCube.scaleTo(0.3f);
        mColorCube.translateTo(-0.3f, -1.0f, 0);
        mColorCube.addPointLight(mPointLight);
        mColorCube.setShowPointLights(true);
        mColorCube.initShadowShader();
        mColorCube.setShadowCamera(mShadowCamera);
        mColorCube.setShadowProjection(mShadowProj);

        mCubes = new ArrayList<>();
        Vector3f color = new Vector3f(0.7f, 0.88f, 0.7f);
        float itemAngel = 360.0f/mCubeSize;
        for (int i = 0; i < mCubeSize; i++) {
            float angel = itemAngel*i;
            float x = (float)(mRadius * Math.cos(Math.toRadians(angel)));
            float z = (float)(mRadius * Math.sin(Math.toRadians(angel)));
            Cube cube = new Cube(color, vs, fs);
            cube.addPointLight(mPointLight);
            cube.setShowPointLights(true);
            cube.initShadowShader();
            cube.setShadowCamera(mShadowCamera);
            cube.setShadowProjection(mShadowProj);
            cube.translateTo(x, -1.0f, z);
            cube.scaleTo(0.12f, 1.0f, 0.08f);
            mCubes.add(cube);
        }

        mDeer = ModelLoader.loadModel(Director.getInstance().getDevice().getCachePath()+"/deer_r.obj");
        float scale = 0.6f;
        mDeer.scaleTo(scale, scale, scale);
        mDeer.translateTo(0, -1.5f, 0);
        //mDeer.rotateTo(-30, 0, 1, 0);
        mDeer.addPointLight(mPointLight);
        mDeer.setShowPointLights(true);
        mDeer.initShadowShader();
        mDeer.setShadowCamera(mShadowCamera);
        mDeer.setShadowProjection(mShadowProj);

        float planeScale = 4.5f;
        vs = Director.getInstance().loaderShaderFromAssets("shader/3d/base_vs.glsl");
        fs = Director.getInstance().loaderShaderFromAssets("shader/3d/shadow_light_fs.glsl");
        mPlane = new Plane(planeScale, planeScale, vs, fs);
        mPlane.translateTo(0, -3.3f, 0);

        mPlane.addPointLight(mPointLight);
        mPlane.initShadowShader();
        mPlane.setShadowCamera(mShadowCamera);
        mPlane.setShadowProjection(mShadowProj);

        mBars3DRenderer = new IBars3DRenderer();

        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void render(float delta) {
        mDepthFrameBuffer.begin();
        glViewport(0, 0, mShadowmapWidth, mShadowmapHeight);
        glClearColor(0.1f, 0.1f, 0.1f, 1);
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);

        Director.getInstance().setLookAtOrigin(true);
        Camera camera = Director.getInstance().getCamera();
        Vector3f cameraPos = camera.getCameraPos();
        cameraPos.y = 6.5f;

        mRotate += delta;

        float radius = 1.3f;
        float angel = -mRotate*20;
        PointLight light = mPointLight;
        light.position.x = (float)Math.cos(Math.toRadians(angel)) * radius;
        light.position.z = (float)Math.sin(Math.toRadians(angel)) * radius;

        // cube
        Shader shadowShader = mColorCube.getShadowShader();
        if (mDebug) {
            shadowShader.use();
            shadowShader.setUniformVec3("lightPosition", mPointLight.position);
            mColorCube.rotateTo(mRotate * 100, 0, 1, 0);
            mColorCube.renderShadow(delta);
        }
        mBars3DRenderer.fallDownSGS(mSGSArray, mCubeSize+2);

        int idx = 0;
        for (Cube c : mCubes) {
            float z = c.getTranslate().z;
            c.setEnableAlpha(true);
            c.setAlpha(Math.min((mRadius+z)/(2*mRadius)+0.25f, 1.0f));
            c.updateYValue(mBars3DRenderer.getDrawSGSBars()[idx++]*1.0f/60.0f);
            c.rotateTo(mRotate, 0, 1, 0);

            Shader ss = c.getShadowShader();
            ss.use();
            ss.setUniformVec3("lightPosition", mPointLight.position);

            c.renderShadow(delta);
        }

        // model
        shadowShader = mDeer.getShadowShader();
        shadowShader.use();
        shadowShader.setUniformVec3("lightPosition", mPointLight.position);
        mDeer.rotateTo(-mRotate*10, 0, 1, 0);
        mDeer.renderShadow(delta);

        // plane
        shadowShader = mPlane.getShadowShader();
        shadowShader.use();
        shadowShader.setUniformVec3("lightPosition", mPointLight.position);
        mPlane.renderShadow(delta);

        mDepthFrameBuffer.end();

        glViewport(0, 0, (int)mScreenSize.x, (int)mScreenSize.y);
        glClearColor(0.2f, 0.2f, 0.2f, 1);
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);

        if (mDebug) {
            mColorCube.render(delta);
        }

        idx = 0;
        for (Cube c : mCubes) {
            float z = c.getTranslate().z;
            c.setEnableAlpha(true);
            c.setAlpha(Math.min((mRadius+z)/(2*mRadius)+0.25f, 1.0f));
            c.updateYValue(mBars3DRenderer.getDrawSGSBars()[idx++]*1.0f/60.0f);
            c.rotateTo(mRotate, 0, 1, 0);

            Shader ss = c.getShadowShader();
            ss.use();
            ss.setUniformVec3("lightPosition", mPointLight.position);

            c.render(delta);
        }

        mDeer.render(delta);

        Shader shader = mPlane.getShader();
        shader.use();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mDepthFrameBuffer.getTexId());
        shader.setUniformInt("shadowmap", 0);

        mPlane.render(delta);

        mFlameParticle.setPosition(light.position.x, light.position.y, light.position.z);
        mFlameParticle.render(delta);

        if (mDebug) {
            mDepthPreview.translateTo(0, 1500, 0);
            mDepthPreview.scaleTo(0.3f);
            mDepthPreview.render(delta);
        }
    }
}
