package me.Barni.window;

import org.lwjgl.PointerBuffer;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;

import java.util.Objects;

import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        GLFW.glfwSetWindowTitle(pWindow, title);
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getWindow() {
        return pWindow;
    }

    private String title;
    private int width, height;
    private long pWindow;
    private static boolean lastFocused, focused, minimized;

    public static boolean isFocused() {
        return focused;
    }

    private static void windowFocusCallback(long win, boolean focused) {
        Window.focused = focused;
    }

    public Window(String title, int width, int height) {
        System.out.println("Window created");
        System.out.println("LWJGL - " + Version.getVersion() + "!");
        this.title = title;
        this.width = width;
        this.height = height;
    }

    public void update() {
        GLFW.glfwPollEvents();

        if (lastFocused != focused) // Is Focus changed
            if (focused)
                focus();
            else
                minimize();

        lastFocused = focused;
    }

    public void focus() {
        GLFW.glfwMaximizeWindow(pWindow);
        GLFW.glfwFocusWindow(pWindow);
        GLFW.glfwRequestWindowAttention(pWindow);
        focused = true;
        minimized = false;
    }

    public void minimize() {
        GLFW.glfwIconifyWindow(pWindow);
        minimized = true;
    }

    public void changeMonitor(int monitorIndex) {
        //Create window
        try {
            pWindow = GLFW.glfwCreateWindow(width, height, title, GLFW.glfwGetMonitors().get(monitorIndex), NULL);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to find monitor number " + monitorIndex + "! (" + e.getMessage() + ")");
        }

        if (pWindow == NULL) {
            throw new IllegalStateException("Unable to create GLFW window!");
        }
        initWindow();
    }

    public void init() {
        //Error callback
        GLFWErrorCallback.createPrint(System.err);

        //Init GLFW
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW!");
        }

        //Configure GLFW
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 4);
        //GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, GLFW.GLFW_TRUE);

        changeMonitor(0);
    }

    private void initWindow()
    {
        //Set mouse callbacks
        GLFW.glfwSetCursorPosCallback(pWindow, MouseHandler::mousePosCallback);
        GLFW.glfwSetMouseButtonCallback(pWindow, MouseHandler::mouseButtonCallback);
        GLFW.glfwSetScrollCallback(pWindow, MouseHandler::mouseScrollCallback);
        GLFW.glfwSetWindowFocusCallback(pWindow, Window::windowFocusCallback);

        //Set Keyboard callbacks
        GLFW.glfwSetKeyCallback(pWindow, KeyboardHandler::keyCallback);

        //Make OpenGL context current
        GLFW.glfwMakeContextCurrent(pWindow);

        //Enable V-sync
        GLFW.glfwSwapInterval(1);

        //Make me.Barni.window visible
        GLFW.glfwShowWindow(pWindow);

        //Critical stuff - Don't remove!!!
        GL.createCapabilities();

        //GL30.glClearColor(.53f,.7f,.8f, 1f);
        GL30.glClearColor(0f, 0f, 0f, 1f);
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glEnable(GL30.GL_MULTISAMPLE);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        focus();
    }

    public void clear() {
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
    }
}
