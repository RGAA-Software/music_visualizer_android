package com.shark.dynamics.basic.instance;

public class InstanceUtil {

    public static Object instance(String name) {
        try {
            Class<?> clazz = Class.forName(name);
            return clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
