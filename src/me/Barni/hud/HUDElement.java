package me.Barni.hud;

import me.Barni.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class HUDElement {

    protected ArrayList<HUDElement> childs = new ArrayList<>();

    protected String name;
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
        for (int i = 0; i < childs.size(); i++) {
            childs.get(i).update();
        }
    }

    public void render(BufferedImage img) {
        for (int i = 0; i < childs.size(); i++) {
            childs.get(i).render(img);
        }
    }
}
