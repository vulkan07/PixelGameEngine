package me.Barni;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {

    boolean colliding;
    private Vec2D moving;


    public Player(Game g, String name, Vec2D pos) {
        super(g, name, pos);
        size.y = 60;
        size.x = 30;
        touchHitbox = new Hitbox((int) (pos.x), (int) (pos.y), (int) size.x / 2 * -1, (int) size.y / 2 * -1, (int) size.x * 2, (int) size.y * 2);
        colliderHitbox = new Hitbox((int) pos.x, (int) pos.y, (int) size.x, (int) size.y);
        resistance = 0.3f;
    }

    @Override
    public void tick() {
        moving = new Vec2D(0, 0);
        if (game.keyboardHandler.getKeyState(KeyboardHandler.SHIFT))
            speed = .4f;
        else
            speed = .8f;
        if (game.keyboardHandler.getKeyState(KeyboardHandler.UP)) {
            moving.y -= speed * 2;
        }
        if (game.keyboardHandler.getKeyState(KeyboardHandler.DOWN)) {
            moving.y += speed * 2;
        }
        if (game.keyboardHandler.getKeyState(KeyboardHandler.LEFT)) {
            moving.x -= speed;
        }
        if (game.keyboardHandler.getKeyState(KeyboardHandler.RIGHT)) {
            moving.x += speed;
        }

        velocity.add(moving);
        //physics();
    }

    private void physics() {
        //UPDATE PHYSICS STUFF
        this.velocity.add(moving);
        this.velocity.add(gravity);

        this.velocity.limit(10);
        velocity.decrease(resistance);

        this.position.add(velocity);

        colliding = false;
        if (!game.keyboardHandler.getKeyState(KeyboardHandler.SPACE))
            for (Hitbox h : touchHitbox.touchingMapTiles(game.map))
                if (h != null) colliding |= colliderHitbox.resolveCollision(h, velocity, position);

        //UPDATE HITBOXES
        touchHitbox.update(position);
        colliderHitbox.update(position);
    }

    @Override
    public void render(BufferedImage img) {
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
