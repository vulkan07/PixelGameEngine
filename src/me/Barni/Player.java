package me.Barni;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {

    boolean colliding, crouching, jumping;
    private Vec2D moving;


    public Player(Game g, String name, Vec2D pos) {
        super(g, name, pos);
        size.y = 64;
        size.x = 32;
        touchHitbox = new Hitbox((int) (pos.x), (int) (pos.y), (int) size.x / 2 * -1, (int) size.y / 2 * -1, (int) size.x * 2, (int) size.y * 2);
        colliderHitbox = new Hitbox((int) pos.x, (int) pos.y, (int) size.x, (int) size.y);
        resistance = 0.3f;
    }

    @Override
    public void tick() {
        super.tick();
        moving = new Vec2D(0, 0);
        if (game.keyboardHandler.getKeyState(KeyboardHandler.SHIFT))
            speed = .85f;
        else
            speed = .5f;

        if (game.keyboardHandler.getKeyState(KeyboardHandler.CTRL)) {
            speed = .35f;
            colliderHitbox.h = 32;
        }
        else
            colliderHitbox.h = size.yi();

        if (game.keyboardHandler.getKeyState(KeyboardHandler.UP)) {
            moving.y -= speed * 3;
        }
        if (game.keyboardHandler.getKeyState(KeyboardHandler.DOWN)) {
            moving.y = 0;
        }
        if (game.keyboardHandler.getKeyState(KeyboardHandler.LEFT)) {
            moving.x -= speed;
        }
        if (game.keyboardHandler.getKeyState(KeyboardHandler.RIGHT)) {
            moving.x += speed;
        }

        if (!locked && active)
            velocity.add(moving);
    }


    @Override
    public void render(BufferedImage img) {

        super.render(img);

        Graphics g = img.getGraphics();

        g.setColor(Color.BLUE);
        g.drawRect(touchHitbox.x, touchHitbox.y, touchHitbox.w, touchHitbox.h);

        if (colliding)
            g.setColor(Color.RED);
        g.drawRect(colliderHitbox.x, colliderHitbox.y, colliderHitbox.w, colliderHitbox.h);

        Hitbox[] hitboxes = touchHitbox.touchingMapTiles(game.map);
        for (Hitbox hg : hitboxes) {
            if (hg == null) continue;
            g.drawRect(hg.x, hg.y, hg.w, hg.h);
        }

        g.setColor(Color.ORANGE);
        g.fillRect((int) position.x, (int) position.y, 5, 5);
    }

}
