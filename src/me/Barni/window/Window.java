package me.Barni.window;

import me.Barni.Game;
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
    private Game game;
    private boolean fullScreen;
    private int width, height;
    private long pWindow;
    private static boolean lastFocused, focused, minimized;

    public static boolean isFocused() {
        return focused;
    }

    private static void windowFocusCallback(long win, boolean focused) {
        Window.focused = focused;
    }

    public Window(Game g, String title, int width, int height) {
        this.game = g;
        g.getLogger().info("[WINDOW] Created [" + width + "x" + height + "]");
        g.getLogger().info("[LWJGL] " + Version.getVersion());
        this.title = title;
        setSize(width, height, false, false);
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
        if (fullScreen) {
            GLFW.glfwMaximizeWindow(pWindow);
            GLFW.glfwRequestWindowAttention(pWindow); //Windows taskbar yellow thingie
        }
        GLFW.glfwFocusWindow(pWindow);
        focused = true;
        minimized = false;
    }

    public void minimize() {
        if (fullScreen)
            GLFW.glfwIconifyWindow(pWindow);
        minimized = true;
    }

    public void destroy() {
        GLFW.glfwDestroyWindow(pWindow);
        pWindow = -1;
    }

    public void changeMonitor(int monitorIndex) {
        destroy();
        //Create window
        try {
            if (fullScreen)
                pWindow = GLFW.glfwCreateWindow(width, height, title, GLFW.glfwGetMonitors().get(monitorIndex), NULL);
            else
                pWindow = GLFW.glfwCreateWindow(width, height, title, NULL, NULL);
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
        GLFWErrorCallback.createPrint(System.out);

        //Init GLFW
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW!");
        }

        //Configure GLFW
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        if (fullScreen) {
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_FALSE);
            GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_FALSE);
        } else {
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_FALSE);
            GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GLFW.GLFW_TRUE);
        }
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 1);

        changeMonitor(0);
    }

    private void setSize(int w, int h, boolean fullScreen, boolean refresh) {
        width = w;
        height = h;
        this.fullScreen = fullScreen;
        if (refresh)
            init();
    }

    private void initWindow() {
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
