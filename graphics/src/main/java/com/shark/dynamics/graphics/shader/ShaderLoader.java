package com.shark.dynamics.graphics.shader;

import android.content.Context;

import com.shark.dynamics.basic.assets.AssetsUtil;

public class ShaderLoader {

    private Context mContext;

    public ShaderLoader(Context context) {
        mContext = context;
    }

    public String loadShaderSourceFromAssets(String path) {
        return AssetsUtil.readAssetFileAsString(mContext, path);
    }

}
