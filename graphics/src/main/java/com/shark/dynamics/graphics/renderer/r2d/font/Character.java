package com.shark.dynamics.graphics.renderer.r2d.font;

public class Character {

    public int id;
    public int x;
    public int y;
    public int width;
    public int height;
    public int xOffset;
    public int yOffset;
    public int xAdvance;

    @Override
    public String toString() {
        return "Character{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", xOffset=" + xOffset +
                ", yOffset=" + yOffset +
                ", xAdvance=" + xAdvance +
                '}';
    }
}
