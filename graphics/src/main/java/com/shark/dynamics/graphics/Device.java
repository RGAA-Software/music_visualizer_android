package com.shark.dynamics.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.WindowManager;

import org.joml.Vector2f;

public class Device {

    private Context mContext;

    public Device(Context context) {
        mContext = context;
    }

    public Vector2f getScreenRealSize() {
        Point point = new Point();
        mContext.getDisplay().getRealSize(point);
        return new Vector2f(point.x, point.y);
    }

    public Vector2f getRealCenter() {
        Vector2f size = getScreenRealSize();
        return new Vector2f(size.x/2, size.y/2);
    }

    public Vector2f getScreenUsableSize() {
        int w = Resources.getSystem().getDisplayMetrics().widthPixels;
        int h = Resources.getSystem().getDisplayMetrics().heightPixels;
        return new Vector2f(w, h);
    }

    public int getPixelSize(int dpSize) {
        return (int) (mContext.getResources().getDisplayMetrics().density * dpSize);
    }

    public String getCachePath() {
        return mContext.getCacheDir().getAbsolutePath();
    }
}
