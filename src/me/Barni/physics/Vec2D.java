package me.Barni.physics;

import org.joml.Vector2f;

public class Vec2D {


    public float x, y;

    /*
    * VECTOR 2D CLASS
    *
    * HAS ARITHMETICS & UTILITY & VECTOR MATH
    *
    * (Almost) every function returns *this*
    * This allows easy chaining of functions.
    * i.e. position.add(velocity).clamp(10)
    *
    * IMPORTANT:
    *   Copy a Vec2D with .copy();    a2 = a.copy();
    *
    *
    * */

    public Vector2f toV2f()
    {
        return new Vector2f(x,y);
    }

    public Vec2D(Vector2f other)
    {
        x = other.x;
        y = other.y;
    }


    /*--------------------*/
    /*--- CONSTRUCTORS ---*/
    public Vec2D() {
        x = 0;
        y = 0;
    }

    public Vec2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2D(float degrees) {
        float rads = (float) Math.toRadians(degrees);
        this.x = (float) Math.sin(rads);
        this.y = (float) Math.cos(rads);
    }


    /*--------------------*/
    /*--- VALUES & COPY --*/
    public int xi() {
        return (int) x;
    }

    public int yi() {
        return (int) y;
    }

    /**Sets x, y to 0**/
    public void nullify() {
        mult(0);
    }

    public Vec2D copy() {
        return new Vec2D(x, y);
    }


    /*--------------------*/
    /*--- ARITHMETICS  ---*/
    public Vec2D add(Vec2D b) {
        x += b.x;
        y += b.y;
        return this;
    }

    public Vec2D sub(Vec2D b) {
        x -= b.x;
        y -= b.y;
        return this;
    }

    public Vec2D mult(Vec2D b) {
        x *= b.x;
        y *= b.y;
        return this;
    }

    public Vec2D div(Vec2D b) {
        x /= b.x;
        y /= b.y;
        return this;
    }


    /*-----------------------------*/
    /*--- ARITHMETICS (Scalar)  ---*/
    public Vec2D add(float b) {
        x += b;
        y += b;
        return this;
    }

    public Vec2D sub(float b) {
        x -= b;
        y -= b;
        return this;
    }

    public Vec2D mult(float b) {
        x *= b;
        y *= b;
        return this;
    }

    public Vec2D div(float b) {
        x /= b;
        y /= b;
        return this;
    }


    /*-----------------*/
    /*--- LIMITING  ---*/
    public Vec2D clamp(float bound) {
        limit(bound);
        lowLimit(-bound);
        return this;
    }

    public Vec2D clamp(float max, float min) {
        limit(max);
        lowLimit(min);
        return this;
    }

    public Vec2D limit(float max) {
        if (x > max)
            x = max;

        if (y > max)
            y = max;
        return this;
    }

    public Vec2D limit(float maxX, float maxY) {
        if (x > maxX)
            x = maxX;

        if (y > maxY)
            y = maxY;
        return this;
    }

    public Vec2D lowLimit(float min) {
        if (x < min)
            x = min;

        if (y < min)
            y = min;
        return this;
    }

    public Vec2D lowLimit(float minX, float minY) {
        if (x < minX)
            x = minX;

        if (y < minY)
            y = minY;
        return this;
    }


    /*--------------------*/
    /*---   UTILITY    ---*/
    public Vec2D scale(float scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    public float mag() {
        return (float) Math.sqrt((x * x + y * y));
    }

    public Vec2D norm() {
        x /= mag();
        y /= mag();
        return this;
    }

    public void print() {
        System.out.println("[" + x + ", " + y + "]");
    }

    public Vec2D decrease(float am) {
        if (Math.abs(x) - am < 0) x = 0;
        else if (x != 0) x -= x > 0 ? am : -am;

        if (Math.abs(y) - am < 0) y = 0;
        else if (y != 0) y -= y > 0 ? am : -am;

        return this;
    }



    public static float dist(Vec2D a, Vec2D b) {
        return (float) Math.sqrt(Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2));
    }

    public float dist(Vec2D a) {
        return (float) Math.sqrt(Math.pow((a.x - x), 2) + Math.pow((a.y - y), 2));
    }

    public float dot(Vec2D other) {
        return x * other.x + y * other.y;
    }


    /*-----------------------*/
    /*--- INTERPOLATIONS  ---*/
    public static float lerp(float v0, float v1, float t) {
        return (1 - t) * v0 + t * v1;
    }

    public Vec2D lerp(Vec2D v1, float t) {
        return new Vec2D(
                (1 - t) * x + t * v1.x,
                (1 - t) * y + t * v1.y);
    }

    public static float remap(float value, float low1, float high1, float low2, float high2) {
        return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
    }
}
