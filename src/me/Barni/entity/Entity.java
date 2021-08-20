package me.Barni.entity;

import me.Barni.*;
import me.Barni.physics.Hitbox;
import me.Barni.physics.Vec2D;
import me.Barni.texture.Texture;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Entity {

    protected Game game;
    public String name;

    public Vec2D position, size, velocity, gravity;
    public float speed, resistance;
    public boolean visible, active, solid, locked, collidesWithMap, alive;


    protected Hitbox touchHitbox;
    protected Hitbox colliderHitbox;
    public Texture texture;
    private int mapID = -1;


    public Hitbox getTouchHitbox() {
        return touchHitbox;
    }

    public Hitbox getColliderHitbox() {
        return colliderHitbox;
    }

    public int getID()
    {
        return mapID;
    }

    public void setID(int id)
    {
        if (mapID != -1)
        {
            game.logger.err("[ENT] ID already set!");
            return;
        }
        mapID = id;
    }

    //====CONSTRUCTOR====\\
    public Entity(Game g, String name) {
        commonConstructor(g, name);
    }

    public Entity(Game g, String name, Vec2D pos) {
        commonConstructor(g, name);
        position = pos;
    }

    public Entity(Game g, String name, Vec2D pos, Vec2D size) {
        commonConstructor(g, name);
        position = pos;
        this.size = size;
    }
    //===================\\

    public void loadFromEntityData(EntityData ed)
    {
        name = ed.name;

        position = ed.position;
        size = ed.size;

        visible = ed.visible;
        active = ed.active;
        solid = ed.solid;
        locked = ed.locked;
        collidesWithMap = ed.collidesWithMap;
        alive = ed.alive;

        texture = ed.texture;

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

    public void loadTexture(String path) {
        texture.loadTexture(game, path, (int) size.x, (int) size.y, true);
    }

    /**
     * This does all that a constructor needs to do anyway
     **/
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
        gravity = null;

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

    public void die(int respawnTimeTicks) {
    }

    public void respawn() {
    }

    public void tick() {
        texture.update(); //Updates texture anyway (not need to be active)
        if (!active) return;
    }



    public void onTouch(Entity other) {
    }

    public void render(BufferedImage img, Camera cam) {
        if (!visible) return;

        Graphics g = img.getGraphics();
        if (texture != null)
            g.drawImage(texture.getTexture(),
                    position.xi() - cam.scroll.xi(),
                    position.yi() - cam.scroll.yi(),
                    size.xi(),
                    size.yi(),
                    null);

    }

}
