package com.shark.dynamics.graphics.renderer.r2d.anim;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.r2d.I2DRenderer;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.r2d.bezier.Bezier;
import com.shark.dynamics.graphics.renderer.texture.Image;
import com.shark.dynamics.graphics.renderer.texture.Texture;
import com.shark.dynamics.graphics.shader.Shader;
import com.shark.dynamics.graphics.util.BufferUtil;
import com.shark.dynamics.graphics.util.GLUtil;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;

public class FrameAnimation extends I2DRenderer {

    private List<Image> mImages;
    private List<Texture> mTextures = new ArrayList<>();

    private int mPerFrameTime = 0;
    private int mCurrentIndex = 0;
    private long mTimeLapses = 0;
    private int mCols;
    private int mRows;

    private int mRunTime;
    private int mRunLapses;
    private boolean mRunForward = true;
    private boolean mReverseAnim = false;

    private List<Vector2f> mPath;
    private Vector2f mLastPoint;
    private Vector2f mCurrentPoint;
    private Vector3f mTintColor = new Vector3f(1,1,1);

    private int mVertexArrayHandle;
    private float[] mVertexArray;
    private FloatBuffer mVertexBuffer;

    public FrameAnimation(String texPath, int cols, int rows) {
        String vs = Director.getInstance().loaderShaderFromAssets("shader/base_2d_vs.glsl");
        String fs = Director.getInstance().loaderShaderFromAssets("shader/texture_2d/anim_tex_fs.glsl");
        mShader = new Shader(vs, fs);
        initRenderer();

        mCols = cols;
        mRows = rows;
        mImages = Director.getInstance().getImageLoader().loadAnimFromAssets(texPath, cols, rows);
        for (Image image : mImages) {
            Texture texture = new Texture(image);
            mTextures.add(texture);
        }

        initWithScale(1.0f, 1.0f);
    }

    private void initWithScale(float xScale, float yScale) {
        glBindVertexArray(mRenderVAO);
        Texture texture = mTextures.get(0);
        mWidth = texture.getWidth();
        mHeight = texture.getHeight();
        mOriginWidth = mWidth;
        mOriginHeight = mHeight;

        mVertexArray = new float[] {
                0,  0,  0,          1.0f, 0.0f, 0.0f,   0.0f, 0.0f,
                mWidth*xScale, 0, 0,       0.0f, 1.0f, 0.0f,   1.0f, 0.0f,
                mWidth*xScale, mHeight*yScale, 0, 0.0f, 0.0f, 1.0f,   1.0f, 1.0f,
                0,  mHeight*yScale, 0,     1.0f, 1.0f, 0.0f,   0.0f, 1.0f
        };

        int[] indices = {
                0, 1, 2,
                2, 3, 0
        };

        int stride = 8 * 4;

        mVertexBuffer = BufferUtil.createFloatBuffer(mVertexArray.length, mVertexArray);

        mVertexArrayHandle = GLUtil.glGenBuffer();
        glBindBuffer(GL_ARRAY_BUFFER, mVertexArrayHandle);
        glBufferData(GL_ARRAY_BUFFER, mVertexArray.length*4, mVertexBuffer, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3*4);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6*4);
        glEnableVertexAttribArray(2);

        //
        IntBuffer indicesBuffer = BufferUtil.createIntBuffer(indices.length, indices);

        int indexArray = GLUtil.glGenBuffer();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexArray);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.length*4, indicesBuffer, GL_STATIC_DRAW);
        glBindVertexArray(0);
    }

    // ms
    public void setPerFrameTime(int time) {
        mPerFrameTime = time;
    }

    public void setBezier(List<Vector2f> path) {
        mPath = path;
    }

    public void setRunTime(int time) {
        mRunTime = time;
    }

    public void setReverseAnim(boolean reverse) {
        mReverseAnim = reverse;
    }

    public void setTintColor(Vector3f color) {
        mTintColor = color;
    }

    @Override
    public boolean isCustomModelMatrix() {
        return true;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (mPerFrameTime <= 0) {
            return;
        }
        mTimeLapses += delta*1000;
        mRunLapses += delta*1000;

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (mTimeLapses >= mPerFrameTime) {
            mTimeLapses = 0;
            if (++mCurrentIndex > (mCols*mRows-1)) {
                mCurrentIndex = 0;
            }
        }
        Texture texture = mTextures.get(mCurrentIndex);
        texture.active(mShader, 0);

        if (mRunLapses > mRunTime) {
            mRunLapses = 0;
            if (mReverseAnim) {
                mRunForward = !mRunForward;
            }
        }

        int pointIdx = (int)(mRunLapses * 1.0f/mRunTime * (mPath.size()-1));
        if (mRunForward) {
            mCurrentPoint = mPath.get(pointIdx);
        } else {
            mCurrentPoint = mPath.get(mPath.size()-1-pointIdx);
        }

        mShader.setUniformVec3("tintColor", mTintColor.x, mTintColor.y, mTintColor.z);

        float angel = getAngel(mLastPoint, mCurrentPoint);
        translateTo(mCurrentPoint.x, mCurrentPoint.y, 0);
        rotateTo((float)Math.toDegrees(angel) + 270, 0, 0, 1);

        mModelMatrix.identity();
        mModelMatrix.translate(mTranslate);

        mModelMatrix.translate(mWidth / 2, mHeight / 2, 0);
        mModelMatrix.rotate((float) Math.toRadians(mRotateDegree), 0, 0, 1);
        mModelMatrix.translate(-mWidth / 2, -mHeight / 2, 0);

        mModelMatrix.scale(mScale);

        glUniformMatrix4fv(mShader.getUniformLocation("model"),
                1,
                false,
                getModelBuffer());

        mLastPoint = mCurrentPoint;
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

    }

    private float getAngel(Vector2f start, Vector2f end) {
        if (start == null || end == null) {
            return 0.0f;
        }
        float dx = end.x - start.x;
        float dy = end.y - start.y;
        if (dx == 0) {
            return 0;
        }
        return (float)Math.atan2(dy, dx);
    }
}
