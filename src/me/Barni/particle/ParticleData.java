package me.Barni.particle;

import me.Barni.physics.Vec2D;

    /**This class is used as a struct, to hold particle data**/
public class ParticleData {


    public int max_particles = 64;
    public boolean emitting = true;
    public int lifespanMin = 40;
    public int lifespanMax = 80;
    public int noise = 1;
    public Vec2D gravity = new Vec2D(0, 0.1f);
    public Vec2D moveForceMin = new Vec2D(-4 , -4);
    public Vec2D moveForceMax = new Vec2D(4 , 4);
}
