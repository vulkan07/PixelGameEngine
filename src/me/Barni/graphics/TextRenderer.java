package me.Barni.graphics;

import me.Barni.Game;
import me.Barni.texture.Texture;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class TextRenderer {

    public static Game game;
    public static int RENDER_QUALITY_MULT = 2;
    public static Font defFont = new Font("Dialog", Font.PLAIN, 20);
    public static FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);

    public  static ShaderProgram textShader;
    public static BufferedImage renderTextToBufferedImage(String text, Color color, float size) {

        if (size != defFont.getSize2D())
            defFont = defFont.deriveFont(size);
        int w = (int)(defFont.getStringBounds(text, frc).getWidth());
        int h = (int)(defFont.getStringBounds(text, frc).getHeight());

        BufferedImage bufferedImage = new BufferedImage(w,h+18, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D)(bufferedImage.getGraphics());
        g2d.setFont(defFont);
        g2d.setColor(color);
        int y = (int)defFont.getStringBounds(text, g2d.getFontRenderContext()).getHeight();
        g2d.drawString(text,0, y);

        return bufferedImage;
    }

    public static void init(Game g) {
        game = g;
        textShader = new ShaderProgram(game);
        textShader.create("gui_text");
        textShader.link();
    }

    public static Texture renderText(String text, Color color, float size) {
        Texture t = new Texture();
        BufferedImage image = renderTextToBufferedImage(text, color, size);
        t.setTexture(game, image);
        t.uploadImageToGPU(0);
        return t;
    }

}