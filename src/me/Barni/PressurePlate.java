package me.Barni;

import java.awt.image.BufferedImage;

public class PressurePlate extends Entity {


    int timer = 0;

    public PressurePlate(Game g, String name, Vec2D pos) {
        super(g, name, pos);
        texture = new Texture();
        locked = true;
        solid = false;

        colliderHitbox = new Hitbox(
                (int) position.x,
                (int) position.y,
                (int) size.x,
                (int) size.y);
        //touchHitbox = colliderHitbox;
    }

    public void tick() {
        if (timer > 0)
            timer--;
    }

    public void onTouch(Entity other) {
        if (timer == 0) {
            other.velocity.y -= 20;
            other.velocity.x = 0;
            other.position.x = position.x;
            timer += 100;
        }
    }


}
