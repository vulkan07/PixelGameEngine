package me.Barni.hud;


import me.Barni.Game;
import me.Barni.physics.Vec2D;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HUDNotification extends HUDElement {

    public String message;
    public Color msgColor;
    private float tx, indent;

    public HUDNotification(Game g, String name, String text, int indent, int y) {
        super(g, name, 0, 0, 1, 32);
        this.x = -w - 1;
        this.indent = indent;

        if (indent < 0) {
            game.logger.err("[HUDNotification] - \"" + name + "\" - negative indent value: " + indent + "! Defaulting to 16.");
            this.indent = 16;
        }

        this.y = y;


        this.message = text;
        if (text == null & text.isEmpty()) {

            game.logger.warn("[HUDNotification] - \"" + name + "\" - Empty message!");
        }

        this.msgColor = Color.WHITE;
        this.visible = false;
    }

    @Override
    public void render(BufferedImage img) {
        x = Vec2D.lerp(x, tx, .08f);

        if (!visible) return;
        if (x <= -w) return;

        Graphics g = img.getGraphics();
        g.setFont(game.getDefaultFont());
        w = g.getFontMetrics().stringWidth(message) + 40;

        g.setColor(color);
        g.fillRect((int) x, (int) y, (int) w, (int) h); //Box
        g.fillRect((int) x, (int) y, 8, (int) h); // Decor box at left

        g.setColor(msgColor);
        g.drawString(message, (int) x + 16, (int) y + 24); //Text
    }

    @Override
    public void update() {
        if (x == -w - 1 && hidden)
            visible = false;

        if (showTimer > 0)
            showTimer--;

        if (showTimer <= 0 && timed) {
            hide();
        }
    }

    private int showTimer;
    private boolean timed, hidden;

    public void show(int time) {
        hidden = false;
        timed = false;
        showTimer = 0;
        if (time > 0) {
            showTimer = time;
            timed = true;
        }
        x = -w - 1;
        tx = indent;
        visible = true;
    }

    public void hide() {
        hidden = true;
        tx = -w - 1;
    }

    public void slideTo(int x) {
        tx = x;
    }

}
