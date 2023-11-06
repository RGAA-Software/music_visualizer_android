package com.shark.dynamics.graphics.renderer.visualizer;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars.SecondOrderBezierCircle;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.texture.Image;
import com.shark.dynamics.graphics.renderer.texture.Texture;

import org.joml.Vector2f;
import org.opencv.core.Size;

public class GLFlowerCircle extends IGLVisualizer {

    private SecondOrderBezierCircle mSmoothCircle;
    private Sprite mBackground;
    private Sprite mCenterImage;
    private float mCenterImageRotate;

    public GLFlowerCircle() {
        Image image = Director.getInstance().getImageLoader().loadFromAssets("images/background_star.jpg", true, new Size(0.5f, 0.5f), 10);
        Texture texture = new Texture(image);
        mBackground = new Sprite(texture, Sprite.SpriteType.kRect,
                Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl"),
                Director.getInstance().loaderShaderFromAssets("shader/texture_2d/tex_enhance_fs.glsl"));
        mBackground.setAsBackground();

        mCenterImage = new Sprite("images/oh_lonely.jpg", Sprite.SpriteType.kCircle);
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        mCenterImage.scaleTo(0.55f, 0.55f, 0.0f);
        mCenterImage.translateTo(sc.x/2 - mCenterImage.getWidth()/2, sc.y/2 - mCenterImage.getHeight()/2, 0);

        mSmoothCircle = new SecondOrderBezierCircle(30, mCenterImage.getWidth()/2 + 30);
        mBarsRenderer = mSmoothCircle;

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

        mSmoothCircle.render(delta);
    }
}
