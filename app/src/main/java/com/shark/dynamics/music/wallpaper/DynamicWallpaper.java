package com.shark.dynamics.music.wallpaper;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import com.shark.dynamics.audio.AudioFFTMonitor;
import com.shark.dynamics.audio.IFFTCallback;
import com.shark.dynamics.graphics.SkEGLConfigChooser;
import com.shark.dynamics.music.effect.EffectItem;
import com.shark.dynamics.music.effect.EffectRender;

public class DynamicWallpaper extends WallpaperService {

    private static final String TAG = "Render";

    private EffectRender mRender;

    @Override
    public Engine onCreateEngine() {
        return new WallpaperEngine();
    }

    public class WallpaperEngine extends Engine {

        private WallpaperSurfaceView mSurfaceView;

        @Override
        public SurfaceHolder getSurfaceHolder() {
            return super.getSurfaceHolder();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.i(TAG, "Engine onCreate : " + this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mSurfaceView.onWallpaperDestroy();
            // mRender.onDestroy();
            Log.i(TAG, "Engine onDestroy : " + this);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.i(TAG, "Engine onSurfaceChanged : " + this);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);

            EffectItem item = new EffectItem();
            item.clazz = "com.shark.dynamics.graphics.renderer.visualizer3D.GLSnowDeer";
            mSurfaceView = new WallpaperSurfaceView(DynamicWallpaper.this);
            mSurfaceView.setEGLConfigChooser(new SkEGLConfigChooser());
            mSurfaceView.setEGLContextClientVersion(3);
            mRender = new EffectRender(DynamicWallpaper.this);
            mRender.setEffectItem(item);
            mSurfaceView.setRenderer(mRender);

            Log.i(TAG, "Engine onSurfaceCreated : " + this);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            Log.i(TAG, "Engine onSurfaceDestroyed : " + this);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                mSurfaceView.onResume();
                mRender.onResume();

                AudioFFTMonitor.getInstance().setFFTCallback((mc, sgs, wa) -> {
                    if (mRender != null) {
                        mRender.updateMCArray(mc);
                        mRender.updateSGSArray(sgs);
                    }
                });

            } else {
                mSurfaceView.onPause();
                mRender.onPause();
            }
            Log.i(TAG, "Engine onVisibilityChanged : " + visible + " " + this + "\n render : " + mRender);
        }

        public class WallpaperSurfaceView extends GLSurfaceView {

            public WallpaperSurfaceView(Context context) {
                super(context);
            }

            public WallpaperSurfaceView(Context context, AttributeSet attrs) {
                super(context, attrs);
            }

            @Override
            public SurfaceHolder getHolder() {
                return WallpaperEngine.this.getSurfaceHolder();
            }

            public void onWallpaperDestroy() {
                super.onDetachedFromWindow();
            }
        }

    }

}
