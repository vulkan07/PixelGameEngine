package me.Barni;

import java.awt.image.BufferedImage;

public class HUD {
    public HUDElement root;
    public Game game;
    public HUD(Game g) {
        this.game = g;
        this.root = new HUDElement(g, "root", 0,0,0,0);
    }

    public void update()
    {
        root.update();
    }

    public void render(BufferedImage img)
    {
        root.render(img);
    }
}
