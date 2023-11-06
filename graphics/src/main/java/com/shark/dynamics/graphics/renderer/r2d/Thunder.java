package com.shark.dynamics.graphics.renderer.r2d;

import android.util.Log;

import com.shark.dynamics.graphics.renderer.ProjectionType;
import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import org.joml.Vector2f;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_LINE_STRIP;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;

public class Thunder extends I2DRenderer {

    private static final String TAG = "Thunder";

    public static class ThunderLine {
        public int lineWidth;
        public List<Vector2f> points;
    }


    private FloatBuffer mVertexBuffer;
    private int mVertexArrayHandle;
    private float[] mVertexArray;

    private List<Vector2f> mPoints = new ArrayList<>();
    private List<ThunderLine> mThunderLines = new ArrayList<>();

    private int mLineWidth = 2;
    private Vector2f mCenter;
    private Random mRandom = new Random();

    private float mScreenScale = 1.0f;

    public Thunder(Vector2f center, String vs, String fs, float screenScale) {
        mShader = new Shader(vs, fs);
        mScreenScale = screenScale;
        mCenter = center;
        regenThunderLines(false);
        init(mPoints);
    }

    public void regenThunderLines(boolean update) {
        mPoints.clear();
        boolean reverse = mRandom.nextFloat() - 0.5f > 0;
        boolean b1Left = false;
        ThunderLine mainLine = genPoints(new Vector2f(mCenter.x/2 + mRandom.nextInt((int) (mCenter.x)), mCenter.y*2), 100,6.0f, 13.0f, true, reverse ? !b1Left : b1Left, 5.2f, 6);
        mPoints.addAll(mainLine.points);
        mThunderLines.add(mainLine);

        boolean b2Left = false;
        ThunderLine tl = genPoints(mainLine.points.get(18+ mRandom.nextInt(5)), 50,6.0f, 13.0f, true, reverse ? !b2Left : b2Left, 5.2f, 2);
        mPoints.addAll(tl.points);
        mThunderLines.add(tl);

        tl = genPoints(tl.points.get(18+ mRandom.nextInt(5)), 15,6.0f, 13.0f, false, false, 5.2f, 1);
        mPoints.addAll(tl.points);
        mThunderLines.add(tl);

        boolean b3Left = true;
        tl = genPoints(mainLine.points.get(30 + mRandom.nextInt(6)), 25,6.0f, 13.0f, false, reverse ? !b3Left : b3Left, 15.2f,3);
        mPoints.addAll(tl.points);
        mThunderLines.add(tl);

        boolean b4Left = true;
        tl = genPoints(mainLine.points.get(50 + mRandom.nextInt(6)), 45,6.0f, 13.0f, false, reverse ? !b4Left : b4Left, 10.5f, 4);
        mPoints.addAll(tl.points);
        mThunderLines.add(tl);

        boolean b5Left = false;
        tl = genPoints(tl.points.get(10 + mRandom.nextInt(5)), 20,2.0f, 13.0f, true, reverse ? !b5Left : b5Left, 5.5f, 2);
        mPoints.addAll(tl.points);
        mThunderLines.add(tl);

        boolean b6Left = false;
        tl = genPoints(mainLine.points.get(40 + mRandom.nextInt(6)), 15,2.0f, 13.0f, false, reverse ? !b6Left : b6Left, 15.5f, 1);
        mPoints.addAll(tl.points);
        mThunderLines.add(tl);

        if (update) {
            updatePoints(mPoints);
        }
    }

    private ThunderLine genPoints(Vector2f start, int pointSize, float xStep, float yStep, boolean bendBack, boolean bendLeft, float bendSize, int lineWidth) {
        List<Vector2f> points = new ArrayList<>();
        for (int i = 0; i < pointSize; i++) {
            Vector2f point = new Vector2f(start.x, start.y - i*yStep*mScreenScale);
            points.add(point);
        }

        List<Vector2f> selectedPoints = new ArrayList<>();
        selectedPoints.add(new Vector2f(points.get(0)));
        for (int i = 0; i < pointSize*3/4; i++) {
            selectedPoints.add(new Vector2f(points.get(mRandom.nextInt(pointSize))));
        }
        Collections.sort(selectedPoints, new Comparator<Vector2f>() {
            @Override
            public int compare(Vector2f o1, Vector2f o2) {
                return Float.compare(o1.y, o2.y);
            }
        });

        float centerHeight = (selectedPoints.get(selectedPoints.size()-1).y + selectedPoints.get(0).y)/2;

        float bend = bendSize * mScreenScale * (bendLeft ? -1 : 1);

        for (int i = 0; i < selectedPoints.size(); i++) {
            Vector2f point = selectedPoints.get(i);
            float m = mRandom.nextFloat();
            m = m - 0.5f;
            if (bendBack) {
                if (i != selectedPoints.size()-1) {
                    if (point.y < centerHeight) {
                        point.x += m * xStep * mScreenScale + bend * i;
                    } else {
                        point.x += m * xStep * mScreenScale + bend * (selectedPoints.size() - i);
                    }
                }
            } else {
                if (i != (selectedPoints.size()-1)) {
                    point.x += m * xStep * mScreenScale + bend * (selectedPoints.size() - i);
                }
            }
        }

        ThunderLine tl = new ThunderLine();
        tl.points = selectedPoints;
        tl.lineWidth = Math.max((int) (lineWidth * mScreenScale), 1);

        return tl;
    }

    private void init(List<Vector2f> points) {
        super.initRenderer();
        mType = ProjectionType.kProj2D;

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
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_DYNAMIC_DRAW);

        int stride = 6 * 4;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3*4);
        glEnableVertexAttribArray(1);

        glLineWidth(6);

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

        for (int i = 0; i < points.size(); i++) {
            Vector2f start = points.get(i);

            mVertexArray[i*6] = start.x;
            mVertexArray[i*6+1] = start.y;
            mVertexArray[i*6+2] = 0;

            mVertexArray[i*6+3] = 1.0f;
            mVertexArray[i*6+4] = 1.0f;
            mVertexArray[i*6+5] = 1.0f;
        }

        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexArray);
        mVertexBuffer.position(0);
    }


    @Override
    public void render(float delta) {
        super.render(delta);

        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_DYNAMIC_DRAW);

        glLineWidth(mLineWidth);

        int offset = 0;
        for (ThunderLine line : mThunderLines) {
            glLineWidth(line.lineWidth);
            int drawSize = line.points.size();
            glDrawArrays(GL_LINE_STRIP, offset, drawSize);
            offset += drawSize;
        }
    }
}
