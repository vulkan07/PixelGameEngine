package me.Barni;


import java.awt.*;
import java.awt.image.BufferedImage;

public class HUDNotification extends HUDElement {

    public String message = "";
    public Color msgColor;
    private float tx, ty, mx, my;

    public HUDNotification(Game g, String name, String text, int x, int y) {
        super(g, name, x, y, 1, 32);
        mx = x;
        my = y;
        this.message = text;
        this.msgColor = Color.WHITE;
        this.visible = false;
    }

    @Override
    public void render(BufferedImage img) {
        mx = Vec2D.lerp(mx, tx, .08f);
        //my = Vec2D.lerp(my, ty, .08f);
        x = (int) mx;
        //y = (int) my;

        if (!visible) return;
        if (mx <= -w) return;

        Graphics g = img.getGraphics();
        g.setFont(game.defaultFont);
        w = g.getFontMetrics().stringWidth(message)+40;

        g.setColor(color);
        g.fillRect(x, y, w, h);
        g.fillRect(x, y, 8, h);

        g.setColor(msgColor);
        g.drawString(message, x + 16, y + 24);
    }

    @Override
    public void update() {
        if (mx == -w-1 && hidden)
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
        mx = -w-1;
        tx = 16;
        visible = true;
    }

    public void hide() {
        hidden = true;
        tx = -w-1;
    }

    public void slideTo(int x, int y) {
        tx = x;
        ty = y;
    }

}
