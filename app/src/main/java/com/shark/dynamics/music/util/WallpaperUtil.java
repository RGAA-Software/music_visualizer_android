package com.shark.dynamics.music.util;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.shark.dynamics.music.wallpaper.DynamicWallpaper;

import java.io.IOException;

public class WallpaperUtil {

    public static void clearWallpaper(Context context) {
        WallpaperManager mgr = WallpaperManager.getInstance(context);
        try {
            mgr.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startDynamicWallpaper(Context context, Class<?> service) {
        final Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(context, service));
        context.startActivity(intent);
    }

}
