package com.shark.dynamics.graphics.renderer.visualizer;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars.VerticalBars;
import com.shark.dynamics.graphics.renderer.bars.VerticalBarsPeeling;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.r2d.anim.FrameAnimation;
import com.shark.dynamics.graphics.renderer.r2d.bezier.BezierPointGenerator;
import com.shark.dynamics.graphics.renderer.texture.Image;
import com.shark.dynamics.graphics.renderer.texture.Texture;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.opencv.core.Size;

import java.util.List;

public class GLVerticalBarsPeeling extends IGLVisualizer {

    private VerticalBarsPeeling mVerticalBars;

    private Sprite mBackground;

    private FrameAnimation mAnim;

    public GLVerticalBarsPeeling() {
        String vs = Director.getInstance()
                .loaderShaderFromAssets("shader/base_2d_vs.glsl");
        String fs = Director.getInstance()
                .loaderShaderFromAssets("shader/base_2d_fs.glsl");
        mVerticalBars = new VerticalBarsPeeling(vs, fs);
        mBarsRenderer = mVerticalBars;
        mVerticalBars.setCenterHorizontal();
        mVerticalBars.setCenterVertical();

        Image image = Director.getInstance().getImageLoader().loadFromAssets("images/background.jpg", true, new Size(0.5f, 0.5f), 10);
        Texture texture = new Texture(image);
        mBackground = new Sprite(texture, Sprite.SpriteType.kRect,
                Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl"),
                Director.getInstance().loaderShaderFromAssets("shader/texture_2d/tex_enhance_fs.glsl"));
        mBackground.setAsBackground();

        mAnim = new FrameAnimation("images/anim.png", 4, 3);
        mAnim.setPerFrameTime(60);
        mAnim.setRunTime(10000);
        mAnim.setReverseAnim(true);
        mAnim.setTintColor(new Vector3f(0.9f, 0.8f, 0.8f));

        List<Vector2f> path = BezierPointGenerator.gen3Bezier(new Vector2f(100, 100), new Vector2f(600, 600), new Vector2f(800, 2000), new Vector2f(1000, 100), 0.001f);
        mAnim.setBezier(path);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        mBackground.getShader().use();
        mBackground.getShader().setUniformFloat("enhance", 0.6f);
        mBackground.render(delta);

        mAnim.render(delta);
        mVerticalBars.render(delta);
    }
}
