package com.shark.dynamics.graphics.renderer.visualizer;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars.VerticalBarsInstance;
import com.shark.dynamics.graphics.renderer.bars.VerticalSplitBars;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleSystem;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleType;
import com.shark.dynamics.graphics.renderer.texture.Image;
import com.shark.dynamics.graphics.renderer.texture.Texture;

import org.opencv.core.Size;

public class GLVerticalSplitBars extends IGLVisualizer {

    private VerticalSplitBars mVerticalBars;
    private Sprite mBackground;
    private ParticleSystem mParticleSystem;

    public GLVerticalSplitBars() {
        String vs = Director.getInstance()
                .loaderShaderFromAssets("shader/instance_vs.glsl");
        String fs = Director.getInstance()
                .loaderShaderFromAssets("shader/instance_color_fs.glsl");
        mVerticalBars = new VerticalSplitBars(vs, fs);
        mBarsRenderer = mVerticalBars;
        mVerticalBars.setCenterHorizontal();
        mVerticalBars.setCenterVertical();

        Image image = Director.getInstance().getImageLoader().loadFromAssets("images/background.jpg", true, new Size(0.5f, 0.5f), 10);
        Texture texture = new Texture(image);
        mBackground = new Sprite(texture, Sprite.SpriteType.kRect,
                Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl"),
                Director.getInstance().loaderShaderFromAssets("shader/texture_2d/tex_enhance_fs.glsl"));
        mBackground.setAsBackground();

        mParticleSystem = new ParticleSystem("images/particle_circle.png");
        mParticleSystem.setParticleType(ParticleType.kGravity);
        mParticleSystem.setGenDuration(5);
        mParticleSystem.setGenParticleCount(2);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        mBackground.getShader().use();
        mBackground.getShader().setUniformFloat("enhance", 0.4f);
        mBackground.render(delta);

        mVerticalBars.render(delta);
        mParticleSystem.render(delta);
    }
}
