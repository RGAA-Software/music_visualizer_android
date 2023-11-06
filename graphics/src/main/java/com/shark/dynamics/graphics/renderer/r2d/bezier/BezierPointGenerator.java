package com.shark.dynamics.graphics.renderer.r2d.bezier;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class BezierPointGenerator {

    public static List<Vector2f> gen2Bezier(Vector2f p0, Vector2f p1, Vector2f p2, float step) {
        List<Vector2f> points = new ArrayList<>();
        for (float t = 0 ; t <= 1.0f; t += step) {
            Vector2f point = new Vector2f();
            float dt = (1 - t);
            float dtd = dt*dt;
            float td = t*t;
            point.x = dtd*p0.x + 2*t*dt*p1.x + td*p2.x;
            point.y = dtd*p0.y + 2*t*dt*p1.y + td*p2.y;
            points.add(point);
        }
        return points;
    }

    public static List<Vector2f> gen3Bezier(Vector2f p0, Vector2f p1, Vector2f p2, Vector2f p3, float step) {
        List<Vector2f> points = new ArrayList<>();
        for (float t = 0 ; t <= 1.0f; t += step) {
            Vector2f point = new Vector2f();
            float dt = (1 - t);
            float dtd = dt*dt;
            float dtt = dtd*dt;

            float td = t*t;
            float tt = td*t;

            point.x = p0.x*dtt + 3*p1.x*t*dtd + 3*p2.x*td*dt + p3.x*tt;
            point.y = p0.y*dtt + 3*p1.y*t*dtd + 3*p2.y*td*dt + p3.y*tt;
            points.add(point);
        }
        return points;
    }

    public static List<Vector2f> genNBezier(List<Vector2f> points, float step) {
        List<Vector2f> result = new ArrayList<>();
        for (float t = 0 ; t <= 1.0f; t += step) {
            result.add(genBezier(t, points));
        }
        return result;
    }

    private static Vector2f genBezier(float t, List<Vector2f> p) {
        if (p.size() < 2) {
            return p.get(0);
        }
        List<Vector2f> nextLayerPoints = new ArrayList<Vector2f>();
        for (int i = 0; i < p.size() - 1; i++) {
            float x = (1 - t) * p.get(i).x + t * p.get(i + 1).x;
            float y = (1 - t) * p.get(i).y + t * p.get(i + 1).y;
            nextLayerPoints.add(new Vector2f(x, y));
        }
        return genBezier(t, nextLayerPoints);
    }

}
