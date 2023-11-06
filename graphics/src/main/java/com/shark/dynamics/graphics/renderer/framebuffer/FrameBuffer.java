package com.shark.dynamics.graphics.renderer.framebuffer;

import android.util.Log;

import com.shark.dynamics.graphics.util.GLUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_BINDING;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.GL_IMPLEMENTATION_COLOR_READ_FORMAT;
import static android.opengl.GLES20.GL_IMPLEMENTATION_COLOR_READ_TYPE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_RENDERBUFFER;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindRenderbuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCheckFramebufferStatus;
import static android.opengl.GLES20.glFramebufferRenderbuffer;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGetIntegerv;
import static android.opengl.GLES20.glRenderbufferStorage;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES30.GL_COLOR_ATTACHMENT1;
import static android.opengl.GLES30.GL_DEPTH24_STENCIL8;
import static android.opengl.GLES30.GL_DEPTH_STENCIL_ATTACHMENT;
import static android.opengl.GLES30.GL_FRAMEBUFFER_DEFAULT;
import static android.opengl.GLES30.glDrawBuffers;

public class FrameBuffer {

    private static final String TAG = "FrameBuffer";

    private int mFrameBufferId;
    private int mFrameBufferTexId;
    private int mFrameBufferTexId2;

    private int mWidth;
    private int mHeight;

    private IntBuffer mDefFrameBufferId = IntBuffer.allocate(1);

    public int getFrameBufferId() {
        return mFrameBufferId;
    }

    public int getFrameBufferTexId() {
        return mFrameBufferTexId;
    }

    public int getFrameBufferTexId2() {
        return mFrameBufferTexId2;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void init(int width, int height) {
        init(width, height, GL_RGBA);
    }

    public void init(int width, int height, int bufferType) {
        mWidth = width;
        mHeight = height;

        // framebuffer configuration
        // -------------------------
        mFrameBufferId = GLUtil.genFrameBuffer();
        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBufferId);
        // create a color attachment texture
        mFrameBufferTexId = GLUtil.genTexture();
        glBindTexture(GL_TEXTURE_2D, mFrameBufferTexId);
        glTexImage2D(GL_TEXTURE_2D, 0, bufferType, (int)width, (int)height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, mFrameBufferTexId, 0);

        mFrameBufferTexId2 = GLUtil.genTexture();
        glBindTexture(GL_TEXTURE_2D, mFrameBufferTexId2);
        glTexImage2D(GL_TEXTURE_2D, 0, bufferType, (int)width, (int)height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, mFrameBufferTexId2, 0);

        // create a renderbuffer object for depth and stencil attachment (we won't be sampling these)
        int rbo = GLUtil.genRenderObject();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, (int)width, (int)height); // use a single renderbuffer object for both a depth AND stencil buffer.
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo); // now actually attach it
        // now that we actually created the framebuffer and added all attachments we want to check if it is actually complete now
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            Log.i(TAG, "FrameBuffer Not complete.");
            return;
        }

        IntBuffer readType = IntBuffer.allocate(1);
        glGetIntegerv(GL_IMPLEMENTATION_COLOR_READ_TYPE, readType);
        readType.position(0);

        IntBuffer readFormat = IntBuffer.allocate(1);
        glGetIntegerv(GL_IMPLEMENTATION_COLOR_READ_FORMAT, readFormat);
        readFormat.position(0);

        int[] attachments = new int[]{ GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1 };
        glDrawBuffers(2, attachments, 0);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
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
