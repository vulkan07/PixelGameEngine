package me.Barni.physics;

import me.Barni.Map;
import me.Barni.Material;
import me.Barni.entity.childs.Player;
import me.Barni.entity.Entity;

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

    private boolean smallAABB(Hitbox other, int smaller) {
        if (!(other.realH - smaller < y || y + h - smaller < other.y))
            if (!(other.realW - smaller < x || x + w - smaller < other.x))
                return true;
        return false;
    }

    void handleTileCollisionForEntities(Entity ent, Hitbox other)
    {

    }

    public boolean resolveTileVSEntityCollision(Entity ent, Hitbox other) {

        if (other.solidType == 0)
            return true;

        boolean touching = smallAABB(other, 1);

        if (other.solidType == 2) {
            if (touching)
                ent.velocity.limit(2);
            return true;
        }

        if (other.solidType == 3) {
            if (ent instanceof Player)
                if (((Player) ent).getLevel() > 2)
                    touching = smallAABB(other, 10);
            if (touching)
                ent.die(200); //120
            return true;
        }

        //IN X ZONE
        if (!(other.realW <= x || realW <= other.x)) {

            //FROM BOTTOM
            if (y <= other.realH && y > other.y) {
                if (ent.velocity.y < 0) {
                    ent.velocity.y = 0;
                    ent.position.y = other.realH;
                    return true;
                }
            }

            //FROM TOP
            if (realH >= other.y && realH < other.realH) {
                if (ent.velocity.y > 0) {
                    if (ent instanceof Player) ((Player) ent).setCanJump(true);
                    ent.velocity.y = 0;
                    ent.position.y = other.y - other.h + (other.h - h);
                    return true;
                }
            }
        }

        //IN Y ZONE
        if (!(other.realH <= y || realH <= other.y)) {

            //FROM LEFT
            if (realW >= other.x && realW < other.realW) {
                if (ent.velocity.x > 0) {
                    ent.velocity.x = 0;
                    ent.position.x = other.x - other.w + (other.w - w);
                    return true;
                }
            }

            //FROM RIGHT
            if (x <= other.realW && x > other.x) {
                if (ent.velocity.x < 0) {
                    ent.velocity.x = 0;
                    ent.position.x = other.realW;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isColliding(Hitbox other) {
        return smallAABB(other, 1) || other.smallAABB(this, 1);
    }

    public boolean isCollidingWithAny(Hitbox[] others) {
        for (Hitbox h : others)
            if (h != null)
                if (AABB(h))
                    return true;
        return false;
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