package me.Barni.graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Texture2 {
    int width, height, id;
    BufferedImage img;
    static final String APP_DIR = "C:\\dev\\textures\\";

    public boolean loadImage(String relPath) {
        String fullPath = APP_DIR + relPath + ".png";
        System.out.println("Loading texture: " + fullPath);
        try {
            img = ImageIO.read(new File(fullPath));
            width = img.getWidth();
            height = img.getHeight();
        } catch (IOException e) {
            System.out.println("Failed to load image! :" + fullPath);
            return false;
        }
        return true;
    }

    public int getID() {
        return id;
    }

    public void generate() {
        id = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, id);

        //Set texture default flags
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST); //Scale down
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST); //Scale up
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);      //Wrap x
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_REPEAT);      //Wrap y
    }
    // S,T = U,V = X,Y

    public void setGLTexParameter(int param, int value) {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, id);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, param, value); //Scale down
    }

    public void bind() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, id);
    }

    public void unBind() {
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
    }

    private static byte[] intARGBtoByteRGBA(int[] argb) {
        byte[] rgba = new byte[argb.length * 4];

        for (int i = 0; i < argb.length; i++) {
            rgba[4 * i    ] = (byte) ((argb[i] >> 16) & 0xff); // R
            rgba[4 * i + 1] = (byte) ((argb[i] >>  8) & 0xff); // G
            rgba[4 * i + 2] = (byte) ((argb[i]      ) & 0xff); // B
            rgba[4 * i + 3] = (byte) ((argb[i] >> 24) & 0xff); // A
        }

        return rgba;
    }

    public void uploadImageToGPU(boolean keepImageInMemory) {

        ByteBuffer buffer = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4); //4 -> RGBA
        buffer.put(
                intARGBtoByteRGBA(
                        img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth())
                )
        ).flip();

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

        if (!keepImageInMemory)
            img = null;
    }

}
