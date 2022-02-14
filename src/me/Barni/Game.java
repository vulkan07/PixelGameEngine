package me.Barni;


import me.Barni.entity.childs.Player;
import window.MouseHandler;
import window.Window;
import me.Barni.hud.HUD;
import me.Barni.hud.HUDButton;
import me.Barni.hud.HUDNotification;
import me.Barni.physics.Vec2D;
import me.Barni.tools.LevelEditor;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public final class Game implements Runnable {

    private boolean running = false;    //Running
    Thread thread;                      //Game thread

    public Window window;               //Window


    private final Random r = new Random(); // Main random

    private int WIDTH, HEIGHT;

    private HUD hud;
    private Logger logger;
    private Map map;
    private Player player;

    public final String GAME_DIR; //Root game directory, which all file readers will use
    public String SHADER_DIR; //Root game directory, which all file readers will use

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
            return msgs[r.nextInt(msgs.length - 1)];
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

        SHADER_DIR = GAME_DIR + "textures\\shaders\\";

        //Logger
        this.logger = new Logger(logLevel);

        //Create main buffer image & get raster
        WIDTH = w;
        HEIGHT = h;


        window = new Window(title + "  -  " + loadRandomTitleMsg(), w, h);

        //Level editor
        //levelEditor = new LevelEditor(this);

        //Intro
        intro = new Intro(this);
        intro.start();

        //Hud
        hud = new HUD(this);

        //Add PlayerNotification
        getHud().getRoot().add(new HUDNotification(this, "PlayerNotification", "<initial>", 16, 30));

        //Add button & set colors
        getHud().getRoot().add(new HUDButton(this, "button", 200, 200, 30, "alma"));
        ((HUDButton) getHud().getRoot().getElement("button")).hoveredColor = new Color(80, 100, 120, 100);
        ((HUDButton) getHud().getRoot().getElement("button")).pressedColor = new Color(0, 150, 190, 100);

        GLFW.glfwMakeContextCurrent(MemoryUtil.NULL);

        //Start unique game thread
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void loadNewMap(String path) {

        if (map != null)
            map.destroy(); //Old map

        MapLoader ml = new MapLoader(this);


        map = ml.loadMap(path); //Load map via ml

        //If map doesn't load, a hardcoded map loads
        if (map == null) {
            map = new Map(this, 3, 5, 32, "<default>");
            byte[] defaultmap = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2,};
            map.setTileArray(defaultmap);
        }

        map.loadTextures(); //Load map textures
        map.createShaderPrograms();

        player = new Player(this, "player", new Vec2D(48, 0)); //Remake player
        player.loadTexture("player_1"); //Load player texture

        map.addEntity(player);          //Add player
        map.cam.followEntity = player;  //Make camera follow player

        try {
            levelEditor.reloadMap(map);     //Refresh level editor
        } catch (NullPointerException ignored) {
        }

        player.godMode = true;

        fadeInScreen(0);//Add screen fading effect
    }

    //--------------------------------\\
    //----------->   Run   <----------\\
    @Override
    public void run() {

        window.init();
        loadNewMap(GAME_DIR + "01.map");
        map.createShaderPrograms();

        //IMPORTANT! Frames and ticks are bounded together
        int fps = 60;
        float framePerTick = 1000000000 / fps;
        float delta = 0;
        long now, last;
        last = System.nanoTime();

        while (!GLFW.glfwWindowShouldClose(window.getWindow())) {
            GLFW.glfwPollEvents();
            now = System.nanoTime();
            delta += (now - last) / framePerTick;
            last = now;

            if (delta >= 1) {
                tick();
                render();
                delta--;
            }
        }
        stop();
    }

    //---------------------------------\\
    //----------->   Stop   <----------\\
    public synchronized void stop() {
        getLogger().info("Game loop stopped");

        GLFW.glfwDestroyWindow(window.getWindow());
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null);

        System.exit(0);
    }

    //---------------------------------\\
    //----------->   Tick   <----------\\
    private void tick() {
        //if (intro.isPlayingIntro()) return; //Return if playing intro

        hud.update();

        MouseHandler.update();

        map.tick();

        //levelEditor.update();
    }

    //---------------------------------\\
    //----------->  Render  <----------\\
    private void render() {
        window.clear();


        MouseHandler.update();

        //-----------------\\
        map.render(map.cam);
        //-----------------\\

        updateScreenFade();

        GLFW.glfwSwapBuffers(window.getWindow());
    }

    private void updateScreenFade() {
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
    }

    //Call to fade the screen FROM black
    public void fadeInScreen(int alphaStart) {
        if (alphaStart < 0 || alphaStart > 255 || isScreenFadingIn)
            return;
        blankAlpha = alphaStart;
        isScreenFadingOut = false;
        isScreenFadingIn = true;
    }

    //Call to fade the screen TO black
    public void fadeOutScreen(int alphaStart) {
        if (alphaStart < 0 || alphaStart > 255 || isScreenFadingOut)
            return;
        blankAlpha = alphaStart;
        isScreenFadingOut = true;
        isScreenFadingIn = false;
    }

    public void resetScreenFade(boolean isFadedOut) {
        isScreenFadingOut = false;
        isScreenFadingIn = false;
        blankAlpha = isFadedOut ? 255 : 0;
    }


    //----------------------------------\\
    //------------ GETTERS -------------\\
    //----------------------------------\\

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