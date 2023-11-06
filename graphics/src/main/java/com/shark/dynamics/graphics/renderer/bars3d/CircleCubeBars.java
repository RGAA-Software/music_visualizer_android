package com.shark.dynamics.graphics.renderer.bars3d;

import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleSystem;
import com.shark.dynamics.graphics.renderer.r3d.Cube;
import com.shark.dynamics.graphics.renderer.r3d.Sprite3D;
import com.shark.dynamics.graphics.renderer.r3d.particlesystem.ParticleSystem3D;
import com.shark.dynamics.graphics.renderer.r3d.particlesystem.ParticleType3D;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class CircleCubeBars extends IBars3DRenderer {

    private int mCubeSize = 30;
    private float mRadius = 2.0f;
    private List<Cube> mCubes;
    private float mRotate;

    private Sprite3D mMagicSprite;

    private ParticleSystem3D mParticleSystem;

    public CircleCubeBars() {
        mCubes = new ArrayList<>();
        float itemAngel = 360.0f/mCubeSize;
        for (int i = 0; i < mCubeSize; i++) {
            float angel = itemAngel*i;
            float x = (float)(mRadius * Math.cos(Math.toRadians(angel)));
            float z = (float)(mRadius * Math.sin(Math.toRadians(angel)));
            Cube cube = new Cube();
            cube.translateTo(x, 0, z);
            cube.scaleTo(0.12f, 1.0f, 0.08f);
            mCubes.add(cube);
        }

        mMagicSprite = new Sprite3D("images/magic.png", Sprite.SpriteType.kRect);
        mMagicSprite.translateTo(0, -1.5f, 0);
        mMagicSprite.scaleTo(1.8f, 1.8f, 0);

        mParticleSystem = new ParticleSystem3D();
        mParticleSystem.setParticleType(ParticleType3D.kCylinder);
        mParticleSystem.setGenParticleCount(8);
        mParticleSystem.setGenDuration(30);
        mParticleSystem.setPosition(0, -1.5f, 0);
        mParticleSystem.setTintColor(new Vector3f(0.3f, 0.9f, 0.2f));
        mParticleSystem.setColorOverlay(true);
    }

    @Override
    public void render(float delta) {
        if (mSGSArray == null) {
            return;
        }

        mRotate += delta*50;

        fallDownSGS(mSGSArray, mCubeSize+2);

        int idx = 0;
        for (Cube c : mCubes) {
            float z = c.getTranslate().z;
            c.setEnableAlpha(true);
            c.setAlpha(Math.min((mRadius+z)/(2*mRadius)+0.25f, 1.0f));
            c.updateYValue(mDrawSGSBars[idx++]*1.0f/60.0f);
            c.rotateTo(mRotate, 0, 1, 0);
            c.render(delta);
        }

        mMagicSprite.setRotateZDegree(mRotate);
        mMagicSprite.setRotateXDegree(-90);
        mMagicSprite.render(delta);

        mParticleSystem.render(delta);
    }
}
