package me.Barni;

import com.sun.istack.internal.NotNull;

public class Vec2D {

    public static final Vec2D ZERO = new Vec2D(0,0);

    public float x, y;

    //CONSTRUCTORS
    public Vec2D() {
        x = 0;
        y = 0;
    }

    public Vec2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    //Arithmetics
    //With Vec2D
    Vec2D add(Vec2D b) {
        x += b.x;
        y += b.y;
        return this;
    }

    Vec2D sub(Vec2D b) {
        x -= b.x;
        y -= b.y;
        return this;
    }

    Vec2D mult(Vec2D b) {
        x *= b.x;
        y *= b.y;
        return this;
    }

    Vec2D div(Vec2D b) {
        x /= b.x;
        y /= b.y;
        return this;
    }

    //With scalar
    Vec2D add(float b) {
        x += b;
        y += b;
        return this;
    }

    Vec2D sub(float b) {
        x -= b;
        y -= b;
        return this;
    }

    Vec2D mult(float b) {
        x *= b;
        y *= b;
        return this;
    }

    Vec2D div(float b) {
        x /= b;
        y /= b;
        return this;
    }

    //UTILITY
    Vec2D scale(float scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    Vec2D limit(float max) {
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


    float mag() {
        return (float) Math.sqrt((x * x + y * y));
    }

    Vec2D normalize() {
        x /= mag();
        y /= mag();
        return this;
    }

    void print() {
        System.out.println("Vec: " + x + ", " + y);
    }

    Vec2D decrease(float am) {
        if (Math.abs(x) - am < 0) x = 0;
        else if (x != 0) x -= x > 0 ? am : am * -1;

        if (Math.abs(y) - am < 0) y = 0;
        else if (y != 0) y -= y > 0 ? am : am * -1;

        return this;
    }

    float dist(@NotNull Vec2D a, @NotNull Vec2D b) {
        return (float) Math.sqrt(Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2));
    }
}
