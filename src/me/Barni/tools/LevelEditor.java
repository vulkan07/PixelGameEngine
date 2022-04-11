package me.Barni.tools;

import me.Barni.*;
import me.Barni.physics.Vec2D;
import me.Barni.window.KeyboardHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class LevelEditor {

    private static Game game;
    private Map map;


    private JFrame pWin = new JFrame();
    private EditorGUI eGUI = new EditorGUI(this);


    Vec2D pos = new Vec2D();
    final int SPEED = 12;

    private boolean freeCam, painting, winFocued;
    private static boolean editing;

    public LevelEditor(Game g) {
        game = g;
        initPWin();
    }

    public static void init(Game g) {
        game = g;
    }

    public void focus() {
        winFocued = true;
        pWin.toFront();
        pWin.setVisible(true);
        pWin.setExtendedState(JFrame.NORMAL);
        pWin.requestFocus();
    }

    public void loseFocus() {
        winFocued= false;
        pWin.setVisible(false);
        pWin.setExtendedState(JFrame.ICONIFIED);
        pWin.toBack();
    }

    private void initPWin() {
        pWin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pWin.setSize(670, 500);
        pWin.setMinimumSize(new Dimension(650, 520));
        pWin.setTitle("Level editor: <Undefined map>");
        pWin.setContentPane(eGUI.rootPanel);
        pWin.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                setEditing(false);
            }
        });
        pWin.addWindowStateListener(e -> {
            if (e.getNewState() == 1){
                setEditing(false);
            }
        });
        pWin.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                winFocued = true;
            }
            public void focusLost(FocusEvent e) {
                winFocued = false;
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
    }

    public void setFreeCam(boolean b) {
        freeCam = b;
        if (!b)
            map.cam.followEntity =map.getPlayer();
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

    public boolean isFocued() {
        return winFocued;
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
            loseFocus();
        }
    }

    public static boolean isEditing() {
        return editing;
    }

    public void update() {
        if (!editing)
            return;

        eGUI.update();

        if (freeCam) {
            map.cam.followEntity = null;
            map.cam.lookAt(pos);

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