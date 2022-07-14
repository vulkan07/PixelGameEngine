package me.Barni.hud;

import me.Barni.Game;
import me.Barni.graphics.RenderableText;
import me.Barni.window.MouseHandler;
import me.Barni.hud.events.ButtonEventListener;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HUDButton extends HUDElement {

    private boolean hovered;
    private boolean pressed;
    public boolean pressable;
    public Color hoveredColor, pressedColor;
    public String text;
    private RenderableText velText;


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
        boolean lastPressed = pressed;
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
                        if (!lastPressed)
                            myListener.onPressed();
                }
            }
        if (myListener != null)
            if (!pressed && lastPressed)
                myListener.onReleased();
    }

    @Override
    public void render() {
        if (!visible) return;

    }

    @Override
    public void init() {
        velText = new RenderableText("",0,0);
    }
}
