package me.Barni.physics;

public class Vec2D {

    public static final Vec2D ZERO = new Vec2D(0, 0);

    public float x, y;

    //CONSTRUCTORS
    public Vec2D() {
        x = 0;
        y = 0;
    }

    /**
     * Same as (int)x
     **/
    public int xi() {
        return (int) x;
    }

    /**
     * Same as (int)y
     **/
    public int yi() {
        return (int) y;
    }

    public Vec2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2D copy() {
        return new Vec2D(x, y);
    }

    //Arithmetics
    //With Vec2D
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

    //With scalar
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

    //UTILITY
    public Vec2D scale(float scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    public Vec2D lowLimit(float min) {
        if (x < min) x = min;
        if (y < min) y = min;
        return this;
    }


    public Vec2D limit(float max) {
        if (Math.abs(x) > max)
            if (x > 0)
                x = max;
            else
                x = max * -1;


        if (Math.abs(y) > max)
            if (y > 0)
                y = max;
            else
                y = max * -1;
        return this;
    }


    public float mag() {
        return (float) Math.sqrt((x * x + y * y));
    }

    public Vec2D normalize() {
        x /= mag();
        y /= mag();
        return this;
    }

    public void print() {
        System.out.println("Vec: " + x + ", " + y);
    }

    public Vec2D decrease(float am) {
        if (Math.abs(x) - am < 0) x = 0;
        else if (x != 0) x -= x > 0 ? am : am * -1;

        if (Math.abs(y) - am < 0) y = 0;
        else if (y != 0) y -= y > 0 ? am : am * -1;

        return this;
    }

    public float dist(Vec2D a, Vec2D b) {
        return (float) Math.sqrt(Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2));
    }

    public float dist(Vec2D a) {
        return (float) Math.sqrt(Math.pow((a.x - x), 2) + Math.pow((a.y - y), 2));
    }

    public float dot(Vec2D other) {
        return x * other.x + y * other.y;
    }

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
