package com.shark.dynamics.graphics.renderer.r3d;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.IRenderer;
import com.shark.dynamics.graphics.renderer.r3d.light.PointLight;
import com.shark.dynamics.graphics.shader.Shader;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES30.glBindVertexArray;

public class I3DRenderer extends IRenderer {

    private List<PointLight> mPointLights = new ArrayList<>();
    private boolean mShowPointLights = false;

    protected Shader mShadowShader;
    protected Camera mShadowCamera;
    private final FloatBuffer mCameraBuffer = ByteBuffer.allocateDirect(16*4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();

    private Matrix4f mShadowProj;
    private final FloatBuffer mProjBuffer = ByteBuffer.allocateDirect(16*4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();

    public I3DRenderer() {
        super();
    }

    public I3DRenderer(String vs, String fs) {
        super(vs, fs);
    }

    public I3DRenderer(String vs, String gs, String fs) {
        super(vs, gs, fs);
    }

    @Override
    protected void initRenderer() {
        super.initRenderer();
    }

    public void initShadowShader() {
        String vs = Director.getInstance().loaderShaderFromAssets("shader/3d/shadow_base_vs.glsl");
        String fs = Director.getInstance().loaderShaderFromAssets("shader/3d/shadow_base_fs.glsl");
        mShadowShader = new Shader(vs, fs);
    }

    public void setShadowCamera(Camera camera) {
        mShadowCamera = camera;
    }

    public void setShadowProjection(Matrix4f proj) {
        mShadowProj = proj;
    }

    public void renderShadow(float delta) {
        mShadowShader.use();
        glBindVertexArray(mRenderVAO);

        setUniformMatrices(mShadowShader);
        loadShadowCamera(mShadowShader);
        loadShadowProjection(mShadowShader);
    }

    public Shader getShadowShader() {
        return mShadowShader;
    }

    public int getRenderVAO() {
        return mRenderVAO;
    }

    public void loadShadowCamera(Shader shader) {
        Matrix4f view = mShadowCamera.lookAtOrigin();
        mCameraBuffer.position(0);
        view.get(mCameraBuffer);
        mCameraBuffer.position(0);
        glUniformMatrix4fv(shader.getUniformLocation("shadowView"),
                1,
                false,
                mCameraBuffer);
    }

    private void loadShadowProjection(Shader shader) {
        mProjBuffer.position(0);
        mShadowProj.get(mProjBuffer);
        mProjBuffer.position(0);
        glUniformMatrix4fv(shader.getUniformLocation("shadowProj"),
                1,
                false,
                mProjBuffer);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        setUniformMatrices(mShader);

        loadPointLights();
        loadCameraPosition();

        if (mShadowCamera != null) {
            loadShadowProjection(mShader);
            loadShadowCamera(mShader);
        }

        if (mEnableAlpha) {
            mShader.setUniformInt("enableAlpha", 1);
            mShader.setUniformFloat("alpha", mAlpha);
        }

        if (mShowPointLights && mPointLights != null && mPointLights.size() > 0) {
            for (PointLight light : mPointLights) {
                light.showIndicator();
            }
        }

        glBindVertexArray(mRenderVAO);
        mShader.use();
    }

    public void setUniformMatrices(Shader shader) {
        glUniformMatrix4fv(shader.getUniformLocation("view"),
                1,
                false,
                Director.getInstance().getViewBuffer3D());

        glUniformMatrix4fv(shader.getUniformLocation("projection"),
                1,
                false,
                Director.getInstance().getPerspectiveMatrix());

        if (!isCustomModelMatrix()) {
            mModelMatrix.identity();
            mModelMatrix.translate(mTranslate);
            mModelMatrix.rotate((float)Math.toRadians(mRotateDegree), mRotateAxis);
            mModelMatrix.rotate((float)Math.toRadians(mRotateYDegree), 0, 1, 0);
            mModelMatrix.rotate((float)Math.toRadians(mRotateXDegree), 1, 0, 0);
            mModelMatrix.rotate((float)Math.toRadians(mRotateZDegree), 0, 0, 1);
            mModelMatrix.scale(mScale);
            glUniformMatrix4fv(shader.getUniformLocation("model"),
                    1,
                    false,
                    getModelBuffer());
        }
    }

    public void addPointLight(PointLight light) {
        if (!mPointLights.contains(light)) {
            mPointLights.add(light);
        }
    }

    public void addPointLights(List<PointLight> lights) {
        mPointLights.addAll(lights);
    }

    public List<PointLight> getPointLights() {
        return mPointLights;
    }

    private void loadPointLights() {
        mShader.setUniformInt("pointLightSize", mPointLights.size());
        for (int i = 0; i < mPointLights.size(); i++) {
            PointLight light = mPointLights.get(i);
            mShader.setUniformVec3("pointLights["+i+"].position", light.position.x, light.position.y, light.position.z);
            mShader.setUniformVec3("pointLights["+i+"].color", light.color.x, light.color.y, light.color.z);
            mShader.setUniformFloat("pointLights["+i+"].constant", light.constant);
            mShader.setUniformFloat("pointLights["+i+"].linear", light.linear);
            mShader.setUniformFloat("pointLights["+i+"].quadratic", light.quadratic);
        }
    }

    private void loadCameraPosition() {
        Vector3f cameraPos = Director.getInstance().getCamera().getCameraPos();
        mShader.setUniformVec3("cameraPosition", cameraPos.x, cameraPos.y, cameraPos.z);
    }

    public void setShowPointLights(boolean show) {
        mShowPointLights = show;
    }

}
