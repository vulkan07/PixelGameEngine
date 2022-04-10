package me.Barni;

import me.Barni.graphics.GraphicsUtils;
import me.Barni.graphics.ShaderProgram;
import me.Barni.graphics.VertexArrayObject;
import me.Barni.texture.Texture;
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Decorative {
    Game game;
    public Texture texture;
    public float x, y;
    public int z, w, h;
    public int id;
    public float parallax;
    public boolean selected;
    //Z = -1 : behind map
    //Z =  0 : before map
    //Z =  1 : before entities

    public Decorative(Game g, float x, float y, int zPlane, float parallax, int w, int h, String path) {
        game = g;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.z = zPlane;
        this.parallax = Math.abs(parallax);
        texture = new Texture();
        texture.loadTexture(game, path, w, h, true);
        texture.uploadImageToGPU(0);
    }

    public void tick() {
        texture.update();
    }


    public void render(VertexArrayObject vao, ShaderProgram shader) {
        if (!texture.isValid()) return;

        float[] vArray = GraphicsUtils.generateVertexArray(x, y, w, h);

        vao.setVertexData(vArray);

        shader.selectTextureSlot("uTexSampler", 0);
        shader.uploadBool("uSelected", selected);
        texture.bind();
        GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexLen(), GL30.GL_UNSIGNED_INT, 0);
        texture.unBind();
    }

    public void renderDebug(Graphics g, Camera cam, boolean selected) {/*
        g.setColor(Color.GREEN);
        g.drawRect(x - cam.scroll.xi(), y - cam.scroll.yi(), w, h);
        if (selected) {
            g.setColor(Color.WHITE);
            g.drawRect(x - cam.scroll.xi() - 2, y - cam.scroll.yi() - 2, w + 4, h + 4);
        }
        g.drawString((texture.getPath() + "(#" + id + ")"), x - cam.scroll.xi(), y - cam.scroll.yi() - 10);
    */}
}
