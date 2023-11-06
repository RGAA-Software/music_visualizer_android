package com.shark.dynamics.graphics.renderer.r2d;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.IRenderer;

import static android.opengl.GLES20.glUniformMatrix4fv;

public class I2DRenderer extends IRenderer {

    protected float mWidth;
    protected float mHeight;

    protected float mOriginWidth;
    protected float mOriginHeight;

    public I2DRenderer() {
        super();
    }

    public I2DRenderer(String vs, String fs) {
        super(vs, fs);
    }

    public I2DRenderer(String vs, String gs, String fs) {
        super(vs, gs, fs);
    }

    @Override
    protected void initRenderer() {
        super.initRenderer();
    }

    public float getWidth() {
        return mWidth;
    }

    public float getHeight() {
        return mHeight;
    }

    @Override
    public void scaleTo(float x, float y, float z) {
        super.scaleTo(x, y, z);
        mWidth = mOriginWidth * x;
        mHeight = mOriginHeight * y;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        glUniformMatrix4fv(mShader.getUniformLocation("view"),
                1,
                false,
                Director.getInstance().getViewMatrix());

        glUniformMatrix4fv(mShader.getUniformLocation("projection"),
                1,
                false,
                Director.getInstance().getOrthographicMatrix());

        if (!isCustomModelMatrix()) {
            mModelMatrix.identity();
            mModelMatrix.translate(mTranslate);

            mModelMatrix.translate(mWidth / 2, mHeight / 2, 0);
            mModelMatrix.rotate((float) Math.toRadians(mRotateDegree), 0, 0, 1);
            mModelMatrix.translate(-mWidth / 2, -mHeight / 2, 0);

            mModelMatrix.scale(mScale);

            glUniformMatrix4fv(mShader.getUniformLocation("model"),
                    1,
                    false,
                    getModelBuffer());
        }

    }
}
