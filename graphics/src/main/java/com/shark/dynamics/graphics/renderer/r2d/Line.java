package com.shark.dynamics.graphics.renderer.r2d;

import com.shark.dynamics.graphics.renderer.ProjectionType;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_LINE_STRIP;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;

public class Line extends I2DRenderer {

    private int mLineWidth = 2;

    private FloatBuffer mVertexBuffer;
    private int mVertexArrayHandle;
    private float[] mVertexArray;
    private List<Vector2f> mPoints = new ArrayList<>();

    private Vector3f mColor;

    public Line(List<Vector2f> points, Vector3f color) {
        mColor = color;
        init(points);
    }

    private void init(List<Vector2f> points) {
        super.initRenderer();

        mPoints.addAll(points);

        mWidth = 100;
        mHeight = 2;
        mOriginWidth = mWidth;
        mOriginHeight = mHeight;

        int lineSize = points.size();

        mVertexArray = new float[lineSize * 6];
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

        mPoints.clear();
        mPoints.addAll(points);

        for (int i = 0; i < points.size(); i++) {
            Vector2f start = points.get(i);

            mVertexArray[i*6] = start.x;
            mVertexArray[i*6+1] = start.y;
            mVertexArray[i*6+2] = 0;

            mVertexArray[i*6+3] = mColor.x;
            mVertexArray[i*6+4] = mColor.y;
            mVertexArray[i*6+5] = mColor.z;
        }

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

        glDrawArrays(GL_LINE_STRIP, 0, mPoints.size());
    }
}
