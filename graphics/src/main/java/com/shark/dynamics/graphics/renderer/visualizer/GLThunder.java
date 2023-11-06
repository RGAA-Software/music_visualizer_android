package com.shark.dynamics.graphics.renderer.visualizer;

import android.util.Log;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars.CircleBars;
import com.shark.dynamics.graphics.renderer.bars.LineWave;
import com.shark.dynamics.graphics.renderer.framebuffer.FrameBuffer;
import com.shark.dynamics.graphics.renderer.r2d.Line;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.r2d.Thunder;
import com.shark.dynamics.graphics.renderer.r2d.bezier.BezierPointGenerator;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleSystem;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleType;
import com.shark.dynamics.graphics.renderer.texture.Image;
import com.shark.dynamics.graphics.renderer.texture.Texture;
import com.shark.dynamics.graphics.shader.Shader;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.opencv.core.Size;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_DST_ALPHA;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glEnable;
import static java.lang.Math.pow;

public class GLThunder extends IGLVisualizer {

    private static final String TAG = "Thunder";

    private CircleBars mCircleBars;

    private Sprite mCenterImage;
    private float mCenterImageRotate = 0;

    private ParticleSystem mParticleSystem;

    private Thunder mThunder;
    private Vector2f mCenter;
    private LineWave mLiveWave;

    private FrameBuffer mFrameBuffer;
    private Sprite mFBSprite;
    private Sprite mBackground;
    private Vector3f mThunderColor = new Vector3f(0xb9*1.0f/255, 0xb9*1.0f/255, 0xFF*1.0f/255);
    private float mThunderDuration = 5000;
    private float mBGAlpha;
    private long mTimeLapses;

    public GLThunder() {
        mCenter = Director.getInstance().getDevice().getRealCenter();
        String vs = Director.getInstance()
                .loaderShaderFromAssets("shader/base_2d_vs.glsl");
        String fs = Director.getInstance()
                .loaderShaderFromAssets("shader/base_2d_fs.glsl");
        mCircleBars = new CircleBars(vs, fs);
        mBarsRenderer = mCircleBars;
        mCircleBars.setCenter();

        mCenterImage = new Sprite("images/oh_lonely.jpg", Sprite.SpriteType.kCircle);
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        mCenterImage.scaleTo(0.35f, 0.35f, 0.0f);
        mCenterImage.translateTo(sc.x/2 - mCenterImage.getWidth()/2, sc.y/2 - mCenterImage.getHeight()/2, 0);

        float screenScale = 1.0f/1.0f;

        mFrameBuffer = new FrameBuffer();
        mFrameBuffer.init((int)(sc.x*screenScale), (int)(sc.y*screenScale));

        String tVS = Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl");
        String tFS = Director.getInstance().loaderShaderFromAssets("shader/uniform_color_alpha_fs.glsl");
        mThunder = new Thunder(new Vector2f(mFrameBuffer.getWidth()/2, mFrameBuffer.getHeight()/2), tVS, tFS, screenScale);

        String fbVS = Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl");
        String fbFS = Director.getInstance().loaderShaderFromAssets("shader/texture_2d/base_2d_tex_fs.glsl");
        mFBSprite = new Sprite(new Texture(mFrameBuffer.getFrameBufferTexId(), mFrameBuffer.getWidth(), mFrameBuffer.getHeight()), Sprite.SpriteType.kRect, fbVS, fbFS);
        mFBSprite.setAsBackground();

        Image image = Director.getInstance().getImageLoader().loadFromAssets("images/background_star.jpg", true, new Size(0.5f, 0.5f), 0);
        Texture texture = new Texture(image);
        mBackground = new Sprite(texture, Sprite.SpriteType.kRect,
                Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl"),
                Director.getInstance().loaderShaderFromAssets("shader/texture_2d/tex_enhance_fs.glsl"));
        mBackground.setAsBackground();

        mParticleSystem = new ParticleSystem("images/rain.png");
        mParticleSystem.setParticleType(ParticleType.kRain);
        mParticleSystem.setGenDuration(300);
        mParticleSystem.setGenParticleCount(1);
        mParticleSystem.setScreenScale(screenScale);

        mLiveWave = new LineWave(50, 50, 350);
        mBarsRenderer = mLiveWave;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        mTimeLapses += delta * 1000;

        //mFrameBuffer.begin();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        float alpha = (float) easeOutElastic(mTimeLapses*1.0f/mThunderDuration*3);

        mBackground.getShader().use();

        if (mTimeLapses >= 1200) {
            mBackground.getShader().setUniformFloat("enhance", mBGAlpha - (mTimeLapses-1200)*1.0f/mThunderDuration/4);
        } else {
            mBGAlpha = 1.0f - alpha/2;
            mBackground.getShader().setUniformFloat("enhance", mBGAlpha);
        }
        mBackground.render(delta);

        Shader shader = mThunder.getShader();
        shader.use();
        shader.setUniformFloat("alpha", Math.max(alpha, 0.1f));
        float thunderLife = 1500;
        float thunderAlpha = 0;
        if (mTimeLapses > thunderLife) {
            thunderAlpha = 1.0f - (mTimeLapses-thunderLife)/mThunderDuration*3.5f;
            shader.setUniformFloat("alpha", Math.max(thunderAlpha, 0.1f));
        }
        shader.setUniformVec3("color", mThunderColor.x, mThunderColor.y, mThunderColor.z);
        mThunder.render(delta);
        if (thunderAlpha >= 0) {
        }

        mParticleSystem.render(delta);

        //mFrameBuffer.end();

        if (mTimeLapses > mThunderDuration) {
            mTimeLapses = 0;
            mThunder.regenThunderLines(true);
        }

        //mFBSprite.render(delta);
        mLiveWave.render(delta);
    }

    private double easeOutElastic(float x) {
        double c4 = (2 * Math.PI) / 3;
        return x == 0
                ? 0
                : x == 1
                ? 1
                : Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * c4) + 1;
    }
}
