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

    //Hack
    //Do not try to understand
    private boolean AABB(Hitbox other) {
        if (!(other.realH < y || y + h < other.y))
            return !(other.realW < x || x + w < other.x);
        return false;
    }

    public boolean smallAABB(Hitbox other, int smaller) {
        if (!(other.realH - smaller < y || y + h - smaller < other.y))
            return !(other.realW - smaller < x || x + w - smaller < other.x);
        return false;
    }

    //What the utter f*ck is this
    public void resolveTileVSEntityCollision(Entity ent, Hitbox other) {

        //return if the tile is void
        if (other.solidType == 0)
            return;

        //Are they touching?
        boolean touching = smallAABB(other,1);

        //If touching, call entity's onTouchTile
        if (touching)
            if ( !ent.onTouchTile(other) ) //onTouchTile returns if collision resolution should be continued
                return;

        //If Material class says other.solidType is not solid, return
        if ( other.solidType != 1 )
            return;

        //Actual collision handling
        //IN X ZONE
        if (!(other.realW <= x || realW <= other.x)) {

            //FROM BOTTOM
            if (y <= other.realH && y > other.y) {
                if (ent.velocity.y < 0) {
                    ent.velocity.y = 0;
                    ent.position.y = other.realH;
                    return;
                }
            }
            //FROM TOP
            if (realH >= other.y && realH < other.realH) {
                if (ent.velocity.y > 0) {
                    if (ent instanceof Player) ((Player) ent).setCanJump(true);
                    ent.velocity.y = 0;
                    ent.position.y = other.y - other.h + (other.h - h);
                    return;
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
                    return;
                }
            }
            //FROM RIGHT
            if (x <= other.realW && x > other.x) {
                if (ent.velocity.x < 0) {
                    ent.velocity.x = 0;
                    ent.position.x = other.realW;
                    return;
                }
            }
        }
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
        Hitbox other = new Hitbox(0, 0, map.getTileSize(), map.getTileSize());

        for (int i = 0; i < map.getTilesLength(); i++) {
            other.x = i % map.getWidth() * map.getTileSize();
            other.y = i / map.getWidth() * map.getTileSize();
            if (isColliding(other)) {
                for (int j = 0; j < out.length; j++) {
                    if (out[j] == null) {
                        out[j] = new Hitbox(other.x, other.y, map.getTileSize(), map.getTileSize());
                        out[j].solidType = Material.isSolid(map.getTile(i).id, map.getTile(i).type);
                        break;
                    }
                }
            }
        }
        return out;
    }
}