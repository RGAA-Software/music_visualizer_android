package com.shark.dynamics.music;

import android.content.Context;

import com.shark.dynamics.basic.assets.AssetsUtil;
import com.shark.dynamics.basic.file.FileUtil;
import com.shark.dynamics.basic.thread.Worker;

import java.io.File;

public class Prepare {

    private static Prepare sInstance = new Prepare();

    public static Prepare getInstance() {
        return sInstance;
    }

    public void prepare(final Context context) {
        Worker.getInstance().postLightTask(new Runnable() {
            @Override
            public void run() {

                String[] copyFiles = new String[] {
                        "models/deer/deer_r.obj",

                        "models/earth/earth.obj",
                        "models/earth/earth.mtl",
                        "models/earth/4096_earth.jpg",

                        "models/rock/rock.obj",
                        "models/rock/rock.mtl",
                        "models/rock/Rock-Texture-Surface.jpg",

                        "models/tree/tree.obj",
                        "models/tree/tree1.obj",
                };

                for (String srcPath : copyFiles) {
                    File dstFile = new File(context.getCacheDir(), FileUtil.getFileName(srcPath));
                    if (!dstFile.exists()) {
                        AssetsUtil.copyData(context, srcPath, dstFile.getAbsolutePath());
                    }
                }
            }
        });
    }

}
