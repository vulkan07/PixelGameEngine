package me.Barni;

public class LevelExit extends Entity {


    private String nextMap;
    private boolean triggered;

    public String getNextMap() {
        return nextMap;
    }

    public void setNextMap(String nextMap) {
        this.nextMap = nextMap;
    }

    public LevelExit(Game g, String name, Vec2D pos, String nextMap) {
        super(g, name, pos);
        this.nextMap = nextMap;
        colliderHitbox = new Hitbox(
                (int) position.x,
                (int) position.y,
                (int) size.x,
                (int) size.y);
    }

    @Override
    public void onTouch(Entity ent) {
        if (!(ent instanceof Player)) return;
        triggered = true;
        game.screenFadingOut = true;
    }

    @Override
    public void tick()
    {
        if (triggered && game.blankAlpha == 255)
            game.loadNewMap(nextMap);
    }
}
