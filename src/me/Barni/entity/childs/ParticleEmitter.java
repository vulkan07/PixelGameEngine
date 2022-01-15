package me.Barni.entity.childs;

import me.Barni.Camera;
import me.Barni.Game;
import me.Barni.entity.Entity;
import me.Barni.particle.Particle;
import me.Barni.particle.ParticleData;
import me.Barni.particle.render.ParticleRenderer;
import me.Barni.physics.Vec2D;

import java.awt.image.BufferedImage;
import java.util.Random;

public class ParticleEmitter extends Entity {
    private final Random r;
    private final Particle[] particles;
    public ParticleData pData;
    public ParticleRenderer renderer;

    public ParticleEmitter(
            Game game,
            String name,
            Vec2D pos,
            ParticleData pData,
            ParticleRenderer renderer) {

        super(game, name, pos);
        this.pData = pData;
        this.renderer = renderer;
        renderer.init(this);

        this.active = true;
        this.collidesWithMap = false;
        this.r = new Random();
        this.position = pos;
        this.particles = new Particle[pData.max_particles];
    }

    public void createParticle(int count) {
        if (pData.emitting) {
            for (int i = 0; i < pData.max_particles; i++) {

                if (particles[i] != null) continue;

                particles[i] = new Particle(
                        position.copy(),
                        pData.moveForceMin == pData.moveForceMax ?
                                pData.moveForceMax.copy() :
                                randomVector(pData.moveForceMin, pData.moveForceMax),
                        pData.gravity,
                        random(pData.lifespanMin, pData.lifespanMax));
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
        for (int i = 0; i < pData.max_particles; i++) {

            //If empty place
            if (particles[i] == null)
                continue;

            //If dead
            if (particles[i].lifetime == 0) {
                particles[i] = null;
                continue;
            }


            //If not dead
            if (pData.noise != 0)
                particles[i].pos.add(
                        new Vec2D(
                                r.nextInt(pData.noise + 1) - pData.noise / 2,
                                r.nextInt(pData.noise + 1) - pData.noise / 2));
            particles[i].tick();
        }
    }

    public void render(BufferedImage img, Camera cam) {
        if (!visible || !active) return;

        renderer.renderParticles(img, cam, particles);
    }

}