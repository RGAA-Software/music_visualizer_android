package com.shark.dynamics.graphics.renderer.r3d.model;


import com.shark.dynamics.graphics.renderer.r3d.I3DRenderer;
import com.shark.dynamics.graphics.renderer.texture.Texture;
import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;
import com.shark.dynamics.graphics.util.MatrixUtil;

import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glDrawElementsInstanced;
import static android.opengl.GLES30.glVertexAttribDivisor;


public class Mesh {

    public List<Vertex> vertices;
    public List<Integer> indices;
    public Material material;

    private Shader mShader;

    private int VBO;
    private int EBO;

    private Matrix4f[] mModels;

    private int mInstanceModelArrayHandle;
    private float[] mInstanceModelArray;
    private FloatBuffer mInstanceModelBuffer;

    public Mesh(List<Vertex> vs, List<Integer> is, Material ts, Shader shader, Matrix4f[] models) {
        mShader = shader;
        vertices = vs;
        indices = is;
        material = ts;
        mModels = models;

        setupData();
    }

    private void setupData() {
        VBO = GLUtil.glGenBuffer();
        EBO = GLUtil.glGenBuffer();

        glBindBuffer(GL_ARRAY_BUFFER, VBO);

        float[] verticles = new float[vertices.size() * (3 + 3 + 2)];
        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);
            verticles[i * 8 + 0] = vertex.position.x;
            verticles[i * 8 + 1] = vertex.position.y;
            verticles[i * 8 + 2] = vertex.position.z;

            verticles[i * 8 + 3] = vertex.normal.x;
            verticles[i * 8 + 4] = vertex.normal.y;
            verticles[i * 8 + 5] = vertex.normal.z;

            verticles[i * 8 + 6] = vertex.texCoords.x;
            verticles[i * 8 + 7] = vertex.texCoords.y;
        }
        FloatBuffer verticesBuffer = BufferUtil.createFloatBuffer(verticles);
        glBufferData(GL_ARRAY_BUFFER, verticles.length*4, verticesBuffer, GL_STATIC_DRAW);

        int[] idxArray = new int[indices.size()];
        int index = 0;
        for (Integer idx : indices) {
            idxArray[index++] = idx;
        }

        IntBuffer indicesBuffer = BufferUtil.createIntBuffer(idxArray.length, idxArray);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, idxArray.length*4, indicesBuffer, GL_STATIC_DRAW);

        int posLoc = 0;//mShader.getAttribLocation("aPos");
        int normalLoc = 3;//mShader.getAttribLocation("aNormal");
        int texLoc = 2;//mShader.getAttribLocation("aTex");

        // 顶点位置
        glEnableVertexAttribArray(posLoc);
        glVertexAttribPointer(posLoc, 3, GL_FLOAT, false, 8 * 4, 0);
        // 顶点法线
        glEnableVertexAttribArray(normalLoc);
        glVertexAttribPointer(normalLoc, 3, GL_FLOAT, false, 8*4, 3*4);
        // 顶点纹理坐标
        glEnableVertexAttribArray(texLoc);
        glVertexAttribPointer(texLoc, 2, GL_FLOAT, false, 8*4, 6*4);

        if (mModels != null) {
            mInstanceModelArray = new float[mModels.length * 16];

            mInstanceModelBuffer = ByteBuffer
                    .allocateDirect(mModels.length * 16 * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();

            MatrixUtil.matrixArrayToFloatArray(mModels, mInstanceModelArray);

            mInstanceModelBuffer.put(mInstanceModelArray);
            mInstanceModelBuffer.position(0);

            mInstanceModelArrayHandle = GLUtil.glGenBuffer();
            glBindBuffer(GL_ARRAY_BUFFER, mInstanceModelArrayHandle);
            glBufferData(GL_ARRAY_BUFFER, mModels.length * 16 * 4, mInstanceModelBuffer, GL_DYNAMIC_DRAW);

            int vec4Size = 4 * 4;
            glEnableVertexAttribArray(4);
            glVertexAttribPointer(4, 4, GL_FLOAT, false, 4 * vec4Size, 0);
            glEnableVertexAttribArray(5);
            glVertexAttribPointer(5, 4, GL_FLOAT, false, 4 * vec4Size, (vec4Size));
            glEnableVertexAttribArray(6);
            glVertexAttribPointer(6, 4, GL_FLOAT, false, 4 * vec4Size, (2 * vec4Size));
            glEnableVertexAttribArray(7);
            glVertexAttribPointer(7, 4, GL_FLOAT, false, 4 * vec4Size, (3 * vec4Size));

            glVertexAttribDivisor(4, 1);
            glVertexAttribDivisor(5, 1);
            glVertexAttribDivisor(6, 1);
            glVertexAttribDivisor(7, 1);
        }
    }


    public void render(float delta) {
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);

        if (material != null && material.textures != null) {
            List<ModelTexture> textures = material.textures;
            for (int i = 0; i < textures.size(); i++) {
                ModelTexture mt = textures.get(i);
                int unit = 1 + i;
                mt.texture.active(mShader, unit);

                ModelTextureType type = mt.type;
                if (type == ModelTextureType.kDiffuse) {
                    mShader.setUniformInt("diffuseImage", unit);
                } else if (type == ModelTextureType.kSpecular) {
                    mShader.setUniformInt("specularImage", unit);
                } else if (type == ModelTextureType.kNormal) {
                    mShader.setUniformInt("normalImage", unit);
                }
            }
        }

        if (mModels == null) {
            glDrawElements(GL_TRIANGLES, indices.size(), GL_UNSIGNED_INT, 0);
        } else {

            MatrixUtil.matrixArrayToFloatArray(mModels, mInstanceModelArray);

            mInstanceModelBuffer.put(mInstanceModelArray);
            mInstanceModelBuffer.position(0);

            glBindBuffer(GL_ARRAY_BUFFER, mInstanceModelArrayHandle);
            glBufferData(GL_ARRAY_BUFFER, mModels.length * 16 * 4, mInstanceModelBuffer, GL_DYNAMIC_DRAW);

            glDrawElementsInstanced(
                    GL_TRIANGLES, indices.size(), GL_UNSIGNED_INT, 0, mModels.length);
        }
    }


}
