package me.Barni;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {

    Game game;
    public String name;
    public Vec2D position, size, velocity, gravity = new Vec2D(0, 0.7f);
    public float speed, resistance;
    public boolean visible, active, solid, locked, collidesWithMap, hasTexture;
    protected Hitbox touchHitbox, colliderHitbox;
    public Texture texture;

    public Entity(Game g, String name) {
        commonConstructor(g, name);
    }

    public Entity(Game g, String name, Vec2D pos) {
        commonConstructor(g, name);
        position = pos;
    }

    public void loadTexture(String imgPath, String animPath) {
        texture.loadTexture(game, imgPath, (int) size.x, (int) size.y, animPath);
        hasTexture = true;
    }

    private void commonConstructor(Game g, String name) {
        game = g;
        this.name = name;

        texture = new Texture();

        visible = true;
        active = true;
        solid = true;
        collidesWithMap = true;

        // -1 is invalid -> uses default physics values
        speed = -1;
        resistance = -1;

        position = new Vec2D(0, 0);
        velocity = new Vec2D(0, 0);
        size = new Vec2D(32, 32);

        touchHitbox = new Hitbox(
                (int) position.x,
                (int) position.y,
                (int) size.x / 2 * -1,
                (int) size.y / 2 * -1,
                (int) size.x * 2,
                (int) size.y * 2);
        colliderHitbox = new Hitbox(
                (int) position.x,
                (int) position.y,
                (int) size.x,
                (int) size.y);
    }

    public void tick() {
        texture.update();
        if (!active) return;
    }

    public void render(BufferedImage img) {
        if (!visible) return;

        Graphics g = img.getGraphics();
        if (hasTexture)
            g.drawImage(texture.getTexture(), (int) position.x, (int) position.y, null);
        g.setColor(Color.RED);
        g.drawRect((int) position.x, (int) position.y, (int) size.x, (int) size.y);
    }

}
