package com.shark.dynamics.graphics.renderer.visualizer;

import com.shark.dynamics.graphics.Director;
import com.shark.dynamics.graphics.renderer.RendererFactory;
import com.shark.dynamics.graphics.renderer.bars.CircleBarsInstance;
import com.shark.dynamics.graphics.renderer.framebuffer.FrameBuffer;
import com.shark.dynamics.graphics.renderer.r2d.Circle;
import com.shark.dynamics.graphics.renderer.r2d.Lines;
import com.shark.dynamics.graphics.renderer.r2d.Point;
import com.shark.dynamics.graphics.renderer.r2d.Polygon;
import com.shark.dynamics.graphics.renderer.r2d.Rectangle;
import com.shark.dynamics.graphics.renderer.r2d.Ring;
import com.shark.dynamics.graphics.renderer.r2d.Sprite;
import com.shark.dynamics.graphics.renderer.r2d.Triangle;
import com.shark.dynamics.graphics.renderer.r2d.anim.FrameAnimation;
import com.shark.dynamics.graphics.renderer.r2d.bezier.Bezier;
import com.shark.dynamics.graphics.renderer.r2d.bezier.BezierPointGenerator;
import com.shark.dynamics.graphics.renderer.r2d.font.Label;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleSystem;
import com.shark.dynamics.graphics.renderer.r2d.particlesystem.ParticleType;
import com.shark.dynamics.graphics.renderer.texture.Image;
import com.shark.dynamics.graphics.renderer.texture.Texture;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glEnable;

public class GLFeatureImpl extends IGLVisualizer {

    private CircleBarsInstance mCircleBars;
    private float mCenterImageRotate = 0;
    private Sprite mCenterImage;
    private Sprite mBackground;
    private ParticleSystem mParticleSystem;
    private Vector2f mCenter;
    private ParticleSystem mTrianglePS;

    private Lines mLine;
    private Lines mLines;
    private Point mPoints;
    private Triangle mTriangle;
    private Rectangle mRectangle;
    private Circle mCircle;
    private Polygon mPolygon;
    private Bezier mBezier;
    private Bezier mBezier3;
    private Bezier mBezierN;

    private FrameBuffer mFrameBuffer;
    private Sprite mFrameBufferSprite;

    private FrameAnimation mAnim;

    private Label mLabel;
    private Ring mRingInner;
    private Ring mRingOuter;
    private ParticleSystem mCenterPS;

    public GLFeatureImpl() {
        String vs = Director.getInstance()
                .loaderShaderFromAssets("shader/instance_vs.glsl");
        String fs = Director.getInstance()
                .loaderShaderFromAssets("shader/base_2d_fs.glsl");

        mCenterImage = new Sprite("images/oh_lonely.jpg", Sprite.SpriteType.kCircle);
        Vector2f sc = Director.getInstance().getDevice().getScreenRealSize();
        mCenterImage.scaleTo(0.55f, 0.55f, 0.0f);
        mCenterImage.translateTo(sc.x/2 - mCenterImage.getWidth()/2, sc.y/2 - mCenterImage.getHeight()/2, 0);
        mCenter = new Vector2f(sc.x/2, sc.y/2);

        mCircleBars = new CircleBarsInstance(vs, fs, mCenterImage.getWidth()/2 + 30);
        mBarsRenderer = mCircleBars;
        mCircleBars.setCenter();

        Image image = Director.getInstance().getImageLoader().loadFromAssets("images/background.jpg", true, new Size(0.5f, 0.5f), 10);
        Texture texture = new Texture(image);
        mBackground = new Sprite(texture, Sprite.SpriteType.kRect);
        mBackground.setAsBackground();

        mParticleSystem = new ParticleSystem();
        mParticleSystem.setParticleType(ParticleType.kSpark);
        //mParticleSystem.setPosition(300, 300);
        mParticleSystem.setGenParticleCount(5);
        mParticleSystem.setGenDuration(30);
        mParticleSystem.setUseDecreaseScale(true);
        mParticleSystem.setColorOverlay(true);
        mParticleSystem.setTintColor(new Vector3f(1.0f, 0.6f, 0.2f));

        mTrianglePS = new ParticleSystem("images/particle_triangle.png");
        mTrianglePS.setParticleType(ParticleType.kTriangle);
        mTrianglePS.setPosition(mCenter.x, mCenter.y);
        mTrianglePS.setGenParticleCount(2);
        mTrianglePS.setGenDuration(500);
        mTrianglePS.setUseDecreaseScale(false);
        mTrianglePS.setColorOverlay(true);
        mTrianglePS.setTintColor(new Vector3f(0.2f, 1.0f, 0.2f));

        mLine = new Lines(new Vector2f(300, 300), new Vector2f(400, 400));

        List<Vector2f> points = new ArrayList<>();
        points.add(new Vector2f(100, 100));
        points.add(new Vector2f(200, 600));
        points.add(new Vector2f(300, 100));
        points.add(new Vector2f(300, 600));
        mLines = new Lines(points);

        List<Vector2f> points1 = new ArrayList<>();
        points1.add(new Vector2f(500, 500));
        points1.add(new Vector2f(550, 500));
        points1.add(new Vector2f(600, 500));
        mPoints = new Point(points1);

        mTriangle = RendererFactory.create(Triangle.class);
        mTriangle.setDrawStroke(true);
        mTriangle.setDrawFill(true);
        mTriangle.translateTo(100, 100, 0);

        mRectangle = RendererFactory.create(Rectangle.class);
        mRectangle.setDrawStroke(true);
        mRectangle.setDrawFill(true);
        mRectangle.translateTo(250, 100, 0);

        mCircle = new Circle();
        mCircle.translateTo(450, 100, 0);
        mCircle.setDrawFill(true);

        mPolygon = new Polygon(6);
        mPolygon.translateTo(650, 100, 0);
        mPolygon.setDrawFill(true);

        mBezier = new Bezier(new Vector2f(150, 350), new Vector2f(450, 650), new Vector2f(850, 350));
        mBezier3 = new Bezier(new Vector2f(150, 550), new Vector2f(350, 450), new Vector2f(750, 900), new Vector2f(950, 550));

        List<Vector2f> bezierNPoints = new ArrayList<>();
        bezierNPoints.add(new Vector2f(300, 1800));
        bezierNPoints.add(new Vector2f(400, 1200));
        bezierNPoints.add(new Vector2f(500, 600));
        bezierNPoints.add(new Vector2f(600, 1300));
        bezierNPoints.add(new Vector2f(700, 1000));
        bezierNPoints.add(new Vector2f(1000, 500));
        bezierNPoints.add(new Vector2f(1000, 400));
        mBezierN = new Bezier(bezierNPoints);


        mFrameBuffer = new FrameBuffer();
        mFrameBuffer.init((int)sc.x, (int)sc.y);

        String fbVS = Director.getInstance()
                .loaderShaderFromAssets("shader/base_2d_vs.glsl");
        //String fbFS = Director.getInstance()
        //        .loaderShaderFromAssets("shader/texture_2d/gray_tex_fs.glsl");
        String fbFS = Director.getInstance()
                .loaderShaderFromAssets("shader/texture_2d/base_2d_tex_fs.glsl");

        mFrameBufferSprite = new Sprite(
                new Texture(mFrameBuffer.getFrameBufferTexId(),
                        (int)sc.x, (int)sc.y),
                Sprite.SpriteType.kRect, fbVS, fbFS);
        //mFrameBufferSprite.setAsBackground();

        mAnim = new FrameAnimation("images/anim.png", 4, 3);
        mAnim.setPerFrameTime(60);
        mAnim.setRunTime(10000);
        mAnim.setReverseAnim(true);
        mAnim.setTintColor(new Vector3f(0.8f, 0.9f, 0.2f));

        List<Vector2f> path = BezierPointGenerator.gen3Bezier(new Vector2f(100, 100), new Vector2f(600, 600), new Vector2f(800, 2000), new Vector2f(1000, 100), 0.001f);
        mAnim.setBezier(path);

        mLabel = new Label("This is TEXT ggg !! 21:38");
        mLabel.translateTo(100, 800, 0);
        //mLabel.scaleTo(2.3f, 2.3f, 0);

        float step = 50;
        mRingInner = new Ring(150, 230, step, new Vector3f(0.9f, 0.5f, 0.5f), new Vector3f(0.9f, 0.2f, 0.3f));
        mRingInner.translateTo(300, 1600, 0);

        float step2 = 10;
        mRingOuter = new Ring(150, 280+step2, step2, new Vector3f(0.9f, 0.2f, 0.3f), new Vector3f(0.9f, 0.2f, 0.3f));
        mRingOuter.translateTo(300-step2-50, 1600-step2-50, 0);
        mRingOuter.setEnableAlpha(false);
        mRingOuter.setAlphaForward(false);

        mCenterPS = new ParticleSystem("images/particle_circle.png");
        mCenterPS.setParticleType(ParticleType.kMovingCenter);
        mCenterPS.setPosition(300+230, 1600+230);
        mCenterPS.setRadius(230-step/2);
        mCenterPS.setGenParticleCount(5);
        mCenterPS.setGenDuration(200);
        mCenterPS.setColorOverlay(true);
        mCenterPS.setTintColor(new Vector3f(0.9f, 0.8f, 0.3f));
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        mFrameBuffer.begin();

        mBackground.getShader().use();
        mBackground.getShader().setUniformFloat("colorEnhance", 0.1f);
        mBackground.render(delta);

        mCircleBars.render(delta);

        mTrianglePS.render(delta);

        mCenterImageRotate -= delta * 12;
        mCenterImage.rotateTo(mCenterImageRotate, 0, 0, 1);
        mCenterImage.render(delta);

        float rotateDegree = -mCenterImageRotate;
        float radius = mCenterImage.getWidth()/2 + 120;
        float x = (float) (radius * Math.cos(Math.toRadians(rotateDegree)));
        float y = (float) (radius * Math.sin(Math.toRadians(rotateDegree)));
        mParticleSystem.setPosition(mCenter.x+x, mCenter.y+y);
        mParticleSystem.render(delta);

        mLine.render(delta);
        mLines.render(delta);
        mTriangle.render(delta);
        mRectangle.render(delta);
        mCircle.render(delta);
        mPolygon.render(delta);

        mBezier.render(delta);
        mBezier3.render(delta);
        mBezierN.render(delta);

        mPoints.render(delta);

        mFrameBuffer.end();

        mFrameBufferSprite.render(delta);

        mAnim.render(delta);

        mCenterPS.render(delta);
        mRingOuter.render(delta);
        mRingInner.render(delta);

        mLabel.render(delta);
    }
}
