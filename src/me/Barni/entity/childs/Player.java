package me.Barni.entity.childs;

import me.Barni.Game;
import me.Barni.graphics.*;
import me.Barni.window.KeyboardHandler;
import me.Barni.entity.Entity;
import me.Barni.particle.ParticleData;
import me.Barni.hud.HUDNotification;
import me.Barni.particle.render.BloodParticleRenderer;
import me.Barni.physics.Hitbox;
import me.Barni.physics.Vec2D;
import me.Barni.texture.Texture;
import org.lwjgl.opengl.GL30;

import java.awt.*;

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

    private int respawnTimer, respawnTime, reducedRespawnTime = 1;

    public int getRespawnTimer() {
        return respawnTimer;
    }

    public int getRespawnTime() {
        return respawnTime;
    }

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
        pem.setSaveable(false);
        game.getMap().addEntity(pem);
        setSaveable(false);
    }

    public void die(int respawnTimeTicks) {
        if (!alive)
            return;

        pem.position.x = position.x + size.x / 2;
        pem.position.y = position.y + size.y / 2;
        pem.pData.emitting = true;
        pem.createParticle(128);
        pem.pData.emitting = false;

        if (godMode) //particles still spawn even if godmode is on
            return;
        //game.getMap().test.setText("Deaths: " + deaths);
        game.getMap().getCamera().lerp = 0.02f;
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

        game.getMap().getCamera().shake(6, 100);
        game.getMap().getCamera().setZoom(game.getMap().getCamera().getZoom() + .05f, false);

        velocity.mult(0);
        position = spawnLocation.copy();
        respawnTimer = respawnTimeTicks / level / reducedRespawnTime;
        respawnTime = respawnTimeTicks / level / reducedRespawnTime;
    }

    public void respawn() {
        game.getMap().getCamera().lerp = game.getMap().getCamera().DEFAULT_LERP;
        visible = true;
        alive = true;
        game.getMap().getCamera().setZoom(game.getMap().getCamera().getZoom() - .05f, false);
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

        if (!alive) {
            respawnTimer--;
            if (respawnTimer <= 0)
                respawn();
            return;
        }

        Vec2D moving = new Vec2D(0, 0);
        //CTRL slowdown
        if (KeyboardHandler.getKeyState(KeyboardHandler.CTRL))
            speed = 0.32f;
        else
            speed = 0.47f;

        //Jump mechanism
        jumped = wantToJump;
        wantToJump = false;
        if (KeyboardHandler.getKeyState(KeyboardHandler.UP) ||
                KeyboardHandler.getKeyState(KeyboardHandler.SPACE)) {
            wantToJump = true;
            //idleTimer = 0;
            if (godMode) {
                moving.y -= speed * 2;
            } else if (canJump && !jumped) {
                moving.y -= 13;
                canJump = false;
            }
        }

        if (KeyboardHandler.getKeyState(KeyboardHandler.DOWN)) {
            moving.y += speed;
            //idleTimer = 0;
        }

        if (KeyboardHandler.getKeyState(KeyboardHandler.LEFT)) {
            moving.x -= speed;
            //idleTimer = 0;
        }
        if (KeyboardHandler.getKeyState(KeyboardHandler.RIGHT)) {
            moving.x += speed;
            //idleTimer = 0;
        }

        if (!locked && active) {
            acceleration.add(moving);
        }
    }

    //private RenderableText velText = new RenderableText("",0,0);

    @Override
    public void render(VertexArrayObject vao, ShaderProgram shader) {
        if (!visible || !texture.isValid()) return;

        float[] vArray = GraphicsUtils.generateVertexArray(position.x, position.y, size.x, size.y);

        vao.setVertexData(vArray);

        shader.bind();
        shader.selectTextureSlot("uTexSampler", 0);
        shader.uploadBool("uSelected", false);
        shader.uploadFloat("uTime", game.getGameTime());
        texture.bind();
        GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexLen(), GL30.GL_UNSIGNED_INT, 0);
        texture.unBind();

        /*
        velText.setSize(20);
        velText.setColor(Color.RED);
        velText.setText(String.valueOf(velocity.x));
//        velText.setX(position.xi());
//        velText.setY(position.yi());
        velText.setX(40);
        velText.setY(40);
        velText.render(game.getMap().getCamera());


        velText.setText(String.valueOf(velocity.y));
        velText.setY(position.y+15);
        velText.render(game.getMap().getCamera());
        */
    }
}
