package me.Barni;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardHandler implements KeyListener {
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
    public static final int ESC = 27;

    public boolean logPresses;
    private boolean[] pressed = new boolean[525];


    public KeyboardHandler(Game g, boolean logPress) {
        game = g;
        this.logPresses = logPress;
    }

    public boolean getKeyState(int key)
    {
        return pressed[key];
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (logPresses) game.logger.info("Pressed " + e.getKeyCode());
        pressed[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressed[e.getKeyCode()] = false;
    }
}
