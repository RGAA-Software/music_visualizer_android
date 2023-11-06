package com.shark.dynamics.graphics.renderer.visualizer;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleSystem;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleType;
import com.shark.dynamics.graphics.renderer.texture.Image;
import com.shark.dynamics.graphics.renderer.texture.Texture;

import org.joml.Vector2f;
import org.opencv.core.Size;

public class GLUniverseAsh extends IGLVisualizer {

    private float mCenterImageRotate = 0;
    private Sprite mCenterImage;
    private Sprite mBackground;
    private Vector2f mCenter;

    private ParticleSystem mParticleSystem;

    public GLUniverseAsh() {
        mCenterImage = new Sprite("images/oh_lonely.jpg", Sprite.SpriteType.kCircle);
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        mCenterImage.scaleTo(0.55f, 0.55f, 0.0f);
        mCenterImage.translateTo(sc.x/2 - mCenterImage.getWidth()/2, sc.y/2 - mCenterImage.getHeight()/2, 0);
        mCenter = new Vector2f(sc.x/2, sc.y/2);

        Image image = Director.getInstance().getImageLoader().loadFromAssets("images/background_star.jpg", true, new Size(0.5f, 0.5f), 10);
        Texture texture = new Texture(image);
        mBackground = new Sprite(texture, Sprite.SpriteType.kRect,
                Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl"),
                Director.getInstance().loaderShaderFromAssets("shader/texture_2d/tex_enhance_fs.glsl"));
        mBackground.setAsBackground();

        mParticleSystem = new ParticleSystem("images/particle.png", 12000);
        mParticleSystem.setPosition(mCenter.x, mCenter.y);
        mParticleSystem.setColorOverlay(true);
        mParticleSystem.setUseDecreaseScale(true);
        mParticleSystem.setGenDuration(20);
        mParticleSystem.setGenParticleCount(100);
        mParticleSystem.setParticleType(ParticleType.kUniverseAsh);
        mParticleSystem.setRadius(mCenterImage.getWidth()/2+20);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        mBackground.getShader().use();
        mBackground.getShader().setUniformFloat("enhance", 0.3f);
        mBackground.render(delta);

        mCenterImageRotate -= delta * 12;
        mCenterImage.rotateTo(mCenterImageRotate, 0, 0, 1);
        mCenterImage.render(delta);

        mParticleSystem.render(delta);
    }

}
