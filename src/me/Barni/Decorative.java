package me.Barni;

import me.Barni.graphics.GraphicsUtils;
import me.Barni.graphics.ShaderProgram;
import me.Barni.graphics.VertexArrayObject;
import me.Barni.texture.Texture;
import org.lwjgl.opengl.GL30;

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
        texture.setNormalMap(true);
        texture.loadTexture(path, w, h);
        texture.uploadImageToGPU(0);
    }

    public void tick() {
        texture.update();
    }


    public void render(VertexArrayObject vao, ShaderProgram shader) {
        if (!texture.isValid()) return;

        float[] vArray = GraphicsUtils.generateVertexArray(x, y, w, h);

        vao.setVertexData(vArray);

        shader.uploadBool("uSelected", selected);
        shader.selectTextureSlot("uTexSampler", 0);
        texture.bind();
        if (texture.isNormalValid()) {
            shader.selectTextureSlot("uNorSampler", 1);
            texture.bindNormal();
        }
        Utils.GLClearErrors();
        GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexLen(), GL30.GL_UNSIGNED_INT, 0);
        Utils.GLCheckError();
        texture.unBind();
    }
}
