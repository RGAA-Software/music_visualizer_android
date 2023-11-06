package com.shark.dynamics.graphics.renderer.texture;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.GLUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_RGB;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES30.GL_TEXTURE_WRAP_R;
import static android.opengl.GLES30.glBindVertexArray;

public class CubeTexture {

    private int mTextureId;

    public CubeTexture(String folderPath) {
        String[] names = new String[] {
                "right.jpg", "left.jpg",
                "top.jpg", "bottom.jpg",
                "front.jpg", "back.jpg",
        };

        List<Image> mImages = new ArrayList<>();
        for (String name : names) {
            String path = folderPath + "/" + name;
            Image image = Director.getInstance().getImageLoader().loadFromAssets(path, false);
            mImages.add(image);
        }

        mTextureId = GLUtil.genTexture();
        glBindTexture(GL_TEXTURE_CUBE_MAP, mTextureId);

        for (int i = 0; i < mImages.size(); i++) {
            Image image = mImages.get(i);
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                    0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image.getData());
        }

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
    }

    public int getTextureId() {
        return mTextureId;
    }

    public void active(Shader shader, int unit) {
        glActiveTexture(GL_TEXTURE0+unit);
        glBindTexture(GL_TEXTURE_CUBE_MAP, mTextureId);
        shader.setUniformInt("cubeImage", unit);
    }

    public void dispose() {
        glDeleteTextures(1, new int[]{mTextureId}, 0);
    }

}
