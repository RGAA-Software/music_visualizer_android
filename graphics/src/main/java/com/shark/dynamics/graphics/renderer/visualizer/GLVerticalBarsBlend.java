package com.shark.dynamics.graphics.renderer.visualizer;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars.VerticalBarsTexture;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.texture.Image;
import com.shark.dynamics.graphics.renderer.texture.Texture;

import org.opencv.core.Size;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_FRONT_AND_BACK;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;

public class GLVerticalBarsBlend extends IGLVisualizer {

    private VerticalBarsTexture mVerticalBars;
    private Sprite mBackground;

    public GLVerticalBarsBlend() {
        String vs = Director.getInstance()
                .loaderShaderFromAssets("shader/instance_vs.glsl");
        String fs = Director.getInstance()
                .loaderShaderFromAssets("shader/texture_2d/base_2d_tex_fs.glsl");
        mVerticalBars = new VerticalBarsTexture(vs, fs, "images/bar_white_vert.png");
        mVerticalBars.setXBarScale(6);
        mVerticalBars.setYBarScale(2);
        mVerticalBars.setCenterHorizontal();
        mVerticalBars.setCenterVertical();
        mBarsRenderer = mVerticalBars;

        Image image = Director.getInstance().getImageLoader().loadFromAssets("images/background_star.jpg", true, new Size(0.5f, 0.5f), 10);
        Texture texture = new Texture(image);
        mBackground = new Sprite(texture, Sprite.SpriteType.kRect,
                Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl"),
                Director.getInstance().loaderShaderFromAssets("shader/texture_2d/tex_enhance_fs.glsl"));
        mBackground.setAsBackground();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        mBackground.getShader().use();
        mBackground.getShader().setUniformFloat("enhance", 0.4f);
        mBackground.render(delta);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        mVerticalBars.render(delta);
        glDisable(GL_BLEND);
    }
}
