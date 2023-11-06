package com.shark.dynamics.graphics.renderer.r2d;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.texture.Texture;

import static android.opengl.GLES20.GL_LINE_STRIP;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;

public class CircleDots extends Polygon {

    public CircleDots(int sliceBorder, float radius) {
        super(sliceBorder, radius, "shader/base_2d_vs.glsl", "shader/circle_dot_fs.glsl");
        mWidth = radius*2;
        mHeight = mWidth;
        mOriginWidth = mWidth;
        mOriginHeight = mHeight;
        mRadius = radius;
        setDrawFill(false);
        setDrawStroke(false);
    }

    @Override
    public boolean isCustomModelMatrix() {
        return false;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        mShader.setUniformFloat("pointSize", 12);
        glDrawArrays(GL_POINTS, 1, mSliceBorder);
    }
}
