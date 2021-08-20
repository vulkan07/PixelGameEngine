package me.Barni.particle.render;

import me.Barni.Camera;
import me.Barni.particle.Particle;
import me.Barni.entity.childs.ParticleEmitter;

import java.awt.image.BufferedImage;

public interface ParticleRenderer {

    void init(ParticleEmitter pem);

    void renderParticles(BufferedImage img, Camera cam, Particle[] particles);
}
