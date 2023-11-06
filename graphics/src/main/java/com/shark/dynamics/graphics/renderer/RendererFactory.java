package com.shark.dynamics.graphics.renderer;

import java.lang.reflect.Constructor;

public class RendererFactory {

    public static <T extends IRenderer> T create(Class<T> t) {
        try {
            return t.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends IRenderer> T create(Class<T> t, String vs, String fs) {
        try {
            Constructor<T> constructor = t.getConstructor(String.class, String.class);
            return constructor.newInstance(vs, fs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T extends IRenderer> T create(Class<T> t, String vs, String gs, String fs) {
        try {
            Constructor<T> constructor = t.getConstructor(String.class, String.class, String.class);
            return constructor.newInstance(vs, gs, fs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
