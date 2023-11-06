package com.shark.dynamics.graphics.renderer.visualizer;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars.CircleDancingDots;
import com.shark.dynamics.graphics.renderer.r2d.Circle;
import com.shark.dynamics.graphics.renderer.r2d.CircleDots;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleSystem;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleType;
import com.shark.dynamics.graphics.renderer.texture.Image;
import com.shark.dynamics.graphics.renderer.texture.Texture;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.opencv.core.Size;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;

public class GLDancingDots extends IGLVisualizer {

    private CircleDancingDots mDancingDots;
    private Sprite mBackground;
    private Sprite mCenterImage;
    private float mCenterImageRotate;

    private ParticleSystem mTrianglePS;

    public GLDancingDots() {
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

        mDancingDots = new CircleDancingDots(mCenterImage.getWidth());
        mBarsRenderer = mDancingDots;

        mTrianglePS = new ParticleSystem("images/particle_triangle.png");
        mTrianglePS.setParticleType(ParticleType.kTriangle);
        mTrianglePS.setPosition(sc.x/2, sc.y/2);
        mTrianglePS.setGenParticleCount(2);
        mTrianglePS.setGenDuration(500);
        mTrianglePS.setUseDecreaseScale(false);
        mTrianglePS.setColorOverlay(true);
        mTrianglePS.setTintColor(new Vector3f(0.2f, 1.0f, 0.2f));
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        mBackground.getShader().use();
        mBackground.getShader().setUniformFloat("enhance", 0.3f);
        mBackground.render(delta);

        mTrianglePS.render(delta);

        mCenterImageRotate -= delta * 12;
        mCenterImage.rotateTo(mCenterImageRotate, 0, 0, 1);
        mCenterImage.render(delta);

        mDancingDots.render(delta);

    }
}
