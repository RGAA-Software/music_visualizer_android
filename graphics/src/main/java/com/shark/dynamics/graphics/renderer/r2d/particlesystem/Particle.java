package com.shark.dynamics.graphics.renderer.r2d.particlesystem;

import org.joml.Matrix4f;

public class Particle {
    public long duration;
    public long runDuration;
    public float x;
    public float y;
    public float z;
    public float xVelocity;
    public float yVelocity;
    public float zVelocity;
    public float alpha = 1.0f;
    public boolean reverseAlpha = false;
    public float xScale;
    public float yScale;
    public float rotate;
    public float rotateDelta;
    public float gravity;

    public Matrix4f model = new Matrix4f();

    public boolean isAlive() {
        return runDuration < duration;
    }

    public float getDecreaseAlpha() {
        if (duration <= 0) {
            return 0;
        }
        float alpha = this.alpha - runDuration*1.0f/duration;
        if (alpha < 0) {
            return 0;
        }
        if (reverseAlpha) {
            return Math.max(1.0f - alpha, 0);
        } else {
            return alpha;
        }
    }

    public float getDecreaseScale() {
        return Math.max(xScale, yScale) * getDecreaseAlpha();
    }

    public void update(float delta) {
        yVelocity += gravity;
        x += xVelocity;
        y += yVelocity;
        z += zVelocity;
        rotate += rotateDelta;
        runDuration += delta*1000;
    }
}
