package com.shark.dynamics.graphics.renderer.visualizer3D;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars3d.CircleBars3D;
import com.shark.dynamics.graphics.renderer.bars3d.CircleBarsTexture3D;
import com.shark.dynamics.graphics.renderer.bars3d.ExploreUniverse;
import com.shark.dynamics.graphics.renderer.r2d.Ring;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.r2d.font.Label;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleSystem;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleType;
import com.shark.dynamics.graphics.renderer.r3d.Sprite3D;
import com.shark.dynamics.graphics.renderer.visualizer.IGLVisualizer;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;

public class GLExploreUniverse extends IGLVisualizer  {

    private ExploreUniverse mEU;

    private CircleBarsTexture3D mCircleBars;
    private Sprite3D mCircleImage;

    private Ring mRingInner;
    private ParticleSystem mCenterPS;
    private Label mTimeLabel;

    private Vector2f mCenter;

    public GLExploreUniverse() {
        glEnable(GL_DEPTH_TEST);

        mEU = new ExploreUniverse();

        mCircleBars = new CircleBarsTexture3D();
        //mCircleBars.translateTo(0, 0, -2.0f);
        mCircleBars.setTintColor(new Vector3f(0.9f, 0.8f, 0.3f));
        mBars3DRenderer = mCircleBars;

        mCircleImage = new Sprite3D("images/oh_lonely.jpg", Sprite.SpriteType.kCircle);
        mCircleImage.scaleTo(1.15f, 1.15f, 1.0f);
        mCircleImage.translateTo(0, 0, -2.0f);

        mCenter = Director.getInstance().getDevice().getRealCenter();

        float step = 50;
        float radius = 230;
        mRingInner = new Ring(150, radius, step, new Vector3f(0.9f, 0.6f, 0.3f),
                new Vector3f(0.9f, 0.8f, 0.3f));
        mRingInner.translateTo(mCenter.x-radius, mCenter.y-radius, 0);

        mCenterPS = new ParticleSystem("images/particle_circle.png");
        mCenterPS.setParticleType(ParticleType.kMovingCenter);
        mCenterPS.setPosition(mCenter.x, mCenter.y);
        mCenterPS.setRadius(radius-step/2);
        mCenterPS.setGenParticleCount(5);
        mCenterPS.setGenDuration(200);
        mCenterPS.setColorOverlay(true);
        mCenterPS.setTintColor(new Vector3f(0.9f, 0.8f, 0.3f));

        mTimeLabel = new Label("20:35");

    }

    @Override
    public void render(float delta) {
        glClear(GL_DEPTH_BUFFER_BIT);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        Director.getInstance().getCamera().getCameraPos().y = 0;

        mCircleBars.render(delta);
        mRingInner.render(delta);

        mTimeLabel.updateText(getCurrentTime());
        mTimeLabel.translateTo(mCenter.x - mTimeLabel.getWidth()/2, mCenter.y, 0);
        mTimeLabel.render(delta);

        mCenterPS.render(delta);
        mEU.render(delta);

        glDisable(GL_BLEND);
    }

    private String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        return hour + ":" + (min < 10 ? "0" + min : min);
    }
}
