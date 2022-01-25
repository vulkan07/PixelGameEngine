package me.Barni;

import me.Barni.physics.Vec2D;
import org.lwjgl.glfw.GLFW;

public class MouseHandler {

    public static final int LMB = 1;
    public static final int RMB = 4;
    public static final int WHEEL = 2;
    public static final int MB4 = 8;
    public static final int MB5 = 16;

    private static float x;
    private static float y;

    private static float scrollX;
    private static float scrollY;

    private static float prevX;
    private static float prevY;

    private static boolean buttons[] = new boolean[5];

    public MouseHandler() {
        x = 0;
        y = 0;
    }

    public static float getDeltaX() {
        return x - prevX;
    }

    public static float getDeltaY() {
        return y - prevY;
    }

    public static float getScrollX() {
        return scrollX;
    }

    public static float getScrollY() {
        return scrollY;
    }

    public static Vec2D getPosition() {
        return new Vec2D(x, y);
    }

    public static boolean isPressed(int b) {
        return buttons[b];
    }

    public static void mousePosCallback(long window, double xpos, double ypos) {
        prevX = x;
        prevY = y;
        x = (float) xpos;
        y = (float) ypos;
    }

    public static void update() {
        prevX = x;
        prevY = y;
        scrollX = 0;
        scrollY = 0;
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (button < buttons.length)
            buttons[button] = action == GLFW.GLFW_PRESS;
    }

    public static void mouseScrollCallback(long window, double xoffset, double yoffset) {
        scrollX = (float) xoffset;
        scrollY = (float) yoffset;
    }
}
