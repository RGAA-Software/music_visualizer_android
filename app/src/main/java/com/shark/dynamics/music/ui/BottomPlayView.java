package com.shark.dynamics.music.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.shark.dynamics.music.R;

import java.util.Timer;
import java.util.TimerTask;


public class BottomPlayView extends View {

    private Bitmap mCoverBitmap;
    private Paint mPaint;
    private BitmapShader mCoverShader;

    private int mDuration;
    private int mCurrentPos;
    private float mSweepAngel = 0;

    private float mDensity;
    private int mBorderPadding;
    private int mStrokeWidth;

    private float mRotateDegrees = 0.0f;
    private Timer mRotateTimer;

    private RectF mCircleRectF = new RectF();

    public BottomPlayView(Context context) {
        this(context, null);
    }

    public BottomPlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("ResourceType")
    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mDensity = Resources.getSystem().getDisplayMetrics().density;

        mBorderPadding = (int) (3 * mDensity);
        mStrokeWidth = (int) (3 * mDensity);

        BitmapFactory.Options options = new BitmapFactory.Options();
        TypedValue value = new TypedValue();
        getResources().openRawResource(R.drawable.default_play_icon, value);
        options.inScaled = false;

        mCoverBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_play_icon, options);
        mCoverShader = new BitmapShader(mCoverBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        mCircleRectF.set(mBorderPadding, mBorderPadding, width-mBorderPadding, height-mBorderPadding);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(mCoverShader);
        canvas.save();
        // 因为是在左上角的控制中心，所以先平移到圆心，在旋转，再平移回去
        // 这跟 OpenGL 中，ortho投影下旋转是一个道理。
        canvas.translate(width/2, height/2);
        canvas.rotate(mRotateDegrees);
        canvas.translate(-width/2,- height/2);

        //mStrokeWidth/2 是 让圆形图片与线没有空隙，画线是上面一半，下面一半，直接减掉会有线的一半宽度的缝隙。
        canvas.drawCircle(width/2, height/2, width/2-mBorderPadding-mStrokeWidth/2, mPaint);
        canvas.restore();

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(getResources().getColor(R.color.main_color));
        mPaint.setStrokeWidth(mStrokeWidth);
        canvas.drawArc(mCircleRectF, -90, mSweepAngel, false, mPaint);

    }

    public void setCoverBitmap(Bitmap bitmap) {
        mCoverBitmap = bitmap;
        mCoverShader = new BitmapShader(mCoverBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    public void setDuration(int duration) {
        mDuration = duration;
        calculateSweepAngel();
    }

    public void setCurrentPos(int pos) {
        mCurrentPos = pos;
        calculateSweepAngel();
    }

    private void calculateSweepAngel() {
        if (mDuration <= 0) {
            return;
        }
        mSweepAngel = mCurrentPos*1.0f/mDuration * 360;
        postInvalidate();
    }


    public void startRotate() {
        stopRotate();
        mRotateTimer = new Timer();
        mRotateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mRotateDegrees += 0.8;
                //mSweepAngel += 0.2;
                postInvalidate();
            }
        }, 0, 20);
    }

    public void stopRotate() {
        if (mRotateTimer != null) {
            mRotateTimer.cancel();
            mRotateTimer.purge();
            mRotateTimer = null;
        }
    }
}
