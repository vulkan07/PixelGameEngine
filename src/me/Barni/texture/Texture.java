package me.Barni.texture;

import me.Barni.Game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Texture {

    Game game;

    private boolean animated, hasAnimation;
    public BufferedImage[] textures;
    private int width;
    private int height;
    private String generalPathName;
    private AnimSequence[] sequences;
    int currSequence, frameCount;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private String path;
    public static final String TEXTURE_BONUS_PATH = "textures\\";

    public String getPath() {
        return path;
    }

    public void loadTexture(Game g, String relativePath, int w, int h, boolean isAnimated) {
        game = g;
        width = w;
        height = h;
        BufferedImage fullImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        this.animated = isAnimated;

        path = relativePath;
        String imgPath = path + ".png";
        String dataPath = path + ".anim";

        sequences = null;
        generalPathName = game.GAME_DIR + TEXTURE_BONUS_PATH;

        File dFile = new File(game.GAME_DIR + TEXTURE_BONUS_PATH + dataPath);
        if (dFile.exists()) {
            sequences = AnimSequenceLoader.loadSequences(game.getLogger(), game.GAME_DIR + TEXTURE_BONUS_PATH + dataPath, this);
            hasAnimation = true;
            animated = true;
        }

        if (sequences == null) {
            animated = false;
            hasAnimation = false;
        }


        //READ IMAGE
        try {
            fullImg = ImageIO.read(new File(game.GAME_DIR + TEXTURE_BONUS_PATH + imgPath));
        } catch (IOException e) {
            errMsg("Can't read file!");
        }

        //CHOP TEXTURES
        textures = new BufferedImage[1];
        textures[0] = fullImg;

        try {
            if (hasAnimation) {
                textures = new BufferedImage[frameCount];
                for (int i = 0; i < frameCount; i++) {

                    int[] px = fullImg.getRGB(width * i, 0, w, h, null, 0, w * h);
                    BufferedImage img = new BufferedImage(fullImg.getWidth() / frameCount, h, BufferedImage.TYPE_INT_ARGB);
                    img.setRGB(0, 0, w, h, px, 0, w * h);

                    textures[i] = img;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            hasAnimation = false;
            sequences = null;
            frameCount = 1;
            animated = false;
            errMsg("Can't chop texture frames!");
        }
    }

    private void errMsg(String msg) {
        game.getLogger().err("[TEXTURE] " + msg + " \n" + game.getLogger().getIndentStr() + "\n    At: " + generalPathName);
    }

    public void update() {
        if (animated) {
            if (sequences[currSequence].isEnded()) {

                String n = sequences[currSequence].nextName;
                if (setAnimationSequence(n))
                    return;

                game.getLogger().err("[TEXTURE] Can't find sequence \"" + n + "\"!");
                animated = false;
                setCurrentFrame(0);
                return;
            }
            sequences[currSequence].update();
        }
    }


    public boolean setAnimationSequence(String seqName) {

        for (int i = 0; i < sequences.length; i++) {
            if (sequences[i].name.equals(seqName)) {
                currSequence = i;
                sequences[i].reset();
                return true;
            }
        }
        errMsg("There's no animation sequence: " + seqName);
        return false;
    }

    public void setCurrentFrame(int frame) {
        if (sequences == null) {
            errMsg("No sequences loaded!");
            return;
        }
        sequences[currSequence].setCurrentFrame(frame);
    }

    public AnimSequence getAnimationSequence(String seqName) {
        for (int i = 0; i < sequences.length; i++) {
            if (sequences[i].name.equals(seqName)) {
                currSequence = i;
                return sequences[i];
            }
        }
        errMsg("There's no animation sequence: " + seqName);
        return null;
    }

    public void setAnimated(boolean animated) {
        if (hasAnimation)
            this.animated = animated;
    }

    public boolean isAnimated() {
        return this.animated;
    }

    public BufferedImage getTexture() {
        if (hasAnimation) {
            return textures[sequences[currSequence].getCurrentFrame()];
        } else
            return textures[0];
    }

}
