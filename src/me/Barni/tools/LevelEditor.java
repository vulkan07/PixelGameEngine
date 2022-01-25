package me.Barni.tools;

import me.Barni.*;
import me.Barni.entity.Entity;
import me.Barni.physics.Hitbox;
import me.Barni.physics.Vec2D;
import me.Barni.texture.Texture;

import javax.swing.*;
import java.awt.*;

public class LevelEditor {

    Game game;
    public Camera cam;
    public Map map;

    JFrame pWin = new JFrame();
    EditorGUI eGUI = new EditorGUI(this);

    public int paintTileIndex = 1;

    public Hitbox selection = new Hitbox(0, 0, 1, 1);
    private Vec2D selectionAnchor = new Vec2D(), selectionDimension = new Vec2D();
    private final Texture mouseGizmo = new Texture();

    private int tPos1, tPos2, selectionType;
    Vec2D pos = new Vec2D();
    Vec2D mouseClick = new Vec2D();
    final int SPEED = 12;

    public final int MOUSE_GIZMO_NONE = 0;
    public final int MOUSE_GIZMO_SELECT = 1;
    public final int MOUSE_GIZMO_PAINT = 2;
    public final int MOUSE_GIZMO_ADD_DEC = 3;
    public final int MOUSE_GIZMO_ADD_ENT = 4;
    public final int MOUSE_GIZMO_MOVE = 5;

    public boolean showGrid;

    boolean outlineEnts, outlineDecs, freeCam, paintingGrid;
    private boolean editing, waitingForMousePress, mousePressObtained;
    private boolean mouseBeenPressed;
    private boolean mouseGizmoUsed = true;

    int[] selectedDecorativesID = new int[]{};
    int[] selectedEntitiesID = new int[]{};

    public LevelEditor(Game game) {
        mouseGizmo.loadTexture(game, "mouse_gizmos", 16, 16, true);
        setMouseGizmo(MOUSE_GIZMO_MOVE);
        this.game = game;
        reloadMap(game.getMap()); //Init variables with map
        initPWin();
    }

    public void requestWindowFocus() {
        pWin.requestFocus();
    }

    public void setSelectionType(int i) {
        if (i >= 0 && i < 2)
            selectionType = i;
    }

    public void reloadMap(Map newMap) {
        map = newMap;
        cam = map.cam;
        //selectedDecorativesID = new int[map.decoratives.length];
        //selectedEntitiesID = new int[map.entities.length];
    }

    //TODO invalidate/reset editor checkboxes & titles on map reload !

    private void initPWin() {
        pWin.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pWin.setSize(670, 600);
        pWin.setMinimumSize(new Dimension(650, 520));
        pWin.setTitle("Level editor: <Undefined map>");
        pWin.setContentPane(eGUI.rootPanel);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
        pWin.setVisible(editing);
        if (editing) {
            pWin.setTitle("Level editor: " + game.getMap().getFileName());
            game.window.requestFocus();
            eGUI.updateTxtPreviewImage();
        }
    }

    public boolean isEditing() {
        return editing;
    }

    public void obtainNewMousePress() {
        setMouseGizmo(MOUSE_GIZMO_ADD_DEC + eGUI.selectionBox.getSelectedIndex());
        waitingForMousePress = true;
        mousePressObtained = false;
        game.window.requestFocus();
    }

    public Vec2D getMouseClickLocation() {
        if (mousePressObtained)
            return mouseClick;
        else
            return null;
    }

    public void update() {
        if (!editing)
            return;

        if (waitingForMousePress)
            if (MouseHandler.isPressed(MouseHandler.LMB)) {
                mouseClick = MouseHandler.getPosition().copy().add(cam.getScroll());
                setMouseGizmo(MOUSE_GIZMO_SELECT);
                waitingForMousePress = false;
                mousePressObtained = true;
                eGUI.trySelectAddButtonAction(selectionType);
            }

        if (freeCam) {
            map.cam.followEntity = null;
            map.cam.lookAt(pos);

            if (game.getKeyboardHandler().getKeyState(KeyboardHandler.UP))
                pos.y -= SPEED;

            if (game.getKeyboardHandler().getKeyState(KeyboardHandler.DOWN))
                pos.y += SPEED;

            if (game.getKeyboardHandler().getKeyState(KeyboardHandler.LEFT))
                pos.x -= SPEED;

            if (game.getKeyboardHandler().getKeyState(KeyboardHandler.RIGHT))
                pos.x += SPEED;
        }

        if (paintingGrid) {
            if (game.getKeyboardHandler().getKeyState(KeyboardHandler.SHIFT))
                eGUI.txtPreview.setText("Background");
            else
                eGUI.txtPreview.setText("Foreground");

            Vec2D selectedTile = MouseHandler.getPosition();
            selectedTile.add(game.getMap().cam.getScroll());
            selectedTile.y -= 16;
            selectedTile.div(32);

            tPos1 = ((int) selectedTile.x + (int) selectedTile.y * map.width);
            if (MouseHandler.isPressed(MouseHandler.LMB)) {
                if (game.getKeyboardHandler().getKeyState(KeyboardHandler.SHIFT)) {
                    try {
                        map.setBackTile(tPos1, paintTileIndex);
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                } else {
                    if (tPos1 != tPos2) {
                        tPos2 = tPos1;
                        try {
                            map.setTile(tPos1, paintTileIndex);
                        } catch (ArrayIndexOutOfBoundsException e) {
                        }
                    }
                }
            } else if (MouseHandler.isPressed(MouseHandler.RMB))
                if (game.getKeyboardHandler().getKeyState(KeyboardHandler.SHIFT)) {
                    try {
                        map.setBackTile(tPos1, 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                } else {
                    try {
                        map.setTile(tPos1, 0);
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                }
        } else {
            //
            //Rectangle selection tool
            //
            if (!mouseBeenPressed && MouseHandler.isPressed(MouseHandler.LMB)) {
                selectionAnchor = MouseHandler.getPosition().copy().add(cam.getScroll());
            }
            if (MouseHandler.isPressed(MouseHandler.LMB)) {
                selectionDimension = MouseHandler.getPosition().copy().add(cam.getScroll());
                selectionDimension.y -= 32;
            }
            mouseBeenPressed = MouseHandler.isPressed(MouseHandler.LMB);

            if (selectionAnchor.x > selectionDimension.x) {
                selection.x = selectionDimension.xi();
                selection.w = selectionAnchor.xi() - selectionDimension.xi();
            } else {
                selection.x = selectionAnchor.xi();
                selection.w = selectionDimension.xi() - selectionAnchor.xi();
            }
            if (selectionAnchor.y > selectionDimension.y) {
                selection.y = selectionDimension.yi();
                selection.h = selectionAnchor.yi() - selectionDimension.yi();
            } else {
                selection.y = selectionAnchor.yi();
                selection.h = selectionDimension.yi() - selectionAnchor.yi();
            }
        }
    }

    Color gridColor = new Color(220, 220, 220);

    public void setMouseGizmo(int index) {
        if (index == 0)
            mouseGizmoUsed = false;
        else {
            mouseGizmoUsed = true;
            mouseGizmo.setCurrentFrame(index - 1);
        }
    }

    public void overlayRender(Graphics g) {
        if (!editing)
            return;

        if (mouseGizmoUsed)
            g.drawImage(mouseGizmo.getTexture(), MouseHandler.getPosition().xi() - 16, MouseHandler.getPosition().yi() - 32, null);

        if (showGrid) {
            /*
            g.setColor(gridColor);
            for (int x = 0; x < game.getMap().width; x++) {
                for (int y = 0; y < game.getMap().height; y++) {
                    g.drawRect(x * 32 - cam.scroll.xi(), y * 32 - cam.getScrollX().yi(), 32, 32);
                }
            }*/
        }

        if (MouseHandler.isPressed(MouseHandler.LMB) && !paintingGrid) {
            //game.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            /*
            g.setColor(Color.ORANGE);
            g.drawRect(selection.x - cam.scroll.xi(),
                    selection.y - cam.scroll.yi(),
                    selection.w,
                    selection.h
            );*/
        }

        if (paintingGrid) {
            Vec2D selectedTile = MouseHandler.getPosition();
            selectedTile.add(game.getMap().cam.getScroll());
            selectedTile.y -= 16;
            selectedTile.div(32);/*
            if (selectedTile.x > 0 && selectedTile.y > 0 && selectedTile.x < map.width && selectedTile.y < map.height)
                g.drawRect(selectedTile.xi() * 32 - cam.scroll.xi(), selectedTile.yi() * 32 - cam.scroll.yi(), 32, 32);
        */
        }

        g.setFont(game.getDefaultFont());

        if (outlineDecs) {
            g.setColor(Color.GREEN);
            int i = 0;
            for (Decorative dec : map.decoratives) {
                if (dec == null)
                    continue;
                boolean selected = false;
                for (int j : selectedDecorativesID)
                    if (j == i) {
                        selected = true;
                        break;
                    }
                dec.renderDebug(g, cam, selected);
                i++;
            }
        }
        if (outlineEnts) {
            g.setColor(Color.RED);
            for (Entity ent : map.entities) {
                if (ent == null)
                    continue;
                boolean selected = false;
                for (int i : selectedEntitiesID)
                    if (i == ent.getID()) {
                        selected = true;
                        break;
                    }
                ent.renderDebug(g, cam, selected);
            }
        }
    }
}
