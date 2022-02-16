package me.Barni.hud;

import me.Barni.Game;
import me.Barni.window.MouseHandler;
import me.Barni.hud.events.ButtonEventListener;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HUDButton extends HUDElement {

    private boolean hovered, pressed, pressed0;
    public boolean pressable;
    public Color hoveredColor, pressedColor;
    public String text;


    private ButtonEventListener myListener;

    public void setListener(ButtonEventListener listener) {
        this.myListener = listener;
    }

    public HUDButton(Game g, String name, int x, int y, int height, String text) {
        super(g, name, x, y, 0, height);
        hoveredColor = new Color(80, 100, 120, 100);
        pressedColor = new Color(0, 150, 190, 100);
        this.pressable = true;
        this.childs = null;
        this.text = text;
    }

    @Override
    public void add(HUDElement elem) {
        game.getLogger().err("Can't add child to a HUDButton!");
    }

    @Override
    public void update() {
        pressed0 = pressed;
        this.hovered = false;
        this.pressed = false;
        if (pressable)
            if (
                    MouseHandler.getPosition().x > x &&
                            MouseHandler.getPosition().y > y + h &&
                            MouseHandler.getPosition().x < x + w &&
                            MouseHandler.getPosition().y < y + h * 2) {
                //Mouse within box
                this.hovered = true;

                //LMB is pressed
                if (MouseHandler.isPressed(MouseHandler.LMB)) {
                    this.pressed = true;
                    if (myListener != null)
                        if (!pressed0)
                            myListener.onPressed();
                }
            }
        if (myListener != null)
            if (!pressed && pressed0)
                myListener.onReleased();
    }

    @Override
    public void render(BufferedImage img) {
        if (!visible) return;
        Graphics g = img.getGraphics();

        g.setColor(color);
        if (hovered)
            g.setColor(hoveredColor);
        if (pressed)
            g.setColor(pressedColor);

        w = g.getFontMetrics().stringWidth(text) + 36;

        g.fillRect((int) x, (int) y, (int) w, (int) h);
        g.drawRect((int) x, (int) y, (int) w - 1, (int) h - 1);

        g.setFont(game.getDefaultFont());

        g.setColor(Color.white);
        if (!pressable)
            g.setColor(Color.GRAY);
        g.drawString(text, (int) x + 8, (int) (y + h - 8));

        if (pressed || hovered) {
            g.drawRect((int) x - 2, (int) y - 2, (int) w + 3, (int) h + 3);
        }
    }


}
