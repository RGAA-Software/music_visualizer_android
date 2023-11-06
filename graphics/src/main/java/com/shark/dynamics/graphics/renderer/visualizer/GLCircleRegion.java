package com.shark.dynamics.graphics.renderer.visualizer;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars.CircleBars;
import com.shark.dynamics.graphics.renderer.bars.CircleRegion;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.texture.Image;
import com.shark.dynamics.graphics.renderer.texture.Texture;

import org.joml.Vector2f;
import org.opencv.core.Size;

public class GLCircleRegion extends IGLVisualizer {

    private CircleRegion mCircleBars;

    private Sprite mCenterImage;
    private float mCenterImageRotate = 0;

    private Sprite mBackground;

    public GLCircleRegion() {
        String vs = Director.getInstance()
                .loaderShaderFromAssets("shader/base_2d_vs.glsl");
        String fs = Director.getInstance()
                .loaderShaderFromAssets("shader/base_2d_fs.glsl");
        Vector2f center = Director.getInstance().getDevice().getRealCenter();
        mCenterImage = new Sprite("images/oh_lonely.jpg", Sprite.SpriteType.kCircle);
        mCenterImage.scaleTo(0.55f, 0.55f, 0.0f);
        mCenterImage.translateTo(center.x - mCenterImage.getWidth()/2, center.y - mCenterImage.getHeight()/2, 0);

        mCircleBars = new CircleRegion(center, mCenterImage.getWidth()/2);
        mBarsRenderer = mCircleBars;

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
        mBackground.getShader().setUniformFloat("enhance", 1.2f);
        mBackground.render(delta);
        mCircleBars.render(delta);

        mCenterImageRotate += delta * 12;
        mCenterImage.rotateTo(mCenterImageRotate, 0, 0, 1);
        mCenterImage.render(delta);
    }
}
