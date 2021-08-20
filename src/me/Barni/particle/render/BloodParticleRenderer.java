package me.Barni.particle.render;

import me.Barni.Camera;
import me.Barni.particle.Particle;
import me.Barni.entity.childs.ParticleEmitter;
import me.Barni.physics.Vec2D;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BloodParticleRenderer implements ParticleRenderer {

    private ParticleEmitter pem;

    @Override
    public void init(ParticleEmitter pem) {
        this.pem = pem;
    }

    @Override
    public void renderParticles(BufferedImage img, Camera cam, Particle[] particles) {
        for (Particle pt : particles) {
            if (pt == null) continue;
            Graphics g = img.getGraphics();
            g.setColor(new Color(255, 0, 0, 100));
            int pSize = (int) Vec2D.remap(pt.lifetime / 2, 0, pem.pData.lifespanMax, 1, 16);
            g.fillOval(pt.pos.xi() - cam.scroll.xi(), pt.pos.yi() - cam.scroll.yi(), pSize, pSize);
        }
    }

}
