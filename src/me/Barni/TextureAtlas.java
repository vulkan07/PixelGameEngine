package me.Barni;

import java.awt.image.BufferedImage;

public class TextureAtlas {
    Game game;
    private BufferedImage[] atlas;
    public final int ATLAS_SIZE;

    public TextureAtlas(Game g, int size, int texture_size) {
        ATLAS_SIZE = size;
        //TEXTURE_SIZE = texture_size;
        atlas = new BufferedImage[ATLAS_SIZE];
        game = g;
        game.logger.info("[ATLAS] Initialized texture atlas");
    }

    public BufferedImage getTexture(int i) {
        return atlas[i];
    }


    public int addTexture(BufferedImage bi) {
        for (int i = 0; i < ATLAS_SIZE; i++)
            if (atlas[i] == null) {
                atlas[i] = bi;
                return i;
            }
        game.logger.err("[ATLAS] Texture atlas is full! Can't add more");
        return -1;
    }

}
