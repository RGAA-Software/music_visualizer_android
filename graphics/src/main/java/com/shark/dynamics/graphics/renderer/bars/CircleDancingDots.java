package com.shark.dynamics.graphics.renderer.bars;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.r2d.Circle;
import com.shark.dynamics.graphics.renderer.r2d.CircleDots;

import org.joml.Vector2f;

public class CircleDancingDots extends IBarsRenderer {

    private int mDotsCount;
    private CircleDots mCircleDots;

    private int mLineSlice;
    private Circle mCircleLine;

    public CircleDancingDots(float imageWidth) {
        super();
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        mDotsCount = 50;
        mCircleDots = new CircleDots(mDotsCount, imageWidth/2 + 60);
        mCircleDots.translateTo(sc.x/2 - mCircleDots.getRadius(), sc.y/2 - mCircleDots.getRadius(), 0);

        mLineSlice = 70;
        mCircleLine = new Circle(mLineSlice, imageWidth/2 + 30);
        mCircleLine.setDrawFill(false);
        mCircleLine.setLineWidth(2);
        mCircleLine.translateTo(sc.x/2 - mCircleLine.getRadius(), sc.y/2 - mCircleLine.getRadius(), 0);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (mSGSArray == null || mMCArray == null) {
            return;
        }
        fallDownSGS(mSGSArray, mLineSlice);

        mCircleLine.increaseAllRadius(mDrawSGSBars, 0.5f);
        mCircleLine.render(delta);

        mCircleDots.increaseAllRadius(mDrawSGSBars, 1.2f);
        mCircleDots.render(delta);

    }
}
