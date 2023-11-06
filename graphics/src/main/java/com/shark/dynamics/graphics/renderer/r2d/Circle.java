package com.shark.dynamics.graphics.renderer.r2d;

public class Circle extends Polygon {

    public Circle() {
        this(60);
    }

    public Circle(int sliceBorder) {
        super(sliceBorder);
    }

    public Circle(int sliceBorder, float radius) {
        super(sliceBorder, radius);
    }

    public Circle(int sliceBorder, float radius, String vs, String fs) {
        super(sliceBorder, radius, vs, fs);
    }
}
