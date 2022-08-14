package me.Barni.graphics;

import me.Barni.Game;
import me.Barni.texture.Texture;

public class FontManager {

    private static Game game;

    private Texture txt;
    private int numCols, numRows, cellWidth, cellHeight, startCharID;
    private static QuadBatch batch;

    public static void init(Game g) {
        game = g;
        batch = new QuadBatch(new float[]{}, new float[]{});
        batch.loadTexture("fonts/consolas");
    }

    public static void renderTEST() {
        batch.render(null);
    }

    //Charset used in the texture is ISO-8859-1



}
