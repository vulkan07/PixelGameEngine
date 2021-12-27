package me.Barni;

import me.Barni.texture.Texture;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Intro {

    Texture t;

    public boolean isPlayingIntro() {
        return playingIntro;
    }

    private boolean playingIntro;
    private int timer, xPos, yPos, logoCount, foundLogos;
    Game game;
    private BufferedImage image;
    private String pathPrefix;

    public Intro(Game game, BufferedImage img) {

        pathPrefix = game.GAME_DIR + Texture.TEXTURE_BONUS_PATH + "logos\\logo";
        File f;
        int i = 0;
        do {
            i++;
            f = new File(pathPrefix+i+".png");
        } while(f.exists());
        logoCount = 0;
        foundLogos = i+1;

        image = img;
        this.game = game;
        t = new Texture();
    }

    public void start() {
        nextLogo();
        playingIntro = true;
        timer = 0;
        game.fadeInScreen(255);
    }

    private void nextLogo() {
        t.loadTexture(game, ("\\logos\\logo"+logoCount), 750, 750, true);
        xPos = game.getWIDTH() / 2 - t.getWidth() / 2;
        yPos = game.getHEIGHT() / 2 - t.getHeight() / 2;
        logoCount++;
    }

    public void skip() {
        if (!playingIntro)
            return;

        game.resetScreenFade(true);
        game.fadeInScreen(255);

        playingIntro = false;
    }
    public void render() {
        if (!playingIntro) return;

        timer++;

        //Fade out
        if (timer >= 150) {
            game.fadeOutScreen(0);
        }

        Graphics g = image.getGraphics();

        //OnFadedOut
        if (timer >= 150 && game.getScreenFadeAlpha() == 255) {
            nextLogo();
            game.fadeInScreen(255);
            timer = 0;

            //End
            if (logoCount >= foundLogos)
                playingIntro = false;
        }

        if (game.getScreenFadeAlpha() != 255) {
            g.clearRect(xPos,yPos, t.getWidth(), t.getHeight());
            g.drawImage(t.getTexture(), xPos, yPos, null);
        }
    }
}
