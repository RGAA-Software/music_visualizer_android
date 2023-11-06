package com.shark.dynamics.graphics.renderer.r3d.particlesystem;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.Particle;

import org.joml.Vector2f;

import java.util.Random;

public class InternalParticleGenerator3D {

    private static Random sRandom = new Random();

    public static Particle genCylinderParticle(float x, float y, float z) {
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        Particle particle = new Particle();
        float particleRegion = 0.2f;

        particle.x = (sRandom.nextFloat()-0.5f) * 0.12f + x;
        particle.y = sRandom.nextFloat()*particleRegion + y;
        particle.z = sRandom.nextFloat()*0.03f + z;

        particle.duration = sRandom.nextInt(6) * 1000;
//        long minDuration = 15*1000;
//        if (particle.duration < minDuration) {
//            particle.duration = minDuration;
//        }

        float vRandom = sRandom.nextFloat();
        if (vRandom < 0.85) {
            //vRandom = 0.85f;
        }
        particle.yVelocity = vRandom * 0.0725f;
        particle.xVelocity = 0;
        particle.xScale = sRandom.nextFloat();
        if (particle.xScale < 0.9f) {
            particle.xScale = 0.9f;
        }
        particle.xScale *= 0.25f;
        particle.yScale = particle.xScale;
        particle.rotate = -10;
        particle.alpha = 1.0f;
        return particle;
    }

    public static Particle genMovingStar() {
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        Particle particle = new Particle();
        float particleRegion = 0.2f;

        particle.x = (sRandom.nextFloat()-0.5f) * 4.5f;
        particle.y = (sRandom.nextFloat()-0.5f) * 5.5f;
        particle.z = sRandom.nextFloat()* -5f;

        particle.duration = sRandom.nextInt(6) * 1000;
        long minDuration = 3*1000;
        if (particle.duration < minDuration) {
            particle.duration = minDuration;
        }

        float vRandom = sRandom.nextFloat();
        if (vRandom < 0.85) {
            vRandom = 0.85f;
        }
        particle.xVelocity = sRandom.nextFloat() * 0.00345f;
        particle.yVelocity = sRandom.nextFloat() * 0.00725f;
        particle.zVelocity = vRandom * 0.08f;

        if (particle.x < 0 && particle.y > 0) {
            particle.xVelocity *= -1;
        } else if (particle.x < 0 && particle.y < 0) {
            particle.xVelocity *= -1;
            particle.yVelocity *= -1;
        } else if (particle.x > 0 && particle.y < 0) {
            particle.yVelocity *= -1;
        }

        particle.xScale = sRandom.nextFloat();
        if (particle.xScale < 0.9f) {
            particle.xScale = 0.9f;
        }
        particle.xScale *= 0.25f;
        particle.yScale = particle.xScale;
        particle.rotate = -10;
        particle.alpha = 1.0f;
        particle.reverseAlpha = true;
        return particle;
    }

    public static Particle genFlameParticle(float x, float y, float z) {
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        Particle particle = new Particle();
        float radius = 0.20f;
        float angel = sRandom.nextFloat()*360;
        float xPos = radius * sRandom.nextFloat() * (float)Math.cos( Math.toRadians(angel) );
        float zPos = radius * sRandom.nextFloat() * (float)Math.sin( Math.toRadians(angel) );
        particle.x = x + xPos;
        particle.z = z + zPos;
        particle.y = y;
        particle.duration = (long) (Math.max(sRandom.nextFloat(), 0.5f) * 2 * 1000);

        float vRandom = sRandom.nextFloat();
        if (vRandom < 0.85) {
            //vRandom = 0.85f;
        }
        particle.xVelocity = vRandom * 0.002f;
        particle.yVelocity = vRandom * 0.0235f;
        particle.zVelocity = vRandom * 0.002f;

        if ((angel >= 0 && angel < 90) || (angel >= 270 && angel < 360)) {
            particle.xVelocity = -particle.xVelocity;
            particle.zVelocity = -particle.zVelocity;
        } else if ((angel >= 90 && angel < 180) || (angel >= 180 && angel < 270)) {
            particle.xVelocity = particle.xVelocity;
            particle.zVelocity = particle.zVelocity;
        }

        particle.xScale = sRandom.nextFloat();
        if (particle.xScale < 0.9f) {
            particle.xScale = 0.9f;
        }
        particle.xScale *= 0.35f;
        particle.yScale = particle.xScale;
        particle.rotate = -10;
        particle.alpha = 1.0f;
        return particle;
    }

    public static Particle genSnowParticle(float alpha, long maxLifeTime) {
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        float aspect = sc.y/sc.x;
        Particle particle = new Particle();
        float particleRegion = 0.2f;

        particle.x = (sRandom.nextFloat()-0.5f) * 4.5f;
        particle.y = sRandom.nextFloat() * particleRegion + aspect * 2;
        particle.z = (0.5f - sRandom.nextFloat()) * 20f;

        particle.duration = sRandom.nextInt((int)maxLifeTime/1000) * 1000;
        long minDuration = maxLifeTime/2;
        if (particle.duration < minDuration) {
            particle.duration = minDuration;
        }

        float vRandom = sRandom.nextFloat();
        if (vRandom < 0.85) {
            vRandom = 0.85f;
        }
        particle.yVelocity = -vRandom * 0.0125f;
        particle.xVelocity = 0;
        particle.xScale = sRandom.nextFloat();
        if (particle.xScale < 0.9f) {
            particle.xScale = 0.9f;
        }
        particle.xScale *= 0.15f;
        particle.yScale = particle.xScale;
        particle.rotate = -10;
        particle.alpha = alpha + particle.z / 10.0f;
        return particle;
    }
}
