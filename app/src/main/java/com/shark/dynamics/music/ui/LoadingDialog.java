package com.shark.dynamics.music.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.shark.dynamics.graphics.SkEGLConfigChooser;
import com.shark.dynamics.music.effect.EffectItem;
import com.shark.dynamics.music.effect.EffectRender;

/**
 * TEST ...
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context) {
        super(context);
        Point point = new Point();
        context.getDisplay().getRealSize(point);
        int width = point.x/2;
        int height = (int) (point.y/3.8f);

        GLSurfaceView surfaceView = new GLSurfaceView(context);
        surfaceView.setEGLContextClientVersion(3);
        surfaceView.setEGLConfigChooser(new SkEGLConfigChooser());
        EffectRender mRender = new EffectRender(context);
        EffectItem item = new EffectItem();
        item.clazz = "com.shark.dynamics.graphics.renderer.visualizer3D.GLShaderIt";
        mRender.updateSize(width, height);
        mRender.setEffectItem(item);
        surfaceView.setRenderer(mRender);

        setContentView(surfaceView);


        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = width;
            params.height = height;
        }

    }
}
