package me.Barni.entity.childs;

import me.Barni.Game;
import me.Barni.entity.Entity;
import me.Barni.physics.Hitbox;
import me.Barni.texture.Texture;
import me.Barni.physics.Vec2D;
import org.json.JSONObject;

public class PressurePlate extends Entity {

    private int timer = 0;

    public int recharge = 100;
    public float force = 15f;
    public boolean strictTrigger = false;

    public PressurePlate(Game g, String name, Vec2D pos) {
        super(g, name, pos);
        locked = true;
        solid = false;

        colliderHitbox = new Hitbox(
                (int) (position.x + size.x / 3),
                (int) position.y,
                (int) size.x / 3,
                (int) size.y);
    }

    public void tick() {
        if (timer > 0)
            timer--;

        texture.update();
    }

    public void onTouch(Entity other) {
        if (timer == 0) {

            if (strictTrigger)
                if (Math.abs(other.position.x - position.x) > 2 || other.velocity.x > 4)
                    return;

            other.acceleration.y = -force;
            if (strictTrigger) {
                other.velocity.x = 0;
                other.position.x = position.x;
            }
            timer += recharge;

            texture.setAnimationSequence("launch");
        }
    }

    @Override
    public JSONObject serialize() {
        JSONObject jobj = super.serialize();
        jobj.put("force", force);
        jobj.put("recharge", recharge);
        jobj.put("strictTrigger", strictTrigger);
        return jobj;
    }

    @Override
    public void deserialize(JSONObject jobj)
    {
        force = jobj.getFloat("force");
        recharge = jobj.getInt("recharge");
        strictTrigger = jobj.getBoolean("strictTrigger");
    }
}
