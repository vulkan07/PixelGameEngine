package me.Barni.hud;

import me.Barni.Game;

import java.awt.image.BufferedImage;

public class HUD {
    private final HUDElement root;
    private final Game game;

    public HUDElement getRoot() {
        return root;
    }

    public HUD(Game g) {
        this.game = g;
        this.root = new HUDElement(g, "root", 0, 0, 0, 0);
    }

    public void init() {
        root.init();
    }

    public void update() {
        root.update();
    }

    public void render() {
        root.render();
    }
}
