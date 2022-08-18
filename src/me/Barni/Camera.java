package me.Barni;

import me.Barni.entity.Entity;
import me.Barni.physics.Vec2D;
import org.joml.Matrix4f;
import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector3f;
import me.Barni.window.MouseHandler;

public class Camera {

    public Entity followEntity;
    public int followDistTreshold = 50;

    private int width, height;


    public float lerp = .05f;
    public final float DEFAULT_LERP = .05f;

    public Vector2f pos, target, center;
    private final Matrix4f projMat, viewMat, defProjMat, defViewMat;

    private float zoom = 1, targZoom = 1;

    public float getZoom() {
        return zoom;
    }

    public Camera(int w, int h) {

        //Initialize vectors
        center = new Vector2f();
        pos = new Vector2f();
        target = new Vector2f();

        //Set dimensions
        setViewSize(w, h);

        //Initialize matrices
        projMat = new Matrix4f();
        viewMat = new Matrix4f();
        defProjMat = new Matrix4f();
        defViewMat = new Matrix4f();
        adjustProjection();

        defProjMat.identity();
        defProjMat.ortho(0, width, height, 0f, 0f, 100f);

        defViewMat.identity();
        defViewMat.lookAt(
                new Vector3f(pos.x, pos.y, 10f),  // Position
                new Vector3f(camFront).add(pos.x, pos.y, 0f),  // Looking at
                camUp                         // Where's up
        );
    }

    public void setViewSize(int w, int h) {
        width = w;
        height = h;
    }

    private int timer, setTimer, ticks;
    private float ampl, setAmpl;
    private Vec2D shake = new Vec2D();
    private Random r = new Random();

    public void shake(float ampl, int duration) {
        this.ampl = ampl;
        this.setAmpl = ampl;
        this.timer = duration;
        this.setTimer = duration;
    }

    public void update() {
        ticks++;
        if (timer > 0) {
            timer--;
            ampl = lerp(setAmpl, 0, 1 - Vec2D.remap(timer, 0, setTimer, 0, 1));
            shake.y = (float) (Math.sin(ticks / ampl) * ampl * 0.1);
            shake.x = (float) (Math.cos(ticks / ampl) * ampl * 0.1);
        }
        if (timer == 0) {
            shake.x = 0;
            shake.y = 0;
        }

        //Follow target entity
        if (followEntity != null)
            if (followEntity.position.dist(new Vec2D(pos)) >= followDistTreshold)
                lookAt(followEntity.position.copy().sub(followEntity.size.copy().div(2)));

        //Lerp pos to target
        pos = pos.lerp(target, lerp);
        targZoom -= MouseHandler.getScrollY() / 15 * targZoom;
        if (targZoom < 0.15f)
            targZoom = 0.15f;
        if (targZoom > 1.75f)
            targZoom = 1.75f;

        //Lerp zoom to target value
        zoom = lerp(zoom, targZoom, 0.03f);
        adjustMatricesToZoom();

        //Update center pos
        center.x = pos.x + (float) width / 2;
        center.y = pos.y + (float) height / 2;
    }

    public float getScrollX() {
        return pos.x;
    }

    public float getScrollY() {
        return pos.y;
    }

    public Vec2D getScroll() {
        return new Vec2D(pos.x, pos.y);
    }

    public Vector2f getScrollV2f() {
        return pos;
    }

    public void move(Vec2D move) {
        pos.add(move.toV2f());
    }

    public void lookAt(Vec2D target) {
        this.target.x = target.x;
        this.target.y = target.y;
    }

    public void setZoom(float v, boolean noLerp) {
        if (noLerp)
            zoom = v;
        else
            targZoom = v;
    }

    private void adjustMatricesToZoom() {
        projMat.identity();
        projMat.ortho(-width/2*zoom, width/2*zoom, height/2*zoom, -height/2*zoom, -1, 10);
    }

    public void adjustProjection() {
        projMat.identity();
        projMat.ortho(0f, 1920f, 1080f, 0f, 0f, 100f);
    }

    public void setSize(int w, int h) {
        width = w;
        height = h;
        adjustMatricesToZoom();
        defProjMat.identity();
        defProjMat.ortho(0, width, height, 0f, 0f, 100f);
    }

    Vector3f camFront = new Vector3f(0f, 0f, -1f);
    Vector3f camUp = new Vector3f(0f, 1f, 0f);

    public Matrix4f getViewMat() {
        viewMat.identity();
        //Adds the shake downscaled by zoom
        float wm = pos.x + (shake.x * ampl / zoom);
        float hm = pos.y + (shake.y * ampl / zoom);
        //Generates viewmatrix
        viewMat.lookAt(
                new Vector3f(wm, hm, 10f),  // Position
                new Vector3f(camFront).add(wm, hm, 0f),  // Looking at
                camUp                         // Where's up
        );
        return viewMat;
    }

    public Matrix4f getDefaultViewMat() {
        Matrix4f m = new Matrix4f();
        m.identity();
        //Shifts center pos according to zoom
        float wm = pos.x;
        float hm = pos.y;
        //Generates viewmatrix
        m.lookAt(
                new Vector3f(wm, hm, 10f),  // Position
                new Vector3f(camFront).add(wm, hm, 0f),  // Looking at
                camUp                         // Where's up
        );
        return m;
    }

    public Matrix4f getProjMat() {
        return projMat;
    }

    public Matrix4f getDefaultProjMat() {
        return defProjMat;
    }


    Vector2f lerp(Vector2f v1, float t) {
        return new Vector2f(
                (1 - t) * pos.x + t * v1.x,
                (1 - t) * pos.y + t * v1.y);
    }

    float lerp(float v0, float v1, float t) {
        return (1 - t) * v0 + t * v1;
    }
}
