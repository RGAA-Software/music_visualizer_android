package com.shark.dynamics.graphics.renderer.r3d.model;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.r3d.I3DRenderer;
import com.shark.dynamics.graphics.shader.Shader;

import org.joml.Vector3f;

import java.util.List;

import static android.opengl.GLES30.glBindVertexArray;

public class Model extends I3DRenderer {

    public List<Mesh> meshes;

    public Model() {
        String vs = Director.getInstance().loaderShaderFromAssets("shader/3d/base_vs.glsl");
        String fs = Director.getInstance().loaderShaderFromAssets("shader/3d/uniform_color_light_fs.glsl");
        mShader = new Shader(vs, fs);
        mShader.use();
        initRenderer();
    }

    public Model(Shader shader) {
        mShader = shader;
        mShader.use();
        initRenderer();
    }

    public void render(float delta) {
        super.render(delta);
        mShader.setUniformVec3("uColor", 1.0f, 1.0f, 1.0f);

        for (Mesh mesh : meshes) {
            mesh.render(delta);
        }
        glBindVertexArray(0);
    }

    @Override
    public void renderShadow(float delta) {
        super.renderShadow(delta);
        for (Mesh mesh : meshes) {
            mesh.render(delta);
        }
        glBindVertexArray(0);
    }
}
