package me.Barni.hud;

import me.Barni.Game;
import me.Barni.graphics.RenderableText;
import me.Barni.physics.Vec2D;
import me.Barni.window.MouseHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class HUDElement {

    protected ArrayList<HUDElement> childs = new ArrayList<>();

    protected String name;
    protected float parentOpacity, opacity, targOpacity;
    public Game game;
    public Color color;
    protected float x, y, w, h;
    protected boolean visible;


    public HUDElement(Game g, String name, int x, int y, int w, int h) {
        this.game = g;
        this.name = name;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.color = new Color(0, 0, 0, 100);
        this.visible = true;
        this.opacity = 255;
        this.targOpacity = 255;

    }

    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void add(HUDElement elem) {
        childs.add(elem);
    }

    public void removeElement(String name) {
        for (int i = 0; i < childs.size(); i++) {
            if (childs.get(i).name.equals(name)) {
                childs.remove(i);
                return;
            }
        }
    }

    public HUDElement getElement(String name) {
        for (int i = 0; i < childs.size(); i++) {
            if (childs.get(i).name.equals(name)) {
                return childs.get(i);
            }
        }
        return null;
    }


    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setSize(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public float getWidth() {
        return w;
    }

    public float getHeight() {
        return h;
    }

    public void update() {
        this.opacity = Vec2D.lerp(this.opacity, targOpacity, .12f);
        if (targOpacity == 0 && opacity < .05f)
            visible = false;

        for (int i = 0; i < childs.size(); i++) {
            childs.get(i).update();
            childs.get(i).parentOpacity = this.opacity;
        }
    }

    public void focus() {
        show();
        game.window.setHideCursor(false);
    }
    public void show() {
        targOpacity = 255;
        visible = true;
    }
    public void hide() {
        targOpacity = 0;
    }

    public void render() {
        if (!visible)
            return;
        for (int i = 0; i < childs.size(); i++) {
            childs.get(i).render();
        }
    }

    public void init() {
        if (childs == null)
            childs = new ArrayList<>();
        for (int i = 0; i < childs.size(); i++) {
            childs.get(i).init();
        }
    }
}
