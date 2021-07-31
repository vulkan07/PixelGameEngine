package me.Barni;

public class Hitbox {

    public int x, y, w, h, realW, realH, offsX, offsY, solidType;

    public Hitbox(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w; //width
        this.h = h; //height
        this.realW = x + w;
        this.realH = y + h;
        this.solidType = 1;
    }

    public Hitbox(int x, int y, int xOffs, int yOffs, int w, int h) {
        this.x = x;
        this.y = y;
        this.offsX = xOffs;
        this.offsY = yOffs;
        this.w = w; //width
        this.h = h; //height
        this.realW = x + w;
        this.realH = y + h;
        this.solidType = 1;
    }

    private boolean AABB(Hitbox other) {
        if (!(other.realH < y || y + h < other.y))
            if (!(other.realW < x || x + w < other.x))
                return true;
        return false;
    }
    //if (x >= other.x && x <= other.w + other.x || x + w >= other.x && x + w <= other.w + other.x)
    //    if (y >= other.y && y <= other.h + other.y || y + h >= other.y && y + h <= other.h + other.y)
    //        return true;


    public boolean resolveCollision(Entity ent, Hitbox other, Vec2D velocity, Vec2D pos) {

        if (other.solidType == 0)
            return true;

        boolean touching = AABB(other);
        if (other.solidType == 2 && touching) {
            velocity.limit(1);
            return true;
        }

        if (other.solidType == 3 && touching) {
            ent.die(120);
            return true;
        }

        //IN X ZONE
        if (!(other.realW <= x || realW <= other.x)) {

            //FROM BOTTOM
            if (y <= other.realH && y > other.y) {
                if (velocity.y < 0) {
                    velocity.y = 0;
                    pos.y = other.realH;
                    return true;
                }
            }

            //FROM TOP
            if (realH >= other.y && realH < other.realH) {
                if (velocity.y > 0) {
                    ((Player)ent).canJump = true;
                    velocity.y = 0;
                    pos.y = other.y - other.h + (other.h - h);
                    return true;
                }
            }
        }

        //IN Y ZONE
        if (!(other.realH <= y || realH <= other.y)) {

            //FROM LEFT
            if (realW >= other.x && realW < other.realW) {
                if (velocity.x > 0) {
                    velocity.x = 0;
                    pos.x = other.x - other.w + (other.w - w);
                    return true;
                }
            }

            //FROM RIGHT
            if (x <= other.realW && x > other.x) {
                if (velocity.x < 0) {
                    velocity.x = 0;
                    pos.x = other.realW;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isColliding(Hitbox other) {
        return AABB(other) || other.AABB(this);
    }

    public boolean isCollidingWithAny(Hitbox[] others) {
        for (Hitbox h : others)
            if (h != null)
                if (AABB(h))
                    return true;
        return false;
    }

    //OLD BECAUSE DOESN'T HANDLE OFFSET
    private void move(Vec2D m) {
        x += (int) m.x;
        y += (int) m.y;
        realW += (int) m.x;
        realH += (int) m.y;
    }

    public void update(Vec2D newPos) {
        this.x = (int) newPos.x + offsX;
        this.y = (int) newPos.y + offsY;
        this.realW = x + w;
        this.realH = y + h;
    }

    public Hitbox[] touchingMapTiles(Map map) {
        Hitbox[] out = new Hitbox[24];
        Hitbox other = new Hitbox(0, 0, map.tileSize, map.tileSize);

        for (int i = 0; i < map.getTilesLength(); i++) {
            //if (Material.solid[map.tiles[i]] == 0)
            //    continue; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! DISABLE TEST NOT SOLID TILES!!
            //HANDLED IN resolveCollision()

            other.x = i % map.width * map.tileSize;
            other.y = i / map.width * map.tileSize;
            if (isColliding(other)) {
                for (int j = 0; j < out.length; j++) {
                    if (out[j] == null) {
                        out[j] = new Hitbox(other.x, other.y, map.tileSize, map.tileSize);
                        out[j].solidType = Material.solid[map.getTile(i)];
                        break;
                    }
                }
            }
        }
        return out;
    }
}