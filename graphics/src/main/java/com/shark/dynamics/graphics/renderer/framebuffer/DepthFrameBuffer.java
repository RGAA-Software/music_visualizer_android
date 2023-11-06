package com.shark.dynamics.graphics.renderer.framebuffer;

import android.opengl.GLES30;
import android.opengl.GLES32;
import android.util.Log;

import com.shark.dynamics.graphics.util.GLUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_DEPTH_ATTACHMENT;
import static android.opengl.GLES20.GL_DEPTH_COMPONENT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_NONE;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCheckFramebufferStatus;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGetIntegerv;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameterfv;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES30.GL_FRAMEBUFFER_DEFAULT;
import static android.opengl.GLES32.GL_CLAMP_TO_BORDER;
import static android.opengl.GLES32.GL_TEXTURE_BORDER_COLOR;

public class DepthFrameBuffer {

    private static final String TAG = "Depth";

    private int mFrameBufferId;
    private int mTexId;
    private IntBuffer mDefFrameBufferId = IntBuffer.allocate(1);

    public int getFrameBufferId() {
        return mFrameBufferId;
    }

    public int getTexId() {
        return mTexId;
    }

    public void init(int width, int height) {
        int[] tia=new int[1];
        GLES30.glGenFramebuffers(1, tia, 0);
        mFrameBufferId=tia[0];

        int renderDepthBufferId;
        GLES30.glGenRenderbuffers(1, tia, 0);
        renderDepthBufferId=tia[0];
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, renderDepthBufferId);
        GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH_COMPONENT16, width, height);

        int[] tempIds = new int[1];
        GLES30.glGenTextures(1, tempIds, 0);

        mTexId=tempIds[0];

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTexId);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        float[] borderColor = new float[]{ 1.0f, 1.0f, 1.0f, 1.0f };
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor, 0);
        GLES30.glTexImage2D
                (
                    GLES30.GL_TEXTURE_2D,
                    0,
                    GLES30.GL_R16F,
                    width,
                    height,
                    0,
                    GLES30.GL_RED,
                    GLES30.GL_FLOAT,
                    null
                );

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBufferId);
        GLES30.glFramebufferTexture2D
                (
                    GLES30.GL_FRAMEBUFFER,
                    GLES30.GL_COLOR_ATTACHMENT0,
                    GLES30.GL_TEXTURE_2D,
                    mTexId,
                    0
                );
        GLES30.glFramebufferRenderbuffer
                (
                        GLES30.GL_FRAMEBUFFER,
                        GLES30.GL_DEPTH_ATTACHMENT,
                        GLES30.GL_RENDERBUFFER,
                        renderDepthBufferId
                );
    }

    public void begin() {
        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBufferId);
    }

    public void end() {
        mDefFrameBufferId.position(0);
        glGetIntegerv(GL_FRAMEBUFFER_DEFAULT, mDefFrameBufferId);
        mDefFrameBufferId.position(0);
        int id = mDefFrameBufferId.get(0);
        glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

}
