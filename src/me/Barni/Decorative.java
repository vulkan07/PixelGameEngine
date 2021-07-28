package me.Barni;

import java.awt.image.BufferedImage;

public class Decorative {
    Game game;
    Texture texture;
    int x, y, w, h;

    public Decorative(Game g, int x, int y, int w, int h, String path) {
        game = g;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        texture = new Texture();
        texture.loadTexture(game, path + ".png", w, h, path + ".anim");
    }

    public void tick() {
        texture.update();
    }

    public void render(BufferedImage img) {
        img.getGraphics().drawImage(texture.getTexture(), x, y, w, h, null);
    }
}
