package me.Barni.graphics;

import me.Barni.Game;
import me.Barni.physics.Vec2D;
import me.Barni.texture.Texture;
import org.lwjgl.opengl.GL30;

import static me.Barni.graphics.GraphicsUtils.QUAD_ELEMENT_ARRAY;

public class Quad {
    private static Game game;
    private static ShaderProgram rectShader;
    private VertexArrayObject vao;
    private Texture t;
    private Vec2D pos, size;

    public Quad(float x, float y, float w, float h) {
        pos = new Vec2D(x,y);
        size = new Vec2D(w,h);
        initGraphics();
    }

    public Quad(Vec2D pos, Vec2D size) {
        this.pos = pos;
        this.size = size;
        initGraphics();
    }

    public static void init(Game g) {
        game = g;
        rectShader = new ShaderProgram(game);
        rectShader.create("gui_rect");
    }

    private void initGraphics() {
        t = new Texture();

        vao = new VertexArrayObject();
        float[] vArray = new float[8];
        vao.setVertexData(vArray);
        vao.setElementData(QUAD_ELEMENT_ARRAY);
        vao.addAttributePointer(2, "pos"); //Position (x,y)
        vao.addAttributePointer(2, "tex"); //TX coords (u,v)
    }

    public void loadTexture(String name) {
        t.loadTexture(name, size.xi(), size.yi());
        t.uploadImageToGPU(0);
    }
    public void setTexture(Texture t) {
        this.t = t;
        t.uploadImageToGPU(0);
    }

    public void render(ShaderProgram sh) {
        vao.bind(false);
        vao.setVertexData(GraphicsUtils.generateVertexArray(pos.x, pos.y, size.x, size.y));
        rectShader.bind();
        rectShader.uploadMat4("uProjMat", game.getMap().getCamera().getDefaultProjMat());
        rectShader.uploadFloat("uAlpha", game.getScreenFadeAlphaNormalized());
        t.bind();
        GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexLen(), GL30.GL_UNSIGNED_INT, 0);
        vao.unBind();
        rectShader.unBind();
    }
}
