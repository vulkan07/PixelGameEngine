package me.Barni.superhexagon;


import me.Barni.Game;
import me.Barni.KeyboardHandler;
import me.Barni.physics.Vec2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class SuperHexagonWorld {

    int playerXpos = 180;
    int polySideCount = 6;
    int playerPolyPos = 3;
    int mapWidth;
    int overallRotation;
    int tickTimer, secondsTimer;
    int rotationDelta = 1;
    int blockSpeed = 1;
    final int playerHalfSize = 10; //player width is twice this
    Color color = new Color(0, 86, 255);
    Color color2 = new Color(163, 150, 194, 181);

    boolean oldRenderMode;
    boolean isRenderModeChanged, willRenderModeChange;
    boolean isPlayerDead;
    boolean oddEven;

    Game game;
    Random r = new Random();
    Vec2D origin;

    PolygonLine[][] polygonLines;

    int[][] playerPosVertexBuffer;
    int[][] hexagonVertexBuffer;
    int[][] currentPolyVertexBuffer;
    int[][] bgDecorPolyBuffer;

    public SuperHexagonWorld(Game g) {
        game = g;
        mapWidth = 360;

        polygonLines = new PolygonLine[polySideCount][4]; //Only 2 blocks can be in a line at once //TEST ONLY
        for (int i = 0; i < polygonLines.length; i++) {
            for (int j = 0; j < polygonLines[i].length; j++) {
                polygonLines[i][j] = new PolygonLine(r.nextInt(800) * -1 + 700, -20);
                //polygonLines[i][j].disable();
            }
        }
        //polygonLines[1][0].enable(-100);

        playerPosVertexBuffer = new int[2][3];             //Triangle buffer
        hexagonVertexBuffer = new int[2][polySideCount];   //Hexagon buffer
        currentPolyVertexBuffer = new int[2][4];           //Rectangle buffer
        bgDecorPolyBuffer = new int[2][4];                 //Rectangle buffer

        origin = new Vec2D(game.getWIDTH() / 2, game.getHEIGHT() / 2);

    }

    public void tick() {
        //===TIMING EVENTS===
        tickTimer++;
        if (tickTimer % (r.nextInt(180) + 120) == 0) {
            rotationDelta++;
            if (r.nextBoolean())
                rotationDelta *= -1;

            if (rotationDelta > 3)
                rotationDelta = -1;
            if (rotationDelta < -3)
                rotationDelta = 1;
            if (rotationDelta == 0)
                rotationDelta = 3;
        }
        if (tickTimer % 180 == 0) {
            isPlayerDead = false;
            blockSpeed++;
        }
        if (tickTimer % 60 == 0) {
            oddEven = !oddEven;
            secondsTimer++;
        }
        //===================

        //Calculate player position
        playerPolyPos = (int) Vec2D.remap(playerXpos, 0, 360, 0, polySideCount);
        if (!oldRenderMode)
            playerPolyPos -= 3;

        if (playerPolyPos < 0)
            playerPolyPos = polySideCount + playerPolyPos;
        if (playerPolyPos == 0)
            playerPolyPos = polySideCount;

        //Step all polygon lines
        int sideCount = 1;
        for (PolygonLine[] pls : polygonLines) {
            for (PolygonLine polygonLine : pls) {
                if (polygonLine.disabled) continue;

                polygonLine.tick(blockSpeed);

                //Check if at deadline
                if (polygonLine.y <= polygonLine.deadLine - playerHalfSize &&
                        polygonLine.y >= polygonLine.deadLine - playerHalfSize - 5) {

                    //Intersect with player
                    if (playerPolyPos == sideCount) {
                        //System.out.println(sideCount);
                        isPlayerDead = true;
                        secondsTimer = 0;
                        rotationDelta = 1;
                        blockSpeed = -1;
                    }

                }

                if (polygonLine.y == polygonLine.deadLine) {
                    polygonLine.y = r.nextInt(200) * -1 - 600;
                }
            }
            sideCount++;
        }

        //Handle inputs
        //Move player & render mode
        if (game.getKeyboardHandler().getKeyState(KeyboardHandler.LEFT))
            playerXpos += 10;
        if (game.getKeyboardHandler().getKeyState(KeyboardHandler.RIGHT))
            playerXpos -= 10;


        isRenderModeChanged = willRenderModeChange;
        willRenderModeChange = false;


        if (game.getKeyboardHandler().getKeyState(KeyboardHandler.ENTER)) {
            willRenderModeChange = true;
            if (!isRenderModeChanged) {
                oldRenderMode = !oldRenderMode;
            }
        }
        if (playerXpos >= 360)
            playerXpos = 1;
        if (playerXpos < 0)
            playerXpos = 360;
    }

    public void render(BufferedImage img) {
        overallRotation += rotationDelta;
        if (overallRotation >= 360)
            overallRotation = 0;

        if (overallRotation < 0)
            overallRotation = 359;

        if (oldRenderMode)
            classicRender(img);
        else
            newRender(img);
    }

    private void newRender(BufferedImage img) {
        Graphics g = img.getGraphics();

        Vec2D playerRotVector = new Vec2D(playerXpos + overallRotation).mult(56);

        int sideCount = 1;

        g.setColor(color2);

        //===DRAW MAP BACKGROUND DECOR LINES===
        for (int i = oddEven ? 1 : 0; i <= polySideCount-1; i += 2) {
            Vec2D polyRotVector = new Vec2D(360 / polySideCount * i + overallRotation).mult(-48);
            Vec2D nextPolyRotVector = new Vec2D(360 / polySideCount * (i + 1) + overallRotation).mult(-48);

            bgDecorPolyBuffer[0][0] = origin.copy().add(polyRotVector.copy().mult(60f)).xi();
            bgDecorPolyBuffer[1][0] = origin.copy().add(polyRotVector.copy().mult(60f)).yi();

            bgDecorPolyBuffer[0][3] = origin.copy().add(nextPolyRotVector.copy().mult(60f)).xi();
            bgDecorPolyBuffer[1][3] = origin.copy().add(nextPolyRotVector.copy().mult(60f)).yi();

            bgDecorPolyBuffer[0][1] = origin.copy().add(polyRotVector).xi();
            bgDecorPolyBuffer[1][1] = origin.copy().add(polyRotVector).yi();

            bgDecorPolyBuffer[0][2] = origin.copy().add(nextPolyRotVector).xi();
            bgDecorPolyBuffer[1][2] = origin.copy().add(nextPolyRotVector).yi();
            g.fillPolygon(bgDecorPolyBuffer[0], bgDecorPolyBuffer[1], 4);
        }
        //=====================================

        //===DRAW POLYGONS===
        g.setColor(color);
        if (isPlayerDead)
            g.setColor(Color.RED);

        for (PolygonLine[] pls : polygonLines) {
            for (PolygonLine polygonLine : pls) {
                if (polygonLine.disabled) continue;

                Vec2D polyRotVector = new Vec2D(360 / polySideCount * sideCount + overallRotation).mult(polygonLine.y * 2);
                Vec2D nextPolyRotVector = new Vec2D(360 / polySideCount * (sideCount + 1) + overallRotation).mult(polygonLine.y * 2);

                //      o________________
                //      \               /
                //       \_____________/
                currentPolyVertexBuffer[0][0] = origin.copy().add(polyRotVector.copy().mult(1.2f)).xi(); //.add(20)
                currentPolyVertexBuffer[1][0] = origin.copy().add(polyRotVector.copy().mult(1.2f)).yi();

                //      ________________o
                //      \               /
                //       \_____________/
                currentPolyVertexBuffer[0][3] = origin.copy().add(nextPolyRotVector.copy().mult(1.2f)).xi();
                currentPolyVertexBuffer[1][3] = origin.copy().add(nextPolyRotVector.copy().mult(1.2f)).yi();

                //      _________________
                //      \               /
                //       o_____________/
                currentPolyVertexBuffer[0][1] = origin.copy().add(polyRotVector).xi();
                currentPolyVertexBuffer[1][1] = origin.copy().add(polyRotVector).yi();

                //      _________________
                //      \               /
                //       \_____________o
                currentPolyVertexBuffer[0][2] = origin.copy().add(nextPolyRotVector).xi();
                currentPolyVertexBuffer[1][2] = origin.copy().add(nextPolyRotVector).yi();

                g.fillPolygon(currentPolyVertexBuffer[0], currentPolyVertexBuffer[1], 4);
            }
            sideCount++;
        }
        //================

        //===DRAW PLAYER===
        g.setColor(color);
        if (isPlayerDead)
            g.setColor(Color.RED);

        //Player triangle vertices
        for (int i = 0; i < 3; i++) {
            playerPosVertexBuffer[0][i] = new Vec2D(360 / 3 * i + playerXpos + overallRotation).mult(8).add(origin).add(playerRotVector).xi();
            playerPosVertexBuffer[1][i] = new Vec2D(360 / 3 * i + playerXpos + overallRotation).mult(8).add(origin).add(playerRotVector).yi();
        }

        g.fillPolygon(playerPosVertexBuffer[0], playerPosVertexBuffer[1], 3);
        //=================

        //===DRAW CENTER HECAGON===
        //Calculate hexagon points
            for (int i = 0; i < polySideCount; i++) {
                Vec2D point = origin.copy();
                Vec2D offset = new Vec2D(360 / polySideCount * i + overallRotation);
                point.add(offset.mult(48));
                hexagonVertexBuffer[0][i] = point.xi();
                hexagonVertexBuffer[1][i] = point.yi();
            }
            g.drawPolygon(hexagonVertexBuffer[0], hexagonVertexBuffer[1], polySideCount);
            g.setFont(game.getDefaultFont());
        //========================

        g.drawString(Integer.toString(secondsTimer), origin.xi()-8, origin.yi());
    }

    private void classicRender(BufferedImage img) {
        Graphics g = img.getGraphics();

        //Polygons
        int sideCount = 1;
        g.setColor(Color.ORANGE);
        for (PolygonLine[] pls : polygonLines) {
            for (PolygonLine polygonLine : pls) {
                if (polygonLine.disabled) continue;
                g.fillRect(
                        game.getWIDTH() / polySideCount * sideCount - game.getWIDTH() / polySideCount,
                        (int) Vec2D.remap(polygonLine.y, -800, 0, 0, game.getHEIGHT() - 30),
                        game.getWIDTH() / polySideCount,
                        24);
            }
            sideCount++;
        }
        //DRAW PLAYER
        g.setColor(Color.WHITE);
        if (isPlayerDead)
            g.setColor(Color.RED);

        //TRIANGLE
        //Left point
        int playerRealX = (int) Vec2D.remap(playerXpos, 0, 360, 0, game.getWIDTH());
        playerPosVertexBuffer[0][0] = playerRealX - playerHalfSize;
        playerPosVertexBuffer[1][0] = game.getHEIGHT() - 64;
        //Right point
        playerPosVertexBuffer[0][1] = playerRealX + playerHalfSize;
        playerPosVertexBuffer[1][1] = game.getHEIGHT() - 64;
        //Upper point
        playerPosVertexBuffer[0][2] = playerRealX;
        playerPosVertexBuffer[1][2] = game.getHEIGHT() - 72 - playerHalfSize;

        g.fillPolygon(playerPosVertexBuffer[0], playerPosVertexBuffer[1], 3);
        g.drawString(Integer.toString(playerPolyPos), origin.xi(), origin.yi());
    }

}
