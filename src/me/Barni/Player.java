package me.Barni;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {

    private Vec2D moving;
    private Hitbox hitbox, h2;

    public Player(Game g, String name, Vec2D pos) {
        super(g, name, pos);
        size.y = 64;
        hitbox = new Hitbox((int) (pos.x - size.x / 2), (int) (pos.y - size.y / 2), (int) size.x * 2, (int) size.y * 2);
        h2 = new Hitbox(64, 64, 32, 32);
        //speed = 6;
    }

    @Override
    public void tick() {
        moving = new Vec2D(0, 0);
        if (game.keyboardHandler.getKeyState(KeyboardHandler.SHIFT))
            speed = 2;
        else
            speed = 4;
        if (game.keyboardHandler.getKeyState(KeyboardHandler.UP)) {
            moving.y -= speed;
        }
        if (game.keyboardHandler.getKeyState(KeyboardHandler.DOWN)) {
            moving.y += speed;
        }
        if (game.keyboardHandler.getKeyState(KeyboardHandler.LEFT)) {
            moving.x -= speed;
        }
        if (game.keyboardHandler.getKeyState(KeyboardHandler.RIGHT)) {
            moving.x += speed;
        }

        hitbox.resolveCollision(h2, moving);

        hitbox.move(moving);
        this.position.add(this.moving);
    }

    @Override
    public void render(BufferedImage img) {
        super.render(img);
        Graphics g = img.getGraphics();

        g.setColor(Color.BLUE);
        if (h2.isColliding(hitbox)) g.setColor(Color.GREEN);

        g.drawRect(hitbox.x, hitbox.y, hitbox.w, hitbox.h);
        g.drawRect(h2.x, h2.y, h2.w, h2.h);

        Hitbox[] hitboxes = hitbox.touchingMapTiles(game.map);
        for (Hitbox hg : hitboxes) {
            if (hg == null) continue;
            g.drawRect(hg.x, hg.y, hg.w, hg.h);
        }
    }

}
