package com.shark.dynamics.audio.thread;

public class SingleThread <T extends SingleTask> extends Thread {

    private T mSingleTask;

    public SingleThread(T task) {
        mSingleTask = task;
    }

    public SingleThread(String name, T task) {
        super(name);
        mSingleTask = task;
    }

    @Override
    public void run() {
        if (mSingleTask != null) {
            mSingleTask.exec();
        }
    }
}
