package com.shark.dynamics.graphics.renderer.visualizer;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars.CircleBars;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;

import org.joml.Vector2f;

public class GLCircleBars extends IGLVisualizer {

    private CircleBars mCircleBars;

    private Sprite mCenterImage;
    private float mCenterImageRotate = 0;

    public GLCircleBars() {
        String vs = Director.getInstance()
                .loaderShaderFromAssets("shader/base_2d_vs.glsl");
        String fs = Director.getInstance()
                .loaderShaderFromAssets("shader/base_2d_fs.glsl");
        mCircleBars = new CircleBars(vs, fs);
        mBarsRenderer = mCircleBars;
        mCircleBars.setCenter();

        mCenterImage = new Sprite("images/oh_lonely.jpg", Sprite.SpriteType.kCircle);
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        mCenterImage.scaleTo(0.55f, 0.55f, 0.0f);
        mCenterImage.translateTo(sc.x/2 - mCenterImage.getWidth()/2, sc.y/2 - mCenterImage.getHeight()/2, 0);
    }

    @Override
    public void render(float delta) {
        if (mPause) {
            return;
        }
        super.render(delta);
        mCircleBars.render(delta);

        mCenterImageRotate += delta * 12;
        mCenterImage.rotateTo(mCenterImageRotate, 0, 0, 1);
        mCenterImage.render(delta);
    }
}
