package me.Barni;

public class Physics {

    Game game;
    public Vec2D gravity = new Vec2D(0, .4f);
    public float def_speed = .8f, def_resistance = .4f;
    public Map map;
    public int boundX, boundY;

    public Physics(Game g, Map m) {
        game = g;
        map = m;
        boundX = m.width*m.tileSize;
        boundY = m.height*m.tileSize;
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

            //Resolve collision: Entity VS Entity
            //TODO optimize this
            for (Entity other : map.entities)
            {
                if (other == null) continue;
                if (other == ent) continue;
                if (ent.touchHitbox.isColliding(other.touchHitbox))
                    ent.colliderHitbox.resolveCollision(other.colliderHitbox, ent.velocity, ent.position);
            }

            //Resolve collision: Entity VS map
            for (Hitbox h : ent.touchHitbox.touchingMapTiles(map)) {
                ent.position.lowLimit(0);
                if (ent.position.x + ent.colliderHitbox.w > boundX)
                    ent.position.x = boundX - ent.colliderHitbox.w;
                if (ent.position.y > boundY)
                    ent.velocity.y -= 500;


                if (h != null ) ent.colliderHitbox.resolveCollision(h, ent.velocity, ent.position);
            }


            ent.touchHitbox.update(ent.position);
            ent.colliderHitbox.update(ent.position);
        }
    }
}
