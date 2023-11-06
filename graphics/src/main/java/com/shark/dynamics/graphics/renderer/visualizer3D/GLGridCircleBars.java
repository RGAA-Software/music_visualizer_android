package com.shark.dynamics.graphics.renderer.visualizer3D;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars3d.CircleBars3D;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.r3d.Camera;
import com.shark.dynamics.graphics.renderer.r3d.Grid;
import com.shark.dynamics.graphics.renderer.r3d.Sprite3D;
import com.shark.dynamics.graphics.renderer.visualizer.IGLVisualizer;

import org.joml.Vector3f;

import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glEnable;

public class GLGridCircleBars extends IGLVisualizer {

    private Grid mGrid;
    private Sprite3D mCircleImage;
    private CircleBars3D mCircleBars;

    private float mCameraMoveRadius = 13.0f;
    private boolean mCameraRotateForward = true;
    private float mMaxCameraAngel = 40;
    private float mCameraAngel = 90;
    private float mCameraDeltaCount = 0;

    private float mTimeLapses;

    public GLGridCircleBars() {

        mGrid = new Grid(20, 20, 1.5f);

        mCircleBars = new CircleBars3D();
        mCircleBars.translateTo(0, 0, -2.0f);
        mBars3DRenderer = mCircleBars;

        mCircleImage = new Sprite3D("images/oh_lonely.jpg", Sprite.SpriteType.kCircle);
        mCircleImage.scaleTo(1.15f, 1.15f, 1.0f);
        mCircleImage.translateTo(0, 0, -2.0f);
        glEnable(GL_DEPTH_TEST);
    }


    @Override
    public void render(float delta) {
        glClear(GL_DEPTH_BUFFER_BIT);

        mTimeLapses += delta;

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
        cameraPos.z = cameraZ;

        mCircleBars.render(delta);

        mCircleImage.rotateTo(mTimeLapses*5, 0,0, 1);
        mCircleImage.render(delta);

        mGrid.render(delta);


    }
}
