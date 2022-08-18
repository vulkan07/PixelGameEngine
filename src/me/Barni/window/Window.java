package me.Barni.window;

import me.Barni.Game;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(pWindow, title);
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
    private static Game game;
    private boolean fullScreen;
    private boolean cursorHidden;
    private int width, height, oldW, oldH;
    private long pWindow;
    private static boolean lastFocused, focused, minimized;
    private GLCapabilities glCapabilities;

    public static boolean isFocused() {
        return focused;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    private static void windowFocusCallback(long win, boolean focused) {
        Window.focused = focused;
    }
    private static void fileDropCallback(long win, int count, long names) {
        PointerBuffer nameBuffer = memPointerBuffer(names, count);
        String path = memUTF8(memByteBufferNT1(nameBuffer.get(0)));
        if (game != null)
            game.loadNewMap(path);
    }

    public Window(Game g, String title, int width, int height, boolean fullScreen) {
        this.game = g;
        g.getLogger().info("[WINDOW] Created [" + width + "x" + height + "]");
        g.getLogger().info("[LWJGL] " + Version.getVersion());
        this.title = title;
        this.fullScreen = fullScreen;
        this.width = width;
        this.height = height;
    }


    public void update() {
        glfwPollEvents();

        if (lastFocused != focused) // Is Focus changed
            if (focused)
                focus();
            else
                minimize();

        lastFocused = focused;
    }

    public void requestAttention(){
        glfwRequestWindowAttention(pWindow); //Windows taskbar yellow thingie
    }

    public void focus() {
        //glfwMaximizeWindow(pWindow);
        glfwFocusWindow(pWindow);
        focused = true;
        minimized = false;
    }

    public void setFullScreen(boolean fullScreen) {

        this.fullScreen = fullScreen;
        if (fullScreen){
            oldW = width;
            oldH = height;
            glfwMaximizeWindow(pWindow);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
            setSize(1920,1080, true);
        } else {
            setSize(oldW,oldH, false);
            oldW = width;
            oldH = height;
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
            glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);
        }
        System.out.printf("%dx%d%n", width, height);
    }


    public void minimize() {
        if (fullScreen)
            glfwIconifyWindow(pWindow);
        minimized = true;
    }

    public void destroy() {
        glfwDestroyWindow(pWindow);
        pWindow = NULL;
    }

    public void changeMonitor(int monitorIndex) {
        destroy();
        //Create window
        try {
            if (fullScreen)
                pWindow = glfwCreateWindow(width, height, title, glfwGetMonitors().get(monitorIndex), NULL);
            else
                pWindow = glfwCreateWindow(width, height, title, NULL, NULL);
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
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW!");
        }

        //Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        if (fullScreen) {
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
            glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
        } else {
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
            glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);
        }
        glfwWindowHint(GLFW_SAMPLES, 2);

        //glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);
        changeMonitor(0);
        glfwMaximizeWindow(pWindow);
    }

    public void setSize(int w, int h, boolean fullScreen) {
        width = w;
        height = h;
        this.fullScreen = fullScreen;
        if (fullScreen) {
            glfwMaximizeWindow(pWindow);
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
        } else {
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);
            glfwWindowHint(GLFW_DECORATED, GLFW_TRUE);
        }
    }


    public void setHideCursor(boolean hidden) {
        cursorHidden = hidden;
        glfwSetInputMode(pWindow, GLFW_CURSOR, cursorHidden ? GLFW_CURSOR_HIDDEN : GLFW_CURSOR_NORMAL);
    }

    public boolean isCursorHidden() {
        return cursorHidden;
    }

    public GLCapabilities getGlCapabilities() {
        return glCapabilities;
    }

    private void initWindow() {
        //Set mouse callbacks
        glfwSetCursorPosCallback(pWindow, MouseHandler::mousePosCallback);
        glfwSetMouseButtonCallback(pWindow, MouseHandler::mouseButtonCallback);
        glfwSetScrollCallback(pWindow, MouseHandler::mouseScrollCallback);
        glfwSetWindowFocusCallback(pWindow, Window::windowFocusCallback);
        glfwSetDropCallback(pWindow, Window::fileDropCallback);
        glfwSetWindowSizeCallback(pWindow, Window::windowRefreshCallback);

        glfwSetInputMode(pWindow, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        //Set Keyboard callbacks
        glfwSetKeyCallback(pWindow, KeyboardHandler::keyCallback);

        //Make OpenGL context current
        glfwMakeContextCurrent(pWindow);

        //Enable V-sync
        glfwSwapInterval(1);

        //Make me.Barni.window visible
        glfwShowWindow(pWindow);

        //Critical stuff - Don't remove!!!
        glCapabilities = GL.createCapabilities();
        GL30.glClearColor(0f, 0f, 0f, 1f);
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glEnable(GL30.GL_MULTISAMPLE);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        focus();

        //Set icon
        try {
            setIcon(pWindow, ImageIO.read(new File(game.GAME_DIR + "game.png")));
        } catch (IOException e) {
            game.getLogger().err("Can't set icon image! " + e.getMessage());
        }
        //Set Cursor
        try {
            setCursorImage(pWindow, ImageIO.read(new File(game.TEXTURE_DIR + "/gui/cursorDef.png")));
        } catch (IOException e) {
            game.getLogger().err("Can't set cursor image! " + e.getMessage());
        }
    }

    //Copied code - DO NOT TOUCH!
    private static void setIcon(long window, BufferedImage img) {
        GLFWImage image = GLFWImage.malloc();
        image.set(img.getWidth(), img.getHeight(), loadImageToByteBuffer(img));

        GLFWImage.Buffer images = GLFWImage.malloc(1);
        images.put(0, image);

        glfwSetWindowIcon(window, images);

        images.free();
        image.free();
    }

    private static void setCursorImage(long window, BufferedImage img) {
        GLFWImage image = GLFWImage.malloc();
        image.set(img.getWidth(), img.getHeight(), loadImageToByteBuffer(img));

        //GLFWImage.Buffer images = GLFWImage.malloc(1);
        //images.put(0, image);

        long cursor = glfwCreateCursor(image, MouseHandler.getPosition().xi(), MouseHandler.getPosition().yi());
        glfwSetCursor(window, cursor);
        //images.free();
        image.free();
    }

    private static ByteBuffer loadImageToByteBuffer(final BufferedImage image) {
        final byte[] buffer = new byte[image.getWidth() * image.getHeight() * 4];
        int counter = 0;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                final int c = image.getRGB(j, i);
                buffer[counter + 0] = (byte) (c << 8 >> 24);
                buffer[counter + 1] = (byte) (c << 16 >> 24);
                buffer[counter + 2] = (byte) (c << 24 >> 24);
                buffer[counter + 3] = (byte) (c >> 24);
                counter += 4;
            }
        }
        ByteBuffer bbuffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 -> RGBA
        bbuffer.put(buffer).flip();
        return bbuffer;
    }

    public void clear() {
        GL30.glClear(GL30.GL_COLOR_BUFFER_BIT);
    }

    private static void windowRefreshCallback(long l, int w, int h) {
        game.resizeWindow(w,h, false);
    }
}
