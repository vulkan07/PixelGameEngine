package me.Barni;

import java.awt.image.BufferedImage;

public class TextureAtlas {
    Game game;
    private Texture[] atlas;
    public final int ATLAS_SIZE;

    public TextureAtlas(Game g, int size, int texture_size) {
        ATLAS_SIZE = size;
        //TEXTURE_SIZE = texture_size;
        atlas = new Texture[ATLAS_SIZE];
        game = g;
        game.logger.info("[ATLAS] Initialized texture atlas");
    }

    public BufferedImage getTexture(int i) {
        if (atlas[i] == null) return null;
        return atlas[i].getTexture();
    }


    public void update() {
        for (Texture t : atlas) {
            if (t != null)
                if (t.isAnimated())
                    t.update();
        }
    }

    public int addTexture(Texture t) {
        for (int i = 0; i < ATLAS_SIZE; i++)
            if (atlas[i] == null) {
                atlas[i] = t;
                return i;
            }
        game.logger.err("[ATLAS] Texture atlas is full! Can't add more");
        return -1;
    }

}
