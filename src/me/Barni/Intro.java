package me.Barni;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Intro {

    Texture t;

    public boolean isPlayingIntro() {
        return playingIntro;
    }

    private boolean playingIntro;
    private int timer, xPos, yPos;
    Game game;
    private BufferedImage image;

    public Intro(Game game, String imgPath, BufferedImage img) {
        image = img;
        this.game = game;
        t = new Texture();
        t.loadTexture(game, imgPath, 794, 734, false);
        xPos = game.WIDTH / 2 - t.width / 2;
        yPos = game.HEIGHT / 2 - t.height / 2;
    }

    public void start() {

        playingIntro = true;
        timer = 0;
        Graphics g = image.getGraphics();
        game.blankAlpha = 255;
        game.screenFadingIn = true;
    }

    public void render() {
        if (!playingIntro) return;

        timer++;

        if (timer >= 150) {
            game.screenFadingOut = true;
        }

        Graphics g = image.getGraphics();

        if (timer >= 150 && game.blankAlpha == 255) {
            playingIntro = false;
            game.screenFadingIn = true;
        }

        if (game.blankAlpha != 255) {
            g.drawImage(t.getTexture(), xPos, yPos, null);
        }
    }
}
