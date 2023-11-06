package com.shark.dynamics.graphics.renderer.r3d.model;

import android.util.Log;

import com.shark.dynamics.basic.file.FileUtil;
import com.shark.dynamics.graphics.renderer.texture.Texture;
import com.shark.dynamics.graphics.shader.Shader;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import assimp.ASSIMP;
import assimp.AiMaterial;
import assimp.AiMesh;
import assimp.AiPostProcessStep;
import assimp.AiPostProcessStepKt;
import assimp.AiScene;
import assimp.AiTexture;
import assimp.DefaultIOSystem;
import assimp.IOSystem;
import assimp.Importer;
import assimp.SettingsKt;
import glm_.vec3.Vec3;


public class ModelLoader {

    private static final String TAG = "Model";

    public static Model loadModel(String path) {
        return loadModel(path, null, null);
    }

    public static Model loadModel(String path, Shader shader) {
        return loadModel(path, shader, null);
    }

    public static Model loadModel(String path, Shader shader, Matrix4f[] models) {
        Log.i(TAG, "path : " + path);
        int triangle = AiPostProcessStep.Triangulate.i;
        SettingsKt.setASSIMP_LOAD_TEXTURES(false);
        AiScene scene = new Importer().readFile(path, triangle);
        Model model = null;
        if (shader == null) {
            model = new Model();
        } else {
            model = new Model(shader);
        }

        String basePath = FileUtil.getPrefix(path);

        Log.i(TAG, "materials : " + scene.getMaterials().size());
        Log.i(TAG, "meshs : " + scene.getMeshes().size());

        List<Mesh> parsedMeshes = new ArrayList<>();
        List<AiMesh> meshes = scene.getMeshes();
        for (int i = 0; i < meshes.size(); i++) {
            AiMesh aiMesh = meshes.get(i);
            int mtIdx = aiMesh.getMaterialIndex();
            Log.i(TAG, "material index : " + mtIdx);
            Mesh mesh = processMesh(basePath, scene, aiMesh, mtIdx, shader == null ? model.getShader() : shader, models);
            parsedMeshes.add(mesh);
        }

        model.meshes = parsedMeshes;
        return model;
    }


    private static Mesh processMesh(String basePath, AiScene scene, AiMesh aiMesh, int mtIdx, Shader shader, Matrix4f[] models) {
        Mesh mesh;

        List<Vertex> vertexList = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> texCoords = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector3f> tangents = new ArrayList<>();
        List<Vector3f> bitangents = new ArrayList<>();

        List<Vec3> verts = aiMesh.getVertices();
        for (int i = 0; i < verts.size(); i++) {
            Vec3 v = verts.get(i);
            vertices.add(new Vector3f(v.getX(), v.getY(), v.getZ()));
        }
        Log.i(TAG, "vertices size : " + vertices.size());

        List<List<float[]>> coords = aiMesh.getTextureCoords();
        if (coords.size() >= 1) {
            List<float[]> cds = coords.get(0);
            for (int i = 0; i < cds.size(); i++) {
                float[] cd = cds.get(i);
                texCoords.add(new Vector2f(cd[0], cd[1]));
            }
        }

        Log.i(TAG, "texCoord size : " + texCoords.size());

//        AIVector3D.Buffer aiTexCoords = aiMesh.getTextureCoords().get(0);
//        if (aiTexCoords != null){
//            while (aiTexCoords.remaining() > 0) {
//                AIVector3D aiTexCoord = aiTexCoords.get();
//                texCoords.add(new Vector2f(aiTexCoord.x(),aiTexCoord.y()));
//            }
//        }

        List<Vec3> norms = aiMesh.getNormals();
        for (int i = 0; i < norms.size(); i++) {
            Vec3 n = norms.get(i);
            normals.add(new Vector3f(n.getX(), n.getY(), n.getZ()));
        }

        Log.i(TAG, "normal size : " + normals.size());


//        AIVector3D.Buffer aiTangents = aiMesh.mTangents();
//        if (aiTangents != null){
//            while (aiTangents.remaining() > 0) {
//                AIVector3D aiTangent = aiTangents.get();
//                tangents.add(new Vector3f(aiTangent.x(),aiTangent.y(),aiTangent.z()));
//            }
//        }
//
//        AIVector3D.Buffer aiBitangents = aiMesh.mBitangents();
//        if (aiBitangents != null){
//            while (aiBitangents.remaining() > 0) {
//                AIVector3D aiBitangent = aiBitangents.get();
//                bitangents.add(new Vector3f(aiBitangent.x(),aiBitangent.y(),aiBitangent.z()));
//            }
//        }


        List<List<Integer>> faces = aiMesh.getFaces();
        for (List<Integer> face : faces) {
            indices.add(face.get(0));
            indices.add(face.get(1));
            indices.add(face.get(2));
        }

        Log.i(TAG, "faces size : " + faces.size());

        for(int i=0; i<vertices.size(); i++){
            Vertex vertex = new Vertex();
            vertex.position = vertices.get(i);
            if (!normals.isEmpty()){
                vertex.normal = normals.get(i);
            } else{
                vertex.normal = new Vector3f(0,0,0);
            }

            if (!texCoords.isEmpty()){
                vertex.texCoords = texCoords.get(i);
            } else{
                vertex.texCoords = new Vector2f(0,0);
            }

            if (!tangents.isEmpty()){
                vertex.tangent = tangents.get(i);
            }

            if (!bitangents.isEmpty()){
                vertex.bitangent = bitangents.get(i);
            }
            vertexList.add(vertex);
        }

        Material material = processMaterial(basePath, scene.getMaterials().get(mtIdx));

        mesh = new Mesh(vertexList, indices, material, shader, models);

        return mesh;
    }


    private static Material processMaterial(String basePath, AiMaterial aiMaterial) {
        List<AiMaterial.Texture> textures = aiMaterial.getTextures();
        Log.i(TAG, "texture size : " + textures.size());
        String path = basePath + "/";
        boolean find = false;
        for (AiMaterial.Texture t : textures) {
            AiTexture.Type type = t.component1();
            if (type == AiTexture.Type.diffuse) {
                Log.i(TAG, "diffuse path : " + t.getFile());
                path = path + t.getFile();
                find = true;
                break;
            }
        }
        if (!find) {
            return null;
        }

        Material material = new Material();
        material.textures = new ArrayList<>();

        ModelTexture tex = new ModelTexture(new Texture(path, false), ModelTextureType.kDiffuse);
        material.textures.add(tex);

        return material;
    }


}
