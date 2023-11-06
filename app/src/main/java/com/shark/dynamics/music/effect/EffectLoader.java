package com.shark.dynamics.music.effect;

import android.content.Context;

import com.google.gson.Gson;
import com.shark.dynamics.basic.assets.AssetsUtil;

public class EffectLoader {


    private EffectLoader() {

    }

    public static void loadEffectsFromAssets(Context context, IEffectLoadListener listener) {
        String effectsConfig = AssetsUtil.readAssetFileAsString(context, "effects.json");
        if (effectsConfig == null) {
            listener.onEffectsLoaded(null);
            return;
        }

        EffectConfig config = new Gson().fromJson(effectsConfig, EffectConfig.class);
        listener.onEffectsLoaded(config);
    }

    public static interface IEffectLoadListener {
        void onEffectsLoaded(EffectConfig config);
    }

}
