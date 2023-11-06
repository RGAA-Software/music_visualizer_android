package com.shark.dynamics.graphics.renderer.r2d.particlesystem;

import com.shark.dynamics.graphics.Director;

import org.joml.Vector2f;

public class InternalParticleGenerator extends ParticleGenerator {

    public static Particle genSnowParticle(float baseScale) {
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        Particle particle = new Particle();
        int particleRegion = 200;

        particle.x = sRandom.nextFloat()*sc.x*4.0f/3.0f;
        particle.y = sRandom.nextFloat()*particleRegion + sc.y - 50;
        particle.duration = sRandom.nextInt(36) * 1000;
        long minDuration = 15*1000;
        if (particle.duration < minDuration) {
            particle.duration = minDuration;
        }
        particle.yVelocity = -sRandom.nextFloat() * 5;
        particle.xVelocity = -sRandom.nextFloat() * 1.2f;
        particle.xScale = sRandom.nextFloat();
        if (particle.xScale < 0.55f) {
            particle.xScale = 0.55f;
        }
        particle.xScale *= baseScale;
        particle.yScale = particle.xScale;
        particle.rotate = sRandom.nextFloat() * 360;
        particle.rotateDelta = (sRandom.nextFloat() > 0.5f ? 1 : -1)*sRandom.nextFloat() * 2.6f;
        return particle;
    }

    public static Particle genSparkParticle(float x, float y) {
        Particle particle = new Particle();
        particle.x = x;
        particle.y = y;
        particle.duration = (long) (sRandom.nextFloat() * 400);
        particle.yVelocity = sRandom.nextFloat() * 7 * (sRandom.nextFloat() > 0.5f ? 1.0f : -1.0f);
        particle.xVelocity = sRandom.nextFloat() * 7 * (sRandom.nextFloat() > 0.5f ? 1.0f : -1.0f);
        particle.xScale = sRandom.nextFloat() + 1.2f;
        particle.yScale = particle.xScale;
        return particle;
    }

    public static Particle genTriangleParticle(float x, float y) {
        Particle particle = new Particle();
        particle.x = x;
        particle.y = y;
        particle.duration = (long) (Math.max(sRandom.nextFloat(), 0.5f) * 30 * 1000);
        particle.yVelocity = sRandom.nextFloat() * 3 * (sRandom.nextFloat() > 0.5f ? 1.0f : -1.0f);
        particle.xVelocity = sRandom.nextFloat() * 3 * (sRandom.nextFloat() > 0.5f ? 1.0f : -1.0f);

        float nextNum = sRandom.nextFloat();
        particle.xScale = (Math.max(nextNum, 0.5f))* 2.5f;
        nextNum = sRandom.nextFloat();
        particle.yScale = (Math.max(nextNum, 0.5f)) * 2.5f;
        particle.rotate = sRandom.nextFloat() * 360;
        particle.rotateDelta = (sRandom.nextFloat() > 0.5f ? 1 : -1)*sRandom.nextFloat() * 2;
        return particle;
    }

    public static Particle genUniverseAsh(float x, float y, float radius) {
        Particle particle = new Particle();
        float angel = sRandom.nextFloat()*360;
        float xPos = radius* (float)Math.cos( Math.toRadians(angel) );
        float yPos = radius* (float)Math.sin( Math.toRadians(angel) );
        particle.x = x + xPos;
        particle.y = y + yPos;
        particle.duration = (long) (Math.max(sRandom.nextFloat(), 0.5f) * 4 * 1000);

        float xVelocity = sRandom.nextFloat() * 1.1f;
        float k = yPos / xPos;
        if (Math.abs(k) > 1) {
            xVelocity /= Math.abs(k);
        }
        if ((angel >= 0 && angel < 90) || (angel >= 270 && angel < 360)) {
            particle.xVelocity = xVelocity;
            particle.yVelocity = xVelocity * k;
        } else if ((angel >= 90 && angel < 180) || (angel >= 180 && angel < 270)) {
            particle.xVelocity = -xVelocity;
            particle.yVelocity = -xVelocity * k;
        }

        float nextNum = sRandom.nextFloat();
        float scale = 0.5f;
        particle.xScale = (Math.max(nextNum, 0.5f))* scale;
        nextNum = sRandom.nextFloat();
        particle.yScale = (Math.max(nextNum, 0.5f)) * scale;
        particle.rotate = sRandom.nextFloat() * 360;
        particle.rotateDelta = (sRandom.nextFloat() > 0.5f ? 1 : -1)*sRandom.nextFloat() * 2;
        return particle;
    }

    public static Particle genUniverseAsh(float x, float y, float radius, float enhanceAngel) {
        Particle particle = new Particle();
        float angel = sRandom.nextFloat()* enhanceAngel*2 + (180-enhanceAngel);
        float xPos = radius* (float)Math.cos( Math.toRadians(angel) );
        float yPos = radius* (float)Math.sin( Math.toRadians(angel) );
        particle.x = x + xPos;
        particle.y = y + yPos;
        particle.duration = (long) (Math.max(sRandom.nextFloat(), 0.5f) * 5 * 1000);

        float xVelocity = sRandom.nextFloat() * 2.1f;
        float k = yPos / xPos;
        if (Math.abs(k) > 1) {
            xVelocity /= Math.abs(k);
        }
        if ((angel >= 0 && angel < 90) || (angel >= 270 && angel < 360)) {
            particle.xVelocity = xVelocity;
            particle.yVelocity = xVelocity * k;
        } else if ((angel >= 90 && angel < 180) || (angel >= 180 && angel < 270)) {
            particle.xVelocity = -xVelocity;
            particle.yVelocity = -xVelocity * k;
        }

        float nextNum = sRandom.nextFloat();
        float scale = 0.5f;
        particle.xScale = (Math.max(nextNum, 0.5f))* scale;
        nextNum = sRandom.nextFloat();
        particle.yScale = (Math.max(nextNum, 0.5f)) * scale;
        particle.rotate = sRandom.nextFloat() * 360;
        particle.rotateDelta = (sRandom.nextFloat() > 0.5f ? 1 : -1)*sRandom.nextFloat() * 2;
        return particle;
    }

    public static Particle genBubbleParticle() {
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        Particle particle = new Particle();
        int particleRegion = 200;

        particle.x = sRandom.nextFloat()*sc.x*3.0f/3.0f;
        particle.y = sRandom.nextFloat()*particleRegion - 50;
        particle.duration = sRandom.nextInt(36) * 1000;
        long minDuration = 15*1000;
        if (particle.duration < minDuration) {
            particle.duration = minDuration;
        }
        particle.yVelocity = sRandom.nextFloat() * 5;
        particle.xVelocity = sRandom.nextFloat() * 1.12f;
        particle.xScale = sRandom.nextFloat() + 1.2f;
        particle.rotate = sRandom.nextFloat() * 360;
        particle.rotateDelta = (sRandom.nextFloat() > 0.5f ? 1 : -1)*sRandom.nextFloat() * 2;
        particle.yScale = particle.xScale;
        return particle;
    }

    public static Particle genRainParticle(float screenScale) {
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        Particle particle = new Particle();
        int particleRegion = 200;

        particle.x = sRandom.nextFloat()*sc.x*4.0f/3.0f;
        particle.y = sRandom.nextFloat()*particleRegion + sc.y - 50;

        particle.x *= screenScale;
        particle.y *= screenScale;

        particle.duration = sRandom.nextInt(36) * 1000;
        long minDuration = 15*1000;
        if (particle.duration < minDuration) {
            particle.duration = minDuration;
        }

        float vRandom = sRandom.nextFloat();
        if (vRandom < 0.85) {
            vRandom = 0.85f;
        }
        particle.yVelocity = -vRandom * 5 * screenScale;
        particle.xVelocity = -sRandom.nextFloat() * 1.2f * screenScale;
        particle.xScale = sRandom.nextFloat();
        if (particle.xScale < 0.9f) {
            particle.xScale = 0.9f;
        }
        particle.xScale *= 1.5f * screenScale;
        particle.yScale = particle.xScale;
        particle.rotate = -10;
        particle.alpha = 0.5f;
        return particle;
    }

    public static Particle genGravityParticle(float screenScale) {
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        Particle particle = new Particle();
        int particleRegion = 200;

        particle.x = sRandom.nextFloat()*sc.x*4.0f/3.0f;
        particle.y = sRandom.nextFloat()*particleRegion + (sc.y-particleRegion)/2;

        particle.x *= screenScale;
        particle.y *= screenScale;

        particle.duration = sRandom.nextInt(3) * 1000;
        float vRandom = sRandom.nextFloat() - 0.5f;

        particle.yVelocity = sRandom.nextFloat() * 16 * screenScale;
        particle.xVelocity = vRandom * 2.2f * screenScale;
        particle.gravity = -0.45f;

        particle.xScale = sRandom.nextFloat();
        if (particle.xScale < 0.9f) {
            particle.xScale = 0.9f;
        }
        particle.xScale *= .6f * screenScale;
        particle.yScale = particle.xScale;
        particle.rotate = -10;
        particle.alpha = 1.0f;
        return particle;
    }


    public static Particle genCenterParticle(float x, float y, float radius) {
        Particle particle = new Particle();
        float angel = sRandom.nextFloat()*360;
        float xPos = radius* (float)Math.cos( Math.toRadians(angel) );
        float yPos = radius* (float)Math.sin( Math.toRadians(angel) );
        particle.x = x + xPos;
        particle.y = y + yPos;
        particle.duration = (long) (Math.max(sRandom.nextFloat(), 0.5f) * 2 * 1000);

        float xVelocity = sRandom.nextFloat() * 2.1f;
        float k = yPos / xPos;
        if (Math.abs(k) > 1) {
            xVelocity /= Math.abs(k);
        }
        if ((angel >= 0 && angel < 90) || (angel >= 270 && angel < 360)) {
            particle.xVelocity = -xVelocity;
            particle.yVelocity = -xVelocity * k;
        } else if ((angel >= 90 && angel < 180) || (angel >= 180 && angel < 270)) {
            particle.xVelocity = xVelocity;
            particle.yVelocity = xVelocity * k;
        }

        float nextNum = sRandom.nextFloat();
        float scale = 1.3f;
        particle.xScale = (Math.max(nextNum, 0.5f))* scale;
        nextNum = sRandom.nextFloat();
        particle.yScale =particle.xScale;
        particle.rotate = sRandom.nextFloat() * 360;
        particle.rotateDelta = (sRandom.nextFloat() > 0.5f ? 1 : -1)*sRandom.nextFloat() * 2;
        return particle;
    }

}
