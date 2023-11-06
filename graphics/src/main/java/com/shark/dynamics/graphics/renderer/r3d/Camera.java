package com.shark.dynamics.graphics.renderer.r3d;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private Vector3f mCameraPos;
    private Vector3f mCameraFront;
    private Vector3f mCameraUp;

    private double mPitch;
    private double mYaw;
    private double mRoll;

    private Matrix4f mView = new Matrix4f();
    private Vector3f mOrigin = new Vector3f(0,0,0);

    public Camera(Vector3f pos, Vector3f front, Vector3f up, double pitch, double yaw, double roll) {
        mCameraPos = pos;
        mCameraFront = front;
        mCameraUp = up;
        mPitch = pitch;
        mYaw = yaw;
        mRoll = roll;
    }


    public Vector3f getCameraPos() {
        return mCameraPos;
    }

    public Vector3f getCameraFront() {
        return mCameraFront;
    }

    public Vector3f getCameraUp() {
        return mCameraUp;
    }

    public void setCameraPos(Vector3f pos) {
        mCameraPos = pos;
    }

    public void setCameraFront(Vector3f front) {
        mCameraFront = front;
    }

    public void setCameraUp(Vector3f up) {
        mCameraUp = up;
    }

    public double getPitch() {
        return mPitch;
    }

    public double getYaw() {
        return mYaw;
    }

    public void updateEulerAngel(double pitch, double yaw, double roll) {
        mPitch = pitch;
        mYaw = yaw;
        mRoll = roll;

        mCameraFront.x = (float) (Math.cos( Math.toRadians(mPitch) ) * Math.cos( Math.toRadians(mYaw) ));
        mCameraFront.y = (float) Math.sin( Math.toRadians(mPitch) );
        mCameraFront.z = (float) (Math.cos( Math.toRadians(mPitch) ) * Math.sin( Math.toRadians(mYaw) ));
        mCameraFront.normalize();
    }

    public Matrix4f lookAt() {
        Vector3f currentPos = new Vector3f(mCameraPos);

        mView = mView.identity();
        mView = mView.lookAt(mCameraPos,
                currentPos.add(mCameraFront),
                mCameraUp);
        return mView;
    }

    public Matrix4f lookAtOrigin() {
        Vector3f currentPos = new Vector3f(mCameraPos);

        mView = mView.identity();
        mView = mView.lookAt(mCameraPos,
                mOrigin,
                mCameraUp);
        return mView;
    }

}
