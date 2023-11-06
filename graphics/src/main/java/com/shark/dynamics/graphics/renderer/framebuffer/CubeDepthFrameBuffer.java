//package com.shark.dynamics.graphics.renderer.framebuffer;
//
//import org.lwjgl.opengl.GL33;
//
//import java.nio.ByteBuffer;
//
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL30.*;
//
//
//public class CubeDepthFrameBuffer {
//
//    private int mFrameBufferId;
//    private int mTextureId;
//
//    public int getFrameBufferId() {
//        return mFrameBufferId;
//    }
//
//    public int getFrameBufferTexId() {
//        return mTextureId;
//    }
//
//    public void init(int width, int height) {
//        // configure depth map FBO
//        // -----------------------
//        mFrameBufferId = glGenFramebuffers();
//        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBufferId);
//        // create depth cubemap texture
//        mTextureId = glGenTextures();
//        glBindTexture(GL_TEXTURE_CUBE_MAP, mTextureId);
//        for (int i = 0; i < 6; ++i){
//            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_DEPTH_COMPONENT, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
//        }
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
//        // attach depth texture as FBO's depth buffer
//
//        GL33.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, mTextureId, 0);
//        glDrawBuffer(GL_NONE);
//        glReadBuffer(GL_NONE);
//        glBindFramebuffer(GL_FRAMEBUFFER, 0);
//    }
//
//    public void begin() {
//        glBindFramebuffer(GL_FRAMEBUFFER, mFrameBufferId);
//    }
//
//    public void end() {
//        glBindFramebuffer(GL_FRAMEBUFFER, 0);
//    }
//
//}
