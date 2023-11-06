package com.shark.dynamics.graphics.renderer.bars3d;

import com.shark.dynamics.graphics.renderer.r3d.particlesystem.ParticleSystem3D;
import com.shark.dynamics.graphics.renderer.r3d.particlesystem.ParticleType3D;

import org.joml.Vector3f;

public class ExploreUniverse extends IBars3DRenderer {

    private ParticleSystem3D mParticleSystem;

    public ExploreUniverse() {

        mParticleSystem = new ParticleSystem3D();
        mParticleSystem.setParticleType(ParticleType3D.kMovingStar);
        mParticleSystem.setGenDuration(200);
        mParticleSystem.setGenParticleCount(5);
        mParticleSystem.setColorOverlay(true);
        mParticleSystem.setTintColor(new Vector3f(0xdd/255.0f, 0xa0/255.0f, 0xdd/255.0f));
    }


    @Override
    public void render(float delta) {
        super.render(delta);

        mParticleSystem.render(delta);

    }
}
