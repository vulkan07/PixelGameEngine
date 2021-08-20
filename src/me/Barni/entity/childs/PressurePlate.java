package me.Barni.entity.childs;

import me.Barni.Game;
import me.Barni.entity.Entity;
import me.Barni.physics.Hitbox;
import me.Barni.texture.Texture;
import me.Barni.physics.Vec2D;

public class PressurePlate extends Entity {

    private int timer = 0;

    public int recharge = 100;
    public float force = 15f;

    public PressurePlate(Game g, String name, Vec2D pos) {
        super(g, name, pos);
        texture = new Texture();
        locked = true;
        solid = false;

        colliderHitbox = new Hitbox(
                (int) (position.x + size.x / 3),
                (int) position.y,
                (int) size.x / 3,
                (int) size.y);
        //touchHitbox = colliderHitbox;
    }

    public void tick() {
        if (timer > 0)
            timer--;


        if (timer == recharge - 10)
            texture.setFrame(2);

        if (timer == recharge - 20)
            texture.setFrame(2);

    }

    public void onTouch(Entity other) {
        if (timer == 0) {
            other.velocity.y = -force;
            other.velocity.x = 0;
            other.position.x = position.x;
            timer += recharge;
            texture.setFrame(1);
        }
    }


}
