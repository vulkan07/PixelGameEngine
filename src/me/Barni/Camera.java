package me.Barni;

import me.Barni.entity.Entity;
import me.Barni.physics.Vec2D;
import org.joml.Matrix4f;
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
    private Matrix4f projMat, viewMat, defProjMat;

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
        defProjMat = new Matrix4f();
        viewMat = new Matrix4f();
        adjutProjection();

        defProjMat.identity();
        defProjMat.ortho(0f, 1920f, 1080f, 0f, 0f, 100f);
    }

    public void setViewSize(int w, int h) {
        width = w;
        height = h;
    }

    public void update() {
        //Follow target entity
        if (followEntity != null)
            if (followEntity.position.dist(new Vec2D(center)) >= followDistTreshold)
                lookAt(followEntity.position.copy().sub(followEntity.size.copy().div(2)));

        //Lerp pos to target
        pos = pos.lerp(target, lerp);
        targZoom += MouseHandler.getScrollY() / 20;

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
        this.target.x = target.x - (float) width / 2;
        this.target.y = target.y - (float) height / 2;
    }

    public void setZoom(float v, boolean noLerp) {
        if (noLerp)
            zoom = v;
        else
            targZoom = v;
    }

    private void adjustMatricesToZoom() {
        projMat.identity();
        projMat.ortho(0f, 1920f, 1080f, 0f, 0f, 100f);
        projMat.scale(zoom);
    }

    public void adjutProjection() {
        projMat.identity();
        projMat.ortho(0f, 1920f, 1080f, 0f, 0f, 100f);
    }

    public Matrix4f getViewMat() {
        Vector3f camFront = new Vector3f(0f, 0f, -1f);
        Vector3f camUp = new Vector3f(0f, 1f, 0f);

        viewMat.identity();

        //Generates viewmatrix
        viewMat.lookAt(
                new Vector3f(pos.x, pos.y, 10f),  // Position
                camFront.add(pos.x, pos.y, 0f),  // Looking at
                camUp                                               // Where's up
        );

        return viewMat;
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
