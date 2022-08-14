package me.Barni.entity.childs;

import me.Barni.Game;
import me.Barni.entity.Entity;
import me.Barni.physics.Hitbox;
import me.Barni.physics.Vec2D;
import org.json.JSONObject;


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
        visible = false;
        colliderHitbox = new Hitbox(
                (int) position.x,
                (int) position.y,
                (int) size.x,
                (int) size.y);
    }

    @Override
    public void onTouch(Entity ent) {
        if (triggered) return;
        if (!(ent instanceof Player)) return;
        triggered = true;
        game.fadeOutScreen(0);
    }

    @Override
    public void tick() {
        if (triggered && game.getScreenFadeAlpha() == 255)
            game.loadNewMap(game.MAP_DIR + nextMap + ".map");
    }


    @Override
    public JSONObject serialize() {
        JSONObject jobj = super.serialize();
        jobj.put("nextLevel", nextMap);
        return jobj;
    }
    @Override
    public void deserialize(JSONObject jobj)
    {
        nextMap = jobj.getString("nextLevel");
    }
}
