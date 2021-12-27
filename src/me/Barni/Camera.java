package me.Barni;

import me.Barni.entity.Entity;
import me.Barni.physics.Hitbox;
import me.Barni.physics.Vec2D;

public class Camera {

    public Vec2D scroll, view;
    private Hitbox visibleArea;
    private Vec2D vecTarget;
    private Game game;
    Map map;
    public float lerp = .05f;
    public final float DEFAULT_LERP = .05f;
    private float zoom = 1f; //TODO
    public Entity followEntity;
    public int followDistTreshold = 50;

    public Camera(Game game, Map map) {
        this.scroll = new Vec2D(0, 0);
        this.vecTarget = new Vec2D(0, 0);
        this.view = new Vec2D(0, 0);
        this.game = game;
        this.map = map;
        visibleArea = new Hitbox(0,0, game.getWIDTH(), game.getHEIGHT());
    }

    public void update() {
        if (followEntity != null)
            if (followEntity.position.dist(view) >= followDistTreshold)
                lookAt(followEntity.position.copy().sub(followEntity.size.copy().div(2)));
        scroll = scroll.lerp(vecTarget, lerp); //lerp animation
        //scroll.lowLimit(0); //Crop view for top and left
        view.x = scroll.x + game.getWIDTH() / 2;
        view.y = scroll.y + game.getHEIGHT() / 2;
        visibleArea.update(scroll);
    }

    public void move(Vec2D move) {
        scroll.add(move);
    }

    public void lookAt(Vec2D target) {
        this.vecTarget.x = target.x - game.getWIDTH() / 2;
        this.vecTarget.y = target.y - game.getHEIGHT() / 2;
    }
}
