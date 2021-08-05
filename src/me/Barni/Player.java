package me.Barni;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {

    boolean canJump, wantToJump, jumped;
    private Vec2D moving;
    public Vec2D spawnLocation;

    ParticleEmitter pem;
    private Texture face = new Texture();

    public int faceIndex = 0;

    private int respawnTimer, respawnTime;
    private int blinkTimer = 100;
    private int idleTimer;


    private int level = 1, maxLevel = 3;
    public int getLevel() {
        return level;
    }

    public void setLevel(int nLevel) {
        this.level = nLevel;
        if (level > maxLevel)
            level = maxLevel;
        if (level < 1)
            level = 1;
        loadTexture("player_" + level);
    }

    public Player(Game g, String name, Vec2D pos) {
        super(g, name, pos);
        size.y = 64;
        size.x = 32;
        touchHitbox = new Hitbox((int) (pos.x), (int) (pos.y), (int) size.x / 2 * -1, (int) size.y / 2 * -1, (int) size.x * 2, (int) size.y * 2);
        colliderHitbox = new Hitbox((int) pos.x, (int) pos.y, (int) size.x, (int) size.y);
        resistance = 0.3f;
        alive = true;
        spawnLocation = new Vec2D();

        face.loadTexture(g, "player_face", size.xi(), size.yi(), true);

        pem = new ParticleEmitter(
                game,
                "playerDieParticle",
                new Vec2D(64, 64),
                new Vec2D(-7, -7),
                new Vec2D(7, 7),
                true,
                128,
                2,
                16,
                92);
        game.map.addEntity(pem);
    }

    public void die(int respawnTimeTicks) {


        faceIndex = 2;
        pem.position.x = position.x + size.x / 2;
        pem.position.y = position.y + size.y / 2;
        pem.emitting = true;
        pem.createParticle(128);
        pem.emitting = false;


        if (game.mapEditing) return;
        alive = false;
        visible = false;

        velocity.mult(0);
        position = spawnLocation.copy();
        respawnTimer = respawnTimeTicks/level;
        respawnTime = respawnTimeTicks/level;
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
        face.frame = faceIndex;
        idleTimer++;

        if (idleTimer > 2000)
            faceIndex = 3;


        blinkTimer--;
        if (idleTimer < 2000) {
            if (blinkTimer <= 0) {
                blinkTimer = 620;
                faceIndex = 1;
            }
            if (blinkTimer == 600) {
                faceIndex = 0;
            }
        }


        if (!alive) {
            respawnTimer--;
            if (respawnTimer <= 0)
                respawn();
            return;
        }

        moving = new Vec2D(0, 0);
        //CTRL slowdown
        if (game.keyboardHandler.getKeyState(KeyboardHandler.CTRL))
            speed = 0.31f;
        else
            speed = 0.5f;

        //Jump mechanism
        jumped = wantToJump;
        wantToJump = false;
        if (game.keyboardHandler.getKeyState(KeyboardHandler.UP) ||
                game.keyboardHandler.getKeyState(KeyboardHandler.SPACE)) {
            wantToJump = true;
            idleTimer = 0;
            if (game.mapEditing) {
                moving.y -= speed * 2;
            } else if (canJump && !jumped) {
                moving.y -= 13;
                canJump = false;
            }
        }

        if (game.keyboardHandler.getKeyState(KeyboardHandler.DOWN)) {
            moving.y += speed;
            idleTimer = 0;
        }

        if (game.keyboardHandler.getKeyState(KeyboardHandler.LEFT)) {
            moving.x -= speed;
            idleTimer = 0;
        }
        if (game.keyboardHandler.getKeyState(KeyboardHandler.RIGHT)) {
            moving.x += speed;
            idleTimer = 0;
        }

        if (!locked && active) {
            velocity.add(moving);
            if (velocity.x > 8)
                velocity.x = 8;
        }
    }


    @Override
    public void render(BufferedImage img, Camera cam) {
        Graphics g = img.getGraphics();
        if (!alive)
        {
            g.setColor(Color.BLUE);
            g.fillRect(0,0,  (int)Vec2D.remap(respawnTimer, 0, respawnTime, 0, game.WIDTH), 16);
        }

        if (!visible) return;
        super.render(img, cam);

        if (face != null)
            g.drawImage(face.getTexture(),
                    position.xi() - cam.scroll.xi(),
                    position.yi() - cam.scroll.yi(),
                    size.xi(),
                    size.yi(),
                    null);
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
