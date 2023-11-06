package com.shark.dynamics.graphics.renderer.visualizer;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars.VerticalBars;
import com.shark.dynamics.graphics.shader.ShaderLoader;

public class GLVerticalBars extends IGLVisualizer {

    private VerticalBars mVerticalBars;

    public GLVerticalBars() {
        String vs = Director.getInstance()
                .loaderShaderFromAssets("shader/base_2d_vs.glsl");
        String fs = Director.getInstance()
                .loaderShaderFromAssets("shader/base_2d_fs.glsl");
        mVerticalBars = new VerticalBars(vs, fs);
        mBarsRenderer = mVerticalBars;
        mVerticalBars.setCenterHorizontal();
        mVerticalBars.setCenterVertical();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        mVerticalBars.render(delta);
    }
}
