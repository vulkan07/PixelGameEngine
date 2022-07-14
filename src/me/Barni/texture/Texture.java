package me.Barni.texture;

import me.Barni.Game;
import me.Barni.Utils;
import me.Barni.exceptions.MaterialException;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Map;

public class Texture {

    private static Game game;

    private boolean animated, hasAnimation;
    public BufferedImage[] textures;
    private int width;
    private int height;
    private boolean amIValid;
    private String generalPathName;
    private AnimSequence[] sequences;
    int currSequence, frameCount;
    private int lastUploadedFrame = -1;
    private boolean markedForReload; //If true, reuploads image to gpu on next update()
    private int id;
    private String path;

    public static void init(Game g) {
        game = g;
    }

    private void errMsg(String msg) {
        game.getLogger().err("[TEXTURE] " + msg + "    At: " + generalPathName + path + ".png");
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
            return textures[sequences[currSequence].getCurrentFrameIndex()];
        } else
            return textures[0];
    }

    public void setTexture(Game g, BufferedImage img) {
        amIValid = true;
        game = g;
        width = img.getWidth();
        height = img.getHeight();
        this.animated = false;
        textures = new BufferedImage[1];
        textures[0] = img;
        path = null;
        lastUploadedFrame = -1;
    }

    public void loadTexture(String relativePath, int w, int h) {
        retries = 0;
        setAnimated(true);
        loadTextureImage(relativePath);

        //Override dimensions (Might be dangerous!)
        this.width = w;
        this.height = h;
    }
    public void loadTexture(String relativePath) {
        retries = 0;
        setAnimated(true);
        loadTextureImage(relativePath);
    }

    private void loadTextureImage(String relativePath) {
        amIValid = true;
        retries++;

        path = relativePath;
        generalPathName = game.TEXTURE_DIR;
        String imgPath = path + ".png";
        String dataPath = path + ".anim";
        BufferedImage fullImg = null;
        sequences = null;

        //READ IMAGE
        try {
            fullImg = ImageIO.read(new File(game.TEXTURE_DIR + imgPath));
            this.width = fullImg.getWidth();
            this.height = fullImg.getHeight();
        } catch (IOException e) {
            if (e instanceof FileNotFoundException)
                game.getLogger().err("[TEXTURE] File not Found: " + game.TEXTURE_DIR + imgPath);
            else
                game.getLogger().err("[TEXTURE] Can't read file: " + game.TEXTURE_DIR + imgPath);
            amIValid = false;
        }


        File dFile = new File(game.TEXTURE_DIR + dataPath);
        if (dFile.exists()) {
            sequences = AnimSequenceLoader.loadSequences(game.TEXTURE_DIR + dataPath, this);
            hasAnimation = true;
            animated = true;
        }

        if (sequences == null) {
            animated = false;
            hasAnimation = false;
        }


        //NO ANIMATION (1 FRAME ONLY)
        textures = new BufferedImage[1];
        textures[0] = fullImg;

        //CHOP TEXTURES
        try {
            if (hasAnimation) {
                textures = new BufferedImage[frameCount];
                this.width /= frameCount;
                for (int i = 0; i < frameCount; i++) {
                    int[] px = fullImg.getRGB(width * i, 0, width, height, null, 0, width * height);
                    BufferedImage img = new BufferedImage(fullImg.getWidth() / frameCount, height, BufferedImage.TYPE_INT_ARGB);
                    img.setRGB(0, 0, width, height, px, 0, width * height);
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
        lastUploadedFrame = -1;

        if (!isValid()) {
            if (retries > 2) { //  <-- Can't find missing.png
                throw new MaterialException("Can't find \"missing.png\"!  :  " + path);
            }
            loadTextureImage("missing");
        }
    }

    private int retries = 0;

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
    public static final int FILTERING_LINEAR = GL30.GL_LINEAR;
    public static final int FILTERING_NEAREST = GL30.GL_NEAREST;
    public static final int WRAP_TO_EDGE = GL30.GL_CLAMP_TO_EDGE;
    public static final int WRAP_REPEAT = GL30.GL_REPEAT;

    public void setFiltering(int mode) {
        bind();
        switch (mode) {
            case FILTERING_LINEAR:
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
            case FILTERING_NEAREST:
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
            default:
                game.getLogger().err("[TEXTURE] Invalid filtering mode: " + mode + " (" + path + ")");
        }
    }

    public void setWrapping(int mode) {
        bind();
        switch (mode) {
            case WRAP_TO_EDGE:
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
            case WRAP_REPEAT:
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);
            default:
                game.getLogger().err("[TEXTURE] Invalid wrapping mode: " + mode + " (" + path + ")");
        }
    }

    public void setGLTexParameter(int param, int value) {
        if (!amIValid) return;
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, id);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, param, value); //Scale down
    }

    public void bind() {
        if (!amIValid) throw new IllegalStateException("Attempted to bind invalid texture! : " + getPath());
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, id);
    }

    public void markForReload() {
        markedForReload = true;
    }

    public void unBind() {
        if (!amIValid) return;
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
    }

    public static byte[] intARGBtoByteRGBA(int[] argb) {
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
        if (frameIndex == lastUploadedFrame) return;
        if (id < 1) generate();
        lastUploadedFrame = frameIndex;

        BufferedImage img;
        img = textures[frameIndex];
        ByteBuffer buffer = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4); //4 -> RGBA
        buffer.put(
                intARGBtoByteRGBA(
                        img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth())
                )
        ).flip();

        bind();
        Utils.GLClearError();
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
        Utils.GLCheckError();
        unBind();
    }

    public void destroy() {
        GL30.glDeleteTextures(id);
        amIValid = false;
    }

    public void update() {
        if (markedForReload) {
            uploadImageToGPU(sequences[currSequence].getCurrentFrameIndex());
            markedForReload = false;
        }
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
