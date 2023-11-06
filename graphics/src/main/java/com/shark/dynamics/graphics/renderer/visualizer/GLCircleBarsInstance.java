package com.shark.dynamics.graphics.renderer.visualizer;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars.CircleBarsInstance;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleSystem;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleType;
import com.shark.dynamics.graphics.renderer.texture.Image;
import com.shark.dynamics.graphics.renderer.texture.Texture;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.opencv.core.Size;

public class GLCircleBarsInstance extends IGLVisualizer {

    private CircleBarsInstance mCircleBars;
    private float mCenterImageRotate = 0;
    private Sprite mCenterImage;
    private Sprite mBackground;
    private ParticleSystem mParticleSystem;
    private Vector2f mCenter;

    public GLCircleBarsInstance() {
        String vs = Director.getInstance()
                .loaderShaderFromAssets("shader/instance_vs.glsl");
        String fs = Director.getInstance()
                .loaderShaderFromAssets("shader/base_2d_fs.glsl");

        mCenterImage = new Sprite("images/oh_lonely.jpg", Sprite.SpriteType.kCircle);
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        mCenterImage.scaleTo(0.55f, 0.55f, 0.0f);
        mCenterImage.translateTo(sc.x/2 - mCenterImage.getWidth()/2, sc.y/2 - mCenterImage.getHeight()/2, 0);
        mCenter = new Vector2f(sc.x/2, sc.y/2);

        mCircleBars = new CircleBarsInstance(vs, fs, mCenterImage.getWidth()/2 + 30);
        mBarsRenderer = mCircleBars;
        mCircleBars.setCenter();

        Image image = Director.getInstance().getImageLoader().loadFromAssets("images/background.jpg", true, new Size(0.5f, 0.5f), 10);
        Texture texture = new Texture(image);
        mBackground = new Sprite(texture, Sprite.SpriteType.kRect,
                Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl"),
                Director.getInstance().loaderShaderFromAssets("shader/texture_2d/tex_enhance_fs.glsl"));
        mBackground.setAsBackground();

        mParticleSystem = new ParticleSystem();
        mParticleSystem.setParticleType(ParticleType.kSpark);
        mParticleSystem.setGenParticleCount(5);
        mParticleSystem.setGenDuration(30);
        mParticleSystem.setUseDecreaseScale(true);
        mParticleSystem.setColorOverlay(true);
        mParticleSystem.setTintColor(new Vector3f(1.0f, 0.6f, 0.2f));
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        mBackground.getShader().use();
        mBackground.getShader().setUniformFloat("enhance", 0.4f);
        mBackground.render(delta);

        mCircleBars.render(delta);

        mCenterImageRotate -= delta * 12;
        mCenterImage.rotateTo(mCenterImageRotate, 0, 0, 1);
        mCenterImage.render(delta);

        float rotateDegree = -mCenterImageRotate;
        float radius = mCenterImage.getWidth()/2;
        float x = (float) (radius * Math.cos(Math.toRadians(rotateDegree)));
        float y = (float) (radius * Math.sin(Math.toRadians(rotateDegree)));
        mParticleSystem.setPosition(mCenter.x+x, mCenter.y+y);
        mParticleSystem.render(delta);
    }
}
