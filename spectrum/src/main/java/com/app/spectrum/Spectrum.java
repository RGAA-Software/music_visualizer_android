package com.app.spectrum;

public class Spectrum {

    static {
        System.loadLibrary("native_spectrum");
    }

    public static float[] sMCArray = new float[256];

    public static native void filterMonsterCat(float[] data);

}
