package me.Barni;

import me.Barni.graphics.GraphicsUtils;
import me.Barni.graphics.ShaderProgram;
import me.Barni.graphics.VertexArrayObject;
import me.Barni.texture.Texture;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

import java.io.File;

public class Intro {

    Texture logoTexture, wheelTexture;

    public boolean isPlayingIntro() {
        return playingIntro;
    }

    private boolean playingIntro;
    private int timer, logoCount, foundLogos;
    Game game;
    private String pathPrefix;


    ShaderProgram logoShader, wheelShader;
    VertexArrayObject vao;
    public static final int[] ELEMENT_ARRAY = {2, 1, 0, 0, 1, 3};

    float[] logoVA, wheelVA;
    int time;
    Matrix4f rot = new Matrix4f();


    public Intro(Game game) {

        pathPrefix = game.TEXTURE_DIR + "logos\\logo";
        File f;
        int i = -1;
        do {
            i++;
            f = new File(pathPrefix + i + ".png");
        } while (f.exists());
        logoCount = 0;
        foundLogos = i;

        this.game = game;
        logoTexture = new Texture();
        wheelTexture = new Texture();
    }

    public void start() {
        nextLogo();
        playingIntro = true;
        timer = 0;
        game.fadeInScreen(255);

        wheelTexture.loadTexture(game, "logos\\wheel", 128, 128, false);
        wheelTexture.uploadImageToGPU(0);

        logoShader = new ShaderProgram(game);
        logoShader.create("logo");
        logoShader.link();

        wheelShader = new ShaderProgram(game);
        wheelShader.create("loadingWheel");
        wheelShader.link();

        vao = new VertexArrayObject();
        float[] vArray = new float[8];
        vao.setVertexData(vArray);
        vao.setElementData(ELEMENT_ARRAY);
        vao.addAttributePointer(2); //Position (x,y)
        vao.addAttributePointer(2); //TX coords (u,v)

        wheelVA = GraphicsUtils.generateVertexArray(0,0,128,128);

        logoVA = new float[16];
        logoVA[0] = -1;    //TL x
        logoVA[1] = -1;    //TL y

        logoVA[2] = 0f;   //U
        logoVA[3] = 1f;   //V

        logoVA[4] = 1;  //BR x
        logoVA[5] = 1;  //BR y

        logoVA[6] = 1f;   //U
        logoVA[7] = 0f;   //V

        logoVA[8] = 1;  //TR x
        logoVA[9] = -1;    //TR y

        logoVA[10] = 1f;   //U
        logoVA[11] = 1f;   //V

        logoVA[12] = -1;    //BL x
        logoVA[13] = 1;  //BL y

        logoVA[14] = 0f;   //U
        logoVA[15] = 0f;   //V
    }


    private void nextLogo() {
        logoTexture.loadTexture(game, ("logos/logo" + logoCount), 1920, 1080, true);
        logoTexture.uploadImageToGPU(0);
        logoCount++;
    }

    public void end() {
        if (!playingIntro)
            return;

        game.resetScreenFade(true);
        game.fadeInScreen(255);

        logoShader.unBind(); //Any shader unbind is good
        logoTexture.unBind(); //Any texture unbind is good

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
            //End check
            if (logoCount >= foundLogos) {
                end();
            } else {
                nextLogo();
                game.fadeInScreen(255);
                timer = 0;
            }
        }

        if (game.getScreenFadeAlpha() != 255) {
            //logo
            logoTexture.bind();

            logoShader.bind();
            logoShader.uploadFloat("uAlpha", game.getScreenFadeAlphaNormalized());

            vao.setVertexData(logoVA);
            GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexLen(), GL30.GL_UNSIGNED_INT, 0);
            logoTexture.unBind();
            logoShader.unBind();


            int fadeTime = 80;
            boolean shouldWheelFade = logoCount == 2 && timer < fadeTime;

            //wheel
            if (logoCount > 1)
                renderWheel(shouldWheelFade ? game.getScreenFadeAlphaNormalized() : 0);
        }
    }

    public void renderWheel(float alpha) {
        time++;
        wheelTexture.bind();
        wheelShader.bind();

        wheelShader.uploadFloat("uAlpha", alpha);

        wheelShader.uploadFloat("uTime", time);
        wheelShader.uploadMat4("uProjMat", game.getMap().getCamera().getDefaultProjMat());
        rot.identity();
        rot.rotate((float)Math.toRadians(time*1.4f), 0, 0, 1);
        wheelShader.uploadMat4("uRotMat", rot);

        vao.bind(false);
        vao.setVertexData(wheelVA);
        GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexLen(), GL30.GL_UNSIGNED_INT, 0);
        wheelTexture.unBind();
        wheelShader.unBind();
    }
}
