package me.Barni.graphics;

import me.Barni.Camera;
import me.Barni.Game;
import me.Barni.texture.Texture;
import org.lwjgl.opengl.GL30;

import java.awt.*;

import static me.Barni.graphics.GraphicsUtils.QUAD_ELEMENT_ARRAY;


public class RenderableText {
    private String text;
    private int x, y, size;
    private Color color;
    private Texture t;
    private boolean outdatedTexture;

    private static Game game;
    private VertexArrayObject vao;

    public RenderableText(String text, int x, int y) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.size = 10;
        this.color = Color.WHITE;
        initGraphics();
    }

    public RenderableText(String text, int x, int y, int size, Color color) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = color;
        initGraphics();
    }

    public static void init(Game g) {
        game = g;
    }

    //Constructors call this
    private void initGraphics() {
        vao = new VertexArrayObject();
        float[] vArray = new float[8];
        vao.setVertexData(vArray);
        vao.setElementData(QUAD_ELEMENT_ARRAY);
        vao.addAttributePointer(2); //Position (x,y)
        vao.addAttributePointer(2); //TX coords (u,v)
        t = new Texture();
        outdatedTexture = true;
    }

    public void render(Camera cam) {
        if (outdatedTexture) {
            outdatedTexture = false;
            t = TextRenderer.renderText(text, color, size * TextRenderer.RENDER_QUALITY_MULT);
        }
        vao.bind(false);
        vao.setVertexData(GraphicsUtils.generateVertexArray(x, y, t.getWidth() / TextRenderer.RENDER_QUALITY_MULT, t.getHeight() / TextRenderer.RENDER_QUALITY_MULT));

        TextRenderer.textShader.bind();
        TextRenderer.textShader.uploadMat4("uProjMat", cam.getProjMat());
        TextRenderer.textShader.uploadMat4("uViewMat", cam.getViewMat());
        TextRenderer.textShader.uploadFloat("uAlpha", game.getScreenFadeAlphaNormalized());
        //TextRenderer.textShader.selectTextureSlot("uTexSampler", 1);
        t.bind();
        GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexLen(), GL30.GL_UNSIGNED_INT, 0);
        vao.unBind();
        TextRenderer.textShader.unBind();
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        outdatedTexture = true;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        outdatedTexture = true;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        outdatedTexture = true;
    }
}
