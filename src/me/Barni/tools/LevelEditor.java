package me.Barni.tools;

import me.Barni.*;
import me.Barni.physics.Vec2D;
import me.Barni.window.KeyboardHandler;

import javax.swing.*;
import java.awt.*;

public class LevelEditor {

    private static Game game;
    private Map map;
    private Camera cam;


    private JFrame pWin = new JFrame();
    private EditorGUI eGUI = new EditorGUI(this);


    private int tPos1, tPos2, selectionType;
    Vec2D pos = new Vec2D();
    Vec2D mouseClick = new Vec2D();
    final int SPEED = 12;


    public boolean showGrid;

    boolean outlineEnts, outlineDecs, freeCam, paintingGrid;
    private boolean waitingForMousePress, mousePressObtained;
    private static boolean editing;
    private boolean mouseBeenPressed;

    int[] selectedDecorativesID = {};
    int[] selectedEntitiesID = {};

    public LevelEditor(Game g) {
        game = g;
        initPWin();
    }

    public static void init(Game g) {
        game = g;
    }
    public void requestWindowFocus() {
        pWin.requestFocus();
    }

    public void setMap(Map newMap) {
        map = newMap;
        cam = map.cam;

        eGUI.setMap(map);
    }

    //Initialize JFrame
    private void initPWin() {
        pWin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pWin.setSize(670, 600);
        pWin.setMinimumSize(new Dimension(650, 520));
        pWin.setTitle("Level editor: <Undefined map>");
        pWin.setContentPane(eGUI.rootPanel);
        pWin.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                setEditing(false);
            }
        });

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    public void setEditing(boolean e) {
        editing = e;
        pWin.setVisible(editing);
        if (editing) {
            pWin.setTitle("Level editor: " + game.getMap().getFileName());
            pWin.requestFocus();
            eGUI.update();
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
}