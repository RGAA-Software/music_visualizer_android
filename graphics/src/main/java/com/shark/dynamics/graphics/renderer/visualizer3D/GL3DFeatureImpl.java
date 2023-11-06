package com.shark.dynamics.graphics.renderer.visualizer3D;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.framebuffer.DepthFrameBuffer;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.r3d.Camera;
import com.shark.dynamics.graphics.renderer.r3d.Cube;
import com.shark.dynamics.graphics.renderer.r3d.Grid;
import com.shark.dynamics.graphics.renderer.r3d.Plane;
import com.shark.dynamics.graphics.renderer.r3d.Sprite3D;
import com.shark.dynamics.graphics.renderer.r3d.light.PointLight;
import com.shark.dynamics.graphics.renderer.r3d.particlesystem.ParticleSystem3D;
import com.shark.dynamics.graphics.renderer.r3d.particlesystem.ParticleType3D;
import com.shark.dynamics.graphics.renderer.texture.Texture;
import com.shark.dynamics.graphics.renderer.visualizer.IGLVisualizer;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;

public class GL3DFeatureImpl extends IGLVisualizer {

    private float mRotate;

    private Sprite3D mSprite3D;
    private Sprite3D mCircleSprite;
    private Cube mCube;
    private Cube mColorCube;
    private Cube mMapCube;
    private Grid mGrid;
    private Plane mPlane;

    private float mCameraMoveRadius = 13.0f;
    private boolean mCameraRotateForward = true;
    private float mMaxCameraAngel = 40;
    private float mCameraAngel = 90;
    private float mCameraDeltaCount = 0;

    private ParticleSystem3D mFlameParticle;


    public GL3DFeatureImpl() {

        //
        mSprite3D = new Sprite3D("images/oh_lonely.jpg");
        mSprite3D.scaleTo(0.23f, 0.23f, 0);
        mSprite3D.translateTo(-0.25f, -1.5f, 0);

        mCircleSprite = new Sprite3D("images/oh_lonely.jpg", Sprite.SpriteType.kCircle);
        mCircleSprite.scaleTo(0.25f, 0.25f, 0);
        mCircleSprite.translateTo(0, -1.5f, 0);

        mCube = new Cube("images/west_omniscient_buddha.jpg", false, false);
        mCube.scaleTo(0.3f, 0.3f, 0.3f);
        mCube.translateTo(2.0f, 0, 0);

        mMapCube = new Cube("images/skybox", true, true);
        //mMapCube.translateTo(0, 3.0f, 0);
        float scaleSize = 1.5f;
        mMapCube.scaleTo(scaleSize, scaleSize, scaleSize);

        String vs = Director.getInstance().loaderShaderFromAssets("shader/3d/base_vs.glsl");
        String fs = Director.getInstance().loaderShaderFromAssets("shader/3d/light_fs.glsl");
        mColorCube = new Cube(vs, fs);
        mColorCube.scaleTo(0.5f, 0.5f, 0.5f);
        mColorCube.translateTo(0f, 1, 0);
        mColorCube.setShowPointLights(true);

        mGrid = new Grid(20, 20, 1.5f);

        mPlane = new Plane(2.0f, 2.0f);
        mPlane.translateTo(0, 0.3f, 0);

        PointLight pointLight = new PointLight(new Vector3f(1, 1.5f, 0), new Vector3f(1,0.5f,1));
        mPlane.addPointLight(pointLight);
        mColorCube.addPointLight(pointLight);
        pointLight = new PointLight(new Vector3f(-1, 1.5f, 0), new Vector3f(0.5f,0.5f,0.5f));
        mPlane.addPointLight(pointLight);

        mFlameParticle = new ParticleSystem3D();
        mFlameParticle.setParticleType(ParticleType3D.kFlame);
        mFlameParticle.setGenDuration(20);
        mFlameParticle.setGenParticleCount(15);
        mFlameParticle.setPosition(-1, 1.5f, 0);
        mFlameParticle.setColorOverlay(true);
        mFlameParticle.setTintColor(new Vector3f(0.9f, 0.2f, 0.3f));


        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        glClear(GL_DEPTH_BUFFER_BIT|GL_COLOR_BUFFER_BIT);

        mRotate += delta;

        float cameraDelta = delta*10;

        if (mCameraDeltaCount > mMaxCameraAngel) {
            mCameraRotateForward = false;
        } else if (mCameraDeltaCount < -mMaxCameraAngel) {
            mCameraRotateForward = true;
        }
        if (mCameraRotateForward) {
            mCameraAngel += cameraDelta;
            mCameraDeltaCount += cameraDelta;
        } else {
            mCameraAngel -= cameraDelta;
            mCameraDeltaCount -= cameraDelta;
        }

        float cameraX = (float)Math.cos(Math.toRadians(mCameraAngel)) * mCameraMoveRadius;
        float cameraZ = (float)Math.sin(Math.toRadians(mCameraAngel)) * mCameraMoveRadius;

        Director.getInstance().setLookAtOrigin(true);
        Camera camera = Director.getInstance().getCamera();
        Vector3f cameraPos = camera.getCameraPos();
        cameraPos.x = cameraX;
        cameraPos.y = 7.0f;
        cameraPos.z = cameraZ;

        mMapCube.rotateTo(mRotate*5, 0, 1, 0);
        mMapCube.render(delta);


        mSprite3D.rotateTo(mRotate*30, 0, 1, 0);
        mSprite3D.render(delta);

        mCircleSprite.render(delta);

        mCube.updateYValue(2.0f);
        mCube.rotateTo(-mRotate*30, 0, 1,0);
        mCube.render(delta);

        mColorCube.rotateTo(mRotate*-100, 0, 1, 0);
        mColorCube.render(delta);

        mPlane.render(delta);

        float particleRadius = 1.0f;
        float x = (float)Math.cos(Math.toRadians(mRotate * 100)) * particleRadius;
        float z = (float)Math.sin(Math.toRadians(mRotate * 100)) * particleRadius;
        mFlameParticle.setPosition(x, 1.5f, z);
        mFlameParticle.render(delta);

        mGrid.render(delta);


        //glDepthMask(false);
    }
}
