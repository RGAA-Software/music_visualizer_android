package com.shark.dynamics.graphics.renderer.texture;

import java.nio.ByteBuffer;

public class Image {

    private int mWidth;
    private int mHeight;
    private int mChannels;
    private ByteBuffer mData;

    public Image(int width, int height, int channels, ByteBuffer data) {
        mWidth = width;
        mHeight = height;
        mChannels = channels;
        mData = data;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getChannels() {
        return mChannels;
    }

    public ByteBuffer getData() {
        return mData;
    }

}
