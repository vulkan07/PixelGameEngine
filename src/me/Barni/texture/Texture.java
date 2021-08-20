package me.Barni.texture;

import me.Barni.Game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Texture {

    Game game;

    private boolean animated;
    public BufferedImage[] textures;
    private int width;
    private int height;
    private int counter;
    private int frame;

    public int getFrame() {
        return frame;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setFrame(int frame) {
        if (frame > frameCount - 1)
            frame = frameCount - 1;
        if (frame < 0)
            frame = 0;
        this.frame = frame;
    }

    private int frameCount;
    public int[] delay;
    private String path, bonusPath = "textures\\";

    public String getPath() {
        return path;
    }


    public boolean isAnimated() {
        return animated;
    }

    public void loadTexture(Game g, String relativePath, int w, int h, boolean isAnimated) {
        game = g;
        width = w;
        height = h;
        String delayStr = null;
        BufferedImage fullImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        this.animated = isAnimated;

        frame = 0;
        counter = 0;

        path = relativePath;
        String imgPath = path + ".png";
        String dataPath = path + ".anim";

        if (animated)
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(game.GAME_DIR + bonusPath + dataPath)));
                delayStr = br.readLine();
            } catch (FileNotFoundException e) {
                game.logger.subInfo("[TEXTURE] Couldn't find .anim file for " + game.GAME_DIR + bonusPath + imgPath + ", image will be stationary");
                animated = false;
            } catch (IOException e) {
                game.logger.err("[TEXTURE] Can't read " + game.GAME_DIR + bonusPath + dataPath);
                animated = false;
            }


        //READ IMAGE
        try {
            fullImg = ImageIO.read(new File(game.GAME_DIR + bonusPath + imgPath));
        } catch (IOException e) {
            game.logger.err("[TEXTURE] Can't read " + game.GAME_DIR + bonusPath + imgPath);
        }


        if (animated) {
            //SPLIT UP & SET frames TO RIGHT AMOUNT
            String[] numsStr = delayStr.split(",");
            game.logger.subInfo("[TEXTURE] .anim frame count: " + numsStr.length);
            frameCount = numsStr.length;
            delay = new int[frameCount];
            animated = true;

            //PARSE STRINGS TO NUMBERS -> delay[]
            for (int i = 0; i < numsStr.length; i++) {
                try {
                    delay[i] = Integer.parseInt(numsStr[i]);
                    //System.out.println(i + " is " + delay[i]);
                } catch (NumberFormatException nfe) {
                    game.logger.err("[TEXTURE] Invalid number format in .anim file");
                    animated = false;
                    frameCount = 1;
                }
            }

        } else {
            frameCount = 1;
            animated = false;
            delay = null;
        }

        //CHOP TEXTURES
        textures = new BufferedImage[1];
        textures[0] = fullImg;

        if (animated) {
            textures = new BufferedImage[frameCount];
            for (int i = 0; i < frameCount; i++) {

                int[] px = fullImg.getRGB(width * i, 0, w, h, null, 0, w * h);
                BufferedImage img = new BufferedImage(fullImg.getWidth() / frameCount, h, BufferedImage.TYPE_INT_ARGB);
                img.setRGB(0, 0, w, h, px, 0, w * h);

                textures[i] = img;
            }
        }
    }

    public void update() {
        if (animated) {
            counter++;
            if (counter == delay[frame]) {
                frame++;
                if (frame == textures.length) {
                    frame = 0;
                }
                counter = 0;
            }
        }
    }

    public BufferedImage getTexture() {
        return textures[this.frame];
    }

}
