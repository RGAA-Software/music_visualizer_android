package com.shark.dynamics.graphics.renderer.visualizer3D;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars3d.CircleCubeBars;
import com.shark.dynamics.graphics.renderer.r3d.Camera;
import com.shark.dynamics.graphics.renderer.visualizer.IGLVisualizer;

import org.joml.Vector3f;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;

public class GLCircleCube extends IGLVisualizer {

    private CircleCubeBars mCircleBars;

    public GLCircleCube() {
        mCircleBars = new CircleCubeBars();
        mBars3DRenderer = mCircleBars;
        glEnable(GL_DEPTH_TEST);
    }


    @Override
    public void render(float delta) {
        glClear(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        Director.getInstance().setLookAtOrigin(true);
        Camera camera = Director.getInstance().getCamera();
        Vector3f cameraPos = camera.getCameraPos();
        cameraPos.y = 6.5f;

        mCircleBars.render(delta);
        glDisable(GL_BLEND);
    }
}
