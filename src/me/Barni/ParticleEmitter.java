package me.Barni;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

//TODO inherit from entity
//TODO use variable 'active'

public class ParticleEmitter extends Entity {
    private Random r;
    private int[][] particles;
    private int max_particles, halfNoise;
    public boolean emitting;
    public int noise, lifespan;
    private Vec2D moveForce = new Vec2D(0, 2f);

    public ParticleEmitter(Game game, String name, Vec2D pos, Vec2D force, boolean start_active, int max_particles, int noise, int lifeTimeTicks) {
        super(game, name, pos);
        this.active = start_active;
        this.emitting = false;
        this.halfNoise = noise / 2;
        this.noise = noise;

        if (noise % 2 != 0) {
            noise--;
        }

        if (force != null)
            this.moveForce = force;

        this.lifespan = lifeTimeTicks;
        this.r = new Random();
        this.position = pos;
        this.max_particles = max_particles;
        this.particles = new int[max_particles][3];
        for (int[] particle : particles)
            particle[2] = 0;
    }

    public void tick() {
        if (!active) return;

        //ADD if possible
        if (emitting) {
            for (int i = 0; i < max_particles; i++) {

                if (particles[i][2] > 0) continue;

                particles[i][0] = position.xi(); //set X
                particles[i][1] = position.yi(); //set Y
                particles[i][2] = lifespan;           //set LIFESPAN(TICKS)
                break;
            }
        }
        //UPDATE
        for (int[] particle : particles) {
            //If dead
            if (particle[2] == 0) {
                particle[2] = 0;   // set lifespan to dead (-1)
            }

            //If not dead
            if (particle[2] > 0) {
                particle[0] += (int) moveForce.x + r.nextInt(noise) - halfNoise; //update X
                particle[1] += (int) moveForce.y + r.nextInt(noise) - halfNoise; //update Y
                particle[2]--;                                                   //Decrement life
            }
        }
    }

    public void render(BufferedImage img, Camera cam) {
        if (!visible || !active) return;

        for (int[] particle : particles) {
            if (particle[2] <= 0) continue;
            Graphics g = img.getGraphics();
            g.setColor(Color.RED);
            //g.setColor(new Color(255, r.nextInt(255), r.nextInt(255)));
            g.fillRect(particle[0] - cam.scroll.xi(), particle[1]  - cam.scroll.yi(), 2,2);
        }
    }

}