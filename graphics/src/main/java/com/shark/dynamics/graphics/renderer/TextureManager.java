package com.shark.dynamics.graphics.renderer;

import android.content.Context;
import android.text.TextUtils;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.texture.Image;
import com.shark.dynamics.graphics.renderer.texture.ImageLoader;
import com.shark.dynamics.graphics.renderer.texture.Texture;

import java.util.HashMap;
import java.util.Map;

public class TextureManager {

    private Map<String, Texture> mCachedTextures;

    public TextureManager() {
        mCachedTextures = new HashMap<>();
    }

    public Texture findTexture(String path) {
        String findKey = null;
        Texture findTexture = null;
        for (String key : mCachedTextures.keySet()) {
            if (TextUtils.equals(key, path)) {
                findKey = key;
                findTexture = mCachedTextures.get(key);
                break;
            }
        }

        if (findTexture != null && !findTexture.isDisposed()) {
            return findTexture;
        }

        if (findTexture != null) {
            mCachedTextures.remove(findKey);
        }

        ImageLoader loader = Director.getInstance().getImageLoader();
        Image image = loader.loadFromAssets(path);
        if (image == null) {
            return null;
        }
        Texture texture = new Texture(image);

        mCachedTextures.put(path, texture);

        return texture;
    }

    public void dispose() {
        for (String key : mCachedTextures.keySet()) {
            Texture texture = mCachedTextures.get(key);
            if (texture != null) {
                texture.dispose();
            }
        }
    }

    public void clear() {
        mCachedTextures.clear();
    }

}
