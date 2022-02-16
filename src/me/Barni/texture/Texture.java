package me.Barni.texture;

import me.Barni.Game;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Texture {

    Game game;

    private boolean animated, hasAnimation;
    public BufferedImage[] textures;
    private int width;
    private int height;
    private boolean amIValid;
    private String generalPathName;
    private AnimSequence[] sequences;
    int currSequence, frameCount;

    //new
    private int id;
    //new

    private String path;
    public static final String TEXTURE_BONUS_PATH = "textures\\";

    private void errMsg(String msg) {
        game.getLogger().err("[TEXTURE] " + msg + " \n" + game.getLogger().getIndentStr() + "\n    At: " + generalPathName);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getPath() {
        return path;
    }

    public void setCurrentFrame(int frame) {
        if (sequences == null) {
            errMsg("No sequences loaded!");
            return;
        }
        sequences[currSequence].setCurrentFrame(frame);
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


    public void loadTexture(Game g, String relativePath, int w, int h, boolean isAnimated) {
        amIValid = true;
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
            errMsg("Can't read file! " + game.GAME_DIR + TEXTURE_BONUS_PATH + imgPath);
            amIValid = false;
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
            amIValid = false;
        }
    }

    public int getID() {
        return id;
    }

    public boolean isValid() {
        return amIValid;
    }

    private void generate() {
        if (id != 0)
            return;


        id = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, id);

        //Set texture default flags
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR); //Scale down
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR); //Scale up
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);      //Wrap x
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);      //Wrap y
    }
    // S,T = U,V = X,Y

    public void setGLTexParameter(int param, int value) {
        if (!amIValid) return;
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, id);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, param, value); //Scale down
    }

    public void bind() {
        if (!amIValid) return;
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, id);
    }

    public void unBind() {
        if (!amIValid) return;
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
    }

    private static byte[] intARGBtoByteRGBA(int[] argb) {
        byte[] rgba = new byte[argb.length * 4];

        for (int i = 0; i < argb.length; i++) {
            rgba[4 * i] = (byte) ((argb[i] >> 16) & 0xff); // R
            rgba[4 * i + 1] = (byte) ((argb[i] >> 8) & 0xff); // G
            rgba[4 * i + 2] = (byte) ((argb[i]) & 0xff); // B
            rgba[4 * i + 3] = (byte) ((argb[i] >> 24) & 0xff); // A
        }

        return rgba;
    }

    public void uploadImageToGPU(int frameIndex) {
        if (!amIValid) return;
        generate();

        BufferedImage img;
        img = textures[frameIndex];

        ByteBuffer buffer = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4); //4 -> RGBA
        buffer.put(
                intARGBtoByteRGBA(
                        img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth())
                )
        ).flip();

        bind();
        GL30.glTexImage2D(
                GL30.GL_TEXTURE_2D,         //Type
                0,                     //Level
                GL30.GL_RGBA8,               //Color Format (internal)
                img.getWidth(),             //Width
                img.getHeight(),            //Height
                0,                    //Border
                GL30.GL_RGBA,                //Color format
                GL11.GL_UNSIGNED_BYTE,       //Buffer type
                buffer);                    //Data
        unBind();
    }

    public void destroy() {
        GL30.glDeleteTextures(id);
        amIValid = false;
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


}
