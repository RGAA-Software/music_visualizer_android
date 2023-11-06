package com.shark.dynamics.audio;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Timer;
import java.util.TimerTask;

public class SpectrumView extends View {

    private float[] mBars = new float[256];
    private float[] mNewBars = new float[256];

    private final Object mValueLock = new Object();

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mScreenWidth;
    private int mScreenHeight;

    private Timer mTimer;

    public SpectrumView(Context context) {
        super(context);
        init();
    }

    public SpectrumView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpectrumView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LinearGradient linearGradient =new LinearGradient(0, 0, 750, 0, Color.parseColor("#FF3322"), Color.parseColor("#EEFF55"), Shader.TileMode.MIRROR);
        mPaint.setShader(linearGradient);
        mScreenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        mScreenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                postInvalidate();
            }
        }, 0, 17);
    }

    public void dispose() {
        mTimer.cancel();
        mTimer.purge();
    }

    public void updateValue(float[] value) {
        synchronized (mValueLock) {
            if (value.length != mBars.length) {
                return;
            }
            System.arraycopy(value, 0, mNewBars, 0, value.length);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int barWidth = 8;
        int barGap = 5;
        int drawCount = 120;
        int offsetFromBottom = 200;
        synchronized (mValueLock) {
            if (mBars.length < drawCount) {
                return;
            }

            for (int i = 0; i < drawCount; i++) {
                float diff = mNewBars[i] - mBars[i];
                mBars[i] += diff * 1.0f/2;
                if (mBars[i] <= 0) {
                    mBars[i] = 0;
                }
            }

            float itemAngel = 360.0f / drawCount;
            for (int i = 0; i < drawCount; i++) {
                int startXPos = i * (barWidth + barGap);
                int yPos = mScreenHeight - offsetFromBottom;
                canvas.drawRoundRect(
                        startXPos,
                        yPos - mBars[i]*2,
                        startXPos + barWidth,
                        yPos,
                        4.0f, 4.0f, mPaint);

                canvas.save();
                canvas.translate(mScreenWidth/2, mScreenHeight/3);
                canvas.rotate(i*itemAngel);
                int radius = 200;
                canvas.drawRoundRect(
                        radius,
                        0,
                        radius + mBars[i]*2,
                        barWidth,
                        4.0f, 4.0f, mPaint);
                canvas.restore();

            }
        }
    }
}
