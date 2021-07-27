package me.Barni;

public class Hitbox {

    public int x, y, w, h, realW, realH, offsX, offsY;

    public Hitbox(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w; //width
        this.h = h; //height
        this.realW = x + w;
        this.realH = y + h;
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


    public boolean resolveCollision(Hitbox other, Vec2D velocity, Vec2D pos) {

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

        for (int i = 0; i < map.tiles.length; i++) {
            if (map.tiles[i] == 0 || map.tiles[i] == 3)
                continue; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! DISABLE TEST ON VOID (and grass) TILES!!
            other.x = i % map.width * map.tileSize;
            other.y = i / map.width * map.tileSize;
            if (isColliding(other)) {
                for (int j = 0; j < out.length; j++) {
                    if (out[j] == null) {
                        out[j] = new Hitbox(other.x, other.y, map.tileSize, map.tileSize);
                        break;
                    }
                }
            }
        }
        return out;
    }
}