package me.Barni;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Entity {

    Game game;
    String name;
    Vec2D position, size, velocity, gravity = new Vec2D(0,0.7f);
    float speed, resistance;
    boolean visible, active, solid, locked;

    public Entity(Game g, String name) {
        this.game = g;
        this.name = name;
        position = new Vec2D(0,0);
        velocity = new Vec2D(0,0);
        size = new Vec2D(32,32);

        visible = true;
        active = true;
        solid = true;
    }

    public Entity(Game g,  String name, Vec2D pos) {
        this.game = g;
        this.name = name;
        position = pos;
        velocity = new Vec2D(0,0);
        size = new Vec2D(32,32);

        visible = true;
        active = true;
        solid = true;
    }

    public void tick() {
        if (!active) return;
    }

    public void render(BufferedImage img) {
        if (!visible) return;

        Graphics g = img.getGraphics();
        g.setColor(Color.RED);
        g.drawRect((int)position.x, (int)position.y, (int)size.x, (int)size.y);
    }

}
