package window;

import me.Barni.Game;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.CallbackI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardHandler {
    Game game;

    public static final int UP = 87;
    public static final int DOWN = 83;
    public static final int LEFT = 65;
    public static final int RIGHT = 68;
    public static final int SPACE = 32;
    public static final int E = 69;
    public static final int Q = 81;
    public static final int R = 82;
    public static final int SHIFT = 16;
    public static final int CTRL = 17;
    public static final int ESC = 27;
    public static final int F1 = 112;
    public static final int PLUS = 107;
    public static final int MINUS = 109;
    public static final int ARROW_UP = 38;
    public static final int ARROW_DOWN = 40;
    public static final int DELETE = 127;
    public static final int ENTER = 10;

    public static final int MAX_KEYS = 525;

    public boolean logPresses;
    private static boolean[] pressed = new boolean[MAX_KEYS];


    public KeyboardHandler(Game g, boolean logPress) {
        game = g;
        this.logPresses = logPress;
    }

    public static boolean getKeyState(int key) {
        return pressed[key];
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (key > MAX_KEYS)
            return;

        if (action == GLFW.GLFW_PRESS) {
            pressed[key] = true;
        }
        if (action == GLFW.GLFW_RELEASE) {
            pressed[key] = false;
        }
    }

    private void handleCommonPresses(int id) {
        switch (id) {
            case F1 + 3:
                game.getLevelEditor().setEditing(!game.getLevelEditor().isEditing());
                break;
            case R:
                game.getMap().dumpCurrentMapIntoFile("CurrentMap");
                break;
            case SPACE:
                if (game.getIntro().isPlayingIntro())
                    game.getIntro().skip();
                break;
        }
    }

}