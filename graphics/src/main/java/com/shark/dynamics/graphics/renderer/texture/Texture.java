package com.shark.dynamics.graphics.renderer.texture;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.shader.Shader;

import static android.opengl.GLES32.*;

import java.nio.IntBuffer;

public class Texture {

    private int mTextureId = 0;
    private boolean mDisposed = false;
    private int mWidth;
    private int mHeight;

    public Texture(String path) {
        this(path, true);
    }

    public Texture(String path, boolean assets) {
        ImageLoader imageLoader = Director.getInstance().getImageLoader();
        Image image = null;
        if (assets) {
            image = imageLoader.loadFromAssets(path);
        } else {
            image = imageLoader.loadFromInternalStorage(path);
        }
        initWithImage(image);
    }

    public Texture(Image image) {
        initWithImage(image);
    }

    public Texture(int texId, int width, int height) {
        mTextureId = texId;
        mWidth = width;
        mHeight = height;
    }

    private void initWithImage(Image image) {
        mWidth = image.getWidth();
        mHeight = image.getHeight();

        IntBuffer buffer = IntBuffer.allocate(1);
        glGenTextures(1, buffer);
        buffer.position(0);
        mTextureId = buffer.get(0);

        glBindTexture(GL_TEXTURE_2D, mTextureId);
        glTexImage2D(GL_TEXTURE_2D,
                0,
                GL_RGBA,
                image.getWidth(),
                image.getHeight(),
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                image.getData());

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void active(Shader shader, int id) {
        glActiveTexture(GL_TEXTURE0+id);
        glBindTexture(GL_TEXTURE_2D, mTextureId);
        shader.setUniformInt("image", id);
    }

    public int getTextureId() {
        return mTextureId;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public boolean isDisposed() {
        return mDisposed;
    }

    public void dispose() {
        glDeleteTextures(1, new int[]{mTextureId}, 0);
        mDisposed = true;
    }

}
