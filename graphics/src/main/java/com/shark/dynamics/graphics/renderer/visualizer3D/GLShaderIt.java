package com.shark.dynamics.graphics.renderer.visualizer3D;

import com.shark.dynamics.graphics.renderer.shaderit.ShaderIt;
import com.shark.dynamics.graphics.renderer.visualizer.IGLVisualizer;

public class GLShaderIt extends IGLVisualizer {

    private ShaderIt mShaderIt;

    public GLShaderIt() {

    }

    public void init(int width, int height) {
        mShaderIt = new ShaderIt(width, height);
    }

    @Override
    public void render(float delta) {
        mShaderIt.render(delta);
    }
}
