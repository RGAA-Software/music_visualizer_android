package com.shark.dynamics.graphics.renderer.r2d;

import com.shark.dynamics.graphics.renderer.ProjectionType;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import org.joml.Vector2f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;

public class Lines extends I2DRenderer {

    private int mLineWidth = 2;

    private FloatBuffer mVertexBuffer;
    private int mVertexArrayHandle;
    private float[] mVertexArray;
    private int mLineSize;

    public Lines(Vector2f start, Vector2f end) {
        List<Vector2f> points = new ArrayList<>();
        points.add(start);
        points.add(end);
        init(points);
    }

    public Lines(List<Vector2f> points) {
        init(points);
    }

    private void init(List<Vector2f> points) {
        super.initRenderer();
        mType = ProjectionType.kProj2D;

        mWidth = 100;
        mHeight = 2;
        mOriginWidth = mWidth;
        mOriginHeight = mHeight;

        mLineSize = points.size()/2;

        mVertexArray = new float[mLineSize*2*6];
        mVertexBuffer = BufferUtil.createFloatBuffer(mVertexArray.length, mVertexArray);

        updatePoints(points);

        mVertexArrayHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_STATIC_DRAW);

        int stride = 6 * 4;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3*4);
        glEnableVertexAttribArray(1);

        glLineWidth(2);

        glBindVertexArray(0);
    }

    public void setLineWidth(int width) {
        mLineWidth = width;
    }

    public void updatePoints(List<Vector2f> points) {
        if (mVertexBuffer == null
                || mVertexArray == null) {
            return;
        }

        for (int i = 0; i < points.size(); i+=2) {
            Vector2f start = points.get(i);
            Vector2f end = points.get(i+1);

            mVertexArray[i*6] = start.x;
            mVertexArray[i*6+1] = start.y;
            mVertexArray[i*6+2] = 0;

            mVertexArray[i*6+3] = 1.0f;
            mVertexArray[i*6+4] = 1.0f;
            mVertexArray[i*6+5] = 1.0f;

            mVertexArray[(i+1)*6] = end.x;
            mVertexArray[(i+1)*6+1] = end.y;
            mVertexArray[(i+1)*6+2] = 0;

            mVertexArray[(i+1)*6+3] = 1.0f;
            mVertexArray[(i+1)*6+4] = 1.0f;
            mVertexArray[(i+1)*6+5] = 1.0f;

        }

        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexArray);
        mVertexBuffer.position(0);
    }

    public void updatePoint(Vector2f start, Vector2f end) {
        mVertexArray[0] = start.x;
        mVertexArray[1] = start.y;
        mVertexArray[6*1 + 0] = end.x;
        mVertexArray[6*1 + 1] = end.y;

        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexArray);
        mVertexBuffer.position(0);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_STATIC_DRAW);

        glLineWidth(mLineWidth);
        for (int i = 0; i < mLineSize; i++) {
            glDrawArrays(GL_LINES, i*2, 2);
        }
    }
}
