package me.Barni.window;

import me.Barni.Game;
import me.Barni.tools.LevelEditor;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardHandler {
    Game game;

    public static final int UP = 87;
    public static final int DOWN = 83;
    public static final int LEFT = 65;
    public static final int RIGHT = 68;
    public static final int SPACE = 32;
    public static final int SHIFT = 340;
    public static final int CTRL = 341;
    public static final int F1 = 290;
    public static final int A = 65;
    public static final int C = 67;
    public static final int D = 68;
    public static final int X = 87;
    public static final int Y = 88;
    public static final int Z = 89;

    public static final int MAX_KEYS = 525;

    public static boolean logPresses;
    private static boolean[] pressed = new boolean[MAX_KEYS];


    public KeyboardHandler(Game g, boolean logPress) {
        game = g;
        logPresses = logPress;
    }

    public static boolean getKeyState(int key) {
        return pressed[key];
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (logPresses)
            System.out.println("Pressed " + key);

        if (key > MAX_KEYS || key < 0)
            return;

        //Catch modified keypress
        if (LevelEditor.isEditing())
        if (mods == GLFW_MOD_CONTROL && key != 341 && action == GLFW_PRESS) {
            switch (key) {
                case C:
                    System.out.println("CTRL+C");
                    break;
                case D:
                    System.out.println("CTRL+D");
                    break;
                //These are bad, need to use localized: glfwSetCharCallback(window, character_callback);
                case Z:
                    System.out.println("CTRL+Z");
                    break;
            }
            return;
        }

        if (action == GLFW_PRESS) {
            pressed[key] = true;
        }
        if (action == GLFW_RELEASE) {
            pressed[key] = false;
        }
    }

    public static void update(Game game) {
        if (pressed[F1 + 2])
            game.getLevelEditor().setEditing(!game.getLevelEditor().isEditing());

        if (pressed[SPACE])
            if (game.getIntro().isPlayingIntro())
                game.getIntro().end();

    }

}
