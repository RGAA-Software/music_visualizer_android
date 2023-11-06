package com.shark.dynamics.graphics.util;

import android.opengl.Matrix;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;

public class MatrixUtil {


    private static final float[] mIdentity = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    public static float[] identityMatrix() {
        float[] matrix = new float[mIdentity.length];
        System.arraycopy(mIdentity, 0, matrix, 0, mIdentity.length);
        return matrix;
    }

    public static FloatBuffer matrixArrayToFloatBuffer(Matrix4f[] mats) {
        float[] array = new float[mats.length*16];
        matrixArrayToFloatArray(mats, array);
        return BufferUtil.createFloatBuffer(array);
    }

    public static void matrixArrayToFloatArray(Matrix4f[] mats, float[] array) {
        Vector4f dest = new Vector4f();
        for (int i = 0; i < mats.length; i++) {
            Matrix4f matrix = mats[i];
            matrix.getColumn(0, dest);
            array[i * 16 + 0] = dest.x;
            array[i * 16 + 1] = dest.y;
            array[i * 16 + 2] = dest.z;
            array[i * 16 + 3] = dest.w;

            matrix.getColumn(1, dest);
            array[i * 16 + 4] = dest.x;
            array[i * 16 + 5] = dest.y;
            array[i * 16 + 6] = dest.z;
            array[i * 16 + 7] = dest.w;

            matrix.getColumn(2, dest);
            array[i * 16 + 8] = dest.x;
            array[i * 16 + 9] = dest.y;
            array[i * 16 + 10] = dest.z;
            array[i * 16 + 11] = dest.w;

            matrix.getColumn(3, dest);
            array[i * 16 + 12] = dest.x;
            array[i * 16 + 13] = dest.y;
            array[i * 16 + 14] = dest.z;
            array[i * 16 + 15] = dest.w;
        }
    }

    public static void vectorArrayToFloatArray(Vector3f[] from, float[] to) {
        for (int i = 0; i < from.length; i++) {
            Vector3f c = from[i];
            to[i*3 + 0] = c.x;
            to[i*3 + 1] = c.y;
            to[i*3 + 2] = c.z;
        }
    }

}
