package me.Barni;

public class Physics {

    Game game;
    public Vec2D gravity = new Vec2D(0, .8f);
    public float def_speed = .8f, def_resistance = .4f;
    public Map map;

    public Physics(Game g, Map m) {
        game = g;
        map = m;
    }

    public void init() {
        for (Entity ent : map.entities) {
            if (ent != null) {
                if (ent.resistance == -1) ent.resistance = def_resistance;
                if (ent.speed == -1) ent.speed = def_speed;
            }
        }
    }

    public void update() {
        for (Entity ent : map.entities) {
            if (ent == null) continue;

            if (!ent.active || ent.locked || !ent.solid || !ent.collidesWithMap) continue;

            ent.velocity.add(gravity);
            ent.velocity.limit(10);
            ent.velocity.decrease(ent.resistance);
            ent.position.add(ent.velocity);

            for (Hitbox h : ent.touchHitbox.touchingMapTiles(map))
                if (h != null ) ent.colliderHitbox.resolveCollision(h, ent.velocity, ent.position);

            ent.touchHitbox.update(ent.position);
            ent.colliderHitbox.update(ent.position);
        }
    }
}
