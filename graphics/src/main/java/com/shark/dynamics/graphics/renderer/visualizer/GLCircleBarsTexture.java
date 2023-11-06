package com.shark.dynamics.graphics.renderer.visualizer;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars.CircleBarsTexture;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.texture.Image;
import com.shark.dynamics.graphics.renderer.texture.Texture;

import org.joml.Vector2f;
import org.opencv.core.Size;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;

public class GLCircleBarsTexture extends IGLVisualizer {
    private static final String TAG = "CircleBarsTexture";

    private CircleBarsTexture mCircleBars;
    private float mCenterImageRotate = 0;
    private Sprite mCenterImage;
    private Sprite mBackground;

    public GLCircleBarsTexture() {
        String vs = Director.getInstance()
                .loaderShaderFromAssets("shader/instance_vs.glsl");
        String fs = Director.getInstance()
                .loaderShaderFromAssets("shader/texture_2d/base_2d_tex_fs.glsl");

        mCenterImage = new Sprite("images/oh_lonely.jpg", Sprite.SpriteType.kCircle);
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        mCenterImage.scaleTo(0.55f, 0.55f, 0.0f);
        mCenterImage.translateTo(sc.x/2 - mCenterImage.getWidth()/2, sc.y/2 - mCenterImage.getHeight()/2, 0);

        mCircleBars = new CircleBarsTexture(vs, fs, "images/bar.png", mCenterImage.getWidth()/2 + 30);
        mBarsRenderer = mCircleBars;
        mCircleBars.setCenter();

        Image image = Director.getInstance().getImageLoader().loadFromAssets("images/background.jpg", true, new Size(0.5f, 0.5f), 10);
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
        mCircleBars.render(delta);
        glDisable(GL_BLEND);

        mParams.rotateDegrees -= delta * 12;
        mCenterImage.rotateTo(mParams.rotateDegrees, 0, 0, 1);
        mCenterImage.render(delta);


    }
}
