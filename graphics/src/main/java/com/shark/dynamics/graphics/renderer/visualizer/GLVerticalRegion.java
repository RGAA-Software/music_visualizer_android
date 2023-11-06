package com.shark.dynamics.graphics.renderer.visualizer;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars.FilterType;
import com.shark.dynamics.graphics.renderer.bars.VerticalRegion;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.shader.Shader;

import org.joml.Vector2f;

public class GLVerticalRegion extends IGLVisualizer {

    private VerticalRegion mRegion;
    private Sprite mBackground;

    private Vector2f mBlurLeftBottom;
    private Vector2f mBlurRightTop;
    private float mBlurHeight;
    private float mYOffset = 1300;
    private boolean mMoveUp = true;

    public GLVerticalRegion() {
        String vs = Director.getInstance()
                .loaderShaderFromAssets("shader/base_2d_vs.glsl");
        String fs = Director.getInstance()
                .loaderShaderFromAssets("shader/base_2d_fs.glsl");

        mRegion = new VerticalRegion(vs, fs);
        mRegion.setCenterHorizontal();
        mBarsRenderer = mRegion;

        mBlurHeight = 300;
        mBlurLeftBottom = new Vector2f(mRegion.getLeftBottomX(), mYOffset);
        mBlurRightTop = new Vector2f(mRegion.getLeftBottomX()+ mRegion.getTotalWidth(), mBlurLeftBottom.y+mBlurHeight);

        mBackground = new Sprite("images/background.jpg", Sprite.SpriteType.kRect,
                Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl"),
                Director.getInstance().loaderShaderFromAssets("shader/texture_2d/region_blur_tex_fs.glsl"));
        mBackground.setAsBackground();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (mYOffset >= 2000) {
            mMoveUp = false;
        }
        if (mYOffset <= 900) {
            mMoveUp = true;
        }
        if (mMoveUp) {
            mYOffset += delta*20;
        } else {
            mYOffset -= delta*20;
        }

        mBlurLeftBottom.y = mYOffset;
        mBlurRightTop.y = mBlurLeftBottom.y+mBlurHeight;

        Shader bgShader = mBackground.getShader();
        bgShader.use();
        bgShader.setUniformFloat("colorEnhance", 0.2f);
        bgShader.setUniformVec2("leftBottom", mBlurLeftBottom.x, mBlurLeftBottom.y);
        bgShader.setUniformVec2("rightTop", mBlurRightTop.x, mBlurRightTop.y);
        mBackground.render(delta);

        mRegion.setInverseBars(false);
        mRegion.setFilterType(FilterType.kSGS);
        mRegion.setYPosition((int) (mBlurRightTop.y+10));
        mRegion.render(delta);

        mRegion.setInverseBars(true);
        mRegion.setFilterType(FilterType.kMonsterCat);
        mRegion.setYPosition((int) (mBlurLeftBottom.y-10));
        mRegion.render(delta);
    }
}
