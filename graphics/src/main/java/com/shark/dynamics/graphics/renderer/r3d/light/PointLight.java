package com.shark.dynamics.graphics.renderer.r3d.light;

import com.shark.dynamics.graphics.renderer.r3d.Cube;
import com.shark.dynamics.graphics.shader.Shader;

import org.joml.Vector3f;

public class PointLight {

    public Cube mLightIndicator;

    public PointLight(Vector3f p, Vector3f c) {
        position = p;
        color = c;
    }

    public Vector3f position;
    public Vector3f color;

    public float constant = 1.0f;
    public float linear = 0.35f;
    public float quadratic = 0.44f;

    public void showIndicator() {
        if (mLightIndicator == null) {
            mLightIndicator = new Cube(color);
        }
        mLightIndicator.scaleTo(0.1f, 0.1f, 0.1f);
        mLightIndicator.translateTo(position.x, position.y, position.z);
        mLightIndicator.render(0);
    }

}
