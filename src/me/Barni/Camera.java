package me.Barni;

import me.Barni.physics.Hitbox;
import me.Barni.physics.Vec2D;

public class Camera {

    public Vec2D scroll, view;
    private Hitbox visibleArea;
    private Vec2D target;
    private Game game;
    Map map;
    float lerp = .05f;
    float zoom = 1f;

    public Camera(Game game, Map map) {
        this.scroll = new Vec2D(0, 0);
        this.target = new Vec2D(0, 0);
        this.view = new Vec2D(0, 0);
        this.game = game;
        this.map = map;
        visibleArea = new Hitbox(0,0, game.WIDTH, game.HEIGHT);
    }

    public void update() {
        scroll = scroll.lerp(target, lerp); //lerp animation
        //scroll.lowLimit(0); //Crop view for top and left
        view.x = scroll.x + game.WIDTH / 2;
        view.y = scroll.y + game.HEIGHT / 2;
        visibleArea.update(scroll);
    }

    public void move(Vec2D move) {
        scroll.add(move);
    }

    public void lookAt(Vec2D target) {
        this.target.x = target.x - game.WIDTH / 2;
        this.target.y = target.y - game.HEIGHT / 2;
    }
}
