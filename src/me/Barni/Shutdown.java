package me.Barni;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class Shutdown implements Runnable {

    private Game game;

    public Shutdown(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        game.thread.interrupt();
    }

}
