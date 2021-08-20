package me.Barni.particle;

import me.Barni.physics.Vec2D;

public class Particle {

    public Vec2D pos, force, gravity;
    public int lifetime, drag;

    public Particle(Vec2D pos, Vec2D force, Vec2D gravity,  int lifetime) {
        this.pos = pos;
        this.force = force;
        this.gravity = gravity;
        this.drag = 0;
        this.lifetime = lifetime;
    }

    public void tick()
    {
        pos.add(force);
        force.add(gravity);
        force.decrease(0.05f);
        lifetime--;
    }
}
