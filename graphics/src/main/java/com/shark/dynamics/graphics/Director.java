package com.shark.dynamics.graphics;

import android.content.Context;

import com.shark.dynamics.graphics.renderer.TextureManager;
import com.shark.dynamics.graphics.renderer.r2d.font.Character;
import com.shark.dynamics.graphics.renderer.r2d.font.Font;
import com.shark.dynamics.graphics.renderer.r2d.font.FontLoader;
import com.shark.dynamics.graphics.renderer.r3d.Camera;
import com.shark.dynamics.graphics.renderer.texture.Image;
import com.shark.dynamics.graphics.renderer.texture.ImageLoader;
import com.shark.dynamics.graphics.renderer.texture.Texture;
import com.shark.dynamics.graphics.shader.ShaderLoader;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.opencv.core.Size;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

public class Director {

    private static final Director sInstance = new Director();

    private Matrix4f mOrthographicMatrix = new Matrix4f();
    private Matrix4f mPerspectiveMatrix = new Matrix4f();
    private Matrix4f mViewMatrix = new Matrix4f();

    private ImageLoader mImageLoader;
    private TextureManager mTextureManager;
    private ShaderLoader mShaderLoader;
    private Device mDevice;
    private FontLoader mFontLoader;

    private Camera mCamera;
    private boolean mLookAtOrigin = false;

    private Font mDefaultFont;

    private boolean mInit;

    private final FloatBuffer mOrthographicBuffer = ByteBuffer.allocateDirect(16*4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();

    private final FloatBuffer mPerspectiveBuffer = ByteBuffer.allocateDirect(16*4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();

    private final FloatBuffer mViewBuffer = ByteBuffer.allocateDirect(16*4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();

    private final FloatBuffer mViewBuffer3D = ByteBuffer.allocateDirect(16*4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer();

    public static Director getInstance() {
        return sInstance;
    }

    public void init(Context context, int width, int height) {
        resetOrtho(width, height);
        resetPerspective(width, height);
        resetView3D();

        mImageLoader = new ImageLoader(context);
        mTextureManager = new TextureManager();
        mShaderLoader = new ShaderLoader(context);
        mDevice = new Device(context);
        mFontLoader = new FontLoader(context);

        mDefaultFont = Director.getInstance().getFontLoader().loadFonts("fonts", "yahei");

        mInit = true;
    }

    public void resetOrtho(int width, int height) {
        mOrthographicMatrix.identity();
        mOrthographicMatrix.ortho(0, width, 0, height, -1, 10);
    }

    public void resetPerspective(float width, float height) {
        mPerspectiveMatrix.identity();
        mPerspectiveMatrix.perspective((float) Math.toRadians(45),
                        width / height,
                        0.1f, 1000.0f);
    }

    public void resetView3D() {
        mCamera = new Camera(new Vector3f(0, 0.5f, 13.3f),
                new Vector3f(0, 0, -1),
                new Vector3f(0, 1, 0),
                0, 270, 0);
    }

    public FloatBuffer getOrthographicMatrix() {
        mOrthographicBuffer.position(0);
        mOrthographicMatrix.get(mOrthographicBuffer);
        mOrthographicBuffer.position(0);
        return mOrthographicBuffer;
    }

    public FloatBuffer getPerspectiveMatrix() {
        mPerspectiveBuffer.position(0);
        mPerspectiveMatrix.get(mPerspectiveBuffer);
        mPerspectiveBuffer.position(0);
        return mPerspectiveBuffer;
    }

    public FloatBuffer getViewMatrix() {
        mViewBuffer.position(0);
        mViewMatrix.get(mViewBuffer);
        mViewBuffer.position(0);
        return mViewBuffer;
    }

    public FloatBuffer getViewBuffer3D() {
        mViewBuffer3D.position(0);
        Matrix4f view = null;
        if (mLookAtOrigin) {
            view = mCamera.lookAtOrigin();
        } else {
            view = mCamera.lookAt();
        }
        view.get(mViewBuffer3D);
        mViewBuffer3D.position(0);
        return mViewBuffer3D;
    }

    public Matrix4f getViewMatrix3D() {
        if (mLookAtOrigin) {
            return mCamera.lookAtOrigin();
        }
        return mCamera.lookAt();
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public Image loadImageFromAssets(String path) {
        return mImageLoader.loadFromAssets(path);
    }

    public Image loadImageFromInternalStorage(String path) {
        return mImageLoader.loadFromInternalStorage(path, false, new Size(1,1), 0);
    }

    public TextureManager getTextureManager() {
        return mTextureManager;
    }

    public Texture findTexture(String path) {
        return mTextureManager.findTexture(path);
    }

    public Device getDevice() {
        return mDevice;
    }

    public ShaderLoader getShaderLoader() {
        return mShaderLoader;
    }

    public FontLoader getFontLoader() {
        return mFontLoader;
    }

    public String loaderShaderFromAssets(String path) {
        return mShaderLoader.loadShaderSourceFromAssets(path);
    }

    public Camera getCamera() {
        return mCamera;
    }

    public void setLookAtOrigin(boolean origin) {
        mLookAtOrigin = origin;
    }

    public Font getDefaultFont() {
        return mDefaultFont;
    }

    public void dispose() {
        mDefaultFont.texture.dispose();
        mDefaultFont = null;
    }
}
