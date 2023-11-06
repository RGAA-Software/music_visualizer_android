package com.shark.dynamics.basic.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;


public class UI {

    private static UI sUI = new UI();

    private boolean mInit = false;
    private Handler mHandler;

    private UI() {

    }

    public static UI getInstance() {
        return sUI;
    }

    public void init() {
        if (mInit) {
            return;
        }
        mHandler = new Handler(Looper.getMainLooper());
        mInit = true;
    }

    public void post(Runnable task) {
        mHandler.post(task);
    }

}
