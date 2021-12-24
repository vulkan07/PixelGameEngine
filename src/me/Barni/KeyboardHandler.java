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
    public static final int CTRL = 17;
    public static final int ESC = 27;
    public static final int F1 = 112;
    public static final int PLUS = 107;
    public static final int MINUS = 109;
    public static final int ARROW_UP = 38;
    public static final int ARROW_DOWN = 40;
    public static final int DELETE = 127;
    public static final int ENTER = 10;

    public boolean logPresses;
    private boolean[] pressed = new boolean[525];


    public KeyboardHandler(Game g, boolean logPress) {
        game = g;
        this.logPresses = logPress;
    }

    public boolean getKeyState(int key) {
        return pressed[key];
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (logPresses) game.logger.info("Pressed " + e.getKeyCode());
        if (game.decorativeEditor.editing) game.decorativeEditor.onKeyPress(e.getKeyCode());
        handleCommonPresses(e.getKeyCode());
        pressed[e.getKeyCode()] = true;
    }

    private void handleCommonPresses(int id) {
        switch (id) {
            case F1+1:
                game.mapEditing = !game.mapEditing;
                break;
            case F1+3:
                game.levelEditor.setEditing(!game.levelEditor.isEditing());
                break;
            case F1 + 2:
                game.decorativeEditor.editing = !game.decorativeEditor.editing;
                break;
            case MINUS:
                game.decorativeEditor.selected--;
                game.mapPaintID--;
                break;
            case PLUS:
                game.mapPaintID++;
                game.decorativeEditor.selected++;
                break;
            case Q:
                game.screenFadingOut = true;
                break;
            case E:
                game.screenFadingIn = true;
                break;
            case R:
                game.map.dumpCurrentMapIntoFile("CurrentMap");
                break;
            case ARROW_DOWN:
                game.decorativeEditor.selectedField++;
                break;
            case ARROW_UP:
                game.decorativeEditor.selectedField--;
                break;
            case SPACE:
                if (game.intro.isPlayingIntro())
                    game.intro.skip();
                break;
        }
        /*
        if (e.getKeyCode() == F2) game.mapEditing = !game.mapEditing;
        if (e.getKeyCode() == MINUS)
            game.mapPaintID--;
        if (e.getKeyCode() == PLUS)
            game.mapPaintID++;
        */
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressed[e.getKeyCode()] = false;
    }
}
