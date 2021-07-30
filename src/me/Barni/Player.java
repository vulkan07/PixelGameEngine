package me.Barni;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {

    boolean crouching, jumping;
    private Vec2D moving;
    ParticleEmitter pem;

    private int respawnTimer;
    public Vec2D spawnLocation;


    public Player(Game g, String name, Vec2D pos) {
        super(g, name, pos);
        size.y = 63;
        size.x = 32;
        touchHitbox = new Hitbox((int) (pos.x), (int) (pos.y), (int) size.x / 2 * -1, (int) size.y / 2 * -1, (int) size.x * 2, (int) size.y * 2);
        colliderHitbox = new Hitbox((int) pos.x, (int) pos.y, (int) size.x, (int) size.y);
        resistance = 0.3f;
        speed = 0.7f;
        alive = true;
        spawnLocation = new Vec2D();

        pem = new ParticleEmitter(
                game,
                "playerDieParticle",
                new Vec2D(64, 64),
                new Vec2D(-7,-7),
                new Vec2D(7,7),
                true,
                128,
                2,
                16,
                92);
        game.map.addEntity(pem);
    }

    public void die(int respawnTimeTicks) {
        pem.position.x = position.x + size.x / 2;
        pem.position.y = position.y + size.y / 2;
        pem.emitting = true;
        pem.createParticle(128);
        pem.emitting = false;

        alive = false;
        visible = false;

        velocity.mult(0);
        position = spawnLocation.copy();
        respawnTimer = respawnTimeTicks;
        //game.screenFadingOut = true;
    }

    public void respawn() {
        visible = true;
        alive = true;
        //game.screenFadingIn = true;
    }

    @Override
    public void tick() {
        super.tick();


        if (!alive) {
            //if (respawnTimer < respawnTime - 2)
            //    pem.emitting = false;

            respawnTimer--;
            if (respawnTimer <= 0)
                respawn();
            return;
        }

        moving = new Vec2D(0, 0);
        if (game.keyboardHandler.getKeyState(KeyboardHandler.CTRL))
            velocity.limit(3);
        /*
        if (game.keyboardHandler.getKeyState(KeyboardHandler.CTRL)) {
            speed = .35f;
            colliderHitbox.h = 32;
        } else
            colliderHitbox.h = size.yi();*/

        if (game.keyboardHandler.getKeyState(KeyboardHandler.UP) ||
                game.keyboardHandler.getKeyState(KeyboardHandler.SPACE)) {
            moving.y -= speed * 2;
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

        if (!locked && active)
            velocity.add(moving);
    }

    /*
    @Override
    public void render(Graphics2D g, Camera cam) {

        super.render(g, cam);

        Graphics g = img.getGraphics();

        //Draw hitbox
        g.setColor(Color.BLUE);
        g.drawRect(touchHitbox.x - cam.scroll.xi(), touchHitbox.y - cam.scroll.yi(), touchHitbox.w, touchHitbox.h);


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

    }*/

}
