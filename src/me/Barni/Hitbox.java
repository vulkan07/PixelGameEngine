package me.Barni;

public class Hitbox {

    public int x, y, w, h;

    public Hitbox(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w; //width
        this.h = h; //height
    }

    private boolean AABB(Hitbox other) {
        if (x >= other.x && x <= other.w + other.x || x + w >= other.x && x + w <= other.w + other.x)
            if (y >= other.y && y <= other.h + other.y || y + h >= other.y && y + h <= other.h + other.y)
                return true;
        return false;
    }

    public void resolveCollision(Hitbox other, Vec2D position) {
        boolean top, down, left, right;
        top   = false;
        down  = false;
        left  = false;
        right = false;

        if (x >= other.x && x <= other.w + other.x)
        {
            //Jobbról
            right = true;
        }
        if (x + w >= other.x && x + w <= other.w + other.x)
        {
            //Balról
            //position.x = position.x > 0 ? 0 : position.x;
            left = true;
        }
        if (y >= other.y && y <= other.h + other.y)
        {
            //Alulról
            //position.y = position.y < 0 ? 0 : position.y;
            down = true;
        }
        if (y + h >= other.y && y + h <= other.h + other.y)
        {
            //Fentről
            //position.y = position.y > 0 ? 0 : position.y;
            top = true;
        }

    }

    public boolean isColliding(Hitbox other) {
        return AABB(other) || other.AABB(this);
    }

    public void move(Vec2D m) {
        x += (int) m.x;
        y += (int) m.y;
    }

    public Hitbox[] touchingMapTiles(Map map) {
        Hitbox[] out = new Hitbox[16];
        Hitbox other = new Hitbox(0, 0, map.tileSize, map.tileSize);

        for (int i = 0; i < map.tiles.length; i++) {
            if (map.tiles[i] == 0) continue; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            other.y = i / map.width * map.tileSize;
            other.x = i % map.width * map.tileSize;
            //System.out.print("> " + other.x + ", "+ other.y);
            if (isColliding(other)) {
                //System.out.print(" -> found");
                for (int j = 0; j < out.length; j++) {
                    if (out[j] == null) {
                        out[j] = new Hitbox(other.x, other.y, map.tileSize, map.tileSize);
                        break;
                    }
                }
            }
            //System.out.println();
        }
        return out;
    }
}


/*
        other.w = map.tileSize;
        other.h = map.tileSize;
        for (int i = 0; i < map.tiles.length; i++) {
            other.y = (i / map.width) * map.tileSize; //Y
            other.x = (i % map.width) * map.tileSize; //X
            if (isColliding(other))
                for (int j = 0; j < out.length; j++)
                    if (out[j] == null) {
                        out[j] = other;
                        break;
                    }
        }
 */