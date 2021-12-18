package me.Barni;

import me.Barni.texture.Texture;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Decorative {
    Game game;
    public Texture texture;
    public int x, y, z, w, h;
    public int id;
    public float parallax;
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
        texture.loadTexture(game, path, w, h, true);
    }

    public void tick() {
        texture.update();
    }

    public void render(BufferedImage img, Camera cam) {
        Graphics g = img.getGraphics();
        g.drawImage(texture.getTexture(), (int)(x - cam.scroll.xi()*parallax), (int)(y - cam.scroll.yi()*parallax), w, h, null);
    }
    public void renderDebug(Graphics g, Camera cam, boolean selected) {
        g.setColor(Color.GREEN);
        g.drawRect(x - cam.scroll.xi(), y - cam.scroll.yi(), w, h);
        if (selected) {
            g.setColor(Color.WHITE);
            g.drawRect(x - cam.scroll.xi() - 2, y - cam.scroll.yi() - 2, w + 4, h + 4);
        }
        g.drawString((texture.getPath() + "(#" + id + ")"), x - cam.scroll.xi(), y - cam.scroll.yi() - 10);
    }
}
