package com.shark.dynamics.graphics.renderer.visualizer;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.bars.ExpandRing;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.texture.Image;
import com.shark.dynamics.graphics.renderer.texture.Texture;

import org.joml.Vector2f;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GLLonelyPlanet extends IGLVisualizer {

    private float mCenterImageRotate = 0;
    private Sprite mCenterImage;
    private Sprite mBackground;
    private Vector2f mCenter;

    private int mMaxRingSize = 5;
    private List<ExpandRing> mExpandRings;
    private long mLastGenRingTime;

    public GLLonelyPlanet() {
        mCenterImage = new Sprite("images/oh_lonely.jpg", Sprite.SpriteType.kCircle);
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        mCenterImage.scaleTo(0.55f, 0.55f, 0.0f);
        mCenterImage.translateTo(sc.x/2 - mCenterImage.getWidth()/2, sc.y/2 - mCenterImage.getHeight()/2, 0);
        mCenter = new Vector2f(sc.x/2, sc.y/2);

        Image image = Director.getInstance().getImageLoader().loadFromAssets("images/background_star.jpg", true, new Size(0.5f, 0.5f), 10);
        Texture texture = new Texture(image);
        mBackground = new Sprite(texture, Sprite.SpriteType.kRect,
                Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl"),
                Director.getInstance().loaderShaderFromAssets("shader/texture_2d/tex_enhance_fs.glsl"));
        mBackground.setAsBackground();

        mExpandRings = new ArrayList<>();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        mLastGenRingTime += delta*1000;

        mBackground.getShader().use();
        mBackground.getShader().setUniformFloat("enhance", 0.3f);
        mBackground.render(delta);

        mCenterImageRotate -= delta * 12;
        mCenterImage.rotateTo(mCenterImageRotate, 0, 0, 1);
        mCenterImage.render(delta);

        genRing();
        renderRings(delta);
    }

    private void genRing() {
        if (mLastGenRingTime >= 800) {
            mLastGenRingTime = 0;
            mExpandRings.add(new ExpandRing(mCenter, mCenterImage.getWidth()/2+6, 10));
        }
        Iterator<ExpandRing> it = mExpandRings.iterator();
        while (it.hasNext()) {
            ExpandRing ring = it.next();
            if (!ring.isAlive()) {
                it.remove();
            }
        }
    }

    private void renderRings(float delta) {
        for (ExpandRing ring : mExpandRings) {
            ring.render(delta);
        }
    }

}
