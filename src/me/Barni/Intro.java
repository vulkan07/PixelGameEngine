package me.Barni;

import me.Barni.graphics.ShaderProgram;
import me.Barni.graphics.VertexArrayObject;
import me.Barni.texture.Texture;
import org.lwjgl.opengl.GL30;

import java.io.File;

public class Intro {

    Texture t;

    public boolean isPlayingIntro() {
        return playingIntro;
    }

    private boolean playingIntro;
    private int timer, xPos, yPos, logoCount, foundLogos;
    Game game;
    private String pathPrefix;


    ShaderProgram logoShader;
    VertexArrayObject vao;
    public static final int[] ELEMENT_ARRAY = {2, 1, 0, 0, 1, 3};


    public Intro(Game game) {

        pathPrefix = game.GAME_DIR + Texture.TEXTURE_BONUS_PATH + "logos\\logo";
        File f;
        int i = 0;
        do {
            i++;
            f = new File(pathPrefix + i + ".png");
        } while (f.exists());
        logoCount = 0;
        foundLogos = i + 1;

        this.game = game;
        t = new Texture();
    }

    public void start() {
        nextLogo();
        playingIntro = true;
        timer = 0;
        game.fadeInScreen(255);


        logoShader = new ShaderProgram(game);
        logoShader.create("logo");
        logoShader.link();

        vao = new VertexArrayObject();
        float[] vArray = new float[8];
        vao.setVertexData(vArray);
        vao.setElementData(ELEMENT_ARRAY);
        vao.addAttributePointer(2); //Position (x,y)
        vao.addAttributePointer(2); //TX coords (u,v)

        float[] va = new float[16];
        va[0] = -1;    //TL x
        va[1] = -1;    //TL y

        va[2] = 0f;   //U
        va[3] = 1f;   //V

        va[4] = 1;  //BR x
        va[5] = 1;  //BR y

        va[6] = 1f;   //U
        va[7] = 0f;   //V

        va[8] = 1;  //TR x
        va[9] = -1;    //TR y

        va[10] = 1f;   //U
        va[11] = 1f;   //V

        va[12] = -1;    //BL x
        va[13] = 1;  //BL y

        va[14] = 0f;   //U
        va[15] = 0f;   //V
        vao.setVertexData(va);
        logoShader.bind();
        t.bind();
    }

    private void nextLogo() {
        t.loadTexture(game, ("\\logos\\logo" + logoCount), 1920, 1080, true);
        t.uploadImageToGPU(0);
        xPos = game.getWIDTH() / 2 - t.getWidth() / 2;
        yPos = game.getHEIGHT() / 2 - t.getHeight() / 2;
        logoCount++;
    }

    public void end() {
        if (!playingIntro)
            return;

        game.resetScreenFade(true);
        game.fadeInScreen(255);

        logoShader.unBind();
        t.unBind();

        playingIntro = false;
    }

    public void render() {
        if (!playingIntro) return;

        timer++;
        //Fade out
        if (timer == 150) {
            game.fadeOutScreen(0);
        }

        //OnFadedOut
        if (timer >= 150 && game.getScreenFadeAlpha() == 255) {
            nextLogo();
            game.fadeInScreen(255);
            timer = 0;

            //End
            if (logoCount >= foundLogos) {
                end();
            }
        }

        if (game.getScreenFadeAlpha() != 255) {
            t.bind();
            logoShader.uploadFloat("uAlpha", game.getScreenFadeAlphaNormalized());
            GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexLen(), GL30.GL_UNSIGNED_INT, 0);
            t.unBind();
        }
    }
}
