package com.shark.dynamics.music.effect;

import java.io.Serializable;
import java.util.Objects;

public class EffectItem implements Serializable {

    public String name;
    public String clazz;
    public String location;
    public String cover;
    public boolean visibility;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EffectItem that = (EffectItem) o;
        return clazz.equals(that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }
}
