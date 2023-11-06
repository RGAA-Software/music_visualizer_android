package com.shark.dynamics.audio.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SLoopThread <T extends SLoopTask> extends Thread {

    private int mMaxTask = Integer.MAX_VALUE;
    private long mDelayMS = -1;
    private BlockingQueue<T> mTasks = new LinkedBlockingQueue<>();

    public SLoopThread(String name) {
        super(name);
    }

    public SLoopThread(String name, int maxTask) {
        super(name);
        mMaxTask = maxTask;
    }

    public SLoopThread(String name, int maxTask, long delayByMS) {
        this(name, maxTask);
        mDelayMS = delayByMS;
    }

    @Override
    public void run() {
        while (!interrupted()) {
            try {
                T task = mTasks.take();
                task.exec();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (mDelayMS != -1) {
                try {
                    Thread.sleep(mDelayMS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void offerTask(T task) {
        // more than max cache, take the head away.
        if (mTasks.size() > mMaxTask) {
            try {
                mTasks.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mTasks.offer(task);
    }
}
