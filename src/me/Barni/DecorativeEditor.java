package me.Barni;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DecorativeEditor {
    Game game;
    Map map;
    int selected = 0, selectedField = 0, speed = 5;
    boolean duplicated, wantToDuplicate, deleted, wantToDelete, editing = false, fieldListening;

    public DecorativeEditor(Game game, Map map) {
        this.game = game;
        this.map = map;
    }

    public void tick() {
        if (!editing) return;

        if (selected < 0)
            selected = map.getDecCount() - 1;
        if (selected > map.getDecCount() - 1)
            selected = 0;
        if (selectedField < 0)
            selectedField = 5;
        if (selectedField > 5)
            selectedField = 0;


        if (game.keyboardHandler.getKeyState(KeyboardHandler.CTRL)) {
            map.decoratives[selected].x /= 32;
            map.decoratives[selected].x *= 32;
            map.decoratives[selected].y /= 32;
            map.decoratives[selected].y *= 32;
        }
        if (game.keyboardHandler.getKeyState(KeyboardHandler.DOWN)) {
            map.decoratives[selected].y += speed;
        }
        if (game.keyboardHandler.getKeyState(KeyboardHandler.UP)) {
            map.decoratives[selected].y -= speed;
        }
        if (game.keyboardHandler.getKeyState(KeyboardHandler.LEFT)) {
            map.decoratives[selected].x -= speed;
        }
        if (game.keyboardHandler.getKeyState(KeyboardHandler.RIGHT)) {
            map.decoratives[selected].x += speed;
        }

        deleted = wantToDelete;
        wantToDelete = false;
        if (game.keyboardHandler.getKeyState(KeyboardHandler.DELETE)) {
            wantToDelete = true;
            if (!deleted) {
                map.removeDecorative(selected);
                selected--;
            }
        }

        duplicated = wantToDuplicate;
        wantToDuplicate = false;
        if (game.keyboardHandler.getKeyState(KeyboardHandler.CTRL) &&
                game.keyboardHandler.getKeyState(KeyboardHandler.SPACE)) {
            wantToDuplicate = true;

            if (!duplicated)
                duplicateDecorative(map.decoratives[selected]);

        }

        map.cam.lookAt(new Vec2D(map.decoratives[selected].x, map.decoratives[selected].y));
    }

    public void duplicateDecorative(Decorative dec) {
        Decorative d = new Decorative(game, dec.x, dec.y, dec.z, dec.parallax, dec.w, dec.h, dec.texture.getPath());
        map.addDecorative(d);
    }

    public void onKeyPress(int keyCode) {
        if (keyCode == KeyboardHandler.ENTER) {
            if (!fieldListening) {
                fieldListening = true;
                game.setFocusable(false); //focus on text field
            }
        }
    }

    public void render(BufferedImage img, Camera cam) {
        if (!editing) return;
        Graphics g = img.getGraphics();

        g.setColor(new Color(0, 0, 0, 50));
        g.fillRect(16, 8, 200, 16);
        g.fillRect(16, 8, 200, map.getDecCount() * 24 + 24);

        g.fillRect(232, 8, 230, 16);
        g.fillRect(232, 8, 230, 24 * 9);
        g.setFont(game.defaultFont);
        g.setColor(Color.WHITE);
        g.drawString("Decorative editing     Properties", 20, 20);

        if (fieldListening) {
            g.setColor(new Color(0, 0, 0, 50));
            g.fillRect(1920 / 2, 1080 / 2, 200, 16);
            g.fillRect(1920 / 2, 1080 / 2, 200, 60);
            g.setColor(Color.WHITE);
            g.drawString(game.textField.getText(), 1920 / 2 + 20, 1080 / 2 + 20);
        }

        for (int i = 0; i < map.decoratives.length; i++) {
            Decorative d = map.decoratives[i];
            if (d != null) {
                if (selected == i) {
                    g.setColor(Color.GREEN);
                    g.drawRect((int) (d.x - cam.scroll.xi() * d.parallax), (int) (d.y - cam.scroll.yi() * d.parallax), d.w, d.h);

                    g.drawString("X            " + d.x, 248, 48);
                    g.drawString("Y            " + d.y, 248, 72);
                    g.drawString("Width      " + d.w, 248, 96);
                    g.drawString("Height     " + d.h, 248, 120);
                    g.drawString("Z-layer    " + d.z, 248, 168);
                    g.drawString("Parallax   " + d.parallax, 248, 192);
                    g.drawString("Material   " + d.texture.getPath(), 248, 216);

                } else
                    g.setColor(Color.WHITE);
                g.drawString(d.texture.getPath(), 24, i * 24 + 48);
            }
        }
    }
}
