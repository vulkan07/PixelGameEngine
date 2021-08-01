package me.Barni;

public class Physics {

    Game game;
    public Vec2D gravity = new Vec2D(0, .5f);
    public float def_speed = .8f, def_resistance = .4f;
    public Map map;
    public int boundX, boundY;

    public Physics(Game g, Map m) {
        game = g;
        map = m;
        boundX = m.width * m.tileSize;
        boundY = m.height * m.tileSize;
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
            if (!ent.active || !ent.collidesWithMap) continue;

            //Resolve collision: Entity VS Entity
            //TODO optimize this
            for (Entity other : map.entities) {
                if (other == null) continue;
                if (other == ent) continue;

                if (ent.colliderHitbox.isColliding(other.colliderHitbox)) {
                    ent.onTouch(other);
                    if (ent.active && !ent.locked && ent.solid && ent.alive && ent.collidesWithMap)
                        if (other.active && !other.locked && other.solid && other.alive && other.collidesWithMap)
                        ent.colliderHitbox.resolveCollision(other, other.colliderHitbox, ent.velocity, ent.position);
                }
            }

            if (ent.locked || !ent.solid || !ent.alive) continue;

            ent.velocity.add(gravity);
            ent.velocity.limit(20);
            ent.velocity.decrease(ent.resistance);
            ent.position.add(ent.velocity);


            //Resolve collision: Entity VS map
            Hitbox[] hList = ent.touchHitbox.touchingMapTiles(map);
            for (int i = 0; i < hList.length - 1; i++) {

                ent.position.lowLimit(0);
                if (ent.position.x + ent.colliderHitbox.w > boundX)
                    ent.position.x = boundX - ent.colliderHitbox.w;
                if (ent.position.y > boundY)
                    ent.die(120);

                //if (ent.touchHitbox.isCollidingWithAny(hList))
                if (hList[i] != null)
                    ent.colliderHitbox.resolveCollision(ent, hList[i], ent.velocity, ent.position);
            }


            ent.touchHitbox.update(ent.position);
            ent.colliderHitbox.update(ent.position);
        }
    }
}
