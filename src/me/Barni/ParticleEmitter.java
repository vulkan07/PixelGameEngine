package me.Barni;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ParticleEmitter extends Entity {
    private final Random r;
    private final Particle[] particles;
    private int max_particles;
    public boolean emitting;
    public int lifespanMin, lifespanMax;
    public int noise;
    public Vec2D gravity = new Vec2D(0, 0.1f);
    private Vec2D moveForceMin;
    private Vec2D moveForceMax;

    public ParticleEmitter(
            Game game,
            String name,
            Vec2D pos,
            Vec2D forceMin,
            Vec2D forceMax,
            boolean start_active,
            int max_particles,
            int noise,
            int lifeTimeMin,
            int lifeTimeMax) {
        super(game, name, pos);
        this.active = start_active;
        this.emitting = false;
        this.noise = noise;


        this.moveForceMin = forceMin;
        this.moveForceMax = forceMax;


        this.lifespanMin = lifeTimeMin;
        this.lifespanMax = lifeTimeMax;
        this.r = new Random();
        this.position = pos;
        this.max_particles = max_particles;
        this.particles = new Particle[max_particles];
    }

    public void createParticle(int count) {
        if (emitting) {
            for (int i = 0; i < max_particles; i++) {

                if (particles[i] != null) continue;

                particles[i] = new Particle(
                        position.copy(),
                        moveForceMin == moveForceMax ? moveForceMax.copy() : randomVector(moveForceMin, moveForceMax),
                        gravity,
                        random(lifespanMin, lifespanMax));
                count--;
                if (count == 0)
                    break;
            }
        }
    }

    private int random(int min, int max) {
        int out = r.nextInt(max);
        if (out < min) return min;
        return out;
    }

    private Vec2D randomVector(Vec2D min, Vec2D max) {
        Vec2D out = new Vec2D();
        int xHalf = max.xi() / 2;
        int yHalf = max.yi() / 2;


        out.x = r.nextInt((int) Math.abs(max.x));
        out.y = r.nextInt((int) Math.abs(max.y));

        if (out.x < min.x) out.x = min.x;
        if (out.y < min.y) out.y = min.y;

        out.x += r.nextFloat();
        out.y += r.nextFloat();

        out.x -= xHalf;
        out.y -= yHalf;

        return out;
    }


    public void tick() {
        if (!active) return;

        //ADD if possible
        createParticle(1);

        //UPDATE
        for (int i = 0; i < max_particles; i++) {

            //If empty place
            if (particles[i] == null)
                continue;

            //If dead
            if (particles[i].lifetime == 0) {
                particles[i] = null;
                continue;
            }


            //If not dead
            if (noise != 0)
                particles[i].pos.add(new Vec2D(r.nextInt(noise + 1) - noise / 2, r.nextInt(noise + 1) - noise / 2));
            particles[i].tick();
        }
    }

    public void render(BufferedImage img, Camera cam) {
        if (!visible || !active) return;

        for (Particle pt : particles) {
            if (pt == null) continue;
            Graphics g = img.getGraphics();
            g.setColor(new Color(255,0,0,100));
            int pSize = (int)Vec2D.remap(pt.lifetime/2, 0, lifespanMax, 1, 16);
            g.fillOval(pt.pos.xi() - cam.scroll.xi(), pt.pos.yi() - cam.scroll.yi(), pSize, pSize);
        }
    }

}