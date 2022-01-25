package me.Barni.graphics;

import me.Barni.MouseHandler;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera2 {

    public Vector2f pos, target;
    private Matrix4f projMat, viewMat;
    public float zoom = 1, targZoom = 1;

    public Camera2(Vector2f pos) {
        this.pos = pos;
        this.target = new Vector2f();
        projMat = new Matrix4f();
        viewMat = new Matrix4f();
        adjutProjection();
    }

    public void setZoom(float v) {

        projMat.identity();
        projMat.ortho(0f, 1920f * v, 1080f * v, 0f, 0f, 100f);
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
                new Vector3f(pos.x, pos.y, 10),  // Position
                camFront.add(pos.x, pos.y, 0f),  // Looking at
                camUp                               // Where's up
        );

        return viewMat;
    }

    public Matrix4f getProjMat() {
        return projMat;
    }


    public void update() {
        pos = pos.lerp(target,.12f);
        targZoom -= MouseHandler.getScrollY()/20;


        zoom = lerp(zoom, targZoom, .1f);
        setZoom(zoom);
    }

    public Vector2f lerp(Vector2f v1, float t) {
        return new Vector2f(
                (1 - t) * pos.x + t * v1.x,
                (1 - t) * pos.y + t * v1.y);
    }

    public static float lerp(float v0, float v1, float t) {
        return (1 - t) * v0 + t * v1;
    }
}
