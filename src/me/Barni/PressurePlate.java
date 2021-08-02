package me.Barni;

import java.awt.image.BufferedImage;

public class PressurePlate extends Entity {


    int timer = 0, recharge = 100;
    float force = 15f;

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
            texture.frame = 2;

        if (timer == recharge - 20)
            texture.frame = 0;

    }

    public void onTouch(Entity other) {
        if (timer == 0) {
            other.velocity.y = -force;
            other.velocity.x = 0;
            other.position.x = position.x;
            timer += recharge;
            texture.frame = 1;
        }
    }


}
