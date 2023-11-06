package com.shark.dynamics.graphics.renderer.visualizer3D;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars3d.Matrices;
import com.shark.dynamics.graphics.renderer.r3d.Camera;
import com.shark.dynamics.graphics.renderer.visualizer.IGLVisualizer;

import org.joml.Vector3f;

import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glEnable;

public class GLMatrix extends IGLVisualizer {

    private Matrices mMatrices;

    public GLMatrix() {
        mMatrices = new Matrices();
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void render(float delta) {
        glClear(GL_DEPTH_BUFFER_BIT);

        Director.getInstance().setLookAtOrigin(true);
        Camera camera = Director.getInstance().getCamera();
        Vector3f cameraPos = camera.getCameraPos();
        cameraPos.y = 6.5f;


        mMatrices.render(delta);
    }
}
