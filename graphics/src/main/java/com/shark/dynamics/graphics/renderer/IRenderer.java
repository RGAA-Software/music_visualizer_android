package com.shark.dynamics.graphics.renderer;

import android.opengl.Matrix;

import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.GLUtil;
import com.shark.dynamics.graphics.util.MatrixUtil;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES32.*;

public class IRenderer {

    protected int mRenderVAO;

    protected Shader mShader;
    protected ProjectionType mType = ProjectionType.kProj2D;
    protected Matrix4f mModelMatrix;
    private FloatBuffer mModelBuffer;

    protected Vector3f mTranslate = new Vector3f();
    protected Vector3f mRotateAxis = new Vector3f();
    protected float mRotateDegree = 0;

    protected float mRotateXDegree;
    protected float mRotateYDegree;
    protected float mRotateZDegree;

    protected Vector3f mScale = new Vector3f(1.0f, 1.0f, 1.0f);

    protected boolean mEnableAlpha = false;
    protected float mAlpha = 1.0f;

    public IRenderer() {
        mShader = new Shader(DefaultShader.sDefaultVertexShader, DefaultShader.sDefaultFragmentShader);
        initRenderer();
    }

    public IRenderer(String vs, String fs) {
        mShader = new Shader(vs, fs);
        initRenderer();

    }

    public IRenderer(String vs, String gs, String fs) {
        mShader = new Shader(vs, gs, fs);
        initRenderer();
    }

    protected void initRenderer() {
        mRenderVAO = GLUtil.glGenVertexArray();
        glBindVertexArray(mRenderVAO);

        mModelMatrix = new Matrix4f();
        mModelBuffer = ByteBuffer.allocateDirect(16*4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
    }

    public Shader getShader() {
        return mShader;
    }

    public boolean isCustomModelMatrix() {
        return false;
    }

    public void render(float delta) {
        glBindVertexArray(mRenderVAO);
        mShader.use();
    }

    public FloatBuffer getModelBuffer() {
        mModelMatrix.get(mModelBuffer);
        mModelBuffer.position(0);
        return mModelBuffer;
    }

    public void translateTo(float x, float y, float z) {
        mTranslate.set(x, y, z);
    }

    public void translateBy(float x, float y, float z) {

    }

    public void rotateTo(float degree, float x, float y, float z) {
        mRotateAxis.set(x, y, z);
        mRotateDegree = degree;
    }

    public void rotateBy(float degree, float x, float y, float z) {

    }

    public void scaleTo(float scale) {
        mScale.set(scale, scale, scale);
    }

    public void scaleTo(float x, float y, float z) {
        mScale.set(x, y, z);
    }

    public void scaleBy(float x, float y, float z) {

    }

    public void setRotateXDegree(float degree) {
        mRotateXDegree = degree;
    }

    public void setRotateYDegree(float degree) {
        mRotateYDegree = degree;
    }

    public void setRotateZDegree(float degree) {
        mRotateZDegree = degree;
    }

    public Vector3f getTranslate() {
        return mTranslate;
    }

    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }

    public void setEnableAlpha(boolean enable) {
        mEnableAlpha = enable;
    }

    public void dispose() {

    }

}
