package com.shark.dynamics.music.effect;

import android.content.Context;
import static android.opengl.GLES32.*;

import android.opengl.GLSurfaceView;
import android.util.Log;

import com.shark.dynamics.basic.instance.InstanceUtil;
import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.shaderit.ShaderIt;
import com.shark.dynamics.graphics.renderer.visualizer.IGLVisualizer;
import com.shark.dynamics.graphics.renderer.visualizer.VisualizerParams;
import com.shark.dynamics.graphics.renderer.visualizer3D.GLShaderIt;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class EffectRender implements GLSurfaceView.Renderer {

    private static final String TAG  = "Render";

    private final Context mContext;
    private long mLastRenderTime = 0;
    private IGLVisualizer mGLVisualizer;
    private EffectItem mEffectItem;

    private int mWidth;
    private int mHeight;

    public EffectRender(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "surface created");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "surface changed");
        glViewport(0, 0, width, height);

        Director.getInstance().init(mContext, width, height);
        VisualizerParams params = null;
        if (mGLVisualizer != null) {
            params = mGLVisualizer.getSavedInstance();
        }
        mGLVisualizer = (IGLVisualizer) InstanceUtil.instance(mEffectItem.clazz);
        if (mGLVisualizer instanceof GLShaderIt) {
            ((GLShaderIt) mGLVisualizer).init(mWidth, mHeight);
        }
        if (params != null && mGLVisualizer != null) {
            mGLVisualizer.setSavedParams(params);
        }

        Log.i(TAG, "surface created ......" + this);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        if (mLastRenderTime == 0) {
            mLastRenderTime = System.currentTimeMillis();
        }
        long currentTime = System.currentTimeMillis();
        float delta = currentTime - mLastRenderTime;
        delta = delta*1.0f/1000;

        mGLVisualizer.render(delta);

        mLastRenderTime = currentTime;
    }

    public void setEffectItem(EffectItem item) {
        mEffectItem = item;
    }

    public void updateMCArray(float[] data) {
        if (mGLVisualizer != null) {
            mGLVisualizer.updateMCArray(data);
        }
    }

    public void updateSGSArray(float[] data) {
        if (mGLVisualizer != null) {
            mGLVisualizer.updateSGSArray(data);
        }
    }

    public void onResume() {
        mLastRenderTime = System.currentTimeMillis();
        if (mGLVisualizer != null) {
            mGLVisualizer.onResume();
        }
    }

    public void onPause() {
        if (mGLVisualizer != null) {
            mGLVisualizer.onPause();
        }
    }

    public void updateSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void onDestroy() {
        Director.getInstance().dispose();
    }
}
