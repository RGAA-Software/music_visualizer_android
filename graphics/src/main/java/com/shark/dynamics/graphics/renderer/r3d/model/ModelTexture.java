package com.shark.dynamics.graphics.renderer.r3d.model;

import com.shark.dynamics.graphics.renderer.texture.Texture;

public class ModelTexture {

    public ModelTexture(Texture tex, ModelTextureType type) {
        texture = tex;
        this.type = type;
    }

    public Texture texture;
    public ModelTextureType type;
}
