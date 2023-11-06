package com.shark.dynamics.music;

import android.app.Application;

import com.shark.dynamics.basic.thread.UI;
import com.shark.dynamics.basic.thread.Worker;

public class SharkApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Worker.getInstance().init();
        UI.getInstance().init();

        Prepare.getInstance().prepare(this);
    }
}
