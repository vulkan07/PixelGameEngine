package me.Barni.graphics;

import me.Barni.Game;
import me.Barni.physics.Vec2D;
import me.Barni.texture.Texture;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;


public class QuadBatch {
    private static Game game;
    private static ShaderProgram rectShader;
    private VertexArrayObject vao;
    private Texture t;
    private Vector4f tint = new Vector4f(1,1,1,1);

    public QuadBatch(float[] positions, float[] texCoords) {
        initGraphics();
        updateData(positions, texCoords);
    }
    public QuadBatch() {
        initGraphics();
        updateData(new float[]{},new float[]{});
    }

    public void updateData(float[] positions, float[] texCoords){
        vao.setVertexData(GraphicsUtils.generateBatchVertexArray(positions, texCoords));
        vao.setElementData(GraphicsUtils.getQuadElementArray(positions.length/4));
    }
    public void setRawData(float[] vertexArray, int numElements){
        vao.setVertexData(vertexArray);
        vao.setElementData(GraphicsUtils.getQuadElementArray(numElements));
    }

    public static void init(Game g) {
        game = g;
        rectShader = Quad.getRectShader();
    }

    private void initGraphics() {
        t = new Texture();

        vao = new VertexArrayObject();
        vao.setVertexData(new float[1]);
        vao.setElementData(new int[1]);
        vao.addAttributePointer(2, "pos"); //Position (x,y)
        vao.addAttributePointer(2, "tex"); //TX coords (u,v)
    }

    public VertexArrayObject getVertexArrayObject() {
        return vao;
    }

    public void loadTexture(String name) {
        t.loadTexture(name);
        t.uploadImageToGPU(0);
    }
    public void setTexture(Texture t) {
        this.t = t;
        t.uploadImageToGPU(0);
    }

    public void render(ShaderProgram shOverride) {
        if (shOverride == null)
            shOverride = rectShader;

        vao.bind(false);
        shOverride.bind();
        shOverride.uploadMat4("uProjMat", game.getMap().getCamera().getDefaultProjMat());
        shOverride.uploadMat4("uViewMat", game.getMap().getCamera().getDefaultViewMat());
        shOverride.uploadFloat("uAlpha", game.getScreenFadeAlphaNormalized());
        shOverride.uploadVec4("tint", tint);
        t.bind();
        GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexLen(), GL30.GL_UNSIGNED_INT, 0);
        vao.unBind();
        shOverride.unBind();
    }

    public void setTint(Vector4f tint) {
        this.tint = tint;
    }

    public Texture getTexture() {
        return t;
    }
}
