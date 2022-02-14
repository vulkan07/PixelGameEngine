package me.Barni.graphics;

import me.Barni.Game;

public class GraphicsDraw {

    public static final int[] ELEMENT_ARRAY = {2, 1, 0, 0, 1, 3};

    public static ShaderProgram shader;
    public static VertexArrayObject ebo;

    public static void init(Game game)
    {
        shader = new ShaderProgram(game);
        shader.create("draw");
        shader.link();
    }
    public static void fillRect(float x, float y, float w, float h)
    {

    }
}
