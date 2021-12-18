package me.Barni.entity.childs;

import me.Barni.Camera;
import me.Barni.Game;
import me.Barni.KeyboardHandler;
import me.Barni.entity.Entity;
import me.Barni.particle.ParticleData;
import me.Barni.hud.HUDNotification;
import me.Barni.particle.render.BloodParticleRenderer;
import me.Barni.physics.Hitbox;
import me.Barni.physics.Vec2D;
import me.Barni.texture.Texture;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {

    public boolean canJump() {
        return canJump;
    }

    public void setCanJump(boolean canJump) {
        this.canJump = canJump;
    }

    private boolean canJump, wantToJump, jumped;
    public Vec2D spawnLocation;

    ParticleEmitter pem;
    private Texture face = new Texture();

    public int faceIndex = 0;

    private int respawnTimer, respawnTime, reducedRespawnTime = 1;
    private int blinkTimer = 100;
    //private int idleTimer;
    private int deaths;


    private int level = 1, maxLevel = 3;
    public boolean godMode;

    public int getLevel() {
        return level;
    }

    public void setLevel(int nLevel) {
        this.level = nLevel;
        if (level > maxLevel)
            level = maxLevel;
        if (level < 1)
            level = 1;
        reducedRespawnTime = 1;
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
        //face.setAnimated(false);

        ParticleData pData = new ParticleData();

        pData.moveForceMin = new Vec2D(-7, -7);
        pData.moveForceMax = new Vec2D(7, 7);
        pData.emitting = false;
        pData.max_particles = 128;
        pData.noise = 2;
        pData.lifespanMin = 16;
        pData.lifespanMax = 92;

        pem = new ParticleEmitter(
                game,
                "playerDieParticle",
                new Vec2D(64, 64),
                pData,
                new BloodParticleRenderer());
        game.map.addEntity(pem);
    }

    public void die(int respawnTimeTicks) {

        pem.position.x = position.x + size.x / 2;
        pem.position.y = position.y + size.y / 2;
        pem.pData.emitting = true;
        pem.createParticle(128);
        pem.pData.emitting = false;

        if (godMode) //particles still spawn even if godmode is on
            return;

        game.map.cam.lerp = 0.02f;
        faceIndex = 2;
        deaths++;
        alive = false;
        visible = false;
        HUDNotification n = (HUDNotification) game.getHud().getRoot().getElement("PlayerNotification");
        if (deaths == 20 && level == 1) {
            n.message = "Calm down! Reducing respawn time";
            reducedRespawnTime = 2;
            n.show(220);
        } else {
            n.message = "Deaths: " + deaths;
            n.show(180);
        }

        velocity.mult(0);
        position = spawnLocation.copy();
        respawnTimer = respawnTimeTicks / level / reducedRespawnTime;
        respawnTime = respawnTimeTicks / level / reducedRespawnTime;
    }

    public void respawn() {
        game.map.cam.lerp = game.map.cam.DEFAULT_LERP;
        visible = true;
        alive = true;
        game.screenFadingIn = true;
    }

    @Override
    public boolean onTouchTile(Hitbox tile) {

        switch (tile.solidType) {
            case 2:
                velocity.clamp(2);
                return false;

            case 3:
                if (level > 2)
                    if (!colliderHitbox.smallAABB(tile, 10))
                        return false;
                die(200);
                return false;

        }
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        face.setCurrentFrame(faceIndex);
        //idleTimer++;

        //if (idleTimer > 2000)
        //    faceIndex = 3;


        blinkTimer--;
        //if (idleTimer < 2000) {
        if (blinkTimer <= 0) {
            blinkTimer = 620;
            faceIndex = 1;
        }
        if (blinkTimer == 600) {
            faceIndex = 0;
        }
        //}

        if (godMode)
            faceIndex = 3;

        if (!alive) {
            respawnTimer--;
            if (respawnTimer <= 0)
                respawn();
            return;
        }

        Vec2D moving = new Vec2D(0, 0);
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
            //idleTimer = 0;
            if (game.mapEditing || godMode) {
                moving.y -= speed * 2;
            } else if (canJump && !jumped) {
                moving.y -= 13;
                canJump = false;
            }
        }

        if (game.keyboardHandler.getKeyState(KeyboardHandler.DOWN)) {
            moving.y += speed;
            //idleTimer = 0;
        }

        if (game.keyboardHandler.getKeyState(KeyboardHandler.LEFT)) {
            moving.x -= speed;
            //idleTimer = 0;
        }
        if (game.keyboardHandler.getKeyState(KeyboardHandler.RIGHT)) {
            moving.x += speed;
            //idleTimer = 0;
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
        if (!alive) {
            g.setColor(Color.BLUE);
            g.fillRect(0, 0, (int) Vec2D.remap(respawnTimer, 0, respawnTime, 0, game.WIDTH), 16);
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

}
