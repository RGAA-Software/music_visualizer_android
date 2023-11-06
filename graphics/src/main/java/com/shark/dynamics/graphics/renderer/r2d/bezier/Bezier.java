package com.shark.dynamics.graphics.renderer.r2d.bezier;

import com.shark.dynamics.graphics.renderer.DefaultShader;
import com.shark.dynamics.graphics.renderer.r2d.I2DRenderer;
import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINE_STRIP;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;

public class Bezier extends I2DRenderer {

    public static enum BezierOrder {
        k2Order, k3Order, kNOrder
    }

    private List<Vector2f> mPoints = new ArrayList<>();
    private int mVertexArrayHandle;
    private float[] mVertexArray;
    private FloatBuffer mVertexBuffer;
    private int mLineItemCount = 6;

    private BezierOrder mOrder = BezierOrder.k2Order;
    private float mBezierTStep = 0.02f;
    private Vector3f mControlPointColor = new Vector3f(1.f,1.f,1.f);

    public Bezier(Vector2f p0, Vector2f p1, Vector2f p2) {
        mPoints.add(p0);
        mPoints.add(p1);
        mPoints.add(p2);
        mOrder = BezierOrder.k2Order;
        init();
    }

    public Bezier(Vector2f p0, Vector2f p1, Vector2f p2, Vector2f p3) {
        this(p0, p1, p2, p3, 0.1f);
    }

    public Bezier(Vector2f p0, Vector2f p1, Vector2f p2, Vector2f p3, float step) {
        mPoints.add(p0);
        mPoints.add(p1);
        mPoints.add(p2);
        mPoints.add(p3);
        mBezierTStep = step;
        mOrder = BezierOrder.k3Order;
        init();
    }

    public Bezier(List<Vector2f> points) {
        mPoints.addAll(points);
        mOrder = BezierOrder.kNOrder;
        init();
    }

    private void init() {
        mShader = new Shader(DefaultShader.sDefaultVertexShader, DefaultShader.sDefaultFragmentShader);
        initRenderer();

        updatePoints(mPoints);

        mVertexBuffer = BufferUtil.createFloatBuffer(mVertexArray.length, mVertexArray);

        mVertexArrayHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_DYNAMIC_DRAW);

        int stride = 6*4;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3*4);
        glEnableVertexAttribArray(1);

        glLineWidth(3);

        glBindVertexArray(0);
    }

    public void updatePoints(Vector2f p0, Vector2f p1, Vector2f p2) {
        updatePoints(p0, p1, p2, null);
    }

    public void updatePoints(Vector2f p0, Vector2f p1, Vector2f p2, Vector2f p3) {
        List<Vector2f> bezierPoints = null;
        if (p3 == null) {
            bezierPoints = BezierPointGenerator.gen2Bezier(p0, p1, p2, mBezierTStep);
        } else if (mPoints.size() == 4) {
            bezierPoints = BezierPointGenerator.gen3Bezier(p0, p1, p2, p3, mBezierTStep);
        }

        mLineItemCount = 6;

        for (int i = 0 ; i < bezierPoints.size(); i++) {
            mVertexArray[i*mLineItemCount + 0] = bezierPoints.get(i).x;
            mVertexArray[i*mLineItemCount + 1] = bezierPoints.get(i).y;
            mVertexArray[i*mLineItemCount + 2] = 0;

            mVertexArray[i*mLineItemCount + 3] = 1;
            mVertexArray[i*mLineItemCount + 4] = 1;
            mVertexArray[i*mLineItemCount + 5] = 1;
        }

        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexArray);
        mVertexBuffer.position(0);

    }

    public void updatePoints(List<Vector2f> points) {
        List<Vector2f> bezierPoints = null;
        if (mOrder == BezierOrder.k2Order) {
            bezierPoints = BezierPointGenerator.gen2Bezier(points.get(0), points.get(1), points.get(2), mBezierTStep);
        } else if (mOrder == BezierOrder.k3Order) {
            bezierPoints = BezierPointGenerator.gen3Bezier(points.get(0), points.get(1), points.get(2), points.get(3), mBezierTStep);
        } else if (mOrder == BezierOrder.kNOrder) {
            bezierPoints = BezierPointGenerator.genNBezier(points, 0.02f);
        }

        mLineItemCount = 6;
        if (mVertexArray == null) {
            mVertexArray = new float[bezierPoints.size() * mLineItemCount + mPoints.size() * mLineItemCount];
        }
        int idx = 0 ;
        for (int i = 0 ; i < bezierPoints.size(); i++) {
            mVertexArray[i*mLineItemCount + 0] = bezierPoints.get(i).x;
            mVertexArray[i*mLineItemCount + 1] = bezierPoints.get(i).y;
            mVertexArray[i*mLineItemCount + 2] = 0;

            mVertexArray[i*mLineItemCount + 3] = 1;
            mVertexArray[i*mLineItemCount + 4] = 1;
            mVertexArray[i*mLineItemCount + 5] = 1;
            idx++;
        }

        for (int i = 0; i < mPoints.size(); i++) {
            int pIdx = idx + i;
            mVertexArray[pIdx * mLineItemCount + 0] = mPoints.get(i).x;
            mVertexArray[pIdx * mLineItemCount + 1] = mPoints.get(i).y;
            mVertexArray[pIdx * mLineItemCount + 2] = 0;

            mVertexArray[pIdx * mLineItemCount + 3] = mControlPointColor.x;
            mVertexArray[pIdx * mLineItemCount + 4] = mControlPointColor.y;
            mVertexArray[pIdx * mLineItemCount + 5] = mControlPointColor.z;
        }

    }


    @Override
    public void render(float delta) {
        super.render(delta);

        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexArray);
        mVertexBuffer.position(0);

        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_DYNAMIC_DRAW);
        int count = mVertexArray.length/mLineItemCount - mPoints.size();
        glDrawArrays(GL_LINE_STRIP, 0, count);

        glDrawArrays(GL_POINTS, count+1, mPoints.size()-2);
    }
}
