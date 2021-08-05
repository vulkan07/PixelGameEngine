package me.Barni;

public class Checkpoint extends Entity {

    private boolean reached = false;

    public Checkpoint(Game g, String name, Vec2D pos) {
        super(g, name, pos, new Vec2D(32, 64));
        colliderHitbox.x = position.xi();
        colliderHitbox.y = position.yi();
        colliderHitbox.w = size.xi();
        colliderHitbox.h = size.yi();
        loadTexture("check_point_off");
    }

    @Override
    public void onTouch(Entity other) {
        if (reached) return;
        if (other.name.equals("player")) {
            if (((Player) other).getLevel() > 1) {
                reached = true;
                loadTexture("check_point");
                ((Player) other).spawnLocation = position.copy();
            }
        }
    }
}
