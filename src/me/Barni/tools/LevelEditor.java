package me.Barni.tools;

import me.Barni.*;
import me.Barni.physics.Vec2D;
import me.Barni.window.KeyboardHandler;
import me.Barni.window.MouseHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class LevelEditor {

    private Game game;
    private Map map;


    private JFrame pWin;
    private EditorGUI eGUI;


    Vec2D pos = new Vec2D();
    final int SPEED = 12;

    private boolean freeCam;
    private boolean painting;
    private boolean winFocused;

    private boolean alwaysRender;
    private static boolean editing;

    public LevelEditor(Game g) {
        game = g;
        eGUI = new EditorGUI(this, game);
        pWin = new JFrame();
        initPWin();
    }

    public void undo() {
        eGUI.undo();
    }

    public void focus() {
        winFocused = true;
        pWin.toFront();
        pWin.setVisible(true);
        pWin.setExtendedState(JFrame.NORMAL);
        pWin.requestFocus();
    }

    public void loseFocus() {
        winFocused = false;
        pWin.setVisible(false);
        pWin.setExtendedState(JFrame.ICONIFIED);
        pWin.toBack();
    }

    private void initPWin() {
        pWin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pWin.setSize(700, 480);
        pWin.setMinimumSize(new Dimension(650, 400));
        pWin.setTitle("Level editor: <Undefined map>");
        pWin.setContentPane(eGUI.rootPanel);
        pWin.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                setEditing(false);
            }
        });
        pWin.addWindowStateListener(e -> {
            if (e.getNewState() == 1) {
                setEditing(false);
            }
        });
        pWin.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                winFocused = true;
            }

            public void focusLost(FocusEvent e) {
                winFocused = false; //Sus
            }
        });

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        loseFocus();
    }

    public void setCamPos(Vec2D pos) {
        this.pos = pos.copy();
    }

    public void setMap(Map newMap) {
        map = newMap;
        eGUI.setMap(map);
        if (editing)
            focus();
    }

    public void setFreeCam(boolean b) {
        freeCam = b;
        if (b) {
            map.getPlayer().locked = true;
            map.getCamera().setZoom(1, false);
        }
        if (!b) {
            map.getPlayer().locked = false;
            map.getCamera().followEntity = map.getPlayer();
        }
    }

    public void setPainting(boolean painting) {
        this.painting = painting;
    }

    public boolean isFreeCam() {
        return freeCam;
    }

    public boolean isPainting() {
        return painting;
    }

    public boolean isFocused() {
        return winFocused;
    }

    public boolean isAlwaysRendering() {
        return alwaysRender;
    }

    public void setAlwaysRender(boolean alwaysRender) {
        this.alwaysRender = alwaysRender;
    }

    public void setEditing(boolean e) {
        editing = e;
        if (editing) {
            focus();
            game.window.setHideCursor(false);
            pWin.setTitle("Level editor: " + game.getMap().getFileName());
            pWin.setVisible(true);
            eGUI.update();
        } else {
            game.window.setHideCursor(true);
            loseFocus();
        }
    }

    public static boolean isEditing() {
        return editing;
    }

    public void update() {
        if (!editing)
            return;

        if (KeyboardHandler.poll_CTRL_Z())
            undo();
        eGUI.update();

        if (freeCam) {
            map.getCamera().followEntity = null;
            map.getCamera().lookAt(pos);

            if (KeyboardHandler.getKeyState(KeyboardHandler.UP))
                pos.y -= SPEED;

            if (KeyboardHandler.getKeyState(KeyboardHandler.DOWN))
                pos.y += SPEED;

            if (KeyboardHandler.getKeyState(KeyboardHandler.LEFT))
                pos.x -= SPEED;

            if (KeyboardHandler.getKeyState(KeyboardHandler.RIGHT))
                pos.x += SPEED;
        }
    }

    public void refresh() {
        eGUI.refresh();
    }
}