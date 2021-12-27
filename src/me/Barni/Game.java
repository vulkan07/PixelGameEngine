package me.Barni;


import me.Barni.entity.childs.Player;
import me.Barni.hud.HUD;
import me.Barni.hud.HUDButton;
import me.Barni.hud.HUDNotification;
import me.Barni.physics.Vec2D;
import me.Barni.tools.LevelEditor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

public final class Game extends Canvas implements Runnable {

    private boolean running = false;    //Running
    Thread thread;                      //Game thread

    public JFrame window;               //Window
    private BufferedImage image;        //Image
    private int[] buffer;               //Main Imageb buffer
    int bgColor = new Color(137, 176, 205).getRGB();   //Color value for canvas clear

    private final Random r = new Random(); // Main random

    private int WIDTH, HEIGHT;

    private HUD hud;
    private MouseHandler mouseHandler;
    private KeyboardHandler keyboardHandler;
    private Logger logger;
    private Map map;
    private Player player;

    public final String GAME_DIR; //Root game directory, which all file readers will use

    //Screen fading variables
    private boolean isScreenFadingIn, isScreenFadingOut;
    private int blankAlpha = 255;

    private Intro intro;
    private LevelEditor levelEditor;

    public String defaultWindowTitle;

    Font defaultFont = new Font("Verdana", Font.PLAIN, 20); // Default font


    //Constructor
    public Game(String wDir) {
        GAME_DIR = wDir;
    }

    //---------------------------------\\
    //--->  Load Title Random Msg  <---\\
    private String loadRandomTitleMsg() {
        String msg = "";
        String lines = "";

        //Try read titles.txt
        try {
            File file = new File(GAME_DIR + "titles.txt");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            lines = new String(data, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            logger.warn("Can't find titles.txt to get random title");
        }

        //If read successfully choose random line as title
        if (!lines.equals("")) {
            String[] msgs = lines.split("\n");
            return msgs[r.nextInt(msgs.length)-1];
        }

        return msg;
    }

    //--------------------------------\\
    //----------->  Start  <----------\\
    public synchronized void start(String title, int w, int h, boolean fullscreen, boolean resizeable, byte logLevel) {

        //this function only can be called once
        if (running) {
            getLogger().err("Tried to start game, while it's running!");
            return;
        }

        //Create main buffer image & get raster
        WIDTH = w;
        HEIGHT = h;
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        buffer = ((DataBufferInt) (image.getRaster().getDataBuffer())).getData();

        //------------------------\\
        //--------WINDOW----------\\
        defaultWindowTitle = title + "  -  " + loadRandomTitleMsg();
        window = new JFrame("Starting...");

        if (fullscreen) {
            window.setUndecorated(true);
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(WIDTH, HEIGHT);
        window.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        window.setLocationRelativeTo(null);
        window.add(this);
        window.setResizable(resizeable);
        window.setVisible(true);
        this.requestFocus();
        //--------WINDOW----------\\
        //------------------------\\

        //Set canvas's bg
        this.setBackground(Color.BLACK);
        //create canvas' BufferStrategy
        createBufferStrategy(2);

        //Logger
        this.logger = new Logger(logLevel);

        //Mouse
        mouseHandler = new MouseHandler(window, this);
        this.addMouseListener(getMouseHandler());
        //Keyboard
        keyboardHandler = new KeyboardHandler(this, false);
        this.addKeyListener(getKeyboardHandler());

        //Map
        loadNewMap(GAME_DIR + "01.map");

        //Level editor
        levelEditor = new LevelEditor(this);

        //Intro
        intro = new Intro(this, image);
        intro.start();

        //Hud
        hud = new HUD(this);

        //Add PlayerNotification
        getHud().getRoot().add(new HUDNotification(this, "PlayerNotification", "<initial>", 16, 30));

        //Add button & set colors
        getHud().getRoot().add(new HUDButton(this, "button", 200, 200, 30, "alma"));
        ((HUDButton) getHud().getRoot().getElement("button")).hoveredColor = new Color(80, 100, 120, 100);
        ((HUDButton) getHud().getRoot().getElement("button")).pressedColor = new Color(0, 150, 190, 100);


        //Start unique game thread
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void loadNewMap(String path) {

        MapLoader ml = new MapLoader(this);


        map = ml.loadMap(path); //Load map via ml

        //If map doesn't load, a hardcoded map loads
        if (map == null) {
            map = new Map(this, 3, 5, 32, "<default>");
            byte[] defaultmap = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2,};
            map.setTileArray(defaultmap);
        }

        map.loadTextures(); //Load map textures

        player = new Player(this, "player", new Vec2D(48, 0)); //Remake player
        player.loadTexture("player_1"); //Load player texture

        map.addEntity(player);          //Add player
        map.cam.followEntity = player;  //Make camera follow player

        try {
            levelEditor.reloadMap(map);     //Refresh level editor
        } catch (NullPointerException ignored) {
        }

        isScreenFadingIn = true;          //Add screen fading effect
    }

    //--------------------------------\\
    //----------->   Run   <----------\\
    @Override
    public void run() {
        //IMPORTANT! Frames and ticks are bounded together
        int fps = 60;
        float framePerTick = 1000000000 / fps;
        float delta = 0;
        long now, last, timer = 0;
        int frames = 0;

        last = System.nanoTime();
        getLogger().info("[GAME] Preferred FPS: " + fps);
        getLogger().info("[GAME] Game loop ready to start\n"); // \n to separate loop logs

        while (running) {

            now = System.nanoTime();
            delta += (now - last) / framePerTick;
            timer += (now - last);
            last = now;


            if (delta >= 1) {

                //=TICK=\\
                tick();

                //=RENDER=\\
                render();

                frames++;
                delta--;
            }

            //Primitive FPS timer
            if (timer >= 1000000000) {

                window.setTitle(defaultWindowTitle + "   |   " + frames + " FPS");
                frames = 0;
                timer = 40;
            }
        }
        stop();
    }

    //---------------------------------\\
    //----------->   Stop   <----------\\
    public synchronized void stop() {
        getLogger().info("Game loop stopped");
        try {
            thread.join();
        } catch (InterruptedException ignored) {
        }
    }

    //---------------------------------\\
    //----------->   Tick   <----------\\
    private void tick() {
        if (intro.isPlayingIntro()) return; //Return if playing intro

        getHud().update();

        getMouseHandler().update();

        getMap().tick();

        getLevelEditor().update();
    }

    //---------------------------------\\
    //----------->  Render  <----------\\
    private void render() {

        //Clear buffer image
        if (blankAlpha != 255 && !intro.isPlayingIntro())
            Arrays.fill(buffer, bgColor);

        //Get image's graphics
        Graphics g = getBufferStrategy().getDrawGraphics();

        //Render intro
        intro.render();
        //Draw image to canvas's graphics
        g.drawImage(image, 0, 0, WIDTH, HEIGHT, null);

        if (blankAlpha != 255 && !intro.isPlayingIntro()) {

            //Render in primitive layer orders
            //TODO make render layers abstract
            getMap().renderDecoratives(image, -1); //Behind map layer
            getMap().renderTiles(image);

            getMap().renderDecoratives(image, 0); //Before map
            getMap().renderEntities(image);

            getMap().renderDecoratives(image, 1); //Before entities

            getHud().render(image);
            g.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
            getLevelEditor().overlayRender(g);
        }


        //Screen fading mechanism
        {
            //None -> back
            if (isScreenFadingOut) {
                blankAlpha += 5;

                if (blankAlpha > 255) {
                    blankAlpha = 255;
                    isScreenFadingOut = false;
                }
            }
            //Black -> none
            if (isScreenFadingIn) {
                blankAlpha -= 5;

                if (blankAlpha < 0) {
                    blankAlpha = 0;
                    isScreenFadingIn = false;
                }
            }

            if (isScreenFadingIn || isScreenFadingOut || blankAlpha == 1) {
                g.setColor(new Color(0, 0, 0, blankAlpha));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }

        g.dispose();
        getBufferStrategy().show();
    }

    //Call to fade the screen FROM black
    public void fadeInScreen(int alphaStart) {
        if (alphaStart < 0 || alphaStart > 255 || isScreenFadingIn)
            return;
        blankAlpha = alphaStart;
        isScreenFadingOut = false;
        isScreenFadingIn = true;
        System.out.println("fadein");
    }

    //Call to fade the screen TO black
    public void fadeOutScreen(int alphaStart) {
        if (alphaStart < 0 || alphaStart > 255 || isScreenFadingOut)
            return;
        blankAlpha = alphaStart;
        isScreenFadingOut = true;
        isScreenFadingIn = false;
        System.out.println("fadeout");
    }

    public void resetScreenFade(boolean isFadedOut) {
        isScreenFadingOut = false;
        isScreenFadingIn = false;
        blankAlpha = isFadedOut ? 255 : 0;
    }


    //----------------------------------\\
    //------------ GETTERS -------------\\
    //----------------------------------\\
    public MouseHandler getMouseHandler() {
        return mouseHandler;
    }

    public KeyboardHandler getKeyboardHandler() {
        return keyboardHandler;
    }

    public Logger getLogger() {
        return logger;
    }

    public Map getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }

    public LevelEditor getLevelEditor() {
        return levelEditor;
    }

    public HUD getHud() {
        return hud;
    }

    public Font getDefaultFont() {
        return defaultFont;
    }

    public int getScreenFadeAlpha() {
        return blankAlpha;
    }

    public Intro getIntro() {
        return intro;
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }
}