package com.shark.dynamics.basic.thread;

import android.os.Handler;
import android.os.HandlerThread;

public class Worker {

    private static Worker sInstance = new Worker();

    private boolean mInit = false;
    private Handler mLightHandler;

    private Worker() {

    }

    public static Worker getInstance() {
        return sInstance;
    }

    public void init() {
        if (mInit) {
            return;
        }
        HandlerThread lightThread = new HandlerThread("light");
        lightThread.start();
        mLightHandler = new Handler(lightThread.getLooper());
        mInit = true;
    }

    public void postLightTask(Runnable task) {
        mLightHandler.post(task);
    }

}
