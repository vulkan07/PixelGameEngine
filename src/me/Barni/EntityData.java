package me.Barni;

public class EntityData {
    public String name;

    public Vec2D position, size;
    public boolean visible = true, active = true, solid = true, locked, collidesWithMap = true, alive = true;

    //public Hitbox touchHitbox;
    //public Hitbox colliderHitbox;
    public Texture texture;

}
