package me.Barni.entity;

import me.Barni.*;
import me.Barni.graphics.ShaderProgram;
import me.Barni.graphics.VertexArrayObject;
import me.Barni.physics.Hitbox;
import me.Barni.physics.Vec2D;
import me.Barni.texture.Texture;
import org.json.JSONObject;
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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

    public int getID() {
        return mapID;
    }

    public void setID(int id) {
        if (mapID != -1) {
            game.getLogger().err("[ENT] ID already set!");
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

    public void loadFromEntityData(EntityData ed) {
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
        texture.uploadImageToGPU(true, 0);
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

    //Overrideable events
    public void onTouch(Entity other) {
    }

    public boolean onTouchTile(Hitbox tile) {
        return true;
    }

    public void render(VertexArrayObject vao, ShaderProgram shader) {
        if (!visible) return;

        float[] vArray = Map.generateVertexArray(position.x,
                position.y, size.x, size.y);

        vao.setVertexData(vArray);


        shader.selectTextureSlot("uTexSampler", 0);
        texture.bind();
        GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexLen(), GL30.GL_UNSIGNED_INT, 0);
        texture.unBind();
    }

    public void renderDebug(Graphics g, Camera cam, boolean selected) {
        /*
        g.setColor(Color.RED);
        g.drawRect(position.xi() - cam.scroll.xi(), position.yi() - cam.scroll.yi(), size.xi(), size.yi());
        String[] className = getClass().toString().split("\\.");
        if (selected) {
            g.setColor(Color.ORANGE);
            g.drawRect(position.xi() - cam.scroll.xi() - 2, position.yi() - cam.scroll.yi() - 2, size.xi() + 4, size.yi() + 4);
        }
        g.drawString(("<" + className[className.length - 1] + "> " + name), position.xi() - cam.scroll.xi(), position.yi() - cam.scroll.yi() - 10);
   */
    }

    public JSONObject serialize() {
        JSONObject jobj = new JSONObject();

        String[] className = getClass().toString().split("\\.");
        jobj.put("class", className[className.length - 1]);
        jobj.put("name", name);
        jobj.put("x", position.xi());
        jobj.put("y", position.yi());
        jobj.put("w", size.xi());
        jobj.put("h", size.yi());
        jobj.put("texture", texture.getPath());

        jobj.put("visible", visible);
        jobj.put("active", active);
        jobj.put("solid", solid);
        jobj.put("locked", locked);
        jobj.put("collidesWithMap", collidesWithMap);
        jobj.put("alive", alive);

        return jobj;
    }

    public void deserialize(JSONObject jobj) {

    }

}
