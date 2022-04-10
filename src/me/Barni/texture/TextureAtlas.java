package me.Barni.texture;

import me.Barni.Game;
import me.Barni.Material;

import java.awt.image.BufferedImage;

public class TextureAtlas {
    Game game;
    private Texture[][] atlas;
    public final int ATLAS_SIZE;

    public TextureAtlas(Game g, int size, int texture_size) {
        ATLAS_SIZE = size;
        //TEXTURE_SIZE = texture_size;
        atlas = new Texture[ATLAS_SIZE][Material.getMaxTypes()];
        game = g;
        game.getLogger().info("[ATLAS] Initialized texture atlas");
    }

    public BufferedImage getImage(int i, int type) {
        if (atlas[i][type] == null) return null;
        return atlas[i][type].getTexture();
    }
    public Texture getTexture(int i, int type) {
        if (atlas[i][type] == null) return null;
        return atlas[i][type];
    }


    public void update() {
        for (Texture[] tl : atlas) {
            for (Texture t : tl) {
                if (t != null)
                    if (t.isAnimated())
                        t.update();
            }
        }
    }

    public int addTexture(Texture[] t) {
        for (int i = 0; i < ATLAS_SIZE; i++)
            if (atlas[i][0] == null) {
                atlas[i] = t;
                return i;
            }
        game.getLogger().err("[ATLAS] Texture atlas is full! Can't add more");
        return -1;
    }

    public void destroy() {
        for (Texture[] tl : atlas) {
            for (Texture t : tl) {
                if (t != null) {
                    t.destroy();
                }
            }
        }
    }
}
