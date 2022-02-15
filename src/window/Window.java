package window;

import me.Barni.Game;
import me.Barni.texture.Texture;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;

import java.util.Arrays;

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


    public Window(String title, int width, int height) {
        System.out.println("Window created");
        System.out.println("LWJGL - " + Version.getVersion() + "!");
        this.title = title;
        this.width = width;
        this.height = height;
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

        //Create window
        pWindow = GLFW.glfwCreateWindow(width, height, title, NULL, NULL);
        if (pWindow == NULL) {
            throw new IllegalStateException("Unable to create GLFW window!");
        }

        //Set mouse callbacks
        GLFW.glfwSetCursorPosCallback(pWindow, MouseHandler::mousePosCallback);
        GLFW.glfwSetMouseButtonCallback(pWindow, MouseHandler::mouseButtonCallback);
        GLFW.glfwSetScrollCallback(pWindow, MouseHandler::mouseScrollCallback);

        //Set Keyboard callbacks
        GLFW.glfwSetKeyCallback(pWindow, KeyboardHandler::keyCallback);

        //Make OpenGL context current
        GLFW.glfwMakeContextCurrent(pWindow);

        //Enable V-sync
        GLFW.glfwSwapInterval(1);

        //Make window visible
        GLFW.glfwShowWindow(pWindow);

        //Critical stuff - Don't remove!!!
        GL.createCapabilities();

        //GL30.glClearColor(.53f,.7f,.8f, 1f);
        GL30.glClearColor(0f, 0f, 0f, 1f);
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glEnable(GL30.GL_MULTISAMPLE);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        GLFW.glfwFocusWindow(pWindow);
    }

    public void clear()
    {
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
    }
}
