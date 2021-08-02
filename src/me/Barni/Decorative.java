package me.Barni;

import java.awt.image.BufferedImage;

public class Decorative {
    Game game;
    Texture texture;
    String path;
    int x, y, z, w, h;
    float parallax;
    //Z = -1 : behind map
    //Z =  0 : before map
    //Z =  1 : before entities

    public Decorative(Game g, int x, int y, int zPlane, float parallax, int w, int h, String path) {
        game = g;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.z = zPlane;
        this.parallax = Math.abs(parallax);
        texture = new Texture();
        this.path = path;
        texture.loadTexture(game, path + ".png", w, h, path + ".anim");
    }

    public void tick() {
        texture.update();
    }

    public void render(BufferedImage img, Camera cam) {
        img.getGraphics().drawImage(texture.getTexture(), (int)(x - cam.scroll.xi()*parallax), (int)(y - cam.scroll.yi()*parallax), w, h, null);
    }
}
