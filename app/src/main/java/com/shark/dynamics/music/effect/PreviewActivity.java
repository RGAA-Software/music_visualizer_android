package com.shark.dynamics.music.effect;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.shark.dynamics.audio.AudioFFTMonitor;
import com.shark.dynamics.graphics.SkEGLConfigChooser;
import com.shark.dynamics.music.R;

public class PreviewActivity extends Activity {

    private GLSurfaceView mGLSurfaceView;
    private EffectRender mRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        window.setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_preview);

        mGLSurfaceView = findViewById(R.id.id_gl_surface_view);

        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager
                .getDeviceConfigurationInfo();

        final boolean supportsEs3 =
                configurationInfo.reqGlEsVersion >= 0x30000;

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        EffectItem item = (EffectItem) intent.getSerializableExtra("item");

        if (supportsEs3) {
            mGLSurfaceView.setEGLContextClientVersion(3);
            mGLSurfaceView.setEGLConfigChooser(new SkEGLConfigChooser());
            mRender = new EffectRender(this);
            Point point = new Point();
            getDisplay().getRealSize(point);
            mRender.updateSize(point.x, point.y);
            mRender.setEffectItem(item);
            mGLSurfaceView.setRenderer(mRender);
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 3.0.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
        mRender.onResume();
        AudioFFTMonitor.getInstance().setFFTCallback((mc, sgs, wa) -> {
            if (mRender != null) {
                mRender.updateMCArray(mc);
                mRender.updateSGSArray(sgs);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
        mRender.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRender.onDestroy();
    }
}