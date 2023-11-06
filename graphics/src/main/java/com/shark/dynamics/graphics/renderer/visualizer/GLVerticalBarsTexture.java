package com.shark.dynamics.graphics.renderer.visualizer;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars.VerticalBarsInstance;
import com.shark.dynamics.graphics.renderer.bars.VerticalBarsTexture;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleSystem;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleType;
import com.shark.dynamics.graphics.renderer.texture.Image;
import com.shark.dynamics.graphics.renderer.texture.Texture;

import org.joml.Vector3f;
import org.opencv.core.Size;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;

public class GLVerticalBarsTexture extends IGLVisualizer {

    private VerticalBarsTexture mVerticalBars;
    private Sprite mBackground;
    private ParticleSystem mParticleSystem;

    public GLVerticalBarsTexture() {
        String vs = Director.getInstance()
                .loaderShaderFromAssets("shader/instance_vs.glsl");
        String fs = Director.getInstance()
                .loaderShaderFromAssets("shader/texture_2d/base_2d_tex_fs.glsl");
        mVerticalBars = new VerticalBarsTexture(vs, fs, "images/bar_vert.png");
        mBarsRenderer = mVerticalBars;
        mVerticalBars.setCenterHorizontal();
        mVerticalBars.setCenterVertical();

        Image image = Director.getInstance().getImageLoader().loadFromAssets("images/background.jpg", true, new Size(0.5f, 0.5f), 10);
        Texture texture = new Texture(image);
        mBackground = new Sprite(texture, Sprite.SpriteType.kRect,
                Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl"),
                Director.getInstance().loaderShaderFromAssets("shader/texture_2d/tex_enhance_fs.glsl"));
        mBackground.setAsBackground();

        mParticleSystem = new ParticleSystem("images/chu_ju.png");
        mParticleSystem.setParticleType(ParticleType.kSnow);
        mParticleSystem.setGenDuration(370);
        mParticleSystem.setGenParticleCount(1);
        mParticleSystem.setColorOverlay(true);
        mParticleSystem.setTintColor(new Vector3f(1,1,1));
        mParticleSystem.setBaseScale(2.6f);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        mBackground.getShader().use();
        mBackground.getShader().setUniformFloat("enhance", 0.4f);
        mBackground.render(delta);

        mParticleSystem.render(delta);

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        mVerticalBars.render(delta);
        glDisable(GL_BLEND);
    }
}
